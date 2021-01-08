package xfuzz.xpath.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.sf.saxon.trans.XPathException;
import xfuzz.xml.builder.NodesFinder;
import xfuzz.xml.builder.Properties;
import xfuzz.xml.builder.SAXON;
import xfuzz.xpath.combiner.XPathCombiner;
import xfuzz.xpath.solver.*;
import xfuzz.xpath.xsd.PreparedXPath;
import xfuzz.xpath.xsd.XPathGenerator;
import xfuzz.xpath.xsd.XSDParser;
import xfuzz.xpath.xsd.XSDPathExpression;

import org.w3c.dom.Attr;
public class Analyzer {
	
	/*
	 * Analyzer is responsible for analyzing XPath expressions' constraints and translating them to SMT-LIB to solve   
	 */
	
	
	public Vector <AnalyzedXPath> run (Vector <PreparedXPath>  PreparedXPathSet , Vector <String> RawXPathSet) throws Exception{
	
		
		
		XSDParser xx = new XSDParser();
		Vector <XSDPathExpression> b = xx.run();
		
			Vector <AnalyzedXPath> XPathSet = new Vector <AnalyzedXPath>();
		for(int i = 0 ; i < RawXPathSet.size() ; ++i){
			String XPath=RawXPathSet.get(i);
			String line=RawXPathSet.get(i);
			
			XPath=new Analyzer().NodePathFinder(XPath);
			
			if(XPath!=null){
			XPathSet=new Analyzer().ParsingPredicateBrackets(Properties.XMLFileName,XPath,XPathSet,line ,PreparedXPathSet );
			
			}	
		}
		
		
		
		Vector <AnalyzedXPath> ReXPathSet = new Vector <AnalyzedXPath>(); 
		for(int i=0 ;i< XPathSet.size();++i ){// Remove any repeated path expression
			boolean Found=false;
			for(int y=0 ; y <ReXPathSet.size() ; ++y ){
				
				if(XPathSet.get(i).XPath.equals(ReXPathSet.get(y).XPath) &&XPathSet.get(i).Value.equals(ReXPathSet.get(y).Value) &&XPathSet.get(i).GenFrom.equals(ReXPathSet.get(y).GenFrom))
					Found=true;	
			}
			if(Found==false){
				ReXPathSet.add(XPathSet.get(i))	;
				
			}
		}
		
		return ReXPathSet;
		
	}
	

	public String NodePathFinder (String XPath) throws Exception{ // also find full path for the node Ex: //student will be returned //course/student
		XSDParser xx = new XSDParser();
		Vector <XSDPathExpression> b = xx.run();
		
		for(int i=0 ; i < XPath.length() ; ++i){
			
			if(XPath.charAt(i)=='/'){
				
				if(i+1 < XPath.length())
					if(XPath.charAt(i+1)=='/'){
						
						String NodeName="";
						for(int m=i+2 ; m < XPath.length() ; ++m){
							
							if(XPath.charAt(m)=='/'||XPath.charAt(m)=='[' || XPath.charAt(m)==')'  || new XPathOperators().map.containsKey(String.valueOf(XPath.charAt(m))) || XPath.charAt(m)==']'){
								
								
							String FullPath =new NodesFinder().FindFullPathForNodeFromXSD(b,NodeName);
						
							if(!FullPath.equals(""))
							XPath=XPath.substring(0, i)+FullPath+XPath.substring(m,XPath.length());// replace //course/student[count(//student)+5] by //course/student[count(//course/student)+5]
							else
								return null;
							i=(XPath.substring(0, i)+FullPath).length();
								break;
							}
							NodeName+=XPath.charAt(m);
								
						}
						
						
					}
			}
		}
		return XPath;
	}
	
	Vector <AnalyzedXPath> ParsingPredicateBrackets(String FilePath , String XPath , Vector <AnalyzedXPath> XPathSet,String SourceXPath ,Vector <PreparedXPath>  PreparedXPathSet ) throws Exception{
		boolean SeenBracket=false;
		int BracketIndex=0;
		
		for(int i=0 ;i < XPath.length(); ++i){
			
			
			if(XPath.charAt(i)=='[' && CheckValueInBracketIfNumber(XPath.substring(i+1) , true) == false){
				
				SeenBracket=true;
				BracketIndex=i;
				
				
			}
			
			if(XPath.charAt(i)==']' && CheckValueInBracketIfNumber(XPath.substring(0,i) , false)==false){
				
				SeenBracket=false;
				
			}
            
			if(SeenBracket==true && new XPathOperators().CheckOp(String.valueOf(XPath.charAt(i))) !=null ){ // if the XPath contain predicate //student[degree='MSc'], This will be converted to //student/degree 
				
				String OP=(String) new XPathOperators().CheckOp(String.valueOf(XPath.charAt(i)));
				
					
					return new XPathOperators().LessOrGreaterThanOP(XPath,BracketIndex,PreparedXPathSet,XPathSet,SourceXPath,i,OP);

				
			}
			
			if(SeenBracket==true && XPath.substring(BracketIndex+1).startsWith("count")){
				
				SAXON s = new SAXON();
				
				int NumberOfNodes=Integer.parseInt(s.ExecuteXPath(Properties.XMLFileName,new XPathOperators().GetValueAfterOp(XPath,BracketIndex)));// number of nodes with operator results   
				
				int NumberOfNodesInXML= Integer.parseInt(s.ExecuteXPath(Properties.XMLFileName,new XPathOperators().GetValueAfterOp(XPath,BracketIndex).substring(0,new XPathOperators().GetValueAfterOp(XPath,BracketIndex).indexOf(')')+1))); // number of nodes only

				
				if(NumberOfNodesInXML < NumberOfNodes){// Ex: if we have three nodes exists in XML and count results =3 , nothing will happen because we already have number of nodes more than the XPath expression need 
				
				for(int b=NumberOfNodesInXML ; b<NumberOfNodes ; ++b ){
				
					AnalyzedXPath a = new AnalyzedXPath();
					
					a.GenFrom=SourceXPath;
					a.XPath=XPath.substring(0,BracketIndex)+"["+(b+1)+"]";
					XPathSet=new AnalyzedXPath().AddXPath(XPathSet, a);
				
					
				}
				return XPathSet;
			}
			
		}
			
			if(SeenBracket==true && XPath.substring(BracketIndex+1).startsWith("@")){
				
				/*
				 * if the XPath has predicate  with attribute.
				 * Ex: //student[@id='S2'][degree='PhD'] 
				 * First of all ,the tool will find the full path and replaced the XPath to be   //course[1]/student[2][degree='PhD']
				 * then invoke the method again to analyse the left part of XPath    
				 */
				
				String Att=XPath.substring(BracketIndex+1,XPath.indexOf("="));
				String AttValue=XPath.substring(BracketIndex+1).substring(XPath.substring(BracketIndex+1).indexOf("=")+1,XPath.substring(BracketIndex+1).indexOf("]"));
				AttValue=AttValue.replaceAll("'", "");
				
				Vector <String> AllPossibleXPath = new NodesFinder().FindFullPathNodeFromXML(XPath.substring(0,BracketIndex)+"/"+Att , PreparedXPathSet ,AttValue);
				for(int c=0 ; c<AllPossibleXPath.size() ;++c){
					
					XPath=AllPossibleXPath.get(c).replaceAll("/"+Att, "")+XPath.substring(XPath.indexOf("]")+1,XPath.length());
					
					return ParsingPredicateBrackets( FilePath ,  XPath , XPathSet, SourceXPath , PreparedXPathSet );
				}
				
			}
		
		
		
		
		}
		
		
		AnalyzedXPath a = new AnalyzedXPath();
		
		a.GenFrom=SourceXPath;
		a.XPath=XPath;
		XPathSet=new AnalyzedXPath().AddXPath(XPathSet, a);
		return XPathSet;
	}
	
	
	
	public boolean CheckValueInBracketIfNumber(String XPath , boolean OpenBracket){
		
		String Val="";
		if(OpenBracket==true)// [
			for(int i=0 ; i < XPath.length() ; ++i){
				if(XPath.charAt(i)==']')
					break;
				Val+=XPath.charAt(i);
				
			}
		if(OpenBracket==false)//]
			for(int i=XPath.length()-1 ; i >= 0 ; --i){
				if(XPath.charAt(i)=='[')
					break;
				Val+=XPath.charAt(i);
				
			}
			try {
				int val=Integer.parseInt(Val.trim());
				return true;
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				return false;
			}
	}
	
	
	public static boolean isInteger(String s) {
	    return isInteger(s,10);
	}

	public static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
	
	public static void main(String[] args) throws Exception {// For testing purpose
		// TODO Auto-generated method stub
		String FilePath="./ExmplesInputs/BasicConInputs/course.xml";
		String XSDFilePath="./ExmplesInputs/BasicConInputs/course.xsd";
		String XPathFilePath="./ExmplesInputs/BasicConInputs/Set1.txt";
		//String XPathFilePath="coursexpath.txt";
		
		File file = new File(XPathFilePath);
		
		
		
		
		Properties.XMLFileName=FilePath;
		Properties.XSDName=XSDFilePath;
		Properties.XPathFileName=XPathFilePath;
		
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
	
		XSDParser xx = new XSDParser();
		Vector <XSDPathExpression> b = xx.run();
		
		
		 
		String line;
		
		XPathGenerator x = new XPathGenerator();
		Vector <PreparedXPath> a = x.run();
		
		Vector <String> XPaths= new Vector <String>();
		while ((line = bufferedReader.readLine()) != null) {
			String XPath=line;
			if(!XPath.startsWith("%"))
			XPaths.add(XPath);
			
			
			
		}
		
		
		Vector <AnalyzedXPath> XPathSet = new Analyzer().run(a,  new XPathCombiner().run( XPaths)); 
		
		
		System.out.println("XPathSet "+XPathSet.size());
		for(int i=0 ; i <XPathSet.size() ; ++i ){
			System.out.print("XPath Set "+XPathSet.get(i).XPath);
			System.out.print("  Value  "+XPathSet.get(i).Value);
			System.out.println("  Gen  "+XPathSet.get(i).GenFrom);
			//System.out.println("   GenFrom  "+ReXPathSet.get(i).GenFrom);
		}
		
	
		
	}
	
}
