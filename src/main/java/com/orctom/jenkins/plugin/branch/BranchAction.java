package com.orctom.jenkins.plugin.branch;

import com.orctom.jenkins.plugin.branch.version.VersionComputer;
import com.orctom.jenkins.plugin.branch.version.VersionComputerFactory;
import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSet;
import hudson.model.*;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        VersionComputer versionComputer = getVersionComputer();
        if (versionComputer.isTrunk()) {
            return versionComputer.getReleaseVersion();
        } else {
            return versionComputer.getNextIncrementalVersion().getReleaseVersion();
        }
    }

    public String computeTrunkVersion() {
        return getVersionComputer().getNextVersion().getSnapshotVersion();
    }

    public String computeBranchJobName() {
        StringBuilder jobName = new StringBuilder(50);
        jobName.append(project.getName().replaceAll("(?i)TRUNK", "BRANCH").replaceAll("([_-]?\\d.*)", ""));

        VersionComputer versionComputer = getVersionComputer();
        if (versionComputer.isTrunk()) {
            jobName.append("_").append(versionComputer.getReleaseVersionDigits());
        } else {
            jobName.append("_").append(versionComputer.getNextIncrementalVersion().getReleaseVersionDigits());
        }

        return jobName.toString();
    }

    public String computeBranchName() {
        VersionComputer versionComputer = getVersionComputer();
        if (versionComputer.isTrunk()) {
            return versionComputer.getReleaseVersionDigits();
        } else {
            return versionComputer.getNextIncrementalVersion().getReleaseVersionDigits();
        }
    }

    private boolean isBranch(String version) {
        return version.contains("-RC-") || version.matches("\\d{2}\\.\\d{2}\\.\\d{2}.*");
    }

    public boolean isTrunk() {
        return !isBranch(getCurrentVersion());
    }

    private String getDefaultVersioningMode() {
        BranchBuildWrapper buildWrapper = project.getBuildWrappersList().get(BranchBuildWrapper.class);
        return buildWrapper.getDefaultVersioningMode();
    }

    private VersionComputer getVersionComputer() {
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

        final String currentVersion = req.getParameter("currentVersion");
        final boolean isBranch = isBranch(currentVersion);

        final String branchVersion = req.getParameter("branchVersion");
        final String branchJobName = req.getParameter("branchJobName");
        final String branchName = req.getParameter("branchName");
        final boolean isCreateBranchJob = null != req.getParameter("createBranchJob");
        final boolean isClearTriggers = null != req.getParameter("clearTriggers");
        final String currentJobName = project.getName();

        BranchArgumentsAction args = new BranchArgumentsAction();
        args.setBranchVersion(branchVersion);
        args.setCurrentVersion(currentVersion);
        args.setBranchJobName(branchJobName);
        args.setBranchBase(branchBase);
        args.setBranchName(branchName);
        args.setCurrentJobName(currentJobName);
        args.setCreateBranchJob(isCreateBranchJob);
        args.setClearTriggers(isClearTriggers);

        List<ParameterValue> values = new ArrayList<ParameterValue>();
        values.add(new StringParameterValue("branchVersion", branchVersion));
        values.add(new StringParameterValue("currentVersion", currentVersion));
        values.add(new StringParameterValue("branchJobName", branchJobName));
        values.add(new StringParameterValue("branchBase", branchBase));
        values.add(new StringParameterValue("branchName", branchName));
        values.add(new StringParameterValue("currentJobName", currentJobName));
        values.add(new StringParameterValue("isCreateBranchJob", isCreateBranchJob ? "true" : "false"));
        values.add(new StringParameterValue("isClearTriggers", isClearTriggers ? "true" : "false"));

        if (!isBranch) {
            final String trunkVersion = req.getParameter("trunkVersion");
            args.setTrunkVersion(trunkVersion);
            values.add(new StringParameterValue("trunkVersion", trunkVersion));
        }

        ParametersAction params = new ParametersAction(values);

        if (project.scheduleBuild(0, new BranchCause(), params, args)) {
            resp.sendRedirect(req.getContextPath() + '/' + project.getUrl());
        } else {
            resp.sendRedirect(req.getContextPath() + '/' + project.getUrl() + '/' + getUrlName() + "/failed");
        }
    }
}
