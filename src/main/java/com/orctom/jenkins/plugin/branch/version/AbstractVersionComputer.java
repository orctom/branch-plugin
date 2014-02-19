package com.orctom.jenkins.plugin.branch.version;

import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by CH on 2/18/14.
 */
public abstract class AbstractVersionComputer implements VersionComputer {

    public static final Pattern VERSION_PATTERN = Pattern.compile("^([\\d\\.]+).*");
    public static final Pattern UBUNTU_VERSION_PATTERN = Pattern.compile("^\\d{2}\\.\\d{2}\\.\\d{2}.*");

    protected final String rawVersion;
    protected final String versionDigits;
    protected final String jobName;

    public AbstractVersionComputer(String version, String jobName) {
        this.rawVersion = version;
        Matcher matcher = VERSION_PATTERN.matcher(version);
        if (matcher.matches()) {
            this.versionDigits = matcher.replaceAll("$1");
        } else {
            this.versionDigits = "1.0.0";
        }
        this.jobName = jobName;
    }

    public boolean isTrunk() {
        return !rawVersion.contains("-RC-")
                && !UBUNTU_VERSION_PATTERN.matcher(rawVersion).matches()
                && rawVersion.endsWith("SNAPSHOT");
    }

    public String getReleaseVersionDigits() {
        return versionDigits;
    }

    public String getReleaseVersion() {
        return versionDigits + "-SNAPSHOT";
    }

    public String getSnapshotVersion() {
        return versionDigits + "-SNAPSHOT";
    }

    protected String increaseVersionNumber(String s) {
        int n = Integer.valueOf(s).intValue() + 1;
        String value = String.valueOf(n);
        if (value.length() < s.length()) {
            // String was left-padded with zeros
            value = StringUtils.leftPad(value, s.length(), "0");
        }
        return value;
    }

    protected static String digits2VersionString(List<String> digits) {
        return StringUtils.join(digits, '.');
    }
}
