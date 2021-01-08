package xfuzz.xpath.xsd;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import xfuzz.xml.builder.Properties;
import xfuzz.xpath.xsd.XSDValTYPES;
public class XSDParser extends DefaultHandler {
	
	/*
	 * Parsing  XSD 
	 * We need that to check nodes and its restrictions when we want to implement or modify the node in XML
	 */
	
	// The original code from http://stackoverflow.com/questions/4746299/generate-get-xpath-from-xml-node-java 
	//it modified by me (Please see below the end of the  original code) 
	
  String xPath = "/";
  XMLReader xmlReader;
  XSDParser parent;
  StringBuilder characters = new StringBuilder();
  Map<String, Integer> elementNameCount = new HashMap<String, Integer>();
 Vector <String> XPathSet = new Vector <String>();
  public XSDParser(XMLReader xmlReader) {
    this.xmlReader = xmlReader;
  }
  public XSDParser() {
	   
	  }
  private Locator locator;
  public void setDocumentLocator(Locator locator) {
    this.locator = locator;
  }
  private XSDParser(String xPath, XMLReader xmlReader,
		  XSDParser parent ,Vector <String> XPathSet , Locator locator ) {
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
    
    for (int x = 0; x < attsLength; x++) {
    
      if(childXPath.contains("attribute"))
      childXPath = childXPath + "[@" + atts.getQName(x) + "='@"
           + atts.getValue(x) + "']";
      else
    	  childXPath = childXPath + "[@" + atts.getQName(x) + "='"
    	           + atts.getValue(x) + "']";
    }
    XPathSet.add(childXPath);
  
    if (locator != null) {
  
    
    }
    XSDParser child = new XSDParser(childXPath,xmlReader, this , XPathSet ,locator);
    xmlReader.setContentHandler(child);
   
  }

  @Override
  public void endElement(String uri, String localName, String qName)
      throws SAXException {
    String value = characters.toString().trim();
  
    if (locator != null) {
    
        
    }
 
    xmlReader.setContentHandler(parent);
  }
  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    characters.append(ch, start, length);
  }

// This is the end of the code from http://stackoverflow.com/questions/4746299/generate-get-xpath-from-xml-node-java 
// The below code implement by me
  public  Vector <XSDPathExpression> run() throws Exception {
    SAXParserFactory spf = SAXParserFactory.newInstance();
    SAXParser sp = spf.newSAXParser();
    XMLReader xr = sp.getXMLReader();

    XSDParser g = new XSDParser(xr);
    xr.setContentHandler(g);
    xr.parse(new InputSource(new FileInputStream(Properties.XSDName)));
    
    
    
    return NodesFromXSD(g.XPathSet) ; //BuildXPath(g.XPathSet);
  }
  
  
	 
	 public Vector <XSDPathExpression> NodesFromXSD(Vector <String> UnClearedNodesPaths){
			Vector <XSDPathExpression> XSDXPathSet= new Vector <XSDPathExpression>(); 
			XSDRestrictionsParser XSDParser = new XSDRestrictionsParser();
			for(int i=0 ; i < UnClearedNodesPaths.size() ; i++){
				String XPath="";
				
				if(contains("@name=",UnClearedNodesPaths.get(i))!=-1){
					String XSDXPath=UnClearedNodesPaths.get(i);
					String XSDPathType=UnClearedNodesPaths.get(i);
					String Type="None";// node's vale type
					String Max="1";//because if the max not appear in XSD that's mean this node should appear in XML just once
					String Min="1"; // same as above 
					String Temp=XSDPathType;
					
				    int e=0;
					while(Temp.indexOf("@name")!=-1){
					e=Temp.indexOf("@name");
					if(e+1 < Temp.length())
					Temp=Temp.substring(e+1);
					else
					break;
					
					
					}
					
					if(contains("@type=",XSDPathType)!=-1)
					{
						
						int Index=contains("@type=",XSDXPath);
						Type=CollectChar(XSDPathType,Index);
						
					}
					if(contains("@maxOccurs=",Temp)!=-1)
					{
						
						int Index=contains("@maxOccurs=",Temp);
						Max=CollectChar(Temp,Index);
						
					}
					if(contains("@minOccurs=",XSDPathType)!=-1)
					{
						
						int Index=contains("@minOccurs=",XSDXPath);
						Min=CollectChar(XSDPathType,Index);
						
					}
					
					while(contains("@name=",XSDXPath)!=-1){
						int index=contains("@name=",XSDXPath);
						if(XPath.length()!=0)
							XPath+="/";
						else XPath+="//";
						for(int m=index ;m<XSDXPath.length() ; ++m){
							if(XSDXPath.charAt(m)!=']'){
								
								XPath+=	XSDXPath.charAt(m);
								}
							else {
								index=m;
								break ;
							}
						}
						
						XSDXPath=XSDXPath.substring(index);
					}
					XPath=XPath.replaceAll("'", "");
					XSDPathExpression P= new XSDPathExpression();
					
					XSDRestrictions Res = XSDParser.EnumerationRestriction(UnClearedNodesPaths,XPath,UnClearedNodesPaths.get(i),XSDXPathSet);
					Res=XSDParser.InclusiveRestriction(UnClearedNodesPaths,XPath,UnClearedNodesPaths.get(i),XSDXPathSet);
					
					if(containNode(XSDXPathSet,XPath)==null){//not in the vector 
						
						
						P.XPath=XPath;
						
						if(Max.contains("unbounded"))
							P.max=-1;// mean unbounded
						else if  (Max.equals("1"))
						P.max=1;
						else
						{
						P.max=ParsingNumberFromString(Max);
						}
						
						
						if(Min.contains("unbounded"))
							P.min=-1;// mean unbounded
						else if  (Min.equals("1"))
						P.min=1;
						else
						{
						P.min=ParsingNumberFromString(Min);
						}
						
						
					
					P.ValueType=FindType(Type);
					//P.Res=Res;
					
					
					XSDXPathSet.add(P);	
						
					
					
						
					}
					else{
						//System.out.println("XPath :" +XPath);
						//System.out.println("XPath : Max" +Max);
					}
					
				}
				
			}
			return XSDXPathSet;
		}
		

			public int contains(String s1, String s2) {
				int index=0;
			  if(s1.equals("")) return -1;
			    while(true) {
			      if(s2.equals("")) return -1;
			      if(s2.startsWith(s1)) return index+s1.length(); // to avoid @name='course' . We do not need @name in xpath
			      s2=s2.substring(1);
			      index++;
			    }
		  }
			 
	 
	 
			 String CollectChar(String Path , int index){
				 String Type="";
				 for(int m=index ;m<Path.length() ; ++m){
						if(Path.charAt(m)!=']'){
							
							Type+=	Path.charAt(m);
							}
						else {
							index=m;
							break ;
						}
					}
				 return Type;
			 }
			 
			int ParsingNumberFromString(String s){
				Pattern p = Pattern.compile("-?\\d+");
				Matcher m = p.matcher(s);
				String ss="";
				while (m.find()) {
					ss+=m.group();
				}
				return Integer.parseInt(ss);
			}
			
			XSDPathExpression containNode(Vector<XSDPathExpression> a , String XPath){
				for(int i=0 ; i <a.size() ; ++i){
					if(a.get(i).XPath.equals(XPath))
						return a.get(i);
				}
				return null;
			}
			
			
			
			XSDValTYPES FindType(String ValType){
				if(ValType.contains("string"))
					return  XSDValTYPES.String;
					if(ValType.contains("decimal"))
						return XSDValTYPES.Decimal;
					if(ValType.contains("integer"))
						return XSDValTYPES.Integer;
					if(ValType.contains("boolean"))
						return XSDValTYPES.Boolean;
					if(ValType.contains("date"))
						return XSDValTYPES.Date;
					if(ValType.contains("time"))
						return XSDValTYPES.Time;
					if(ValType.equals("None"))
						return XSDValTYPES.None;
					if(ValType.contains("byte"))
						return XSDValTYPES.Integer;
					
					return XSDValTYPES.Undefined;
				
			}
}
