/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import endpoint.GameServerEndpoint;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import model.game.Game;
import model.game.GameException;
import model.game.GameManager;

/**
 *
 * @author Ferenc_S
 */
@WebServlet("/addGame")
public class AddGameServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AddGameServlet.class.getName());
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String gameId = request.getParameter("username");
        String username_1 = request.getParameter("username_1");
        String username_2 = request.getParameter("username_2");
        try {
            GameManager.getInstance().addGame(new Game(gameId, username_1, username_2));
        } catch (GameException ex) {
            logger.log(Level.SEVERE, "Game creation failed due to exception: ", ex);
        }
    }

}
