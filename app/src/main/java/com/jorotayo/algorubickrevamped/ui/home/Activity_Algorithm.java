package com.jorotayo.algorubickrevamped.ui.home;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;
import com.jorotayo.algorubickrevamped.OnBackPressed;
import com.jorotayo.algorubickrevamped.R;

public class Activity_Algorithm extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_algorithm);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Apply status bar insets to toolbar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbar), (view, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            view.setPadding(0, topInset, 0, 0);
            return WindowInsetsCompat.CONSUMED;
        });

        if (getIntent().getExtras() != null) {
            setUpViewAlgPage();
        } else {
            setUpNewAlgPage();
        }
    }

    private void setUpViewAlgPage() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.algorithm_activity_container, Fragment_ViewAlgorithm.newInstance(getIntent().getLongExtra("algorithm_id", 0)));
        transaction.commit();
    }

    public void setUpNewAlgPage() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.algorithm_activity_container, Fragment_NewAlgorithm.newInstance());
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
