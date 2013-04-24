package arke.sample.rps.data;

import arke.ContainerDataException;

import java.util.List;

public interface PlayerDAO {

    int create(Player player) throws ContainerDataException;

    void update(Player player) throws ContainerDataException;

    Player findByName(String name) throws ContainerDataException;

    Player findByUserId(long userId) throws ContainerDataException;

    Player find(int id) throws ContainerDataException;

    List<Player> findByCurrentGameId(int gameId) throws ContainerDataException;

    List<Player> findAvailable(int max) throws ContainerDataException;
}
