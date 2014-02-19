package com.orctom.jenkins.plugin.branch.version;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CH on 2/18/14.
 */
public class RCVersionComputer extends DefaultVersionComputer {

    public RCVersionComputer(String version, String jobName) {
        super(version, jobName);
    }

    @Override
    public VersionComputer getNextIncrementalVersion() {
        List<String> _digits = new ArrayList<String>(this.digits);
        if (_digits.size() >= 3) {
            _digits.set(_digits.size() - 1, increaseVersionNumber(_digits.get(_digits.size() - 1)));
        }
        if (2 == _digits.size()) {
            _digits.add("1");
        }
        if (1 == _digits.size()) {
            _digits.add("0");
            _digits.add("1");
        }

        return new RCVersionComputer(digits2VersionString(_digits), jobName);
    }

    @Override
    public String getReleaseVersion() {
        return versionDigits + "-RC-SNAPSHOT";
    }
}
