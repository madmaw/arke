package arke.container.jpa.messenger.sms.android;

import android.content.Intent;
import android.os.Bundle;
import arke.ContainerException;
import arke.ContentTypeUtils;
import arke.container.jpa.JPAContainer;
import arke.container.jpa.data.PersistentDevice;
import arke.container.jpa.data.PersistentDeviceDAO;
import arke.container.jpa.data.PersistentMessagePart;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

public class AndroidIntentDeliveryHandler {

    private static final Logger LOG = Logger.getLogger(AndroidIntentDeliveryHandler.class.getName());

    public static final String FIELD_MESSAGE = "message";
    public static final String FIELD_ADDRESS = "address";
    public static final String FIELD_TYPE = "type";

    public static final String ENCODING = "utf-8";

    private JPAContainer container;

    private PersistentDeviceDAO persistentDeviceDAO;

    public AndroidIntentDeliveryHandler(
            JPAContainer container,
            PersistentDeviceDAO persistentDeviceDAO
    ) {
        this.container = container;
        this.persistentDeviceDAO = persistentDeviceDAO;
    }

    public void deliverIntent(Intent intent) throws Exception {
        if( intent != null ) {

            Bundle bundle = intent.getExtras();
            if( bundle != null ) {
                String message = bundle.getString(FIELD_MESSAGE);
                String address = bundle.getString(FIELD_ADDRESS);
                String type = bundle.getString(FIELD_TYPE);

                if( type != null && address != null && message != null ) {
                    LOG.info("got message "+message+" from "+address+" of type "+type);

                    PersistentDevice device = this.persistentDeviceDAO.findByNameAndType(address, type);
                    if( device == null ) {
                        device = new PersistentDevice();
                        device.setDeviceName(address);
                        device.setDeviceType(type);
                    }
                    device.setLastUsed(new Date());
                    int id = persistentDeviceDAO.createOrUpdate(device);

                    ArrayList<PersistentMessagePart> parts = new ArrayList<PersistentMessagePart>(1);
                    PersistentMessagePart part = new PersistentMessagePart();
                    part.setSequenceNumber(0);
                    part.setContentType(ContentTypeUtils.toTextPlainMimeType(ENCODING));
                    part.setPayload(message.getBytes(ENCODING));
                    parts.add(part);

                    this.container.handleInboundMessage(
                            device,
                            parts
                    );
                }
            }
        }
    }
}
