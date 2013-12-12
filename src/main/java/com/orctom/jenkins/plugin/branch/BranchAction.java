package com.orctom.jenkins.plugin.branch;

import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSet;
import hudson.model.PermalinkProjectAction;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.shared.release.versions.DefaultVersionInfo;
import org.apache.maven.shared.release.versions.VersionParseException;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author: CH
 * @since: 11/15/13 5:16 PM
 */
public class BranchAction implements PermalinkProjectAction {

    private MavenModuleSet project;

    public BranchAction(MavenModuleSet project) {
        this.project = project;
    }

    public String getIconFileName() {
        return "installer.gif";
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
        String version = "NaN";
        final MavenModule rootModule = getRootModule();
        if (rootModule != null && StringUtils.isNotBlank(rootModule.getVersion())) {
            try {
                DefaultVersionInfo dvi = new DefaultVersionInfo(rootModule.getVersion());
                version = dvi.getReleaseVersionString();
            } catch (VersionParseException vpEx) {
                Logger logger = Logger.getLogger(this.getClass().getName());
                logger.log(Level.WARNING, "Failed to compute next version.", vpEx);
                version = rootModule.getVersion().replace("-SNAPSHOT", "");
            }
        }
        return version;
    }

    public String computeBranchJobName() {
        String version = "NaN-SNAPSHOT";
        final MavenModule rootModule = getRootModule();
        if (rootModule != null && StringUtils.isNotBlank(rootModule.getVersion())) {
            try {
                DefaultVersionInfo dvi = new DefaultVersionInfo(rootModule.getVersion());
                version = dvi.getNextVersion().getSnapshotVersionString();
            } catch (Exception vpEx) {
                Logger logger = Logger.getLogger(this.getClass().getName());
                logger.log(Level.WARNING, "Failed to compute next version.", vpEx);
            }
        }
        return version;
    }

    public void doSubmit(StaplerRequest req, StaplerResponse resp) throws IOException, ServletException {
        System.out.println("submit and branching...");
    }
}
