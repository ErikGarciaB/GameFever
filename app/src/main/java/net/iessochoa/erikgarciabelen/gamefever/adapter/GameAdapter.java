package net.iessochoa.erikgarciabelen.gamefever.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iessochoa.erikgarciabelen.gamefever.R;
import net.iessochoa.erikgarciabelen.gamefever.ui.games.GamesFragment;

import java.util.ArrayList;


public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private ArrayList<String> games;

    private OnItemClickExpandListener listenerExpand;
    private OnItemClickInviteListener listenerInvite;
    private OnItemClickPlayListener listenerPlay;

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_games, parent, false);
        return new GameViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        if (games != null){
            if(games.get(position).equals(GamesFragment.TIC_TAC_TOE)){
                holder.tvGame.setText(R.string.tic_tac_toe);
                holder.ivGame.setImageResource(R.drawable.ic_tictactoe);
                holder.tvGameID.setText(GamesFragment.TIC_TAC_TOE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (games != null){
            return games.size();
        }
        else return 0;
    }

    public void setGames(ArrayList<String> games){
        this.games = games;
        notifyDataSetChanged();
    }

    public class GameViewHolder extends RecyclerView.ViewHolder{

        private TextView tvGame, tvGameID;
        private ImageView ivGame, ivGameExpand;
        private LinearLayout llGame;
        private LinearLayout llOptions;
        private Button btPlay, btInvite;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGame = itemView.findViewById(R.id.tvGame);
            tvGameID = itemView.findViewById(R.id.tvGameID);
            ivGame = itemView.findViewById(R.id.ivGame);
            ivGameExpand = itemView.findViewById(R.id.ivGameExpand);
            llGame = itemView.findViewById(R.id.llGame);
            llOptions = itemView.findViewById(R.id.llOptions);
            btPlay = itemView.findViewById(R.id.btPlay);
            btInvite = itemView.findViewById(R.id.btPlayInvite);

            ivGameExpand.setOnClickListener(v -> {
                if (listenerExpand != null){
                    listenerExpand.onItemExpandClick(llGame, llOptions, ivGameExpand);
                }
            });

            btPlay.setOnClickListener(v -> {
                if (listenerPlay != null){
                    listenerPlay.onItemPlayClick(tvGameID);
                }
            });

            btInvite.setOnClickListener(v -> {
                if (listenerInvite != null){
                    listenerInvite.onItemInviteClick(tvGameID);
                }
            });
        }
    }

    public interface OnItemClickExpandListener {
        void onItemExpandClick(LinearLayout llGame, LinearLayout llOptions, ImageView ivGameExpand);
    }

    public interface OnItemClickPlayListener {
        void onItemPlayClick(TextView tvGameID);
    }

    public interface OnItemClickInviteListener{
        void onItemInviteClick(TextView tvGameID);
    }

    public void setOnClickListenerExpand(OnItemClickExpandListener listenerExpand) {
        this.listenerExpand = listenerExpand;
    }

    public void setOnClickListenerInvite(OnItemClickInviteListener listenerInvite) {
        this.listenerInvite = listenerInvite;
    }

    public void setOnClickListenerPlay(OnItemClickPlayListener listenerPlay) {
        this.listenerPlay = listenerPlay;
    }
}
