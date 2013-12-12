package com.orctom.jenkins.plugin.branch;

import hudson.Extension;
import hudson.Launcher;
import hudson.Util;
import hudson.maven.*;
import hudson.model.*;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by CH on 12/11/13.
 */
public class BranchBuildWrapper extends BuildWrapper {

    private transient Logger log = LoggerFactory.getLogger(BranchBuildWrapper.class);

    @DataBoundConstructor
    public BranchBuildWrapper() {
    }

    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        return super.setUp(build, launcher, listener);
    }

    @Override
    public Collection<? extends Action> getProjectActions(AbstractProject job) {
        Collection<BranchAction> actions = new ArrayList<BranchAction>();
        actions.add(new BranchAction((MavenModuleSet) job));
        return actions;
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
        log = LoggerFactory.getLogger(BranchBuildWrapper.class);
        return this;
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
    }
}
