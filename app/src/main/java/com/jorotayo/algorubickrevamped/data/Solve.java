package com.jorotayo.algorubickrevamped.data;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class Solve {


    @Id
    public long id;
    public String solve_cube_size;
    public String solve_date;
    public int solve_milliseconds;
    public String solve_scramble;
    public String solve_time;

    public Solve(String solve_cube_size, String solve_scramble, String solve_time, int solve_milliseconds, String solve_date) {
        this.solve_cube_size = solve_cube_size;
        this.solve_date = solve_date;
        this.solve_time = solve_time;
        this.solve_milliseconds = solve_milliseconds;
        this.solve_scramble = solve_scramble;
    }

    public Solve() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSolve_cube_size() {
        return solve_cube_size;
    }

    public void setSolve_cube_size(String solve_cube_size) {
        this.solve_cube_size = solve_cube_size;
    }

    public String getSolve_date() {
        return solve_date;
    }

    public void setSolve_date(String solve_date) {
        this.solve_date = solve_date;
    }

    public int getSolve_milliseconds() {
        return solve_milliseconds;
    }

    public void setSolve_milliseconds(int solve_milliseconds) {
        this.solve_milliseconds = solve_milliseconds;
    }

    public String getSolve_scramble() {
        return solve_scramble;
    }

    public void setSolve_scramble(String solve_scramble) {
        this.solve_scramble = solve_scramble;
    }

    public String getSolve_time() {
        return solve_time;
    }

    public void setSolve_time(String solve_time) {
        this.solve_time = solve_time;
    }

}
