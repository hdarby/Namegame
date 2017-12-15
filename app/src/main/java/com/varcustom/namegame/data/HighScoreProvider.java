package com.varcustom.namegame.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.varcustom.namegame.model.Score;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by hdarby on 12/11/2017.
 */

public class HighScoreProvider {

    private static final String NAMEGAME = "namegame";
    private static final String ATTEMPTS = "attempts";
    private static final String CORRECT = "correct";

    private Score mHighScore;
    private Context mContext;

    public HighScoreProvider(Context context) {
        mContext = context;
        mHighScore = new Score();
        retrieveHighScore();
    }

    private void retrieveHighScore() {
        SharedPreferences prefs = mContext.getSharedPreferences(NAMEGAME, MODE_PRIVATE);

        mHighScore.setAttempts(prefs.getInt(ATTEMPTS, 0));
        mHighScore.setCorrect(prefs.getInt(CORRECT, 0));
    }

    public void clearHighScore() {
        setHighScore(new Score());
    }

    public boolean isNewHighScore(Score score) {

        if (mHighScore.getAttempts() == 0) {
            // If we have no high score, any score is a new high score
            return true;
        }

        if (score.getAttempts() == 0) {
            // No valid scores to check, just return false
            return false;
        }

        // Compare two valid scores

        float high = (float) (mHighScore.getCorrect() * 100 / mHighScore.getAttempts());
        float newScore = (float) (score.getCorrect() * 100 / score.getAttempts());
        return high < newScore;
    }

    public Score getHighScore() {
        return new Score(mHighScore.getAttempts(), mHighScore.getCorrect());
    }

    public void setHighScore(Score score) {

        // Locally and in SharedPrefs

        mHighScore.setAttempts(score.getAttempts());
        mHighScore.setCorrect(score.getCorrect());

        SharedPreferences.Editor editor = mContext.getSharedPreferences(NAMEGAME, MODE_PRIVATE).edit();

        editor.putInt(ATTEMPTS, score.getAttempts());
        editor.putInt(CORRECT, score.getCorrect());

        editor.apply();
    }

}
