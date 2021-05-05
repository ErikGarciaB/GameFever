package net.iessochoa.erikgarciabelen.gamefever.ui.friends;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import net.iessochoa.erikgarciabelen.gamefever.R;
import net.iessochoa.erikgarciabelen.gamefever.model.FirebaseContract;
import net.iessochoa.erikgarciabelen.gamefever.model.FriendRelation;
import net.iessochoa.erikgarciabelen.gamefever.model.Invitation;
import net.iessochoa.erikgarciabelen.gamefever.ui.invitations.InvitationsActivity;
import net.iessochoa.erikgarciabelen.gamefever.ui.searchfriends.SearchFriendsActivity;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {

    public static String EXTRA_INVITATION = "net.iessochoa.erikgarciabelen.gamefever.ui.friends";
    public static String EXTRA_FRIENDS = "net.iessochoa.erikgarciabelen.gamefever.ui.friends.friends_FRIENDS";

    private FriendsViewModel friendsViewModel;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private TextView tvSearchFriends;
    private Button btInvitations;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    final private ArrayList<Invitation> globalInvitations = new ArrayList<>();
    final private ArrayList<Invitation> userInvitations = new ArrayList<>();
    final private ArrayList<FriendRelation> friends = new ArrayList<>();

    /**
     * Initialize the activity.
     * @param inflater
     * @param savedInstanceState
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        friendsViewModel =
                new ViewModelProvider(this).get(FriendsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_friends, container, false);
        initializeComponents(root);

        // If the SearchFriends button is pressed, the SearchFriends Activity is created and started.
        // The Activity takes a list of invitations to filter between invited and non-invited users.
        tvSearchFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(getActivity(), SearchFriendsActivity.class);
                searchIntent.putExtra(EXTRA_INVITATION, globalInvitations);
                searchIntent.putExtra(EXTRA_FRIENDS, friends);
                startActivity(searchIntent);
            }
        });

        // If the Invitations button is pressed, the Invitations Activity is created and started.
        // The Activity takes all the invitations to the user.
        btInvitations.setOnClickListener(v -> {
            Intent invitationIntent = new Intent(getActivity(), InvitationsActivity.class);
            invitationIntent.putExtra(EXTRA_INVITATION, userInvitations);
            startActivity(invitationIntent);
        });
        return root;
    }

    /**
     * Initialize the components of the Activity.
     * @param root Is used to initialize the components.
     */
    private void initializeComponents(View root){
        tvSearchFriends = root.findViewById(R.id.tvSearchFriends);
        btInvitations = root.findViewById(R.id.btInvitations);
    }

    /**
     * Consult the Firebase Database the count of invitations that have the user and collect all the invitations.
     */

    private void getInvitationsCount() {
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

    private void getFriends(){
        friends.clear();

        db.collection(FirebaseContract.FriendRelation.COLLECTION_NAME).get().addOnCompleteListener(task -> {
            for(QueryDocumentSnapshot document : task.getResult()){
                FriendRelation fr = document.toObject(FriendRelation.class);
                String userName1 = fr.getUser1().getName();
                String userName2 = fr.getUser2().getName();
                if (userName1.equals(auth.getCurrentUser().getDisplayName()) || userName2.equals(auth.getCurrentUser().getDisplayName()))
                    friends.add(fr);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getInvitationsCount();
        getFriends();
    }
}