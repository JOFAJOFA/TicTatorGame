/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package endpoint;

import java.io.IOException;
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
import model.game.GameManagerImpl;
import model.user.User;
import static model.game.WinnerType.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import static property.Constants.GAME_CALLBACK;
import static property.Constants.MM_SERVER_IP;
import property.PropertyHandler;

/**
 *
 * @author Ferenc_S
 */
@ServerEndpoint("/gameServerEndpoint/{gameId}/{username}")
public class GameServerEndpoint {

    private static final Logger logger = Logger.getLogger(GameServerEndpoint.class.getName());

    GameManager gm = GameManagerImpl.getInstance(); // Dependency injection would be nice.

    @OnOpen
    public void handleOpen(@PathParam("username") String username, @PathParam("gameId") String gameId, Session session) {
        try {
            // By this point, game should have been created
            gm.findGameById(gameId).setUser(new User(session, username));
            session.getBasicRemote().sendText("{\"board\":[]}");
        } catch (GameException ex) {
            logger.log(Level.INFO, "Connection could not be opened due to game exception: {0}", ex.getMessage());
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Connection could not be opened due to IO exception: ", ex);
        }
    }

    @OnMessage
    public void handleMessage(String message, Session userSession) {
        try {
            String[] params = message.split("&");
            String gameId = params[0];
            String username = params[1];
            int index = Integer.parseInt(params[2]);
            Game currentGame = gm.findGameById(gameId);
            currentGame.makeMove(username, index);
            if (currentGame.getWinner() != ONGOING) {
                gm.removeGame(gameId);
                logger.info(String.format("Game %s successfully finished", currentGame.toJson()));
                sendGameResults(currentGame);
            }
        } catch (GameException ex) {
            logger.log(Level.INFO, "Move could not be made due to game exception: {0}", ex.getMessage());
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Move could not be made due to IO", ex);
        }
    }

    @OnClose
    public void handleClose(Session userSession) {
        /*
        TODO: handle if user quits before end of game
         */
    }

    private void sendGameResults(Game currentGame) throws IOException {
        sendPost(currentGame.toJson().toString());
    }

    private void sendPost(String json) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            String remoteAddr = PropertyHandler.getInstance().getValue(MM_SERVER_IP) + GAME_CALLBACK;
            HttpPost request = new HttpPost(remoteAddr);
            StringEntity params = new StringEntity(json.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            CloseableHttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                logger.warning(String.format("The request was not handled correctly by remote server at %s. Error: %s", remoteAddr, response.getStatusLine().getStatusCode()));
            }
        } finally {
            httpClient.close();
        }
    }
}
