package org.laradong.ccp.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.Map;

public class XmlUtil {

    public static String getNodeValue(Document doc, String xpath) {
        XPath xPath = XPathFactory.newInstance().newXPath();
        Element root = doc.getDocumentElement();
        try {
            Node node = (Node) xPath.evaluate(xpath, root, XPathConstants.NODE);
            if (node == null) {
                return null;
            }
            return node.getTextContent();
        } catch (XPathExpressionException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void appendNode(Document doc, String name, String value, Map<String, String> attributes) {
        Element root = doc.getDocumentElement();
        Element node = doc.createElement(name);
        if (value != null) {
            node.setTextContent(value);
        }
        if (attributes != null) {
            for (String key : attributes.keySet()) {
                node.setAttribute(key, attributes.get(name));
            }
        }
        root.appendChild(node);
    }

    /**
     * 创建XML文档.
     *
     * <p>
     * 示例： xx
     * </p>
     * <p>
     * 注意事项： xx
     * </p>
     *
     * @param rootName
     * @return
     */
    public static Document createDocument(String rootName, Map<String, String> attributes) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
            return null;
        }
        Document doc = builder.newDocument();
        Element root = doc.createElement(rootName);
        if (attributes != null) {
            for (String name : attributes.keySet()) {
                String value = attributes.get(name);
                root.setAttribute(name, value);
            }
        }
        doc.appendChild(root);
        return doc;
    }

    /**
     * 校验XML内容.
     *
     * <p>
     * 示例： xx
     * </p>
     * <p>
     * 注意事项： xx
     * </p>
     *
     * @param xsdContent xsd文件内容
     * @param xmlContent xml文件内容
     * @return
     */
    public static boolean validateXmlSchemaByString(String xsdContent, String xmlContent) {
        if (StringUtils.isBlank(xsdContent) || StringUtils.isBlank(xmlContent)) {
            return false;
        }

        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Reader xmlReader = new BufferedReader(new StringReader(xmlContent));
            Reader xsdReader = new BufferedReader(new StringReader(xsdContent));
            Source xsdSource = new StreamSource(xsdReader);
            Schema schema = factory.newSchema(xsdSource);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xmlReader));
        } catch (IOException | SAXException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 校验XML文件.
     *
     * <p>
     * 示例： xx
     * </p>
     * <p>
     * 注意事项： xx
     * </p>
     *
     * @param xsdPath schema文件地址
     * @param xmlPath xml文件地址
     * @return
     */
    public static boolean validateXmlSchemaByFile(String xsdPath, String xmlPath) {
        File xsd = new File(xsdPath);
        File xml = new File(xmlPath);
        if (!xsd.isFile() || !xsd.exists() || !xml.isFile() || !xml.exists()) {
            return false;
        }

        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(xsd);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xml));
        } catch (IOException | SAXException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 将XML文档转为字符串.
     *
     * <p>
     * 示例： xx
     * </p>
     * <p>
     * 注意事项： xx
     * </p>
     *
     * @param doc
     * @return
     */
    public static String xmlToString(Document doc) {
        TransformerFactory tf = TransformerFactory.newInstance();
        try {
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.getBuffer().toString();
        } catch (TransformerException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 将XML文档按格式转为HTML字符串.
     *
     * <p>
     * 示例： xx
     * </p>
     * <p>
     * 注意事项： xx
     * </p>
     *
     * @param xml
     * @param xslt
     * @return
     */
    public static String xmlToHtml(Source xml, Source xslt) {
        StringWriter sw = new StringWriter();
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer trasform = tFactory.newTransformer(xslt);
            trasform.transform(xml, new StreamResult(sw));
            return sw.toString();
        } catch (TransformerException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 将字符串转为XML文档.
     *
     * <p>
     * 示例： xx
     * </p>
     * <p>
     * 注意事项： xx
     * </p>
     *
     * @param content
     * @return
     */
    public static Document parse(String content) {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        Document document = null;
        InputStream is = null;
        try {
            builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            is = new ByteArrayInputStream(content.getBytes());
            document = builder.parse(is);
        } catch (ParserConfigurationException | IOException | SAXException ex) {
            ex.printStackTrace();
        } finally {
            if (is != null) {
                IOUtils.closeQuietly(is);
            }
        }
        return document;
    }

    /**
     * 清理文档中的指定节点.
     *
     * @param doc   文档
     * @param xpath 指定路径
     */
    public static void clearNode(Document doc, String xpath) {
        XPath xPath = XPathFactory.newInstance().newXPath();
        Element root = doc.getDocumentElement();
        try {
            NodeList nodes = (NodeList) xPath.evaluate(xpath, root, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); ++i) {
                Node node = nodes.item(i);
                node.getParentNode().removeChild(node);
            }
        } catch (XPathExpressionException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 添加节点.
     *
     * <p>
     * 示例： xx
     * </p>
     * <p>
     * 注意事项： xx
     * </p>
     *
     * @param doc
     * @param name
     * @param value
     */
    public static void appendNode(Document doc, String name, String value) {
        Element element = doc.createElement(name);
        element.appendChild(doc.createTextNode(value));
        Element root = doc.getDocumentElement();
        root.appendChild(element);
    }

}
