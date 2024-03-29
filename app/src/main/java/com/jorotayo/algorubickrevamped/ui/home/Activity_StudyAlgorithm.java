package com.jorotayo.algorubickrevamped.ui.home;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.jorotayo.algorubickrevamped.R;

public class Activity_StudyAlgorithm extends AppCompatActivity {
    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_algorithm_study_new);
        String str = "practice";
        Fragment newFragment;
        FragmentTransaction transaction;
        if (getIntent().getExtras().containsKey(str)) {
            newFragment = Fragment_PracticeAlgorithm.newInstance(getIntent().getIntegerArrayListExtra(str));
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.study_fragment_space, newFragment);
            transaction.commit();
            return;
        }
        newFragment = Fragment_LearnAlgorithm.newInstance(getIntent().getIntegerArrayListExtra("learn"));
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.study_fragment_space, newFragment);
        transaction.commit();
    }
}
