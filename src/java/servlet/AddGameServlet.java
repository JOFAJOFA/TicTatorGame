/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import endpoint.GameServerEndpoint;
import java.io.IOException;
import java.io.StringReader;
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
            logger.info(String.format("Game %s successfully created.", game.toJson()));
        } catch (GameException ex) {
            logger.log(Level.SEVERE, "Game creation failed due to game exception: ", ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Game creation failed due to IO exception:", ex);
        }
    }
}
