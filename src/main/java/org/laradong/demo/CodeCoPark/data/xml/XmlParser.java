package org.laradong.demo.CodeCoPark.data.xml;

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
     * Create a xml document.
     *
     * @param rootName the root element name
     * @return a document with one root element
     * @throws ParserConfigurationException
     */
    public Document createDocument(String rootName) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        Element root = document.createElement(rootName);
        document.appendChild(root);
        return document;
    }

    /**
     * Create a node in the document under the root element.
     *
     * @param document the document to create
     * @param name     node name
     */
    public void createNode(Document document, String name, String value) {
        Element node = document.createElement(name);
        node.setTextContent(value);
        Element root = document.getDocumentElement();
        root.appendChild(node);
    }
}
