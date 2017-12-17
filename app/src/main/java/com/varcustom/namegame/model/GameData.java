package com.varcustom.namegame.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hdarby on 12/12/2017.
 */

public class GameData implements Parcelable {

    public static final String MATCH_GAME = "Match Game";
    public static final String MATT_GAME = "Matt Game";
    public static final String SECOND_CHANCE_GAME = "Second Chance";

    public static final Creator CREATOR = new Creator() {
        public GameData createFromParcel(Parcel source) {
            return new GameData(source);
        }

        @Override
        public GameData[] newArray(int size) {
            return new GameData[size];
        }
    };

    private GameMode mGameMode;
    private int mNumPeople;
    private String mGameName;

    public GameData(GameMode gameMode) {
        mGameMode = gameMode;

        switch (mGameMode) {
            case NAME_GAME:
                mNumPeople = 6;
                mGameName = MATCH_GAME;
                break;

            case MATT_GAME:
                mNumPeople = 6;
                mGameName = MATT_GAME;
                break;

            case SECOND_CHANCE_GAME:
                mNumPeople = 6;
                mGameName = SECOND_CHANCE_GAME;

            default:
                mNumPeople = 6;
                mGameName = MATCH_GAME;
                break;
        }
    }

    public GameData(Parcel in) {
        int mode = in.readInt();
        mGameMode = GameMode.values()[mode];
        mGameName = in.readString();
        mNumPeople = in.readInt();
    }

    public GameMode getGameMode() {
        return mGameMode;
    }

    public void setGameMode(GameMode mode) {
        mGameMode = mode;
    }

    public int getNumPeople() {
        return mNumPeople;
    }

    public String getGameName() {
        return mGameName;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mGameMode.ordinal());
        dest.writeString(mGameName);
        dest.writeInt(mNumPeople);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public enum GameMode {
        NAME_GAME(1),
        MATT_GAME(2),
        SECOND_CHANCE_GAME(3);

        private int mMode;

        GameMode(int mode) {
            mMode = mode;
        }

    }
}
