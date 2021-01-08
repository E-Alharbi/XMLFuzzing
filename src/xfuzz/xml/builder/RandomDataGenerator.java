package xfuzz.xml.builder;

import java.time.LocalTime;
import java.util.GregorianCalendar;
import java.util.Random;

import xfuzz.xpath.xsd.*;
public class RandomDataGenerator {

	/*
	 * This random data generator generates variety of data types as needed 
	 */
	
	public static void main(String[] args) { // For testing purpose
		// TODO Auto-generated method stub

		System.out.println(new RandomDataGenerator().GetString(false));
		System.out.println(new RandomDataGenerator().GetTime());
	}

	public String GetValue(XSDValTYPES t , XSDPathExpression Node){
		
		if(Node.Res!=null){
			System.out.println("The node has res "+Node.XPath);
			if(Node.Res.type==XSDRestrictionsTypes.enumeration){
				Enumeration e = (Enumeration) Node.Res;
				Random rand = new Random(); 
		
				return e.getSet().get(rand.nextInt(e.getSet().size()));
			}
			if(Node.Res.type==XSDRestrictionsTypes.inclusive){
				Inclusive I = (Inclusive) Node.Res;
				
		
				return GetIntWithMinMaxLimit(String.valueOf(I.max), String.valueOf(I.min));
			}
		}
			
		if(XSDValTYPES.None==t)
			return "";
		if(XSDValTYPES.Boolean==t){
			boolean v =GetBoolean();
			if(v==true)
				return "true";
			else
				return "false";
		}
		if(XSDValTYPES.Date==t)
			return GetDate();
		if(XSDValTYPES.Integer==t)
			 return String.valueOf(GetInt());
		if(XSDValTYPES.Decimal==t)
			 return String.valueOf(GetDouble());
		
		if(XSDValTYPES.String==t)
			return GetString(false);
		if(XSDValTYPES.Time==t)
			return GetTime();
		
		return "";
	}
	
	public String GetString (boolean huge){
		
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        int len=20;
        if (huge==true)
        	len=1000;
        while (salt.length() < len) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
		
	}
	public double GetDouble(){
		Random rand = new Random(); 
		int wholePart = rand.nextInt(10) + 30; 
		double decimalPart = rand.nextDouble(); 
		double pickedNumber = wholePart + decimalPart; 
		
		return pickedNumber;
	}
	public int GetInt(){
		Random rand = new Random(); 
		return rand.nextInt(1000); 
	}
	
	public boolean GetBoolean(){
		Random rand = new Random(); 
		if((rand.nextInt(2)+1)%2==0)
			return true;
		return false;
	}
	
	public String GetDate(){
		 GregorianCalendar gc = new GregorianCalendar();
		
	        int year = randBetween(2010, 2016);

	        gc.set(gc.YEAR, year);

	        int dayOfYear = randBetween(1, gc.getActualMaximum(gc.DAY_OF_YEAR));

	        gc.set(gc.DAY_OF_YEAR, dayOfYear);

	        return gc.get(gc.YEAR) + "-" + (gc.get(gc.MONTH) + 1) + "-" + gc.get(gc.DAY_OF_MONTH);

	}
	 public static int randBetween(int start, int end) {
	        return start + (int)Math.round(Math.random() * (end - start));
	    }
	 
	 public String GetTime(){
		  Random random = new Random();
		
		     
		    return  random.nextInt(24)+":"+random.nextInt(60)+":"+random.nextInt(60);
		 
	 }
	 public String  GetIntWithMaxLimit(String Max){
		 
		 Random rand = new Random(); 
			return String.valueOf(rand.nextInt(Integer.parseInt(Max)));  
	 }
 public String  GetIntWithMinLimit(String Min){
		 
		 Random rand = new Random(); 
			return String.valueOf((rand.nextInt(Integer.parseInt(Min))+Integer.parseInt(Min)));  
	 }
 public String  GetIntWithMinMaxLimit(String Max , String Min){
	 
	 Random rand = new Random(); 
		return String.valueOf(rand.nextInt((Integer.valueOf(Max) - Integer.valueOf(Min)) + 1) + Integer.valueOf(Min));  
 }
}
