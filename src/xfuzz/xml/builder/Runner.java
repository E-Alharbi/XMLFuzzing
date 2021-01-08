package xfuzz.xml.builder;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPathVariableResolver;

import org.translet.processor.XPathExpr;
import org.xml.sax.InputSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.lib.NamespaceConstant;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.xpath.XPathFactoryImpl;
import table.draw.Block;
import table.draw.Board;
import table.draw.Table;
import xfuzz.validator.Validator;
import xfuzz.xpath.analyzer.AnalyzedXPath;
import xfuzz.xpath.analyzer.Analyzer;
import xfuzz.xpath.analyzer.UnsatFormulaContainer;
import xfuzz.xpath.classifier.XPathClassifier;
import xfuzz.xpath.combiner.XPathCombiner;
import xfuzz.xpath.solver.XPathSetSolver;
import xfuzz.xpath.sorter.XPathSetSorter;
import xfuzz.xpath.xsd.Enumeration;
import xfuzz.xpath.xsd.PreparedXPath;
import xfuzz.xpath.xsd.XPathGenerator;
import xfuzz.xpath.xsd.XSDParser;
import xfuzz.xpath.xsd.XSDRestrictionsTypes;
import xfuzz.xpath.xsd.XSDValTYPES;
import xfuzz.xpath.xsd.XSDPathExpression;

import com.sun.xml.xsom.parser.XSOMParser;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;


public class Runner  {
	
	// The main class to run the tool 
	
	
	public  void run() throws Exception{
		
		int CountTheNumberOfProducedXMLDoc=0;
		WriteToFile w = new WriteToFile();
		
		// Deleting the previous files from output folder  
		File file = new File("./Output");      
	       String[] myFiles;    
	           if(file.isDirectory()){
	               myFiles = file.list();
	               for (int i=0; i<myFiles.length; i++) {
	                   File myFile = new File(file, myFiles[i]); 
	                   myFile.delete();
	               }
	            }
		
	           
	        File XPathfile = new File(Properties.XPathFileName);
	   		FileReader fileReader = new FileReader(XPathfile);
	   		BufferedReader bufferedReader = new BufferedReader(fileReader);
	   	
	   		
	   	//load the XPath set into vector	
	   		System.out.println("Reading XPath expressions file ...");
	   	 String line;
	   	 Vector <String> RawXPathSet=new Vector <String>();
	   		while ((line = bufferedReader.readLine()) != null) {
	   			
	   			//System.out.println(x);
	   			if(!line.startsWith("%"))
	   			RawXPathSet.add(line);
	   			
	   		}
	   		 
	   		bufferedReader.close();
	           
	           
	    //The inputs validation process   
	   		
		Validator v = new Validator();
		System.out.println("Validating XML document ...");
		if(!v.ValidateXML(Properties.XMLFileName)){
			
			return;
		}
		System.out.println("Validating XPath  expressions ...");
		if(!v.ValidateSetXPath(RawXPathSet)){
			
			return;
		}
		System.out.println("Validating XML document against XSD ...");
		if(!v.ValidateXMLAgainstXSD(Properties.XMLFileName, Properties.XSDName)){
			
			return;
		}
	   //End the inputs validation process 
		
	
		XPathGenerator x = new XPathGenerator(); // Generating the path expressions from current XML document
		Vector <PreparedXPath> a = x.run();
		System.out.println("Prasing XSD ...");
		XSDParser xx = new XSDParser(); // The XSD parser 
		Vector <XSDPathExpression> b = xx.run(); // the output of XSD parser
		
		
	System.out.println("Solving the constraints ...");	
	Analyzer analyzer = new Analyzer(); // The analyzer 
	Vector <AnalyzedXPath> XPathSet= new Vector <AnalyzedXPath>();
    if(Properties.Combiner==true) // using combiner 
	 XPathSet=analyzer.run(a,new XPathCombiner().run(RawXPathSet));
     if(Properties.Combiner==false)// no combiner
	 XPathSet=analyzer.run(a,RawXPathSet);

		
		 Vector<Vector<AnalyzedXPath>> ListOfClassifiedXPathSet= new XPathClassifier().run(XPathSet);
         Vector<Vector<AnalyzedXPath>> ListOfXPathSet= new XPathSetSolver().run(ListOfClassifiedXPathSet);
        

		 HashMap ImpoissbleTestCases=new  HashMap();
		 System.out.println("Building the XML documents  ...");	
		 //The XML builder starts building the XML document
		for(int L=0 ; L <ListOfXPathSet.size() ; ++L ){
			a = x.run();
			
			XPathSet=ListOfXPathSet.get(L);
		
		
		
		// Merging the path expressions set from current XML document with the set of XPath set
		for(int i=0 ; i <XPathSet.size() ; ++i ){
			
			SAXON s = new SAXON();
			
			RandomDataGenerator r=new  RandomDataGenerator();
			if(s.run(XPathSet.get(i).XPath)==false){
			
			NodesFinder FindRelatedNodes = new NodesFinder();
			Vector <XSDPathExpression> Nodes=FindRelatedNodes.FindChildrenNodes(b,XPathSet.get(i).XPath,"");
			String NodeValueIfAany=XPathSet.get(i).Value;
			if(NodeValueIfAany==null || NodeValueIfAany.equals(""))
				NodeValueIfAany	=r.GetValue(Nodes.get(0).ValueType ,Nodes.get(0) );
			if(new PreparedXPath().Add(a, new PreparedXPath(XPathSet.get(i).XPath , NodeValueIfAany, true), b)==true)
			for(int m=1 ; m <Nodes.size();++m ){
				
				PreparedXPath EE = new PreparedXPath();
				EE.XPath=Nodes.get(m).XPath;
				EE.Value=r.GetValue(Nodes.get(m).ValueType ,Nodes.get(m) );//Random data
				EE.IsNewXPath=true;
				a.add(EE);
				
				
				
			}
			else {
				ImpoissbleTestCases.put(L, L);
				
			}
		}
			
			else{
				
				if(s.run(XPathSet.get(i).XPath)==true &&XPathSet.get(i).Value!=null ){
					
					for(int n=0 ; n <a.size() ; ++n ){
						if(a.get(n).XPath.equals(XPathSet.get(i).XPath)){
						
							PreparedXPath EE = new PreparedXPath();
							EE.XPath=XPathSet.get(i).XPath;
							EE.Value=XPathSet.get(i).Value;
							EE.IsNewXPath=false;
							if(new PreparedXPath().Update(a, XPathSet.get(i), b, n)==false){
								ImpoissbleTestCases.put(L, L);
								
							}
						}
					}
					
					
				}
			}
		}
		
		
		// The path expressions sorter 
		a= new XPathSetSorter().Sort(b, a);
		
		
	
		XPathExpr [] xp= new XPathExpr[a.size()];
		for(int i=0 ; i<a.size() ; ++i){
			
			xp[i]=  new XPathExpr(a.get(i).XPath,a.get(i).Value );

		}
		
		 List xpaths = Arrays.asList(xp);
		
	
		
		
		
		// passing the path expressions set to Translet tool  
		try {
			if(ImpoissbleTestCases.containsKey(L)==false){//Do not produce  XML file for impossible test cases. When the list index is exists in this vector that mean this impossible test cases  
				Translet t = new Translet();
				String XML= t.transletToXML(xpaths);
				
				w.WriteXMLToFile("./Output/"+L+".xml", new FormatingXML().formatXML(XML));
			++CountTheNumberOfProducedXMLDoc;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		}
		
		
		
		
		
	
		w.CreateReport("./Output/"+"Report.txt",b,ListOfXPathSet , ImpoissbleTestCases);
	
		
		
		 System.out.println("Done!");
		 
		 System.out.println(CountTheNumberOfProducedXMLDoc +" XML documetn(s) have been created.");
		
	}
	
	
}
