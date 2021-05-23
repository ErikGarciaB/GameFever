package net.iessochoa.erikgarciabelen.gamefever.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class History {

    private String playerName1;
    private String playerName2;
    private String gameName;


    private Boolean player1Win;
    private Boolean player2Win;

    @ServerTimestamp
    private Date time;

    public History(){}

    public History(String playerName, String playerName2, String gameName,Boolean player1Win, Boolean player2Win) {
        this.playerName1 = playerName;
        this.playerName2 = playerName2;
        this.gameName = gameName;
        this.player1Win = player1Win;
        this.player2Win = player2Win;
    }

    public String getPlayerName1() {
        return playerName1;
    }

    public void setPlayerName1(String playerName) {
        this.playerName1 = playerName;
    }

    public String getPlayerName2() {
        return playerName2;
    }

    public void setPlayerName2(String playerName2) {
        this.playerName2 = playerName2;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Boolean getPlayer1Win() {
        return player1Win;
    }

    public void setPlayer1Win(Boolean player1Win) {
        this.player1Win = player1Win;
    }

    public Boolean getPlayer2Win() {
        return player2Win;
    }

    public void setPlayer2Win(Boolean player2Win) {
        this.player2Win = player2Win;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
