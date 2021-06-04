package net.iessochoa.erikgarciabelen.gamefever.ui.games;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import net.iessochoa.erikgarciabelen.gamefever.ui.fragments.GamesFragment;

import static net.iessochoa.erikgarciabelen.gamefever.ui.fragments.FriendsFragment.EXTRA_FRIENDS;

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

    private static boolean CHAT_ENABLE = true;
    public static boolean MATCH_ENDED = false;
    public static boolean MATCH_DRAWED = false;
    public static boolean YOUR_TURN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        /**
         * Detect if the mobile has internet. If the mobile doesn't have internet the application shut down.
         */
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.CONNECTED) {
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
    }


    /**
     * Initialize the components
     */
    private void initializeComponents() {
        CHAT_ENABLE = true;
        MATCH_DRAWED = false;
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

        /**
         * Finish the activity
         */
        btTicTacToeReturn.setOnClickListener(v -> finish());

        /**
         * Create and launch the chat activity of the game
         */
        btGameChat.setOnClickListener(v -> {
            if (CHAT_ENABLE) {
                Intent chatIntent = new Intent(this, GameChatActivity.class);
                chatIntent.putExtra(EXTRA_FRIENDS, ttt);
                startActivity(chatIntent);
            }
        });
        initializeTicTacToeAdapter();
    }


    /**
     * Create the adapter and get the information of the game
     */
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
            /**
             * When the game is changed in the database, check if the turns. If is detected that the games is removed of the databases (winned)
             * check if the user is the winner and is showed on the screen
             * @param type
             * @param snapshot
             * @param newIndex
             * @param oldIndex
             */
            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DocumentSnapshot snapshot, int newIndex, int oldIndex) {
                Log.e("TYPE", type.name());
                TicTacToe ttt = snapshot.toObject(TicTacToe.class);
                rvTicTacToe.smoothScrollToPosition(0);
                checkTurn(ttt);

                if (type.equals(ChangeEventType.REMOVED)) {
                    checkWinner(ttt);
                    CHAT_ENABLE = false;
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

    /**
     * Check who is the winner.
     * @param ttt
     */
    private void checkWinner(TicTacToe ttt) {
        if (PLAYERNUMBER == 1 && ttt.getPlayer2Turn())
            tvTurn.setText(R.string.victory);
        else if(PLAYERNUMBER == 1 && ttt.getPlayer1Turn())
            tvTurn.setText(R.string.defeat);

        if (PLAYERNUMBER == 2 && ttt.getPlayer1Turn())
            tvTurn.setText(R.string.victory);
        else if (PLAYERNUMBER == 2 && ttt.getPlayer2Turn())
            tvTurn.setText(R.string.defeat);

        if (ttt.getDrawGame())
            tvTurn.setText(R.string.draw);
    }

    /**
     * Check the turn of the user
     * @param ttt
     */
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

    /**
     * Get who is the user on the game
     */
    private void getPlayerNumber() {
        if (ttt.getPlayer1().getName().equals(auth.getCurrentUser().getDisplayName()))
            PLAYERNUMBER = 1;
        else
            PLAYERNUMBER = 2;
    }
}