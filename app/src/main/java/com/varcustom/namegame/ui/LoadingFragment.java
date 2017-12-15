package com.varcustom.namegame.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.varcustom.namegame.R;
import com.varcustom.namegame.model.GameData;

public class LoadingFragment extends Fragment {


    public static LoadingFragment newInstance() {
        LoadingFragment fragment = new LoadingFragment();

        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_loading, container, false);



        return view;
    }
}
