package arke.container.jpa.data;

import arke.ContainerDataException;

import java.util.List;

public interface PersistentUserPropertyDAO {
    List<PersistentUserProperty> findByUserId(int userId) throws ContainerDataException;

    PersistentUserProperty findByUserIdAndKey(int userId, String key) throws ContainerDataException;

    void create(PersistentUserProperty property) throws ContainerDataException;

    void update(PersistentUserProperty property) throws ContainerDataException;

    void delete(int id) throws ContainerDataException;

    void delete(int userId, String key) throws ContainerDataException;
}
