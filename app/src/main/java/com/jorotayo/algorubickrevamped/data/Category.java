package com.jorotayo.algorubickrevamped.data;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class Category {

    @Id
    public long id;
    public String category_name;

    public Category(String category_name) {
        this.category_name = category_name;
    }

    public Category(){

    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String name) {
        this.category_name = name;
    }
}
