package org.laradong.demo.CodeCoPark.xml;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Lara on 2017/6/9.
 */
public class XmlParser {
    /**
     * 创建基本的jenkins配置文件.
     * <p>
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
}
