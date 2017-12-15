package com.varcustom.namegame.model;

/**
 * Created by hdarby on 12/12/2017.
 */

public class GameData {

    public static final String MATCH_GAME = "Match Game";
    public static final String MATT_GAME = "Matt Game";
    public static final String SECOND_CHANCE_GAME = "Second Chance";
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

    public enum GameMode {
        NAME_GAME,
        MATT_GAME,
        SECOND_CHANCE_GAME
    }
}
