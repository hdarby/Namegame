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
import com.varcustom.namegame.model.Score;

public class EndgameFragment extends Fragment {

    private TextView mScore;
    private TextView mHighScore;
    private TextView mClearHighScore;
    private TextView mPlayAgain;
    private TextView mChooseNewGameType;
    private TextView mAllDone;

    private EndgameListener mEndgameListener;

    public static EndgameFragment newInstance() {
        EndgameFragment fragment = new EndgameFragment();

        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mEndgameListener = (EndgameListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement EndgameListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_endgame, container, false);

        mScore = view.findViewById(R.id.your_score);
        mHighScore = view.findViewById(R.id.high_score);
        mClearHighScore = view.findViewById(R.id.clear_high_score);
        mClearHighScore.setOnClickListener((View lambdaView) ->
        {
            mEndgameListener.clearHighScore();
            setScores();
        });
        mPlayAgain = view.findViewById(R.id.play_again);
        mPlayAgain.setOnClickListener((View lambdaView) -> mEndgameListener.playAgain());
        mChooseNewGameType = view.findViewById(R.id.new_game_type);
        mChooseNewGameType.setOnClickListener((View lambdaView) -> mEndgameListener.chooseNewGameType());
        mAllDone = view.findViewById(R.id.all_done);
        mAllDone.setOnClickListener((View lambdaView) -> mEndgameListener.allDone());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        setScores();
    }

    private void setScores() {
        Score s = mEndgameListener.getScore();
        Score hs = mEndgameListener.getHighScore();
        String scoreString = "Score: " + s.getScoreAsPercentage() + "% (" + s.getScoreAsRatio() + ")";

        StringBuilder highScoreString = new StringBuilder("High Score: ");
        if (hs.getAttempts() == 0) {
            highScoreString.append("None");
        } else {
            highScoreString.append(hs.getScoreAsPercentage() + "% (" + hs.getScoreAsRatio() + ")");
        }

        mScore.setText(scoreString);
        mHighScore.setText(highScoreString);
    }

    public interface EndgameListener {
        Score getScore();
        Score getHighScore();
        void clearHighScore();
        void playAgain();
        void chooseNewGameType();
        void allDone();

    }
}
