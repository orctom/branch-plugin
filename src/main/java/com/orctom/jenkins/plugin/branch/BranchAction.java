package com.orctom.jenkins.plugin.branch;

import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSet;
import hudson.model.ParameterValue;
import hudson.model.Hudson;
import hudson.model.ParametersAction;
import hudson.model.PermalinkProjectAction;
import hudson.model.StringParameterValue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.release.versions.VersionInfo;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import com.orctom.jenkins.plugin.branch.version.VersionComputer;
import com.orctom.jenkins.plugin.branch.version.VersionComputerFactory;

/**
 * @author: CH
 * @since: 11/15/13 5:16 PM
 */
public class BranchAction implements PermalinkProjectAction {

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
    public String getBranchBase() {
        return project.getBuildWrappersList().get(BranchBuildWrapper.class).getBranchBasePath();
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

    public String computeBranchName() {
        String selectedVersionMode = getDefaultVersioningMode();

        if (VersionComputer.RELEASE_CANDIDATE.getName().equals(selectedVersionMode)) {
            String version = getCurrentVersion();
            if (null != version) {
                return version.replaceAll("-SNAPSHOT", "");
            }
        }

        return df.format(new Date());
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
        final String branchBase = wrapper.getBranchBasePath();

        final String branchVersion = req.getParameter("branchVersion");
        final String trunkVersion = req.getParameter("trunkVersion");
        final String branchJobName = req.getParameter("branchJobName");
        final String branchName = req.getParameter("branchName");
        final boolean isCreateBranchJob = null != req.getParameter("createBranchJob");
        final boolean isClearTriggers = null != req.getParameter("clearTriggers");
        final String trunkJobName = project.getName();

        BranchArgumentsAction args = new BranchArgumentsAction();
        args.setBranchVersion(branchVersion);
        args.setTrunkVersion(trunkVersion);
        args.setBranchJobName(branchJobName);
        args.setBranchBase(branchBase);
        args.setBranchName(branchName);
        args.setTrunkJobName(trunkJobName);
        args.setCreateBranchJob(isCreateBranchJob);
        args.setClearTriggers(isClearTriggers);

        List<ParameterValue> values = new ArrayList<ParameterValue>();
        values.add(new StringParameterValue("branchVersion", branchVersion));
        values.add(new StringParameterValue("trunkVersion", trunkVersion));
        values.add(new StringParameterValue("branchJobName", branchJobName));
        values.add(new StringParameterValue("branchBase", branchBase));
        values.add(new StringParameterValue("branchName", branchName));
        values.add(new StringParameterValue("trunkJobName", trunkJobName));
        values.add(new StringParameterValue("isCreateBranchJob", isCreateBranchJob ? "true" : "false"));
        values.add(new StringParameterValue("isClearTriggers", isClearTriggers ? "true" : "false"));
        ParametersAction params = new ParametersAction(values);

        if (project.scheduleBuild(0, new BranchCause(), params, args)) {
            resp.sendRedirect(req.getContextPath() + '/' + project.getUrl());
        } else {
            resp.sendRedirect(req.getContextPath() + '/' + project.getUrl() + '/' + getUrlName() + "/failed");
        }
    }
}
