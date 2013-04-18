package arke.container.jpa.data;

public interface UserDAO {

    int create(User user) throws ContainerDataException;

    void update(User user) throws ContainerDataException;

    User find(int userId) throws ContainerDataException;
}
