/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.game;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.websocket.Session;
import static model.game.FieldType.*;
import model.user.User;

/**
 *
 * @author Ferenc_S
 */
public class Game {

    String username1;
    String username2;
    // The moment of game creation user Sessions don't yet exist
    User user1;
    User user2;
    String id;

    FieldType[] board = new FieldType[9];
    boolean xTurn = true;

    public String getId() {
        return id;
    }

    public Game(String id, String username1, String username2) {
        this.id = id;
        this.username1 = username1;
        this.username2 = username2;
        Arrays.fill(board, FieldType.EMPTY);
    }

    private void insertIntoBoard(int index) throws GameException {
        if (board[index] == EMPTY) {
            board[index] = xTurn ? X : O;
            xTurn = !xTurn;
        } else {
            throw new GameException("This field is already marked!");
        }

    }

    public void makeMove(int index) throws GameException, IOException {
        if (user1 == null || user2 == null) {
            throw new GameException("Users are not set yet!");
        }
        insertIntoBoard(index);
        if (true)// winner
        {
            sendNewBoard();
        }
    }

    public void sendNewBoard() throws IOException {
        String board = boardToJson();
        user1.sendText(board);
        user2.sendText(board);
    }

    private String boardToJson() {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (FieldType t : board) {
            builder.add(t.name());
        }
        return builder.build().toString();
    }

    public void setUser(User user) throws GameException {
        if (username1.equals(user.getUsername())) {
            user1 = user;
        }
        else if (username2.equals(user.getUsername())) {
            user2 = user;
        } else {
            throw new GameException(String.format("This user (%s) has nothing to do with this game(ID: %s, Players: %s, %s)!", user.getUsername(), id, username1, username2));
        }
    }

    public boolean isFinished() {
        return false;
    }
}
