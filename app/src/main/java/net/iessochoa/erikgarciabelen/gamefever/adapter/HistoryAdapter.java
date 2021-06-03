package net.iessochoa.erikgarciabelen.gamefever.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;

import net.iessochoa.erikgarciabelen.gamefever.R;
import net.iessochoa.erikgarciabelen.gamefever.model.History;

public class HistoryAdapter extends FirestoreRecyclerAdapter<History, HistoryAdapter.HistoryHolder> {

    private Context context;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public HistoryAdapter(@NonNull FirestoreRecyclerOptions<History> options, Context context) {
        super(options);
        this.context = context;
    }

    /**
     * Bind the information of the invitation to layout's component
     * @param holder
     * @param position
     */
    @Override
    protected void onBindViewHolder(@NonNull HistoryHolder holder, int position, @NonNull History model) {
        String userName = auth.getCurrentUser().getDisplayName();

        String rivalName = (model.getPlayerName1().equals(userName)) ? model.getPlayerName2() : model.getPlayerName1();

        holder.tvHistoryGame.setText(String.format(context.getString(R.string.game_with), model.getGameName(), rivalName));
        holder.tvDate.setText(model.getTime().toString());

        if (model.getPlayerName1().equals(userName)) {
            if (model.getPlayer1Win())
                holder.tvResult.setText(R.string.history_victory);
            else
                holder.tvResult.setText(R.string.history_defeat);
        } else{
            if (model.getPlayer2Win())
                holder.tvResult.setText(R.string.history_victory);
            else
                holder.tvResult.setText(R.string.history_defeat);
        }

        if (!model.getPlayer1Win() && !model.getPlayer2Win())
            holder.tvResult.setText(R.string.history_draw);

    }

    /**
     * Create the view holder and assign it to the layout item
     * @param parent
     * @param viewType
     * @return the view holder
     */
    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryHolder(itemView);
    }

    /**
     * Create the viewholder of the recyclerView and create the components.
     */
    public class HistoryHolder extends RecyclerView.ViewHolder {

        TextView tvHistory, tvHistoryGame, tvDate, tvResult;

        public HistoryHolder(@NonNull View itemView) {
            super(itemView);
            tvHistory = itemView.findViewById(R.id.tvHistory);
            tvHistoryGame = itemView.findViewById(R.id.tvHistoryGame);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvResult = itemView.findViewById(R.id.tvResult);
        }
    }
}
