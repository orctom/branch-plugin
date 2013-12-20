package com.orctom.jenkins.plugin.branch;

import com.orctom.jenkins.plugin.branch.version.VersionComputer;
import com.orctom.jenkins.plugin.branch.version.VersionComputerFactory;
import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSet;
import hudson.model.Hudson;
import hudson.model.PermalinkProjectAction;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.release.versions.VersionInfo;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author: CH
 * @since: 11/15/13 5:16 PM
 */
public class BranchAction implements PermalinkProjectAction {

    private static Logger LOGGER = Logger.getLogger(BranchAction.class.getName());
    private static SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

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

    public String getCurrentVersion() {
        final MavenModule rootModule = getRootModule();
        if (rootModule != null && StringUtils.isNotBlank(rootModule.getVersion())) {
            return rootModule.getVersion();
        } else {
            return null;
        }
    }

    public String computeBranchVersion() {
        String branchVersion = getVersionComputer().getReleaseVersionString();
        if (branchVersion.endsWith(Artifact.SNAPSHOT_VERSION)) {
            return branchVersion;
        } else {
            return branchVersion + "-SNAPSHOT";
        }
    }

    public String computeTrunkVersion() {
        VersionInfo nextVersion = getVersionComputer().getNextVersion();
        if (null == nextVersion) {
            return "NaN-SNAPSHOT";
        } else {
            return nextVersion.getSnapshotVersionString();
        }
    }

    public String computeBranchJobName() {
        String selectedVersionMode = getDefaultVersioningMode();

        StringBuilder jobName = new StringBuilder(50);
        jobName.append(project.getName().replaceAll("(?i)TRUNK", "BRANCH").replaceAll("([_-]?\\d)", ""));

        if (VersionComputer.RELEASE_CANDIDATE.getName().equals(selectedVersionMode)) {
            String version = getCurrentVersion();
            if (null != version) {
                jobName.append("_").append(version.replaceAll("-SNAPSHOT", ""));
            }
        } else {
            String dateString = df.format(new Date());
            jobName.append("_").append(dateString);
        }

        return jobName.toString();
    }

    public String computeBranchURL() {
        StringBuilder url = new StringBuilder(100);
        url.append(project.getBuildWrappersList().get(BranchBuildWrapper.class).getBranchBasePath());

        if ('/' != url.charAt(url.length() - 1)) {
            url.append("/");
        }

        String selectedVersionMode = getDefaultVersioningMode();

        if (VersionComputer.RELEASE_CANDIDATE.getName().equals(selectedVersionMode)) {
            final MavenModule rootModule = getRootModule();
            String version = getCurrentVersion();
            if (null != version) {
                url.append(version.replaceAll("-SNAPSHOT", ""));
            }
        } else {
            String dateString = df.format(new Date());
            url.append(dateString).append("/");
        }

        return url.toString();
    }

    private String getDefaultVersioningMode() {
        BranchBuildWrapper buildWrapper = project.getBuildWrappersList().get(BranchBuildWrapper.class);
        return buildWrapper.getDefaultVersioningMode();
    }

    private VersionInfo getVersionComputer() {
        String currentVersion = "NaN-SNAPSHOT";
        final MavenModule rootModule = getRootModule();
        if (rootModule != null && StringUtils.isNotBlank(rootModule.getVersion())) {
            currentVersion = rootModule.getVersion();
        }
        String selectedVersionMode = getDefaultVersioningMode();

        return VersionComputerFactory.getVersionComputer(selectedVersionMode, currentVersion, project.getName());
    }

    public void doSubmit(StaplerRequest req, StaplerResponse resp) throws IOException, ServletException {
        Hudson.getInstance().checkPermission(Hudson.ADMINISTER);
        BranchBuildWrapper wrapper = project.getBuildWrappersList().get(BranchBuildWrapper.class);
    }

}
