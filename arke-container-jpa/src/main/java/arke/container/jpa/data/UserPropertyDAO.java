package arke.container.jpa.data;

import java.util.List;

public interface UserPropertyDAO {
    List<UserProperty> findByUserId(int userId) throws ContainerDataException;

    UserProperty findByUserIdAndKey(int userId, String key) throws ContainerDataException;

    void create(UserProperty property) throws ContainerDataException;

    void update(UserProperty property) throws ContainerDataException;

    void delete(int id) throws ContainerDataException;

    void delete(int userId, String key) throws ContainerDataException;
}
