package xfuzz.xpath.analyzer;

import java.util.HashMap;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Log;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

//import com.microsoft.z3.*;

/*
 * This class has been implemented by modify the Z3 tool examples to meet our tool requirements
 * The examples can be found here  http://rise4fun.com/Z3/tutorial/guide
 */


public class Z3 {
	@SuppressWarnings("serial")
    class TestFailedException extends Exception
    {
        public TestFailedException()
        {
            super("Check FAILED");
        }
    };
    
	public static void main(String[] args) throws TestFailedException {
		// TODO Auto-generated method stub
		String str = "Your string";

		byte[] array = str.getBytes();
		
		System.out.println("string " + new String(array));
	//	System.out.println(new Z3().CheckFormula("(benchmark tst :extrafuns (( AcademicInfomark  Int )( AcademicInfoyear  Int )( PersonalInfoAddressPostCode  Int )( PersonalInfonameFname  Int )) :formula (or ( = PersonalInfonameFname  323969696939 )(and ( > PersonalInfoAddressPostCode  11111  )(and  (and ( > AcademicInfomark   100  ) ( > AcademicInfomark  0) ( < AcademicInfomark  100)) ( = AcademicInfoyear  3239504849553932 ))))  "));
	
		
			System.out.println(new Z3().CheckFormula("(benchmark tst :extrafuns (( mark  Int )) :formula (and (or (and (= mark 100)(= mark 50))(= mark 10))(< mark 100) (>= mark 0) (<= mark 100) (>= mark 0) (<= mark 100)) (>= mark 0) (<= mark 100))) (>= mark 0) (<= mark 100) ) "));
		
		
		
		
	}//(benchmark tst :extrafuns ((AcademicInfo/mark Real)) :formula (> AcademicInfo/mark 300 ))

	public String CheckFormula(String SMT) throws TestFailedException
    {
		System.out.println(SMT);
		com.microsoft.z3.Global.ToggleWarningMessages(true);
        Log.open("test.log");
      
        Log.append("CheckFormula");
        HashMap<String, String> cfg = new HashMap<String, String>();
        cfg.put("model", "true");
       // cfg.put("proof", "true");
        Context ctx = new Context(cfg);
        
        ctx.parseSMTLIBString(SMT,null, null, null, null);
      // for (BoolExpr f : ctx.getSMTLIBFormulas())
        //    System.out.println("formula " + f);
        try {
			@SuppressWarnings("unused")
			Model m = check(ctx, ctx.mkAnd(ctx.getSMTLIBFormulas()),
			        Status.SATISFIABLE);
			//System.out.println(m.toString());
			return m.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		}
      
        
        
        
        
    }
	
	Model check(Context ctx, BoolExpr f, Status sat) throws TestFailedException
    {
        Solver s = ctx.mkSolver();
        s.add(f);
        if (s.check() != sat)
            throw new TestFailedException();
        if (sat == Status.SATISFIABLE){
        	
            return s.getModel();
        }
        else
            return null;
    }
}
