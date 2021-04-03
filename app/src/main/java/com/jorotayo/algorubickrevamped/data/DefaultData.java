package com.jorotayo.algorubickrevamped.data;

import java.util.ArrayList;
import java.util.Arrays;

public class DefaultData {
    ArrayList<String> alg_names = new ArrayList<>(Arrays.asList("Right Sexy Move", "Left Sexy Move", "H-Perm", "Z-Perm", "T-Perm", "J1-Perm", "J2-Perm", "N1-Perm", "N2-Perm", "V-Perm", "Y-Perm", "E-perm", "F-Perm", "R1-Perm", "R2-Perm"));
    ArrayList<String> algs = new ArrayList<>(Arrays.asList("R,U,R',U'", "L',U',L,U", "M2,U,M2,U2,M2,U,M2", "M2,U,M2,U,M',U2,M2,U2,M',U2", "R,U,R',U',R,F,R2,U',R',U',R',U,R',F'", "R',U,L',U2,R,U',R',U2,R,L,U'", "R,U,R',F',R,U,R',U',R',F,R2,U',R',U'", "L,U',R,U2,L',U,R',L,U',R,U2,L',U,R',U", "R',U,L',U2,R,U',L,R',U,L',U2,R,U',L,U'", "R',U,R',d',R',F',R2,U',R',U,R',F,R,F", "F,R,U',R',U',R,U,R',F',R,U,R',U',R',F,R,F'", "X',R,U',R',D,R,U,R',u2,R',U,R,D,R',U',R", "R',U2,R',d',R',F',R2,U',R',U,R',F,R,U',F", "L,U2',L',U2',L,F',L',U',L,U,L,F,L2',U", "R',U2,R,U2,R',F,R,U,R',U',R',F',R2,U'"));
    ArrayList<String> categories = new ArrayList<>(Arrays.asList("Triggers", "Triggers", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL", "PLL"));
    ArrayList<String> descriptions = new ArrayList<>(Arrays.asList("Insert bottom right corner and trigger", "Insert bottom left corner and trigger", "Swap opposite Edges (4e)", "Swap adjacent Edges (4e)", "Swap two opposite edges and two corners (2e2c)", "Swap two opposite corners, swap two adjacent edges (2c2e)", "Swap two corners, swap two adjacent edges (2e2c)", "Swap diagonal corner and opposite edge, left to right (2e2c)", "wap diagonal corner and opposite edge, Right to left (2e2c)", "Swap inline adjacent edge and diagonal corner (2e2c)", "Cross shaped swap diagonal corner and adjacent edge (2e2c)", "Swap opposite corners (4c)", "Swap inline 2 edges and 2 corners (2e2c)", "Swap opposite corners and adjacent edge (2e2c)", "Swap opposite corners and adjacent edge (2e2c)"));
    ArrayList<String> images = new ArrayList<>(Arrays.asList("R.drawable.right_edge", "R.drawable.left_edge", "R.drawable.h_perm", "R.drawable.z_perm", "R.drawable.t_perm", "R.drawable.j1_perm", "R.drawable.j2_perm", "R.drawable.n1_perm", "R.drawable.n2_perm", "R.drawable.v_perm", "R.drawable.y_perm", "R.drawable.e_perm", "R.drawable.f_perm", "R.drawable.r1_perm", "R.drawable.r2_perm"));

    public DefaultData() {
    }

    public ArrayList<Algorithm> getDefaultAlgs() {
        ArrayList<Algorithm> algorithmArrayList = new ArrayList<>();
        for (int i = 0; i < this.alg_names.size(); i++) {
           // Algorithm algorithm = new Algorithm((String) this.alg_names.get(i), this.algs.get(i), (String) this.descriptions.get(i), (String) this.categories.get(i), false, true, false, 0, 0, false, (String) this.images.get(i));
            Algorithm algorithm = new Algorithm(alg_names.get(i), algs.get(i),descriptions.get(i), images.get(i), categories.get(i), 0, 0, false, false, false, false);
        }
        return algorithmArrayList;
    }
}
