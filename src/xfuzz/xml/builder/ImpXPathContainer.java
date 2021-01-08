package xfuzz.xml.builder;

import java.util.Vector;

public class ImpXPathContainer {

	// A container for impossible test cases 
	static Vector <ImpXPath> Container =  new  Vector <ImpXPath>();
	
	public  void add (String XPath , String Reason ){
		
		Container.add(new ImpXPath(XPath , Reason) );
		
	}
	public String get (String XPath){
		System.out.println("Imp XPath Container "+Container.size());
		for (int i=0 ; i< Container.size() ; ++i){
			if(Container.get(i).XPaht.equals(XPath)){
				return Container.get(i).ImpReason;
			}
		}
		
		return null;
	}
	
}

class ImpXPath{
	
	String XPaht;
	String ImpReason;
	
	public ImpXPath (String xpath , String impReason){
		this.XPaht= xpath;
		this.ImpReason= impReason;
	}
}