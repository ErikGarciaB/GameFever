package net.iessochoa.erikgarciabelen.gamefever.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import net.iessochoa.erikgarciabelen.gamefever.R;
import net.iessochoa.erikgarciabelen.gamefever.model.TicTacToe;

import java.util.ArrayList;

public class ContinueAdapter extends RecyclerView.Adapter<ContinueAdapter.ContinueViewHolder> {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ArrayList<TicTacToe> ttts = new ArrayList<>();
    private OnItemClickEnterGame listenerEnterGameListener;
    private Context c;


    /**
     * Create the view holder and assign it to the layout item
     * @param parent
     * @param viewType
     * @return the view holder
     */
    @NonNull
    @Override
    public ContinueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_continue, parent, false);
        return new ContinueViewHolder(itemView);
    }

    /**
     * Bind the information of the invitation to layout's component
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ContinueViewHolder holder, int position) {
        if (!ttts.isEmpty()){
            TicTacToe ttt = ttts.get(position);
            String playerName = (ttt.getPlayer1().getName().equals(auth.getCurrentUser().getDisplayName()))
                    ? ttt.getPlayer2().getName() : ttt.getPlayer1().getName();
            holder.tvMatchName.setText(String.format(c.getString(R.string.match_with), playerName));
        }
    }

    /**
     * Get the size of the games.
     * @return The count.
     */
    @Override
    public int getItemCount() {
        if (!ttts.isEmpty())
            return ttts.size();
        else return 0;
    }

    /**
     * Setter of the TicTacToe games and the activity context.
     * @param ttts
     * @param c
     */
    public void setTtts(ArrayList<TicTacToe> ttts, Context c){
        this.ttts = ttts;
        this.c = c;
        notifyDataSetChanged();
    }

    /**
     * Create the viewholder of the recyclerView and create the components.
     */
    public class ContinueViewHolder extends RecyclerView.ViewHolder{

        private TextView tvMatchName;
        private Button btEnterGame;

        public ContinueViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMatchName = itemView.findViewById(R.id.tvMatchName);
            btEnterGame = itemView.findViewById(R.id.btEnterGame);

            /**
             * Create the listeners of the buttons
             */
            btEnterGame.setOnClickListener(v -> {
                if (listenerEnterGameListener != null)
                    listenerEnterGameListener.onItemEnterGameListener(ttts.get(ContinueViewHolder.this.getAdapterPosition()));
            });
        }
    }

    public interface OnItemClickEnterGame{
        void onItemEnterGameListener(TicTacToe ttt);
    }

    public void setOnListenerEnterGameListener(OnItemClickEnterGame listenerEnterGameListener) {
        this.listenerEnterGameListener = listenerEnterGameListener;
    }
}
