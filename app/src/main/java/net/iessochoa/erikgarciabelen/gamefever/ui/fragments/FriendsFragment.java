package net.iessochoa.erikgarciabelen.gamefever.ui.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import net.iessochoa.erikgarciabelen.gamefever.R;
import net.iessochoa.erikgarciabelen.gamefever.adapter.FriendAdapter;
import net.iessochoa.erikgarciabelen.gamefever.model.FirebaseContract;
import net.iessochoa.erikgarciabelen.gamefever.model.FriendRelation;
import net.iessochoa.erikgarciabelen.gamefever.model.Invitation;
import net.iessochoa.erikgarciabelen.gamefever.ui.friends.ChatActivity;
import net.iessochoa.erikgarciabelen.gamefever.ui.friends.InvitationsActivity;
import net.iessochoa.erikgarciabelen.gamefever.ui.friends.SearchFriendsActivity;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {

    public static String EXTRA_INVITATION = "net.iessochoa.erikgarciabelen.gamefever.ui.friends";
    public static String EXTRA_FRIENDS = "net.iessochoa.erikgarciabelen.gamefever.ui.friends.friends_FRIENDS";

    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private TextView tvSearchFriends;
    private Button btInvitations;

    private static final FriendAdapter adapter = new FriendAdapter();

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private RecyclerView rvFriends;
    private final ArrayList<Invitation> globalInvitations = new ArrayList<>();
    private final ArrayList<Invitation> userInvitations = new ArrayList<>();
    private final ArrayList<FriendRelation> friends = new ArrayList<>();

    /**
     * Initialize the activity.
     * @param inflater
     * @param savedInstanceState
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_friends, container, false);

        initializeComponents(root);

        /**
         * If the SearchFriends button is pressed, the SearchFriends Activity is created and started.
         * The Activity takes a list of invitations to filter between invited and non-invited users.
         */

        tvSearchFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(getActivity(), SearchFriendsActivity.class);
                searchIntent.putExtra(EXTRA_INVITATION, globalInvitations);
                searchIntent.putExtra(EXTRA_FRIENDS, friends);
                startActivity(searchIntent);
            }
        });

        /**
         * If the Invitations button is pressed, the Invitations Activity is created and started.
         * The Activity takes all the invitations to the user.
         */
        btInvitations.setOnClickListener(v -> {
            Intent invitationIntent = new Intent(getActivity(), InvitationsActivity.class);
            invitationIntent.putExtra(EXTRA_INVITATION, userInvitations);
            startActivity(invitationIntent);
        });

        /**
         * Thread sleep to try stopping the user to change fragment quickly and making the app crash
         */
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return root;
    }

    /**
     * Initialize the components of the Activity.
     * @param root Is used to initialize the components.
     */
    private void initializeComponents(View root){
        tvSearchFriends = root.findViewById(R.id.tvSearchFriends);
        btInvitations = root.findViewById(R.id.btInvitations);

        rvFriends = root.findViewById(R.id.rvFriends);
        rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFriends.setAdapter(adapter);

        /**
         * Create an animation that show chat and invitation button.
         */
        adapter.setOnExpandListener((vlOptions, ivExpand, cdFriend) -> {
            if(vlOptions.getVisibility() == View.VISIBLE){
                TransitionManager.beginDelayedTransition(cdFriend,
                        new AutoTransition());
                vlOptions.setVisibility(View.GONE);
                ivExpand.setImageResource(R.drawable.ic_down_sign);
            }
            else {
                TransitionManager.beginDelayedTransition(cdFriend,
                        new AutoTransition());
                vlOptions.setVisibility(View.VISIBLE);
                ivExpand.setImageResource(R.drawable.ic_up_sign);
            }
        });

        /**
         * Initialize the chat activity with the friend selected
         */
        adapter.setOnChatListener(fr -> {
            Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
            chatIntent.putExtra(EXTRA_FRIENDS, fr);
            startActivity(chatIntent);
        });

        adapter.setOnDeleteListener(fr -> deleteFriend(fr.getId()));
    }

    /**
     * Consult the Firebase Database the count of invitations that have the user and collect all the invitations.
     */
    private void getInvitations() {
        db.collection(FirebaseContract.InvitationEntry.COLLECTION_NAME).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                globalInvitations.clear();
                userInvitations.clear();
                String actualUserName = auth.getCurrentUser().getDisplayName();
                for (QueryDocumentSnapshot q : task.getResult()){
                    globalInvitations.add(q.toObject(Invitation.class));
                }
                for (Invitation i: globalInvitations) {
                    if (i.getInvitedUser().getName().equals(actualUserName))
                        userInvitations.add(i);
                }
                btInvitations.setText(getString(R.string.number_invitations, userInvitations.size()));
            }
        });
    }

     private void deleteFriend(String friendRelationId){
         AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
         alert.setMessage(R.string.sure_delete)
                 .setTitle(R.string.alert);
         alert.setPositiveButton(R.string.yes, (dialog, which) -> {
             db.collection(FirebaseContract.FriendRelation.COLLECTION_NAME)
                     .document(friendRelationId).collection(FirebaseContract.ChatEntry.COLLECTION_NAME)
                     .get().addOnCompleteListener(task -> {
                        for (DocumentSnapshot d : task.getResult()){
                            db.collection(FirebaseContract.FriendRelation.COLLECTION_NAME)
                                    .document(friendRelationId).collection(FirebaseContract.ChatEntry.COLLECTION_NAME)
                                    .document(d.getId()).delete();
                        }
             });
             db.collection(FirebaseContract.FriendRelation.COLLECTION_NAME)
                    .document(friendRelationId).delete().addOnCompleteListener(task -> {
                        Toast.makeText(this.getActivity(), R.string.delete_confirm, Toast.LENGTH_SHORT).show();
                        getFriends(auth.getCurrentUser().getDisplayName());
            });
         });
         alert.setNegativeButton(R.string.no, (dialog, which) -> {});
         alert.show();
     }

    /**
     * Get all the friends from the database.
     */
    public void getFriends(String userName){
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
            adapter.setFriendList(friends);
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        getInvitations();
        getFriends(auth.getCurrentUser().getDisplayName());
    }

}