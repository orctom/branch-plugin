package com.orctom.jenkins.plugin.branch.version;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by CH on 2/18/14.
 */
public class DefaultVersionComputer extends AbstractVersionComputer {

    protected final List<String> digits;

    public DefaultVersionComputer(String version, String jobName) {
        super(version, jobName);
        digits = parseDigits(versionDigits);
    }

    private List<String> parseDigits(String versionDigits) {
        return Arrays.asList(StringUtils.split(versionDigits, '.'));
    }

    public VersionComputer getNextVersion() {
        List<String> _digits = new ArrayList<String>(this.digits);
        if (_digits.size() >= 3) {
            _digits.set(_digits.size() - 2, increaseVersionNumber(_digits.get(_digits.size() - 2)));
            _digits.set(_digits.size() - 1, "0");
        }
        if (2 == _digits.size()) {
            _digits.set(1, increaseVersionNumber(_digits.get(1)));
        }
        if (1 == _digits.size()) {
            _digits.add("1");
        }

        return new DefaultVersionComputer(digits2VersionString(_digits), jobName);
    }

    public VersionComputer getNextIncrementalVersion() {
        List<String> _digits = new ArrayList<String>(this.digits);
        if (1 == _digits.size()) {
            _digits.add("0");
        }
        _digits.set(_digits.size() - 1, increaseVersionNumber(_digits.get(_digits.size() - 1)));

        return new DefaultVersionComputer(digits2VersionString(_digits), jobName);
    }
}
