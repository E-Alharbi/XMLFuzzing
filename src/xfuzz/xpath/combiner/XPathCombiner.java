package xfuzz.xpath.combiner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

import xfuzz.xml.builder.NodesFinder;
import xfuzz.xml.builder.Properties;
import xfuzz.xpath.analyzer.AnalyzedXPath;
import xfuzz.xpath.analyzer.Analyzer;
import xfuzz.xpath.analyzer.FormulaElement;
import xfuzz.xpath.analyzer.XPathOperators;
import xfuzz.xpath.xsd.PreparedXPath;
import xfuzz.xpath.xsd.XPathGenerator;

public class XPathCombiner {
/*
 * Combiner combines the XPath set that aim to change the same node and solve the constraints
 */
	
	public Vector<String> run (Vector <String> XPathSet) throws Exception{
		
		Vector <String> CombinedXPathSet= new Vector<String>();
		
		
		Vector <FormulaElement> ReFormulaElements = new Vector <FormulaElement>();
		for(int i=0 ; i < XPathSet.size() ; ++i){
			
			String XPath =new Analyzer().NodePathFinder(XPathSet.get(i));
			
			boolean SeenBracket=false;
			int BracketIndex=0;
			boolean ISMetAnyOFTheseConditions=false;
			for(int m = 0 ; m < XPath.length() ; ++m){
				if(XPath.charAt(m)=='[' && new Analyzer().CheckValueInBracketIfNumber(XPath.substring(m+1) , true) == false){
					
					SeenBracket=true;
					BracketIndex=m;
					
					
				}
				
				if(XPath.charAt(m)==']' && new Analyzer().CheckValueInBracketIfNumber(XPath.substring(0,m) , false)==false){
					
					SeenBracket=false;
					
				}
	            
				if(SeenBracket==true && new XPathOperators().CheckOp(String.valueOf(XPath.charAt(m))) !=null ){ // if the XPath contain predicate //student[degree='MSc'], This will be converted to //student/degree 
					ISMetAnyOFTheseConditions=true;
					
					String OP=(String) new XPathOperators().CheckOp(String.valueOf(XPath.charAt(m)));
					Vector <FormulaElement> FormulaElements = new XPathOperators().ParseFormulaFromXPath(XPath , BracketIndex);
				
					for(FormulaElement E : FormulaElements){
					
					Vector <String> AllPossibleXPath =  new NodesFinder().FindFullPathNodeFromXML(E.Node.trim() , new XPathGenerator().run() , "");
					
					if(AllPossibleXPath.size()==0)
				     ReFormulaElements.add(new FormulaElement(E.Node.trim() , E.Op , E.Value , E.ConconditionOP ,XPath  ));

					for(int c=0 ; c<AllPossibleXPath.size() ;++c){
						
						ReFormulaElements.add(new FormulaElement(AllPossibleXPath.get(c) , E.Op , E.Value , E.ConconditionOP ,XPath  ));
					}
				}
				break;// ParseFormulaFromXPath will parse the remain restrictions  
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
					
					Vector <String> AllPossibleXPath = new NodesFinder().FindFullPathNodeFromXML(XPath.substring(0,BracketIndex)+"/"+Att , new XPathGenerator().run() ,AttValue);
					for(int c=0 ; c<AllPossibleXPath.size() ;++c){
					
						XPath=AllPossibleXPath.get(c).replaceAll("/"+Att, "")+XPath.substring(XPath.indexOf("]")+1,XPath.length());
						
						//return ParsingPredicateBrackets( FilePath ,  XPath , XPathSet, SourceXPath , PreparedXPathSet );
					}
					
				}
				

			}
		
			if(	ISMetAnyOFTheseConditions==false){
				CombinedXPathSet.add(XPath);
			}
		}
		
		Vector <FormulaElement> RemovedFormulaElements = new Vector <FormulaElement>();// to avoid  java.util.ConcurrentModificationException
		
		
		
		while(!ReFormulaElements.isEmpty()){
			String GeneratedXPath=ReFormulaElements.get(0).Node.substring(0,GetLastSlashIndex(ReFormulaElements.get(0).Node))+"[";
			
			boolean IsFirstRes=true;
			Vector <FormulaElement> DepElements = new Vector <FormulaElement>();
               for(FormulaElement FE : ReFormulaElements){
				
				if( (ReFormulaElements.get(0).GenFrom.equals(FE.GenFrom))){
					GeneratedXPath+=CreateXPath(FE ,IsFirstRes);
					IsFirstRes=false;
					DepElements.add(FE);
				}
					
		}
               
               ReFormulaElements.remove(0) ;
               
               for(FormulaElement SE : DepElements){    
            	   for(FormulaElement FE : ReFormulaElements){
            		   if( FE.Node.equals(SE.Node) && !FE.GenFrom.equals(SE.GenFrom) && !RemovedFormulaElements.contains(SE) ){
            			   GeneratedXPath+=CreateXPath(FE,false);
            			   
            			   for(FormulaElement ELE : ReFormulaElements){
            				   if( (ELE.GenFrom.equals(FE.GenFrom) && !ELE.equals(FE) )){
            					   GeneratedXPath+=CreateXPath(ELE,false);
            					  
            						 RemovedFormulaElements.add(ELE);
            			   }
            				   
            			   }
            			   
            			   RemovedFormulaElements.add(FE);
            		   }
            	   }
            	   
               }
               ReFormulaElements.removeAll(DepElements);
               ReFormulaElements.removeAll(RemovedFormulaElements);
               GeneratedXPath+="]";
           	CombinedXPathSet.add(GeneratedXPath);
		
		}
		// use stack 
		//return CombineXPath(ListOfCombinedXPathSet);
		return CombinedXPathSet;
	}
	int GetLastSlashIndex(String XPath){
		int index=0;
		for(int i=0 ; i <XPath.length() ; ++i ){
			if(XPath.charAt(i)=='/')
				index=i;
		}
		return index;
	}
	String CreateXPath(FormulaElement Elemenet , boolean IsFirstRes){
		if(IsFirstRes==true){
			if(!Elemenet.Node.startsWith("//"))
				return Elemenet.Node.substring(GetLastSlashIndex(Elemenet.Node)+1,Elemenet.Node.length()) +" "+Elemenet.Op +Elemenet.Value;
				else
					return Elemenet.Node +" "+Elemenet.Op +Elemenet.Value;
		
		}
		
		
		if(!Elemenet.ConconditionOP.trim().equals("") ){
			if(!Elemenet.Node.startsWith("//"))
			return Elemenet.ConconditionOP+" "+Elemenet.Node.substring(GetLastSlashIndex(Elemenet.Node)+1,Elemenet.Node.length()) +" "+Elemenet.Op +Elemenet.Value;
			else
				return Elemenet.ConconditionOP+" "+Elemenet.Node +" "+Elemenet.Op +Elemenet.Value;
	
			}
			else{
				if(!Elemenet.Node.startsWith("//"))
				return " and "+Elemenet.Node.substring(GetLastSlashIndex(Elemenet.Node)+1,Elemenet.Node.length()) +" "+Elemenet.Op  +Elemenet.Value;
				else
				return " and "+Elemenet.Node +" "+Elemenet.Op  +Elemenet.Value;

			}   
		
	}
	public Vector<String> CombineXPath (Vector<Vector<AnalyzedXPath>> ListOfCombinedXPathSet){
		 Vector <String> ANewSetOfXPath= new Vector<String>();
		 for(int L=0 ; L <ListOfCombinedXPathSet.size() ; ++L ){
				System.out.println("List : "+L);
				for(int i=0 ; i < ListOfCombinedXPathSet.get(L).size() ; ++i){
					System.out.print("element : "+ListOfCombinedXPathSet.get(L).get(i).XPath);
					System.out.println("  GenFrom: "+ListOfCombinedXPathSet.get(L).get(i).GenFrom);
					
					String XPath=ListOfCombinedXPathSet.get(L).get(i).GenFrom;
					for(int y=0 ;y < ListOfCombinedXPathSet.get(L).size() ; ++y ){
					
						for(int m=0 ; m <  ListOfCombinedXPathSet.get(L).get(y).GenFrom.length() ; ++m){
							
							if(ListOfCombinedXPathSet.get(L).get(y).GenFrom.charAt(m)=='[' && new Analyzer().CheckValueInBracketIfNumber(ListOfCombinedXPathSet.get(L).get(y).GenFrom.substring(m+1) , true) == false &&ListOfCombinedXPathSet.get(L).get(y).GenFrom.charAt(m+1)!='@'){
								
								
									if(XPath.endsWith("]")){
									XPath=XPath.substring(0, XPath.length()-1);
									
								}
								
									
									if(!XPath.contains(ListOfCombinedXPathSet.get(L).get(y).GenFrom.substring(m+1,ListOfCombinedXPathSet.get(L).get(y).GenFrom.length()).replaceAll("\\]", "")))
									XPath+=" and "+ListOfCombinedXPathSet.get(L).get(y).GenFrom.substring(m+1,ListOfCombinedXPathSet.get(L).get(y).GenFrom.length());
								  
									break;
								
							}
							
						}
						
						
					}
					
					
					if(!XPath.endsWith("]"))// in case a [ is missing
						XPath+="]";
					ANewSetOfXPath.add(XPath);
				}
	     	}
		 
		 return ANewSetOfXPath;
	}

	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		String FilePath="Example.xml";
		String XSDFilePath="Example.xsd";
		String XPathFilePath="ExampleXPathSet.txt";
		
		Properties.XMLFileName=FilePath;
		Properties.XSDName=XSDFilePath;
		Properties.XPathFileName=XPathFilePath;
		Properties.SubsetAlgorithm=false;
		//Properties.ProcessingXPathForCombiner=true;
		   File XPathfile = new File(Properties.XPathFileName);
	   		FileReader fileReader = new FileReader(XPathfile);
	   		BufferedReader bufferedReader = new BufferedReader(fileReader);
	   	
	   		
	   		
	   		String line;
	   	 Vector <String> RawXPathSet=new Vector <String>();
	   		while ((line = bufferedReader.readLine()) != null) {
	   			
	   			//System.out.println(x);
	   			if(!line.startsWith("%"))
	   			RawXPathSet.add(line);
	   			
	   		}
	   		 
	   		bufferedReader.close();
		XPathGenerator x = new XPathGenerator();
		Vector <PreparedXPath> a = x.run();
		//Vector <String> ANewSetOfXPath = new XPathCombiner().run(new Analyzer().run(a, RawXPathSet));
		Vector <String> ANewSetOfXPath = new XPathCombiner().run( RawXPathSet);
		for(int m=0 ; m <  ANewSetOfXPath.size() ; ++m){
			System.out.println("Com "+ANewSetOfXPath.get(m));
		}
		
	}
	
}
