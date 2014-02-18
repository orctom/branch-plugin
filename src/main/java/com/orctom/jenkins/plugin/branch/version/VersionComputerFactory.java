package com.orctom.jenkins.plugin.branch.version;

/**
 * Created by CH on 12/17/13.
 */
public class VersionComputerFactory {

    public static VersionComputer getVersionComputer(String versioningMode, String currentVersion, String jobName) {
        VersionComputers computer = Enum.valueOf(VersionComputers.class, versioningMode);

        try {
            VersionComputer versionComputer = computer.getVersionComputerClass().getConstructor(String.class, String.class).newInstance(currentVersion, jobName);
            return versionComputer;
        } catch (Exception e) {
            e.printStackTrace();
            return new DefaultVersionComputer(currentVersion, jobName);
        }
    }
}
