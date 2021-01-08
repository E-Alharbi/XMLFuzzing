package xfuzz.xpath.analyzer;

public class FormulaElement {

	
	public String Node;
	public String Op;
	public String Value;
	public String ConconditionOP;
	public String GenFrom;
	public FormulaElement (String node, String op , String value ,String conconditionOP , String genfrom){
		
		this.Node=node;
		this.Op=op;
		this.Value=value;
		this.ConconditionOP=conconditionOP;
		this.GenFrom=genfrom;
	}
}
