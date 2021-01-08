package xfuzz.examples;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;

import xfuzz.xml.builder.Properties;
import xfuzz.xml.builder.Runner;
import xfuzz.xpath.xsd.PreparedXPath;
import xfuzz.xpath.xsd.XPathGenerator;
import xfuzz.xpath.xsd.XSDParser;
import xfuzz.xpath.xsd.XSDPathExpression;

public class BasicConditionsExample {

	// This example created from the basic conditions experiment from the evaluation chapter
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		String FilePath="./ExmplesInputs/BasicConInputs/course.xml";
		String XSDFilePath="./ExmplesInputs/BasicConInputs/course.xsd";
		String XPathFilePath="./ExmplesInputs/BasicConInputs/Set"+args[0]+".txt"; // choose set from 1 to 7
		System.out.println("-----The Set of XPath Expressions-----");
		FileReader fileReader = new FileReader(XPathFilePath);
		BufferedReader bufferedReader = new BufferedReader(fileReader);	 
		String line;

		while ((line = bufferedReader.readLine()) != null) {
			String XPath=line;
			if(!XPath.startsWith("%"))
			System.out.println(XPath);

		}
		bufferedReader.close();
		Properties.XMLFileName=FilePath;
		Properties.XSDName=XSDFilePath;
		Properties.XPathFileName=XPathFilePath;
		Properties.SubsetAlgorithm=true;
		Properties.Combiner=true;
		new Runner().run();
	}

}
