package xfuzz.xpath.solver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

import xfuzz.validator.Validator;
import xfuzz.xml.builder.NodesFinder;
import xfuzz.xml.builder.Properties;
import xfuzz.xml.builder.SAXON;
import xfuzz.xpath.analyzer.AnalyzedXPath;
import xfuzz.xpath.analyzer.Analyzer;
import xfuzz.xpath.classifier.XPathClassifier;
import xfuzz.xpath.combiner.XPathCombiner;
import xfuzz.xpath.xsd.PreparedXPath;
import xfuzz.xpath.xsd.XPathGenerator;
import xfuzz.xpath.xsd.XSDParser;
import xfuzz.xpath.xsd.XSDPathExpression;

public class XPathSetSolver {

	/*
	 * This class will decide how many file will be generated based on the XPath set.
	 * Ex:
	 * //student[degree='PhD'] >> Go to container  1 
       //student[4] >> Go to container 1 
       //student[count(//student)+5] >> some of them will go to container 1 and the other go to the other  containers
       //student[degree='BSc'] >> Go to container 2 
       //student[degree='MSc']  >> Go to container 3 
        * The above set need a least three different containers to  satisfy them.
        * 
	 */
	
		
public Vector<Vector<AnalyzedXPath>> run ( Vector<Vector<AnalyzedXPath>> ListOfClassifiedXPathSet) throws Exception{
		
	if(Properties.SubsetAlgorithm==true){
		return SubsetAlgorithm(ListOfClassifiedXPathSet);
	}
	else
	{
		return MaxTestCasesAlgorithm(ListOfClassifiedXPathSet);
	}
	
	   
		
	}
	

public Vector<Vector<AnalyzedXPath>> MaxTestCasesAlgorithm ( Vector<Vector<AnalyzedXPath>> ListOfClassifiedXPathSet) throws Exception{
	
	 Vector<AnalyzedXPath> XPathSet= new Vector<AnalyzedXPath>();
	 Vector<AnalyzedXPath> ReturnedXPathSet= new Vector<AnalyzedXPath>();
	
	 for(int i=0 ;i<ListOfClassifiedXPathSet.size() ; ++i)
		 XPathSet.addAll(ListOfClassifiedXPathSet.get(i));
	 ListOfClassifiedXPathSet.removeAllElements();//we do not need the elements anymore
	 for(int v=0; v  <XPathSet.size() ; ++v){
		 if(new XSDPathExpression().CheckNodeRes(new XSDPathExpression().FindNode(new NodesFinder().RemovePredicatesBrackets(XPathSet.get(v).XPath), new XSDParser().run()), XPathSet.get(v).Value,XPathSet.get(v)))
			 for(int e=0; e  <XPathSet.size() ; ++e){
			 if(XPathSet.get(v).GenFrom.equals(XPathSet.get(e).GenFrom))
			 ReturnedXPathSet.add(XPathSet.get(e));
			 }
		 
	
	 if(ReturnedXPathSet.size()>0)
	 for(int i= 0 ; i <XPathSet.size() -1; ++i){
		 if(v==i)
			 ++i;
		boolean Found=false;
		 for(int m=0 ; m<ReturnedXPathSet.size() ; ++m ){
			 if(XPathSet.get(i).XPath.equals(ReturnedXPathSet.get(m).XPath)) {
				 Found=true;
				 break;
			 }
			 
		 }
		 if(Found==false){
			 if(new XSDPathExpression().CheckNodeRes(new XSDPathExpression().FindNode(new NodesFinder().RemovePredicatesBrackets(XPathSet.get(i).XPath), new XSDParser().run()), XPathSet.get(i).Value,XPathSet.get(i)))
	
			 
			 ReturnedXPathSet.add(XPathSet.get(i));
		 }
		 
		 
		 
	 }
	
	
	 ListOfClassifiedXPathSet.add(new Vector (ReturnedXPathSet));// using new vector to avoid delete the elements 
	 
	 ReturnedXPathSet.removeAllElements();
	 
}
	 
	Vector ReVector = new Vector();       
	for (Vector v : ListOfClassifiedXPathSet)
		if(v.size() > ReVector.size())
			ReVector=new Vector (v);
	ListOfClassifiedXPathSet.removeAllElements();
	ListOfClassifiedXPathSet.add(ReVector);
	 return ListOfClassifiedXPathSet;
	 
}

public Vector<Vector<AnalyzedXPath>> SubsetAlgorithm ( Vector<Vector<AnalyzedXPath>> ListOfClassifiedXPathSet){
	 Vector<Vector<AnalyzedXPath>> FinalListOfXPathSet= new Vector<Vector<AnalyzedXPath>>();
		for(int d=0 ;d<ListOfClassifiedXPathSet.size() ; d++){
		Vector<Vector<AnalyzedXPath>> ListOfXPathSet= new Vector<Vector<AnalyzedXPath>>();
		Vector<AnalyzedXPath> Set=ListOfClassifiedXPathSet.get(d);
		for(int i=0 ; i <Set.size() ; ++i ){
			boolean IsAdded=false;
			
			for(int m=0 ;m<ListOfXPathSet.size() ; ++m ){
				if(IsInTheSet(ListOfXPathSet.get(m),Set.get(i).XPath)==false){
					
					ListOfXPathSet.get(m).add(Set.get(i));
					IsAdded=true;
				}
			}
			if(IsAdded==false)
			{
				if(ListOfXPathSet.size()!=0){
					Vector<Vector<AnalyzedXPath>> TempListOfXPathSet= new Vector<Vector<AnalyzedXPath>>();
				for(int v=0 ; v<ListOfXPathSet.size() ;++v){
					Vector<AnalyzedXPath> NewSet = new Vector<AnalyzedXPath>();
					for(int b=0 ;b<ListOfXPathSet.get(v).size();++b){
						if(!Set.get(i).XPath.equals(ListOfXPathSet.get(v).get(b).XPath)){
							
						NewSet.add(ListOfXPathSet.get(v).get(b));
						}
					}
					NewSet.add(Set.get(i));
					TempListOfXPathSet.add(NewSet);
				}
				
				ListOfXPathSet.addAll(TempListOfXPathSet);
				
				}
				else{
				Vector<AnalyzedXPath> NewSet = new Vector<AnalyzedXPath>();
				NewSet.add(Set.get(i));
				ListOfXPathSet.add(NewSet);
				}
				//NewSet.add(Set.get(i));
				//ListOfXPathSet.add(NewSet);
			}
			
			
			
			
		}
		FinalListOfXPathSet.addAll(ListOfXPathSet);
}
		return FinalListOfXPathSet;
}
	
	
	 boolean IsInTheSet (Vector<AnalyzedXPath> XPathSet , String XPath){
		 
		 for(int i=0 ; i<XPathSet.size() ; ++i){
				
				if(XPathSet.get(i).XPath.equals(XPath)){
					return true;
					
				}
			}
			return false;
	 }
	 
	 public static  void main (String [] args) throws Exception{ // For testing purpose
		 String FilePath="./ExmplesInputs/BasicConInputs/course.xml";
			String XSDFilePath="./ExmplesInputs/BasicConInputs/course.xsd";
			String XPathFilePath="./ExmplesInputs/BasicConInputs/Set1.txt";
			
			
			Properties.XMLFileName=FilePath;
			Properties.XSDName=XSDFilePath;
			Properties.XPathFileName=XPathFilePath;
			Properties.SubsetAlgorithm=true;
			//Properties.ProcessingXPathForCombiner=true;
			Validator v = new Validator();
			XPathGenerator x = new XPathGenerator();
			Vector <PreparedXPath> a = x.run();
		
			
			XSDParser xx = new XSDParser();
			Vector <XSDPathExpression> b = xx.run();
			
			File XPathfile = new File(Properties.XPathFileName);
			FileReader fileReader = new FileReader(XPathfile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			Vector <String> RawXPathSet=new Vector <String>();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				
				//System.out.println(x);
				RawXPathSet.add(line);
				
			}
			 
			bufferedReader.close();
			 
			 
			Analyzer analyzer = new Analyzer();
			Vector <AnalyzedXPath> XPathSet=analyzer.run(a,new XPathCombiner().run( RawXPathSet));
			//XPathCombiner XC = new XPathCombiner();
			//XPathSet=analyzer.run(a,XC.run(XPathSet));// run the analyzer again after combine xpath
			System.out.println("XPathSet "+XPathSet.size());
			
			
			 Vector<Vector<AnalyzedXPath>> ListOfClassifiedXPathSet= new XPathClassifier().run(XPathSet);
			
	         Vector<Vector<AnalyzedXPath>> ListOfXPathSet= new XPathSetSolver().run(ListOfClassifiedXPathSet);
	         System.out.println("ListOfXPathSet.size() "+ListOfXPathSet.size());
	     	for(int L=0 ; L <ListOfXPathSet.size() ; ++L ){
				System.out.println("List : "+L +" List size "+ListOfXPathSet.get(L).size());
				for(int i=0 ; i < ListOfXPathSet.get(L).size() ; ++i){
					System.out.print("element : "+ListOfXPathSet.get(L).get(i).XPath);
					System.out.println("  value: "+ListOfXPathSet.get(L).get(i).Value);
				}
	     	}
			 
		}
		
}
