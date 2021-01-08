package xfuzz.xpath.xsd;

import java.util.Random;
import java.util.Vector;

import xfuzz.xml.builder.Properties;

public class Tester {

	// Tester for XSD Parser 
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String FilePath="course.xml";
		String XSDFilePath="course.xsd";
		String XPathFilePath="coursexpath.txt";
		
		Properties.XMLFileName=FilePath;
		Properties.XSDName=XSDFilePath;
		Properties.XPathFileName=XPathFilePath;
		XSDParser x = new XSDParser();
		 Vector <XSDPathExpression> a = x.run();
		 System.out.println(a.size());
		
		 for(int i=0 ; i<a.size() ; ++i){
			 System.out.println("Path :"+a.get(i).XPath +" Value Type: "+a.get(i).ValueType + " Max: " + a.get(i).max +" Min: "+a.get(i).min );
			 if(a.get(i).Res!=null)	{
				 if(a.get(i).Res.type==XSDRestrictionsTypes.enumeration){
			 Enumeration Res = (Enumeration) a.get(i).Res;
			 for(int m=0 ; m < Res.getSet().size() ; ++m){
				 System.out.println("Res "+Res.getSet().get(m));
				 System.out.println("Res "+Res.base);
			 }
				 }
			 if(a.get(i).Res.type==XSDRestrictionsTypes.inclusive){
				 Inclusive Res = (Inclusive) a.get(i).Res;
				 System.out.println(" Res.min "+Res.min);
				 System.out.println(" Res.max "+Res.max);
			 }
			 }
		 }
		 
		//new Tester().BuildXPath(a);
	}

	
	
	
	 
}
