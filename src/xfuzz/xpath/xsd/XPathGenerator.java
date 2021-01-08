package xfuzz.xpath.xsd;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import xfuzz.xml.builder.Properties;

public class XPathGenerator extends DefaultHandler {
	
	/*
	 * Generating all possible XPaths from XML then add more nodes on them  
	 * The XPath generator generates the path expressions from current XML document to send to Translet tool 
	 * after merging with the set of XPath expressions 
	 */
	
	// The original code from http://stackoverflow.com/questions/4746299/generate-get-xpath-from-xml-node-java 
   //it modified by me 
  String xPath = "/";
  XMLReader xmlReader;
  XPathGenerator parent;
  StringBuilder characters = new StringBuilder();
  Map<String, Integer> elementNameCount = new HashMap<String, Integer>();
 Vector <PreparedXPath> XPathSet = new Vector <PreparedXPath>();
  public XPathGenerator(XMLReader xmlReader) {
    this.xmlReader = xmlReader;
  }
  public XPathGenerator() {
	   
	  }
  private Locator locator;
  public void setDocumentLocator(Locator locator) {
    this.locator = locator;
  }
  private XPathGenerator(String xPath, XMLReader xmlReader,
		  XPathGenerator parent ,Vector <PreparedXPath> XPathSet , Locator locator ) {
    this(xmlReader);
    this.xPath = xPath;
    this.parent = parent;
    this.XPathSet = XPathSet;
    this.locator= locator;
  }

  
  
  @Override
  public void startElement(String uri, String localName, String qName,
      Attributes atts) throws SAXException {
    Integer count = elementNameCount.get(qName);
    if (null == count) {
      count = 1;
    } else {
      count++;
    }
    elementNameCount.put(qName, count);
    String childXPath = xPath + "/" + qName + "[" + count + "]";

    int attsLength = atts.getLength();
    String XPathQurey ="";
    for (int x = 0; x < attsLength; x++) {
     
       XPathQurey = childXPath + "/@" + atts.getQName(x) ;
             
    
       PreparedXPath EX = new PreparedXPath();
	   EX.XPath=XPathQurey;
       EX.Value=atts.getValue(x);
       XPathSet.add(EX);
    }
  
    XPathGenerator child = new XPathGenerator(childXPath,xmlReader, this , XPathSet ,locator);
    xmlReader.setContentHandler(child);
   
  }

  @Override
  public void endElement(String uri, String localName, String qName)
      throws SAXException {
	  String value = characters.toString().trim();
	  
    PreparedXPath EX = new PreparedXPath();
    EX.XPath=xPath;
    EX.Value=value;
    EX.IsNewXPath=false;
    XPathSet.add(EX);
   
    xmlReader.setContentHandler(parent);
  }
  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    characters.append(ch, start, length);
  }



  public  Vector <PreparedXPath> run() throws Exception {
    SAXParserFactory spf = SAXParserFactory.newInstance();
    SAXParser sp = spf.newSAXParser();
    XMLReader xr = sp.getXMLReader();

    XPathGenerator g = new XPathGenerator(xr);
    xr.setContentHandler(g);
    xr.parse(new InputSource(new FileInputStream(Properties.XMLFileName)));
    
    
    
    return g.XPathSet;
  }
  
 

}
