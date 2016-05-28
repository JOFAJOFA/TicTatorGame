/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.game;

import java.util.ArrayList;
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
            instance.addGame(new Game("game1", "Game1User1", "Game1User2")); // uuh... totally generated from some post request...
            instance.addGame(new Game("game2", "Game2User1", "Game2User2"));
            instance.addGame(new Game("game3", "Game3User1", "Game3User2"));
            instance.addGame(new Game("game4", "Game4User1", "Game4User2"));
        }
        return instance;
    }

    public Game findGameById(String id) throws GameException {
        Game game = games.get(id);
        if (game != null) {
            return game;
        }
        throw new GameException("The game doesn't exists!");
    }

    public void addGame(Game game) {
        games.put(game.getId(), game);
    }

    public void removeGame(String gameId) {
        games.remove(gameId);
    }

}
