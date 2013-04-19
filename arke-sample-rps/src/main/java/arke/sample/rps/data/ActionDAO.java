package arke.sample.rps.data;

import arke.ContainerDataException;

import java.util.List;

public interface ActionDAO {

    int create(Action action) throws ContainerDataException;

    void update(Action action) throws ContainerDataException;

    int createOrUpdate(Action action) throws ContainerDataException;

    List<Action> findForGameIdAndTurn(int gameId, int turnNumber) throws ContainerDataException;

    Action findForGameIdPlayerIdAndTurn(int gameId, int playerId, int turnNumber) throws ContainerDataException;
}
