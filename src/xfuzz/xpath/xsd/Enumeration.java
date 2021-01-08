package xfuzz.xpath.xsd;

import java.util.Vector;

public class Enumeration extends XSDRestrictions {

	// Enumeration Restriction
	Vector<String> set;// The set of values 
	
	public Enumeration(){
		set = new Vector<String>();
	}
	
	
	public Vector<String> getSet() {
		   return set;
		} 
	public void setVal(String Val){
		set.add(Val);
	}
	public boolean CheckValue(String Value){// use when xFuzz want to check the value that insert by a user 
		
		for(int i=0 ; i<set.size() ; ++i){
			if(set.get(i).equals(Value))
				return true;//found
		}
		return false;//not found
	}
}
