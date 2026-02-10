package com.jorotayo.algorubickrevamped.ui.home;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.jorotayo.algorubickrevamped.MainActivity;
import com.jorotayo.algorubickrevamped.ObjectBox;
import com.jorotayo.algorubickrevamped.OnBackPressed;
import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Algorithm;
import com.jorotayo.algorubickrevamped.utils.UtilMethods;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.objectbox.Box;

public class AlgorithmHomeFragment extends Fragment implements OnBackPressed {

    public static ActionMode actionMode;

    private RecyclerView algorithmRecycler;
    private AlgorithmRecyclerAdapter algorithmRecyclerAdapter;
    private Box<Algorithm> algorithmBox;

    private Spinner algorithmsFilterSpinner;
    private TextView homeAlgsNumber;

    private final ArrayList<Integer> selectedList = new ArrayList<>();
    private ArrayList<Algorithm> algorithmArrayList = new ArrayList<>();

    private View root;

    // ─────────────────────────────────────────────────────────────
    // ActionMode (contextual selection)
    // ─────────────────────────────────────────────────────────────

    private final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            ((AppCompatActivity) requireActivity())
                    .getSupportActionBar()
                    .hide();
            mode.getMenuInflater().inflate(R.menu.contextual_menu, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            mode.setTitle(
                    algorithmRecyclerAdapter.getSelectedItemCount() + " selected"
            );
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            Intent intent = new Intent(getContext(), Activity_StudyAlgorithm.class);
            selectedList.clear();
            selectedList.addAll(algorithmRecyclerAdapter.getSelectedItems());

            if (item.getItemId() == R.id.contextual_learn) {
                Toast.makeText(getContext(), "Learn multiple", Toast.LENGTH_SHORT).show();
                intent.putExtra("learn", selectedList);
            } else if (item.getItemId() == R.id.contextual_practice) {
                Toast.makeText(getContext(), "Practice multiple", Toast.LENGTH_SHORT).show();
                intent.putExtra("practice", selectedList);
            } else {
                return false;
            }

            startActivity(intent);
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            ((AppCompatActivity) requireActivity())
                    .getSupportActionBar()
                    .show();
            actionMode = null;
            algorithmRecyclerAdapter.clearSelection();
        }
    };

    // ─────────────────────────────────────────────────────────────
    // Fragment lifecycle
    // ─────────────────────────────────────────────────────────────

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);

        homeAlgsNumber = root.findViewById(R.id.home_total_number);
        algorithmRecycler = root.findViewById(R.id.algorithm_recycler);
        algorithmsFilterSpinner = root.findViewById(R.id.algorithms_filter_spinner);

        root.findViewById(R.id.create_new_algorithm_btn)
                .setOnClickListener(v ->
                        startActivity(new Intent(getContext(), Activity_Algorithm.class))
                );

        algorithmRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        algorithmRecycler.setHasFixedSize(true);

        algorithmBox = ObjectBox.getBoxStore().boxFor(Algorithm.class);

        if (algorithmBox.isEmpty()) {
            for (Algorithm alg : loadDefaultAlgorithmsFromJson()) {
                algorithmBox.put(alg);
            }
        }

        algorithmArrayList = new ArrayList<>(algorithmBox.getAll());
        homeAlgsNumber.setText(String.valueOf(algorithmArrayList.size()));

        setupAdapter();
        setupSpinner();

        return root;
    }

    // ─────────────────────────────────────────────────────────────
    // Adapter setup
    // ─────────────────────────────────────────────────────────────

    private void setupAdapter() {

        algorithmRecyclerAdapter = new AlgorithmRecyclerAdapter(
                algorithmArrayList,
                getContext(),

                // Card click
                this::onAlgorithmClick,

                // Image click
                this::onAlgorithmImageClick,

                // Favourite click
                (position, view) -> onFavouriteAlgorithmClick(position),

                // Learnt click
                (position, view) -> onLearntAlgorithmClick(position),

                // Long click (START ACTION MODE)
                (position, view) -> onLongClickAlgorithm()
        );

        algorithmRecycler.setAdapter(algorithmRecyclerAdapter);
    }

    private void onAlgorithmClick(int position) {

        if (actionMode != null) {
            algorithmRecyclerAdapter.toggleSelection(position);

            actionMode.setTitle(
                    algorithmRecyclerAdapter.getSelectedItemCount() + " selected"
            );

            if (algorithmRecyclerAdapter.getSelectedItemCount() == 0) {
                actionMode.finish();
            }

            return;
        }

        // Normal click (no ActionMode)
        Intent intent = new Intent(getContext(), Activity_Algorithm.class);
        intent.putExtra("algorithm_id", algorithmArrayList.get(position).id);
        startActivity(intent);
    }


    private void onLongClickAlgorithm() {
        if (actionMode == null) {

            actionMode = ((MainActivity) requireActivity())
                    .startSupportActionMode(actionModeCallback);
        }
        actionMode.setTitle(
                algorithmRecyclerAdapter.getSelectedItemCount() + " selected"
        );

        if(actionMode != null && algorithmRecyclerAdapter.getSelectedItemCount() == 0){
            actionMode.finish();
        }
    }

    private void onLearntAlgorithmClick(int position) {
        Toast.makeText(
            getContext(),
            algorithmArrayList.get(position).alg_name + " Learnt",
            Toast.LENGTH_SHORT
    ).show();
        Algorithm alg = algorithmArrayList.get(position);
        alg.learnt = !alg.learnt;
        algorithmBox.put(alg);
    }

    private void onFavouriteAlgorithmClick(int position) {
        if (actionMode != null) actionMode.finish();
        Toast.makeText(
                getContext(),
                algorithmArrayList.get(position).alg_name + " Favourited",
                Toast.LENGTH_SHORT
        ).show();
        Algorithm alg = algorithmArrayList.get(position);
        alg.favourite_alg = !alg.favourite_alg;
        algorithmBox.put(alg);
    }

    public void onAlgorithmImageClick(int position, View v) {
        ActionMode actionMode = AlgorithmHomeFragment.actionMode;
        if (actionMode != null) {
            actionMode.finish();
        }

        Algorithm currentAlg = algorithmArrayList.get(position);
        Toast.makeText(
                getContext(),
                String.format("%s - Image Clicked", currentAlg.alg_name),
                Toast.LENGTH_SHORT
        ).show();

        showAlgorithmImageDialog(currentAlg);

        algorithmRecyclerAdapter.clearSelection();
        algorithmRecyclerAdapter.notifyDataSetChanged();
    }

    private void showAlgorithmImageDialog(Algorithm algorithm) {
        Dialog caseDialog = new Dialog(requireContext());
        caseDialog.setContentView(R.layout.algorithm_image_dialog);

        ImageView dialog_alg_image_icon = caseDialog.findViewById(R.id.dialog_alg_image_icon);
        TextView dialog_alg_image_name = caseDialog.findViewById(R.id.dialog_alg_image_name);
        TextView dialog_alg_image_alg = caseDialog.findViewById(R.id.dialog_alg_image_alg);

        UtilMethods.LoadAlgorithmIcon(getContext(), dialog_alg_image_icon, algorithm);
        dialog_alg_image_name.setText(algorithm.alg_name);
        dialog_alg_image_alg.setText(algorithm.alg);

        caseDialog.show();
    }
    // ─────────────────────────────────────────────────────────────
    // Spinner (sorting)
    // ─────────────────────────────────────────────────────────────

    private void setupSpinner() {
        List<String> filters = List.of("Created Date", "Algorithm Name", "Category");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                filters
        );

        algorithmsFilterSpinner.setAdapter(adapter);

        algorithmsFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                algorithmArrayList = new ArrayList<>(algorithmBox.getAll());

                switch (filters.get(position)) {
                    case "Algorithm Name":
                        Collections.sort(algorithmArrayList, new Algorithm.CompareAlgorithmName());
                        break;
                    case "Category":
                        Collections.sort(algorithmArrayList, new Algorithm.CompareCategory());
                        break;
                    case "Created Date":
                        Collections.sort(algorithmArrayList, new Algorithm.CompareCreatedDate());
                        break;
                }

                algorithmRecyclerAdapter.setFilter(algorithmArrayList);
                homeAlgsNumber.setText(String.valueOf(algorithmArrayList.size()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // ─────────────────────────────────────────────────────────────
    // JSON loader
    // ─────────────────────────────────────────────────────────────

    private List<Algorithm> loadDefaultAlgorithmsFromJson() {
        List<Algorithm> list = new ArrayList<>();
        try {
            InputStream is = requireContext().getAssets().open("default_algs.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            JSONArray array = new JSONArray(new String(buffer, StandardCharsets.UTF_8));

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                int imageRes = getResources().getIdentifier(
                        obj.getString("image"),
                        "drawable",
                        requireContext().getPackageName()
                );

                list.add(new Algorithm(
                        obj.getString("name"),
                        obj.getString("alg"),
                        obj.getString("description"),
                        String.valueOf(imageRes),
                        obj.getString("category"),
                        0, 0, false, false, false, false, 0
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ─────────────────────────────────────────────────────────────
    // Back press
    // ─────────────────────────────────────────────────────────────

    @Override
    public void customBackPressed() {
        if (actionMode != null) {
            actionMode.finish();
        } else {
            requireActivity().finish();
        }
    }
}
