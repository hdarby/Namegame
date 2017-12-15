package com.varcustom.namegame.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.varcustom.namegame.R;
import com.varcustom.namegame.model.GameData;

public class GameModeChooserFragment extends Fragment {

    private TextView mMatchGame;
    private TextView mMattGame;
    private TextView mSecondChanceGame;

    private GameModeChooserListener mGameModeChooserListener;

    public static GameModeChooserFragment newInstance() {
        GameModeChooserFragment fragment = new GameModeChooserFragment();

        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mGameModeChooserListener = (GameModeChooserListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement GameModeChooserListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_game_mode_chooser, container, false);

        mMatchGame = view.findViewById(R.id.match);
        mMatchGame.setOnClickListener((View lambdaView) ->
                mGameModeChooserListener.setGameMode(GameData.GameMode.NAME_GAME));

        mMattGame = view.findViewById(R.id.matt);
        mMattGame.setOnClickListener((View lambdaView) ->
                mGameModeChooserListener.setGameMode(GameData.GameMode.MATT_GAME));

        mSecondChanceGame = view.findViewById(R.id.second_chance);
        mSecondChanceGame.setOnClickListener((View lambdaView) ->
                mGameModeChooserListener.setGameMode(GameData.GameMode.SECOND_CHANCE_GAME));

        return view;
    }

    interface GameModeChooserListener {
        void setGameMode(GameData.GameMode mode);
    }

}
