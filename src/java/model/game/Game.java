/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.game;

import java.io.IOException;
import java.util.Arrays;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import static model.game.FieldType.*;
import model.user.User;

/**
 *
 * @author Ferenc_S
 */
public class Game {

    String username_1;
    String username_2;
    // The moment of game creation user Sessions don't yet exist
    User user1;
    User user2;
    String id;

    FieldType[] board = new FieldType[9];
    FieldType winner = EMPTY; // coincidentally, FieldType is perfect for storing winnerType
    boolean xTurn = true;

    public void setUser(User user) throws GameException {
        if (username_1.equals(user.getUsername())) {
            user1 = user;
        } else if (username_2.equals(user.getUsername())) {
            user2 = user;
        } else {
            throw new GameException(String.format("This user (%s) has nothing to do with this game(ID: %s, Players: %s, %s)!", user.getUsername(), id, username_1, username_2));
        }
    }

    public String getId() {
        return id;
    }

    public FieldType getWinner() {
        return winner;
    }

    public Game(String id, String username1, String username2) {
        this.id = id;
        this.username_1 = username1;
        this.username_2 = username2;
        Arrays.fill(board, FieldType.EMPTY);
    }

    public void makeMove(int index) throws GameException, IOException {
        if (user1 == null || user2 == null) {
            throw new GameException("Users are not set yet!");
        }
        insertIntoBoard(index);
        sendNewBoard();
        winner = winner();
        if (winner != EMPTY) {
            sendWinner(winner);
        }
    }

    public JsonObject toJson() {
        String user_win = winner == X ? username_1 : username_2;
        String user_los = winner == O ? username_2 : username_1;
        boolean draw = winner == EMPTY;
        return Json.createObjectBuilder()
                .add("gameId", id)
                .add("winner", user_win)
                .add("loser", user_los)
                .add("isDraw", draw).build();
    }

    private void insertIntoBoard(int index) throws GameException {
        if (board[index] == EMPTY) {
            board[index] = xTurn ? X : O;
            xTurn = !xTurn;
        } else {
            throw new GameException(String.format("This field at index %s is already marked!", index));
        }
    }

    private FieldType winner() {

        // all rows
        for (int offset = 0; offset < 8; offset += 3) {
            int x_sum = 0; // why int? the JVM models stacks using offsets that are multiples of 32 bits
            int o_sum = 0;
            for (int i = 0; i < 3; i++) {
                if (board[i + offset] == EMPTY) {
                    break;
                } else if (board[i + offset] == X) {
                    x_sum++;
                } else if (board[i + offset] == O) {
                    o_sum++;
                }
            }
            if (x_sum == 3) {
                return X;
            }
            if (o_sum == 3) {
                return O;
            }
        }
        // all cols
        for (int offset = 0; offset < 3; offset++) {
            int x_sum = 0; // 0 3 6    1 4 7   2 5 8
            int o_sum = 0;
            for (int i = 0; i < 8; i = +3) {
                if (board[i + offset] == EMPTY) {
                    break;
                } else if (board[i + offset] == X) {
                    x_sum++;
                } else if (board[i + offset] == O) {
                    o_sum++;
                }
            }
            if (x_sum == 3) {
                return X;
            }
            if (o_sum == 3) {
                return O;
            }
        }
        return EMPTY;
    }

    private String boardToJson() {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (FieldType t : board) {
            builder.add(t.name());
        }
        return builder.build().toString();
    }

    private void sendNewBoard() throws IOException {
        String board = boardToJson();
        user1.sendText(board);
        user2.sendText(board);
    }

    private void sendWinner(FieldType type) throws IOException {
        if (type == X) {
            user1.sendText("You are winner!");
            user2.sendText("You are lose!");
        }
        user1.sendText("You are lose!");
        user2.sendText("You are winner!");
    }
}
