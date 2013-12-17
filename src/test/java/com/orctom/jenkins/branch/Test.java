package com.orctom.jenkins.branch;

/**
 * Created by CH on 12/17/13.
 */
public class Test {
    public static void main(String[] args) {
        String name = "WWW-trunk_20234234";
        System.out.println(name.replaceAll("(?i)TRUNK", "BRANCH").replaceAll("\d"));
    }
}
