package com.orctom.jenkins.plugin.branch.builder;

import hudson.EnvVars;
import hudson.Launcher;
import hudson.XmlFile;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Job;
import hudson.model.TopLevelItem;
import hudson.tasks.Builder;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by CH on 12/23/13.
 */
public class CreateBranchJobBuilder extends Builder {

    private static Logger LOGGER = Logger.getLogger(CreateBranchJobBuilder.class.getName());

    private String trunkJobName;
    private String branchJobName;
    private String branchURL;
    private boolean isClearTriggers;

    public CreateBranchJobBuilder(String trunkJobName, String branchJobName, String branchURL, boolean isClearTriggers) {
        this.trunkJobName = trunkJobName;
        this.branchJobName = branchJobName;
        this.branchURL = branchURL;
        this.isClearTriggers = isClearTriggers;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        EnvVars env = build.getEnvironment(listener);

        listener.getLogger().println("Creating branch job: " + branchJobName);

        if (StringUtils.isBlank(branchJobName)) {
            listener.getLogger().println("Skipped, not branch job name set");
            return false;
        }

        // Expand the variable expressions in job names.
        String trunkJobNameExpanded = env.expand(trunkJobName);
        String branchJobNameExpanded = env.expand(branchJobName);

        if (StringUtils.isBlank(branchJobNameExpanded)) {
            listener.getLogger().println("Skipped, not branch job name set");
            return false;
        }

        listener.getLogger().println(String.format("Copying %s to %s", trunkJobNameExpanded, branchJobNameExpanded));

        // Reteive the job to be copied from.
        TopLevelItem trunkJob = Jenkins.getInstance().getItem(trunkJobNameExpanded);

        if (trunkJob == null) {
            listener.getLogger().println("Error: Item was not found.");
            return false;
        } else if (!(trunkJob instanceof Job<?, ?>)) {
            listener.getLogger().println("Error: Item was found, but is not a job.");
            return false;
        }

        // Check whether the job to be copied to is already exists.
        TopLevelItem branchJob = Jenkins.getInstance().getItem(branchJobNameExpanded);
        if (branchJob != null) {
            listener.getLogger().println(String.format("Already exists: %s", branchJobNameExpanded));
            return false;
        }

        listener.getLogger().println(String.format("Fetching configuration of %s...", trunkJobNameExpanded));

        XmlFile file = ((Job<?, ?>) trunkJob).getConfigFile();
        String jobConfigXmlString = file.asString();
        String encoding = file.sniffEncoding();

        // Apply additional operations to the retrieved XML.
        try {
            jobConfigXmlString = transformConfigToBranch(jobConfigXmlString);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to transform the job config to point to branch SCM", e);
            listener.getLogger().println("Failed to transform the job config to point to branch SCM");
        }

        // Create the job copied to.
        listener.getLogger().println(String.format("Creating %s", branchJobNameExpanded));
        InputStream is = new ByteArrayInputStream(jobConfigXmlString.getBytes(encoding));
        branchJob = Jenkins.getInstance().createProjectFromXML(branchJobNameExpanded, is);
        if (branchJob == null) {
            listener.getLogger().println(String.format("Failed to create %s", branchJobNameExpanded));
            return false;
        }

        return true;
    }

    private String transformConfigToBranch(String jobConfigXmlString) throws ParserConfigurationException, IOException, SAXException, TransformerException, XPathExpressionException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(jobConfigXmlString));
        Document doc = builder.parse(is);
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();

        // update scm to point to branch
        NodeList locations = (NodeList) xpath.compile("//scm/locations/*/remote").evaluate(doc, XPathConstants.NODESET);
        int len = locations.getLength();
        if (1 == len) {
            locations.item(0).setTextContent(branchURL);
        } else {
            boolean branchUrlEndWithSlash = branchURL.endsWith("/");
            for (int i = 0; i < len; i++) {
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

        // clear triggers
        if (isClearTriggers) {
            Node triggerNode = (Node) xpath.compile("//triggers").evaluate(doc, XPathConstants.NODE);
            NodeList triggers = triggerNode.getChildNodes();
            if (triggers.getLength() > 0) {
                for (int i = triggers.getLength() - 1; i >= 0; i--) {
                    Node trigger = triggers.item(i);
                    triggerNode.removeChild(trigger);
                }
            }
        }

        // get rid of branch-plugin it's self
//        Node buildWrapperNode = (Node) xpath.compile("//buildWrappers").evaluate(doc, XPathConstants.NODE);
//        NodeList buildWrappers = buildWrapperNode.getChildNodes();
//        if (buildWrappers.getLength() > 0) {
//            for (int i = buildWrappers.getLength() - 1; i >= 0; i--) {
//                Node buildWrapper = buildWrappers.item(i);
//                if ("com.orctom.jenkins.plugin.branch.BranchBuildWrapper".equals(buildWrapper.getNodeName())) {
//                    buildWrapperNode.removeChild(buildWrapper);
//                }
//            }
//        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        writer.flush();
        return writer.getBuffer().toString();
    }
}
