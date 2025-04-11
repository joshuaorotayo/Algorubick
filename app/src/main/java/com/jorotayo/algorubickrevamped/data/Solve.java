package com.jorotayo.algorubickrevamped.data;

import android.webkit.DateSorter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

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

    public Solve (String solve_cube_size, String solve_scramble, String solve_time, int solve_milliseconds, String solve_date) {
        this.solve_cube_size = solve_cube_size;
        this.solve_date = solve_date;
        this.solve_time = solve_time;
        this.solve_milliseconds = solve_milliseconds;
        this.solve_scramble = solve_scramble;
    }

    public Solve() {
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getSolve_cube_size()
    {
        return solve_cube_size;
    }

    public void setSolve_cube_size(String solve_cube_size) {
        this.solve_cube_size = solve_cube_size;
    }

    public String getSolve_date()
    {
        return solve_date;
    }

    public Date getSolve_dateDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.ENGLISH);

        try {
             return formatter.parse(solve_date);
        } catch (ParseException e) {
            return new Date(0);
        }
    }

    public void setSolve_date(String solve_date)
    {
        this.solve_date = solve_date;
    }

    public int getSolve_milliseconds()
    {
        return solve_milliseconds;
    }

    public void setSolve_milliseconds(int solve_milliseconds) {
        this.solve_milliseconds = solve_milliseconds;
    }

    public String getSolve_scramble()
    {
        return solve_scramble;
    }

    public void setSolve_scramble(String solve_scramble)
    {
        this.solve_scramble = solve_scramble;
    }

    public String getSolve_time()
    {
        return solve_time;
    }

    public void setSolve_time(String solve_time)
    {
        this.solve_time = solve_time;
    }


    public static class CompareSolvedDate implements Comparator<Solve>{
        @Override
        public int compare(Solve solve1, Solve solve2) {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.ENGLISH);

            Date solve1Date, solve2Date;
            try {
                solve1Date = formatter.parse(solve1.solve_date);
            } catch (ParseException e) {
                solve1Date = new Date(0);
            }

            try {
                solve2Date = formatter.parse(solve2.solve_date);
            } catch (ParseException e) {
                solve2Date = new Date(0);
            }

            assert solve1Date != null;
            return solve1Date.compareTo(solve2Date);
        }
    }

/*    public static class CompareSolveTime implements Comparator<Solve>{
        @Override
        public int compare(Solve solve1, Solve solve2) {
            return Long.compare(solve1.solve_milliseconds, solve2.solve_milliseconds);
        }
    }

    public static class CompareCubeSize implements Comparator<Solve>{
        @Override
        public int compare(Solve solve1, Solve solve2) {
            return solve1.solve_cube_size.compareTo(solve2.solve_cube_size);
        }
    }*/
}
