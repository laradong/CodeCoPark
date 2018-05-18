package mqq.sdet.ci.coveritysubp.util;

import org.apache.commons.lang.StringUtils;
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
    public static final int BUILD_SCRIPT = 1;// 变更脚本
    public static final int ASSIGNED_NODE = 2;// 变更标签
    public static final int REPO = 3;// 变更版本库
    private static final Logger logger = LoggerFactory.getLogger(Constant.LOG_BUSINESS);

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
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
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

    /**
     * 修改某个节点值.
     *
     * <p>
     * 示例： xx
     * </p>
     * <p>
     * 注意事项： xx
     * </p>
     *
     * @param document 原始XML文档
     * @param field    要修改的字段名
     * @param value    要修改的值
     */
    public void changeValue(Document document, int field, String value) {
        if (document == null || field <= 0 || StringUtils.isBlank(value)) {
            return;
        }

        XPath xpath = XPathFactory.newInstance().newXPath();
        Element root = document.getDocumentElement();
        try {
            switch (field) {
                case BUILD_SCRIPT:
                    String path =
                            "/project/properties/hudson.model.ParametersDefinitionProperty/parameterDefinitions/hudson.model.StringParameterDefinition/";
                    Node buildScriptNameNode =
                            (Node) xpath.evaluate(path + "name[text()='BUILD_SCRIPT_NAME']/following-sibling::defaultValue", root, XPathConstants.NODE);
                    if (buildScriptNameNode == null) {
                        logger.warn("failed, xml has illegal buildScriptNameNode xpath, document={}.", xmlToString(document));
                        break;
                    }
                    buildScriptNameNode.setTextContent(value);
                    break;
                case ASSIGNED_NODE:
                    Node assignedNode = (Node) xpath.evaluate("/project/assignedNode", root, XPathConstants.NODE);
                    if (assignedNode == null) {
                        logger.warn("failed, xml has illegal assignedNode xpath, document={}.", xmlToString(document));
                        break;
                    }
                    assignedNode.setTextContent(value);
                    break;
                default:
                    logger.warn("unsupported field={}, value={}", field, value);
                    break;
            }
        } catch (XPathExpressionException ex) {
            logger.warn("failed to update value, document={}, XPathExpressionException={}", xmlToString(document), ex.getLocalizedMessage(), ex);
        }
    }

    /**
     * 变更Git版本库相关内容.
     *
     * <p>
     * 示例： xx
     * </p>
     * <p>
     * 注意事项： SVN从环境变量取，不需要这个操作。
     * </p>
     *
     * @param document     原始XML文档
     * @param repoUrl      版本库地址
     * @param credentialId git版本库的用户名/密码
     * @param branch       git版本库的分支
     */
    public void changeGitRepository(Document document, String repoUrl, String credentialId, String branch) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Element root = document.getDocumentElement();

        try {
            if (StringUtils.isBlank(credentialId)) {
                logger.warn("failed, credentialId is null!");
                return;
            }
            Node credentialsIdNode = (Node) xpath.evaluate("/project/scm/userRemoteConfigs/hudson.plugins.git.UserRemoteConfig/credentialsId",
                    root, XPathConstants.NODE);
            if (credentialsIdNode == null) {
                logger.warn("failed, xml has illegal credentialsIdNode xpath, credentialId={}.", credentialId);
                return;
            }
            credentialsIdNode.setTextContent(credentialId);

            Node urlNode =
                    (Node) xpath.evaluate("/project/scm/userRemoteConfigs/hudson.plugins.git.UserRemoteConfig/url", root, XPathConstants.NODE);
            if (urlNode == null) {
                logger.warn("failed, xml has illegal urlNode xpath, repoUrl={}.", repoUrl);
                return;
            }
            urlNode.setTextContent(repoUrl);

            Node branchNode = (Node) xpath.evaluate("/project/scm/branches/hudson.plugins.git.BranchSpec/name", root, XPathConstants.NODE);
            if (branchNode == null) {
                logger.warn("failed, xml has illegal branchNode xpath, branch={}.", branch);
                return;
            }
            branchNode.setTextContent(branch);
        } catch (XPathExpressionException ex) {
            logger.warn("failed to update value, document={}, XPathExpressionException={}", xmlToString(document), ex.getLocalizedMessage(), ex);
        }
    }
}
