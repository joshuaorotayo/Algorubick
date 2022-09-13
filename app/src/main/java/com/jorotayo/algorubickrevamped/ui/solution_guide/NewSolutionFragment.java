package com.jorotayo.algorubickrevamped.ui.solution_guide;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.jorotayo.algorubickrevamped.KeyboardFragment;
import com.jorotayo.algorubickrevamped.ObjectBox;
import com.jorotayo.algorubickrevamped.OnBackPressed;
import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Solution;
import com.jorotayo.algorubickrevamped.data.Steps;
import com.jorotayo.algorubickrevamped.data.Steps_;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.objectbox.Box;

import static android.content.ContentValues.TAG;

public class NewSolutionFragment extends Fragment implements OnClickListener, OnBackPressed {
    private static final String ARG_PARAM1 = "param1";
    private MaterialAlertDialogBuilder closeSolutionDialog;
    private Solution currentSolution;
    private Intent intent;
    private EditText new_solution_creator;
    private EditText new_solution_description;
    private EditText new_solution_name;
    private ScrollView pageScroller;
    private Box<Solution> solutionBox;
    private Box<Steps> stepsBox;
    private TextInputLayout til_solution_creator;
    private TextInputLayout til_solution_description;
    private TextInputLayout til_solution_name;
    private View view;

    public static NewSolutionFragment newInstance() {
        return new NewSolutionFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ArrayList<Integer> mParam1 = getArguments().getIntegerArrayList(ARG_PARAM1);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_solution_new, container, false);
        ((SolutionActivity) requireActivity()).getSupportActionBar().setTitle("Create New Solution");
        Button addStepBtn = view.findViewById(R.id.add_step_btn);
        new_solution_name = view.findViewById(R.id.new_solution_name);
        new_solution_creator = view.findViewById(R.id.new_solution_creator);
        new_solution_description = view.findViewById(R.id.new_solution_description);
        til_solution_name = view.findViewById(R.id.til_solution_name);
        til_solution_creator = view.findViewById(R.id.til_solution_creator);
        til_solution_description = view.findViewById(R.id.til_solution_description);
        Button saveNewSolutionBtn = view.findViewById(R.id.save_new_solution_btn);
        pageScroller = view.findViewById(R.id.create_solution_activity_scroller);
        solutionBox = ObjectBox.getBoxStore().boxFor(Solution.class);
        stepsBox = ObjectBox.getBoxStore().boxFor(Steps.class);
        addStepBtn.setOnClickListener(this);
        saveNewSolutionBtn.setOnClickListener(this);
        createCloseSolutionDialog();
        checkEditSolution();
        return view;
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.add_step_btn) {
            addStepView();
        } else if (id == R.id.save_new_solution_btn) {
            saveSolution();
            Log.d(TAG, "onClick() returned: Save Solution");
        }
    }

    private void checkEditSolution() {
        Intent intent = requireActivity().getIntent();
        if (intent.hasExtra("edit")) {
            editSolution();
        } else {
            addStepView();
        }
    }

    private void editSolution() {
        Objects.requireNonNull(((SolutionActivity) requireActivity()).getSupportActionBar()).setTitle("Edit Solution");
        Solution solution = solutionBox.get(this.intent.getLongExtra("edit", 0));
        currentSolution = solution;
        new_solution_name.setText(solution.getSolutionName());
        new_solution_creator.setText(this.currentSolution.getSolutionCreator());
        new_solution_description.setText(this.currentSolution.getSolutionDescription());
        List<Steps> editSteps = stepsBox.query().equal(Steps_.solutionName, currentSolution.getSolutionName()).build().find();
        int currentStep = editSteps.size();
        for (int i = 0; i < currentStep; i++) {
            View view2 = getLayoutInflater().inflate(R.layout.item_step, null);
            view2.setTag("stepView");
            ((ViewGroup) view.findViewById(R.id.step_linear_container)).addView(view2, -1);
            EditText stepDescriptionEditText = view2.findViewById(R.id.item_step_step_description);
            final EditText algorithmInputSpace = view2.findViewById(R.id.item_step_step_algorithm);
            ((EditText) view2.findViewById(R.id.item_step_step_name)).setText(editSteps.get(i).getStepName());
            stepDescriptionEditText.setText(editSteps.get(i).getStepDescription());
            algorithmInputSpace.setText(editSteps.get(i).getStepAlgorithm());
            algorithmInputSpace.setOnClickListener(v -> NewSolutionFragment.this.openKeyboard(algorithmInputSpace));
            scrollBottom();
        }
    }

    private void createCloseSolutionDialog() {
        CharSequence charSequence = "Cancel";
        closeSolutionDialog = new MaterialAlertDialogBuilder(requireContext()).setTitle("Close without saving?").setMessage("If you carry on the current Solution will be closed without saving. Click ok if you are fine to do ").setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                NewSolutionFragment.this.requireActivity().finish();
            }
        }).setNegativeButton(charSequence, (dialog, which) -> dialog.dismiss());
    }

    private void saveSolution() {
        String solutionName = new_solution_name.getText().toString();
        String solutionCreator = new_solution_creator.getText().toString();
        String solutionDescription = new_solution_description.getText().toString();
        if (validateSolution()) {
            Solution newSolution = new Solution(solutionName, solutionCreator, solutionDescription);
            if (getActivity().getActionBar().getTitle().toString().contains("Edit")) {
                newSolution.id = currentSolution.id;
            }
            solutionBox.put(newSolution);
            saveSteps(solutionName);
            //return;
        }
    }

    private boolean validateSolution() {
        boolean valid = true;
        clearErrors();
        if (new_solution_name.getText().toString().isEmpty()) {
            til_solution_name.setError("Solution Name cannot be blank");
            valid = false;
        }
        if (new_solution_creator.getText().toString().isEmpty()) {
            til_solution_creator.setError("Solution creator cannot be blank");
            valid = false;
        }
        if (new_solution_description.getText().toString().isEmpty()) {
            til_solution_description.setError("Solution Description cannot be blank");
            valid = false;
        }
        if (valid){
            return true;
        }else {
            new MaterialAlertDialogBuilder(requireContext()).setTitle("Solution Error").setMessage("Fill in all Solution details before saving").setIcon(R.drawable.incorrect_48_r).show();
            return false;
        }
    }

    private void clearErrors() {
        String str = "";
        til_solution_name.setError(str);
        til_solution_creator.setError(str);
        til_solution_description.setError(str);
    }

    private void saveSteps(String solutionName) {
        String str = "Debug";
        if (((SolutionActivity) getActivity()).getSupportActionBar().getTitle().toString().contains("Edit")) {
            Log.d(str, "saveSteps: EDIT");
            stepsBox.remove(this.stepsBox.query().equal(Steps_.solutionName, currentSolution.getSolutionName()).build().find());
        }
        ViewGroup main = view.findViewById(R.id.step_linear_container);
        for (int index = 0; index < main.getChildCount(); index++) {
            View nextChild = main.getChildAt(index);
            EditText stepNameEditText = nextChild.findViewById(R.id.item_step_step_name);
            EditText stepDescriptionEditText = nextChild.findViewById(R.id.item_step_step_description);
            EditText stepAlgorithmEditText = nextChild.findViewById(R.id.item_step_step_algorithm);
            Steps steps = new Steps();
            steps.setSolutionName(solutionName);
            steps.setStepNumber(index);
            steps.setStepName(stepNameEditText.getText().toString());
            steps.setStepDescription(stepDescriptionEditText.getText().toString());
            steps.setStepAlgorithm(stepAlgorithmEditText.getText().toString());
            if (!(steps.stepDescription.isEmpty() || steps.stepName.isEmpty())) {
                stepsBox.put(steps);
            }
            Log.d(str, "saveSteps: NEW");
        }
    }

    private void addStepView() {
        View view2 = getLayoutInflater().inflate(R.layout.item_step, null);
        view2.setTag("stepView");
        ((ViewGroup) view.findViewById(R.id.step_linear_container)).addView(view2, -1);
        final EditText algorithmInputSpace = view2.findViewById(R.id.item_step_step_algorithm);
        algorithmInputSpace.setOnClickListener(v -> NewSolutionFragment.this.openKeyboard(algorithmInputSpace));
        scrollBottom();
    }

    private void scrollBottom() {
        pageScroller.postDelayed(() -> NewSolutionFragment.this.pageScroller.fullScroll(130), 5);
    }

    private void openKeyboard(EditText editText) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.add(R.id.new_solution_keyboard_space, new KeyboardFragment(editText));
        ft.commit();
        closeKeyboard();
        scrollBottom();
    }

    private void closeKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            Objects.requireNonNull((InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return false;
        }
        customBackPressed();
        return true;
    }

    @Override
    public void customBackPressed() {
        closeSolutionDialog.show();
    }

    public void saveBackPressed(String text){
        requireActivity().onBackPressed();
    }
}
