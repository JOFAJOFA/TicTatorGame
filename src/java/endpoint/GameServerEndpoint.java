/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package endpoint;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
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
import static model.game.WinnerType.*;

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
            session.getBasicRemote().sendText("{\"board\":[]}");
        } catch (GameException ex) {
            logger.log(Level.SEVERE, "Connection could not be opened due to game exception: ", ex);
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
            logger.log(Level.SEVERE, "Move could not be made due to game exception:", ex);
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

    private void sendGameResults(Game currentGame) {
     
    }
    private void sendPost() throws Exception {
		String url = "https://selfsolve.apple.com/wcResults.do";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", "lel");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";
		
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		//print result
		System.out.println(response.toString());

	}
}
