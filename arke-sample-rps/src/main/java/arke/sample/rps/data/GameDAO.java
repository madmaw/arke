package arke.sample.rps.data;

import arke.ContainerDataException;

public interface GameDAO {
    int create(Game game) throws ContainerDataException;

    void update(Game game) throws ContainerDataException;

    Game find(int id) throws ContainerDataException;
}
