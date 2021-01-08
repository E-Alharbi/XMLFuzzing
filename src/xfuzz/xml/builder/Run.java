package xfuzz.xml.builder;

public class Run {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub


		// TODO Auto-generated method stub
	
	   //String FilePath="./ExmplesInputs/BasicConInputs/course.xml";
		//String XSDFilePath="./ExmplesInputs/BasicConInputs/course.xsd";
		//String XPathFilePath="./ExmplesInputs/BasicConInputs/Set1.txt";
		String FilePath="Example.xml";
		String XSDFilePath="Example.xsd";
		String XPathFilePath="ExampleXPathSet.txt";
		
		
			Properties.Combiner=true;	
		
		
		
			
			Properties.SubsetAlgorithm=false;
	
		
		
		Properties.XMLFileName=FilePath;
		Properties.XSDName=XSDFilePath;
		Properties.XPathFileName=XPathFilePath;
		
		
		new Runner().run();
	
		
	}

}
