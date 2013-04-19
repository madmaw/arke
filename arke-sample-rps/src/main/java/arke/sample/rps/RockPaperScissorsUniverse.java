package arke.sample.rps;

import arke.*;
import arke.container.template.TemplateContainer;
import arke.container.template.TemplateUniverse;
import arke.sample.rps.data.*;

import java.util.*;

public class RockPaperScissorsUniverse implements TemplateUniverse {

    public static final int MIN_NAME_LENGTH = 3;
    public static final int MAX_NAME_LENGTH = 9;

    public static final Action.Type[] VALID_AI_ACTION_TYPES = {Action.Type.ROCK, Action.Type.PAPER, Action.Type.SCISSORS};
    public static final Action.Type[] VALID_PLAYER_ACTION_TYPES = {Action.Type.ROCK, Action.Type.PAPER, Action.Type.SCISSORS, Action.Type.QUIT};

    public static final String TEMPLATE_ID_CREATE_PLAYER_FAILED_NAME_TOO_SHORT = "create_player_failed_name_too_short";
    public static final String TEMPLATE_ID_CREATE_PLAYER_FAILED_NAME_ALREADY_EXISTS = "create_player_failed_name_already_exists";
    public static final String TEMPLATE_ID_CREATE_PLAYER_FAILED_NAME_TOO_LONG = "create_player_failed_name_too_long";
    public static final String TEMPLATE_ID_CREATE_PLAYER_SUCCESS = "create_player_success";
    public static final String TEMPLATE_ID_PLAY_PLAYER_REMOVED = "play_player_removed";
    public static final String TEMPLATE_ID_PLAY_TURN_START = "play_turn_start";
    public static final String TEMPLATE_ID_PLAY_WIN = "play_win";

    private PlayerDAO playerDAO;
    private GameDAO gameDAO;
    private ActionDAO actionDAO;
    private Random random;
    private long turnIntervalMillis;

    public RockPaperScissorsUniverse(PlayerDAO playerDAO, GameDAO gameDAO, ActionDAO actionDAO, long turnIntervalMillis) {
        this.playerDAO = playerDAO;
        this.gameDAO = gameDAO;
        this.actionDAO = actionDAO;
        this.random = new Random();
        this.turnIntervalMillis = turnIntervalMillis;
    }

    @Override
    public void handleMessage(TemplateContainer container, Message message) throws Exception {
        MessageWrapper wrapper = new MessageWrapper(message);
        if( message instanceof InboundMessage ) {
            // this is what we expect
            InboundMessage inboundMessage = (InboundMessage)message;
            Long sourceUserId = inboundMessage.getSourceUserId();
            if( sourceUserId == null ) {
                // assume they are creating an account
                handleCreateAccount(container, inboundMessage, sourceUserId, wrapper.getText());
            } else {
                Player player = this.playerDAO.findByUserId(sourceUserId);
                if( player == null ) {
                    handleCreateAccount(container, inboundMessage, sourceUserId, wrapper.getText());
                } else {
                    // are they playing a game?
                    if( player.getCurrentGameId() == null ) {
                        handleLobbyAction(container, inboundMessage, player, wrapper.getText());
                    } else {
                        handleGameAction(container, inboundMessage, player, wrapper.getText());
                    }
                }
            }
        } else if( message instanceof ScheduledMessage) {
            ScheduledMessage scheduledMessage = (ScheduledMessage)message;
            // it's a timeout (probably), do something with that
            handleTimeout(container, scheduledMessage, wrapper.getText());
        }
    }

    private void handleCreateAccount(TemplateContainer container, InboundMessage inboundMessage, Long sourceUserId, String text) throws Exception {
        // go until the first white space

        StringTokenizer st = new StringTokenizer(text);
        final String name = st.nextToken();
        if( name == null || name.length() < MIN_NAME_LENGTH ) {
            container.sendMessage(
                    inboundMessage,
                    new String[]{TEMPLATE_ID_CREATE_PLAYER_FAILED_NAME_TOO_SHORT},
                    new HashMap<String, Object>(){{ put("name", name); put("minlength", MIN_NAME_LENGTH); }}
            );
        } else if( name.length() > MAX_NAME_LENGTH ) {
            container.sendMessage(
                    inboundMessage,
                    new String[]{TEMPLATE_ID_CREATE_PLAYER_FAILED_NAME_TOO_LONG},
                    new HashMap<String, Object>(){{ put("name", name); put("maxlength", MAX_NAME_LENGTH); }}
            );
        } else {
            // does the name exist
            Player player = this.playerDAO.findByName(name);
            if( player != null ) {
                container.sendMessage(inboundMessage, new String[]{TEMPLATE_ID_CREATE_PLAYER_FAILED_NAME_ALREADY_EXISTS}, new HashMap<String, Object>(){{ put("name", name); }});
            } else {
                if( sourceUserId == null ) {
                    sourceUserId = container.createUser(inboundMessage);
                }

                // create a player
                player = new Player();
                player.setName(name);
                player.setUserId(sourceUserId);

                int playerId = this.playerDAO.create(player);

                // is there a timezone?
                if( st.hasMoreTokens() ) {
                    String timeZoneId = st.nextToken();
                    if( timeZoneId != null && timeZoneId.length() > 0 ) {
                        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
                        User user = container.getUser(sourceUserId);
                        user.setTimeZone(timeZone);
                    }
                }
            }
        }

    }

    private void handleGameAction(TemplateContainer container, InboundMessage inboundMessage, final Player player, String text) throws Exception {
        StringTokenizer st = new StringTokenizer(text);
        String command = st.nextToken();
        Action.Type actionType = Action.Type.valueOf(command);
        if( actionType == null || !actionType.isUserDriven() ) {
            handleCommonAction(container, inboundMessage, player, text);
        } else {
            registerPlayerMove(container, player, actionType);
        }
    }

    private void registerPlayerMove(TemplateContainer container, final Player player, final Action.Type actionType) throws Exception {
            // look up the game
        final Game game = gameDAO.find(player.getCurrentGameId());
        Action action = actionDAO.findForGameIdPlayerIdAndTurn(game.getId(), player.getId(), game.getTurnNumber());
        if( action == null ) {
            action = new Action();
            action.setGameId(game.getId());
            action.setPlayerId(player.getId());
            action.setTime(new Date());
            action.setTurn(game.getTurnNumber());
        }
        action.setType(actionType);
        actionDAO.createOrUpdate(action);

        final Action playerAction = action;

        // check the game state
        List<Player> allPlayers = this.playerDAO.findByCurrentGameId(game.getId());
        HashMap<Integer, Player> players = new HashMap<Integer, Player>(allPlayers.size());
        HashSet<Integer> unmovedPlayerIds = new HashSet<Integer>(allPlayers.size());
        for( final Player allPlayer : allPlayers ) {
            unmovedPlayerIds.add(allPlayer.getId());
            players.put(allPlayer.getId(), allPlayer);
            // report the move
            if( allPlayer.getUserId() != null ) {
                container.sendMessage(
                        allPlayer.getUserId(),
                        new String[]{},
                        new HashMap<String, Object>(){{
                            put("recipient", allPlayer);
                            put("actor", player);
                            put("action", playerAction);
                        }},
                        false
                );
            }
        }
        List<Action> allActions = actionDAO.findForGameIdAndTurn(game.getId(), game.getTurnNumber());
        for( Action allAction : allActions ) {
            unmovedPlayerIds.remove(allAction.getPlayerId());
        }
        boolean allPlayersMoved = unmovedPlayerIds.size() == 0;
        if( allPlayersMoved ) {
            // remove the scheduled message
            if( game.getScheduledTimeoutMessageId() != null ) {
                container.cancelScheduledMessage(game.getScheduledTimeoutMessageId());
            }
            HashSet<Integer> unsuccessfulActionIds = new HashSet<Integer>(allActions.size());
            for( int i=0; i<allActions.size(); i++ ) {
                Action action1 = allActions.get(i);
                for( int j=allActions.size(); j>0; ) {
                    j--;
                    if( i != j ) {
                        Action action2 = allActions.get(j);
                        int comparison = action2.getType().compare(action1.getType());
                        if( comparison < 0 ) {
                            unsuccessfulActionIds.add(action2.getId());
                        }
                    }
                }
            }
            // remove any users with unsuccessful action ids from the game
            if( unsuccessfulActionIds.size() < allActions.size() ) {
                for( Action allAction : allActions ) {
                    if( unsuccessfulActionIds.contains( allAction.getId() ) ) {
                        final Player removedPlayer = players.remove(allAction.getPlayerId());
                        if( removedPlayer != null ) {
                            removedPlayer.setCurrentGameId(null);
                            playerDAO.update(removedPlayer);
                            for( final Player allPlayer : allPlayers ) {
                                if( allPlayer.getUserId() != null ) {
                                    container.sendMessage(
                                            allPlayer.getUserId(),
                                            new String[]{TEMPLATE_ID_PLAY_PLAYER_REMOVED},
                                            new HashMap<String, Object>(){{
                                                put("removed", removedPlayer);
                                                put("recipient", allPlayer);
                                            }},
                                            removedPlayer.getId().equals(allPlayer.getId())
                                    );
                                }
                            }
                        }
                    }
                }
            }
            // get the remaining players in the game
            if( players.size() == 0 ) {
                // UH OH!
                throw new ContainerException("somehow ended up with zero users!");
            } else if( players.size() == 1 ) {
                // winner!
                final Player winner = players.values().iterator().next();
                winner.setCurrentGameId(null);
                winner.setGamesWon(winner.getGamesWon() + 1);
                playerDAO.update(winner);
                if( winner.getUserId() != null ) {
                    container.sendMessage(
                            winner.getUserId(),
                            new String[]{TEMPLATE_ID_PLAY_WIN},
                            new HashMap<String, Object>(){{
                                put("turnNumber", game.getTurnNumber());
                                put("recipient", winner);
                                put("winner", winner);
                            }},
                            true
                    );
                }
            } else {
                // keep going
                startTurn(container, game, game.getTurnNumber() + 1);
            }
        }
    }

    private void startTurn(TemplateContainer container, Game game, int turnNumber) throws Exception {
        final List<Player> players = this.playerDAO.findByCurrentGameId(game.getId());

        for( final Player player : players ) {
            if( player.getUserId() != null ) {
                // notify of turn starting
                container.sendMessage(
                        player.getUserId(),
                        new String[]{TEMPLATE_ID_PLAY_TURN_START},
                        new HashMap<String, Object>(){{
                            put("recipient", player);
                            put("players", players);
                        }},
                        true
                );
            }
        }

        for( Player player : players ) {
            if( player.getUserId() == null ) {
                // make an AI turn
                int index = random.nextInt(VALID_AI_ACTION_TYPES.length);
                Action.Type actionType = VALID_AI_ACTION_TYPES[index];
                registerPlayerMove(container, player, actionType);
            }
        }
        // lodge a timeout
        Date date = new Date();
        date.setTime(date.getTime() + this.turnIntervalMillis);
        ScheduledMessage scheduledMessage = new ScheduledMessage(date);
        // TODO scheduled message should have a property-style interface
        container.scheduleMessage()
        gameDAO.update(game);
    }

    private void handleLobbyAction(TemplateContainer container, InboundMessage inboundMessage, Player player, String text) throws Exception {

    }

    private void handleCommonAction(TemplateContainer container, InboundMessage inboundMessage, Player player, String text) throws Exception {

    }

    private void handleTimeout(TemplateContainer container, ScheduledMessage scheduledMessage, String text) throws Exception {

    }
}
