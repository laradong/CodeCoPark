package mqq.sdet.ci.coveritysubp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * 解析jenkins的job.xml文件.
 *
 * <p>
 * 使用方法：描述
 * </p>
 *
 * <p>
 * 注意事项：描述
 * </p>
 *
 * @author laradong
 * @version 2017年6月2日 下午3:36:34
 * @since JDK1.8
 */
public class JobXmlParser {
    private static final Logger logger = LoggerFactory.getLogger(Constant.LOG_BUSINESS);

    /**
     * 创建基本的jenkins配置文件.
     *
     * <p>
     * 示例： xx
     * </p>
     * <p>
     * 注意事项： xml基础框架，节点为空。
     * </p>
     *
     * @return xml文档
     */
    public Document createBaseConfigDocument() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        Element root = document.createElement("project");
        document.appendChild(root);
        return document;
    }

    /**
     * 创建标签节点.
     *
     * <p>
     * 示例： xx
     * </p>
     * <p>
     * 注意事项： xx
     * </p>
     *
     * @param document Job配置文档
     * @param value    标签名
     */
    public void createAssignedNode(Document document, String value) {
        Element assignedNode = document.createElement("assignedNode");
        assignedNode.setTextContent(value);
        Element root = document.getDocumentElement();
        root.appendChild(assignedNode);
    }

    /**
     * 创建脚本节点.
     *
     * <p>
     * 示例： xx
     * </p>
     * <p>
     * 注意事项： xx
     * </p>
     *
     * @param document Job配置文档
     * @param value    脚本名
     */
    public void createBuildScriptName(Document document, String value) {
        final Element properties = document.createElement("properties");
        final Element parametersDefinitionProperty = document.createElement("hudson.model.ParametersDefinitionProperty");
        final Element parameterDefinitions = document.createElement("parameterDefinitions");
        final Element stringParameterDefinition = document.createElement("hudson.model.StringParameterDefinition");
        final Element name = document.createElement("name");
        name.setTextContent("BUILD_SCRIPT_NAME");
        final Element defaultValue = document.createElement("defaultValue");
        defaultValue.setTextContent(value);

        Element root = document.getDocumentElement();
        root.appendChild(properties);
        properties.appendChild(parametersDefinitionProperty);
        parametersDefinitionProperty.appendChild(parameterDefinitions);
        parameterDefinitions.appendChild(stringParameterDefinition);
        stringParameterDefinition.appendChild(name);
        stringParameterDefinition.appendChild(defaultValue);
    }


    /**
     * 创建Git版本库节点.
     *
     * <p>
     * 示例： xx
     * </p>
     * <p>
     * 注意事项： SVN从环境变量取，不需要这个操作。
     * </p>
     *
     * @param document     带加节点的XML文档
     * @param repoUrl      版本库地址
     * @param credentialId git版本库的用户名/密码
     * @param branch       git版本库的分支
     */
    public void createGitRepository(Document document, String repoUrl, String credentialId, String branch) {
        Element root = document.getDocumentElement();
        XPath xpath = XPathFactory.newInstance().newXPath();
        String path = "/project/scm/userRemoteConfigs/hudson.plugins.git.UserRemoteConfig";
        try {
            Node url = (Node) xpath.evaluate(path + "/url", root, XPathConstants.NODE);
            if (url == null) {
                logger.warn("createGitRepository failed, repoUrl is null, repoUrl={}, credentialId={}, branch={}.", repoUrl, credentialId, branch);
            } else {
                url.setTextContent(repoUrl);
            }

            Node credentialsId = (Node) xpath.evaluate(path + "/credentialsId", root, XPathConstants.NODE);
            if (credentialsId == null) {
                logger.warn("createGitRepository failed, credentialsId is null, repoUrl={}, credentialId={}, branch={}.", repoUrl, credentialId,
                        branch);
            } else {
                credentialsId.setTextContent(credentialId);
            }

            Node branches = (Node) xpath.evaluate("/project/scm/branches/hudson.plugins.git.BranchSpec/name", root, XPathConstants.NODE);
            if (branches == null) {
                logger.warn("createGitRepository failed, branch is null, repoUrl={}, credentialId={}, branch={}.", repoUrl, credentialId, branch);
            } else {
                branches.setTextContent(branch);
            }
        } catch (XPathExpressionException ex) {
            logger.warn("createGitRepository failed, repoUrl={}, credentialId={}, branch={}, XPathExpressionException={}.", repoUrl, credentialId,
                    branch, ex.getLocalizedMessage(), ex);
        }
    }

    /**
     * xml文档转字符串.
     *
     * <p>
     * 示例： xx
     * </p>
     * <p>
     * 注意事项： xx
     * </p>
     *
     * @param doc xml文档
     * @return 字符串
     */
    public String xmlToString(Document doc) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty("encoding", "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.getBuffer().toString();
        } catch (TransformerException ex) {
            logger.warn("xmlToString failed, TransformerException={}.", ex.getLocator(), ex);
            return null;
        }
    }

    /**
     * 字符串转xml文档.
     *
     * <p>
     * 示例： xx
     * </p>
     * <p>
     * 注意事项： xx
     * </p>
     *
     * @param xml 字符串
     * @return xml文档
     */
    public Document parse(String xml) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xml)));
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            logger.warn("parse xml failed, xml={}, Exception={}", xml, ex.getLocalizedMessage(), ex);
            return null;
        }
    }

    /**
     * 将文档保存为文件.
     *
     * @param doc  xml文档
     * @param file 保存文件
     * @return 是否成功
     */
    public boolean saveDocumentFile(Document doc, File file) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        try {
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(source, result);
            return true;
        } catch (TransformerException ex) {
            logger.warn("saveDocumentFile failed, TransformerException={}.", ex.getLocalizedMessage(), ex);
            return false;
        }
    }
}
