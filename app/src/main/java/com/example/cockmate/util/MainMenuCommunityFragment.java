package com.example.cockmate.util;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.cockmate.R;

public class MainMenuCommunityFragment extends Fragment {

    Button allBtn, flavorBtn, baseBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_menu_community, container, false);

        allBtn = v.findViewById(R.id.all_button);
        flavorBtn = v.findViewById(R.id.flavor_button);
        baseBtn = v.findViewById(R.id.base_button);

        allBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BoardActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }

}