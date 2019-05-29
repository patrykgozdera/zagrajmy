package com.example.patryk.zagrajmy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeFragment extends Fragment {

    private View inflatedView = null;
    private TextView homeTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflatedView = inflater.inflate(R.layout.fragment_home, container, false);
        homeTitle = (TextView) inflatedView.findViewById(R.id.textView2);

        if (LoginActivity.userName != null) {
            homeTitle.setText("Witaj, " + LoginActivity.userName + "!");
        } else {
            homeTitle.setText("Witaj!");
        }

        return inflatedView;
    }
}
