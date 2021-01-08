package xfuzz.xml.builder;

import java.io.File;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.sax.SAXSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPathVariableResolver;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.lib.NamespaceConstant;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.xpath.XPathFactoryImpl;
import xfuzz.xpath.xsd.XSDParser;
import xfuzz.xpath.xsd.XSDValTYPES;
import xfuzz.xpath.xsd.XSDPathExpression;

import org.xml.sax.Locator;
public class SAXON implements XPathVariableResolver {

	/*
	 * Run XPath expression 
	 */
	
	 XPath xpe;
	  DocumentInfo doc;
	public SAXON () throws XPathFactoryConfigurationException, MalformedURLException, XPathException{
		System.setProperty("javax.xml.xpath.XPathFactory:"+NamespaceConstant.OBJECT_MODEL_SAXON,
	              "net.sf.saxon.xpath.XPathFactoryImpl");
		
	        XPathFactory xpf = XPathFactory.newInstance(NamespaceConstant.OBJECT_MODEL_SAXON);
	        XPath xpe = xpf.newXPath();
	       
	        InputSource is = new InputSource(new File(Properties.XMLFileName).toURL().toString());
	        SAXSource ss = new SAXSource(is);
	        Configuration config = ((XPathFactoryImpl) xpf).getConfiguration();
	        DocumentInfo doc = config.buildDocument(ss);

	       
	        xpe.setXPathVariableResolver(this);

	       

	       
	}
	
	public boolean run(String XPath) throws Exception {

       
        System.setProperty("javax.xml.xpath.XPathFactory:"+NamespaceConstant.OBJECT_MODEL_SAXON,
              "net.sf.saxon.xpath.XPathFactoryImpl");

        XPathFactory xpf = XPathFactory.newInstance(NamespaceConstant.OBJECT_MODEL_SAXON);
        XPath xpe = xpf.newXPath();
       
        InputSource is = new InputSource(new File(Properties.XMLFileName).toURL().toString());
        SAXSource ss = new SAXSource(is);
        Configuration config = ((XPathFactoryImpl) xpf).getConfiguration();
        DocumentInfo doc = config.buildDocument(ss);

        
        xpe.setXPathVariableResolver(this);

        
       
       
        XPathExpression findnode =
            xpe.compile(XPath);
        
       
       
                List matchednodes = (List)findnode.evaluate(doc, XPathConstants.NODESET);
                
                
                boolean found = false;
                boolean IsFirstNode=true;
                NodeInfo FirstNode=null;
                if (matchednodes != null) {
                    for (Iterator iter = matchednodes.iterator(); iter.hasNext();) {

                       
                        found = true;

                     
                       
                        return true;
                    }
                    
                }

               
                if (!found) {
                  //  System.err.println("Not Found!!");
                    return false;
                }
          
      
        
         
        return false;
    }
	@Override
	public Object resolveVariable(QName variableName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String ExecuteXPath (String FileName ,String XPath  ) throws Exception{
		System.setProperty("javax.xml.xpath.XPathFactory:"+NamespaceConstant.OBJECT_MODEL_SAXON,
	              "net.sf.saxon.xpath.XPathFactoryImpl");

	       XPathFactory xpf = XPathFactory.newInstance(NamespaceConstant.OBJECT_MODEL_SAXON);
	        XPath xpe = xpf.newXPath();
	      
	        InputSource is = new InputSource(new File(FileName).toURL().toString());
	        SAXSource ss = new SAXSource(is);
	        Configuration config = ((XPathFactoryImpl) xpf).getConfiguration();
	        DocumentInfo doc = config.buildDocument(ss);

	       
		 XPathExpression CountVal =xpe.compile(XPath);
		 
		  System.out.println(XPath);
          if(CountVal.evaluate(doc, XPathConstants.STRING).equals(""))
        	  return "'null'";
          System.out.println(CountVal.evaluate(doc, XPathConstants.STRING));
           
        XSDPathExpression Node = new XSDPathExpression().FindNode(new NodesFinder().RemovePredicatesBrackets(XPath), new XSDParser().run());
        
        if(Node.ValueType==XSDValTYPES.String)
          return "'"+(CountVal.evaluate(doc, XPathConstants.STRING).toString())+"'";//0 if not exists
          else
          return (CountVal.evaluate(doc, XPathConstants.STRING).toString());//0 if not exists  
	}
	
	public String Count (String FileName ,String XPath  ) throws Exception{
		System.setProperty("javax.xml.xpath.XPathFactory:"+NamespaceConstant.OBJECT_MODEL_SAXON,
	              "net.sf.saxon.xpath.XPathFactoryImpl");

	       XPathFactory xpf = XPathFactory.newInstance(NamespaceConstant.OBJECT_MODEL_SAXON);
	        XPath xpe = xpf.newXPath();
	      
	        InputSource is = new InputSource(new File(FileName).toURL().toString());
	        SAXSource ss = new SAXSource(is);
	        Configuration config = ((XPathFactoryImpl) xpf).getConfiguration();
	        DocumentInfo doc = config.buildDocument(ss);

	       
		 XPathExpression CountVal =xpe.compile(XPath);
		 
		
          return (CountVal.evaluate(doc, XPathConstants.STRING).toString());//0 if not exists  
	}
}
