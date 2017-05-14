package com.bahaso.typecase;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bahaso.R;
import com.bahaso.fragmenthelper.FragmentBahaso;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmptyFragmentForTesting extends FragmentBahaso {


    public EmptyFragmentForTesting() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_empty_fragment_for_testing, container, false);
    }

}
