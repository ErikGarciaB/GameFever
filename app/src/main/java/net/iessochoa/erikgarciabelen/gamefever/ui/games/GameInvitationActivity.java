package net.iessochoa.erikgarciabelen.gamefever.ui.games;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import net.iessochoa.erikgarciabelen.gamefever.R;
import net.iessochoa.erikgarciabelen.gamefever.adapter.GameInvitationAdapter;
import net.iessochoa.erikgarciabelen.gamefever.model.FirebaseContract;
import net.iessochoa.erikgarciabelen.gamefever.model.FriendRelation;
import net.iessochoa.erikgarciabelen.gamefever.model.Message;
import net.iessochoa.erikgarciabelen.gamefever.model.TicTacToe;
import net.iessochoa.erikgarciabelen.gamefever.model.User;

import java.util.ArrayList;

public class GameInvitationActivity extends AppCompatActivity {

    private RecyclerView rvGameInvitation;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button btGameInvitationReturn;
    private static String gameID;
    private GameInvitationAdapter adapter = new GameInvitationAdapter();
    private ArrayList<FriendRelation> friends = new ArrayList<>();
    private ArrayList<TicTacToe> ttts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_invitation);
        initializeComponents();

        btGameInvitationReturn.setOnClickListener(v -> finish());
    }


    private void initializeComponents(){
        rvGameInvitation = findViewById(R.id.rvGameInvitation);
        btGameInvitationReturn = findViewById(R.id.btGameInvitationReturn);
        friends = getIntent().getExtras().getParcelableArrayList(GamesFragment.EXTRA_GAMES);
        gameID = getIntent().getExtras().getString(GamesFragment.EXTRA_GAMES_ID);
        ttts = getIntent().getExtras().getParcelableArrayList(GamesFragment.EXTRA_GAMES_TTT);

        rvGameInvitation.setLayoutManager(new LinearLayoutManager(this));
        rvGameInvitation.setAdapter(adapter);
        adapter.setFriendRelations(friends, ttts);

        adapter.setOnListenerGameListener(fr -> createGameWithFriend(fr));

    }


    private void createGameWithFriend(FriendRelation fr){
        if (gameID.equals(GamesFragment.TIC_TAC_TOE))
            createTicTacToeGame(fr);
    }


    private void createTicTacToeGame(FriendRelation fr){
        User user1, user2;
        String username1, username2, pk;
        if (fr.getUser1().getName().equals(auth.getCurrentUser().getDisplayName())){
            user1 = fr.getUser1();
            user2 = fr.getUser2();
        }
        else{
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
}