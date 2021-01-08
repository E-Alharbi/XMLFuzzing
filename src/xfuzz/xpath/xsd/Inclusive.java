package xfuzz.xpath.xsd;

public class Inclusive extends XSDRestrictions {

	public int min;
	public int max;
	
	
public boolean CheckValue(int Value){// use when xFuzz want to check the value that enter by a user 
		
		if(Value <= max && Value >=min  )
			return true;
		else
			return false;
	}

}
