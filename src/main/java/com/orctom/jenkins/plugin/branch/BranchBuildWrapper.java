package com.orctom.jenkins.plugin.branch;

import com.orctom.jenkins.plugin.branch.builder.CreateBranchJobBuilder;
import com.orctom.jenkins.plugin.branch.version.VersionComputer;
import hudson.Extension;
import hudson.Launcher;
import hudson.maven.AbstractMavenProject;
import hudson.maven.MavenModuleSet;
import hudson.model.*;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.RunList;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by CH on 12/11/13.
 */
public class BranchBuildWrapper extends BuildWrapper {

    private String branchBasePath;
    private String defaultVersioningMode;

    private final int numberOfReleaseBuildsToKeep = 2;

    @DataBoundConstructor
    public BranchBuildWrapper(String branchBasePath, String defaultVersioningMode) {
        this.branchBasePath = branchBasePath;
        this.defaultVersioningMode = defaultVersioningMode;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Environment setUp(final AbstractBuild build, final Launcher launcher, final BuildListener listener) throws IOException, InterruptedException {
        if (!isBranchBuild(build)) {
            return new Environment() {};
        }

        BranchArgumentsAction args = build.getAction(BranchArgumentsAction.class);
        StringBuilder buildGoals = new StringBuilder();

        final String trunkJobName = args.getTrunkJobName();
        final String branchJobName = args.getBranchJobName();
        final String branchURL = args.getBranchURL();
        final String branchBase = args.getBranchBase();
        final String branchName = args.getBranchName();
        final String branchVersion = args.getBranchVersion();
        final String trunkVersion = args.getTrunkVersion();
        final boolean isCreateBranchJob = args.isCreateBranchJob();
        final boolean isClearTriggers = args.isClearTriggers();

        buildGoals.append("release:clean release:branch")
                .append(" -D").append("scmCommentPrefix=[branching-plugin]")
                .append(" -D").append("updateBranchVersions=true")
                .append(" -D").append("updateWorkingCopyVersions=true")
                .append(" -D").append("autoVersionSubmodules=true")
                .append(" -D").append("remoteTagging=false")
                .append(" -D").append("suppressCommitBeforeBranch=true")
                .append(" -D").append("branchBase=").append(branchBase)
                .append(" -D").append("branchName=").append(branchName)
                .append(" -D").append("releaseVersion=").append(branchVersion)
                .append(" -D").append("developmentVersion=").append(trunkVersion);

        if (StringUtils.isNotEmpty(args.getScmUserName()) && StringUtils.isNotEmpty(args.getScmPassword())) {
            buildGoals.append(" -D").append("username=").append(args.getScmUserName());
            buildGoals.append(" -D").append("password=").append(args.getScmPassword());
        }

        build.addAction(new BranchArgumentInterceptorAction(buildGoals.toString()));
        build.addAction(new BranchBadgeAction(args.getBranchVersion(), args.getTrunkVersion()));

        return new Environment() {

			@Override
            public boolean tearDown(AbstractBuild bld, BuildListener lstnr) throws IOException, InterruptedException {
                boolean retVal = true;

                int buildsKept = 0;
                if (bld.getResult() != null && bld.getResult().isBetterOrEqualTo(Result.SUCCESS)) {
                    // keep this build.
                    lstnr.getLogger().println("[Branch] keep this branch build.");
                    bld.keepLog();
                    buildsKept++;

                    // the value may have changed since a previous release so go searching...
                    for (Run run : (RunList<? extends Run>) (bld.getProject().getBuilds())) {
                    	lstnr.getLogger().println(String.format("[Branch] checking build %s", run.getNumber()));
                        if (isSuccessfulBranchBuild(run)) {
                            if (bld.getNumber() != run.getNumber()) { // not sure we still need this check..
                                if (shouldKeepBuildNumber(numberOfReleaseBuildsToKeep, buildsKept)) {
                                    buildsKept++;
                                    if (!run.isKeepLog()) {
                                        lstnr.getLogger().println("[Branch] keep branch build: " + run.getNumber());
                                        run.keepLog(true);
                                    }
                                } else {
                                    if (run.isKeepLog()) {
                                        lstnr.getLogger().println("[Branch] removing keep branch build: " + run.getNumber());
                                        run.keepLog(false);
                                    }
                                }
                            }
                        }
                    }

                    if (isCreateBranchJob) {
                        new CreateBranchJobBuilder(trunkJobName, branchJobName, branchURL, isClearTriggers).perform(build, launcher, lstnr);
                    }
                }

                return retVal;
            }

            /**
             * evaluate if the specified build is a successful release build (not including dry runs)
             * @param run the run to check
             * @return <code>true</code> if this is a successful release build that is not a dry run.
             */
            private boolean isSuccessfulBranchBuild(Run run) {
                BranchBadgeAction a = run.getAction(BranchBadgeAction.class);
                if (a != null && !run.isBuilding() && run.getResult().isBetterOrEqualTo(Result.SUCCESS)) {
                    return true;
                }
                return false;
            }

            private boolean shouldKeepBuildNumber(int numToKeep, int numKept) {
                if (numToKeep == -1) {
                    return true;
                }
                return numKept < numToKeep;
            }
        };
    }

    @SuppressWarnings("rawtypes")
	@Override
    public Collection<? extends Action> getProjectActions(AbstractProject job) {
        final BranchAction branchAction = new BranchAction((MavenModuleSet) job);
        return Arrays.asList(branchAction);
    }

    @SuppressWarnings("unchecked")
	private boolean isBranchBuild(@SuppressWarnings("rawtypes") AbstractBuild build) {
        return (build.getCause(BranchCause.class) != null);
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    public String getBranchBasePath() {
        return branchBasePath;
    }

    public String getDefaultVersioningMode() {
        return defaultVersioningMode;
    }

    @Extension
    public static class DescriptorImpl extends BuildWrapperDescriptor {

        public DescriptorImpl() {
            super(BranchBuildWrapper.class);
            load();
        }

        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return (item instanceof AbstractMavenProject);
        }

        @Override
        public String getDisplayName() {
            return Messages.Wrapper_DisplayName();
        }

        public List<VersionComputer> getVersioningModes() {
            return new ArrayList<VersionComputer>(Arrays.asList(VersionComputer.values()));
        }
    }
}
