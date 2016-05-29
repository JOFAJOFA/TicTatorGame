/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.game;

/**
 *
 * @author Ferenc_S
 */
public interface GameManager {

    void addGame(Game game) throws GameException;

    Game findGameById(String id) throws GameException;

    void removeGame(String gameId) throws GameException;
    
}
