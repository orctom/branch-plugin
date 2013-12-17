package com.orctom.jenkins.plugin.branch;

import com.orctom.jenkins.plugin.branch.version.VersionComputer;
import org.apache.maven.shared.release.versions.DefaultVersionInfo;
import org.apache.maven.shared.release.versions.VersionInfo;
import org.apache.maven.shared.release.versions.VersionParseException;

/**
 * Created by CH on 12/17/13.
 */
public class VersionComputerFactory {

    public static VersionInfo getVersionComputer(String versioningMode, String version) {
        VersionComputer computer = Enum.valueOf(VersionComputer.class, versioningMode);

        try {
            VersionInfo versionComputer = computer.getVersionInfoClass().getConstructor(String.class).newInstance(version);
            return versionComputer;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                return new DefaultVersionInfo(version);
            } catch (VersionParseException e1) {
                e1.printStackTrace();
                return null;
            }
        }
    }
}
