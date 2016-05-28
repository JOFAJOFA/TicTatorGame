/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package endpoint;

import java.io.IOException;
import java.util.Iterator;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import model.game.Game;
import model.game.GameException;
import model.game.GameManager;
import model.user.User;

/**
 *
 * @author Ferenc_S
 */
@ServerEndpoint("/gameServerEndpoint/{gameId}/{username}")
public class GameServerEndpoint {

    GameManager gm = GameManager.getInstance(); // Dependency injection would be nice.

    @OnOpen
    public void handleOpen(@PathParam("username") String username, @PathParam("gameId") String gameId, Session session) throws GameException, IOException {
        // By this point, game should have been created
        System.out.println("Session opened");
        gm.findGameById(gameId).setUser(new User(session, username));
        session.getBasicRemote().sendText("Starting soon :)");
    }

    @OnMessage
    public void handleMessage(String message, Session userSession) throws IOException, GameException {
        String[] params = message.split("&");
        String gameId = params[0];
        String username = params[1];
        int index = Integer.parseInt(params[2]);
        Game currentGame = gm.findGameById(gameId);
        currentGame.makeMove(index);
        if(currentGame.isFinished()){
            gm.removeGame(gameId);
        }
    }

    @OnClose
    public void handleClose(Session userSession) {
        /*
        TODO: handle if user quits before end of game
        */
    }
}
