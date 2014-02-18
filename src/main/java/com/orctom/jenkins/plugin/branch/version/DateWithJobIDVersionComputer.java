package com.orctom.jenkins.plugin.branch.version;

import java.util.Date;

/**
 * Created by CH on 2/18/14.
 */
public class DateWithJobIDVersionComputer extends DateVersionComputer {

    public DateWithJobIDVersionComputer(String version, String jobName) {
        super(version, jobName);
    }

    @Override
    public String getReleaseVersion() {
        return RELEASE_VERSION_FORMATTER.format(new Date()) + "-" + jobName.replaceAll("\\_.*", "") + "-SNAPSHOT";
    }
}
