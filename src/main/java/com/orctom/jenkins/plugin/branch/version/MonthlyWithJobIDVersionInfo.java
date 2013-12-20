package com.orctom.jenkins.plugin.branch.version;

import org.apache.maven.shared.release.versions.VersionInfo;
import org.apache.maven.shared.release.versions.VersionParseException;

import java.util.Date;

/**
 * Created by CH on 12/17/13.
 */
public class MonthlyWithJobIDVersionInfo extends MonthlyVersionInfo {

    public MonthlyWithJobIDVersionInfo(String version, String jobName) throws VersionParseException {
        super(version, jobName);
    }

    @Override
    public VersionInfo getNextVersion() {
        return super.getNextVersion();
    }

    @Override
    protected String incrementVersionString(String s) {
        return super.incrementVersionString(s);
    }

    @Override
    public String getSnapshotVersionString() {
        return super.getSnapshotVersionString();
    }

    @Override
    public String getReleaseVersionString() {
        return releaseVersionFormatter.format(new Date()) + "-" + jobName.replaceAll("\\_.*", "") + "-SNAPSHOT";
    }
}
