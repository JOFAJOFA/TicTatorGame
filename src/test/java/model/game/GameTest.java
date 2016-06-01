/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.game;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import java.io.IOException;
import model.user.User;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Ferenc_S
 */
public class GameTest extends AbstractBenchmark {

    Game game;
    User user1;
    User user2;
    String username1;
    String username2;
    String gameId;

    public GameTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws GameException {
        gameId = "gameId";
        username1 = "Alice";
        username2 = "Bob";
        user1 = new User(new MockSession(), username1);
        user2 = new User(new MockSession(), username2);
        game = new Game(gameId, username1, username2);
        game.setUser(user1);
        game.setUser(user2);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of setUser method with unrelated user, of class Game.
     */
    @Test(expected = GameException.class)
    public void testSetUserFake() throws Exception {
        System.out.println("setUser");
        User user = new User(new MockSession(), "DefinitelyNotAlice");
        Game instance = new Game(gameId, username1, username2);
        instance.setUser(user);
    }

    /**
     * Test of getWinner method, of class Game.
     */
    @org.junit.Test
    public void testGetWinnerOnGoing() throws GameException {
        System.out.println("getWinner");
        Game instance = game;
        WinnerType expResult = WinnerType.ONGOING;
        WinnerType result = instance.getWinner();
        assertEquals(expResult, result);
    }

    /**
     * Test of getWinner method, of class Game.
     */
    @org.junit.Test
    public void testGetWinnerPlayer1() throws GameException, IOException {
        System.out.println("getWinner");
        Game instance = game;
        game.makeMove(username1, 0);
        game.makeMove(username2, 1);
        game.makeMove(username1, 3);
        game.makeMove(username2, 2);
        game.makeMove(username1, 6);
        WinnerType expResult = WinnerType.PLAYER_1;
        WinnerType result = instance.getWinner();
        assertEquals(expResult, result);
    }

    /**
     * Test of makeMove method, of class Game.
     */
    @org.junit.Test
    public void testMakeMove() throws Exception {
        System.out.println("makeMove");
        String username = "Alice";
        int index = 0;
        Game instance = game;
        instance.makeMove(username, index);
    }

    /**
     * Test of makeMove method, where an unknown user tries to move twice of
     * class Game.
     */
    @Test(expected = GameException.class)
    public void testMakeMoveUnknownUser() throws Exception {
        System.out.println("makeMove");
        String username = "NotAlice";
        int index = 0;
        Game instance = game;
        instance.makeMove(username, index);
    }

    /**
     * Test of makeMove method, where the same user tries to move twice 
     */
    @Test(expected = GameException.class)
    public void testMakeMoveCheat() throws Exception {
        System.out.println("makeMove");
        Game instance = game;
        instance.makeMove(username1, 0);
        instance.makeMove(username2, 1);
        instance.makeMove(username2, 2);
    }

    @Test
    public void playFullGameFromScratch1() throws GameException, IOException {
        String username_1 = "Alpha";
        String username_2 = "Beta";
        String gameId = "gameId";
        User user_1 = new User(new MockSession(), username_1);
        User user_2 = new User(new MockSession(), username_2);
        Game instance = new Game(gameId, username_1, username_2);
        instance.setUser(user_1);
        instance.setUser(user_2);

        instance.makeMove(username_1, 0);
        instance.makeMove(username_2, 1);
        instance.makeMove(username_1, 2);
        instance.makeMove(username_2, 3);
        instance.makeMove(username_1, 4);
        instance.makeMove(username_2, 5);
        instance.makeMove(username_1, 6);

        assertEquals(WinnerType.PLAYER_1, instance.getWinner());
    }

    @Test
    public void playFullGameFromScratch2() throws GameException, IOException {
        String username_1 = "Alpha";
        String username_2 = "Beta";
        String gameId = "gameId";
        User user_1 = new User(new MockSession(), username_1);
        User user_2 = new User(new MockSession(), username_2);
        Game instance = new Game(gameId, username_1, username_2);
        instance.setUser(user_1);
        instance.setUser(user_2);

        instance.makeMove(username_1, 0);
        instance.makeMove(username_2, 2);
        instance.makeMove(username_1, 1);
        instance.makeMove(username_2, 3);
        instance.makeMove(username_1, 5);
        instance.makeMove(username_2, 4);
        instance.makeMove(username_1, 6);
        instance.makeMove(username_2, 7);
        instance.makeMove(username_1, 8);

        assertEquals(WinnerType.DRAW, instance.getWinner());
    }

    @BenchmarkOptions(benchmarkRounds = 1000, warmupRounds = 20)
    @Test
    public void playFullGameFromScratchBenchmark1() throws GameException, IOException {
        String username_1 = "Alpha";
        String username_2 = "Beta";
        String gameId = "gameId";
        User user_1 = new User(new MockSession(), username_1);
        User user_2 = new User(new MockSession(), username_2);
        Game instance = new Game(gameId, username_1, username_2);
        instance.setUser(user_1);
        instance.setUser(user_2);
        instance.makeMove(username_1, 0);
        instance.makeMove(username_2, 2);
        instance.makeMove(username_1, 1);
        instance.makeMove(username_2, 3);
        instance.makeMove(username_1, 5);
        instance.makeMove(username_2, 4);
        instance.makeMove(username_1, 6);
        instance.makeMove(username_2, 7);
        instance.makeMove(username_1, 8);
        assertEquals(WinnerType.DRAW, instance.getWinner());
    }

    @BenchmarkOptions(benchmarkRounds = 1000, warmupRounds = 20)
    @Test
    public void playFullGameFromScratchBenchmark2() throws GameException, IOException {
        String username_1 = "Alpha";
        String username_2 = "Beta";
        String gameId = "gameId";
        User user_1 = new User(new MockSession(), username_1);
        User user_2 = new User(new MockSession(), username_2);
        Game instance = new Game(gameId, username_1, username_2);
        instance.setUser(user_1);
        instance.setUser(user_2);

        instance.makeMove(username_1, 0);
        instance.makeMove(username_2, 1);
        instance.makeMove(username_1, 2);
        instance.makeMove(username_2, 3);
        instance.makeMove(username_1, 4);
        instance.makeMove(username_2, 5);
        instance.makeMove(username_1, 6);

        assertEquals(WinnerType.PLAYER_1, instance.getWinner());
    }
}
