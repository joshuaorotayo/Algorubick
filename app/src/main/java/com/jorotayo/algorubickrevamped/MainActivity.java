package com.jorotayo.algorubickrevamped;

import static com.jorotayo.algorubickrevamped.ui.home.AlgorithmHomeFragment.actionMode;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jorotayo.algorubickrevamped.data.Category;
import com.jorotayo.algorubickrevamped.ui.home.AlgorithmHomeFragment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.objectbox.Box;

public class MainActivity extends AppCompatActivity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public void onCreate(Bundle savedInstanceState) {

        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIconTint(Color.WHITE);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbar), (view, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            view.setPadding(0, topInset, 0, 0);
            return WindowInsetsCompat.CONSUMED;
        });

        BottomNavigationView navView = findViewById(R.id.nav_view);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Check ActionMode first
                if (AlgorithmHomeFragment.actionMode != null) {
                    AlgorithmHomeFragment.actionMode.finish();
                    return;
                }

                // Get current fragment
                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);

                if (navHostFragment != null) {
                    Fragment currentFragment = navHostFragment.getChildFragmentManager()
                            .getPrimaryNavigationFragment();

                    // Check if we're on the home fragment
                    if (currentFragment instanceof AlgorithmHomeFragment) {
                        // On home fragment - close the app
                        finish();
                    } else {
                        // Not on home - navigate back to home
                        NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
                        navController.navigate(R.id.navigation_home);
                    }
                } else {
                    finish();
                }
            }
        });

//        setSupportActionBar(toolbar);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_timer, R.id.navigation_solution_guide, R.id.navigation_notation).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        ObjectBox.init(this);

        SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);

        // Initialize categories in background
        if (!settings.getBoolean("FIRST_RUN", false)) {
            executor.execute(() -> {
                Box<Category> categoryBox = ObjectBox.getBoxStore().boxFor(Category.class);
                categoryBox.put(new Category("Default"));
                categoryBox.put(new Category("Cross"));
                categoryBox.put(new Category("EOLL"));
                categoryBox.put(new Category("F2L"));
                categoryBox.put(new Category("OLL"));
                categoryBox.put(new Category("PLL"));
                categoryBox.put(new Category("Triggers"));

                // Update SharedPreferences on main thread
                runOnUiThread(() -> {
                    settings.edit().putBoolean("FIRST_RUN", true).apply();
                });
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);
        return true;
    }
}