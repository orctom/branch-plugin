package com.orctom.jenkins.branch;

/**
 * Created by CH on 12/17/13.
 */
public class Test {
    public static void main(String[] args) {
        System.out.println("IOD_TRUNK".replaceAll("\\_.*", ""));
        System.out.println("WWW_TRUNK".replaceAll("\\_.*", ""));
        System.out.println("BSD_BRANCH".replaceAll("\\_.*", ""));
        System.out.println("VKG".replaceAll("\\_.*", ""));
    }
}
