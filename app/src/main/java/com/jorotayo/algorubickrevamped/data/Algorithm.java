package com.jorotayo.algorubickrevamped.data;

import java.util.Comparator;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class Algorithm {

    @Id
    public long id;
    public String alg_name;
    public String alg;
    public String alg_description;
    public String algorithm_icon;
    public String category;
    public int practiced_correctly_int;
    public int practiced_number_int;
    public boolean custom_alg;
    public boolean favourite_alg;
    public boolean selected_alg;
    public boolean learnt;
    public long createdTime;

    public Algorithm(String alg_name, String alg, String alg_description, String algorithm_icon, String category, int practiced_correctly_int, int practiced_number_int, boolean custom_alg, boolean favourite_alg, boolean selected_alg, boolean learnt, long createdTime) {
        this.alg_name = alg_name;
        this.alg = alg;
        this.alg_description = alg_description;
        this.algorithm_icon = algorithm_icon;
        this.category = category;
        this.practiced_correctly_int = practiced_correctly_int;
        this.practiced_number_int = practiced_number_int;
        this.custom_alg = custom_alg;
        this.favourite_alg = favourite_alg;
        this.selected_alg = selected_alg;
        this.learnt = learnt;
        this.createdTime = createdTime;
    }

    public Algorithm() {
        //empty Constructor
    }

    public String getAlg() {
        return alg;
    }

    public void setAlg(String alg) {
        this.alg = alg;
    }

    public String getAlg_description() {
        return alg_description;
    }

    public void setAlg_description(String alg_description) {
        this.alg_description = alg_description;
    }

    public String getAlg_name() {
        return alg_name;
    }

    public void setAlg_name(String alg_name) {
        this.alg_name = alg_name;
    }

    public String getAlgorithm_icon() {
        return algorithm_icon;
    }

    public void setAlgorithm_icon(String algorithm_icon) {
        this.algorithm_icon = algorithm_icon;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isCustom_alg() {
        return custom_alg;
    }

    public void setCustom_alg() {
        this.custom_alg ^= custom_alg;
    }

    public boolean isFavourite_alg() {
        return favourite_alg;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isLearnt() {
        return learnt;
    }

    public void setLearnt() {
        this.learnt ^= learnt;
    }

    public int getPracticed_correctly_int() {
        return practiced_correctly_int;
    }

    public void setPracticed_correctly_int(int practiced_correctly_int) {
        this.practiced_correctly_int = practiced_correctly_int;
    }

    public int getPracticed_number_int() {
        return practiced_number_int;
    }

    public void setPracticed_number_int(int practiced_number_int) {
        this.practiced_number_int = practiced_number_int;
    }

    public boolean isSelected_alg() {
        return selected_alg;
    }

    public void toggleSelectedAlg() {
        this.selected_alg ^= true;
    }

    public boolean getSelected_alg() {
        return selected_alg;
    }

    public void setSelected_alg(boolean selected) {
        this.selected_alg = selected;
    }

    public void setFavourite_alg() {
        this.favourite_alg ^= favourite_alg;
    }

    public void setCreatedTime() { this.createdTime = System.currentTimeMillis(); }

    public Long getCreatedTime() { return this.createdTime;}

    public static class CompareAlgorithmName implements Comparator<Algorithm>{
        @Override
        public int compare (Algorithm algorithm1, Algorithm algorithm2){
            return algorithm1.getAlg_name().compareTo(algorithm2.alg_name);
        }
    }

    public static class CompareCreatedDate implements Comparator<Algorithm>{
        @Override
        public int compare (Algorithm algorithm1, Algorithm algorithm2){
            return algorithm1.getCreatedTime().compareTo(algorithm2.createdTime);
        }
    }

    public static class CompareCategory implements Comparator<Algorithm>{
        @Override
        public int compare (Algorithm algorithm1, Algorithm algorithm2){
            return algorithm1.getCategory().compareTo(algorithm2.getCategory());
        }
    }
}

