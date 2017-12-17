package com.varcustom.namegame.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.varcustom.namegame.model.GameData;
import com.varcustom.namegame.model.Person;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by hdarby on 12/11/2017.
 */

public class GameInstanceProvider implements Parcelable {

    public static final Creator<GameInstanceProvider> CREATOR = new Creator<GameInstanceProvider>() {
        @Override
        public GameInstanceProvider createFromParcel(Parcel in) {
            return new GameInstanceProvider(in);
        }

        @Override
        public GameInstanceProvider[] newArray(int size) {
            return new GameInstanceProvider[size];
        }
    };
    GameData mGameData;
    ArrayList<Person> mPeople;
    PersonListProvider mPersonListProvider;

    public GameInstanceProvider(@NonNull final GameData gameData, @NonNull final PersonListProvider personListProvider) {

        Log.d(getClass().getSimpleName(), "GameMode = " + gameData.getGameMode());
        mGameData = gameData;
        mPersonListProvider = personListProvider;
    }

    protected GameInstanceProvider(Parcel in) {
        mGameData = in.readParcelable(GameData.class.getClassLoader());
        mPeople = in.createTypedArrayList(Person.CREATOR);
        mPersonListProvider = in.readParcelable(PersonListProvider.class.getClassLoader());
    }

    public GameData getGameData() {
        return mGameData;
    }

    public void init() {
        // Matt mode, filter by people named Matt or Matthew
        if (getGameData().getGameMode() == GameData.GameMode.MATT_GAME) {
            ArrayList<Person> mattList = mPersonListProvider.getPeople().stream()
                    .filter(person -> person.getFirstName().contains("Matt"))
                    .collect(Collectors.toCollection(ArrayList::new));

            mPeople = mPersonListProvider.getRandomMatts(mattList, mGameData.getNumPeople());
        } else {
            mPeople = mPersonListProvider.getRandomPeople(mGameData.getNumPeople());
        }
    }

    public ArrayList<Person> getPeople() {
        return mPeople;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mGameData, 0);
        Person[] people = new Person[mPeople.size()];
        people = mPeople.toArray(people);
        dest.writeParcelableArray(people, 0);
        dest.writeParcelable(mPersonListProvider, 0);

    }
}
