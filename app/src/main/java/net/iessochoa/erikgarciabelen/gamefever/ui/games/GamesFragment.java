package net.iessochoa.erikgarciabelen.gamefever.ui.games;

import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import net.iessochoa.erikgarciabelen.gamefever.R;
import net.iessochoa.erikgarciabelen.gamefever.adapter.GameAdapter;
import net.iessochoa.erikgarciabelen.gamefever.model.FirebaseContract;
import net.iessochoa.erikgarciabelen.gamefever.model.FriendRelation;
import net.iessochoa.erikgarciabelen.gamefever.model.TicTacToe;

import java.util.ArrayList;
import java.util.List;

public class GamesFragment extends Fragment {

    public static final String TIC_TAC_TOE = "TIC_TAC_TOE";
    public static final String EXTRA_GAMES = "net.iessochoa.erikgarciabelen.gamefever.ui.games";
    public static final String EXTRA_GAMES_ID = "net.iessochoa.erikgarciabelen.gamefever.ui.gamesID";
    public static final String EXTRA_GAMES_TTT = "net.iessochoa.erikgarciabelen.gamefever.ui.TTT";

    private GameAdapter adapter = new GameAdapter();
    private RecyclerView rvGames;
    private ArrayList<String> games = new ArrayList<>();
    private Button btContinue;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final ArrayList<FriendRelation> friends = new ArrayList<>();
    private final ArrayList<TicTacToe> ttts = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_games, container, false);
        initializeComponents(root);

        btContinue.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), ContinueGamesActivity.class);
            intent.putParcelableArrayListExtra(EXTRA_GAMES_TTT ,ttts);
            startActivity(intent);
        });

        return root;
    }

    private void initializeComponents(View root) {
        games.add(TIC_TAC_TOE);
        btContinue = root.findViewById(R.id.btContinue);
        rvGames = root.findViewById(R.id.rvGames);
        rvGames.setLayoutManager(new LinearLayoutManager(getContext()));
        rvGames.setAdapter(adapter);

        adapter.setGames(games);

        adapter.setOnClickListenerExpand((llGame, llOptions, ivGameExpand) -> {
            if (llOptions.getVisibility() == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(llGame,
                        new AutoTransition());
                llOptions.setVisibility(View.GONE);
                ivGameExpand.setImageResource(R.drawable.ic_down_sign);
            } else {
                TransitionManager.beginDelayedTransition(llGame,
                        new AutoTransition());
                llOptions.setVisibility(View.VISIBLE);
                ivGameExpand.setImageResource(R.drawable.ic_up_sign);
            }
        });

        adapter.setOnClickListenerInvite((tvGameID) -> {
            Intent inviteIntent = new Intent(getActivity(), GameInvitationActivity.class);
            inviteIntent.putParcelableArrayListExtra(EXTRA_GAMES, friends);
            inviteIntent.putExtra(EXTRA_GAMES_ID, tvGameID.getText().toString());
            inviteIntent.putParcelableArrayListExtra(EXTRA_GAMES_TTT, ttts);
            startActivity(inviteIntent);
        });

        adapter.setOnClickListenerPlay((tvGameID) -> {

        });

    }



    @Override
    public void onResume() {
        super.onResume();
        getFriends(auth.getCurrentUser().getDisplayName());
    }

    private void getFriends(String userName){
        friends.clear();

        db.collection(FirebaseContract.FriendRelation.COLLECTION_NAME).get().addOnCompleteListener(task -> {
            for(QueryDocumentSnapshot document : task.getResult()){
                FriendRelation fr = document.toObject(FriendRelation.class);
                String userName1 = fr.getUser1().getName();
                String userName2 = fr.getUser2().getName();
                if (userName1.equals(userName) || userName2.equals(userName)) {
                    friends.add(fr);
                }
            }
            getTicTacToeGames(friends);
        });
    }

    private void getTicTacToeGames(ArrayList<FriendRelation> friends) {

        ttts.clear();

        List<String> pks = new ArrayList<>();
        String pk, username1, username2;
        for (FriendRelation fr : friends) {
            username1 = fr.getUser1().getName();
            username2 = fr.getUser2().getName();

            pk = TIC_TAC_TOE + "-" + username1 + "-" + username2;
            pks.add(pk);
            pk = TIC_TAC_TOE + "-" + username2 + "-" + username1;
            pks.add(pk);
        }

        if (!friends.isEmpty()) {
            db.collection(FirebaseContract.TicTacToeGameEntry.COLLECTION_NAME)
                    .whereIn(FirebaseContract.TicTacToeGameEntry.ID, pks).get().addOnCompleteListener(task -> {
                for (DocumentSnapshot d : task.getResult()) {
                    ttts.add(d.toObject(TicTacToe.class));
                }
            });
        }
    }
}