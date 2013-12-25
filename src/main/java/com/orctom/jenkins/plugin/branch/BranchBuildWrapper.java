package com.orctom.jenkins.plugin.branch;

import com.orctom.jenkins.plugin.branch.job.CreateBranchJobBuilder;
import com.orctom.jenkins.plugin.branch.version.VersionComputer;
import hudson.Extension;
import hudson.Launcher;
import hudson.Util;
import hudson.maven.*;
import hudson.model.*;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.RunList;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by CH on 12/11/13.
 */
public class BranchBuildWrapper extends BuildWrapper {

    private static Logger LOGGER = Logger.getLogger(BranchBuildWrapper.class.getName());

    private String branchBasePath;
    private String defaultVersioningMode;

    private final int numberOfReleaseBuildsToKeep = 2;

    @DataBoundConstructor
    public BranchBuildWrapper(String branchBasePath, String defaultVersioningMode) {
        this.branchBasePath = branchBasePath;
        this.defaultVersioningMode = defaultVersioningMode;
    }

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
                .append(" -D").append("updateBranchVersions=true")
                .append(" -D").append("updateWorkingCopyVersions=true")
                .append(" -D").append("autoVersionSubmodules=true")
                .append(" -D").append("remoteTagging=false")
                .append(" -D").append("suppressCommitBeforeBranch=true")
                .append(" -D").append("branchBase=").append(branchBase)
                .append(" -D").append("branchName=").append(branchName)
                .append(" -D").append("releaseVersion=").append(branchVersion)
                .append(" -D").append("developmentVersion=").append(trunkVersion);

        build.addAction(new BranchArgumentInterceptorAction(buildGoals.toString()));
        build.addAction(new BranchBadgeAction(args.getBranchVersion(), args.getTrunkVersion()));

        return new Environment() {

            @Override
            public boolean tearDown(@SuppressWarnings("rawtypes") AbstractBuild bld, BuildListener lstnr)
                    throws IOException, InterruptedException {
                boolean retVal = true;
                final MavenModuleSet mmSet = getModuleSet(bld);
                BranchArgumentsAction args = bld.getAction(BranchArgumentsAction.class);

                int buildsKept = 0;
                if (bld.getResult() != null && bld.getResult().isBetterOrEqualTo(Result.SUCCESS)) {
                    if (numberOfReleaseBuildsToKeep > 0 || numberOfReleaseBuildsToKeep == -1) {
                        // keep this build.
                        lstnr.getLogger().println("[Branch] assigning keep build to current build.");
                        bld.keepLog();
                        buildsKept++;
                    }

                    // the value may have changed since a previous release so go searching...
                    for (Run run : (RunList<? extends Run>) (bld.getProject().getBuilds())) {
                        LOGGER.info("checking build #{}" + run.getNumber());
                        if (isSuccessfulReleaseBuild(run)) {
                            LOGGER.info("build #{} was successful." + run.getNumber());
                            if (bld.getNumber() != run.getNumber()) { // not sure we still need this check..
                                if (shouldKeepBuildNumber(numberOfReleaseBuildsToKeep, buildsKept)) {
                                    buildsKept++;
                                    if (!run.isKeepLog()) {
                                        lstnr.getLogger().println(
                                                "[Branch] assigning keep build to build " + run.getNumber());
                                        run.keepLog(true);
                                    }
                                } else {
                                    if (run.isKeepLog()) {
                                        lstnr.getLogger().println(
                                                "[Branch] removing keep build from build " + run.getNumber());
                                        run.keepLog(false);
                                    }
                                }
                            }
                        } else {
                            LOGGER.info("build #{} was NOT successful release build." + run.getNumber());
                        }
                    }

                    if (isCreateBranchJob) {
                        new CreateBranchJobBuilder(trunkJobName, branchJobName, branchURL, isClearTriggers).perform(build, launcher, lstnr);
                    }
                }

                return retVal;
            }

            /**
             * evaluate if the specified build is a sucessful release build (not including dry runs)
             * @param run the run to check
             * @return <code>true</code> if this is a successful release build that is not a dry run.
             */
            private boolean isSuccessfulReleaseBuild(Run run) {
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

    @Override
    public Collection<? extends Action> getProjectActions(AbstractProject job) {
        final BranchAction branchAction = new BranchAction((MavenModuleSet) job);
        return Arrays.asList(branchAction);
    }

    private boolean isBranchBuild(@SuppressWarnings("rawtypes") AbstractBuild build) {
        return (build.getCause(BranchCause.class) != null);
    }

    private MavenModuleSet getModuleSet(AbstractBuild<?, ?> build) {
        if (build instanceof MavenBuild) {
            MavenBuild m2Build = (MavenBuild) build;
            MavenModule mm = m2Build.getProject();
            MavenModuleSet mmSet = mm.getParent();
            return mmSet;
        } else if (build instanceof MavenModuleSetBuild) {
            MavenModuleSetBuild m2moduleSetBuild = (MavenModuleSetBuild) build;
            MavenModuleSet mmSet = m2moduleSetBuild.getProject();
            return mmSet;
        } else {
            return null;
        }
    }

    @Override
    public DescriptorImpl getDescriptor() {
        // see Descriptor javadoc for more about what a descriptor is.
        return (DescriptorImpl) super.getDescriptor();
    }

    private Object readResolve() {
        LOGGER = Logger.getLogger(BranchBuildWrapper.class.getName());
        return this;
    }

    public String getBranchBasePath() {
        return branchBasePath;
    }

    public String getDefaultVersioningMode() {
        return defaultVersioningMode;
    }

    @Extension
    public static class DescriptorImpl extends BuildWrapperDescriptor {

        private String numberOfBranchBuildsToKeep = "2";

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

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            numberOfBranchBuildsToKeep = Util.fixEmpty(json.getString("numberOfBranchBuildsToKeep"));
            save();
            return true;
        }

        public String getNumberOfBranchBuildsToKeep() {
            return numberOfBranchBuildsToKeep;
        }

        public List<VersionComputer> getVersioningModes() {
            return new ArrayList<VersionComputer>(Arrays.asList(VersionComputer.values()));
        }
    }
}
