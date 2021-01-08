package xfuzz.xpath.analyzer;

import java.util.Vector;

public class UnsatFormulaContainer {

	// A container for unsat formulas 
	
	public static Vector <UnsetFormula> UnsetXPath= new Vector <UnsetFormula>();;
	
	
	public void Add (String XPath , String Formula){
		UnsetXPath.add(new UnsetFormula(XPath,Formula) );
		
	}
	
	
	public static String getXPath(int i){
		
		return   UnsetXPath.get(i).XPath ;
	}
    public static String getFormula(int i){
		
		return  " \n Formula :"+ UnsetXPath.get(i).Formula;
	}
}
class UnsetFormula {
	
	String XPath;
	String Formula;
	
	public UnsetFormula (String xpath , String Formula){
		this.XPath=xpath;
		this.Formula=Formula;
	}
}
 
