package arke.container.jpa.data.ormlite;

import arke.*;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.Callable;

public class TransactionalORMLiteContainerProxy implements Container {

    private Container proxied;
    private ConnectionSource connectionSource;

    public TransactionalORMLiteContainerProxy(ConnectionSource connectionSource, Container proxied) {
        this.proxied = proxied;
        this.connectionSource = connectionSource;
    }

    @Override
    public void sendMessage(final InboundMessage sourceMessage, final Message outboundMessage) throws ContainerException {
        try {
            TransactionManager.callInTransaction(
                    this.connectionSource,
                    new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            proxied.sendMessage(sourceMessage, outboundMessage);
                            return null;
                        }
                    }
            );
        } catch( SQLException ex ) {
            throw new ContainerException(ex);
        }
    }

    @Override
    public void sendMessage(final OutboundMessage message, final boolean immediately) throws ContainerException {
        try {
            TransactionManager.callInTransaction(
                    this.connectionSource,
                    new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            proxied.sendMessage(message, immediately);
                            return null;
                        }
                    }
            );
        } catch( SQLException ex ) {
            throw new ContainerException(ex);
        }

    }

    @Override
    public long scheduleMessage(final ScheduledMessage message) throws ContainerException {
        try {
            return TransactionManager.callInTransaction(
                    this.connectionSource,
                    new Callable<Long>() {
                        @Override
                        public Long call() throws Exception {
                            return proxied.scheduleMessage(message);
                        }
                    }
            );
        } catch( SQLException ex ) {
            throw new ContainerException(ex);
        }
    }

    @Override
    public void cancelScheduledMessage(final long id) throws ContainerException {
        try {
            TransactionManager.callInTransaction(
                    this.connectionSource,
                    new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            proxied.cancelScheduledMessage(id);
                            return null;
                        }
                    }
            );
        } catch( SQLException ex ) {
            throw new ContainerException(ex);
        }
    }

    @Override
    public void attachUser(final InboundMessage sourceMessage, final long existingUserId) throws ContainerException {
        try {
            TransactionManager.callInTransaction(
                    this.connectionSource,
                    new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            proxied.attachUser(sourceMessage, existingUserId);
                            return null;
                        }
                    }
            );
        } catch( SQLException ex ) {
            throw new ContainerException(ex);
        }
    }

    @Override
    public long createUser(final InboundMessage sourceMessage) throws ContainerException {
        try {
            return TransactionManager.callInTransaction(
                    this.connectionSource,
                    new Callable<Long>() {
                        @Override
                        public Long call() throws Exception {
                            return proxied.createUser(sourceMessage);
                        }
                    }
            );
        } catch( SQLException ex ) {
            throw new ContainerException(ex);
        }
    }

    @Override
    public Device getPreferredDevice(long userId) throws ContainerException {
        // should this be a proxied device?
        return proxied.getPreferredDevice(userId);
    }

    @Override
    public User getUser(long userId) throws ContainerException {
        // should this be a proxied device?
        return proxied.getUser(userId);
    }
}
