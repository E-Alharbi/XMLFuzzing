package xfuzz.xpath.xsd;

import java.util.Vector;

public class XSDRestrictionsParser {

	/*
	 * The parser parses nodes restrictions  form XSD
	 */
	XSDRestrictions EnumerationRestriction(Vector <String> XSDXPathSet , String XSDXPath , String UnparsedXPath,Vector <XSDPathExpression> XSDXPathCon){
		
		String Val="";
		
		if(UnparsedXPath.contains("restriction")){
			if(UnparsedXPath.contains("enumeration")){
				
				Enumeration Res = new Enumeration();
				int Index=0;
				 Index=new XSDParser().contains("@base='",UnparsedXPath);
				String base=new XSDParser().CollectChar(UnparsedXPath,Index);
				//System.out.println("Val "+Val +" base " +base);
				
				Res.base=new XSDParser().FindType(base);
				Res.type=XSDRestrictionsTypes.enumeration;
				int indexforFirstEnu=XSDXPathSet.indexOf(UnparsedXPath);// The index for first enumeration for the node 
				//System.out.println("indexforFirstEnu "+indexforFirstEnu);
				for(int i=indexforFirstEnu ; i<XSDXPathSet.size() ;++i){
					if(!XSDXPathSet.get(i).contains("enumeration"))
						break;
					 Index=new XSDParser().contains("@value='",XSDXPathSet.get(i));
					Val=new XSDParser().CollectChar(XSDXPathSet.get(i),Index);
					Res.setVal(Val.replaceAll("'", ""));
					//System.out.println("Res size "+Res.getSet().size());
					//XSDXPathSet.remove(i);
				}
				if(XSDXPathCon.get(XSDXPathCon.indexOf(new XSDParser().containNode(XSDXPathCon,XSDXPath))).Res==null){
				XSDPathExpression p = XSDXPathCon.get(XSDXPathCon.indexOf(new XSDParser().containNode(XSDXPathCon,XSDXPath)));
				p.Res=Res;
				XSDXPathCon.set(XSDXPathCon.indexOf(new XSDParser().containNode(XSDXPathCon,XSDXPath)), p);}
				return Res;
			}
		}
		return null;
	}
	
	
      XSDRestrictions InclusiveRestriction(Vector <String> XSDXPathSet , String XSDXPath , String UnparsedXPath,Vector <XSDPathExpression> XSDXPathCon){
		
		String Val="";
		
		if(UnparsedXPath.contains("restriction")){
			if(UnparsedXPath.contains("xs:minInclusive")){
				//System.out.println("InclusiveRestriction ");
				Inclusive Res = new Inclusive();
				int Index=0;
				 Index=new XSDParser().contains("@base='",UnparsedXPath);
				String base=new XSDParser().CollectChar(UnparsedXPath,Index);
				//System.out.println("Val "+Val +" base " +base);
				
				Res.base=new XSDParser().FindType(base);
				Res.type=XSDRestrictionsTypes.inclusive;
				
				int indexforFirstEnu=XSDXPathSet.indexOf(UnparsedXPath); 
				//System.out.println("indexforFirstEnu "+indexforFirstEnu);
				
				for(int i=indexforFirstEnu ; i<XSDXPathSet.size() ;++i){
					if(!XSDXPathSet.get(i).contains("minInclusive") && !XSDXPathSet.get(i).contains("maxInclusive")){
						//System.out.println("break!!");
						break;
					}
						
					
						 Index=new XSDParser().contains("@value='",XSDXPathSet.get(i));
							Val=new XSDParser().CollectChar(XSDXPathSet.get(i),Index);
							if(XSDXPathSet.get(i).contains("minInclusive")){
							Res.min=Integer.parseInt(Val.replaceAll("'", ""));
							//System.out.println("Res.min "+Res.min);
							}
							
					
					if(XSDXPathSet.get(i).contains("maxInclusive")){
						Res.max=Integer.parseInt(Val.replaceAll("'", ""));
						//System.out.println("Res.max "+Res.max);
					}
					//XSDXPathSet.remove(i);
				}
				
				if(XSDXPathCon.get(XSDXPathCon.indexOf(new XSDParser().containNode(XSDXPathCon,XSDXPath))).Res==null){
				XSDPathExpression p = XSDXPathCon.get(XSDXPathCon.indexOf(new XSDParser().containNode(XSDXPathCon,XSDXPath)));
				p.Res=Res;
				XSDXPathCon.set(XSDXPathCon.indexOf(new XSDParser().containNode(XSDXPathCon,XSDXPath)), p);}
				return Res;
				
			}
			
		}
		
		return null;
	}
	
}
