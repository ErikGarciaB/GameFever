package net.iessochoa.erikgarciabelen.gamefever.model;

public class FirebaseContract {
    
    public static class TicTacToeGameEntry{
        public static final String COLLECTION_NAME = "TicTacToeGame";
        public static final String MAP = "map";
        public static final String MOVE_COUNTER = "moveCounter";
        public static final String PLAYER1 = "player1";
        public static final String PLAYER1TURN = "player1Turn";
        public static final String PLAYER1WIN = "player1Win";
        public static final String PLAYER2 = "player2";
        public static final String PLAYER2TURN = "player2Turn";
        public static final String PLAYER2WIN = "player1Win";
        public static final String DRAWGAME = "drawGame";
        public static final String ID = "id";
    }

    public static class HistoryEntry{
        public static final String COLLECTION_NAME = "History";
        public static final String PLAYER_NAME_1 = "playerName1";
        public static final String PLAYER_NAME_2 = "playerName2";
        public static final String GAME_NAME = "gameName";
        public static final String PLAYER_1_WIN = "player1Win";
        public static final String PLAYER_2_WIN = "player2Win";
        public static final String DATE = "time";
    }

    public static class ChatEntry{
        public static final String COLLECTION_NAME = "Chat";
        public static final String BODY = "body";
        public static final String DATE = "time";
        public static final String USER = "user";
    }

    public static class UserEntry{
        public static final String COLLECTION_NAME = "UserInfo";
        public static final String NAME = "Name";
        public static final String LAST_TIME_CONNECTED = "LastTimeConnected";
    }

    public static class InvitationEntry{
        public static final String COLLECTION_NAME = "Invitations";
        public static final String HOST_USER = "HostUser";
        public static final String INVITED_USER = "InvitedUser";
    }

    public static class FriendRelation{
        public static final String COLLECTION_NAME = "FriendRelation";
        public static final String USER_1 = "User1";
        public static final String USER_2 = "User2";
        public static final String ID = "id";
    }
    
}
