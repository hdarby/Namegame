package com.varcustom.namegame.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.varcustom.namegame.model.Person;
import com.varcustom.namegame.network.NameGameApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hdarby on 12/7/2017.
 */

public class PersonListProvider {

    private Context mContext;

    private PersonListRepository mPersonListRepository;
    private PersonListRepository.Listener mListener;

    private List<Person> mPeople;
    private boolean mLoaded;

    public PersonListProvider(Context context) {
        mContext = context;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://willowtreeapps.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mListener = new PersonListRepository.Listener() {
            @Override
            public void onLoadFinished(@NonNull List<Person> people) {
                mPeople = people;
                mLoaded = true;
                Log.d(getClass().getSimpleName(), "Loaded " + people.size() + " people");
            }

            @Override
            public void onError(@NonNull Throwable error) {
                mLoaded = false;
            }
        };

        mPersonListRepository = new PersonListRepository(retrofit.create(NameGameApi.class), mListener);

    }

    public PersonListRepository getPersonListRepository() {
        return mPersonListRepository;
    }

    public List<Person> getPeople() {
        return mPeople;
    }

    public boolean isLoaded() {
        return mLoaded;
    }

    public List<Person> getRandomPeople(int numberOfPeople) {
        List<Person> people = new ArrayList<>();

        if (mPeople.size() <= numberOfPeople) {
            return mPeople;
        }

        while (people.size() < numberOfPeople) {
            Person p = mPeople.get(new Random().nextInt(mPeople.size()));
            if (!people.contains(p)) {
                people.add(p);
                Log.d(getClass().getSimpleName(), "Person = " + p.getName());
            }
        }

        return people;
    }

    public List<Person> getRandomMatts(List<Person> matts, int numberOfPeople) {
        List<Person> people = new ArrayList<>();

        if (matts.size() <= numberOfPeople) {
            return matts;
        }

        while (people.size() < numberOfPeople) {
            Person p = matts.get(new Random().nextInt(matts.size()));
            if (!people.contains(p)) {
                people.add(p);
                Log.d(getClass().getSimpleName(), "Person = " + p.getName());
            }
        }

        return people;
    }
}
