package xfuzz.xpath.classifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

import xfuzz.validator.Validator;
import xfuzz.xml.builder.Properties;
import xfuzz.xpath.analyzer.AnalyzedXPath;
import xfuzz.xpath.analyzer.Analyzer;
import xfuzz.xpath.combiner.XPathCombiner;
import xfuzz.xpath.solver.XPathSetSolver;
import xfuzz.xpath.xsd.PreparedXPath;
import xfuzz.xpath.xsd.XPathGenerator;
import xfuzz.xpath.xsd.XSDParser;
import xfuzz.xpath.xsd.XSDPathExpression;

public class XPathClassifier {

	
	/*
	 * Classifier will classify the set xpath based on the second node in xpath
	 * 
	 * 	//if the XPath gen from same XPath then keep in one set
		// or classify by the second node 
	 */
	

	public Vector<Vector<AnalyzedXPath>> run (Vector<AnalyzedXPath> Set){
		
		//if the XPath gen from same XPath then keep in one set
		// or classify by the second node 
		
		Vector<Vector<AnalyzedXPath>> ListOfClassifiedXPathSet= new Vector<Vector<AnalyzedXPath>>();
		while(!Set.isEmpty()){
			String  [] SecondNodeInXPath=Set.get(0).XPath.split("/");
			
			Vector<AnalyzedXPath> List= new Vector<AnalyzedXPath>();
			for(int m=0 ; m <Set.size() ; ++m ){
				String  [] CurrentSecondNodeInXPath=Set.get(m).XPath.split("/");
				
				if(CurrentSecondNodeInXPath[3].equals(SecondNodeInXPath[3]) ||  Set.get(m).GenFrom.equals(Set.get(0).GenFrom)){
				//if(Set.get(m).GenFrom.equals(Set.get(0).GenFrom)){
	
				List.add(Set.get(m));
					//Set.remove(Set.get(m));
				}
		}
			Set.removeAll(List);
			ListOfClassifiedXPathSet.add(List);
			
	}
		return ListOfClassifiedXPathSet;
	}
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String FilePath="./ExmplesInputs/BasicConInputs/course.xml";
		String XSDFilePath="./ExmplesInputs/BasicConInputs/course.xsd";
		String XPathFilePath="./ExmplesInputs/BasicConInputs/Set1.txt";
		
		
		Properties.XMLFileName=FilePath;
		Properties.XSDName=XSDFilePath;
		Properties.XPathFileName=XPathFilePath;
		
		Validator v = new Validator();
		XPathGenerator x = new XPathGenerator();
		Vector <PreparedXPath> a = x.run();
	
		
		XSDParser xx = new XSDParser();
		Vector <XSDPathExpression> b = xx.run();
		
		String line;
		File XPathfile = new File(Properties.XPathFileName);
		FileReader fileReader = new FileReader(XPathfile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		 Vector <String> RawXPathSet=new Vector <String>();
			while ((line = bufferedReader.readLine()) != null) {
				
				
				RawXPathSet.add(line);
				
			}
			 
			bufferedReader.close();
		
		 
		 
		Analyzer analyzer = new Analyzer();
		Vector <AnalyzedXPath> XPathSet=analyzer.run(a,new XPathCombiner().run( RawXPathSet));
		
		System.out.println("XPathSet "+XPathSet.size());
		 Vector<Vector<AnalyzedXPath>> ListOfXPathSet= new XPathClassifier().run(XPathSet);
		 System.out.println("XPathSet "+ListOfXPathSet.size());
		 
			for(int L=0 ; L <ListOfXPathSet.size() ; ++L ){
				System.out.println("List : "+L);
				for(int i=0 ; i < ListOfXPathSet.get(L).size() ; ++i){
					System.out.print("element : "+ListOfXPathSet.get(L).get(i).XPath);
					System.out.println("  value: "+ListOfXPathSet.get(L).get(i).Value);
				}
	     	}
	}
}
