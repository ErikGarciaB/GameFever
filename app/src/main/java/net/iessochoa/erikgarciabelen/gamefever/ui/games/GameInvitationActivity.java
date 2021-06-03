package net.iessochoa.erikgarciabelen.gamefever.ui.games;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import net.iessochoa.erikgarciabelen.gamefever.R;
import net.iessochoa.erikgarciabelen.gamefever.adapter.GameInvitationAdapter;
import net.iessochoa.erikgarciabelen.gamefever.model.FirebaseContract;
import net.iessochoa.erikgarciabelen.gamefever.model.FriendRelation;
import net.iessochoa.erikgarciabelen.gamefever.model.Message;
import net.iessochoa.erikgarciabelen.gamefever.model.TicTacToe;
import net.iessochoa.erikgarciabelen.gamefever.model.User;
import net.iessochoa.erikgarciabelen.gamefever.ui.fragments.GamesFragment;

import java.util.ArrayList;

public class GameInvitationActivity extends AppCompatActivity {

    private RecyclerView rvGameInvitation;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button btGameInvitationReturn;
    private static String gameID;
    private final GameInvitationAdapter adapter = new GameInvitationAdapter();
    private ArrayList<FriendRelation> friends = new ArrayList<>();
    private ArrayList<TicTacToe> ttts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_invitation);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        /**
         * Detect if the mobile has internet. If the mobile doesn't have internet the application shut down.
         */
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.CONNECTED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.no_intenet_alert).setMessage(R.string.no_internet_message);
            builder.setOnDismissListener(dialog -> {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                finish();
            });
            builder.setPositiveButton(R.string.ok, (dialog, which) -> {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                finish();
            }).show();
        }

        initializeComponents();

        btGameInvitationReturn.setOnClickListener(v -> finish());
    }


    /**
     * Initialize the components and the adapter
     */
    private void initializeComponents() {
        rvGameInvitation = findViewById(R.id.rvGameInvitation);
        btGameInvitationReturn = findViewById(R.id.btGameInvitationReturn);
        rvGameInvitation.setLayoutManager(new LinearLayoutManager(this));
        rvGameInvitation.setAdapter(adapter);
        getFriends(auth.getCurrentUser().getDisplayName());
        gameID = getIntent().getExtras().getString(GamesFragment.EXTRA_GAMES_ID);


        adapter.setOnListenerGameListener(fr -> createGameWithFriend(fr));

    }


    /**
     * Create a game with a user's friend in the database.
     * @param fr Is the relation between the user and his friend
     */
    private void createGameWithFriend(FriendRelation fr) {
        if (gameID.equals(GamesFragment.TIC_TAC_TOE))
            createTicTacToeGame(fr);
    }


    /**
     * Create in the database the new TicTacToe game with the userFriend.
     *
     * @param fr Is the object in the database that contain the relationship
     */
    private void createTicTacToeGame(FriendRelation fr) {
        User user1, user2;
        String username1, username2, pk;
        if (fr.getUser1().getName().equals(auth.getCurrentUser().getDisplayName())) {
            user1 = fr.getUser1();
            user2 = fr.getUser2();
        } else {
            user1 = fr.getUser2();
            user2 = fr.getUser1();
        }

        username1 = user1.getName();
        username2 = user2.getName();

        pk = gameID + "-" + username1 + "-" + username2;

        TicTacToe ttt = new TicTacToe(user1, user2, pk);
        db.collection(FirebaseContract.TicTacToeGameEntry.COLLECTION_NAME).document(pk).set(ttt);
        db.collection(FirebaseContract.TicTacToeGameEntry.COLLECTION_NAME).document(pk)
                .collection(FirebaseContract.ChatEntry.COLLECTION_NAME).document().set(new Message())
                .addOnCompleteListener(task -> {
                    Intent i = new Intent(this, TicTacToeActivity.class);
                    i.putExtra(GamesFragment.EXTRA_GAMES_TTT, ttt);
                    startActivity(i);
                    finish();
                });


    }

    /**
     * Get the user's friends in the database. Then get the matches that user have.
     * @param userName
     */
    private void getFriends(String userName) {
        db.collection(FirebaseContract.FriendRelation.COLLECTION_NAME).get().addOnCompleteListener(task -> {
            for (QueryDocumentSnapshot document : task.getResult()) {
                FriendRelation fr = document.toObject(FriendRelation.class);
                String userName1 = fr.getUser1().getName();
                String userName2 = fr.getUser2().getName();

                if (userName1.equals(userName))
                    friends.add(fr);
                if (userName2.equals(userName))
                    friends.add(fr);

            }
            getTicTacToeGames(friends);
        });
    }

    /**
     * Get the unfinished tictactoegames of the users.
     *
     * @param friends The friends that has the user to find if they have a game unfinished
     */
    private void getTicTacToeGames(ArrayList<FriendRelation> friends) {
        ttts.clear();
        db.collection(FirebaseContract.TicTacToeGameEntry.COLLECTION_NAME)
                .get().addOnCompleteListener(task -> {
            for (DocumentSnapshot d : task.getResult()) {
                getTTTs(d.toObject(TicTacToe.class));
            }

            adapter.setFriendRelations(friends, ttts);
        });
    }

    /**
     * Get a Tic Tac Toe game and filter if the user is in the game.
     * @param ttt Is the game to filter
     */
    private void getTTTs(TicTacToe ttt) {
        String primaryKey = ttt.getId();
        String[] strings = primaryKey.split("-");

        String username1 = strings[1];
        String username2 = strings[2];

        if (auth.getCurrentUser().getDisplayName().equals(username1) || auth.getCurrentUser().getDisplayName().equals(username2))
            ttts.add(ttt);

    }
}