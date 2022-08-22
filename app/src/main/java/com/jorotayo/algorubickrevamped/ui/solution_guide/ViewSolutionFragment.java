package com.jorotayo.algorubickrevamped.ui.solution_guide;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jorotayo.algorubickrevamped.ObjectBox;
import com.jorotayo.algorubickrevamped.OnBackPressed;
import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Solution;
import com.jorotayo.algorubickrevamped.data.Steps;
import com.jorotayo.algorubickrevamped.data.Steps_;
import com.jorotayo.algorubickrevamped.ui.solution_guide.StepsRecyclerAdapter.OnStepListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

import io.objectbox.Box;

public class ViewSolutionFragment extends Fragment implements OnClickListener, OnStepListener, OnBackPressed {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String ARG_PARAM1 = "param1";
    Builder deleteDialog;
    private Solution currentSolution;
    private Box<Solution> solutionBox;
    private ArrayList<Steps> stepsArrayList = new ArrayList();
    private Box<Steps> stepsBox;
    private RecyclerView view_solution_steps_recycler;

    static ViewSolutionFragment newInstance() {
        return new ViewSolutionFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ArrayList<Integer> mParam1 = getArguments().getIntegerArrayList(ARG_PARAM1);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_solution_view, container, false);
        this.solutionBox = ObjectBox.getBoxStore().boxFor(Solution.class);
        this.stepsBox = ObjectBox.getBoxStore().boxFor(Steps.class);
        this.currentSolution = this.solutionBox.get(requireActivity().getIntent().getExtras().getLong("Solution"));
        Objects.requireNonNull(((SolutionActivity) requireActivity()).getSupportActionBar()).setTitle("View Solution");
        Objects.requireNonNull(((SolutionActivity) requireActivity()).getSupportActionBar()).setSubtitle(this.currentSolution.getSolutionName());
        setupStepsAdapter();
        setupDeleteDialog();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("StepsBoxSize: ");
        stringBuilder.append(this.stepsBox.getAll().size());
        Log.d("StepsBox", stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append("onCreateView: ");
        stringBuilder.append(this.stepsArrayList.size());
        Log.d("StepsArrayList", stringBuilder.toString());
        ImageView view_solution_image = view.findViewById(R.id.view_solution_image);
        TextView view_solution_creator = view.findViewById(R.id.view_solution_creator);
        TextView view_solution_description = view.findViewById(R.id.view_solution_description);
        ((TextView) view.findViewById(R.id.view_solution_name)).setText(this.currentSolution.getSolutionName());
        view_solution_creator.setText(this.currentSolution.getSolutionCreator());
        String stringBuilder2 = "" +
                this.currentSolution.getSolutionDescription();
        view_solution_description.setText(stringBuilder2);
        view_solution_image.setImageResource(R.drawable.cfop);
        this.view_solution_steps_recycler = view.findViewById(R.id.view_solution_steps_recycler);
        this.view_solution_steps_recycler.setAdapter(new StepsRecyclerAdapter(this.stepsArrayList, this));
        this.view_solution_steps_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        this.view_solution_steps_recycler.setHasFixedSize(true);
        return view;
    }

    private void setupDeleteDialog() {
        Builder title = new Builder(getContext()).setTitle("Delete Solution");
        String stringBuilder = "Are you sure you want to delete the current Solution:" +
                this.currentSolution.getSolutionName();
        String str = "No";
        this.deleteDialog = title.setMessage(stringBuilder).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ViewSolutionFragment.this.deleteSolution();
            }
        }).setNegativeButton(str, (dialog, which) -> dialog.dismiss()).setIcon(R.drawable.incorrect_48_r).setCancelable(true);
    }

    private void deleteSolution() {
        Iterator it = this.stepsArrayList.iterator();
        while (it.hasNext()) {
            this.stepsBox.remove((Steps) it.next());
        }
        this.solutionBox.remove(this.currentSolution);
        requireActivity().onBackPressed();
    }

    private void setupStepsAdapter() {
        ArrayList arrayList = (ArrayList) this.stepsBox.query().equal(Steps_.solutionName, this.currentSolution.getSolutionName()).build().find();
        this.stepsArrayList = arrayList;
        Collections.sort(arrayList);
    }

    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.view_solution_menu, menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            customBackPressed();
            return true;
        } else if (itemId == R.id.delete_solution_menu_button) {
            this.deleteDialog.show();
            return true;
        } else if (itemId != R.id.edit_solution_menu_button) {
            return false;
        } else {
            editSolution();
            return true;
        }
    }

    private void editSolution() {
        NewSolutionFragment newFragment = NewSolutionFragment.newInstance();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_solution_guide, newFragment);
        transaction.addToBackStack("editSolution");
        getActivity().getIntent().putExtra("edit", this.currentSolution.id);
        transaction.commit();
    }

    public void onClick(View v) {
        v.getId();
    }

    public void onStepClick(int position) {
        Toast.makeText(getContext(), "On Step Click", Toast.LENGTH_SHORT).show();
    }

    public void onStepImageClick(int position, View v) {
        Toast.makeText(getContext(), "On Step Image Click", Toast.LENGTH_SHORT).show();
    }

    public void onResume() {
        super.onResume();
        ArrayList arrayList = (ArrayList) this.stepsBox.query().equal(Steps_.solutionName, this.currentSolution.getSolutionName()).build().find();
        this.stepsArrayList = arrayList;
        Collections.sort(arrayList);
        this.view_solution_steps_recycler.setAdapter(new StepsRecyclerAdapter(this.stepsArrayList, this));
    }

    public void customBackPressed() {
        requireActivity().finish();
    }

}
