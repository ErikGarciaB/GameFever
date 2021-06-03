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

import net.iessochoa.erikgarciabelen.gamefever.R;
import net.iessochoa.erikgarciabelen.gamefever.adapter.ContinueAdapter;
import net.iessochoa.erikgarciabelen.gamefever.model.FirebaseContract;
import net.iessochoa.erikgarciabelen.gamefever.model.FriendRelation;
import net.iessochoa.erikgarciabelen.gamefever.model.TicTacToe;
import net.iessochoa.erikgarciabelen.gamefever.ui.fragments.GamesFragment;

import java.util.ArrayList;

public class ContinueGamesActivity extends AppCompatActivity {


    private RecyclerView rvContinuedGames;
    private Button btCGReturn;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ArrayList<FriendRelation> friends = new ArrayList<>();

    private ArrayList<TicTacToe> ttts = new ArrayList<>();
    private ContinueAdapter adapter = new ContinueAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue_games);

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
        btCGReturn.setOnClickListener(v -> finish());

        getTicTacToeGames();
    }

    /**
     * Initialize the components and the adapter.
     */
    private void initializeComponents(){
        btCGReturn = findViewById(R.id.btCGReturn);
        rvContinuedGames = findViewById(R.id.rvContinueGames);
        rvContinuedGames.setLayoutManager(new LinearLayoutManager(this));
        rvContinuedGames.setAdapter(adapter);

        /**
         * When the listener of the adapter is pressed the user is send to the selected game.
         */
        adapter.setOnListenerEnterGameListener(ttt -> {
            Intent i = new Intent(this, TicTacToeActivity.class);
            i.putExtra(GamesFragment.EXTRA_GAMES_TTT, ttt);
            startActivity(i);
            finish();
        });

    }

    /**
     * Get the unfinished tictactoegames of the users.
     *
     */
    private void getTicTacToeGames() {
        db.collection(FirebaseContract.TicTacToeGameEntry.COLLECTION_NAME)
                .get().addOnCompleteListener(task -> {
            for (DocumentSnapshot d : task.getResult()) {
                getTTTs(d.toObject(TicTacToe.class));
            }

            adapter.setTtts(ttts, getApplicationContext());
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