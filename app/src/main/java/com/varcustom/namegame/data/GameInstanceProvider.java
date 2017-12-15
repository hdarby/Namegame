package com.varcustom.namegame.data;

import android.support.annotation.NonNull;
import android.util.Log;

import com.varcustom.namegame.model.GameData;
import com.varcustom.namegame.model.Person;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hdarby on 12/11/2017.
 */

public class GameInstanceProvider {

    GameData mGameData;
    List<Person> mPeople;
    PersonListProvider mPersonListProvider;

    public GameInstanceProvider(@NonNull final GameData gameData, @NonNull final PersonListProvider personListProvider) {

        Log.d(getClass().getSimpleName(), "GameMode = " + gameData.getGameMode());
        mGameData = gameData;
        mPersonListProvider = personListProvider;
    }

    public GameData getGameData() {
        return mGameData;
    }

    public void init() {
        // Matt mode, filter by people named Matt or Matthew
        if (getGameData().getGameMode() == GameData.GameMode.MATT_GAME) {
            List<Person> mattList = mPersonListProvider.getPeople().stream()
                    .filter(person -> person.getFirstName().contains("Matt"))
                    .collect(Collectors.toList());

            mPeople = mPersonListProvider.getRandomMatts(mattList, mGameData.getNumPeople());
        } else {
            mPeople = mPersonListProvider.getRandomPeople(mGameData.getNumPeople());
        }
    }

    public List<Person> getPeople() {
        return mPeople;
    }
}
