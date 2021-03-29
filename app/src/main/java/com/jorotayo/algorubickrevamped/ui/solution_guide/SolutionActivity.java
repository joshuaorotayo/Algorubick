package com.jorotayo.algorubickrevamped.ui.solution_guide;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.jorotayo.algorubickrevamped.OnBackPressed;
import com.jorotayo.algorubickrevamped.R;

public class SolutionActivity extends AppCompatActivity {
    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution);
        FragmentTransaction transaction;
        if (getIntent().getExtras() != null) {
            ViewSolutionFragment newFragment = ViewSolutionFragment.newInstance();
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container_solution_guide, newFragment);
            transaction.commit();
            return;
        }
        NewSolutionFragment newFragment2 = NewSolutionFragment.newInstance();
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_solution_guide, newFragment2);
        transaction.commit();
    }

    public void onBackPressed() {
        OnBackPressed fragment = (OnBackPressed) getSupportFragmentManager().findFragmentById(R.id.fragment_container_solution_guide);
        if (fragment != null) {
            fragment.customBackPressed();
        } else {
            super.onBackPressed();
        }
    }
}
