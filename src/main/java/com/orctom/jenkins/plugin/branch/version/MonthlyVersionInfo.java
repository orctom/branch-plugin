package com.orctom.jenkins.plugin.branch.version;

import org.apache.commons.lang.time.DateUtils;
import org.apache.maven.shared.release.versions.VersionInfo;
import org.apache.maven.shared.release.versions.VersionParseException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by CH on 12/17/13.
 */
public class MonthlyVersionInfo extends DefaultVersionInfo {

    private static Logger LOGGER = Logger.getLogger(MonthlyVersionInfo.class.getName());

    protected static SimpleDateFormat nextVersionFormatter = new SimpleDateFormat("yy.MM");
    protected static SimpleDateFormat releaseVersionFormatter = new SimpleDateFormat("yy.MM.dd");

    public MonthlyVersionInfo(String version, String jobName) throws VersionParseException {
        super(version, jobName);
    }

    @Override
    public VersionInfo getNextVersion() {
        Date date = DateUtils.addMonths(new Date(), 1);
        String nextVersion = nextVersionFormatter.format(date) + "-SNAPSHOT";

        MonthlyVersionInfo version = null;
        try {
            version = new MonthlyVersionInfo(nextVersion, jobName);
        } catch (VersionParseException e) {
            LOGGER.log(Level.WARNING, "Failed to calculate next version", e);
        }
        return version;
    }

    @Override
    public String getSnapshotVersionString() {
        return strVersion;
    }

    @Override
    public String getReleaseVersionString() {
        return releaseVersionFormatter.format(new Date()) + "-SNAPSHOT";
    }
}
