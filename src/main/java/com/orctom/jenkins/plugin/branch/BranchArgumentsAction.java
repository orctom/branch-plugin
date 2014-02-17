package com.orctom.jenkins.plugin.branch;

import hudson.model.Action;

/**
 * Created by CH on 12/23/13.
 */
public class BranchArgumentsAction implements Action {

    private String scmUserName;
    private String scmPassword;

    private String branchVersion;
    private String trunkVersion;
    private String currentVersion;

    private String branchJobName;
    private String branchBase;
    private String branchName;

    private String currentJobName;
    private boolean isCreateBranchJob;
    private boolean isClearTriggers;

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return null;
    }

    public String getUrlName() {
        return null;
    }

    public String getScmUserName() {
        return scmUserName;
    }

    public void setScmUserName(String scmUserName) {
        this.scmUserName = scmUserName;
    }

    public String getScmPassword() {
        return scmPassword;
    }

    public void setScmPassword(String scmPassword) {
        this.scmPassword = scmPassword;
    }

    public String getBranchVersion() {
        return branchVersion;
    }

    public void setBranchVersion(String branchVersion) {
        this.branchVersion = branchVersion;
    }

    public String getTrunkVersion() {
        return trunkVersion;
    }

    public void setTrunkVersion(String trunkVersion) {
        this.trunkVersion = trunkVersion;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getBranchJobName() {
        return branchJobName;
    }

    public void setBranchJobName(String branchJobName) {
        this.branchJobName = branchJobName;
    }

    public String getBranchBase() {
        return branchBase;
    }

    public void setBranchBase(String branchBase) {
        this.branchBase = branchBase;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchURL() {
        if (branchBase.endsWith("/")) {
            return branchBase + branchName;
        } else {
            return branchBase + "/" + branchName;
        }
    }

    public String getCurrentJobName() {
        return currentJobName;
    }

    public void setCurrentJobName(String currentJobName) {
        this.currentJobName = currentJobName;
    }

    public boolean isCreateBranchJob() {
        return isCreateBranchJob;
    }

    public void setCreateBranchJob(boolean isCreateBranchJob) {
        this.isCreateBranchJob = isCreateBranchJob;
    }

    public boolean isClearTriggers() {
        return isClearTriggers;
    }

    public void setClearTriggers(boolean isClearTriggers) {
        this.isClearTriggers = isClearTriggers;
    }
}
