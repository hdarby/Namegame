package com.varcustom.namegame.data;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
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

public class PersonListProvider implements Parcelable {

    private Context mContext;

    private PersonListRepository mPersonListRepository;
    private PersonListRepository.Listener mListener;

    private ArrayList<Person> mPeople;
    private boolean mLoaded;

    public PersonListProvider(Context context) {
        mContext = context;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://willowtreeapps.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mListener = new PersonListRepository.Listener() {
            @Override
            public void onLoadFinished(@NonNull ArrayList<Person> people) {
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

    protected PersonListProvider(Parcel in) {
        mPeople = in.createTypedArrayList(Person.CREATOR);
        mLoaded = in.readByte() != 0;
    }

    public static final Creator<PersonListProvider> CREATOR = new Creator<PersonListProvider>() {
        @Override
        public PersonListProvider createFromParcel(Parcel in) {
            return new PersonListProvider(in);
        }

        @Override
        public PersonListProvider[] newArray(int size) {
            return new PersonListProvider[size];
        }
    };

    public PersonListRepository getPersonListRepository() {
        return mPersonListRepository;
    }

    public ArrayList<Person> getPeople() {
        return mPeople;
    }

    public boolean isLoaded() {
        return mLoaded;
    }

    public ArrayList<Person> getRandomPeople(int numberOfPeople) {
        ArrayList<Person> people = new ArrayList<>();

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

    public ArrayList<Person> getRandomMatts(ArrayList<Person> matts, int numberOfPeople) {
        ArrayList<Person> people = new ArrayList<>();

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Person[] people = new Person[mPeople.size()];
        people = mPeople.toArray(people);
        dest.writeParcelableArray(people, 0);
        dest.writeInt(mLoaded ? 1 : 0);
    }
}
