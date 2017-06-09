package org.laradong.demo.CodeCoPark.xml;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Lara on 2017/6/9.
 */
public class XmlParserTest {
    private XmlParser parser = new XmlParser();

    @Test
    public void testCreateBaseConfigDocument() throws ParserConfigurationException {
        Document document = parser.createBaseConfigDocument();
        Assert.assertNotNull(document);
    }

}