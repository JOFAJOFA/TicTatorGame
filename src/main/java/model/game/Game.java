/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.game;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import static model.game.FieldType.*;
import static model.game.WinnerType.*;
import model.user.User;
import property.Constants;
import property.PropertyHandler;

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
    WinnerType winner = ONGOING;
    int movesMade = 0;
    boolean user_1turn = true;
    final long createdAt;
    long expiresAt = 0;

    public void setUser(User user) throws GameException {
        if (username_1.equals(user.getUsername())) {
            user1 = user;
        } else if (username_2.equals(user.getUsername())) {
            user2 = user;
        } else {
            throw new GameException(String.format("This user (%s) has nothing to do with this game(ID: %s, Players: %s, %s)!", user.getUsername(), id, username_1, username_2));
        }
    }

    public String getUsername_1() {
        return username_1;
    }

    public String getUsername_2() {
        return username_2;
    }

    public String getId() {
        return id;
    }
    
    public String getCreatedAt(){
        return new Date(createdAt).toString();
    }

    public WinnerType getWinner() {
        return winner;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }

    public Game(String id, String username1, String username2) {
        this.id = id;
        this.username_1 = username1;
        this.username_2 = username2;
        Arrays.fill(board, FieldType.EMPTY);
        createdAt = System.currentTimeMillis();
        expiresAt = System.currentTimeMillis() + Constants.GAME_EXPIRES;
    }

    public void makeMove(String username, int index) throws GameException, IOException {
        if (user1 == null || user2 == null) {
            throw new GameException("Users are not set yet!");
        }

        insertIntoBoard(username, index);
        expiresAt = System.currentTimeMillis() + Constants.GAME_EXPIRES; // if a game is untouched for 2 minutes, it will be removed when a new game is created
        movesMade++;
        sendNewBoard();
        winner = winner();
        if (winner != ONGOING) {
            sendWinner(winner);
        }
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("gameId", id)
                .add("player1", username_1)
                .add("player2", username_2)
                .add("result", winner.getDescciption()).build();
    }

    private void insertIntoBoard(String username, int index) throws GameException {
        if (board[index] == EMPTY && thisUsersTurn(username)) {
            board[index] = user_1turn ? X : O;
            user_1turn = !user_1turn;
        } else {
            throw new GameException(String.format("This field at index %s is already marked!", index));
        }
    }

    private WinnerType winner() {
        // all rows
        for (int offset = 0; offset < 8; offset += 3) {
            int x_sum = 0; // why int? the JVM models stacks using offsets that are multiples of 32 bits
            int o_sum = 0;
            ROW:
            for (int i = 0; i < 3; i++) {
                switch (board[i + offset]) {
                    case EMPTY:
                        break ROW;
                    case X:
                        x_sum++;
                        break;
                    case O:
                        o_sum++;
                        break;
                    default:
                        break;
                }
            }
            if (x_sum == 3) {
                return PLAYER_1;
            }
            if (o_sum == 3) {
                return PLAYER_2;
            }
        }
        // all cols
        for (int offset = 0; offset < 3; offset++) {
            int x_sum = 0;
            int o_sum = 0;
            COL:
            for (int i = 0; i < 8; i += 3) {
                switch (board[i + offset]) {
                    case EMPTY:
                        break COL;
                    case X:
                        x_sum++;
                        break;
                    case O:
                        o_sum++;
                        break;
                    default:
                        break;
                }
            }
            if (x_sum == 3) {
                return PLAYER_1;
            }
            if (o_sum == 3) {
                return PLAYER_2;
            }
        }

        // diag 1
        int x_sum = 0;
        int o_sum = 0;
        for (int i = 0; i < 3; i++) {
            switch (board[i + i * 3]) {
                case EMPTY:
                    break;
                case X:
                    x_sum++;
                    break;
                case O:
                    o_sum++;
                    break;
                default:
                    break;
            }
        }
        if (x_sum == 3) {
            return PLAYER_1;
        }
        if (o_sum == 3) {
            return PLAYER_2;
        }

        // diag 2
        x_sum = 0;
        o_sum = 0;
        for (int i = 0; i < 3; i++) {
            switch (board[i * 2 + 2]) {
                case EMPTY:
                    break;
                case X:
                    x_sum++;
                    break;
                case O:
                    o_sum++;
                    break;
                default:
                    break;
            }
        }
        if (x_sum == 3) {
            return PLAYER_1;
        }
        if (o_sum == 3) {
            return PLAYER_2;
        }
        return movesMade == 9 ? DRAW : ONGOING;
    }

    private String boardToJson() {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (FieldType t : board) {
            builder.add(t.name());
        }
        return Json.createObjectBuilder().add("board", builder.build()).build().toString();
    }

    private void sendNewBoard() throws IOException {
        String board = boardToJson();
        user1.sendText(board);
        user2.sendText(board);
    }

    private void sendWinner(WinnerType type) throws IOException {
        switch (type) {
            case PLAYER_1:
                user1.sendText("You are winner!");
                user2.sendText("You are lose!");
                break;
            case PLAYER_2:
                user1.sendText("You are lose!");
                user2.sendText("You are winner!");
                break;
            default:
                user1.sendText("Draw.");
                user2.sendText("Draw.");
        }
    }

    private boolean thisUsersTurn(String username) {
        if (user_1turn && username.equals(username_1)) {
            return true;
        }
        if (!user_1turn && username.equals(username_2)) {
            return true;
        }
        return false;
    }
}
