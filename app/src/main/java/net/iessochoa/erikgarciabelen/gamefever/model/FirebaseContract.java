package net.iessochoa.erikgarciabelen.gamefever.model;

public class FirebaseContract {
    public static class TicTacToeGameEntry{
        public static final String COLLECTION_NAME = "TicTacToeGame";
        public static final String MAP = "Map";
        public static final String PLAYER1 = "Player1";
        public static final String PLAYER1TURN = "Player1Turn";
        public static final String PLAYER1WIN = "Player1Win";
        public static final String PLAYER2 = "Player2";
        public static final String PLAYER2TURN = "Player2Turn";
        public static final String PLAYER2WIN = "Player1Win";
    }

    public static class ChatEntry{
        public static final String COLLECTION_NAME = "Chat";
        public static final String BODY = "body";
        public static final String DATE = "date";
        public static final String USER = "user";
    }

    public static class UserEntry{
        public static final String COLLECTION_NAME = "UserInfo";
        public static final String NAME = "Name";
        public static final String LAST_TIME_CONNECTED = "LastTimeConnected";
    }

    public static class InvitationEntry{
        public static final String COLLECTION_NAME = "Invitations";
        public static final String HAS_ACCEPTED = "HasAccepted";
        public static final String HAS_DECLINED = "HasDeclined";
        public static final String HOST_USER = "HostUser";
        public static final String INVITED_USER = "InvitedUser";
    }

    public static class FriendRelation{
        public static final String COLLECTION_NAME = "FriendRelation";
        public static final String USER_1 = "User1";
        public static final String USER_2 = "User2";
    }
}
