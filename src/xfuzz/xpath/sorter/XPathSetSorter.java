package xfuzz.xpath.sorter;

import java.util.Vector;

import org.translet.processor.XPathExpr;

import xfuzz.xml.builder.NodesFinder;
import xfuzz.xml.builder.Properties;
import xfuzz.xpath.xsd.PreparedXPath;
import xfuzz.xpath.xsd.XPathGenerator;
import xfuzz.xpath.xsd.XSDParser;
import xfuzz.xpath.xsd.XSDPathExpression;

public class XPathSetSorter {
/*
 * This class will sort the set before write the XML based on nodes order in the XSD
 * The sorter is built based on bubble sort algorithm with customized the algorithm to compare the two set (XPath set and the set of  path expressions from XSD)  
 * pseudo code
 * loop i on XPath set
 *  A=find XPath index from XSD
 *  while is sorted =false
 *  assume is sorted =true
 *  loop m  on XPath set 
 *  B=find index for current set XPath from XSD
 *  if (A < B)
 *  if(i > m){
 *  update index for XPath in set to m
 *  set boolean to not sorted =false
 *  }
 *  end while
 *  end loop 2
 *  end loop 1
 *  
 */
	public static  void main (String [] args) throws Exception{ // For testing purpose
		
		String FilePath="TextPro.xml";
		String XSDFilePath="TextPro.xsd";
		String XPathFilePath="coursexpath.txt";
		
		Properties.XMLFileName=FilePath;
		Properties.XSDName=XSDFilePath;
		Properties.XPathFileName=XPathFilePath;
		Properties.SubsetAlgorithm=false;
		Vector <PreparedXPath> PreparedSet = new Vector <PreparedXPath> ();
		PreparedSet.add(new PreparedXPath("//A/C/E",null,false));
		PreparedSet.add(new PreparedXPath("//A/B",null,false));
		PreparedSet.add(new PreparedXPath("//A",null,false));
		PreparedSet.add(new PreparedXPath("//A/C",null,false));
		PreparedSet.add(new PreparedXPath("//A/C/D",null,false));
		Vector <PreparedXPath> s = new XPathSetSorter().Sort(new XSDParser().run(), PreparedSet);
		
		
		
		
	
	}
	public Vector <PreparedXPath> Sort (Vector <XSDPathExpression> XSDSet , Vector <PreparedXPath> PreparedSet){
		
		
		for (int i=0 ; i <PreparedSet.size() ; ++i){
			
			XSDPathExpression NodeInXSD = new XSDPathExpression().FindNode(new NodesFinder().RemovePredicatesBrackets(PreparedSet.get(i).XPath), XSDSet);
			int Index=XSDSet.indexOf(NodeInXSD);
		
			boolean IsSorted=false;
			int IndexinTheSet=i;
			while(IsSorted==false){
				IsSorted=true;
			for(int m=0 ; m <PreparedSet.size() ; ++m){
				NodeInXSD = new XSDPathExpression().FindNode(new NodesFinder().RemovePredicatesBrackets(PreparedSet.get(m).XPath), XSDSet);
				int CurrentXPathIndex=XSDSet.indexOf(NodeInXSD);
				if(Index < CurrentXPathIndex)
					if(IndexinTheSet > m){
						PreparedXPath temp = PreparedSet.get(i);
						PreparedSet.remove(PreparedSet.get(i));
						PreparedSet.add(m, temp);
						IsSorted=false;
						IndexinTheSet=m;
						
					}
			}
			
			}
			
		}
		
		return PreparedSet;
	}
	
	void printset (Vector <PreparedXPath> PreparedSet){
		System.out.println("========================");
		for(int i=0 ; i<PreparedSet.size() ; ++i){
			System.out.println(""+PreparedSet.get(i).XPath);
			
		

		}
	}
}
