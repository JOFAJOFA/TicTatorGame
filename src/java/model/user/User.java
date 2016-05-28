package model.user;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import javax.websocket.Session;

/**
 *
 * @author Ferenc_S
 */
public class User {
    Session session;
    String username;

    public Session getSession() {
        return session;
    }

    public String getUsername() {
        return username;
    }

    public User(Session session, String username) {
        this.session = session;
        this.username = username;
    }

    public void sendText(String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }
    
}
