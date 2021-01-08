package xfuzz.xml.builder;

import java.io.*;
import java.util.*;

import table.draw.Block;
import table.draw.Board;
import table.draw.Table;
import xfuzz.xpath.analyzer.AnalyzedXPath;
import xfuzz.xpath.analyzer.UnsatFormulaContainer;
import xfuzz.xpath.xsd.PreparedXPath;
import xfuzz.xpath.xsd.XSDPathExpression;


public class WriteToFile {

	/*
	 * Some methods to write XML or other data to files such as creating the output report  
	 * 
	 */
	
	public boolean WriteSetToTxtFile(String FilePath ,Vector <String> V ){
		try {
		    BufferedWriter out = new BufferedWriter(new FileWriter(FilePath));
		   
		       for(int i=0 ; i  < V.size() ; ++i){
		    	   out.write(V.get(i) +"\n");  
		       }
		    out.close();
		    return true;
		}
		catch (IOException e)
		{
		    System.out.println("Exception ");
return false;
		}
		
	}
	public boolean WriteXMLToFile(String FilePath ,String XMLContent ) throws IOException{
		File theDir = new File("Output");

		// if the directory does not exist, create it
		if (!theDir.exists()) {
		    System.out.println("creating directory: " + theDir.getName());
		    boolean result = false;

		    try{
		        theDir.mkdir();
		        result = true;
		    } 
		    catch(SecurityException se){
		        //handle it
		    }        
		    if(result) {    
		        System.out.println("DIR created");  
		    }
		}
		  BufferedWriter out = new BufferedWriter(new FileWriter(FilePath));
		  out.write(XMLContent); 
		  out.close();
		  return true;
	}
	
	public boolean WriteStringToTextFile(String FilePath ,String Text) throws IOException{
		
		 BufferedWriter out = new BufferedWriter(new FileWriter(FilePath));
		
		  out.write(Text); 
		  out.close();
		  return true;
	}
	
	String WriteXSDRes(Vector <XSDPathExpression> b){
		List<String> headersList = Arrays.asList("Node", "Value Type", "Max", "Min", "Restrictions");
        List<List<String>> rowsList = new ArrayList<List<String>>();
       
       // Board board = new Board(150);
       
        	
    	
       int Wdith=0;
		 for(int i=0 ; i<b.size() ; ++i){
			 ArrayList<String> list = new ArrayList<String>();
			 list.add(b.get(i).XPath);
			 list.add(b.get(i).ValueType.toString());
			 list.add(String.valueOf(b.get(i).max));
			 list.add(String.valueOf(b.get(i).min));
			 if(b.get(i).Res!=null)
			 list.add(String.valueOf(b.get(i).Res.type));
			 else
				 list.add("");
			 rowsList.add(list);
			 if(b.get(i).XPath.length()>Wdith)
				 Wdith=b.get(i).XPath.length();
		 }
		 List<Integer> colWidthsListEdited = Arrays.asList(Wdith, 13, 6, 13, 13);
		 List<Integer> colAlignList = Arrays.asList(
				    Block.DATA_TOP_LEFT, 
				    Block.DATA_CENTER, 
				    Block.DATA_CENTER, 
				    Block.DATA_CENTER, 
				    Block.DATA_CENTER);
				
		 Board board = new Board(150);
		
		 Table table = new Table(board, 150, headersList, rowsList);
		 table.setGridMode(Table.GRID_FULL).setColWidthsList(colWidthsListEdited);
		 table.setColAlignsList(colAlignList);
		 Block tableBlock = table.tableToBlocks();
		 board.setInitialBlock(tableBlock);
		 board.build();
		 String tableString = board.getPreview();
	
		 return tableString;
		 
		  //String tableString = board.setInitialBlock(new Table(board, 150, headersList, rowsList).tableToBlocks()).build().getPreview();
	
		
	}
	
	/*
	public void CreateReport(Vector <XSDXPath> b ,Vector <AnalyzedXPath> XPathSet ,Vector <PreparedXPath> a) throws IOException{
		Re+="XSD Parser: \n";
		Re+=WriteXSDRes(b);
		Re+="\n XPath Anlayzer: \n";
		Re+=WriteAnlayzerResults(XPathSet);
		Re+="\n Final XPath Set \n";
		Re+=WriteFinalSetofXPath(a,XPathSet);
		
		
		
	}
	*/
	public void CreateReport(String FileName ,Vector <XSDPathExpression> b , Vector<Vector<AnalyzedXPath>> ListOfXPathSet ,	HashMap ImpoissbleTestCases ) throws IOException{
		
		String Re;
		Re="XSD Parser: \n";
		Re+=WriteXSDRes(b);
		
		if(ListOfXPathSet.size()!=0){
			Re+="\n XPath Analyzer: \n";
		Re+=WriteAnlayzerResults(ListOfXPathSet);
		Re+="\n Possible Test Cases: \n";
		
		Re+=WriteTestCases(ListOfXPathSet ,ImpoissbleTestCases , true);
		//Re+="\n Final XPath Set \n";
		//Re+=WriteFinalSetofXPath(a,XPathSet);
		}
		if(new UnsatFormulaContainer().UnsetXPath.size() != 0){
		Re+="\n Unsatisfiable Formulas: \n";
		Re+=WriteUnsetFormula();}
		
		
		
		PrintReport( FileName , Re);
		
		
		
		
	}
	public void PrintReport(String FileName ,String Re) throws IOException{
		
		WriteStringToTextFile(FileName,Re);
	}
	
	
	String WriteUnsetFormula(){
		List<String> headersList = Arrays.asList("XPath");
        List<List<String>> rowsList = new ArrayList<List<String>>();
      
       // Board board = new Board(150);
       
        	
    	
       int WdithForXPathCol=0;
       int WdithForValueCol=0;
   
     		for(int i=0 ; i <UnsatFormulaContainer.UnsetXPath.size() ; ++i ){
     			
     			 ArrayList<String> list = new ArrayList<String>();
     			//list.add(UnsetFormulaContainer.getFormula(i));
     			 list.add(UnsatFormulaContainer.getXPath(i));
     			 
     			
    			
    			 rowsList.add(list);
    			 
    			 if(UnsatFormulaContainer.getXPath(i).length()>WdithForXPathCol)
    				 WdithForXPathCol=UnsatFormulaContainer.getXPath(i).length();
    			 if(UnsatFormulaContainer.getFormula(i).length()>WdithForValueCol)
    				 WdithForValueCol=UnsatFormulaContainer.getFormula(i).length();
     		}
     
       
		 
       
       
       List<Integer> colWidthsListEdited = Arrays.asList(WdithForXPathCol+2);
	   List<Integer> colAlignList = Arrays.asList(
				    Block.DATA_CENTER
				  
				 
				    
				   );
       String tableString = CreatTable(WdithForXPathCol,headersList,rowsList,colWidthsListEdited,colAlignList);
	
		 return tableString;
		 
		  //String tableString = board.setInitialBlock(new Table(board, 150, headersList, rowsList).tableToBlocks()).build().getPreview();
	
		
	}
	
	String WriteAnlayzerResults(Vector<Vector<AnalyzedXPath>> ListOfXPathSet){
		List<String> headersList = Arrays.asList("XPath", "Produced From ","Value");
        List<List<String>> rowsList = new ArrayList<List<String>>();
      
       // Board board = new Board(150);
       
        	
    	
       int WdithForXPathCol=0;
       int WdithForValueCol=0;
       int WdithForProcCol=0;
       Vector <AnalyzedXPath> CheckedIfXPathIsPrinted= new Vector <AnalyzedXPath>();
       for (int m=0 ; m <ListOfXPathSet.size() ; ++m)
       {
    	   Vector<AnalyzedXPath> XPathSet=ListOfXPathSet.get(m);
    	   
		 for(int i=0 ; i<XPathSet.size() ; ++i){
			 if(!CheckedIfXPathIsPrinted.contains(XPathSet.get(i))){
			 ArrayList<String> list = new ArrayList<String>();
			 list.add(XPathSet.get(i).XPath);
			 list.add(XPathSet.get(i).GenFrom);
			 if(XPathSet.get(i).Value!=null)
			 list.add(XPathSet.get(i).Value);
			 else
				 list.add("");	 
			 rowsList.add(list);
			 if(XPathSet.get(i).XPath.length()>WdithForXPathCol)
				 WdithForXPathCol=XPathSet.get(i).XPath.length();
			 if(XPathSet.get(i).GenFrom.length()>WdithForProcCol)
				 WdithForProcCol=XPathSet.get(i).GenFrom.length();
			 if(XPathSet.get(i).Value!=null && XPathSet.get(i).Value.length()>WdithForValueCol)
				 WdithForValueCol=XPathSet.get(i).Value.length();
			 
			 CheckedIfXPathIsPrinted.add(XPathSet.get(i));
			 }
			
		 }
		 
       
       }
       List<Integer> colWidthsListEdited = Arrays.asList(WdithForXPathCol+2, WdithForProcCol+2,WdithForValueCol+5);
	   List<Integer> colAlignList = Arrays.asList(
				    Block.DATA_TOP_LEFT, 
				    Block.DATA_CENTER,
				    Block.DATA_CENTER
				    
				   );
       String tableString = CreatTable(WdithForXPathCol,headersList,rowsList,colWidthsListEdited,colAlignList);
	
		 return tableString;
		 
		  //String tableString = board.setInitialBlock(new Table(board, 150, headersList, rowsList).tableToBlocks()).build().getPreview();
	
		
	}
	
	
	
	String WriteFinalSetofXPath(Vector <PreparedXPath> a , Vector <AnalyzedXPath> XPathSet){
		List<String> headersList = Arrays.asList("XPath", "Implemented?");
        List<List<String>> rowsList = new ArrayList<List<String>>();
      
       // Board board = new Board(150);
       
        	
    	
       int Wdith=0;
       
		 for(int i=0 ; i<XPathSet.size() ; ++i){
			
			 ArrayList<String> list = new ArrayList<String>();
			 boolean Found=false;
			 for(int m=0 ; m < a.size() ; ++m){
				
				 if(a.get(m).IsNewXPath==true && XPathSet.get(i).XPath.equals(a.get(m).XPath) &&XPathSet.get(i).Value==null ){
					 list.add(XPathSet.get(i).XPath);
					 list.add("Yes");
					 rowsList.add(list);
					 Found=true;
					 break;
				 }
				 
				 if(XPathSet.get(i).XPath.equals(a.get(m).XPath) && XPathSet.get(i).Value.equals(a.get(m).Value) ){
					// if(a.get(m).IsNewXPath==true && XPathSet.get(i).XPath.equals(a.get(m).XPath)){
				
				 list.add(XPathSet.get(i).XPath);
				 list.add("Yes");
				 rowsList.add(list);
				 Found=true;
				 //break;
				 
				 }
				 
			 }
			
			 if(Found==false){
				
				 list.add(XPathSet.get(i).XPath);
				 list.add("No");
				 rowsList.add(list);
			 }
			 if(XPathSet.get(i).XPath.length()>Wdith)
				 Wdith=XPathSet.get(i).XPath.length();
		 }
		 
		 
		
		 
			
			 
			 
		 List<Integer> colWidthsListEdited = Arrays.asList(Wdith, Wdith,Wdith);
		   List<Integer> colAlignList = Arrays.asList(
					    Block.DATA_TOP_LEFT, 
					    Block.DATA_CENTER
					   
					   );	 
		 
		 
		 
		 String tableString = CreatTable(Wdith,headersList,rowsList,colWidthsListEdited,colAlignList);
	
		 return tableString;
	}
	
	
	
	String WriteTestCases(Vector<Vector<AnalyzedXPath>> ListOfXPathSet, HashMap ImpoissbleTestCases , boolean PrintPoissbleCases){
		List<String> headersList = Arrays.asList("XPath", "Test Case Number", "Value" );
        List<List<String>> rowsList = new ArrayList<List<String>>();
      
       // Board board = new Board(150);
        List<Integer> colWidthsListEdited = Arrays.asList(0, 0,0);
 	   List<Integer> colAlignList = Arrays.asList(
 				    Block.DATA_TOP_LEFT, 
 				    Block.DATA_CENTER,
 				    Block.DATA_CENTER
 				   );	
        	
    	
       int Wdith=0;
       int WdithValueCol=0;
       int WdithReasonCol=0;
      
       
       if(PrintPoissbleCases==true){
       for (int m=0 ; m <ListOfXPathSet.size() ; ++m)
       {
    	 
    	   if(ImpoissbleTestCases.containsKey(m)==false){
    		  
    	   Vector<AnalyzedXPath> XPathSet=ListOfXPathSet.get(m);
    	  
		 for(int i=0 ; i<XPathSet.size() ; ++i){
			 ArrayList<String> list = new ArrayList<String>();
			 list.add(XPathSet.get(i).XPath);
			 list.add(String.valueOf(m));
			 if(XPathSet.get(i).Value!=null)
			 list.add(XPathSet.get(i).Value);
			 else
				 list.add("New Node"); 
			
			
			 rowsList.add(list);
			 if(XPathSet.get(i).XPath.length()>Wdith)
				 Wdith=XPathSet.get(i).XPath.length();
			 if(XPathSet.get(i).Value!=null && XPathSet.get(i).Value.length()>WdithValueCol)
				 WdithValueCol=XPathSet.get(i).Value.length();
			
		 }
		 
       }
       
       }
       colWidthsListEdited = Arrays.asList(Wdith, 25,WdithValueCol+5);
 	   colAlignList = Arrays.asList(
 				    Block.DATA_TOP_LEFT, 
 				    Block.DATA_CENTER,
 				    Block.DATA_CENTER
 				   );	}
       else{
    	   headersList = Arrays.asList("XPath", "Test Case Number", "Value" , "Reason" );
    	   //for (int m=0 ; m <ImpoissbleTestCases.size() ; ++m)
    	   for (Object  m : ImpoissbleTestCases.values()) 
           {
        	  
        	   Vector<AnalyzedXPath> XPathSet=ListOfXPathSet.get((int)m);
        	  
    		 for(int i=0 ; i<XPathSet.size() ; ++i){
    			 String ImpReason=new ImpXPathContainer().get(XPathSet.get(i).XPath);
    			 ArrayList<String> list = new ArrayList<String>();
    			 list.add(XPathSet.get(i).XPath);
    			 list.add(String.valueOf(ImpoissbleTestCases.get(m)));
    			 if(XPathSet.get(i).Value!=null)
    			 list.add(XPathSet.get(i).Value);
    			 else
    			 list.add("New Node"); 
    			 if(ImpReason==null)
    				 list.add("");
    			 else
    			 list.add(ImpReason);
    			
    			
    			 rowsList.add(list);
    			 if(XPathSet.get(i).XPath.length()>Wdith)
    				 Wdith=XPathSet.get(i).XPath.length();
    			 if(XPathSet.get(i).Value!=null && XPathSet.get(i).Value.length()>WdithValueCol)
    				 WdithValueCol=XPathSet.get(i).Value.length();
    			 
    			 if(ImpReason!=null && ImpReason.length()>WdithReasonCol)
    				 WdithReasonCol=ImpReason.length();
    		 }
    		 
           
           
           }   
    	   colWidthsListEdited = Arrays.asList(Wdith, 25,WdithValueCol+5 , WdithReasonCol+2);
     	   colAlignList = Arrays.asList(
     				    Block.DATA_TOP_LEFT, 
     				    Block.DATA_CENTER,
     				    Block.DATA_CENTER,
     				   Block.DATA_CENTER
     				   );	
	}
       
		
      
		if(rowsList.size()==0  && ImpoissbleTestCases.size()!=0)
			return " \n Impossible Test Cases \n"+ WriteTestCases( ListOfXPathSet,  ImpoissbleTestCases ,  false);

		 String tableString = CreatTable(Wdith,headersList,rowsList ,colWidthsListEdited ,colAlignList);
	if(PrintPoissbleCases==true && ImpoissbleTestCases.size()!=0)
	{  
		 return tableString +" \n Impossible Test Cases \n"+ WriteTestCases( ListOfXPathSet,  ImpoissbleTestCases ,  false);
	
	}
		 else
		 return tableString;	
		  //String tableString = board.setInitialBlock(new Table(board, 150, headersList, rowsList).tableToBlocks()).build().getPreview();
	
		
	}
	String CreatTable (int Wdith ,List<String> headersList,List<List<String>> rowsList ,List<Integer> colWidthsListEdited , List<Integer> colAlignList){
		
		
		 Board board = new Board(1000);
			
		 Table table = new Table(board, 1000, headersList, rowsList);
		 table.setGridMode(Table.GRID_FULL).setColWidthsList(colWidthsListEdited);
		 table.setColAlignsList(colAlignList);
		 Block tableBlock = table.tableToBlocks();
		 board.setInitialBlock(tableBlock);
		 board.build();
		 return board.getPreview();
	}
}
