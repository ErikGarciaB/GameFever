package net.iessochoa.erikgarciabelen.gamefever.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
import net.iessochoa.erikgarciabelen.gamefever.model.Message;
import net.iessochoa.erikgarciabelen.gamefever.model.TicTacToe;
import net.iessochoa.erikgarciabelen.gamefever.model.User;
import net.iessochoa.erikgarciabelen.gamefever.ui.games.ContinueGamesActivity;
import net.iessochoa.erikgarciabelen.gamefever.ui.games.GameInvitationActivity;
import net.iessochoa.erikgarciabelen.gamefever.ui.games.TicTacToeActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamesFragment extends Fragment {

    public static final String TIC_TAC_TOE = "TIC_TAC_TOE";
    public static final String EXTRA_GAMES = "net.iessochoa.erikgarciabelen.gamefever.ui.games";
    public static final String EXTRA_GAMES_ID = "net.iessochoa.erikgarciabelen.gamefever.ui.gamesID";
    public static final String EXTRA_GAMES_TTT = "net.iessochoa.erikgarciabelen.gamefever.ui.TTT";

    private final User actualUser = new User();
    private Random r = new Random();
    private GameAdapter adapter = new GameAdapter();
    private RecyclerView rvGames;
    private ArrayList<String> games = new ArrayList<>();
    private Button btContinue;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<User> nonFriends = new ArrayList<>();

    private final ArrayList<FriendRelation> friends = new ArrayList<>();
    private final ArrayList<TicTacToe> ttts = new ArrayList<>();
    private final ArrayList<String> friendNames = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_games, container, false);


        initializeComponents(root);

        /**
         * Start the ContinueGamesActivity
         */
        btContinue.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ContinueGamesActivity.class);
            startActivity(intent);
        });

        return root;
    }

    /**
     * Initialize the components and the adapter
     *
     * @param root
     */
    private void initializeComponents(View root) {
        games.clear();
        games.add(TIC_TAC_TOE);
        btContinue = root.findViewById(R.id.btContinue);
        rvGames = root.findViewById(R.id.rvGames);
        rvGames.setLayoutManager(new LinearLayoutManager(getContext()));
        rvGames.setAdapter(adapter);

        adapter.setGames(games);

        /**
         * Expand the item selected in the game adapter.
         */
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

        /**
         * Create the Invitation activity and start it
         */
        adapter.setOnClickListenerInvite((tvGameID) -> {
            Intent inviteIntent = new Intent(getActivity(), GameInvitationActivity.class);
            inviteIntent.putExtra(EXTRA_GAMES_ID, tvGameID.getText().toString());
            startActivity(inviteIntent);
        });

        /**
         * Search in the database the users that doesn't have a relation with the user. Then the application select one user randomly
         * and create a game match.
         */
        adapter.setOnClickListenerPlay((tvGameID) -> {
                getFriends(auth.getCurrentUser().getDisplayName());
        });

    }


    /**
     * Get a list of users that don't have relation with the user. When the query is done, select one of the player and a match is created.
     * @param userNames Is the names of the users that don't have relation with the user in the firebase database.
     */
    private void getNonFriends(List<String> userNames) {
        db.collection(FirebaseContract.UserEntry.COLLECTION_NAME)
                .whereNotIn(FirebaseContract.UserEntry.NAME, userNames).get()
                .addOnCompleteListener(task -> {
                    for (DocumentSnapshot d : task.getResult()) {
                        User u = d.toObject(User.class);
                        nonFriends.add(u);
                    }
                    /**
                     * If there is one user or more that is not friend, select a random user and initialize the game with it.
                     */
                    if (!nonFriends.isEmpty()) {
                        int random = r.nextInt(nonFriends.size());
                        User rivalUser = nonFriends.get(random);

                        String pk = TIC_TAC_TOE + "-" + actualUser.getName() + "-" + rivalUser.getName();
                        TicTacToe ttt = new TicTacToe(actualUser, rivalUser, pk);

                        db.collection(FirebaseContract.TicTacToeGameEntry.COLLECTION_NAME).document(pk).set(ttt);
                        db.collection(FirebaseContract.TicTacToeGameEntry.COLLECTION_NAME).document(pk)
                                .collection(FirebaseContract.ChatEntry.COLLECTION_NAME).document().set(new Message())
                                .addOnCompleteListener(task1 -> {
                                    Intent i = new Intent(getActivity(), TicTacToeActivity.class);
                                    i.putExtra(GamesFragment.EXTRA_GAMES_TTT, ttt);
                                    startActivity(i);
                                });
                    }
                    /**
                     * If not, a message show up.
                     */
                    else {
                        Toast.makeText(getContext(), R.string.no_users, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Get the user's friends and the non-Friend user's. Then call to get all the games.
     * @param userName The username
     */
    private void getFriends(String userName) {
        friends.clear();
        friendNames.clear();
        nonFriends.clear();

        String actualName = auth.getCurrentUser().getDisplayName();
        Log.e("ERRORUSER", auth.getCurrentUser().getDisplayName());

        db.collection(FirebaseContract.UserEntry.COLLECTION_NAME).document(actualName)
                .get().addOnCompleteListener(task -> {
            User user = task.getResult().toObject(User.class);
            actualUser.setName(user.getName());
        });

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
            friendNames.add(auth.getCurrentUser().getDisplayName());
        });


    }

    /**
     * Get the unfinished tictactoegames of the users.
     *
     * @param friends The friends that has the user to find if they have a game unfinished
     */
    private void getTicTacToeGames(ArrayList<FriendRelation> friends) {
        List<Integer> randomPositions = new ArrayList<>();
        List<String> auxFriendsName = new ArrayList<>();
        ttts.clear();
        db.collection(FirebaseContract.TicTacToeGameEntry.COLLECTION_NAME)
                .get().addOnCompleteListener(task -> {
            for (DocumentSnapshot d : task.getResult()) {
                getUsersTTTs(d.toObject(TicTacToe.class));
            }
            /**
             * Slice the friends list to search the nonFriends if the size is greater than 10 in the firebase Database due to the policy of Firebase.
             * Firebase can't search more than 10 usersName in one query.
             */
            if (friendNames.size() >= 10) {
                while(randomPositions.size() != 10){
                    int random = r.nextInt(friendNames.size());
                    if (!randomPositions.contains(random))
                        auxFriendsName.add(friendNames.get(random));
                }
                getNonFriends(auxFriendsName);
            } else{
                getNonFriends(friendNames);
            }
        });
    }

    /**
     * Get a Tic Tac Toe game and filter if the user is in the game.
     * @param ttt Is the game to filter
     */
    private void getUsersTTTs(TicTacToe ttt) {
        String primaryKey = ttt.getId();
        String[] strings = primaryKey.split("-");

        String username1 = strings[1];
        String username2 = strings[2];

        if (actualUser.getName().equals(username1) || actualUser.getName().equals(username2))
            ttts.add(ttt);

        if (actualUser.getName().equals(username1))
            friendNames.add(username2);
        if (actualUser.getName().equals(username2))
            friendNames.add(username1);


    }
}