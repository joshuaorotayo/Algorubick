package com.jorotayo.algorubickrevamped.ui.solution_guide;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jorotayo.algorubickrevamped.ObjectBox;
import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Solution;
import com.jorotayo.algorubickrevamped.data.Solution_;
import com.jorotayo.algorubickrevamped.ui.solution_guide.SolutionRecyclerAdapter.OnSolutionListener;

import java.util.ArrayList;

import io.objectbox.Box;

public class SolutionGuideFragment extends Fragment implements OnClickListener, OnSolutionListener, OnItemSelectedListener {
    private View root;
    private ArrayList<Solution> solutionArrayList = new ArrayList<>();
    private Box<Solution> solutionBox;
    private SolutionRecyclerAdapter solutionRecyclerAdapter;
    private Spinner solution_sort_spinner;
    private RecyclerView solutionsRecycler;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_solution_guide, container, false);
        this.root = inflate;
        this.solutionsRecycler = inflate.findViewById(R.id.solution_guide_recycler);
        Button createNewSolutionBtn = this.root.findViewById(R.id.create_new_solution_btn);
        this.solution_sort_spinner = this.root.findViewById(R.id.solution_sort_spinner);
        createNewSolutionBtn.setOnClickListener(this);
        Box<Solution> boxFor = ObjectBox.getBoxStore().boxFor(Solution.class);
        this.solutionBox = boxFor;
        this.solutionArrayList = (ArrayList<Solution>) boxFor.getAll();
        this.solutionsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.solutionsRecycler.setHasFixedSize(true);
        solutionRecyclerAdapter = new SolutionRecyclerAdapter(this.solutionArrayList, this);
        this.solutionsRecycler.setAdapter(solutionRecyclerAdapter);
        setupSpinner();
        return this.root;
    }

    public void onClick(View v) {
        if (v.getId() == R.id.create_new_solution_btn) {
            startActivity(new Intent(getContext(), SolutionActivity.class));
        }
    }

    public void onSolutionClick(int position) {
        Intent intent = new Intent(getContext(), SolutionActivity.class);
        Long solutionId = this.solutionArrayList.get(position).id;
        intent.putExtra("Solution", solutionId);
        startActivity(intent);
    }

    public void onSolutionImageClick(int position, View view) {
        Toast.makeText(getContext(), "Solution Image Click", Toast.LENGTH_SHORT).show();
    }

    private void setupSpinner() {
        Spinner spinner = this.root.findViewById(R.id.solution_sort_spinner);
        this.solution_sort_spinner = spinner;
        spinner.setOnItemSelectedListener(this);
        ArrayList<String> solution_sort = new ArrayList<>();
        solution_sort.add("Solution Name");
        solution_sort.add("Solution Creator");
        ArrayAdapter<String> solutionAdapter = new ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, solution_sort);
        solution_sort_spinner.setAdapter(solutionAdapter);
        this.solution_sort_spinner.setAdapter(solutionAdapter);
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        String item = this.solution_sort_spinner.getSelectedItem().toString();
        this.solution_sort_spinner.setSelection(position);
        if (position == 0) {
            sortByName();
        } else if (position == 1) {
            sortByCreator();
        }
        SolutionRecyclerAdapter solutionRecyclerAdapter = new SolutionRecyclerAdapter(this.solutionArrayList, this);
        this.solutionRecyclerAdapter = solutionRecyclerAdapter;
        this.solutionsRecycler.setAdapter(solutionRecyclerAdapter);
    }

    private void sortByName() {
        this.solutionArrayList = (ArrayList<Solution>) this.solutionBox.query().order(Solution_.solutionName).build().find();
    }

    private void sortByCreator() {
        this.solutionArrayList = (ArrayList<Solution>) this.solutionBox.query().order(Solution_.solutionCreator).build().find();
    }

    public void sortBySteps() {
        this.solutionArrayList = (ArrayList<Solution>) this.solutionBox.query().order(Solution_.solutionCreator).build().find();
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void onResume() {
        super.onResume();
        this.solutionArrayList = (ArrayList<Solution>) this.solutionBox.getAll();
        this.solutionsRecycler.setAdapter(new SolutionRecyclerAdapter(this.solutionArrayList, this));
    }
}
