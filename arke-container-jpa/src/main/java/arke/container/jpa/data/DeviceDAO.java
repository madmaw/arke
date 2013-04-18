package arke.container.jpa.data;

public interface DeviceDAO {
    int create(Device device) throws ContainerDataException;

    void update(Device device) throws ContainerDataException;

    int createOrUpdate(Device device) throws ContainerDataException;

    Device find(int id) throws ContainerDataException;

    Device findMostRecentForUserId(int userId) throws ContainerDataException;
}
