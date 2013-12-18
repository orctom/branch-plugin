package com.orctom.jenkins.branch;

/**
 * Created by CH on 12/17/13.
 */
public class Test {
    public static void main(String[] args) {
        String name = "BARZAARVOICE_TRUNK";
        System.out.println(name.replaceAll("(?i)TRUNK", "BRANCH").replaceAll("([_-]?\\d)", ""));
    }
}
