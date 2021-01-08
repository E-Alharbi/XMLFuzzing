package xfuzz.xpath.analyzer;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import net.sf.saxon.trans.XPathException;
import xfuzz.xml.builder.NodesFinder;
import xfuzz.xml.builder.Properties;
import xfuzz.xml.builder.RandomDataGenerator;
import xfuzz.xml.builder.SAXON;
import xfuzz.xpath.analyzer.Z3.TestFailedException;
import xfuzz.xpath.xsd.Enumeration;
import xfuzz.xpath.xsd.Inclusive;
import xfuzz.xpath.xsd.PreparedXPath;
import xfuzz.xpath.xsd.XSDParser;
import xfuzz.xpath.xsd.XSDRestrictionsTypes;
import xfuzz.xpath.xsd.XSDPathExpression;

public class XPathOperators {

	HashMap map ;
	HashMap StrMap = new HashMap ();// to store string bytes
	public XPathOperators (){
		map= new HashMap();
		map.put("=", "=");
		map.put("<", "<");
		map.put("<=", "<=");
		map.put(">", ">");
		map.put(">=", ">=");
		map.put("!=", "!=");
		map.put("!", "!");
		
	}
	public Object CheckOp(String OP){
		return map.get(OP);
	}
	
	public Vector <AnalyzedXPath> LessOrGreaterThanOP (String XPath , int BracketIndex ,Vector <PreparedXPath>  PreparedXPathSet ,Vector <AnalyzedXPath> XPathSet ,String SourceXPath , int i , String OP) throws Exception{
		
		
		String NodeName=XPath.substring(BracketIndex+1, i);
		
		
		String Value=GetValueAfterOp(XPath,i);
		
		
		
		ParseFormula(XPath,BracketIndex ,XPathSet ,PreparedXPathSet);
	
		
		return XPathSet;
	}
	
	String GetValueAfterOp(String XPath ,int i){
		String Value="";
		boolean IsSeenOpenBr=false;// to solve the problem when the value is a node Ex://student[1][PersonalInfo/name/Fname=//student[2]/PersonalInfo/name/Fname]
		
		for(int m=i+1 ; m <XPath.length() ; ++m ){
			if(XPath.charAt(m)=='[')
				IsSeenOpenBr=true;
			if(XPath.charAt(m)==']' && IsSeenOpenBr==false)
				break;
			if(XPath.charAt(m)==']')
				IsSeenOpenBr=false;
			Value+=XPath.charAt(m);
		}
		
		return Value;
	}
	Vector <String> GetAllPoissbleXPathFRomXMLForNode(String XPath ,Vector <PreparedXPath>  PreparedXPathSet , String att ) throws Exception{
		
		return new NodesFinder().FindFullPathNodeFromXML(XPath , PreparedXPathSet , "");
	}
	
	
	void ParseFormula (String XPath , int BracketIndex , Vector <AnalyzedXPath> XPathSet ,Vector <PreparedXPath>  PreparedXPathSet) throws Exception{
		
		Vector <FormulaElement> FormulaElements = ParseFormulaFromXPath(XPath , BracketIndex);
		
	
		
		
		String FormulaRe =  CreateFormula(FormulaElements ,StrMap ,XPath ,   BracketIndex);
		
		if(FormulaRe==null){ // no results 
			
		}
		else{
		
			for(int i=0 ; i <FormulaElements.size() ; ++i  ){
				
				for(int m=0 ; m <FormulaRe.split("\n").length ; m=m+2 ){
					if(FormulaRe.split("\n")[m].contains(FormulaElements.get(i).Node.replaceAll("/", "").replaceAll("\\[", "").replaceAll("\\]", ""))){

						String NodeV=FormulaRe.split("\n")[m+1].replace(")", "");
						
						Vector <String> AllPossibleXPath;
						if(FormulaElements.get(i).Node.startsWith("//")){
							AllPossibleXPath = GetAllPoissbleXPathFRomXMLForNode(FormulaElements.get(i).Node.trim() , PreparedXPathSet , "");
							
							if(AllPossibleXPath.size()==0)
								AllPossibleXPath.add(FormulaElements.get(i).Node.trim());
						}
						else{
						AllPossibleXPath = GetAllPoissbleXPathFRomXMLForNode(XPath.substring(0,BracketIndex)+"/"+FormulaElements.get(i).Node , PreparedXPathSet , "");
						
						if(AllPossibleXPath.size()==0)
							AllPossibleXPath.add(XPath.substring(0,BracketIndex)+"/"+FormulaElements.get(i).Node);
						}
						
						for(int c=0 ; c<AllPossibleXPath.size() ;++c){
							
							AnalyzedXPath a = new AnalyzedXPath(); // no [ or count in XPath
							a.GenFrom=XPath;
							a.XPath=AllPossibleXPath.get(c);
							
								if(StrMap.containsKey(NodeV.trim())){
							
								
									a.Value=((String)StrMap.get(NodeV.trim()));
									
									
									a.Value=a.Value.replaceAll("'", "");
								}
								else{
									a.Value=NodeV.trim();
								}
							
							
						
							
							XPathSet=new AnalyzedXPath().AddXPath(XPathSet, a);
						}
						
						
					}	
				}
				
				
			}
		}
	}
	
	
	public Vector <FormulaElement> ParseFormulaFromXPath(String XPath , int BracketIndex ) throws Exception{
		Vector <FormulaElement> FormulaElements = new Vector <FormulaElement>();
		String NodeName="";
		String NodeValue="";
		String OP="";
		boolean ISOpSeen=false;
		String TempXPath=XPath.substring(BracketIndex+1);
		boolean IsSeenOpenBr=false;
		String ANDorOR="";
		while(TempXPath.length()!=0){ // parsing between [ ]
			
			
			if(TempXPath.startsWith("and ") || TempXPath.startsWith("or ")){
				if(!NodeName.startsWith("//"))// if the node does not has the full path 
					NodeName=XPath.substring(0,BracketIndex)+"/"+NodeName;
				
				
				if(NodeValue.trim().startsWith("//"))// if the value is a node
				{
					
					SAXON s = new SAXON();
					NodeValue=s.ExecuteXPath(Properties.XMLFileName,new Analyzer().NodePathFinder(NodeValue)); 
					
				}
				FormulaElements.add(new FormulaElement(NodeName , OP ,NodeValue ,ANDorOR , XPath));
				
				if(TempXPath.startsWith("and ")){
				TempXPath=TempXPath.substring(TempXPath.indexOf("and ")+4);
				
				ANDorOR="and";
				
				}
				else{
				TempXPath=TempXPath.substring(TempXPath.indexOf("or ")+3);
				ANDorOR="or";
				
				
				}
				
				NodeName="";
				NodeValue="";
				OP="";
				ISOpSeen=false;
				
			}
			if(ISOpSeen==true && TempXPath.charAt(0)!=']'){
				
				NodeValue+=TempXPath.charAt(0);
			}
			else{
				if(ISOpSeen==true &&IsSeenOpenBr==true)
					NodeValue+=TempXPath.charAt(0);
			}
			
			
		
			
			if(TempXPath.charAt(0)=='[')
				IsSeenOpenBr=true;
			if(TempXPath.charAt(0)==']')
				IsSeenOpenBr=false;
			
			if(!map.containsKey(TempXPath.substring(0, 1))){// for < or <=
				
				if(ISOpSeen==false)
				NodeName+=TempXPath.charAt(0);
				
			}
			else{
			
				ISOpSeen=true;
				if(map.containsKey(TempXPath.substring(0, 2))){
				OP=TempXPath.substring(0, 2);
				TempXPath=TempXPath.substring(1);
				}
				else
				OP=TempXPath.substring(0, 1);	
			}
			
				
			if(TempXPath.charAt(0)==']' && IsSeenOpenBr==false &&TempXPath.trim().length()==1 ){
				if(!NodeName.startsWith("//"))// if the node does not has the full path to avoid forumla errors Ex: (AcadmicMark ) (student[2]/AcadmicMark)
					NodeName=XPath.substring(0,BracketIndex)+"/"+NodeName;
				
				if(NodeValue.trim().startsWith("//"))// if the value is a node
				{
					SAXON s = new SAXON();
					NodeValue=s.ExecuteXPath(Properties.XMLFileName,new Analyzer().NodePathFinder(NodeValue)); 
					
				}
				
				FormulaElements.add(new FormulaElement(NodeName , OP ,NodeValue ,ANDorOR , XPath));

				break;
			}
		TempXPath=TempXPath.substring(1);
			
			
		}
		
		return FormulaElements;
	}
	
	
	
	boolean ConvertStrToBytes(FormulaElement FE , HashMap StrMap){
	
		String NodeValue=FE.Value;
		int IndexTobeRemovedAfter=-1;
		for(int i=NodeValue.length()-1 ; i >=0 ; i--){// to remove any whitespace that can be after ' because if any will effect on converting str to bytes
		if(NodeValue.charAt(i)=='\''){
			IndexTobeRemovedAfter=i;
			break;
		}
		}
		if(IndexTobeRemovedAfter!= -1)
			NodeValue=NodeValue.substring(0, IndexTobeRemovedAfter);
		byte[] array =NodeValue.replaceAll("'", "").getBytes();
		String StrInBytes="";
	    for (int n=0 ; n < array.length ; ++n)
	    	StrInBytes+=array[n];
	   
	    StrMap.put(StrInBytes, NodeValue);
	    // StrMap.put(FE.Node, NodeValue);
	   
	    FE.Value=StrInBytes;
	    return true;
	}
	
	String ConvertStrToBytes(String Val , HashMap StrMap){
		
		byte[] array = Val.getBytes();
		String StrInBytes="";
	    for (int n=0 ; n < array.length ; ++n)
	    	StrInBytes+=array[n];
	   
	    StrMap.put(StrInBytes, Val);
	   
	    //Val=StrInBytes;
	    return StrInBytes;
	}
	
	String AppendXSDResToFormula(String Formula , String XPath , String FormulaFun) throws Exception{
		
		
		if(XPath.split("//").length==3){
			XPath="/"+XPath.split("//")[2];
			
		}
		XSDPathExpression node = new XSDPathExpression().FindNode(new NodesFinder().RemovePredicatesBrackets(XPath).trim(),  new XSDParser().run());
		
         if(node.Res!=null && node.Res.type==XSDRestrictionsTypes.inclusive){
        	 Inclusive IRes= (Inclusive)node.Res;
        	
        	 
        Formula="( and "+Formula + " ( >= "+FormulaFun+" "+IRes.min + ") ( <= "+FormulaFun +" " +IRes.max +"))";

        	
         
         
         }
         if(node.Res!=null && node.Res.type==XSDRestrictionsTypes.enumeration){
        	 Enumeration ERes= (Enumeration)node.Res;
        	 Formula="(and "+Formula +" (or ";
        	 for(int i=0 ; i<ERes.getSet().size() ; ++i){
        		
        		 
        		 Formula+="( = "+FormulaFun +" "+ ConvertStrToBytes(ERes.getSet().get(i) ,StrMap) + " )";
        		
        		
     		}
        	 Formula+="))";
         }
         
         
         
         return Formula;
	}
	
	String CreateFormula (Vector <FormulaElement> FormulaElements , HashMap StrMap , String XPath , int  BracketIndex) throws Exception{
		String Funs="";
		String Formula="";
		
		
	
		
			
		for(int i=0 ; i <FormulaElements.size() ; ++i  ){
			
			if(FormulaElements.get(i).Value.trim().startsWith("'")==true){// if start with ' that's mean is a string
				
			  
				ConvertStrToBytes(FormulaElements.get(i) ,StrMap );
			}
			
			
			Funs+="( "+FormulaElements.get(i).Node +" Int )";
		
			String Node="";
			if(FormulaElements.get(i).Op.equals("!=")){
				 Node="(not ( "+"="+" "+ FormulaElements.get(i).Node +" "+ FormulaElements.get(i).Value +"))";

			}
			else{
			 Node=" ( "+FormulaElements.get(i).Op+" "+ FormulaElements.get(i).Node +" "+ FormulaElements.get(i).Value +")";
			}
			Node=AppendXSDResToFormula(Node,XPath.substring(0,BracketIndex)+"/"+FormulaElements.get(i).Node ,FormulaElements.get(i).Node);

			
			if(i > 0 && !FormulaElements.get(i).ConconditionOP.equals(""))	
	        Formula="( "+FormulaElements.get(i).ConconditionOP + Formula +Node + ")";
			else
			Formula+=Node;	
			
		
		}
		
		
		
		Funs=Funs.replace("/", "");
		Formula=Formula.replace("/", "");
		Funs=Funs.replace("[", "");
		Formula=Formula.replace("[", "");
		Funs=Funs.replace("]", "");
		Formula=Formula.replace("]", "");
		
		String FormulaRe=new Z3().CheckFormula("(benchmark tst :extrafuns ("+Funs+") :formula "+Formula);
		
		if(FormulaRe==null){
		new UnsatFormulaContainer().Add(XPath, Formula);
		
		}
		return FormulaRe;
	}
	
}
