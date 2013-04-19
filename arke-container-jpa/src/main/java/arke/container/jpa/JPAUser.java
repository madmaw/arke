package arke.container.jpa;

import arke.ContainerException;
import arke.User;
import arke.container.jpa.data.*;

import java.util.*;

public class JPAUser implements User {

    private PersistentUser user;
    private TimeZone systemTimeZone;

    private PersistentUserDAO persistentUserDAO;
    private PersistentUserPropertyDAO persistentUserPropertyDAO;

    public JPAUser(PersistentUser user, TimeZone systemTimeZone, PersistentUserDAO persistentUserDAO, PersistentUserPropertyDAO persistentUserPropertyDAO) {
        this.user = user;
        this.systemTimeZone = systemTimeZone;
        this.persistentUserDAO = persistentUserDAO;
        this.persistentUserPropertyDAO = persistentUserPropertyDAO;
    }

    @Override
    public Map<String, String> getProperties() throws ContainerException {

        List<PersistentUserProperty> persistentUserProperties = this.persistentUserPropertyDAO.findByUserId((int) this.user.getId());
        HashMap<String, String> result = new HashMap<String, String>(persistentUserProperties.size());

        for( PersistentUserProperty persistentUserProperty : persistentUserProperties) {
            result.put(persistentUserProperty.getKey(), persistentUserProperty.getValue());
        }

        return result;
    }

    @Override
    public void setProperty(String key, String value) throws ContainerException {
        PersistentUserProperty property = this.persistentUserPropertyDAO.findByUserIdAndKey(this.user.getId(), key);
        if( property != null ) {
            property.setValue(value);
            this.persistentUserPropertyDAO.update(property);
        } else {
            property = new PersistentUserProperty();
            property.setUserId(this.user.getId());
            property.setKey(key);
            property.setValue(value);
            this.persistentUserPropertyDAO.create(property);
        }
    }

    @Override
    public void removeProperty(String key) throws ContainerException {
        this.persistentUserPropertyDAO.delete((int) this.user.getId(), key);
    }

    @Override
    public void block(String reasonBlocked) throws ContainerException {
        PersistentUser persistentUser = this.persistentUserDAO.find((int)this.user.getId());
        persistentUser.setBlocked(true);
        persistentUser.setBlockReason(reasonBlocked);
        this.persistentUserDAO.update(persistentUser);
    }

    @Override
    public TimeZone getTimeZone() throws ContainerException {
        String timeZoneId = this.user.getTimeZoneId();
        if( timeZoneId != null ) {
            return TimeZone.getTimeZone(timeZoneId);
        } else {
            return this.systemTimeZone;
        }
    }

    @Override
    public void setTimeZone(TimeZone timeZone) throws ContainerException {
        if( timeZone != null ) {
            this.user.setTimeZoneId(timeZone.getID());
        } else {
            this.user.setTimeZoneId(null);
        }
        this.persistentUserDAO.update(this.user);
    }
}
