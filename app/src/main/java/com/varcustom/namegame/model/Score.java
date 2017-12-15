package com.varcustom.namegame.model;

/**
 * Created by hdarby on 12/11/2017.
 */

public class Score {

    private int mAttempts;
    private int mCorrect;

    public Score() {
        mAttempts = 0;
        mCorrect = 0;
    }

    public Score(int attempts, int correct) {
        mAttempts = attempts;
        mCorrect = correct;
    }

    public int getAttempts() {
        return mAttempts;
    }

    public void setAttempts(int attempts) {
        mAttempts = attempts;
    }

    public int getCorrect() {
        return mCorrect;
    }

    public void setCorrect(int correct) {
        mCorrect = correct;
    }

    public String getScoreAsPercentage() {
        if (mAttempts != 0) {
            return String.format("%d", mCorrect * 100 / mAttempts);
        }

        return "No score";
    }

    public String getScoreAsRatio() {
        return mCorrect + "/" + mAttempts;
    }

    public void correctAnswer() {
        mAttempts++;
        mCorrect++;
    }

    public void incorrectAnswer() {
        mAttempts++;
    }

    public void reset() {
        mAttempts = 0;
        mCorrect = 0;
    }
}
