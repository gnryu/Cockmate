package com.example.cockmate.util;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.cockmate.R;

public class MainMenuBookmarkFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main_menu_bookmark, container, false);
//        return rootView;
        View v = inflater.inflate(R.layout.fragment_main_menu_bookmark, container, false);
        return v;
    }
}