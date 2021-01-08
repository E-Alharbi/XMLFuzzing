package xfuzz.xml.builder;

public class Tester {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	
	  // String FilePath="./ExmplesInputs/BasicConInputs/course.xml";
		//String XSDFilePath="./ExmplesInputs/BasicConInputs/course.xsd";
		//String XPathFilePath="./ExmplesInputs/BasicConInputs/Set1.txt";
		 //String FilePath="Example.xml";
		//String XSDFilePath="Example.xsd";
			//String XPathFilePath="ExampleXPathSet.txt";
		
		if(args[0].equals("CT")){
			Properties.Combiner=true;	
		}
		else if(args[0].equals("CF")){
			Properties.Combiner=false;	
		}
		else{
			System.out.println("Wrong combiner option!!");
			return;
		}
		
		if(args[1].equals("ST")){
			
			Properties.SubsetAlgorithm=true;
		}
		else if(args[1].equals("SF")){
			
			Properties.SubsetAlgorithm=false;
		}
		else{
			System.out.println("Wrong sub algorthim option!!");
			return;
		}
		String FilePath=args[2];
		String XSDFilePath=args[3];
		String XPathFilePath=args[4];
		
		Properties.XMLFileName=FilePath;
		Properties.XSDName=XSDFilePath;
		Properties.XPathFileName=XPathFilePath;
		
		
		new Runner().run();
	}

}
