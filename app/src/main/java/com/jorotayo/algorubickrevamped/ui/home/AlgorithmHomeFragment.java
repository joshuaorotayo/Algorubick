package com.jorotayo.algorubickrevamped.ui.home;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.view.ActionMode;
import androidx.appcompat.view.ActionMode.Callback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jorotayo.algorubickrevamped.MainActivity;
import com.jorotayo.algorubickrevamped.ObjectBox;
import com.jorotayo.algorubickrevamped.OnBackPressed;
import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Algorithm;
import com.jorotayo.algorubickrevamped.data.Algorithm_;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.objectbox.Box;
import io.objectbox.query.Query;

public class AlgorithmHomeFragment extends Fragment implements OnClickListener, AlgorithmRecyclerAdapter.OnAlgorithmListener, OnBackPressed {
    private final ArrayList<Integer> selectedList = new ArrayList<>();
    ArrayList<String> alg_names = new ArrayList<>(Arrays.asList("Right Sexy Move", "Left Sexy Move", "H-Perm", "Z-Perm", "T-Perm", "J1-Perm", "J2-Perm", "N1-Perm", "N2-Perm", "V-Perm", "Y-Perm", "E-perm", "F-Perm", "R1-Perm", "R2-Perm"));
    ArrayList<String> algs = new ArrayList<>(Arrays.asList("R,U,R',U'", "L',U',L,U", "M2,U,M2,U2,M2,U,M2", "M2,U,M2,U,M',U2,M2,U2,M',U2", "R,U,R',U',R,F,R2,U',R',U',R',U,R',F'", "R',U,L',U2,R,U',R',U2,R,L,U'", "R,U,R',F',R,U,R',U',R',F,R2,U',R',U'", "L,U',R,U2,L',U,R',L,U',R,U2,L',U,R',U", "R',U,L',U2,R,U',L,R',U,L',U2,R,U',L,U'", "R',U,R',d',R',F',R2,U',R',U,R',F,R,F", "F,R,U',R',U',R,U,R',F',R,U,R',U',R',F,R,F'", "X',R,U',R',D,R,U,R',u2,R',U,R,D,R',U',R", "R',U2,R',d',R',F',R2,U',R',U,R',F,R,U',F", "L,U2,L',U2,L,F',L',U',L,U,L,F,L2,U", "R',U2,R,U2,R',F,R,U,R',U',R',F',R2,U'"));
    ArrayList<String> categories = new ArrayList<>(Arrays.asList("Triggers", "Triggers", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL"));
    ArrayList<String> descriptions = new ArrayList<>(Arrays.asList("Insert bottom right corner and trigger", "Insert bottom left corner and trigger", "Swap opposite Edges (4e)", "Swap adjacent Edges (4e)", "Swap two opposite edges and two corners (2e2c)", "Swap two opposite corners, swap two adjacent edges (2c2e)", "Swap two corners, swap two adjacent edges (2e2c)", "Swap diagonal corner and opposite edge, left to right (2e2c)", "wap diagonal corner and opposite edge, Right to left (2e2c)", "Swap inline adjacent edge and diagonal corner (2e2c)", "Cross shaped swap diagonal corner and adjacent edge (2e2c)", "Swap opposite corners (4c)", "Swap inline 2 edges and 2 corners (2e2c)", "Swap opposite corners and adjacent edge (2e2c)", "Swap opposite corners and adjacent edge (2e2c)"));
    ArrayList<String> images = new ArrayList<>(Arrays.asList("R.drawable.right_edge", "R.drawable.left_edge", "R.drawable.h_perm", "R.drawable.z_perm", "R.drawable.t_perm", "R.drawable.j1_perm", "R.drawable.j2_perm", "R.drawable.n1_perm", "R.drawable.n2_perm", "R.drawable.v_perm", "R.drawable.y_perm", "R.drawable.e_perm", "R.drawable.f_perm", "R.drawable.r1_perm", "R.drawable.r2_perm"));
    public static ActionMode actionMode;
    private ArrayList<Algorithm> algorithmArrayList = new ArrayList<>();
    private Box<Algorithm> algorithmBox;
    private RecyclerView algorithmRecycler;
    private AlgorithmRecyclerAdapter algorithmRecyclerAdapter;

    private final Callback actionModeCallback = new Callback() {

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.contextual_menu, menu);
            mode.setTitle(algorithmRecyclerAdapter.getSelectedItemCount() + " Algorithm(s) Selected");
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            Intent study2Intent = new Intent(getContext(), Activity_StudyAlgorithm.class);
            switch (item.getItemId()) {
                case R.id.contextual_learn /*2131362000*/:
                    Toast.makeText(getActivity(), "Learn multiple", Toast.LENGTH_SHORT).show();
                    selectedList.addAll(algorithmRecyclerAdapter.getSelectedItems());
                    actionMode.finish();
                    algorithmRecyclerAdapter.clearSelected();
                    study2Intent.putExtra("learn", selectedList);
                    startActivity(study2Intent);
                    return true;
                case R.id.contextual_practice /*2131362001*/:
                    Toast.makeText(getActivity(), "Practice multiple", Toast.LENGTH_SHORT).show();
                    selectedList.addAll(algorithmRecyclerAdapter.getSelectedItems());
                    actionMode.finish();
                    algorithmRecyclerAdapter.clearSelected();
                    study2Intent.putExtra("practice", selectedList);
                    startActivity(study2Intent);
                    return true;
                default:
                    return false;
            }
        }

        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
        }
    };

    private String algorithms_filter_text;
    private TextView home_algs_number;
    private View root;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View inflate = inflater.inflate(R.layout.fragment_home, container, false);
        root = inflate;
        home_algs_number = inflate.findViewById(R.id.home_total_number);
        root.findViewById(R.id.create_new_algorithm_btn).setOnClickListener(this);
        algorithmRecycler = root.findViewById(R.id.algorithm_recycler);
        Box<Algorithm> boxFor = ObjectBox.getBoxStore().boxFor(Algorithm.class);
        algorithmBox = boxFor;
        if (boxFor.isEmpty()) {
            getDefaultAlgs();
        }
        algorithmArrayList = (ArrayList<Algorithm>) algorithmBox.getAll();
        TextView textView = home_algs_number;
        textView.setText("" + algorithmBox.getAll().size());
        algorithmRecyclerAdapter = new AlgorithmRecyclerAdapter(algorithmArrayList, this, getContext());
        algorithmRecycler.setAdapter(algorithmRecyclerAdapter);
        algorithmRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        algorithmRecycler.setHasFixedSize(true);
        setupHomeSpinner();
        return root;
    }

    public void onResume() {
        super.onResume();
        algorithmArrayList = (ArrayList<Algorithm>) algorithmBox.getAll();
        algorithmRecyclerAdapter = new AlgorithmRecyclerAdapter(algorithmArrayList, this, getContext());
        algorithmRecycler.setAdapter(algorithmRecyclerAdapter);
    }

    private void setupHomeSpinner() {
        Spinner algorithms_filter_spinner = root.findViewById(R.id.algorithms_filter_spinner);
        algorithms_filter_spinner.setGravity(View.TEXT_ALIGNMENT_CENTER);
        List<String> algorithm_filter = new ArrayList<>();
        algorithm_filter.add("Recently Created");
        algorithm_filter.add("Most Practiced");
        algorithm_filter.add("Most Correct");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, algorithm_filter);
        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        algorithms_filter_spinner.setAdapter(dataAdapter);
        algorithms_filter_text = algorithms_filter_spinner.getSelectedItem().toString();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.create_new_algorithm_btn) {
            startActivity(new Intent(getContext(), Activity_Algorithm.class));
            return;
        }
        String stringBuilder = "Unexpected value: " +
                v.getId();
        throw new IllegalStateException(stringBuilder);
    }

    public void onAlgorithmClick(int position) {
        Intent intent = new Intent(getContext(), Activity_Algorithm.class);
        intent.putExtra("algorithm_id", algorithmArrayList.get(position).id);
        startActivity(intent);
    }

    public void onAlgorithmImageClick(int position, View v) {
        ActionMode actionMode = AlgorithmHomeFragment.actionMode;
        if (actionMode != null) {
            actionMode.finish();
        }
        Algorithm currentAlg = algorithmArrayList.get(position);
        Toast.makeText(getContext(), String.format("%s - Image Clicked",currentAlg.alg_name), Toast.LENGTH_SHORT).show();
        Dialog caseDialog = new Dialog(getContext());
        caseDialog.setContentView(R.layout.algorithm_image_dialog);
        ImageView dialog_alg_image_icon = caseDialog.findViewById(R.id.dialog_alg_image_icon);
        TextView dialog_alg_image_name = caseDialog.findViewById(R.id.dialog_alg_image_name);
        TextView dialog_alg_image_alg = caseDialog.findViewById(R.id.dialog_alg_image_alg);
        String str = "";
        int id = getResources().getIdentifier(currentAlg.getAlgorithm_icon().replace("R.drawable.", str), "drawable", getActivity().getPackageName());
        if (currentAlg.getAlgorithm_icon() == str) {
            dialog_alg_image_icon.setBackgroundResource(R.drawable.cfop);
        } else {
            dialog_alg_image_icon.setBackgroundResource(id);
        }
        dialog_alg_image_name.setText(currentAlg.alg_name);
        dialog_alg_image_alg.setText(currentAlg.alg);
        caseDialog.show();

        algorithmRecyclerAdapter.clearSelected();
        algorithmRecyclerAdapter.selected_items.clear();
        algorithmRecyclerAdapter.notifyDataSetChanged();
    }

    public void onAlgorithmFavouriteClick(int position, View v) {
        Algorithm alg = algorithmArrayList.get(position);
        alg.setFavourite_alg();
        algorithmBox.put(alg);
        algorithmArrayList = (ArrayList<Algorithm>) algorithmBox.getAll();
        algorithmRecyclerAdapter = new AlgorithmRecyclerAdapter(algorithmArrayList, this, getContext());
        algorithmRecycler.setAdapter(algorithmRecyclerAdapter);
        Context context = getContext();
        String stringBuilder = "Favourite" + position;
        Toast.makeText(context, stringBuilder, Toast.LENGTH_SHORT).show();
    }

    public void onAlgorithmLearntClick(int position, View v) {
        Toast.makeText(getContext(), "Learnt", Toast.LENGTH_SHORT).show();
    }

    public void onAlgorithmLongClick(int pos, View v) {
        if (algorithmRecyclerAdapter.getSelectedItemCount() == 0) {
            if (actionMode != null) {
                actionMode.finish();
                clearSelected();
            }
        } else {
            actionMode = ((MainActivity) getActivity()).startSupportActionMode(actionModeCallback);
        }
    }

    private void loadAlgorithmIcon(Algorithm currentAlg) {

    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        algorithms_filter_text = parent.getItemAtPosition(position).toString();
        Context context = parent.getContext();
        String stringBuilder = "Selected: " +
                algorithms_filter_text;
        Toast.makeText(context, stringBuilder, Toast.LENGTH_SHORT).show();
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void onPrepareOptionsMenu(Menu menu) {
        SearchView searchView = (SearchView) menu.findItem(R.id.actionbar_search).setVisible(true).getActionView();
        searchView.setImeOptions(6);
        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            public boolean onQueryTextSubmit(String query) {
                algorithmRecyclerAdapter.getFilter().filter(query);
                TextView access$100 = home_algs_number;
                String stringBuilder = "" +
                        algorithmRecyclerAdapter.getItemCount();
                access$100.setText(stringBuilder);
                return true;
            }

            public boolean onQueryTextChange(String newText) {
                algorithmRecyclerAdapter.getFilter().filter(newText);
                TextView access$100 = home_algs_number;
                String stringBuilder = "" +
                        algorithmRecyclerAdapter.getItemCount();
                access$100.setText(stringBuilder);
                return true;
            }
        });
        super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        menuItem.getItemId();
        return super.onOptionsItemSelected(menuItem);
    }

    private void clearSelected() {
        for (Integer selected : algorithmRecyclerAdapter.getSelectedItems()) {
            Objects.requireNonNull(algorithmRecycler.findViewHolderForAdapterPosition(selected)).itemView.setBackgroundResource(R.color.white);
            LinearLayout card = (LinearLayout) algorithmRecycler.findViewHolderForAdapterPosition(selected).itemView.findViewById(R.id.algorithm_card_texts);

            for (int i = 0; i < card.getChildCount(); i++) {
                View v = card.getChildAt(i);
                if (v instanceof TextView) {
                    ((TextView) v).setTextColor(Color.rgb(0, 0, 0));
                }
            }
            algorithmRecycler.findViewHolderForAdapterPosition(selected).itemView.findViewById(R.id.algorithm_item_checks).setVisibility(View.VISIBLE);
            algorithmRecycler.findViewHolderForAdapterPosition(selected).itemView.findViewById(R.id.algorithm_selected_checks).setVisibility(View.GONE);

        }
    }

    public void getDefaultAlgs() {
        for (int i = 0; i < alg_names.size(); i++) {
            algorithmBox.put(new Algorithm(alg_names.get(i), algs.get(i), descriptions.get(i), images.get(i), categories.get(i), 0, 0, false, false, false, false));
        }
    }

    @Override
    public void customBackPressed() {
        if (actionMode != null) {
            actionMode.finish();
            clearSelected();
            Toast.makeText(getContext(), "Action Mode", Toast.LENGTH_SHORT).show();
        } else {
            requireActivity().finish();
        }
    }

}
