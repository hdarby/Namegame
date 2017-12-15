package com.varcustom.namegame.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.varcustom.namegame.model.Person;
import com.varcustom.namegame.network.NameGameApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonListRepository {

    @NonNull
    private final NameGameApi api;
    @NonNull
    private List<Listener> listeners = new ArrayList<>(1);
    @Nullable
    private List<Person> mPeople;

    public PersonListRepository(@NonNull NameGameApi api, Listener... listeners) {
        this.api = api;
        if (listeners != null) {
            this.listeners = new ArrayList<>(Arrays.asList(listeners));
        }
        load();
    }

    private void load() {
        this.api.getPersonProfiles().enqueue(new Callback<List<Person>>() {
            @Override
            public void onResponse(Call<List<Person>> call, Response<List<Person>> response) {
                mPeople = response.body();
                for (Listener listener : listeners) {
                    listener.onLoadFinished(mPeople);
                }
            }

            @Override
            public void onFailure(Call<List<Person>> call, Throwable t) {
                for (Listener listener : listeners) {
                    listener.onError(t);
                }
            }
        });
    }

    public void register(@NonNull Listener listener) {
        if (listeners.contains(listener))
            throw new IllegalStateException("Listener is already registered.");
        listeners.add(listener);
        if (mPeople != null) {
            listener.onLoadFinished(mPeople);
        }
    }

    public void unregister(@NonNull Listener listener) {
        listeners.remove(listener);
    }

    public interface Listener {
        void onLoadFinished(@NonNull List<Person> people);

        void onError(@NonNull Throwable error);
    }

}
