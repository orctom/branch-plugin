package com.orctom.jenkins.plugin.branch.version;

/**
 * Created by CH on 12/17/13.
 */

public enum VersionComputers {

    DEFAULT("DEFAULT", "Default", DefaultVersionComputer.class),
    RELEASE_CANDIDATE("RELEASE_CANDIDATE", "Release Candidate", RCVersionComputer.class),
    MONTHLY("MONTHLY", "Ubuntu like Monthly", DateVersionComputer.class),
    MONTHLY_WITH_JOB_ID("MONTHLY_WITH_JOB_ID", "Ubuntu like Monthly with Job Name/ID", DateWithJobIDVersionComputer.class);

    private String name;
    private String description;
    private Class<? extends VersionComputer> versionComputerClass;

    VersionComputers(String name, String description, Class<? extends VersionComputer> versionComputerClass) {
        this.name = name;
        this.description = description;
        this.versionComputerClass = versionComputerClass;
    }

    public String getName() {
        return name;
    }

    public Class<? extends VersionComputer> getVersionComputerClass() {
        return versionComputerClass;
    }

    public String getDescription() {
        return description;
    }
}