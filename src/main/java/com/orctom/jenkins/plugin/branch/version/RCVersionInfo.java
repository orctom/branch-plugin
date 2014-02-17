package com.orctom.jenkins.plugin.branch.version;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.release.versions.VersionInfo;
import org.apache.maven.shared.release.versions.VersionParseException;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by CH on 12/17/13.
 */
public class RCVersionInfo extends DefaultVersionInfo {

    private static Logger LOGGER = Logger.getLogger(RCVersionInfo.class.getName());

    protected String jobName;

    public RCVersionInfo(String version, String jobName) throws VersionParseException {
        super(version, jobName);
    }

    @Override
    public VersionInfo getNextVersion() {
        DefaultVersionInfo version = null;
        if (digits != null) {
            List digits = new ArrayList(this.digits);
            String annotationRevision = this.annotationRevision;
            if (StringUtils.isNumeric(annotationRevision)) {
                annotationRevision = incrementVersionString(annotationRevision);
            } else {
                if (1 == digits.size()) {
                    digits.add("0");
                }

                digits.set(1, incrementVersionString((String) digits.get(1)));

                if (digits.size() > 2) {
                    for (int i = 2; i < digits.size() - 1; i++) {
                        digits.set(i, "0");
                    }
                }
            }

            version = new DefaultVersionInfo(digits, annotation, annotationRevision, buildSpecifier, annotationSeparator, annotationRevSeparator, buildSeparator);
        }
        return version;
    }

    public VersionInfo getNextIncrementalVersion() {
        DefaultVersionInfo version = null;
        if (digits != null) {
            List digits = new ArrayList(this.digits);
            String annotationRevision = this.annotationRevision;
            if (StringUtils.isNumeric(annotationRevision)) {
                annotationRevision = incrementVersionString(annotationRevision);
            } else {
                if (digits.size() <= 2) {
                    digits.add("1");
                } else {
                    digits.set( digits.size() - 1, incrementVersionString( (String) digits.get( digits.size() - 1 ) ) );
                }
            }

            version = new DefaultVersionInfo(digits, annotation, annotationRevision, buildSpecifier, annotationSeparator, annotationRevSeparator, buildSeparator);
        }
        return version;
    }

    @Override
    public String getSnapshotVersionString() {
        if (Artifact.SNAPSHOT_VERSION.equals(strVersion)) {
            return "1.0-SNAPSHOT";
        }

        if (!strVersion.endsWith(Artifact.SNAPSHOT_VERSION)) {
            return strVersion + "-SNAPSHOT";
        }

        return strVersion;
    }

    @Override
    public String getReleaseVersionString() {
        if (Artifact.SNAPSHOT_VERSION.equals(strVersion)) {
            return "1.0-RC-SNAPSHOT";
        }

        return strVersion.replaceAll("^([0-9\\.]+).*", "$1") + "-RC-SNAPSHOT";

    }
}
