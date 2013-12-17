package com.orctom.jenkins.plugin.branch.version;

import org.apache.maven.shared.release.versions.DefaultVersionInfo;
import org.apache.maven.shared.release.versions.VersionInfo;
import org.apache.maven.shared.release.versions.VersionParseException;

/**
 * Created by CH on 12/17/13.
 */
public class RCVersionInfo extends DefaultVersionInfo {

    public RCVersionInfo(String version) throws VersionParseException {
        super(version);
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
        return super.getReleaseVersionString();
    }
}
