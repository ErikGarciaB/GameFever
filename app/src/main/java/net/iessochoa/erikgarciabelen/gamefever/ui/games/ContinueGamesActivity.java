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
import net.iessochoa.erikgarciabelen.gamefever.adapter.ContinueAdapter;
import net.iessochoa.erikgarciabelen.gamefever.model.TicTacToe;

import java.util.ArrayList;

public class ContinueGamesActivity extends AppCompatActivity {

    public static String EXTRA_CONTINUE_ID = "package net.iessochoa.erikgarciabelen.gamefever.ui.games.id";

    private RecyclerView rvContinuedGames;
    private Button btCGReturn;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ArrayList<TicTacToe> ttts;
    private ContinueAdapter adapter = new ContinueAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue_games);
        initializeComponents();
        btCGReturn.setOnClickListener(v -> finish());
    }


    private void initializeComponents(){
        btCGReturn = findViewById(R.id.btCGReturn);
        rvContinuedGames = findViewById(R.id.rvContinueGames);
        rvContinuedGames.setLayoutManager(new LinearLayoutManager(this));
        ttts = getIntent().getExtras().getParcelableArrayList(GamesFragment.EXTRA_GAMES_TTT);

        rvContinuedGames.setAdapter(adapter);

        adapter.setTtts(ttts, getApplicationContext());

        adapter.setOnListenerEnterGameListener(ttt -> {
            Intent i = new Intent(this, TicTacToeActivity.class);
            i.putExtra(GamesFragment.EXTRA_GAMES_TTT, ttt);
            startActivity(i);
            finish();
        });

    }



}