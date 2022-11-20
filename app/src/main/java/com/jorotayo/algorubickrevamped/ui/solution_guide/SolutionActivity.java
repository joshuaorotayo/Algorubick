package com.jorotayo.algorubickrevamped.ui.solution_guide;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.FragmentTransaction;

import com.jorotayo.algorubickrevamped.OnBackPressed;
import com.jorotayo.algorubickrevamped.R;

public class SolutionActivity extends AppCompatActivity implements MenuProvider {
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
        addMenuProvider(this);

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

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        // TODO: 18/11/2022 implement search for solutions
        MenuItem actionBarSearch = menu.findItem(R.id.actionbar_search);
        MenuItem actionBarStatistics = menu.findItem(R.id.actionbar_statistics);
        if (actionBarSearch != null){
            menu.findItem(R.id.actionbar_search).setVisible(false);
        }
        if( actionBarStatistics != null){
            menu.findItem(R.id.actionbar_statistics).setVisible(false);
        }
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
