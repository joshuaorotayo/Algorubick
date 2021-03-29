package com.jorotayo.algorubickrevamped.ui.home;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.jorotayo.algorubickrevamped.OnBackPressed;
import com.jorotayo.algorubickrevamped.R;

public class AlgorithmActivity extends AppCompatActivity {
    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_algorithm);
        if (getIntent().getExtras() != null) {
            setUpViewAlgPage();
        } else {
            setUpNewAlgPage();
        }
    }

    private void setUpViewAlgPage() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.algorithm_activity_container, ViewAlgorithmFragment.newInstance(getIntent().getLongExtra("algorithm_id", 0)));
        transaction.commit();
    }

    public void setUpNewAlgPage() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.algorithm_activity_container, NewAlgorithmFragment.newInstance());
        transaction.commit();
    }

    private void viewAlgorithm() {
        Toast.makeText(this, "View Algorithm", Toast.LENGTH_SHORT).show();
    }

    public void onBackPressed() {
        OnBackPressed fragment = (OnBackPressed) getSupportFragmentManager().findFragmentById(R.id.algorithm_activity_container);
        if (fragment != null) {
            fragment.customBackPressed();
        } else {
            super.onBackPressed();
        }
    }
}
