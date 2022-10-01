package com.jorotayo.algorubickrevamped.data;

import java.util.ArrayList;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class Solution {
    public static int lastCreatorID = 0;
    @Id
    public long id;
    public String solutionCreator;
    public String solutionDescription;
    public String solutionIconLocation;
    public String solutionName;

    public Solution(String solutionName, String solutionCreator, String solutionDescription, String solutionIconLocation) {
        this.solutionName = solutionName;
        this.solutionCreator = solutionCreator;
        this.solutionDescription = solutionDescription;
        this.solutionIconLocation = solutionIconLocation;
    }

    public Solution(){

    }

    public static ArrayList<Solution> createSolutionList(int numSolutions) {
        ArrayList<Solution> contacts = new ArrayList();
        for (int i = 1; i <= numSolutions; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Solution Name ");
            int i2 = lastCreatorID + 1;
            lastCreatorID = i2;
            stringBuilder.append(i2);
            String stringBuilder2 = stringBuilder.toString();
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("Creator Name_");
            stringBuilder3.append(lastCreatorID);
            contacts.add(new Solution(stringBuilder2, stringBuilder3.toString(), "Description of the solution that will be created and listed",""));
        }
        return contacts;
    }

    public String getSolutionName() {
        return this.solutionName;
    }

    public void setSolutionName(String solutionName) {
        this.solutionName = solutionName;
    }

    public String getSolutionCreator() {
        return this.solutionCreator;
    }

    public void setSolutionCreator(String solutionCreator) {
        this.solutionCreator = solutionCreator;
    }

    public String getSolutionDescription() {
        return this.solutionDescription;
    }

    public void setSolutionDescription(String solutionDescription) {
        this.solutionDescription = solutionDescription;
    }

    public String getSolutionIconLocation() {
        return this.solutionIconLocation;
    }

    public void setSolutionIconLocation(String solutionIconLocation) {
        this.solutionIconLocation = solutionIconLocation;
    }

    public void selectSolution(String text) {
        this.solutionName = text;
    }
}
