package com.orctom.jenkins.branch;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Created by CH on 12/17/13.
 */
public class Test {

    public static String branchURL = "https://ecomsvn.officedepot.com/svn/ECOM/branches/maven/bazaarvoice/1.5";

    public static String xml2 = "<?xml version='1.0' encoding='UTF-8'?>\n" +
            "<maven2-moduleset plugin=\"maven-plugin@1.518\">\n" +
            "  <actions/>\n" +
            "  <description>WWW TRUNK</description>\n" +
            "  <logRotator class=\"hudson.tasks.LogRotator\">\n" +
            "    <daysToKeep>-1</daysToKeep>\n" +
            "    <numToKeep>5</numToKeep>\n" +
            "    <artifactDaysToKeep>-1</artifactDaysToKeep>\n" +
            "    <artifactNumToKeep>-1</artifactNumToKeep>\n" +
            "  </logRotator>\n" +
            "  <keepDependencies>false</keepDependencies>\n" +
            "  <properties/>\n" +
            "  <scm class=\"hudson.scm.SubversionSCM\" plugin=\"subversion@1.45\">\n" +
            "    <locations>\n" +
            "      <hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "        <remote>https://ecomsvn.officedepot.com/svn/ECOM/trunk/odglobal/Config</remote>\n" +
            "        <local>Config</local>\n" +
            "        <depthOption>infinity</depthOption>\n" +
            "        <ignoreExternalsOption>false</ignoreExternalsOption>\n" +
            "      </hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "      <hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "        <remote>https://ecomsvn.officedepot.com/svn/ECOM/trunk/odshare/GlobalBase</remote>\n" +
            "        <local>GlobalBase</local>\n" +
            "        <depthOption>infinity</depthOption>\n" +
            "        <ignoreExternalsOption>false</ignoreExternalsOption>\n" +
            "      </hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "      <hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "        <remote>https://ecomsvn.officedepot.com/svn/ECOM/trunk/odshare/config-commons</remote>\n" +
            "        <local>config-commons</local>\n" +
            "        <depthOption>infinity</depthOption>\n" +
            "        <ignoreExternalsOption>false</ignoreExternalsOption>\n" +
            "      </hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "      <hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "        <remote>https://ecomsvn.officedepot.com/svn/ECOM/trunk/odshare/GlobalTestBase</remote>\n" +
            "        <local>GlobalTestBase</local>\n" +
            "        <depthOption>infinity</depthOption>\n" +
            "        <ignoreExternalsOption>false</ignoreExternalsOption>\n" +
            "      </hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "      <hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "        <remote>https://ecomsvn.officedepot.com/svn/ECOM/trunk/odshare/cdapj</remote>\n" +
            "        <local>cdapj</local>\n" +
            "        <depthOption>infinity</depthOption>\n" +
            "        <ignoreExternalsOption>false</ignoreExternalsOption>\n" +
            "      </hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "      <hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "        <remote>https://ecomsvn.officedepot.com/svn/ECOM/trunk/odshare/nacdapj</remote>\n" +
            "        <local>nacdapj</local>\n" +
            "        <depthOption>infinity</depthOption>\n" +
            "        <ignoreExternalsOption>false</ignoreExternalsOption>\n" +
            "      </hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "      <hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "        <remote>https://ecomsvn.officedepot.com/svn/ECOM/trunk/odshare/ODSAPConnector</remote>\n" +
            "        <local>ODSAPConnector</local>\n" +
            "        <depthOption>infinity</depthOption>\n" +
            "        <ignoreExternalsOption>false</ignoreExternalsOption>\n" +
            "      </hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "      <hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "        <remote>https://ecomsvn.officedepot.com/svn/ECOM/trunk/odshare/ODSI</remote>\n" +
            "        <local>ODSI</local>\n" +
            "        <depthOption>infinity</depthOption>\n" +
            "        <ignoreExternalsOption>false</ignoreExternalsOption>\n" +
            "      </hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "      <hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "        <remote>https://ecomsvn.officedepot.com/svn/ECOM/trunk/odshare/GlobalEJB</remote>\n" +
            "        <local>GlobalEJB</local>\n" +
            "        <depthOption>infinity</depthOption>\n" +
            "        <ignoreExternalsOption>false</ignoreExternalsOption>\n" +
            "      </hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "      <hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "        <remote>https://ecomsvn.officedepot.com/svn/ECOM/trunk/odshare/GlobalServices</remote>\n" +
            "        <local>GlobalServices</local>\n" +
            "        <depthOption>infinity</depthOption>\n" +
            "        <ignoreExternalsOption>false</ignoreExternalsOption>\n" +
            "      </hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "      <hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "        <remote>https://ecomsvn.officedepot.com/svn/ECOM/trunk/odshare/GlobalWebBase</remote>\n" +
            "        <local>GlobalWebBase</local>\n" +
            "        <depthOption>infinity</depthOption>\n" +
            "        <ignoreExternalsOption>false</ignoreExternalsOption>\n" +
            "      </hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "      <hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "        <remote>https://ecomsvn.officedepot.com/svn/ECOM/trunk/odglobal/globalWeb</remote>\n" +
            "        <local>globalWeb</local>\n" +
            "        <depthOption>infinity</depthOption>\n" +
            "        <ignoreExternalsOption>false</ignoreExternalsOption>\n" +
            "      </hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "      <hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "        <remote>https://ecomsvn.officedepot.com/svn/ECOM/trunk/WASodglobal/WASod_ear</remote>\n" +
            "        <local>WASod_ear</local>\n" +
            "        <depthOption>infinity</depthOption>\n" +
            "        <ignoreExternalsOption>false</ignoreExternalsOption>\n" +
            "      </hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "      <hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "        <remote>https://ecomsvn.officedepot.com/svn/ECOM/trunk/odshare/iodCoreMasterBuild</remote>\n" +
            "        <local>iodCoreMasterBuild</local>\n" +
            "        <depthOption>infinity</depthOption>\n" +
            "        <ignoreExternalsOption>false</ignoreExternalsOption>\n" +
            "      </hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "      <hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "        <remote>https://ecomsvn.officedepot.com/svn/ECOM/trunk/odglobal/iodMasterBuild</remote>\n" +
            "        <local>iodMasterBuild</local>\n" +
            "        <depthOption>infinity</depthOption>\n" +
            "        <ignoreExternalsOption>false</ignoreExternalsOption>\n" +
            "      </hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "      <hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "        <remote>https://ecomsvn.officedepot.com/svn/ECOM/trunk/odshare/GuidedNavigation</remote>\n" +
            "        <local>GuidedNavigation</local>\n" +
            "        <depthOption>infinity</depthOption>\n" +
            "        <ignoreExternalsOption>false</ignoreExternalsOption>\n" +
            "      </hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "      <hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "        <remote>https://ecomsvn.officedepot.com/svn/ECOM/trunk/odshare/GlobalUtil</remote>\n" +
            "        <local>GlobalUtil</local>\n" +
            "        <depthOption>infinity</depthOption>\n" +
            "        <ignoreExternalsOption>false</ignoreExternalsOption>\n" +
            "      </hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "      <hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "        <remote>https://ecomsvn.officedepot.com/svn/ECOM/trunk/odshare/services/PromoServiceAPI</remote>\n" +
            "        <local>PromoServiceAPI</local>\n" +
            "        <depthOption>infinity</depthOption>\n" +
            "        <ignoreExternalsOption>false</ignoreExternalsOption>\n" +
            "      </hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "      <hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "        <remote>https://ecomsvn.officedepot.com/svn/ECOM/trunk/odshare/services/PromoService</remote>\n" +
            "        <local>PromoService</local>\n" +
            "        <depthOption>infinity</depthOption>\n" +
            "        <ignoreExternalsOption>false</ignoreExternalsOption>\n" +
            "      </hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "      <hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "        <remote>https://ecomsvn.officedepot.com/svn/ECOM/trunk/odshare/services/CMSServiceAPI</remote>\n" +
            "        <local>CMSServiceAPI</local>\n" +
            "        <depthOption>infinity</depthOption>\n" +
            "        <ignoreExternalsOption>false</ignoreExternalsOption>\n" +
            "      </hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "      <hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "        <remote>https://ecomsvn.officedepot.com/svn/ECOM/trunk/odshare/services/CMSService</remote>\n" +
            "        <local>CMSService</local>\n" +
            "        <depthOption>infinity</depthOption>\n" +
            "        <ignoreExternalsOption>false</ignoreExternalsOption>\n" +
            "      </hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "    </locations>\n" +
            "    <excludedRegions></excludedRegions>\n" +
            "    <includedRegions></includedRegions>\n" +
            "    <excludedUsers></excludedUsers>\n" +
            "    <excludedRevprop></excludedRevprop>\n" +
            "    <excludedCommitMessages></excludedCommitMessages>\n" +
            "    <workspaceUpdater class=\"hudson.scm.subversion.UpdateWithCleanUpdater\"/>\n" +
            "    <ignoreDirPropChanges>false</ignoreDirPropChanges>\n" +
            "    <filterChangelog>false</filterChangelog>\n" +
            "  </scm>\n" +
            "  <canRoam>true</canRoam>\n" +
            "  <disabled>false</disabled>\n" +
            "  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>\n" +
            "  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>\n" +
            "  <triggers class=\"vector\"/>\n" +
            "  <concurrentBuild>false</concurrentBuild>\n" +
            "  <rootModule>\n" +
            "    <groupId>com.officedepot.iod</groupId>\n" +
            "    <artifactId>iodMasterBuild</artifactId>\n" +
            "  </rootModule>\n" +
            "  <rootPOM>iodMasterBuild/pom.xml</rootPOM>\n" +
            "  <goals>clean install -DskipTests=true -Dmeta.brand=od</goals>\n" +
            "  <mavenOpts>-Xmx1536m -XX:MaxPermSize=768m</mavenOpts>\n" +
            "  <aggregatorStyleBuild>true</aggregatorStyleBuild>\n" +
            "  <incrementalBuild>false</incrementalBuild>\n" +
            "  <perModuleEmail>true</perModuleEmail>\n" +
            "  <ignoreUpstremChanges>true</ignoreUpstremChanges>\n" +
            "  <archivingDisabled>false</archivingDisabled>\n" +
            "  <resolveDependencies>false</resolveDependencies>\n" +
            "  <processPlugins>false</processPlugins>\n" +
            "  <mavenValidationLevel>-1</mavenValidationLevel>\n" +
            "  <runHeadless>false</runHeadless>\n" +
            "  <disableTriggerDownstreamProjects>false</disableTriggerDownstreamProjects>\n" +
            "  <settings class=\"jenkins.mvn.DefaultSettingsProvider\"/>\n" +
            "  <globalSettings class=\"jenkins.mvn.DefaultGlobalSettingsProvider\"/>\n" +
            "  <reporters/>\n" +
            "  <publishers/>\n" +
            "  <buildWrappers>\n" +
            "    <com.orctom.jenkins.plugin.branch.BranchBuildWrapper plugin=\"branch-plugin@1.0-SNAPSHOT\">\n" +
            "      <branchBasePath>https://ecomsvn.officedepot.com/svn/ECOM/branches/maven/</branchBasePath>\n" +
            "      <defaultVersioningMode>MONTHLY</defaultVersioningMode>\n" +
            "    </com.orctom.jenkins.plugin.branch.BranchBuildWrapper>\n" +
            "    <org.jvnet.hudson.plugins.m2release.M2ReleaseBuildWrapper plugin=\"m2release@0.12.0\">\n" +
            "      <scmUserEnvVar></scmUserEnvVar>\n" +
            "      <scmPasswordEnvVar></scmPasswordEnvVar>\n" +
            "      <releaseEnvVar>IS_M2RELEASEBUILD</releaseEnvVar>\n" +
            "      <releaseGoals>-Dresume=false release:prepare release:perform</releaseGoals>\n" +
            "      <dryRunGoals>-Dresume=false -DdryRun=true release:prepare</dryRunGoals>\n" +
            "      <selectCustomScmCommentPrefix>false</selectCustomScmCommentPrefix>\n" +
            "      <selectAppendHudsonUsername>false</selectAppendHudsonUsername>\n" +
            "      <selectScmCredentials>false</selectScmCredentials>\n" +
            "      <numberOfReleaseBuildsToKeep>0</numberOfReleaseBuildsToKeep>\n" +
            "    </org.jvnet.hudson.plugins.m2release.M2ReleaseBuildWrapper>\n" +
            "  </buildWrappers>\n" +
            "  <prebuilders/>\n" +
            "  <postbuilders/>\n" +
            "  <runPostStepsIfResult>\n" +
            "    <name>SUCCESS</name>\n" +
            "    <ordinal>0</ordinal>\n" +
            "    <color>BLUE</color>\n" +
            "  </runPostStepsIfResult>\n" +
            "</maven2-moduleset>";

    public static String xml = "<?xml version='1.0' encoding='UTF-8'?>\n" +
            "<maven2-moduleset plugin=\"maven-plugin@1.518\">\n" +
            "  <actions/>\n" +
            "  <description></description>\n" +
            "  <logRotator class=\"hudson.tasks.LogRotator\">\n" +
            "    <daysToKeep>-1</daysToKeep>\n" +
            "    <numToKeep>5</numToKeep>\n" +
            "    <artifactDaysToKeep>-1</artifactDaysToKeep>\n" +
            "    <artifactNumToKeep>-1</artifactNumToKeep>\n" +
            "  </logRotator>\n" +
            "  <keepDependencies>false</keepDependencies>\n" +
            "  <properties/>\n" +
            "  <scm class=\"hudson.scm.SubversionSCM\" plugin=\"subversion@1.45\">\n" +
            "    <locations>\n" +
            "      <hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "        <remote>https://ecomsvn.officedepot.com/svn/ECOM/trunk/poc/BazaarVoice</remote>\n" +
            "        <local>.</local>\n" +
            "        <depthOption>infinity</depthOption>\n" +
            "        <ignoreExternalsOption>false</ignoreExternalsOption>\n" +
            "      </hudson.scm.SubversionSCM_-ModuleLocation>\n" +
            "    </locations>\n" +
            "    <excludedRegions></excludedRegions>\n" +
            "    <includedRegions></includedRegions>\n" +
            "    <excludedUsers></excludedUsers>\n" +
            "    <excludedRevprop></excludedRevprop>\n" +
            "    <excludedCommitMessages></excludedCommitMessages>\n" +
            "    <workspaceUpdater class=\"hudson.scm.subversion.UpdateWithCleanUpdater\"/>\n" +
            "    <ignoreDirPropChanges>false</ignoreDirPropChanges>\n" +
            "    <filterChangelog>false</filterChangelog>\n" +
            "  </scm>\n" +
            "  <canRoam>true</canRoam>\n" +
            "  <disabled>false</disabled>\n" +
            "  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>\n" +
            "  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>\n" +
            "  <triggers class=\"vector\">\n" +
            "    <hudson.triggers.SCMTrigger>\n" +
            "      <spec>0 4 4 * *</spec>\n" +
            "      <ignorePostCommitHooks>false</ignorePostCommitHooks>\n" +
            "    </hudson.triggers.SCMTrigger>\n" +
            "  </triggers>\n" +
            "  <concurrentBuild>false</concurrentBuild>\n" +
            "  <rootModule>\n" +
            "    <groupId>com.officedepot.services.bazaarvoice</groupId>\n" +
            "    <artifactId>BazaarVoiceMasterBuild</artifactId>\n" +
            "  </rootModule>\n" +
            "  <rootPOM>BazaarVoiceMasterBuild/pom.xml</rootPOM>\n" +
            "  <goals>clean install -DskipTests=true</goals>\n" +
            "  <aggregatorStyleBuild>true</aggregatorStyleBuild>\n" +
            "  <incrementalBuild>false</incrementalBuild>\n" +
            "  <perModuleEmail>true</perModuleEmail>\n" +
            "  <ignoreUpstremChanges>true</ignoreUpstremChanges>\n" +
            "  <archivingDisabled>false</archivingDisabled>\n" +
            "  <resolveDependencies>false</resolveDependencies>\n" +
            "  <processPlugins>false</processPlugins>\n" +
            "  <mavenValidationLevel>-1</mavenValidationLevel>\n" +
            "  <runHeadless>false</runHeadless>\n" +
            "  <disableTriggerDownstreamProjects>false</disableTriggerDownstreamProjects>\n" +
            "  <settings class=\"jenkins.mvn.DefaultSettingsProvider\"/>\n" +
            "  <globalSettings class=\"jenkins.mvn.DefaultGlobalSettingsProvider\"/>\n" +
            "  <reporters/>\n" +
            "  <publishers/>\n" +
            "  <buildWrappers>\n" +
            "    <com.orctom.jenkins.plugin.branch.BranchBuildWrapper plugin=\"branch-plugin@1.0-SNAPSHOT\">\n" +
            "      <branchBasePath>https://ecomsvn.officedepot.com/svn/ECOM/branches//maven/bazaarvoice/</branchBasePath>\n" +
            "      <defaultVersioningMode>RELEASE_CANDIDATE</defaultVersioningMode>\n" +
            "      <numberOfReleaseBuildsToKeep>2</numberOfReleaseBuildsToKeep>\n" +
            "    </com.orctom.jenkins.plugin.branch.BranchBuildWrapper>\n" +
            "  </buildWrappers>\n" +
            "  <prebuilders/>\n" +
            "  <postbuilders/>\n" +
            "  <runPostStepsIfResult>\n" +
            "    <name>FAILURE</name>\n" +
            "    <ordinal>2</ordinal>\n" +
            "    <color>RED</color>\n" +
            "  </runPostStepsIfResult>\n" +
            "</maven2-moduleset>";

    public static String transformConfigToBranch(String jobConfigXmlString) throws ParserConfigurationException, IOException, SAXException, TransformerException, XPathExpressionException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(jobConfigXmlString));
        Document doc = builder.parse(is);
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();

        // update scm to point to branch
        NodeList locations = (NodeList) xpath.compile("//scm/locations/*/remote").evaluate(doc, XPathConstants.NODESET);
        int len = locations.getLength();
        System.out.println(len);
        if (1 == len) {
            locations.item(0).setTextContent(branchURL);
        } else {
            boolean branchUrlEndWithSlash = branchURL.endsWith("/");
            for (int i = 0; i < locations.getLength(); i++) {
                Node location = locations.item(i);
                String value = location.getTextContent();
                if (null != value && value.startsWith("http")) {
                    if (branchUrlEndWithSlash) {
                        location.setTextContent(branchURL + value.substring(value.lastIndexOf("/") + 1));
                    } else {
                        location.setTextContent(branchURL + value.substring(value.lastIndexOf("/")));
                    }
                }
            }
        }

        // get rid of triggers
        Node triggerNode = (Node) xpath.compile("//triggers").evaluate(doc, XPathConstants.NODE);
        NodeList triggers = triggerNode.getChildNodes();
        if (triggers.getLength() > 0) {
            for (int i = triggers.getLength() - 1; i >= 0; i--) {
                Node trigger = triggers.item(i);
                triggerNode.removeChild(trigger);
            }
        }

        // get rid of branch-plugin it's self
        Node buildWrapperNode = (Node) xpath.compile("//buildWrappers").evaluate(doc, XPathConstants.NODE);
        NodeList buildWrappers = buildWrapperNode.getChildNodes();
        if (buildWrappers.getLength() > 0) {
            for (int i = buildWrappers.getLength() - 1; i >= 0; i--) {
                Node buildWrapper = buildWrappers.item(i);
                if ("com.orctom.jenkins.plugin.branch.BranchBuildWrapper".equals(buildWrapper.getNodeName())) {
                    buildWrapperNode.removeChild(buildWrapper);
                }
            }
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        writer.flush();
        String output = writer.getBuffer().toString();

        return output;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(transformConfigToBranch(xml2));
    }
}
