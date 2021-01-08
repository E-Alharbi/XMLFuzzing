package xfuzz.xpath.analyzer;

import java.util.Vector;

public class AnalyzedXPath {

	public String XPath;
	public String GenFrom;// if the XPath produced from another XPath Ex: course/student[4] from course/student[count(course/student)+1]
	public String Value;//if there is a value for the node Ex: student[name='a'] a is a value
	
	public Vector <AnalyzedXPath> AddXPath (Vector <AnalyzedXPath> XPathSet , AnalyzedXPath XPath){
		
		
		XPathSet.add(XPath);
		
		return XPathSet;
	}
}
