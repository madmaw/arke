package arke.container.jpa.data;

import arke.ContainerDataException;

import java.util.List;

public interface PersistentDevicePropertyDAO {

    List<PersistentDeviceProperty> findByDeviceId(int deviceId) throws ContainerDataException;

    PersistentDeviceProperty findByDeviceIdAndKey(int deviceId, String key) throws ContainerDataException;

    void create(PersistentDeviceProperty property) throws ContainerDataException;

    void update(PersistentDeviceProperty property) throws ContainerDataException;

    void delete(int id) throws ContainerDataException;

}
