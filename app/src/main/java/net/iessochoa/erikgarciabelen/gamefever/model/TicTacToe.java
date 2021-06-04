package net.iessochoa.erikgarciabelen.gamefever.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class TicTacToe implements Parcelable {

    private ArrayList<Integer> map;

    private Integer moveCounter;

    private User player1;
    private User player2;

    private String id;

    private Boolean player1Turn;
    private Boolean player1Win;
    private Boolean player2Turn;
    private Boolean player2Win;
    private Boolean drawGame;

    public TicTacToe(){}

    public TicTacToe(User player1, User player2, String id){
        map = createMap();
        this.player1 = player1;
        this.player2 = player2;
        this.id = id;
        moveCounter = 0;
        player1Turn = true;
        player1Win = false;
        player2Turn = false;
        player2Win = false;
        drawGame = false;
    }

    private ArrayList<Integer> createMap (){
        ArrayList<Integer> out = new ArrayList<>();
        for(int i = 0; i < 9; i++)
            out.add(0);
        return out;
    }

    protected TicTacToe(Parcel in) {
        if (in.readByte() == 0x01) {
            map = new ArrayList<Integer>();
            in.readList(map, Integer.class.getClassLoader());
        } else {
            map = null;
        }
        player1 = (User) in.readValue(User.class.getClassLoader());
        player2 = (User) in.readValue(User.class.getClassLoader());
        id = in.readString();
        byte player1TurnVal = in.readByte();
        player1Turn = player1TurnVal == 0x02 ? null : player1TurnVal != 0x00;
        byte player1WinVal = in.readByte();
        player1Win = player1WinVal == 0x02 ? null : player1WinVal != 0x00;
        byte player2TurnVal = in.readByte();
        player2Turn = player2TurnVal == 0x02 ? null : player2TurnVal != 0x00;
        byte player2WinVal = in.readByte();
        player2Win = player2WinVal == 0x02 ? null : player2WinVal != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (map == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(map);
        }
        dest.writeValue(player1);
        dest.writeValue(player2);
        dest.writeString(id);
        if (player1Turn == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (player1Turn ? 0x01 : 0x00));
        }
        if (player1Win == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (player1Win ? 0x01 : 0x00));
        }
        if (player2Turn == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (player2Turn ? 0x01 : 0x00));
        }
        if (player2Win == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (player2Win ? 0x01 : 0x00));
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TicTacToe> CREATOR = new Parcelable.Creator<TicTacToe>() {
        @Override
        public TicTacToe createFromParcel(Parcel in) {
            return new TicTacToe(in);
        }

        @Override
        public TicTacToe[] newArray(int size) {
            return new TicTacToe[size];
        }
    };

    public ArrayList<Integer> getMap() {
        return map;
    }

    public void setMap(ArrayList<Integer> map) {
        this.map = map;
    }

    public Integer getMoveCounter() {
        return moveCounter;
    }

    public void setMoveCounter(Integer moveCounter) {
        this.moveCounter = moveCounter;
    }

    public User getPlayer1() {
        return player1;
    }

    public void setPlayer1(User player1) {
        this.player1 = player1;
    }

    public User getPlayer2() {
        return player2;
    }

    public void setPlayer2(User player2) {
        this.player2 = player2;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getPlayer1Turn() {
        return player1Turn;
    }

    public void setPlayer1Turn(Boolean player1Turn) {
        this.player1Turn = player1Turn;
    }

    public Boolean getPlayer1Win() {
        return player1Win;
    }

    public void setPlayer1Win(Boolean player1Win) {
        this.player1Win = player1Win;
    }

    public Boolean getPlayer2Turn() {
        return player2Turn;
    }

    public void setPlayer2Turn(Boolean player2Turn) {
        this.player2Turn = player2Turn;
    }

    public Boolean getPlayer2Win() {
        return player2Win;
    }

    public void setPlayer2Win(Boolean player2Win) {
        this.player2Win = player2Win;
    }

    public Boolean getDrawGame() {
        return drawGame;
    }

    public void setDrawGame(Boolean drawGame) {
        this.drawGame = drawGame;
    }
}
