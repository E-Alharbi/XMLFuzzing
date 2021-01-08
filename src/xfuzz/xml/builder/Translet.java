package xfuzz.xml.builder;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.translet.builder.NodeCreator;
import org.translet.helper.Logger;
import org.translet.processor.DataIterator;
import org.translet.processor.ProcessException;
import org.translet.processor.Translator;
import org.translet.processor.XPathExpr;
import org.translet.processor.XPathProcessor;
import org.translet.processor.XPathProcessorImpl;
import org.translet.processor.dom.DOMTranslatorResult;



public class Translet {

	
	/*
	 * Translate an XPath to XML by using Translet tool
	 */
	
	private static final Logger log =
			Logger.getInstance(Translet.class.getName());
	public String transletToXML( List xpaths) throws TransformerException, ProcessException {
		// TODO Auto-generated method stub

		
		
		try
        {
            final Iterator it     = xpaths.iterator();

            // Create an inline data iterator from the final fields.
            DataIterator dit = new DataIterator() {

               public boolean hasNext() {
                   return it.hasNext();
               }

               public XPathExpr nextXPath() {
                   return (XPathExpr) it.next();
               }
            };

            DOMTranslatorResult result = new DOMTranslatorResult();
            Translator.getInstance().translate(dit,result);

            
         
           
        	System.out.println(result.getDocument().getFirstChild());
        	
        	
        	Source input = new DOMSource(result.getDocument());

        	
        	
        	StringWriter writer = new StringWriter();
        	Result result1 = new StreamResult(writer);
        	TransformerFactory tf = TransformerFactory.newInstance();
        	Transformer transformer = tf.newTransformer();
        	transformer.transform(input, result1);
        	
        	return writer.toString();
        }
        catch(ProcessException pe)
        {
        	//pe.printStackTrace();
            log.info("Error",pe);
        	
            return "";
        }
		
		
	}
	private static XPathProcessor newProcessor(String name)
	{
		XPathProcessor xp = new XPathProcessorImpl(name);
		xp.setHandler(new NodeCreator());
		return xp;
	}
}
