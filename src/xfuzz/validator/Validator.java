package xfuzz.validator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.sf.saxon.lib.NamespaceConstant;
import xfuzz.xpath.analyzer.AnalyzedXPath;
import xfuzz.xpath.xsd.XSDPathExpression;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.xml.sax.SAXException;
public class Validator {

	public static void main(String[] args) throws XPathFactoryConfigurationException, ParserConfigurationException {
		// TODO Auto-generated method stub

		System.out.println(new Validator().ValidateXPath("//course[1][@name='C++]/node[1]/node[1]/node[1]/node[1]/node[1]") );
		//System.out.println(new Validator().ValidateXML("course.xml"));
	}

	
	public boolean ValidateSetXPath (Vector <String> XPathSet) throws XPathFactoryConfigurationException{
	
		System.out.println("Checking XPath queries validation ...  ");
		
			
					for(int i=0 ; i < XPathSet.size() ; ++i)
					if(ValidateXPath(XPathSet.get(i))==false){
						
						return false;
					}
					
				
		return true;
	}
	
	public boolean ValidateXPath (String XPath) throws XPathFactoryConfigurationException{
		
		System.out.println(XPath +" checking .... ");
		System.setProperty("javax.xml.xpath.XPathFactory:"+NamespaceConstant.OBJECT_MODEL_SAXON,
	              "net.sf.saxon.xpath.XPathFactoryImpl");
		//System.out.println(System.getProperty("javax.xml.xpath.XPathFactory:"+NamespaceConstant.OBJECT_MODEL_SAXON));
		
		//XPathFactory xpf = XPathFactory.newInstance(NamespaceConstant.OBJECT_MODEL_SAXON);
	        //XPath xpe = xpf.newXPath();
		XPath xpe = XPathFactory.newInstance().newXPath();
	        //System.err.println("Loaded XPath Provider " + xpe.getClass().getName());
	        try {
				XPathExpression testXpath =
				        xpe.compile(XPath);
				System.out.println(XPath + " valid");
				return true;
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				//System.out.println(" invalid XPath");
				System.out.println(XPath + " invalid");
				return false;
			}
	}
	
	public boolean ValidateXML (String FilePath) throws ParserConfigurationException{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		try {
			db.parse(new File(FilePath));
			return true;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	
	public boolean ValidateXMLAgainstXSD(String XMLFilePath, String XSDFilePath)
	{
	    try
	    {
	        SchemaFactory factory = 
	            SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	        Schema schema = factory.newSchema(new StreamSource(new File(XSDFilePath)));
	        javax.xml.validation.Validator  validator = schema.newValidator();
	        validator.validate(new StreamSource(new File(XMLFilePath)));
	        System.out.println("XML document vaild aginst XSD");
	        return true;
	    }
	    catch(Exception ex)
	    {
	    	ex.printStackTrace();
	    	System.out.println("XML file document vaild aginst XSD");
	        return false;
	    }
	}
	
	
}
