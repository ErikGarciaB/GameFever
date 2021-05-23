 package net.iessochoa.erikgarciabelen.gamefever.ui.friends;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import net.iessochoa.erikgarciabelen.gamefever.R;
import net.iessochoa.erikgarciabelen.gamefever.model.FirebaseContract;
import net.iessochoa.erikgarciabelen.gamefever.model.FriendRelation;
import net.iessochoa.erikgarciabelen.gamefever.model.Invitation;
import net.iessochoa.erikgarciabelen.gamefever.model.User;

import java.util.ArrayList;

public class SearchFriendsActivity extends AppCompatActivity {

    private SearchView svFriendName;
    private RecyclerView rvFriends;
    private Button btSFReturn;
    private FirestoreRecyclerAdapter<User, SearchFriendViewHolder> adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);
        initializeComponents();

        svFriendName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchUsers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchUsers(null);
                return false;
            }
        });
        btSFReturn.setOnClickListener(v -> finish());
    }


    /**
     * Initialize the components of the activity
     */
    private void initializeComponents() {
        svFriendName = findViewById(R.id.svFriendName);
        rvFriends = findViewById(R.id.rvSearchFriends);
        btSFReturn = findViewById(R.id.btSFReturn);
        rvFriends.setLayoutManager(new LinearLayoutManager(this));
        initializeAdapter();


    }

    /**
     * Initialize the adapter of the users in the firebase.
     */
    private void initializeAdapter() {
        Query query = db.collection(FirebaseContract.UserEntry.COLLECTION_NAME)
                .orderBy(FirebaseContract.UserEntry.NAME);


        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<User, SearchFriendViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull SearchFriendViewHolder holder, int position, @NonNull User model) {
                holder.setTextViews(model.getName());
                holder.setAddFriends();
            }

            @NonNull
            @Override
            public SearchFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searchfriends, parent, false);
                return new SearchFriendViewHolder(view);
            }
        };
        rvFriends.setAdapter(adapter);
    }

    /**
     * Create the query that will search the users in the firebase database
     * @param userName if the username is empty the query will search all the users, if not,
     *                  will search users that begins with the param
     */
    private void searchUsers(String userName) {
        Query query;
        if (userName != null) {
            query = db.collection(FirebaseContract.UserEntry.COLLECTION_NAME)
                    .orderBy(FirebaseContract.UserEntry.NAME).startAt(userName);
        } else
            query = db.collection(FirebaseContract.UserEntry.COLLECTION_NAME)
                    .orderBy(FirebaseContract.UserEntry.NAME);


        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter.updateOptions(options);
    }

    /**
     * Create and control the view for each item in the recyclerView
     */
    private class SearchFriendViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView tvName, tvAlert;
        private ImageView ivAddFriends;
        final ArrayList<Invitation> invitations = getIntent().getExtras().getParcelableArrayList(FriendsFragment.EXTRA_INVITATION);
        final ArrayList<FriendRelation> friends = getIntent().getExtras().getParcelableArrayList(FriendsFragment.EXTRA_FRIENDS);

        public SearchFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        /**
         * Create the textview component of the item and put the username in one text
         * @param userName Is the name that will be display in the textview
         */
        public void setTextViews(String userName) {
            tvAlert = view.findViewById(R.id.tvAlert);
            tvName = view.findViewById(R.id.tvName);
            tvName.setText(userName);
        }


        /**
         * Create the button that will be use to create a invitation and filter
         * the user that is already invited and the one that is already your friend
         */
        public void setAddFriends() {
            final boolean[] isInvited = new boolean[1];
            isInvited[0] = false;
            boolean isUserInvitedYou = false;
            boolean isUserCurrentUser = false;
            boolean isUserFriend = false;
            String currentName = auth.getCurrentUser().getDisplayName();
            ivAddFriends = view.findViewById(R.id.ivAddFriend);
            String invitedName, hostName, textName;
            textName = tvName.getText().toString();

            if (textName.equals(auth.getCurrentUser().getDisplayName()))
                isUserCurrentUser = true;
            if (isUserCurrentUser) {
                ivAddFriends.setVisibility(View.INVISIBLE);
                tvAlert.setVisibility(View.VISIBLE);
                tvAlert.setText(R.string.user_is_you);
            } else {
                for (Invitation i : invitations) {
                    invitedName = i.getInvitedUser().getName();
                    hostName = i.getHostUser().getName();
                    if (invitedName.equals(textName) && hostName.equals(currentName))
                        isInvited[0] = true;
                    else if (invitedName.equals(currentName) && hostName.equals(textName)) {
                        isUserInvitedYou = true;
                    }
                }
                for (FriendRelation friend : friends){
                    if (friend.getUser1().getName().equals(textName) || friend.getUser2().getName().equals(textName))
                        isUserFriend = true;
                }
                if (!isInvited[0] && !isUserInvitedYou) {
                    ivAddFriends.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SearchFriendsActivity.this);
                            builder.setMessage(String.format(getString(R.string.confirm_invitation), tvName.getText()))
                                    .setTitle(R.string.confirm_title);
                            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final User[] users = new User[2];
                                    db.collection(FirebaseContract.UserEntry.COLLECTION_NAME).document(auth.getCurrentUser().getDisplayName()).get().
                                            addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    users[0] = task.getResult().toObject(User.class);
                                                    db.collection(FirebaseContract.UserEntry.COLLECTION_NAME).document(tvName.getText().toString()).get().
                                                            addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    users[1] = task.getResult().toObject(User.class);
                                                                    sendInvitation(users[0], users[1]);
                                                                    ivAddFriends.setVisibility(View.INVISIBLE);
                                                                    tvAlert.setVisibility(View.VISIBLE);
                                                                    tvAlert.setText(R.string.already_invited);
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.show();
                        }
                    });
                }
                if (isInvited[0]) {
                    ivAddFriends.setVisibility(View.INVISIBLE);
                    tvAlert.setVisibility(View.VISIBLE);
                    tvAlert.setText(R.string.already_invited);
                }
                if (isUserInvitedYou) {
                    ivAddFriends.setVisibility(View.INVISIBLE);
                    tvAlert.setVisibility(View.VISIBLE);
                    tvAlert.setText(R.string.user_is_invited);
                }
                if (isUserFriend){
                    ivAddFriends.setVisibility(View.INVISIBLE);
                    tvAlert.setVisibility(View.VISIBLE);
                    tvAlert.setText(R.string.user_is_friend);
                }
            }
        }

    }


    /**
     * Create the invitation in the database
     * @param hostUser Is the current user
     * @param invitedUser Is the user that is invited
     */
    private void sendInvitation(User hostUser, User invitedUser) {
        Invitation inv = new Invitation(hostUser, invitedUser);
        db.collection(FirebaseContract.InvitationEntry.COLLECTION_NAME).document().set(inv);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }
}