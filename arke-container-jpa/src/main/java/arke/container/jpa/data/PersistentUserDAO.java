package arke.container.jpa.data;

import arke.ContainerDataException;

public interface PersistentUserDAO {

    int create(PersistentUser persistentUser) throws ContainerDataException;

    void update(PersistentUser persistentUser) throws ContainerDataException;

    PersistentUser find(int userId) throws ContainerDataException;
}
