/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.game;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import property.Constants;
import property.PropertyHandler;
import servlet.AddGameServlet;

/**
 *
 * @author Ferenc_S
 */
public class GameManagerImpl implements GameManager {

    private static final Logger logger = Logger.getLogger(GameManagerImpl.class.getName());
    private long lastUpdate = 0;
    private static GameManagerImpl instance = null;
    Map<String, Game> games = new HashMap();

    public static GameManagerImpl getInstance() {
        if (instance == null) {
            instance = new GameManagerImpl();
            instance.lastUpdate = System.currentTimeMillis();
        }
        return instance;
    }

    @Override
    public Game findGameById(String id) throws GameException {
        Game game = games.get(id);
        if (game != null) {
            return game;
        }
        throw new GameException(String.format("The game (ID: %s) doesn't exists!", id));
    }

    @Override
    public Collection<Game> getAllGames() {
        return games.values();
    }

    @Override
    public void addGame(Game game) throws GameException {
        if (games.containsKey(game.getId())) {
            throw new GameException(String.format("The game (ID: %s) is already in progress!", game.getId()));
        }
        games.put(game.getId(), game);
        if (System.currentTimeMillis() - lastUpdate > Constants.GAME_EXPIRES) {
            oldGameCollector();
        }
    }

    @Override
    public void removeGame(String gameId) throws GameException {
        if (!games.containsKey(gameId)) {
            throw new GameException(String.format("The game (ID: %s) is NOT in progress!", gameId));
        }
        games.remove(gameId);
    }

    private void oldGameCollector() {
        Set<String> gameIds = games.keySet();
        logger.info(String.format("Old game collection started. Number of games before: %s", gameIds.size()));

        for (Iterator<Map.Entry<String, Game>> it = games.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Game> entry = it.next();
            if (entry.getValue().isExpired()) {
                it.remove();
            }

            logger.info(String.format("Old game collection finished. Number of games after: %s", games.size()));
        }

    }
}
