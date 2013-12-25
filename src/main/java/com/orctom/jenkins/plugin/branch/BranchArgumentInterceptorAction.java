package com.orctom.jenkins.plugin.branch;

import hudson.maven.MavenArgumentInterceptorAction;
import hudson.maven.MavenModuleSetBuild;
import hudson.util.ArgumentListBuilder;

/**
 * Created by CH on 12/23/13.
 */
public class BranchArgumentInterceptorAction implements MavenArgumentInterceptorAction {

    private String goalsAndOptions;

    public BranchArgumentInterceptorAction(String goalsAndOptions) {
        this.goalsAndOptions = goalsAndOptions;
    }

    public String getGoalsAndOptions(MavenModuleSetBuild build) {
        return goalsAndOptions;
    }

    public ArgumentListBuilder intercept(ArgumentListBuilder mavenargs, MavenModuleSetBuild build) {
        return null;
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
