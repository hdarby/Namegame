package com.varcustom.namegame.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.varcustom.namegame.model.Score;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by hdarby on 12/11/2017.
 */

public class HighScoreProvider implements Parcelable {

    public static final Creator<HighScoreProvider> CREATOR = new Creator<HighScoreProvider>() {
        @Override
        public HighScoreProvider createFromParcel(Parcel in) {
            return new HighScoreProvider(in);
        }

        @Override
        public HighScoreProvider[] newArray(int size) {
            return new HighScoreProvider[size];
        }
    };
    private static final String NAMEGAME = "namegame";
    private static final String ATTEMPTS = "attempts";
    private static final String CORRECT = "correct";
    //private Context mContext;
    private Score mHighScore;

    public HighScoreProvider(Context context) {
        mHighScore = new Score(0, 0);
        retrieveHighScore(context);
    }

    protected HighScoreProvider(Parcel in) {
        mHighScore = in.readParcelable(Score.class.getClassLoader());
    }

    private void retrieveHighScore(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(NAMEGAME, MODE_PRIVATE);

        mHighScore.setAttempts(prefs.getInt(ATTEMPTS, 0));
        mHighScore.setCorrect(prefs.getInt(CORRECT, 0));
    }

    public void clearHighScore(Context context) {
        setHighScore(context, new Score());
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

    public void setHighScore(Context context, Score score) {

        // Locally and in SharedPrefs

        mHighScore.setAttempts(score.getAttempts());
        mHighScore.setCorrect(score.getCorrect());

        SharedPreferences.Editor editor = context.getSharedPreferences(NAMEGAME, MODE_PRIVATE).edit();

        editor.putInt(ATTEMPTS, score.getAttempts());
        editor.putInt(CORRECT, score.getCorrect());

        editor.apply();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeParcelable(mHighScore, 0);
    }
}
