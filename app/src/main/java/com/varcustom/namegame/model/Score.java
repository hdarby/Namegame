package com.varcustom.namegame.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hdarby on 12/11/2017.
 */

public class Score implements Parcelable {

    public static final Creator<Score> CREATOR = new Creator<Score>() {
        @Override
        public Score createFromParcel(Parcel source) {
            return new Score(source);
        }

        @Override
        public Score[] newArray(int size) {
            return new Score[size];
        }
    };

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

    private Score(Parcel in) {
        mAttempts = in.readInt();
        mCorrect = in.readInt();

    }

    public int getAttempts() {
        return mAttempts;
    }

    public void setAttempts(int attempts) { mAttempts = attempts; }

    public int getCorrect() {
        return mCorrect;
    }

    public void setCorrect(int correct) { mCorrect = correct; }

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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mAttempts);
        dest.writeInt(this.mCorrect);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
