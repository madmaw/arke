package arke.sample.rps;

import arke.*;
import arke.container.template.TemplateContainer;
import arke.container.template.TemplateUniverse;
import arke.sample.rps.data.*;

import java.util.*;
import java.util.logging.Logger;

public class RockPaperScissorsUniverse implements TemplateUniverse {

    private static final Logger LOG = Logger.getLogger(RockPaperScissorsUniverse.class.getName());

    public static final int MIN_NAME_LENGTH = 3;
    public static final int MAX_NAME_LENGTH = 9;

    public static final Action.Type[] VALID_AI_ACTION_TYPES = {Action.Type.ROCK, Action.Type.PAPER, Action.Type.SCISSORS};

    public static final String TEMPLATE_ID_CREATE_PLAYER_FAILED_NAME_TOO_SHORT = "create_player_failed_name_too_short";
    public static final String TEMPLATE_ID_CREATE_PLAYER_FAILED_NAME_ALREADY_EXISTS = "create_player_failed_name_already_exists";
    public static final String TEMPLATE_ID_CREATE_PLAYER_FAILED_NAME_TOO_LONG = "create_player_failed_name_too_long";
    public static final String TEMPLATE_ID_CREATE_PLAYER_SUCCESS = "create_player_success";
    public static final String TEMPLATE_ID_PLAY_PLAYER_MOVED = "play_player_moved";
    public static final String TEMPLATE_ID_PLAY_PLAYER_REMOVED = "play_player_removed";
    public static final String TEMPLATE_ID_PLAY_TURN_START = "play_turn_start";
    public static final String TEMPLATE_ID_PLAY_WIN = "play_win";
    public static final String TEMPLATE_ID_LOBBY_ENTERED = "lobby_entered";
    public static final String TEMPLATE_ID_GAME_STARTED = "game_started";
    public static final String TEMPLATE_ID_LOBBY_UNRECOGNISED_COMMAND = "lobby_unrecognised_command";
    public static final String TEMPLATE_ID_PLAY_UNRECOGNISED_COMMAND = "play_unrecognised_command";
    public static final String TEMPLATE_ID_NO_SUCH_PLAYER = "no_such_player";
    public static final String TEMPLATE_ID_PLAYER_BUSY = "player_busy";
    public static final String TEMPLATE_ID_PLAYER_INACTIVE = "player_inactive";
    public static final String TEMPLATE_ID_TIMEZONE_SET = "timezone_set";
    public static final String TEMPLATE_ID_LEFT_GAME = "left_game";


    public static final String PROPERTY_GAME_ID = "game_id";
    public static final String PROPERTY_TURN_NUMBER = "turn_number";

    public static final String ACTION_START = "START";
    public static final String ACTION_STOP = "STOP";
    public static final String ACTION_TIMEZONE = "TZ";

    private PlayerDAO playerDAO;
    private GameDAO gameDAO;
    private ActionDAO actionDAO;
    private Random random;
    private long turnIntervalMillis;
    private TimeZone systemTimeZone;

    public RockPaperScissorsUniverse(PlayerDAO playerDAO, GameDAO gameDAO, ActionDAO actionDAO, long turnIntervalMillis, TimeZone systemTimeZone) {
        this.playerDAO = playerDAO;
        this.gameDAO = gameDAO;
        this.actionDAO = actionDAO;
        this.random = new Random();
        this.turnIntervalMillis = turnIntervalMillis;
        this.systemTimeZone = systemTimeZone;
    }

    @Override
    public void handleMessage(TemplateContainer container, Message message) throws Exception {
        MessageWrapper wrapper = new MessageWrapper(message);
        String text = wrapper.getText();
        LOG.info("received message "+ text);
        if( message instanceof InboundMessage ) {
            // this is what we expect
            InboundMessage inboundMessage = (InboundMessage)message;
            Long sourceUserId = inboundMessage.getSourceUserId();
            if( sourceUserId == null ) {
                // assume they are creating an account
                handleCreateAccount(container, inboundMessage, sourceUserId, text);
            } else {
                Player player = this.playerDAO.findByUserId(sourceUserId);
                if( player == null ) {
                    handleCreateAccount(container, inboundMessage, sourceUserId, text);
                } else {
                    // are they playing a game?
                    if( player.getCurrentGameId() == null ) {
                        handleLobbyAction(container, inboundMessage, player, text);
                    } else {
                        Game game = gameDAO.find(player.getCurrentGameId());
                        handleGameAction(container, inboundMessage, player, text, game.getTurnNumber());
                    }
                }
            }
        } else if( message instanceof ScheduledMessage) {
            ScheduledMessage scheduledMessage = (ScheduledMessage)message;
            // it's a timeout (probably), do something with that
            handleTimeout(container, scheduledMessage);
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
                    new HashMap<String, Object>() {{
                        put("name", name);
                        put("minlength", MIN_NAME_LENGTH);
                    }}
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
                        handleTimeZoneChange(container, sourceUserId, timeZoneId, false);
                    }
                }

                final Player newPlayer = player;
                container.sendMessage(sourceUserId, new String[]{TEMPLATE_ID_CREATE_PLAYER_SUCCESS}, new HashMap<String, Object>(){{
                    put("player", newPlayer);
                }}, false);
                notifyEnteredLobby(container, player);
            }
        }

    }



    private void handleGameAction(TemplateContainer container, InboundMessage inboundMessage, final Player player, String text, int turnNumber) throws Exception {
        StringTokenizer st = new StringTokenizer(text);
        String command = st.nextToken();
        Action.Type actionType;
        try {
            actionType = Action.Type.valueOf(command.toUpperCase());
        } catch ( IllegalArgumentException ex ) {
            actionType = null;
        }
        if( actionType != null ) {
            registerPlayerMove(container, player, actionType, turnNumber);
            if( actionType == Action.Type.STOP) {
                // stop
                handleStopAction(container, player, actionType);
            }
        } else {
            handleUnrecognisedCommand(container, inboundMessage, player, command, false);
        }
    }

    private void registerPlayerMove(TemplateContainer container, final Player player, final Action.Type actionType, int turnNumber) throws Exception {
            // look up the game
        int gameId = player.getCurrentGameId();
        Action action = actionDAO.findForGameIdPlayerIdAndTurn(gameId, player.getId(), turnNumber);
        if( action == null ) {
            action = new Action();
            action.setGameId(gameId);
            action.setPlayerId(player.getId());
            action.setTime(new Date());
            action.setTurn(turnNumber);
        }
        action.setType(actionType);
        actionDAO.createOrUpdate(action);

        checkGameState(container, gameId);
    }

    private void checkGameState(TemplateContainer container, int gameId) throws Exception {
        final Game game = gameDAO.find(gameId);
        List<Player> allPlayers = this.playerDAO.findByCurrentGameId(game.getId());
        HashSet<Integer> unmovedPlayerIds = new HashSet<Integer>(allPlayers.size());
        HashMap<Integer, Player> players = new HashMap<Integer, Player>(allPlayers.size());
        for( final Player allPlayer : allPlayers ) {
            unmovedPlayerIds.add(allPlayer.getId());
            players.put(allPlayer.getId(), allPlayer);
        }
        // check the game state
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
                final Action action1 = allActions.get(i);
                for( final Player allPlayer : allPlayers ) {
                    // report the move
                    if( allPlayer.getUserId() != null && !allPlayer.isInactive() ) {
                        final Player player = playerDAO.find(action1.getPlayerId());
                        container.sendMessage(
                                allPlayer.getUserId(),
                                new String[]{TEMPLATE_ID_PLAY_PLAYER_MOVED},
                                new HashMap<String, Object>(){{
                                    put("recipient", allPlayer);
                                    put("actor", player);
                                    put("action", action1);
                                }},
                                false
                        );
                    }
                }

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
                                if( allPlayer.getUserId() != null && !allPlayer.isInactive() && allAction.getType().isMove() ) {
                                    container.sendMessage(
                                            allPlayer.getUserId(),
                                            new String[]{TEMPLATE_ID_PLAY_PLAYER_REMOVED},
                                            new HashMap<String, Object>(){{
                                                put("removed", removedPlayer);
                                                put("recipient", allPlayer);
                                            }},
                                            false
                                    );
                                    if( removedPlayer.getId().equals(allPlayer.getId()) ) {
                                        // notify that the player has entered the lobby
                                        notifyEnteredLobby(container, allPlayer);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // get the remaining players in the game
            if( players.size() == 0 ) {
                // UH OH! Everyone quit?

            } else if( players.size() == 1 ) {
                // winner!
                final Player winner = players.values().iterator().next();
                winner.setCurrentGameId(null);
                winner.setGamesWon(winner.getGamesWon() + 1);
                playerDAO.update(winner);
                if( winner.getUserId() != null && !winner.isInactive() ) {
                    container.sendMessage(
                            winner.getUserId(),
                            new String[]{TEMPLATE_ID_PLAY_WIN},
                            new HashMap<String, Object>(){{
                                put("turnNumber", game.getTurnNumber());
                                put("recipient", winner);
                                put("winner", winner);
                            }},
                            false
                    );
                    notifyEnteredLobby(container, winner);
                }
            } else {
                // keep going
                startTurn(container, game, game.getTurnNumber() + 1, null, null);
            }
        }
    }

    private void startTurn(TemplateContainer container, Game game, final int turnNumber, final Player challengingPlayer, final Action.Type firstMove) throws Exception {
        final Date date = new Date();
        date.setTime(date.getTime() + this.turnIntervalMillis);

        // update the turn number
        game.setTurnNumber(turnNumber);

        // lodge a timeout
        ScheduledMessage scheduledMessage = new ScheduledMessage(date);
        MessageWrapper wrapper = new MessageWrapper(scheduledMessage);
        wrapper.setPlainTextPart(PROPERTY_GAME_ID, Integer.toString(game.getId()));
        wrapper.setPlainTextPart(PROPERTY_TURN_NUMBER, Integer.toString(turnNumber));
        long scheduledMessageId = container.scheduleMessage(scheduledMessage);
        game.setScheduledTimeoutMessageId(scheduledMessageId);

        gameDAO.update(game);

        final List<Player> players = this.playerDAO.findByCurrentGameId(game.getId());

        for( final Player player : players ) {
            if( player.getUserId() != null && !player.isInactive() ) {

                // convert the specified date to a local date
                TimeZone timeZone = container.getUser(player.getUserId()).getTimeZone();
                final Date localTimeout;
                if( timeZone != null ) {
                    long adjustedTime = date.getTime() - this.systemTimeZone.getOffset(date.getTime()) + timeZone.getOffset(date.getTime());
                    localTimeout = new Date(adjustedTime);
                } else {
                    localTimeout = date;
                }

                // notify of turn starting
                container.sendMessage(
                        player.getUserId(),
                        new String[]{TEMPLATE_ID_PLAY_TURN_START},
                        new HashMap<String, Object>(){{
                            put("turn", turnNumber);
                            put("recipient", player);
                            put("players", players);
                            if( challengingPlayer == null || firstMove == null || !challengingPlayer.getId().equals(player.getId()) ) {
                                put("timeout", localTimeout);
                            } else {
                                put("move", firstMove);
                            }
                        }},
                        true
                );
            }
        }

        if( challengingPlayer != null && firstMove != null ) {
            registerPlayerMove(container, challengingPlayer, firstMove, turnNumber);
        }

        for( Player player : players ) {
            if( player.getUserId() == null ) {
                // make an AI turn
                int index = random.nextInt(VALID_AI_ACTION_TYPES.length);
                Action.Type actionType = VALID_AI_ACTION_TYPES[index];
                registerPlayerMove(container, player, actionType, turnNumber);
            }
        }
    }

    private void handleLobbyAction(TemplateContainer container, InboundMessage inboundMessage, Player player, String text) throws Exception {
        StringTokenizer st = new StringTokenizer(text);
        String command = st.nextToken();
        if( ACTION_START.equalsIgnoreCase(command) ) {
            handleStartAction(container, inboundMessage, player, st, null);
        } else if( ACTION_STOP.equalsIgnoreCase(command) ) {
            handleStopAction(container, player, Action.Type.STOP);
        } else if( ACTION_TIMEZONE.equalsIgnoreCase(command) ) {
            handleTimeZoneChange(container, player.getUserId(), st.nextToken(), true);
        } else {
            // check that it isn't a move (we will accept those) too
            try {
                Action.Type actionType = Action.Type.valueOf(command.toUpperCase());
                handleStartAction(container, inboundMessage, player, st, actionType);
            } catch( IllegalArgumentException ex ) {
                handleUnrecognisedCommand(container, inboundMessage, player, command, true);

            }
        }
    }

    private void handleStartAction(TemplateContainer container, InboundMessage inboundMessage, Player player, StringTokenizer st, Action.Type firstMove) throws Exception {
        int playerCount = 1;
        // players get saved later, but should probably do so now
        player.setInactive(false);
        ArrayList<Player> additionalPlayers = new ArrayList<Player>(st.countTokens());
        while( st.hasMoreTokens() ) {
            final String playerName = st.nextToken();
            Player additionalPlayer = playerDAO.findByName(playerName);
            if( additionalPlayer == null ) {
                // notify user of non-existent player
                container.sendMessage(player.getUserId(), new String[]{TEMPLATE_ID_NO_SUCH_PLAYER}, new HashMap<String, Object>(){{
                    put("name", playerName);
                }}, false);
            } else if( additionalPlayer.isInactive() ) {
                // notify user of inactive player
                container.sendMessage(player.getUserId(), new String[]{TEMPLATE_ID_PLAYER_INACTIVE}, new HashMap<String, Object>(){{
                    put("name", playerName);
                }}, false);
            } else if( additionalPlayer.getCurrentGameId() != null ) {
                // notify user of busy player
                container.sendMessage(player.getUserId(), new String[]{TEMPLATE_ID_PLAYER_BUSY}, new HashMap<String, Object>(){{
                    put("name", playerName);
                }}, false);
            } else {
                // TODO check timezone is suitable for play (don't start games with players who are going to bed or asleep)
                additionalPlayers.add(additionalPlayer);
            }
            playerCount++;
        }
        if( playerCount < 2 ) {
            playerCount = 2;
        }
        startGame(container, player, additionalPlayers, playerCount, firstMove);
    }

    private void handleStopAction(TemplateContainer container, Player player, final Action.Type reason) throws Exception {
        if( player.getUserId() != null ) {
            // TODO send a message
            container.sendMessage(
                    player.getUserId(),
                    new String[]{TEMPLATE_ID_LEFT_GAME},
                    new HashMap<String, Object>(){{
                        put("reason", reason);
                    }},
                    true
            );
        }
        player.setInactive(true);
        player.setCurrentGameId(null);
        playerDAO.update(player);
        // don't bother contacting them
    }

    private void handleTimeZoneChange(TemplateContainer container, long userId, String timeZoneId, boolean immediate) throws Exception {
        User user = container.getUser(userId);
        final TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
        user.setTimeZone(timeZone);
        container.sendMessage(
                userId,
                new String[]{TEMPLATE_ID_TIMEZONE_SET},
                new HashMap<String, Object>(){{
                    put("timezone", timeZone);
                }},
                immediate
        );
    }

    private void handleUnrecognisedCommand(TemplateContainer container, InboundMessage inboundMessage, Player player, final String command, boolean lobby) throws Exception {
        String templateId;
        if( lobby ) {
            templateId = TEMPLATE_ID_LOBBY_UNRECOGNISED_COMMAND;
        } else {
            templateId = TEMPLATE_ID_PLAY_UNRECOGNISED_COMMAND;
        }
        container.sendMessage(
                player.getUserId(),
                new String[]{templateId},
                new HashMap<String, Object>(){{
                    put("command", command);
                }},
                true
        );
    }

    private void handleTimeout(TemplateContainer container, ScheduledMessage scheduledMessage) throws Exception {
        // set all users that don't have actions associated with them to timeout
        MessageWrapper wrapper = new MessageWrapper(scheduledMessage);
        String gameIdString = wrapper.getTextPart(PROPERTY_GAME_ID);
        int gameId = Integer.parseInt(gameIdString);
        String turnNumberString = wrapper.getTextPart(PROPERTY_TURN_NUMBER);
        int turnNumber = Integer.parseInt(turnNumberString);
        List<Player> allPlayers = playerDAO.findByCurrentGameId(gameId);
        List<Action> actions = actionDAO.findForGameIdAndTurn(gameId, turnNumber);

        HashMap<Integer, Player> unmovedPlayers = new HashMap<Integer, Player>(allPlayers.size());
        for( Player allPlayer : allPlayers ) {
            unmovedPlayers.put(allPlayer.getId(), allPlayer);
        }
        for( Action action : actions ) {
            unmovedPlayers.remove(action.getPlayerId());
        }
        Date now = new Date();

        for( Integer unmovedPlayerId : unmovedPlayers.keySet() ) {
            Action action = new Action();
            action.setGameId(gameId);
            action.setPlayerId(unmovedPlayerId);
            action.setTime(now);
            action.setTurn(turnNumber);
            action.setType(Action.Type.TIMEOUT);
            actionDAO.create(action);
        }

        checkGameState(container, gameId);
        for( Player player : unmovedPlayers.values() ) {
            // stop
            handleStopAction(container, player, Action.Type.TIMEOUT);
        }

    }

    private void startGame(TemplateContainer container, final Player challengingPlayer, List<Player> specifiedPlayers, int minPlayers, Action.Type firstMove) throws Exception {
        Game game = new Game();

        Date now = new Date();
        game.setDateStarted(now);
        int gameId = gameDAO.create(game);

        final ArrayList<Player> players = new ArrayList<Player>();
        players.add(challengingPlayer);

        // set up the players for the game
        for( Player specifiedPlayer : specifiedPlayers ) {
            if( !specifiedPlayer.getId().equals(challengingPlayer.getId()) && !specifiedPlayer.isInactive() ) {
                players.add(specifiedPlayer);
            }
        }
        int numPlayers = players.size();
        if( numPlayers < minPlayers ) {
            List<Player> additionalPlayers = playerDAO.findAvailable(minPlayers - numPlayers + 1);
            for( Player additionalPlayer : additionalPlayers ) {
                if( !additionalPlayer.getId().equals(challengingPlayer.getId()) ) {
                    players.add(additionalPlayer);
                    if( players.size() >= minPlayers ) {
                        break;
                    }
                }
            }
        }

        while( players.size() < minPlayers ) {
            // create AI players
            Player player = new Player();
            // find a free name
            StringBuffer nameBuffer = new StringBuffer("_AI");
            boolean done = false;
            while( !done ) {
                int c = 'a' + random.nextInt(('z' - 'a')+1);
                nameBuffer.append((char)c);
                String name = nameBuffer.toString();
                Player existing = playerDAO.findByName(name);
                if( existing == null ) {
                    player.setName(name);
                    done = true;
                }
            }
            playerDAO.create(player);
            players.add(player);
        }

        // add everyone
        for( final Player player : players ) {
            player.setCurrentGameId(gameId);
            player.setGamesPlayed(player.getGamesPlayed() + 1);
            playerDAO.update(player);
            if( player.getUserId() != null && !player.isInactive() ) {
                // notify
                container.sendMessage(
                        player.getUserId(),
                        new String[]{TEMPLATE_ID_GAME_STARTED},
                        new HashMap<String, Object>(){{
                            put("recipient", player);
                            put("players", players);
                            put("challenger", challengingPlayer);
                        }},
                        false
                );
            }
        }


        startTurn(container, game, 1, challengingPlayer, firstMove);
    }

    private void notifyEnteredLobby(TemplateContainer container, final Player player) throws ContainerException {
        container.sendMessage(
                player.getUserId(),
                new String[]{TEMPLATE_ID_LOBBY_ENTERED},
                new HashMap<String, Object>(){{
                    this.put("player", player);
                }},
                true);
    }
}
