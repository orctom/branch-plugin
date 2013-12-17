package com.orctom.jenkins.plugin.branch.version;

import org.apache.maven.shared.release.versions.VersionInfo;

/**
 * Created by CH on 12/17/13.
 */

public enum VersionComputer {

    DEFAULT("DEFAULT", "Default", VersionInfo.class),
    RC("RELEASE_CANDIDATE", "Release Candidate", RCVersionInfo.class),
    MONTHLY("MONTHLY", "Ubuntu like Monthly", MonthlyVersionInfo.class),
    MONTHLY_WITH_JOB_ID("MONTHLY_WITH_JOB_ID", "Ubuntu like Monthly with Job Name/ID", MonthlyWithJobIDVersionInfo.class);

    private String name;
    private String description;
    private Class<? extends VersionInfo> versionInfoClass;

    VersionComputer(String name, String description, Class<? extends VersionInfo> versionInfoClass) {
        this.name = name;
        this.description = description;
        this.versionInfoClass = versionInfoClass;
    }

    public String getName() {
        return name;
    }

    public Class<? extends VersionInfo> getVersionInfoClass() {
        return versionInfoClass;
    }

    public String getDescription() {
        return description;
    }
}