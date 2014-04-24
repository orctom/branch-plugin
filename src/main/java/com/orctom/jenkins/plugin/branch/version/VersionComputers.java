package com.orctom.jenkins.plugin.branch.version;

/**
 * Created by CH on 12/17/13.
 */

public enum VersionComputers {

    DEFAULT("DEFAULT", "Default", DefaultVersionComputer.class),
    RELEASE_CANDIDATE("RELEASE_CANDIDATE", "x.x.x-RC-SNAPSHOT", RCVersionComputer.class),
    MONTHLY("MONTHLY", "trunk: yy.MM-SNAPSHOT, branch: yy.MM.dd-SNAPSHOT", DateVersionComputer.class),
    MONTHLY_WITH_JOB_ID("MONTHLY_WITH_JOB_ID", "trunk: yy.MM-SNAPSHOT, branch: yy.MM.dd-${first world in Job name}-SNAPSHOT", DateWithJobIDVersionComputer.class),
    MONTHLY_WITH_JOB_ID_STATIC_TRUNK("MONTHLY_WITH_JOB_ID_STATIC_TRUNK", "trunk: no change, branch: yy.MM.dd-${first world in Job name}-SNAPSHOT", DateWithJobIDStaticTrunkVersionComputer.class);

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