This project is implemented by Emad Alharbi and supervised by Dr Nir Piterman. Informatics Department, University of Leicester.

This tool is a fuzzing tool to change an XML document based on given XPath expressions.


What do you need to run the tool?
The tool's inputs are an XML document, a set of XPath expressions and XSD document.

How to run the tool?
The main class to run the tool is xfuzz.xml.builder.tester. You can set your inputs in this class.

Are there any examples?
The package xfuzz.examples has an example. The inputs files for this example are located in /ExamplesInputs
 
Where can I find the tool's output?
The tool will create a folder called "Output". 

What is the tool's report?
A report shows details about the XSD parser results, unsat formulas, combiner results and reasons for impossible test cases.  


Our tool packages:
xfuzz.examples
xfuzz.xml.builder
xfuzz.xpath.analyzer
xfuzz.xpath.classifier
xfuzz.xpath.combiner
xfuzz.xpath.solver
xfuzz.xpath.sorter
xfuzz.xpath.xsd

Translet tool packages:
org.translet.builder
org.translet.helper
org.translet.processor
org.translet.processor.dom
org.translet.rules    