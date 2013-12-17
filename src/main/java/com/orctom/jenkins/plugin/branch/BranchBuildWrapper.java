package com.orctom.jenkins.plugin.branch;

import com.orctom.jenkins.plugin.branch.version.VersionComputer;
import hudson.Extension;
import hudson.Launcher;
import hudson.Util;
import hudson.maven.*;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by CH on 12/11/13.
 */
public class BranchBuildWrapper extends BuildWrapper {

    private static Logger LOGGER = Logger.getLogger(BranchBuildWrapper.class.getName());

    private String branchBasePath;
    private String defaultVersioningMode;

    @DataBoundConstructor
    public BranchBuildWrapper(String branchBasePath, String defaultVersioningMode) {
        this.branchBasePath = branchBasePath;
        this.defaultVersioningMode = defaultVersioningMode;
    }

    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        return new Environment() {};
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

    public List<VersionComputer> getVersioningModes() {
        return new ArrayList<VersionComputer>(Arrays.asList(VersionComputer.values()));
    }

    @Extension
    public static class DescriptorImpl extends BuildWrapperDescriptor {

        private String numberOfBranchBuildsToKeep = "2";

        public static final String VERSIONING_INCREASE_MAJOR = "VERSIONING_INCREACE_MAJOR";
        public static final String VERSIONING_INCREASE_MINOR = "VERSIONING_INCREACE_MINOR";
        public static final String VERSIONING_INCREASE_MICRO = "VERSIONING_INCREACE_MICRO";
        public static final String VERSIONING_INCREASE_DATE_MONTHLY = "VERSIONING_INCREACE_DATE_MONTHLY";

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
    }
}
