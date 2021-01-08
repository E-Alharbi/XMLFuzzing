package xfuzz.xml.builder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class FormatingXML {

	/*
	 * This container will hold the new XML and build a new XML file 
	 */
		
	 public String formatXML(String input)
	    {
	        try
	        {
	            Transformer transformer = TransformerFactory.newInstance()
	                    .newTransformer();
	            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	            transformer.setOutputProperty(
	                    "{http://xml.apache.org/xslt}indent-amount", "3");

	            StreamResult result = new StreamResult(new StringWriter());
	            DOMSource source = new DOMSource(parseXml(input));
	            transformer.transform(source, result);
	            return result.getWriter().toString();
	        } catch (Exception e)
	        {
	            e.printStackTrace();
	            return input;
	        }
	    }

	    private Document parseXml(String in)
	    {
	        try
	        {
	            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	            DocumentBuilder db = dbf.newDocumentBuilder();
	            InputSource is = new InputSource(new StringReader(in));
	            return db.parse(is);
	        } catch (Exception e)
	        {
	            throw new RuntimeException(e);
	        }
	    }
	
}
