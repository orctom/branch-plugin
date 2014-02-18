package com.orctom.jenkins.plugin.branch;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by CH on 2/18/14.
 */
public class Test {
    public static final Pattern VERSION_PATTERN = Pattern.compile("^([\\d\\.]+).*");

    public static void main(String[] args) {
        List<String> list = Arrays.asList(StringUtils.split("1.0.0", '.'));
        for (String str : list) {
            System.out.println(str);
        }
    }
}
