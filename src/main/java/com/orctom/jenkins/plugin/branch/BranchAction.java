package com.orctom.jenkins.plugin.branch;

import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSet;
import hudson.model.Hudson;
import hudson.model.PermalinkProjectAction;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.shared.release.versions.VersionInfo;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author: CH
 * @since: 11/15/13 5:16 PM
 */
public class BranchAction implements PermalinkProjectAction {

    private static Logger LOGGER = Logger.getLogger(BranchAction.class.getName());

    private MavenModuleSet project;

    public BranchAction(MavenModuleSet project) {
        this.project = project;
    }

    public String getIconFileName() {
        return "/plugin/branch-plugin/img/branch.gif";
    }

    public String getDisplayName() {
        return Messages.BranchAction_CreateNewBranch();
    }

    public String getUrlName() {
        return "branch";
    }

    public List<Permalink> getPermalinks() {
        return new ArrayList<Permalink>();
    }

    public Collection<MavenModule> getModules() {
        return project.getModules();
    }

    public MavenModule getRootModule() {
        return project.getRootModule();
    }

    public String computeBranchVersion() {
        return getVersionComputer().getReleaseVersionString();
    }

    public String computeTrunkVersion() {
        return getVersionComputer().getNextVersion().getSnapshotVersionString();
    }

    public String computeBranchJobName() {
        String name = project.getName().replaceAll("(?i)TRUNK", "BRANCH");
        String[] names = name.split("[^a-zA-Z']+");
        return null;
    }

    private String getDefaultVersioningMode() {
        BranchBuildWrapper buildWrapper = project.getBuildWrappersList().get(BranchBuildWrapper.class);
        return buildWrapper.getDefaultVersioningMode();
    }

    private VersionInfo getVersionComputer() {
        String version = "NaN-SNAPSHOT";
        final MavenModule rootModule = getRootModule();
        if (rootModule != null && StringUtils.isNotBlank(rootModule.getVersion())) {
            version = rootModule.getVersion();
        }
        BranchBuildWrapper buildWrapper = project.getBuildWrappersList().get(BranchBuildWrapper.class);
        String selectedVersionMode = buildWrapper.getDefaultVersioningMode();
        return VersionComputerFactory.getVersionComputer(selectedVersionMode, version);
    }

    public void doSubmit(StaplerRequest req, StaplerResponse resp) throws IOException, ServletException {
        Hudson.getInstance().checkPermission(Hudson.ADMINISTER);
        BranchBuildWrapper wrapper = project.getBuildWrappersList().get(BranchBuildWrapper.class);
    }
}
