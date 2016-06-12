/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import model.game.Game;
import model.game.GameManager;
import model.game.GameManagerImpl;

/**
 *
 * @author Ferenc_S
 */
@WebServlet("/showGames")
public class ShowGamesServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ShowGamesServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("GG");
        RequestDispatcher requestDispatcher;
        request.setAttribute("games", GameManagerImpl.getInstance().getAllGames());
        requestDispatcher = request.getRequestDispatcher("/WEB-INF/showGames.jsp");

        requestDispatcher.forward(request, response);
    }
}
