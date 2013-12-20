package com.orctom.jenkins.plugin.branch.version;

import org.apache.maven.shared.release.versions.VersionInfo;
import org.apache.maven.shared.release.versions.VersionParseException;

/**
 * Created by CH on 12/17/13.
 */
public class VersionComputerFactory {

    public static VersionInfo getVersionComputer(String versioningMode, String currentVersion, String jobName) {
        VersionComputer computer = Enum.valueOf(VersionComputer.class, versioningMode);

        try {
            VersionInfo versionComputer = computer.getVersionInfoClass().getConstructor(String.class, String.class).newInstance(currentVersion, jobName);
            return versionComputer;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                return new DefaultVersionInfo(currentVersion, jobName);
            } catch (VersionParseException e1) {
                e1.printStackTrace();
                return null;
            }
        }
    }
}
