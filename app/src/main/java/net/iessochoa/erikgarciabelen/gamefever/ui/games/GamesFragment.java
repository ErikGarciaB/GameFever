package net.iessochoa.erikgarciabelen.gamefever.ui.games;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import net.iessochoa.erikgarciabelen.gamefever.R;

public class GamesFragment extends Fragment {

    private GamesViewModel gamesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        gamesViewModel = new ViewModelProvider(this).get(GamesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_games, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        gamesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}