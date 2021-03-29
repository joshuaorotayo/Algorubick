package com.jorotayo.algorubickrevamped.ui.home;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.objectbox.Box;

public class AlgorithmHomeFragment extends Fragment implements OnClickListener, com.jorotayo.algorubickrevamped.ui.home.AlgorithmRecyclerAdapter.OnAlgorithmListener, OnItemSelectedListener {
    private final ArrayList selectedList = new ArrayList();
    ArrayList<String> alg_names = new ArrayList(Arrays.asList("Right Sexy Move", "Left Sexy Move", "H-Perm", "Z-Perm", "T-Perm", "J1-Perm", "J2-Perm", "N1-Perm", "N2-Perm", "V-Perm", "Y-Perm", "E-perm", "F-Perm", "R1-Perm", "R2-Perm"));
    ArrayList algs = new ArrayList(Arrays.asList("R,U,R',U'", "L',U',L,U", "M2,U,M2,U2,M2,U,M2", "M2,U,M2,U,M',U2,M2,U2,M',U2", "R,U,R',U',R,F,R2,U',R',U',R',U,R',F'", "R',U,L',U2,R,U',R',U2,R,L,U'", "R,U,R',F',R,U,R',U',R',F,R2,U',R',U'", "L,U',R,U2,L',U,R',L,U',R,U2,L',U,R',U", "R',U,L',U2,R,U',L,R',U,L',U2,R,U',L,U'", "R',U,R',d',R',F',R2,U',R',U,R',F,R,F", "F,R,U',R',U',R,U,R',F',R,U,R',U',R',F,R,F'", "X',R,U',R',D,R,U,R',u2,R',U,R,D,R',U',R", "R',U2,R',d',R',F',R2,U',R',U,R',F,R,U',F", "L,U2,L',U2,L,F',L',U',L,U,L,F,L2,U", "R',U2,R,U2,R',F,R,U,R',U',R',F',R2,U'"));
    ArrayList<String> categories = new ArrayList(Arrays.asList("Triggers", "Triggers", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL"));
    ArrayList<String> descriptions = new ArrayList(Arrays.asList("Insert bottom right corner and trigger", "Insert bottom left corner and trigger", "Swap opposite Edges (4e)", "Swap adjacent Edges (4e)", "Swap two opposite edges and two corners (2e2c)", "Swap two opposite corners, swap two adjacent edges (2c2e)", "Swap two corners, swap two adjacent edges (2e2c)", "Swap diagonal corner and opposite edge, left to right (2e2c)", "wap diagonal corner and opposite edge, Right to left (2e2c)", "Swap inline adjacent edge and diagonal corner (2e2c)", "Cross shaped swap diagonal corner and adjacent edge (2e2c)", "Swap opposite corners (4c)", "Swap inline 2 edges and 2 corners (2e2c)", "Swap opposite corners and adjacent edge (2e2c)", "Swap opposite corners and adjacent edge (2e2c)"));
    ArrayList<String> images = new ArrayList(Arrays.asList("R.drawable.right_edge", "R.drawable.left_edge", "R.drawable.h_perm", "R.drawable.z_perm", "R.drawable.t_perm", "R.drawable.j1_perm", "R.drawable.j2_perm", "R.drawable.n1_perm", "R.drawable.n2_perm", "R.drawable.v_perm", "R.drawable.y_perm", "R.drawable.e_perm", "R.drawable.f_perm", "R.drawable.r1_perm", "R.drawable.r2_perm"));
    int selectedNum = 0;
    private ActionMode actionMode;
    private ArrayList<Algorithm> algorithmArrayList = new ArrayList();
    private Box<Algorithm> algorithmBox;
    private RecyclerView algorithmRecycler;
    private AlgorithmRecyclerAdapter algorithmRecyclerAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View inflate = inflater.inflate(R.layout.fragment_home, container, false);
        this.root = inflate;
        this.home_algs_number = inflate.findViewById(R.id.home_total_number);
        this.root.findViewById(R.id.create_new_algorithm_btn).setOnClickListener(this);
        this.algorithmRecycler = this.root.findViewById(R.id.algorithm_recycler);
        Box boxFor = ObjectBox.getBoxStore().boxFor(Algorithm.class);
        this.algorithmBox = boxFor;
        if (boxFor.isEmpty()) {
            getDefaultAlgs();
        }
        this.algorithmArrayList = (ArrayList) this.algorithmBox.getAll();
        TextView textView = this.home_algs_number;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(this.algorithmArrayList.size());
        textView.setText(stringBuilder.toString());
        AlgorithmRecyclerAdapter algorithmRecyclerAdapter = new AlgorithmRecyclerAdapter(this.algorithmArrayList, this, getContext());
        this.algorithmRecyclerAdapter = algorithmRecyclerAdapter;
        this.algorithmRecycler.setAdapter(algorithmRecyclerAdapter);
        this.algorithmRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.algorithmRecycler.setHasFixedSize(true);
        setupHomeSpinner();
        return this.root;
    }

    private final Callback actionModeCallback = new Callback() {
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.contextual_menu, menu);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(AlgorithmHomeFragment.this.algorithmRecyclerAdapter.getSelectedItemCount());
            stringBuilder.append(" Algorithm(s) Selected");
            mode.setTitle(stringBuilder.toString());
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            Intent study2Intent = new Intent(AlgorithmHomeFragment.this.getContext(), com.jorotayo.algorubickrevamped.ui.home.StudyAlgorithmActivity.class);
            switch (item.getItemId()) {
                case R.id.contextual_learn /*2131362000*/:
                    Toast.makeText(AlgorithmHomeFragment.this.getActivity(), "Learn multiple", Toast.LENGTH_SHORT).show();
                    for (Integer selected : AlgorithmHomeFragment.this.algorithmRecyclerAdapter.getSelectedItems()) {
                        AlgorithmHomeFragment.this.selectedList.add(selected);
                    }
                    AlgorithmHomeFragment.this.actionMode.finish();
                    AlgorithmHomeFragment.this.algorithmRecyclerAdapter.clearSelected();
                    study2Intent.putExtra("learn", AlgorithmHomeFragment.this.selectedList);
                    AlgorithmHomeFragment.this.startActivity(study2Intent);
                    return true;
                case R.id.contextual_practice /*2131362001*/:
                    Toast.makeText(AlgorithmHomeFragment.this.getActivity(), "Practice multiple", Toast.LENGTH_SHORT).show();
                    for (Integer selected2 : AlgorithmHomeFragment.this.algorithmRecyclerAdapter.getSelectedItems()) {
                        AlgorithmHomeFragment.this.selectedList.add(selected2);
                    }
                    AlgorithmHomeFragment.this.actionMode.finish();
                    AlgorithmHomeFragment.this.algorithmRecyclerAdapter.clearSelected();
                    study2Intent.putExtra("practice", AlgorithmHomeFragment.this.selectedList);
                    AlgorithmHomeFragment.this.startActivity(study2Intent);
                    return true;
                default:
                    return false;
            }
        }

        public void onDestroyActionMode(ActionMode mode) {
            AlgorithmHomeFragment.this.actionMode = null;
        }
    };

    private String algorithms_filter_text;
    private TextView home_algs_number;
    private View root;

    public void onResume() {
        super.onResume();
        this.algorithmArrayList = (ArrayList) this.algorithmBox.getAll();
        AlgorithmRecyclerAdapter algorithmRecyclerAdapter = new AlgorithmRecyclerAdapter(this.algorithmArrayList, this, getContext());
        this.algorithmRecyclerAdapter = algorithmRecyclerAdapter;
        this.algorithmRecycler.setAdapter(algorithmRecyclerAdapter);
    }

    private void setupHomeSpinner() {
        Spinner algorithms_filter_spinner = this.root.findViewById(R.id.algorithms_filter_spinner);
        List<String> algorithm_filter = new ArrayList();
        algorithm_filter.add("Recently Added");
        algorithm_filter.add("Most Practiced");
        algorithm_filter.add("Most Correct");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, algorithm_filter);
        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        algorithms_filter_spinner.setAdapter(dataAdapter);
        this.algorithms_filter_text = algorithms_filter_spinner.getSelectedItem().toString();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.create_new_algorithm_btn) {
            startActivity(new Intent(getContext(), AlgorithmActivity.class));
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unexpected value: ");
        stringBuilder.append(v.getId());
        throw new IllegalStateException(stringBuilder.toString());
    }

    public void onAlgorithmClick(int position) {
        Intent intent = new Intent(getContext(), com.jorotayo.algorubickrevamped.ui.home.AlgorithmActivity.class);
        intent.putExtra("algorithm_id", this.algorithmArrayList.get(position).id);
        startActivity(intent);
    }

    public void onAlgorithmImageClick(int position, View v) {
        ActionMode actionMode = this.actionMode;
        if (actionMode != null) {
            actionMode.finish();
        }
        Algorithm currentAlg = this.algorithmArrayList.get(position);
        Toast.makeText(getContext(), "Image Clicked", Toast.LENGTH_SHORT).show();
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
    }

    public void onAlgorithmFavouriteClick(int position, View v) {
        Algorithm alg = this.algorithmArrayList.get(position);
        alg.setFavourite_alg();
        this.algorithmBox.put(alg);
        this.algorithmArrayList = (ArrayList) this.algorithmBox.getAll();
        AlgorithmRecyclerAdapter algorithmRecyclerAdapter = new AlgorithmRecyclerAdapter(this.algorithmArrayList, this, getContext());
        this.algorithmRecyclerAdapter = algorithmRecyclerAdapter;
        this.algorithmRecycler.setAdapter(algorithmRecyclerAdapter);
        Context context = getContext();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Favourite");
        stringBuilder.append(position);
        Toast.makeText(context, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
    }

    public void onAlgorithmLearntClick(int position, View v) {
        Toast.makeText(getContext(), "Learnt", Toast.LENGTH_SHORT).show();
    }

    public void onAlgorithmLongClick(int pos, View v) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("onAlgorithmLongClick: ");
        stringBuilder.append(this.algorithmRecyclerAdapter.getSelectedItemCount());
        Log.d("AlgorithmHomeFragment", stringBuilder.toString());
        if (this.algorithmRecyclerAdapter.getSelectedItemCount() == 0) {
            this.actionMode.finish();
        } else {
            this.actionMode = ((MainActivity) getActivity()).startSupportActionMode(this.actionModeCallback);
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.algorithms_filter_text = parent.getItemAtPosition(position).toString();
        Context context = parent.getContext();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Selected: ");
        stringBuilder.append(this.algorithms_filter_text);
        Toast.makeText(context, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

/*    public void createAlgorithmList(int numAlgorithms) {
        for (int i = 1; i <= numAlgorithms; i++) {
            Box box = this.algorithmBox;
            Object obj = r4;
            Algorithm algorithm = new Algorithm("Algorithm_name", "R',U,R2,D',B,F,L'", "Description of an algorithm", "Triggers", true, true, false, 5, 5, false, "");
            box.put(obj);
        }
    }*/

    public void onPrepareOptionsMenu(Menu menu) {
        SearchView searchView = (SearchView) menu.findItem(R.id.actionbar_search).setVisible(true).getActionView();
        searchView.setImeOptions(6);
        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            public boolean onQueryTextSubmit(String query) {
                AlgorithmHomeFragment.this.algorithmRecyclerAdapter.getFilter().filter(query);
                TextView access$100 = AlgorithmHomeFragment.this.home_algs_number;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("");
                stringBuilder.append(AlgorithmHomeFragment.this.algorithmRecyclerAdapter.getItemCount());
                access$100.setText(stringBuilder.toString());
                return true;
            }

            public boolean onQueryTextChange(String newText) {
                AlgorithmHomeFragment.this.algorithmRecyclerAdapter.getFilter().filter(newText);
                TextView access$100 = AlgorithmHomeFragment.this.home_algs_number;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("");
                stringBuilder.append(AlgorithmHomeFragment.this.algorithmRecyclerAdapter.getItemCount());
                access$100.setText(stringBuilder.toString());
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
        this.algorithmRecyclerAdapter.getSelectedItems();
        for (Integer selected : this.algorithmRecyclerAdapter.getSelectedItems()) {
            Objects.requireNonNull(this.algorithmRecycler.findViewHolderForAdapterPosition(selected)).itemView.setBackgroundResource(R.color.white);
        }
    }

    public void getDefaultAlgs() {
        for (int i = 0; i < this.alg_names.size(); i++) {
            this.algorithmBox.put(new Algorithm(i, alg_names.get(i), algs.get(i).toString(), descriptions.get(i), categories.get(i), images.get(i), 0, 0, false, false, false, false));
        }
    }
}
