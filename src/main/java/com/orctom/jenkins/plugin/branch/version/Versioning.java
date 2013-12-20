package com.orctom.jenkins.plugin.branch.version;

/**
 * Created by CH on 12/19/13.
 */
public interface Versioning {

    Versioning getNextVersion();

    String getReleaseVersion();

    String getSnapshotVersion();

    String getBranchVersion();
}
