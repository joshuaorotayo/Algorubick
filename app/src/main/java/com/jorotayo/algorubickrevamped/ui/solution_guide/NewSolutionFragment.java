package com.jorotayo.algorubickrevamped.ui.solution_guide;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.jorotayo.algorubickrevamped.KeyboardFragment;
import com.jorotayo.algorubickrevamped.ObjectBox;
import com.jorotayo.algorubickrevamped.OnBackPressed;
import com.jorotayo.algorubickrevamped.R;
import com.jorotayo.algorubickrevamped.data.Solution;
import com.jorotayo.algorubickrevamped.data.Steps;
import com.jorotayo.algorubickrevamped.data.Steps_;
import com.jorotayo.algorubickrevamped.utils.UtilMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.objectbox.Box;
import io.objectbox.query.QueryBuilder;

public class NewSolutionFragment extends Fragment implements OnClickListener, OnBackPressed {
    private static final String ARG_PARAM1 = "param1";
    private MaterialAlertDialogBuilder closeSolutionDialog;
    private Solution currentSolution;
    private Intent intent;
    private ArrayList<Integer> mParam1;
    private EditText new_solution_creator;
    private EditText new_solution_description;
    private EditText new_solution_name;
    private ScrollView pageScroller;
    private Box<Solution> solutionBox;
    private Box<Steps> stepsBox;
    private TextInputLayout til_solution_creator;
    private TextInputLayout til_solution_description;
    private TextInputLayout til_solution_name;
    private View view, currentStepView;
    private ViewGroup main;
    private boolean editingStart = true;
    private boolean editingSolutionIcon = false;
    private int currentIndex = 0;
    private ImageView add_solution_icon_preview;

    public static NewSolutionFragment newInstance() {
        return new NewSolutionFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getIntegerArrayList(ARG_PARAM1);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_solution_new, container, false);
        ((SolutionActivity) requireActivity()).getSupportActionBar().setTitle("Create New Solution");
        Button addStepBtn = view.findViewById(R.id.add_step_btn);
        ImageView add_new_solution_icon_btn = view.findViewById(R.id.add_new_solution_icon_btn);
        add_solution_icon_preview = view.findViewById(R.id.new_solution_icon_preview);
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
        add_new_solution_icon_btn.setOnClickListener(this);
        saveNewSolutionBtn.setOnClickListener(this);
        createCloseSolutionDialog();
        checkEditSolution();
        return view;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_step_btn:
                addStepView();
                scrollBottom();
                return;
            case R.id.add_new_solution_icon_btn:
                editingSolutionIcon = true;
                ImageSelection(this);
                return;
            case R.id.save_new_solution_btn:
                saveSolution();
                return;
        }
    }

    private void checkEditSolution() {
        intent = requireActivity().getIntent();
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
        UtilMethods.LoadStepIcon(getContext(), add_solution_icon_preview, currentSolution.solutionIconLocation);
        List<Steps> editSteps = stepsBox.query().equal(Steps_.solutionName, currentSolution.getSolutionName(), QueryBuilder.StringOrder.CASE_INSENSITIVE).build().find();
        int currentStep = editSteps.size();
        for (int i = 0; i < currentStep; i++) {
            View view2 = getLayoutInflater().inflate(R.layout.item_step, null);
            view2.setTag("stepView");
            ((ViewGroup) view.findViewById(R.id.step_linear_container)).addView(view2, -1);
            EditText stepDescriptionEditText = view2.findViewById(R.id.item_step_step_description);
            final EditText algorithmInputSpace = view2.findViewById(R.id.item_step_step_algorithm);
            Button new_solution_step_image_start_btn = view2.findViewById(R.id.new_solution_step_image_start_btn);
            Button new_solution_step_image_end_btn = view2.findViewById(R.id.new_solution_step_image_end_btn);
            ImageView step_start_image_preview = view2.findViewById(R.id.step_start_image_preview);
            ImageView step_end_image_preview = view2.findViewById(R.id.step_end_image_preview);
            ((EditText) view2.findViewById(R.id.item_step_step_name)).setText(editSteps.get(i).getStepName());
            stepDescriptionEditText.setText(editSteps.get(i).getStepDescription());
            algorithmInputSpace.setText(editSteps.get(i).getStepAlgorithm());
            UtilMethods.LoadStepIcon(getContext(), step_start_image_preview, editSteps.get(i).stepImageStart);
            UtilMethods.LoadStepIcon(getContext(), step_end_image_preview, editSteps.get(i).stepImageEnd);
            step_start_image_preview.setTag(editSteps.get(i).stepImageStart);
            step_end_image_preview.setTag(editSteps.get(i).stepImageEnd);
            new_solution_step_image_start_btn.setOnClickListener(v -> StepImageSelection(this, currentIndex, true, view2));
            new_solution_step_image_end_btn.setOnClickListener(v -> StepImageSelection(this, currentIndex, false, view2));
            algorithmInputSpace.setOnClickListener(v -> NewSolutionFragment.this.openKeyboard(algorithmInputSpace));
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
        String solutionImage = "";
        if(add_solution_icon_preview.getTag() != null ){
            solutionImage = add_solution_icon_preview.getTag().toString();
        }
        if (validateSolution()) {
            Solution newSolution = new Solution(solutionName, solutionCreator, solutionDescription, solutionImage);
            if (Objects.requireNonNull(Objects.requireNonNull(((SolutionActivity) requireActivity()).getSupportActionBar()).getTitle()).toString().contains("Edit")) {
                newSolution.id = currentSolution.id;
            }
            solutionBox.put(newSolution);
            saveSteps(solutionName);
        }
        requireActivity().finish();
    }

    private boolean validateSolution() {
        boolean valid = true;
        clearErrors();
        if (new_solution_name.getText().toString().isEmpty()) {
            til_solution_name.setError(getText(R.string.solution_name_error));
            valid = false;
        }
        if (new_solution_creator.getText().toString().isEmpty()) {
            til_solution_name.setError(getText(R.string.solution_creator_error));
            valid = false;
        }
        if (new_solution_description.getText().toString().isEmpty()) {
            til_solution_name.setError(getText(R.string.solution_description_error));
            valid = false;
        }
        if (valid) {
            return true;
        } else {
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
        if (Objects.requireNonNull(Objects.requireNonNull(((SolutionActivity) requireActivity()).getSupportActionBar()).getTitle()).toString().contains("Edit")) {
            stepsBox.remove(this.stepsBox.query().equal(Steps_.solutionName, currentSolution.getSolutionName(), QueryBuilder.StringOrder.CASE_INSENSITIVE).build().find());
        }
        main = view.findViewById(R.id.step_linear_container);
        for (int index = 0; index < main.getChildCount(); index++) {
            View nextChild = main.getChildAt(index);
            EditText stepNameEditText = nextChild.findViewById(R.id.item_step_step_name);
            EditText stepDescriptionEditText = nextChild.findViewById(R.id.item_step_step_description);
            EditText stepAlgorithmEditText = nextChild.findViewById(R.id.item_step_step_algorithm);
            ImageView step_start_image_preview = nextChild.findViewById(R.id.step_start_image_preview);
            ImageView step_end_image_preview = nextChild.findViewById(R.id.step_end_image_preview);
            Steps steps = new Steps();
            steps.setSolutionName(solutionName);
            steps.setStepNumber(index);
            steps.setStepName(stepNameEditText.getText().toString());
            steps.setStepDescription(stepDescriptionEditText.getText().toString());
            steps.setStepAlgorithm(stepAlgorithmEditText.getText().toString());
            if(step_start_image_preview.getTag() != null){
                steps.setStepImageStart(step_start_image_preview.getTag().toString());
            }
            if(step_end_image_preview.getTag() != null){
                steps.setStepImageEnd(step_end_image_preview.getTag().toString());
            }
            if (!(steps.stepDescription.isEmpty() || steps.stepName.isEmpty())) {
                stepsBox.put(steps);
            }
        }
    }

    private void addStepView() {
        View view2 = getLayoutInflater().inflate(R.layout.item_step, null);
        main = view.findViewById(R.id.step_linear_container);
        int currentIndex = main.getChildCount();
        view2.setTag("stepView, " + currentIndex);
        ((ViewGroup) view.findViewById(R.id.step_linear_container)).addView(view2, -1);
        final EditText algorithmInputSpace = view2.findViewById(R.id.item_step_step_algorithm);
        algorithmInputSpace.setOnClickListener(v -> openKeyboard(algorithmInputSpace));
        Button new_solution_step_image_start_btn = view2.findViewById(R.id.new_solution_step_image_start_btn);
        Button new_solution_step_image_end_btn = view2.findViewById(R.id.new_solution_step_image_end_btn);
        new_solution_step_image_start_btn.setOnClickListener(v -> StepImageSelection(this, currentIndex, true, view2));
        new_solution_step_image_end_btn.setOnClickListener(v -> StepImageSelection(this, currentIndex, false, view2));
    }

    private void StepImageSelection(Fragment fragment, int currentImgIndex, boolean editingStartImg, View currentView) {
        currentIndex = currentImgIndex;
        editingStart = editingStartImg;
        currentStepView = currentView;
        editingSolutionIcon = false;
        String[] mimeTypes = {"image/png", "image/jpg", "image/jpeg"};
        ImagePicker.Companion.with(fragment)
                .crop()
                .compress(1024)
                .galleryMimeTypes(  //Exclude gif images
                        mimeTypes
                )
                .start();
    }

    public void ImageSelection(Fragment fragment) {
        editingSolutionIcon = false;
        String[] mimeTypes = {"image/png", "image/jpg", "image/jpeg"};
        ImagePicker.Companion.with(fragment)
                .crop()
                .compress(1024)
                .galleryMimeTypes(  //Exclude gif images
                        mimeTypes
                )
                .start();
    }



    @Override
    public final void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ImageView step_start_image_preview;
            assert data != null;
            if (editingSolutionIcon) {
                Uri solution_alg_uri = data.getData();
                add_solution_icon_preview.setImageURI(solution_alg_uri);
                if(solution_alg_uri == null) {
                    add_solution_icon_preview.setTag("");
                } else {
                    add_solution_icon_preview.setTag(solution_alg_uri.toString());
                }
            } else if (editingStart) {
                Uri start_alg_uri = data.getData();
                step_start_image_preview = currentStepView.findViewById(R.id.step_start_image_preview);
                step_start_image_preview.setImageURI(start_alg_uri);
                step_start_image_preview.setTag(start_alg_uri.toString());
            } else {
                Uri end_alg_uri = data.getData();
                ImageView step_end_image_preview = currentStepView.findViewById(R.id.step_end_image_preview);
                step_end_image_preview.setImageURI(end_alg_uri);
                step_end_image_preview.setTag(end_alg_uri.toString());
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getActivity(), getText(R.string.no_image_selected), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), getText(R.string.image_selection_cancelled), Toast.LENGTH_SHORT).show();
        }
    }

    private void scrollBottom() {
        //only scrolls to the bottom if a new step has been added
        pageScroller.postDelayed(() -> NewSolutionFragment.this.pageScroller.fullScroll(130), 5);
    }

    private void openKeyboard(EditText editText) {
        closeKeyboard();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.add(R.id.new_solution_keyboard_space, new KeyboardFragment(editText));
        ft.commit();
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

    public void saveBackPressed() {
        requireActivity().onBackPressed();
    }
}
