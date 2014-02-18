package com.orctom.jenkins.plugin.branch.version;

/**
 * Created by CH on 2/18/14.
 */
public interface VersionComputer {

    boolean isTrunk();
    VersionComputer getNextVersion();
    VersionComputer getNextIncrementalVersion();
    String getReleaseVersion();
    String getSnapshotVersion();
    String getReleaseVersionDigits();
}
