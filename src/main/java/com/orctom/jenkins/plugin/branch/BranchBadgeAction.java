package com.orctom.jenkins.plugin.branch;

import hudson.model.BuildBadgeAction;

/**
 * Created by CH on 12/23/13.
 */
public class BranchBadgeAction implements BuildBadgeAction {

    private String branchVersion;
    private String trunkVersion;

    public String getTooltip() {
        return "trunk: " + trunkVersion + ", branch: " + branchVersion;
    }

    public String getBranchVersion() {
        return branchVersion;
    }

    public String getTrunkVersion() {
        return trunkVersion;
    }

    public BranchBadgeAction(String branchVersion, String trunkVersion) {
        this.branchVersion = branchVersion;
        this.trunkVersion = trunkVersion;
    }

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return null;
    }

    public String getUrlName() {
        return null;
    }
}
