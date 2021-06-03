package net.iessochoa.erikgarciabelen.gamefever.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import net.iessochoa.erikgarciabelen.gamefever.R;
import net.iessochoa.erikgarciabelen.gamefever.model.FriendRelation;
import net.iessochoa.erikgarciabelen.gamefever.model.TicTacToe;
import net.iessochoa.erikgarciabelen.gamefever.model.User;

import java.util.ArrayList;

public class GameInvitationAdapter extends RecyclerView.Adapter<GameInvitationAdapter.GameInvitationViewHolder> {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ArrayList<FriendRelation> friendRelations;

    private ArrayList<TicTacToe> ttts;
    private OnItemClickInviteGameListener listenerGameListener;

    /**
     * Create the view holder and assign it to the layout item
     * @param parent
     * @param viewType
     * @return the view holder
     */
    @NonNull
    @Override
    public GameInvitationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game_invitation, parent, false);
        return new GameInvitationViewHolder(itemView);
    }

    /**
     * Bind the information of the invitation to layout's component
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull GameInvitationViewHolder holder, int position) {
        if (friendRelations != null){
            FriendRelation fr = friendRelations.get(position);
            String friendName = (fr.getUser1().getName().equals(auth.getCurrentUser().getDisplayName()))
                    ? fr.getUser2().getName() : fr.getUser1().getName();
            holder.tvInvitationName.setText(friendName);

            if (isAlreadyInvited(friendName)){
                holder.tvGameAlert.setVisibility(View.VISIBLE);
                holder.btInviteGame.setVisibility(View.INVISIBLE);
            } else {
                holder.tvGameAlert.setVisibility(View.INVISIBLE);
                holder.btInviteGame.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Search if the user is already on the game
     * @param username The user to search
     * @return true if is in one game.
     */
    private boolean isAlreadyInvited(String username){
        boolean bool = false;
        for(TicTacToe ttt : ttts){
            User u1 = ttt.getPlayer1();
            User u2 = ttt.getPlayer2();

            if (u2.getName().equals(username)||u1.getName().equals(username))
                bool = true;
        }
        return bool;
    }

    @Override
    public int getItemCount() {
        if (friendRelations != null)
            return friendRelations.size();
        else return 0;
    }

    /**
     * Set of the friends and the tictactoes games
     * @param friendRelations
     * @param ttts
     */
    public void setFriendRelations(ArrayList<FriendRelation> friendRelations, ArrayList<TicTacToe> ttts){
        this.friendRelations = friendRelations;
        this.ttts = ttts;
        notifyDataSetChanged();
    }

    /**
     * Create the viewholder of the recyclerView and create the components.
     */
    public class GameInvitationViewHolder extends RecyclerView.ViewHolder{

        private TextView tvGameAlert, tvInvitationName;
        private Button btInviteGame;

        public GameInvitationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGameAlert = itemView.findViewById(R.id.tvGameAlert);
            tvInvitationName = itemView.findViewById(R.id.tvInvitationName);
            btInviteGame = itemView.findViewById(R.id.btInviteGame);

            /**
             * Create the listener of the button
             */
            btInviteGame.setOnClickListener(v ->{
                if (listenerGameListener != null){
                    listenerGameListener.onItemInviteGameListener(friendRelations.get(GameInvitationViewHolder.this.getAdapterPosition()));
                }
            });
        }
    }

    public interface OnItemClickInviteGameListener{
        void onItemInviteGameListener(FriendRelation fr);
    }

    public void setOnListenerGameListener(OnItemClickInviteGameListener listenerGameListener) {
        this.listenerGameListener = listenerGameListener;
    }
}
