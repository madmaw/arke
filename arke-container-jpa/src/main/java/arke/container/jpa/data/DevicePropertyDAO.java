package arke.container.jpa.data;

import java.util.List;

public interface DevicePropertyDAO {

    List<DeviceProperty> findByDeviceId(int deviceId) throws ContainerDataException;

    DeviceProperty findByDeviceIdAndKey(int deviceId, String key) throws ContainerDataException;

    void create(DeviceProperty property) throws ContainerDataException;

    void update(DeviceProperty property) throws ContainerDataException;

    void delete(int id) throws ContainerDataException;

}
