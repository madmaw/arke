package arke.container.jpa;

import arke.ContainerException;
import arke.Device;
import arke.container.jpa.data.PersistentDevice;
import arke.container.jpa.data.PersistentDeviceProperty;
import arke.container.jpa.data.PersistentDevicePropertyDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JPADevice implements Device {

    private PersistentDevice device;
    private PersistentDevicePropertyDAO persistentDevicePropertyDAO;

    public JPADevice(PersistentDevice device, PersistentDevicePropertyDAO persistentDevicePropertyDAO) {
        this.device = device;
        this.persistentDevicePropertyDAO = persistentDevicePropertyDAO;
    }

    @Override
    public Map<String, String> getProperties(long userId) throws ContainerException {

        List<PersistentDeviceProperty> persistentDeviceProperties = this.persistentDevicePropertyDAO.findByDeviceId(this.device.getId());
        HashMap<String, String> result = new HashMap<String, String>(persistentDeviceProperties.size());

        for( PersistentDeviceProperty persistentDeviceProperty : persistentDeviceProperties) {
            result.put(persistentDeviceProperty.getKey(), persistentDeviceProperty.getValue());
        }

        return result;
    }

}
