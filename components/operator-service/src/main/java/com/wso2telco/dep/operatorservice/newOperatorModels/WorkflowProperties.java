package com.wso2telco.dep.operatorservice.newOperatorModels;

import com.wso2telco.core.dbutils.fileutils.PropertyFileReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class WorkflowProperties {

    private static final String WORKFLOW_PROPERTIES_FILE = "workflow.properties";
    private static final String WORKFLOW_PROPERTIES_XML_FILE = "workflow-configuration.xml";
    private static final String SERVICE_HOST = "service.host";
    private static final String KEY_WORKFLOW_EMAIL_NOTIFICATION_HOST = "workflow.email.notification.host";
    private static final String KEY_WORKFLOW_EMAIL_NOTIFICATION_FROM_ADDRESS = "workflow.email.notification.from.address";
    private static final String KEY_WORKFLOW_EMAIL_NOTIFICATION_FROM_PASSWORD = "workflow.email.notification.from.password";
    private static final String PUBLISHER_ROLE_START_WITH ="workflow.Publisher.role.start.with";
    private static final String PUBLISHER_ROLE_END_WITH ="workflow.Publisher.role.end.with";
    private static final String MANDATE_SERVICE_HOST = "mandate.service.host";

    private static HashMap<String, String> propertiesMap = null;
    private static final Log log = LogFactory.getLog(WorkflowProperties.class);

    public static Properties loadWorkflowProperties(){
        Properties props=null;
        props = PropertyFileReader.getFileReader().getProperties(WORKFLOW_PROPERTIES_FILE);
        return props;
    }

    public static Map<String, String> loadWorkflowPropertiesFromXML() {
        if (propertiesMap == null) {
            try {
                propertiesMap = new HashMap<String, String>();

                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

                String carbonHome = System.getProperty("carbon.home");
                String workflowPropertiesFile = carbonHome + "/repository/conf/" + WORKFLOW_PROPERTIES_XML_FILE;

                Document document = builder.parse(new File(workflowPropertiesFile));
                Element rootElement = document.getDocumentElement();

                NodeList nodeList = rootElement.getElementsByTagName("Property");
                if (nodeList != null && nodeList.getLength() > 0) {
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node node = nodeList.item(i);
                        String nodeName = node.getAttributes().getNamedItem("name").getNodeValue();
                        if (nodeName.equalsIgnoreCase(SERVICE_HOST)
                                || nodeName.equalsIgnoreCase(KEY_WORKFLOW_EMAIL_NOTIFICATION_HOST)
                                || nodeName.equalsIgnoreCase(KEY_WORKFLOW_EMAIL_NOTIFICATION_FROM_ADDRESS)
                                || nodeName.equalsIgnoreCase(KEY_WORKFLOW_EMAIL_NOTIFICATION_FROM_PASSWORD)
                                || nodeName.equalsIgnoreCase(PUBLISHER_ROLE_START_WITH)
                                || nodeName.equalsIgnoreCase(PUBLISHER_ROLE_END_WITH)
                                || nodeName.equalsIgnoreCase(MANDATE_SERVICE_HOST)) {
                            String value = ((Element)node).getTextContent();
                            propertiesMap.put(nodeName, value);
                        } else {
                            //Not a matching property
                        }
                    }
                }
            } catch (Exception e) {
                String errorMessage = "Error in WorkflowProperties.loadWorkflowPropertiesFromXML";
                log.error(errorMessage, e);
            }
        } else {
            //Return already loaded propertiesMap
        }
        return propertiesMap;
    }
}
