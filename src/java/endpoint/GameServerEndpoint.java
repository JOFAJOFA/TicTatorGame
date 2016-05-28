/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package endpoint;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import static model.game.FieldType.*;

/**
 *
 * @author Ferenc_S
 */
@ServerEndpoint("/gameServerEndpoint/{gameId}/{username}")
public class GameServerEndpoint {

    private static final Logger logger = Logger.getLogger(GameServerEndpoint.class.getName());

    GameManager gm = GameManager.getInstance(); // Dependency injection would be nice.

    @OnOpen
    public void handleOpen(@PathParam("username") String username, @PathParam("gameId") String gameId, Session session) {
        try {
            // By this point, game should have been created
            System.out.println("Session opened");
            gm.findGameById(gameId).setUser(new User(session, username));
            session.getBasicRemote().sendText("Starting soon :)");
        } catch (GameException ex) {
            logger.log(Level.SEVERE, "Connection could not be opened due to game exception: ", ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Connection could not be opened due to IO exception: ", ex);
        }
    }

    @OnMessage
    public void handleMessage(String message, Session userSession)  {
        try {
            String[] params = message.split("&");
            String gameId = params[0];
            String username = params[1];
            int index = Integer.parseInt(params[2]);
            Game currentGame = gm.findGameById(gameId);
            currentGame.makeMove(index);
            if (currentGame.getWinner() != EMPTY) {                
                logger.info(String.format("Game %s successfully finished", currentGame.toJson()));
            }
        } catch (GameException ex) {
            Logger.getLogger(GameServerEndpoint.class.getName()).log(Level.SEVERE, "Move could not be made due to game exception:", ex);
        } catch (IOException ex) {
            Logger.getLogger(GameServerEndpoint.class.getName()).log(Level.SEVERE, "Move could not be made due to IO", ex);
        }
    }

    @OnClose
    public void handleClose(Session userSession) {
        /*
        TODO: handle if user quits before end of game
         */
    }
}
