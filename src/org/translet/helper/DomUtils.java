package org.translet.helper;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.w3c.dom.*;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

public class DomUtils
{
	public static String serialize(Node n)
	throws IOException
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		serialize(n,outStream);
		return outStream.toString();
	}

    public static String serialize(Node n, OutputFormat format)
    throws IOException
    {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        serialize(n,outStream,format);
        return outStream.toString();
    }

    public static void serialize(Node n, OutputStream outStream)
    throws IOException
    {
        OutputFormat format = new OutputFormat();
        format.setIndenting(true);
        format.setLineSeparator("\n");

        serialize(n,outStream,format);
    }

    public static void serialize(Node n, OutputStream outStream, OutputFormat format)
	throws IOException
	{
        if(null==n || null==outStream || null==format)
            throw new NullPointerException("Argument is null.");

        XMLSerializer ser = new XMLSerializer(outStream,format);

        if(n instanceof Document)
            ser.serialize((Document) n);
        else if(n instanceof Element)
            ser.serialize((Element) n);
        else if(n instanceof DocumentFragment)
            ser.serialize((DocumentFragment) n);
        else
            throw new IllegalArgumentException("The node can't be serialized. type:"+n.getNodeType());
	}

	/**
	 * Returns a node with tagname and at the specified position.
	 * The position is an index from a 1 based array.
	 */
	public static Node getFirstChildWithIndex(Node root, String tagName, int position)
	{
		int count = 0;
		for(Node node  = root.getFirstChild();
		           node != null;
		           node  = node.getNextSibling())
		{
			if(!tagName.equals(node.getNodeName())) continue;

			count++;
			if(position==count) return node;
		}

		return null;
	}

	public static Node getFirstChildWithTag(Node root, String tagName)
	{
		if(null==root || null==tagName || 0==tagName.length()) return null;

		for(Node node  = root.getFirstChild();
	           node != null;
	           node  = node.getNextSibling())
		{
			if(tagName.equals(node.getNodeName())) return node;
		}

		return null;
	}

    public static Document parse(InputStream is, String encoding, Properties p)
    throws SAXException,IOException,ParserConfigurationException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        if(p!=null)
        {
            for(Enumeration e = p.propertyNames();e.hasMoreElements();)
            {
                String name = (String) e.nextElement();
                dbf.setAttribute(name,p.getProperty(name));
            }
        }

        DocumentBuilder db = dbf.newDocumentBuilder();

        if(encoding!=null && encoding.length()>0)
        {
            InputSource isource = new InputSource(is);
            isource.setEncoding(encoding);
            return db.parse(isource);
        }
        else
            return db.parse(is);
    }
}
