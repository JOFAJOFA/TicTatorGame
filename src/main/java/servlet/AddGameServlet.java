/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import model.game.Game;
import model.game.GameException;
import model.game.GameManagerImpl;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import static property.Constants.MM_SERVER_IP;
import static property.Constants.SET_IP;
import property.PropertyHandler;

/**
 *
 * @author Ferenc_S
 */
@WebServlet("/addGame")
public class AddGameServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AddGameServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            JsonReader jsonReader = Json.createReader(request.getInputStream());
            JsonObject gameJson = jsonReader.readObject();
            jsonReader.close();
            String gameId = gameJson.getString("gameId");
            String player1 = gameJson.getString("player1");
            String player2 = gameJson.getString("player2");
            Game game = new Game(gameId, player1, player2);
            GameManagerImpl.getInstance().addGame(game);
            setIpToMatchMaking(gameId);
            logger.info(String.format("Game %s successfully created.", game.toJson()));
        } catch (GameException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            logger.log(Level.SEVERE, "Game creation failed due to game exception: ", ex);
        } catch (IOException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logger.log(Level.SEVERE, "Game creation failed due to IO exception:", ex);
        }
    }

    /*
    Notifies the MM service about what server ended up taking care of the game so that
    MM can tell the user which IP to connect to
     */
    private void setIpToMatchMaking(String gameId) {
        try {
            // Make it a post request
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            String remoteAddr = PropertyHandler.getInstance().getValue(MM_SERVER_IP);
            HttpPost request = new HttpPost(remoteAddr + SET_IP + gameId);
            logger.info(String.format("Ip is sent to %s for gameId %s.", remoteAddr, gameId));
            httpClient.execute(request);
            httpClient.close();
        } catch (IOException ex) {
            Logger.getLogger(AddGameServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
