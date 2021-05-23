package net.iessochoa.erikgarciabelen.gamefever.ui.games;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.ChangeEventListener;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import net.iessochoa.erikgarciabelen.gamefever.R;
import net.iessochoa.erikgarciabelen.gamefever.adapter.TicTacToeAdapter;
import net.iessochoa.erikgarciabelen.gamefever.model.FirebaseContract;
import net.iessochoa.erikgarciabelen.gamefever.model.TicTacToe;

import static net.iessochoa.erikgarciabelen.gamefever.ui.friends.FriendsFragment.EXTRA_FRIENDS;

public class TicTacToeActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TicTacToe ttt;
    Button btTicTacToeReturn, btGameChat;
    TextView tvUsername1, tvUsername2, tvTurn;
    RecyclerView rvTicTacToe;
    TicTacToeAdapter adapter;
    String primaryKey;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String[] usernames;


    // 1 = CIRCLE
    // 2 = CROSS
    public static int PLAYERNUMBER = 0;

    public static boolean MATCH_ENDED = false;
    public static boolean YOUR_TURN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);
        initializeComponents();
    }


    private void initializeComponents() {
        ttt = getIntent().getExtras().getParcelable(GamesFragment.EXTRA_GAMES_TTT);
        primaryKey = ttt.getId();
        getPlayerNumber();
        btTicTacToeReturn = findViewById(R.id.btTicTacToeReturn);
        btGameChat = findViewById(R.id.btGameChat);
        tvUsername1 = findViewById(R.id.tvUsername1);
        tvUsername2 = findViewById(R.id.tvUsername2);
        tvTurn = findViewById(R.id.tvTurn);
        rvTicTacToe = findViewById(R.id.rvTicTacToe);
        rvTicTacToe.setLayoutManager(new LinearLayoutManager(this));

        String[] strings = primaryKey.split("-");
        usernames = strings;

        tvUsername1.setText(strings[1]);
        tvUsername2.setText(strings[2]);

        checkTurn(ttt);

        btTicTacToeReturn.setOnClickListener(v -> finish());

        btGameChat.setOnClickListener(v -> {
            Intent chatIntent = new Intent(this, GameChatActivity.class);
            chatIntent.putExtra(EXTRA_FRIENDS, ttt);
            startActivity(chatIntent);
        });
        initializeTicTacToeAdapter();
    }


    private void initializeTicTacToeAdapter() {
        Query q = db.collection(FirebaseContract.TicTacToeGameEntry.COLLECTION_NAME)
                .whereEqualTo(FirebaseContract.TicTacToeGameEntry.ID, primaryKey);

        FirestoreRecyclerOptions<TicTacToe> options = new
                FirestoreRecyclerOptions.Builder<TicTacToe>()
                .setQuery(q, TicTacToe.class)
                .setLifecycleOwner(this)
                .build();

        if (adapter != null) {
            adapter.stopListening();
        }

        adapter = new TicTacToeAdapter(options, this);
        rvTicTacToe.setAdapter(adapter);

        adapter.startListening();

        adapter.getSnapshots().addChangeEventListener(new ChangeEventListener() {
            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DocumentSnapshot snapshot, int newIndex, int oldIndex) {
                Log.e("TYPE", type.name());
                TicTacToe ttt = snapshot.toObject(TicTacToe.class);
                rvTicTacToe.smoothScrollToPosition(0);
                checkTurn(ttt);

                if (type.equals(ChangeEventType.REMOVED)) {
                    checkWinner(ttt);
                }
            }


            @Override
            public void onDataChanged() {
            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e) {

            }
        });

    }

    private void checkWinner(TicTacToe ttt) {
        if (PLAYERNUMBER == 1 && ttt.getPlayer2Turn())
            tvTurn.setText(R.string.victory);
        else if(PLAYERNUMBER == 1 && ttt.getPlayer1Turn())
            tvTurn.setText(R.string.defeat);

        if (PLAYERNUMBER == 2 && ttt.getPlayer1Turn())
            tvTurn.setText(R.string.victory);
        else if (PLAYERNUMBER == 2 && ttt.getPlayer2Turn())
            tvTurn.setText(R.string.defeat);

        if (ttt.getMoveCounter() == 9)
            tvTurn.setText(R.string.draw);
    }

    private void checkTurn(TicTacToe ttt) {

        if (PLAYERNUMBER == 1) {
            if (ttt.getPlayer1Turn()) {
                tvTurn.setText(R.string.your_Turn);
                YOUR_TURN = true;
            } else {
                tvTurn.setText(String.format(getString(R.string.enemy_Turn), usernames[2]));
                YOUR_TURN = false;
            }
        }
        if (PLAYERNUMBER == 2) {
            if (ttt.getPlayer2Turn()) {
                tvTurn.setText(R.string.your_Turn);
                YOUR_TURN = true;
            } else {
                tvTurn.setText(String.format(getString(R.string.enemy_Turn), usernames[1]));
                YOUR_TURN = false;
            }
        }
    }

    private void getPlayerNumber() {
        if (ttt.getPlayer1().getName().equals(auth.getCurrentUser().getDisplayName()))
            PLAYERNUMBER = 1;
        else
            PLAYERNUMBER = 2;
    }
}