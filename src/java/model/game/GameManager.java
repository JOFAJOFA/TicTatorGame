/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.game;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Ferenc_S
 */
public class GameManager {

    private static GameManager instance = null;
    Map<String, Game> games = new HashMap();

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public Game findGameById(String id) throws GameException {
        Game game = games.get(id);
        if (game != null) {
            return game;
        }
        throw new GameException(String.format("The game (ID: %s) doesn't exists!", id));
    }

    public void addGame(Game game) throws GameException {
        if (games.containsKey(game.getId())) {
            throw new GameException(String.format("The game (ID: %s) is already in progress!", game.getId()));
        }
        games.put(game.getId(), game);
    }

    public void removeGame(String gameId) throws GameException {
        if (!games.containsKey(gameId)) {
            throw new GameException(String.format("The game (ID: %s) is NOT in progress!", gameId));
        }
        games.remove(gameId);
    }

}
