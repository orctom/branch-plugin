package com.orctom.jenkins.plugin.branch.version;

import java.util.Date;

/**
 * Created by CH on 2/18/14.
 */
public class DateWithJobIDStaticTrunkVersionComputer extends DateVersionComputer {

    public DateWithJobIDStaticTrunkVersionComputer(String version, String jobName) {
        super(version, jobName);
    }

    @Override
    public String getReleaseVersion() {
        return RELEASE_VERSION_FORMATTER.format(new Date()) + "-" + jobName.replaceAll("\\_.*", "") + "-SNAPSHOT";
    }

    @Override
    public VersionComputer getNextVersion() {
        return this;
    }

    @Override
    public String getSnapshotVersion() {
        return rawVersion;
    }
}
