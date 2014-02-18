package com.orctom.jenkins.plugin.branch.version;

import org.apache.commons.lang.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by CH on 2/18/14.
 */
public class DateVersionComputer extends AbstractVersionComputer {

    protected static final SimpleDateFormat NEXT_VERSION_FORMATTER = new SimpleDateFormat("yy.MM");
    protected static final SimpleDateFormat RELEASE_VERSION_FORMATTER = new SimpleDateFormat("yy.MM.dd");
    protected static final SimpleDateFormat RELEASE_VERSION_DIGITS_FORMATTER = new SimpleDateFormat("yyyyMMdd");

    public DateVersionComputer(String version, String jobName) {
        super(version, jobName);
    }

    public VersionComputer getNextVersion() {
        Date date = DateUtils.addMonths(new Date(), 1);
        String nextVersion = NEXT_VERSION_FORMATTER.format(date);
        return new DateVersionComputer(nextVersion, jobName);
    }

    public VersionComputer getNextIncrementalVersion() {
        return new DateVersionComputer(getReleaseVersion(), jobName);
    }

    public String getReleaseVersion() {
        return RELEASE_VERSION_FORMATTER.format(new Date()) + "-SNAPSHOT";
    }

    @Override
    public String getReleaseVersionDigits() {
        return RELEASE_VERSION_DIGITS_FORMATTER.format(new Date());
    }
}
