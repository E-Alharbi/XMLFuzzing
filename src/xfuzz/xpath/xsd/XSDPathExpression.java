package xfuzz.xpath.xsd;

import java.util.Vector;

import xfuzz.xml.builder.ImpXPathContainer;
import xfuzz.xpath.analyzer.AnalyzedXPath;

public class XSDPathExpression {

	public int NodeNum;
	public String XPath;
	public	XSDValTYPES ValueType;
	public int max;// if value  -1 mean unbounded
	public int min;// if value  -1 mean unbounded
	public XSDRestrictions Res;
	
	// To find node in XSD set. It need to check the node restrictions 
	public XSDPathExpression FindNode(String XPath , Vector<XSDPathExpression> XSDSet ){
		
		for(int i=0 ; i <XSDSet.size() ; ++i ){
			if(XPath.equals(XSDSet.get(i).XPath))
			{
				return XSDSet.get(i);
			}
		}
		return null;//not found
	}
	public boolean CheckNodeRes(XSDPathExpression XPath , String Value , AnalyzedXPath AnXPath){
		
		boolean IsVaild=true;
		if(XPath.Res==null){
			
			return true;

		}
		if(XPath.Res.type==XSDRestrictionsTypes.enumeration){
			Enumeration e = (Enumeration) XPath.Res;
			
		
			if(e.CheckValue(Value)==false){
			new ImpXPathContainer().add(AnXPath.XPath, "The value does not satisfy the enumeration restriction");
			IsVaild=false;
			}
			//return  e.CheckValue(Value);
		}
		 if(XPath.Res.type==XSDRestrictionsTypes.inclusive){
			 Inclusive I = (Inclusive) XPath.Res;
			
			 
			 if(I.CheckValue(Integer.parseInt(Value.trim()))==false){
				new ImpXPathContainer().add(AnXPath.XPath, "The value does not satisfy the inclusive restriction");
				IsVaild=false;
			 }
			
		 }
		 
		 if(IsVaild==false)
			 return false;
		 
		 return true;
	}
	
}
