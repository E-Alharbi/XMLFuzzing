package xfuzz.xpath.xsd;

import java.net.MalformedURLException;
import java.util.Random;
import java.util.Vector;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import net.sf.saxon.trans.XPathException;
import xfuzz.xml.builder.ImpXPathContainer;
import xfuzz.xml.builder.NodesFinder;
import xfuzz.xml.builder.Properties;
import xfuzz.xml.builder.SAXON;
import xfuzz.xpath.analyzer.AnalyzedXPath;

public class PreparedXPath {

//The set of XPath that ready to build.

	public String XPath;
	public String Value;
	public boolean IsNewXPath;//false if current otherwise true
	public PreparedXPath(){
		
	}
	public PreparedXPath(String xpath , String value,boolean isnewxpath){
		this.XPath=xpath;
		this.Value=value;
		this.IsNewXPath=isnewxpath;
	}
	public boolean Update (Vector<PreparedXPath> Set ,AnalyzedXPath XPath , Vector <XSDPathExpression> XSDXPathSet , int IndexThatChanged ){
		String XPathWithBrec=new NodesFinder().RemovePredicatesBrackets(XPath.XPath);
		//XPath.XPath=new NodesFinder().RemovePredicatesBrackets(XPath.XPath);
		XSDPathExpression NodeInXSD = new XSDPathExpression().FindNode(XPathWithBrec, XSDXPathSet);
		
		if(new XSDPathExpression().CheckNodeRes(NodeInXSD, XPath.Value , XPath)){
			 Set.get(IndexThatChanged).Value=XPath.Value;	
			 return true;
		}
		else{
			return false;
		}
		
	}
	
	public boolean Add (Vector<PreparedXPath> Set ,PreparedXPath XPath , Vector <XSDPathExpression> XSDXPathSet) throws Exception{
		
		String XPathWithBrec=new NodesFinder().RemovePredicatesBrackets(XPath.XPath);// to find node childrens
		XSDPathExpression NodeInXSD = new XSDPathExpression().FindNode(XPathWithBrec, XSDXPathSet);
		
		
		int count=Integer.parseInt(new SAXON().Count(Properties.XMLFileName, "count("+new NodesFinder().RemovePredicatesBracketsFromTheLAstNodeInThePath(XPath.XPath)+")")); // check the number of nodes in the XML file, if less than max then add it. 
		
		for(int i=0 ; i < Set.size() ; ++i){
			
				if(new NodesFinder().RemovePredicatesBracketsFromTheLAstNodeInThePath(Set.get(i).XPath).equals(new NodesFinder().RemovePredicatesBracketsFromTheLAstNodeInThePath(XPath.XPath))&& Set.get(i).IsNewXPath==true)
					++count;
			
			
		}
		
	
	AllocateNode(XSDXPathSet,Set,XPathWithBrec);
	if(NodeInXSD.max==-1){//-1 mean unbounded
		
		Set.add(XPath);
		return true;
	}
     if(count<NodeInXSD.max){
    	
	Set.add(XPath);
	return true;
	}
	
	new ImpXPathContainer().add(XPath.XPath, "Does not satisfy maxOccurs restriction");

	 if(Properties.SubsetAlgorithm==false){
		 return true;
	 }
		return false;
	}
	
	void AllocateNode (Vector <XSDPathExpression> XSDXPathSet ,Vector<PreparedXPath> Set , String XPath  ){
	
		XSDPathExpression NodeInXSD = new XSDPathExpression().FindNode(XPath, XSDXPathSet);
		int Index=XSDXPathSet.indexOf(NodeInXSD);
		int CorrectPosForTheNode=0;
		for(int i=0 ; i <XSDXPathSet.size() ; ++i ){
			NodeInXSD = new XSDPathExpression().FindNode(new NodesFinder().RemovePredicatesBrackets(XSDXPathSet.get(i).XPath), XSDXPathSet);
			int IndexForCurrentNode=XSDXPathSet.indexOf(NodeInXSD);
			if(Index < IndexForCurrentNode){
				CorrectPosForTheNode=i;
				
			}
		}
		
		
	}
	
}
