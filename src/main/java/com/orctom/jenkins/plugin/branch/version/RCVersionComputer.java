package com.orctom.jenkins.plugin.branch.version;

/**
 * Created by CH on 2/18/14.
 */
public class RCVersionComputer extends DefaultVersionComputer {

    public RCVersionComputer(String version, String jobName) {
        super(version, jobName);
    }

    @Override
    public String getReleaseVersion() {
        return versionDigits + "-RC-SNAPSHOT";
    }
}
