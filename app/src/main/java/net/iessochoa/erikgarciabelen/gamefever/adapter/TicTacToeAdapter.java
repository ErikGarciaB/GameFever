package net.iessochoa.erikgarciabelen.gamefever.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import net.iessochoa.erikgarciabelen.gamefever.R;
import net.iessochoa.erikgarciabelen.gamefever.model.FirebaseContract;
import net.iessochoa.erikgarciabelen.gamefever.model.History;
import net.iessochoa.erikgarciabelen.gamefever.model.TicTacToe;
import net.iessochoa.erikgarciabelen.gamefever.model.User;
import net.iessochoa.erikgarciabelen.gamefever.ui.games.TicTacToeActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TicTacToeAdapter extends FirestoreRecyclerAdapter<TicTacToe, TicTacToeAdapter.TicTacToeHolder> {

    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public TicTacToeAdapter(@NonNull FirestoreRecyclerOptions<TicTacToe> options, Context context) {
        super(options);
        this.context = context;
    }

    /**
     * Bind the information of the invitation to layout's component and create the component's behaviour
     * @param holder
     * @param position
     */
    @Override
    protected void onBindViewHolder(@NonNull TicTacToeHolder holder, int position, @NonNull TicTacToe model) {
        fillMap(holder, model);
        fillListeners(holder, model);
    }

    /**
     * Create the listeners of the layout's components
     * @param holder
     * @param model
     */
    private void fillListeners(TicTacToeHolder holder, TicTacToe model) {
        holder.ib0.setOnClickListener(v -> doPlay(0, model));
        holder.ib1.setOnClickListener(v -> doPlay(1, model));
        holder.ib2.setOnClickListener(v -> doPlay(2, model));
        holder.ib3.setOnClickListener(v -> doPlay(3, model));
        holder.ib4.setOnClickListener(v -> doPlay(4, model));
        holder.ib5.setOnClickListener(v -> doPlay(5, model));
        holder.ib6.setOnClickListener(v -> doPlay(6, model));
        holder.ib7.setOnClickListener(v -> doPlay(7, model));
        holder.ib8.setOnClickListener(v -> doPlay(8, model));
    }


    /**
     * Make the play of the player and detect if he can do it.
     * @param position Is the position of the table that play
     * @param model Is the TicTacToe Table
     */
    private void doPlay(int position, TicTacToe model) {
        if (TicTacToeActivity.YOUR_TURN && !TicTacToeActivity.MATCH_ENDED) {
            if (model.getMap().get(position) != 0)
                Toast.makeText(context, R.string.square_filled, Toast.LENGTH_SHORT).show();
            else {
                makePlay(position, model);
            }
        } else if (!TicTacToeActivity.YOUR_TURN && !TicTacToeActivity.MATCH_ENDED){
            Toast.makeText(context, R.string.not_your_turn, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Change the turns of the player in the database and check if the game is over.
     * @param position Is the position of the table played
     * @param model Is the TicTacToe Table
     */
    private void makePlay(int position, TicTacToe model) {
        Map<String, Object> fieldToUpdate = new HashMap<>();
        ArrayList<Integer> map = model.getMap();
        map.set(position, TicTacToeActivity.PLAYERNUMBER);
        model.setMap(map);
        fieldToUpdate.put(FirebaseContract.TicTacToeGameEntry.MAP, map);
        if (model.getPlayer1Turn()) {
            fieldToUpdate.put(FirebaseContract.TicTacToeGameEntry.PLAYER1TURN, false);
            fieldToUpdate.put(FirebaseContract.TicTacToeGameEntry.PLAYER2TURN, true);
        } else {
            fieldToUpdate.put(FirebaseContract.TicTacToeGameEntry.PLAYER1TURN, true);
            fieldToUpdate.put(FirebaseContract.TicTacToeGameEntry.PLAYER2TURN, false);
        }
        fieldToUpdate.put(FirebaseContract.TicTacToeGameEntry.MOVE_COUNTER, model.getMoveCounter() + 1);
        model.getMap().set(position, TicTacToeActivity.PLAYERNUMBER);
        db.collection(FirebaseContract.TicTacToeGameEntry.COLLECTION_NAME)
                .document(model.getId()).update(fieldToUpdate).addOnCompleteListener(task -> {
            checkGame(model, position);
        });
    }

    /**
     * Check if the game is done.
     * @param model  Is the TicTacToe Table
     * @param position Is the last position of the table played
     */
    private void checkGame(TicTacToe model, int position){
        ArrayList<Integer> map = model.getMap();
        boolean draw = true;

        int x, y, playerNumber;

        playerNumber = TicTacToeActivity.PLAYERNUMBER;
        y = position / 3;
        x = position - (y * 3);


        int[][] board = {{map.get(0), map.get(1), map.get(2)}, {map.get(3), map.get(4), map.get(5)}, {map.get(6), map.get(7), map.get(8)}};

        for (int i = 0; i < 3; i++) {
            if (board[i][x] != playerNumber)
                break;
            if (i == 2){
                draw = false;
                matchWinned(playerNumber, model);
            }
        }

        for (int i = 0; i < 3; i++) {
            if (board[y][i] != playerNumber)
                break;
            if (i == 2) {
                draw = false;
                matchWinned(playerNumber, model);
            }
        }

        if (x == y){
            for (int i = 0; i < 3; i++) {
                if (board[i][i] != playerNumber)
                    break;
                if (i == 2) {
                    draw = false;
                    matchWinned(playerNumber, model);
                }
            }
        }

        if (x + y == 2){
            for(int i = 0; i < 3; i++){
                if (board[i][(2) - i] != playerNumber)
                    break;
                if (i == 2) {
                    draw = false;
                    matchWinned(playerNumber, model);
                }
            }
        }

        if (draw){
            if (model.getMoveCounter() + 1 == 9)
                matchDrawed(model);
        }


    }

    /**
     * Delete the drawed game from the database and create the history of the two players
     * @param model is the tic tac toe Game
     */
    private void matchDrawed(TicTacToe model){
        User player1, player2;
        player1 = model.getPlayer1();
        player2 = model.getPlayer2();
        TicTacToeActivity.MATCH_DRAWED = true;

        History h = new History(player1.getName(), player2.getName(), context.getString(R.string.tic_tac_toe), false, false);

        db.collection(FirebaseContract.UserEntry.COLLECTION_NAME)
                .document(player1.getName())
                .collection(FirebaseContract.HistoryEntry.COLLECTION_NAME)
                .add(h);

        db.collection(FirebaseContract.UserEntry.COLLECTION_NAME)
                .document(player2.getName())
                .collection(FirebaseContract.HistoryEntry.COLLECTION_NAME)
                .add(h);

        db.collection(FirebaseContract.TicTacToeGameEntry.COLLECTION_NAME)
                .document(model.getId()).collection(FirebaseContract.ChatEntry.COLLECTION_NAME).get()
                .addOnCompleteListener(task -> {
                    for (DocumentSnapshot d : task.getResult()){
                        db.collection(FirebaseContract.TicTacToeGameEntry.COLLECTION_NAME).document(model.getId())
                                .collection(FirebaseContract.ChatEntry.COLLECTION_NAME).document(d.getId()).delete();
                    }
                });

        db.collection(FirebaseContract.TicTacToeGameEntry.COLLECTION_NAME)
                .document(model.getId()).delete();
    }

    /**
     * Delete the winned game from the database and create the history of the two players
     * @param model is the tic tac toe Game
     */
    private void matchWinned(int playerNumber, TicTacToe model){

        User player1, player2;
        player1 = model.getPlayer1();
        player2 = model.getPlayer2();

        if (playerNumber == 1)
            model.setPlayer1Win(true);
        else
            model.setPlayer2Win(true);



        History h = new History(player1.getName(), player2.getName(), context.getString(R.string.tic_tac_toe), model.getPlayer1Win(), model.getPlayer2Win());

        db.collection(FirebaseContract.UserEntry.COLLECTION_NAME)
                .document(player1.getName())
                .collection(FirebaseContract.HistoryEntry.COLLECTION_NAME)
                .add(h);



        db.collection(FirebaseContract.UserEntry.COLLECTION_NAME)
                .document(player2.getName())
                .collection(FirebaseContract.HistoryEntry.COLLECTION_NAME)
                .add(h);

        db.collection(FirebaseContract.TicTacToeGameEntry.COLLECTION_NAME)
             .document(model.getId()).delete();
    }


    /**
     * Populate the imagebuttons with a circle or a cross.
     * @param holder
     * @param model
     */
    private void fillMap(TicTacToeHolder holder, TicTacToe model) {
        putImage(holder.ib0, model.getMap().get(0));
        putImage(holder.ib1, model.getMap().get(1));
        putImage(holder.ib2, model.getMap().get(2));
        putImage(holder.ib3, model.getMap().get(3));
        putImage(holder.ib4, model.getMap().get(4));
        putImage(holder.ib5, model.getMap().get(5));
        putImage(holder.ib6, model.getMap().get(6));
        putImage(holder.ib7, model.getMap().get(7));
        putImage(holder.ib8, model.getMap().get(8));
    }

    /**
     * Put a cross or a circle in a imagebutton
     * @param ib
     * @param field
     */
    private void putImage(ImageButton ib, int field) {
        if (field == 1)
            ib.setImageResource(R.drawable.ic_circle);
        else if (field == 2)
            ib.setImageResource(R.drawable.ic_deny_friend);

    }


    /**
     * Create the view holder and assign it to the layout item
     * @param parent
     * @param viewType
     * @return the view holder
     */
    @NonNull
    @Override
    public TicTacToeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tictactoe, parent, false);
        return new TicTacToeHolder(itemView);
    }

    /**
     * Create the viewholder of the recyclerView and create the components.
     */
    public class TicTacToeHolder extends RecyclerView.ViewHolder {

        ImageButton ib0, ib1, ib2, ib3, ib4, ib5, ib6, ib7, ib8;

        public TicTacToeHolder(@NonNull View itemView) {
            super(itemView);
            ib0 = itemView.findViewById(R.id.ib0);
            ib1 = itemView.findViewById(R.id.ib1);
            ib2 = itemView.findViewById(R.id.ib2);
            ib3 = itemView.findViewById(R.id.ib3);
            ib4 = itemView.findViewById(R.id.ib4);
            ib5 = itemView.findViewById(R.id.ib5);
            ib6 = itemView.findViewById(R.id.ib6);
            ib7 = itemView.findViewById(R.id.ib7);
            ib8 = itemView.findViewById(R.id.ib8);
        }
    }
}
