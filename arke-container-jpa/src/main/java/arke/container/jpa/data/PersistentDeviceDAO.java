package arke.container.jpa.data;

import arke.ContainerDataException;

public interface PersistentDeviceDAO {
    int create(PersistentDevice persistentDevice) throws ContainerDataException;

    void update(PersistentDevice persistentDevice) throws ContainerDataException;

    int createOrUpdate(PersistentDevice persistentDevice) throws ContainerDataException;

    PersistentDevice find(int id) throws ContainerDataException;

    PersistentDevice findMostRecentForUserId(int userId) throws ContainerDataException;

    PersistentDevice findByNameAndType(String name, String type) throws ContainerDataException;
}
