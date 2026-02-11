package com.jorotayo.algorubickrevamped.ui.home;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;
import com.jorotayo.algorubickrevamped.R;

public class Activity_StudyAlgorithm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge FIRST
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Set navigation bar color BEFORE setContentView
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));

        setContentView(R.layout.activity_algorithm_study_new);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (view, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            view.setPadding(0, topInset, 0, 0);
            return WindowInsetsCompat.CONSUMED;
        });

        String str = "practice";
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(str)) {
            loadPracticeAlgorithmView(str);
        } else {
            loadLearnAlgorithmView();
        }
    }

    private void loadPracticeAlgorithmView(String str) {
        Fragment newFragment = Fragment_PracticeAlgorithm.newInstance(
                getIntent().getIntegerArrayListExtra(str)
        );
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setReorderingAllowed(true);
        transaction.replace(R.id.study_fragment_space, newFragment);
        transaction.commit();
    }

    private void loadLearnAlgorithmView() {
        Fragment newFragment = Fragment_LearnAlgorithm.newInstance(
                getIntent().getIntegerArrayListExtra("learn")
        );
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setReorderingAllowed(true);
        transaction.replace(R.id.study_fragment_space, newFragment);
        transaction.commit();
    }

    public void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    public void setToolbarSubTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(title);
        }
    }
}