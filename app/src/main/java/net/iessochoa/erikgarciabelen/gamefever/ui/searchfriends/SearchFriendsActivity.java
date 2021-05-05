package net.iessochoa.erikgarciabelen.gamefever.ui.searchfriends;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import net.iessochoa.erikgarciabelen.gamefever.ui.friends.FriendsFragment;

import java.util.ArrayList;

public class SearchFriendsActivity extends AppCompatActivity {

    private SearchView svFriendName;
    private RecyclerView rvFriends;
    private FirestoreRecyclerAdapter<User, UserViewHolder> adapter;
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
    }


    private void initializeComponents() {
        svFriendName = findViewById(R.id.svFriendName);
        rvFriends = findViewById(R.id.rvFriends);
        rvFriends.setLayoutManager(new LinearLayoutManager(this));
        initializeAdapter();


    }

    private void initializeAdapter() {
        Query query = db.collection(FirebaseContract.UserEntry.COLLECTION_NAME)
                .orderBy(FirebaseContract.UserEntry.NAME);


        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<User, UserViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
                holder.setTextViews(model.getName());
                holder.setAddFriends();
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searchfriends, parent, false);
                return new UserViewHolder(view);
            }
        };
        rvFriends.setAdapter(adapter);
    }

    private void searchUsers(String userName) {
        Query query;
        if (userName != null) {
            query = db.collection(FirebaseContract.UserEntry.COLLECTION_NAME)
                    .orderBy(FirebaseContract.UserEntry.NAME).startAt(userName).endAt(userName + "\uf8ff");
        } else
            query = db.collection(FirebaseContract.UserEntry.COLLECTION_NAME)
                    .orderBy(FirebaseContract.UserEntry.NAME);


        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter.updateOptions(options);
    }

    private class UserViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView tvName, tvAlert;
        private ImageView ivAddFriends;
        final ArrayList<Invitation> invitations = getIntent().getExtras().getParcelableArrayList(FriendsFragment.EXTRA_INVITATION);
        final ArrayList<FriendRelation> friends = getIntent().getExtras().getParcelableArrayList(FriendsFragment.EXTRA_FRIENDS);

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setTextViews(String userName) {
            tvAlert = view.findViewById(R.id.tvAlert);
            tvName = view.findViewById(R.id.tvName);
            tvName.setText(userName);
        }


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