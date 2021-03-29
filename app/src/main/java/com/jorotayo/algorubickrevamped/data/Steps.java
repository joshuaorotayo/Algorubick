package com.jorotayo.algorubickrevamped.data;

import java.util.ArrayList;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class Steps implements Comparable {

    @Id
    public long id;
    public String solutionName;
    public String stepAlgorithm;
    public String stepDescription;
    public String stepImageEnd;
    public String stepImageStart;
    public String stepName;
    public int stepNumber;

    public Steps(String solutionName, int stepNumber, String stepName, String stepDescription, String stepAlgorithm, String stepImageStart, String stepImageEnd) {
        this.solutionName = solutionName;
        this.stepNumber = stepNumber;
        this.stepName = stepName;
        this.stepDescription = stepDescription;
        this.stepAlgorithm = stepAlgorithm;
        this.stepImageStart = stepImageStart;
        this.stepImageEnd = stepImageEnd;
    }

    public Steps() {

    }

    public static ArrayList<Steps> createFakeSteps(int numSteps) {
        ArrayList<Steps> steps = new ArrayList();
        for (int i = 1; i <= numSteps; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Solution_");
            stringBuilder.append(i);
            String stringBuilder2 = stringBuilder.toString();
            stringBuilder = new StringBuilder();
            stringBuilder.append("Name of Step_");
            stringBuilder.append(i);
            steps.add(new Steps(stringBuilder2, i, stringBuilder.toString(), "Description for the step", "R,U2,F2,S,X,B'", "", ""));
        }
        return steps;
    }

    public String getSolutionName() {
        return this.solutionName;
    }

    public void setSolutionName(String solutionName) {
        this.solutionName = solutionName;
    }

    public int getStepNumber() {
        return this.stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getStepName() {
        return this.stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getStepDescription() {
        return this.stepDescription;
    }

    public void setStepDescription(String stepDescription) {
        this.stepDescription = stepDescription;
    }

    public String getStepAlgorithm() {
        return this.stepAlgorithm;
    }

    public void setStepAlgorithm(String stepAlgorithm) {
        this.stepAlgorithm = stepAlgorithm;
    }

    public String getStepImageStart() {
        return this.stepImageStart;
    }

    public void setStepImageStart(String stepImageStart) {
        this.stepImageStart = stepImageStart;
    }

    public String getStepImageEnd() {
        return this.stepImageEnd;
    }

    public void setStepImageEnd(String stepImageEnd) {
        this.stepImageEnd = stepImageEnd;
    }

    public int compareTo(Object o) {
        return this.stepNumber - ((Steps) o).getStepNumber();
    }
}
