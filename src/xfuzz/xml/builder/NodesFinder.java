package xfuzz.xml.builder;

import java.util.Vector;

import xfuzz.xpath.xsd.PreparedXPath;
import xfuzz.xpath.xsd.XPathGenerator;
import xfuzz.xpath.xsd.XSDParser;
import xfuzz.xpath.xsd.XSDRestrictions;
import xfuzz.xpath.xsd.XSDValTYPES;
import xfuzz.xpath.xsd.XSDPathExpression;

public class NodesFinder {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		String XSDFilePath="course.xsd";
		XSDParser x = new XSDParser();
		Vector <XSDPathExpression> a = x.run();
		Vector b = new NodesFinder().FindChildrenNodes(a, "//course/student","/student");
		for(int i=0 ; i<b.size() ; ++i ){
			System.out.println(b.get(i));
		}
		System.out.println(new NodesFinder().RemovePredicatesBrackets("//course[1]/student[4]"));
	}

	//Find children nodes for a node
	public Vector<XSDPathExpression> FindChildrenNodes(Vector <XSDPathExpression> XPathSet,String TargetXPath , String SubXPath){
		Vector <XSDPathExpression> Results= new Vector<XSDPathExpression>();
		
		
		for(int i=0 ; i<XPathSet.size() ; ++i ){
			String []XSDPath= XPathSet.get(i).XPath.split("/");//arr2
			String [] XPath=TargetXPath.split("/");//arr1
			boolean IsChlidren=true;
			int index=0;
			for(int m=0 ; m<XPath.length;++m){
				index=m;
				if(m>=XSDPath.length){
					IsChlidren=false;
					break;
				}
				String Ele=RemovePredicatesBrackets(XPath[m]);
				
				if(!XSDPath[m].equals(Ele))
					IsChlidren=false;
					
			}
			if(IsChlidren==true){
				
				String FullXPath=TargetXPath;
				for(int n=index+1 ; n <XSDPath.length;++n )
					FullXPath+="/"+XSDPath[n];
				
				XSDPathExpression p = new XSDPathExpression();
				
				p.ValueType=XPathSet.get(i).ValueType;
				p.max=XPathSet.get(i).max;
				p.min=XPathSet.get(i).min;
				p.XPath=FullXPath;
				p.Res=XPathSet.get(i).Res;
				Results.add(p);
				
			}
			
		}
		
		
	
		
		return Results;
	}
	public String RemovePredicatesBrackets (String XPath){
		boolean IsBracket=false;
		String Re="";
		for(int i=0 ; i<XPath.length() ; ++i){
			
			if(XPath.charAt(i)=='[')
				IsBracket=true;
			if(XPath.charAt(i)==']')
				IsBracket=false;
			if(IsBracket==false)
			 Re+=XPath.charAt(i);
		}
		Re=Re.replaceAll("]", "");
		return Re;
	}
	public String RemovePredicatesBracketsFromTheLAstNodeInThePath(String XPath){
		boolean IsBracket=false;
		
		int BracketCharIndex=XPath.length(); // if there is no [] at the end of XPAth //course/student[1]/PersonalInfo
		for(int i=XPath.length()-1 ; i>=0 ; --i){
			
			if(XPath.charAt(i)==']')
				IsBracket=true;
			if(XPath.charAt(i)=='['){
				IsBracket=false;
				BracketCharIndex=i;
			}
			
			if(XPath.charAt(i)=='/')
				break;
		}
		//System.out.println(BracketCharIndex);
		return XPath.substring(0, BracketCharIndex);
	}
	// find full path EX: NodeNmae = student return //course/student
	public String FindFullPathForNodeFromXSD(Vector <XSDPathExpression> XPathSet , String NodeName){
		
		for(int i=0;i<XPathSet.size() ;++i){
			String XPath=XPathSet.get(i).XPath;
			int ForwardSlashIndex=0;
			while(true ){
				if(XPath.equals(NodeName.trim())){
				
					return XPathSet.get(i).XPath;
				}
				
				//XPath=XPath.substring(1);
				
				if(XPath.contains("/")){
					
				while(ForwardSlashIndex==XPath.indexOf("/")){
				XPath=XPath.substring(XPath.indexOf("/"));
				if(ForwardSlashIndex==XPath.indexOf("/"))
			    XPath=XPath.substring(1);
				
				}
			
				ForwardSlashIndex=XPath.indexOf("/");
				}
				else
			     break;
			      
				if(XPath.equals(""))
					break;
			}
			
		}
		return "";//not found
	}
	
	public Vector <String> FindFullPathNodeFromXML( String XPath ,Vector <PreparedXPath>  PreparedXPathSet , String AttValue) throws Exception
	{
		/*
		 * This mthod finds all xpath for a xpath from XML 
		 * Ex: 
		 * if we have /course/student[3]/degree , it will return /course[1]/student[3]/degree[1]
		 * The nodes spilt the xpath into array 
		 * arr1[0]=course
		 * arr[1]=student[3]
		 * arr[2]=degree
		 * 
		 * arr2[0]=course[1]  >>the brackets  will be removed  in matching  because arr1[0] has not bracket
		 * arr2[1]=student[3] >> will not be removed 
		 * arr2[2]=degree[1] >> will be removed in matching 
		 * 
		 * then match each element from arr1 to elements in arr2
		 * - if the element in arr1 has bracket , the brackets in arr2 element will not be  removed otherwise,the brackets will be removed  
		 */
		
		
		
		Vector <String> XPaths= new Vector<String>();
	
		Vector <PreparedXPath> SetOfXPath = PreparedXPathSet;
		
		String[] arr1 = XPath.split("/");
		for(int i=0 ; i < SetOfXPath.size() ; ++i){
			String[] arr2 = SetOfXPath.get(i).XPath.split("/");
			
			if(arr1.length==arr2.length){
			
				boolean Matched=false;
				for(int m=0 ; m <arr1.length ; ++m){
					
					if(arr1[m].contains("[")){
						if(arr1[m].equals(arr2[m])){
							Matched=true;
						}
					
					else{
						Matched=false;
						break;
					}
					}
					else{
						if(arr1[m].trim().equals(RemovePredicatesBrackets(arr2[m]))){
							Matched=true;
						}
						else{
							Matched=false;
							break;
						}
					}
				}
				
				if(Matched==true){
                  if(!AttValue.equals("")){
                	  if(AttValue.equals(SetOfXPath.get(i).Value)){
					
					XPaths.add(SetOfXPath.get(i).XPath);
					
                	  }
                  }
                  else{
                	 
  					XPaths.add(SetOfXPath.get(i).XPath);
                  }
				}
			}
		}
		return XPaths;
	}
}
