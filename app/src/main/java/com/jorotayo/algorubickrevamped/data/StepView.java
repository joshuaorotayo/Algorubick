package com.jorotayo.algorubickrevamped.data;

import android.widget.EditText;

import io.objectbox.annotation.Entity;

public class StepView {
    EditText editStepAlgorithm;
    EditText editStepDescription;
    EditText editStepName;

    public StepView(EditText editStepName, EditText editStepDescription, EditText editStepAlgorithm) {
        this.editStepName = editStepName;
        this.editStepDescription = editStepDescription;
        this.editStepAlgorithm = editStepAlgorithm;
    }

    public EditText getEditStepName() {
        return this.editStepName;
    }

    public void setEditStepName(EditText editStepName) {
        this.editStepName = editStepName;
    }

    public EditText getEditStepDescription() {
        return this.editStepDescription;
    }

    public void setEditStepDescription(EditText editStepDescription) {
        this.editStepDescription = editStepDescription;
    }

    public EditText getEditStepAlgorithm() {
        return this.editStepAlgorithm;
    }

    public void setEditStepAlgorithm(EditText editStepAlgorithm) {
        this.editStepAlgorithm = editStepAlgorithm;
    }
}
