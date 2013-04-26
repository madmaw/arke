package arke.container.jpa;

import arke.*;
import arke.container.jpa.data.*;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JPAContainer implements Container, JPAMessageHandler {

    private static final Logger LOG = Logger.getLogger(JPAContainer.class.getSimpleName());

    private Executor executor;
    private Messenger messenger;

    private TimeZone systemTimeZone;

    private PersistentUserDAO persistentUserDAO;
    private PersistentDeviceDAO persistentDeviceDAO;
    private PersistentInboundMessageDAO persistentInboundMessageDAO;
    private PersistentOutboundMessageDAO persistentOutboundMessageDAO;
    private PersistentScheduledMessageDAO persistentScheduledMessageDAO;
    private PersistentMessagePartDAO persistentMessagePartDAO;
    private PersistentUserPropertyDAO persistentUserPropertyDAO;
    private PersistentDevicePropertyDAO persistentDevicePropertyDAO;
    private Universe universe;
    private DeviceBasedTimeZoneGuesser timeZoneGuesser;

    private MessageRunnable messageRunnable;

    private class MessageRunnable implements Runnable {

        private Date nextScheduledMessageTime;
        private boolean done;

        @Override
        public void run() {
            while( !done ) {
                try {
                    synchronized (this) {



                        PersistentScheduledMessage nextScheduledMessage = persistentScheduledMessageDAO.findFirstUndelivered();
                        if( nextScheduledMessage != null ) {
                            nextScheduledMessageTime = nextScheduledMessage.getScheduledTime();
                        } else {
                            nextScheduledMessageTime = null;
                        }
                        try {
                            if( nextScheduledMessageTime == null ) {
                                this.wait();
                            } else {
                                long toWait = nextScheduledMessageTime.getTime() - (new Date()).getTime();
                                if( toWait > 0 ) {
                                    this.wait(toWait);
                                }
                            }
                        } catch( InterruptedException ex ) {
                            // just skip ahead
                        }
                    }
                    if( !done ) {
                        // pull up all the unhandled messages
                        List<PersistentInboundMessage> inboundMessages = persistentInboundMessageDAO.findUnhandled();
                        for( PersistentInboundMessage inboundMessage : inboundMessages ) {
                            PersistentDevice persistentDevice = persistentDeviceDAO.find(inboundMessage.getDeviceId());
                            List<PersistentMessagePart> parts = persistentMessagePartDAO.findByInboundMessageId(inboundMessage.getId());
                            // casting it up
                            JPAInboundMessage jpaMessage = new JPAInboundMessage(inboundMessage, persistentDevice,  (List)parts);
                            try {
                                // mark as handled
                                inboundMessage.setHandled(true);
                                universe.handleMessage(JPAContainer.this, jpaMessage);
                            } catch( Exception ex ) {
                                LOG.log(Level.WARNING, "unable to deliver incomming message "+jpaMessage, ex);
                                inboundMessage.setFailureReason(ExceptionUtils.getStackTrace(ex));
                            }

                            persistentInboundMessageDAO.update(inboundMessage);
                        }
                        // pull up all the scheduled events
                        Date now = new Date();
                        List<PersistentScheduledMessage> scheduledMessages = persistentScheduledMessageDAO.findUndeliveredScheduledBeforeTime(now);
                        for( PersistentScheduledMessage scheduledMessage : scheduledMessages ) {
                            List<PersistentMessagePart> parts = persistentMessagePartDAO.findByScheduledMessageId(scheduledMessage.getId());
                            ScheduledMessage message = new ScheduledMessage(scheduledMessage.getScheduledTime(), (List)parts);
                            try {
                                scheduledMessage.setDeliveryTime(new Date());
                                universe.handleMessage(JPAContainer.this, message);

                            } catch( Exception ex ) {
                                LOG.log(Level.WARNING, "unable to deliver scheduled message "+message, ex);
                                scheduledMessage.setFailureReason(ExceptionUtils.getStackTrace(ex));
                            }
                            persistentScheduledMessageDAO.update(scheduledMessage);
                        }
                    }
                } catch( Exception ex ) {
                    // we're done, it's broken
                    LOG.log(Level.SEVERE, "unable to continue, exiting", ex);
                    done = true;
                }
            }
        }

        public void wakeup() {
            this.notify();
        }

        public void checkSchedule(Date toCheck) {
            synchronized (this) {
                if( this.nextScheduledMessageTime == null || this.nextScheduledMessageTime.after(toCheck) ) {
                    wakeup();
                }
            }
        }

        public void stop() {
            synchronized (this) {
                this.done = true;
                wakeup();
            }
        }
    }

    public JPAContainer(
            Executor executor,
            Universe universe,
            Messenger messenger,
            TimeZone systemTimeZone,
            PersistentUserDAO persistentUserDAO,
            PersistentDeviceDAO persistentDeviceDAO,
            PersistentInboundMessageDAO persistentInboundMessageDAO,
            PersistentOutboundMessageDAO persistentOutboundMessageDAO,
            PersistentScheduledMessageDAO persistentScheduledMessageDAO,
            PersistentMessagePartDAO persistentMessagePartDAO,
            PersistentUserPropertyDAO persistentUserPropertyDAO,
            PersistentDevicePropertyDAO persistentDevicePropertyDAO,
            DeviceBasedTimeZoneGuesser timeZoneGuesser
    ) {
        this.executor = executor;

        this.universe = universe;
        this.messenger = messenger;
        this.systemTimeZone = systemTimeZone;

        this.persistentUserDAO = persistentUserDAO;
        this.persistentDeviceDAO = persistentDeviceDAO;
        this.persistentInboundMessageDAO = persistentInboundMessageDAO;
        this.persistentOutboundMessageDAO = persistentOutboundMessageDAO;
        this.persistentScheduledMessageDAO = persistentScheduledMessageDAO;
        this.persistentMessagePartDAO = persistentMessagePartDAO;
        this.persistentUserPropertyDAO = persistentUserPropertyDAO;
        this.persistentDevicePropertyDAO = persistentDevicePropertyDAO;

        this.timeZoneGuesser = timeZoneGuesser;
    }

    public void handleInboundMessage(PersistentDevice from, List<PersistentMessagePart> parts) throws Exception {
        synchronized (this.messageRunnable) {

            // TODO do in transaction (very important)
            PersistentInboundMessage inboundMessage = new PersistentInboundMessage();
            inboundMessage.setDeviceId(from.getId());
            inboundMessage.setTimeReceived(new Date());
            inboundMessage.setHandled(false);
            int messageId = this.persistentInboundMessageDAO.create(inboundMessage);

            for( int i=0; i<parts.size(); i++ ) {
                PersistentMessagePart part = parts.get(i);
                part.setSequenceNumber(i);
                part.setInboundMessageId(messageId);
                this.persistentMessagePartDAO.create(part);
            }
            // notify the handler
            this.messageRunnable.wakeup();
        }
    }

    public void start() {
        if( this.messageRunnable == null || this.messageRunnable.done == true ) {
            this.messageRunnable = new MessageRunnable();
            Thread thread = new Thread(this.messageRunnable);
            thread.start();
        }
    }

    public void stop() {
        if( this.messageRunnable != null ) {
            this.messageRunnable.stop();
        }
    }


    @Override
    public void sendMessage(InboundMessage sourceMessage, Message outboundMessage) throws ContainerException {
        JPAInboundMessage jpaInboundMessage = (JPAInboundMessage)sourceMessage;
        sendMessage(jpaInboundMessage.getSourcePersistentDevice(), outboundMessage, null, true);
    }

    @Override
    public void sendMessage(OutboundMessage message, boolean immediately) throws ContainerException {
        int userId = (int) message.getToUserId();
        PersistentDevice persistentDevice = getResponseDevice(userId);
        sendMessage(persistentDevice, message, userId, immediately);
    }

    public PersistentDevice getResponseDevice(long userId) throws ContainerException {
        return this.persistentDeviceDAO.findMostRecentForUserId((int)userId);
    }

    public void sendMessage(final PersistentDevice targetPersistentDevice, final Message message, final Integer targetUserId, boolean immediately) throws ContainerDataException {

        final Date now = new Date();

        // queue up the message
        final PersistentOutboundMessage persistentOutboundMessage = new PersistentOutboundMessage();
        if( targetUserId != null ) {
            persistentOutboundMessage.setTargetUserId(targetUserId);
        } else {
            persistentOutboundMessage.setTargetDeviceId(targetPersistentDevice.getId());
        }

        persistentOutboundMessage.setTimeLodged(now);
        // TODO do in transaction
        int persistentOutboundMessageId = this.persistentOutboundMessageDAO.create(persistentOutboundMessage);

        // create the parts
        List<Message.Part> parts = message.getParts();
        persistParts(parts, persistentOutboundMessageId, null, null);

        if( immediately ) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        // TODO do in a transaction
                        // pull everything out
                        List<PersistentMessagePart> toSend;
                        if( targetUserId != null ) {
                            toSend = persistentMessagePartDAO.findPendingOutboundByUserIdBefore(targetUserId, now);
                        } else {
                            // use the device
                            toSend = persistentMessagePartDAO.findPendingOutboundByDeviceIdBefore(targetPersistentDevice.getId(), now);
                        }
                        try {
                            String receipt = messenger.sendMessage(targetPersistentDevice, toSend);
                            persistentOutboundMessage.setTimeSent(now);
                            persistentOutboundMessage.setReceipt(receipt);
                            HashSet<Integer> sent = new HashSet<Integer>(toSend.size());
                            for( int i=0; i<toSend.size(); i++ ) {
                                PersistentMessagePart persistentMessagePart = toSend.get(i);
                                Integer outboundMessageId = persistentMessagePart.getOutboundMessageId();
                                if( !sent.contains(outboundMessageId) ) {
                                    sent.add(outboundMessageId);
                                    persistentOutboundMessageDAO.updateTimeSent(outboundMessageId, now);
                                }
                            }
                        } catch( Exception ex ) {
                            LOG.log(Level.WARNING, "sending outbound message failed "+message, ex);
                            // mark as failed (TODO retry at some point)
                            persistentOutboundMessage.setFailureReason(ExceptionUtils.getStackTrace(ex));
                        }
                        persistentOutboundMessageDAO.update(persistentOutboundMessage);
                    } catch( Exception ex) {
                        LOG.log(Level.SEVERE, "could not send outbound message "+message, ex);
                    }
                }
            };
            executor.execute(runnable);
        }
    }

    private void persistParts(List<Message.Part> parts, Integer persistentOutboundMessageId, Integer persistentInboundMessageId, Integer persistentScheduledMessageId) throws ContainerDataException {
        for( int i=0; i<parts.size(); i++ ) {
            Message.Part part = parts.get(i);
            PersistentMessagePart persistentMessagePart = new PersistentMessagePart();
            persistentMessagePart.setOutboundMessageId(persistentOutboundMessageId);
            persistentMessagePart.setInboundMessageId(persistentInboundMessageId);
            persistentMessagePart.setScheduledMessageId(persistentScheduledMessageId);
            persistentMessagePart.setSequenceNumber(i);
            persistentMessagePart.setContentType(part.getContentType());
            persistentMessagePart.setType(part.getType());
            persistentMessagePart.setPayload(part.getPayload());
            this.persistentMessagePartDAO.create(persistentMessagePart);
        }

    }

    @Override
    public long scheduleMessage(ScheduledMessage message) throws ContainerException {
        // queue the message
        PersistentScheduledMessage persistentScheduledMessage = new PersistentScheduledMessage();
        persistentScheduledMessage.setScheduledTime(message.getScheduledTime());
        persistentScheduledMessage.setInsertionTime(new Date());
        // TODO transaction
        int result = this.persistentScheduledMessageDAO.create(persistentScheduledMessage);

        persistParts(message.getParts(), null, null, result);

        return result;
    }

    @Override
    public void cancelScheduledMessage(long id) throws ContainerException {
        // delete the message
        this.persistentScheduledMessageDAO.updateCanceled((int)id, true);
    }

    @Override
    public void attachUser(InboundMessage sourceMessage, long existingUserId) throws ContainerException {
        attachUser(sourceMessage, existingUserId, null);
    }
    public void attachUser(InboundMessage sourceMessage, long existingUserId, PersistentUser user) throws ContainerException {
        // attach the communication method to an existing user
        JPAInboundMessage jpaInboundMessage = (JPAInboundMessage)sourceMessage;
        PersistentDevice sourcePersistentDevice = jpaInboundMessage.getSourcePersistentDevice();
        sourcePersistentDevice.setOwnerId((int)existingUserId);
        this.persistentDeviceDAO.update(sourcePersistentDevice);
        // attempt to guess timezone if we haven't already set it
        if( user == null ) {
            user = this.persistentUserDAO.find((int)existingUserId);
        }
        if( user.getTimeZoneId() == null ) {
            // attempt to guess

            String timeZoneId;
            try {
                timeZoneId = this.timeZoneGuesser.guessTimeZoneId(sourcePersistentDevice);
            } catch( Exception ex ) {
                // just move on
                LOG.log(Level.WARNING, "unable to guess timezone", ex);
                timeZoneId = null;
            }
            if( timeZoneId != null ) {
                user.setTimeZoneId(timeZoneId);
                this.persistentUserDAO.update(user);
            }
        }
    }

    @Override
    public long createUser(InboundMessage sourceMessage) throws ContainerException {
        // create a new persistentUser
        PersistentUser persistentUser = new PersistentUser();
        long result = this.persistentUserDAO.create(persistentUser);
        this.attachUser(sourceMessage, result, persistentUser);
        return result;
    }

    @Override
    public Device getPreferredDevice(long userId) throws ContainerException {
        PersistentDevice persistentDevice = getResponseDevice(userId);
        return new JPADevice(persistentDevice, this.persistentDevicePropertyDAO);
    }

    @Override
    public User getUser(long userId) throws ContainerException {
        PersistentUser persistentUser = this.persistentUserDAO.find((int)userId);
        return new JPAUser(persistentUser, this.systemTimeZone, this.persistentUserDAO, this.persistentUserPropertyDAO);
    }
}
