/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.game;

/**
 *
 * @author Ferenc_S
 */
public enum WinnerType {
    ONGOING("ongoing"), DRAW("draw"), PLAYER_1("playerX"), PLAYER_2("playerO");

    private final String fieldDescription;

    private WinnerType(String value) {
        fieldDescription = value;
    }

    public String getDescciption() {
        return fieldDescription;
    }
}
