package org.laradong.demo.CodeCoPark.data.xml;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Lara on 2017/6/9.
 */
public class XmlParserTest {
    private XmlParser parser = new XmlParser();
    private String rootName = "project";
    private String nodeName = "machine";
    private String nodeValue = "Linux";

    @Test
    public void testCreateDocument() throws ParserConfigurationException {
        Document document = parser.createDocument(rootName);
        Assert.assertNotNull(document);
        Element root = document.getDocumentElement();
        Assert.assertNotNull(root);
        Assert.assertEquals(rootName, root.getTagName());
    }

    @Test
    public void testCreateNode() throws ParserConfigurationException {
        Document document = parser.createDocument(rootName);
        Assert.assertNotNull(document);
        parser.createNode(document, "", "");
    }

}