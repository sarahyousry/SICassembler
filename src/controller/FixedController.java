package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class FixedController {
	
	String Label[]    = new String[1000];
	String opCode[]   = new String[1000];
	String operands[] = new String[1000];
	String ErrorArr[] = new String[5];
	int PC ;
	String directivesList[] = {"start","end","byte","word","resw","resb","equ","org","base"};
	String opcodeList[] = {"RMO","LDA","LDB","LDX","LDS","LDT","STA","STB","STX","STT","STR","LDCH","STCH","ADD","SUB","ADDR","SUBR","COMP"
            ,"COMR","J","JEQ","JLT","JGT","TIX","TIXR"};
	int i=0;
	int count = 0;
	int error = 0;
	int criticalerror = 0;
	int index = 0;
	int errorindex = 0;
	int flag=0;
	
	public void ReadFixedFile()
	{
		errorindex=0;
		PC=0;
	    BufferedReader reader;
	    int j=0;
	    int commentflag = 0;
		try {
			
			reader = new BufferedReader(new FileReader("srcFile-Fixed.txt"));
			String line = reader.readLine();
			
			while(line != null) {
				
				System.out.println(line);
													    	
			    	if(line.charAt(0) == '.')
			    	{
			    		commentflag=1;
			    	} 
			    	else
			    	{
			    		commentflag = 0;
			    	}
			    				       	
			       	if(commentflag == 0)
					 VALIDATEINSTRUCTION(line);
			       	
			     	line = reader.readLine(); //next line
		        }
			
		    error = 0;   	
		    commentflag=0;   		
			reader.close();
			for(int i=0; i<index; i++)
			{
				System.out.println("Label Array "+Label[i]);
				System.out.println("Opcode Array "+opCode[i]);
				System.out.println("operands Array "+operands[i]);
			}
			ValidateInstruction(Label,opCode,operands);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(IOException w)
		{
			w.printStackTrace();
		}
	  
	  }
	
    public void VALIDATEINSTRUCTION(String str)
    {
    	 String Labelstr="";
    	 String Opcode="";
    	 String operandsstr="";
    	 
    	 if(str.length() > 17 && str.charAt(8) == ' ' && str.charAt(16) == ' ')
    	 {     
    		 for(int i=0; i<str.length(); i++)
    		 {       	
    			 if(i<8)       	 
    			 {       		
    				 Labelstr = Labelstr + str.charAt(i);   	
    			 }
        	 
        	
    			 if(i>8 && i<16)        	
    			 {        		
    				 Opcode = Opcode + str.charAt(i);  
    			 }
        	 
        	 
    			 if(i>16 && i<str.length())       	 
    			 {       		
    				 operandsstr = operandsstr + str.charAt(i); 
    			 } 
    			 
    		 }
    	 }
    	 else if(str.length() < 16 )
    	 {     
    		 for(int i=0; i<str.length(); i++)
    		 {       	
    			 if(i<8)       	 
    			 {       		
    				 Labelstr = Labelstr + str.charAt(i);   	
    			 }
        	 
        	
    			 if(i>8 && i<16)        	
    			 {        		
    				 Opcode = Opcode + str.charAt(i);  
    			 }
        	 
        	 
    			 if(i>16 && i<str.length())       	 
    			 {       		
    				 operandsstr = operandsstr + str.charAt(i); 
    			 } 
    			 
    		 }
    	 }
    	 else
    	 {
    		 ErrorArr[errorindex] = "\t"+"error [07] : 'wrong g operation prefix '";
			 errorindex++; 
			 criticalerror = 1;
    	 }
    	 
    	 if(str.charAt(0) == ' ' && !Labelstr.equals("        "))
    	 {
    		 ErrorArr[errorindex] = "\t"+"'Illegal format in Label Field'";
			 errorindex++; 
		 } 
    	
    	 ///VALIDATE NO SPACE BETWEEN CHARACTERS
    	 for(int j=0; j < Labelstr.length()-1; j++)
    	 {
    		 if(Labelstr.charAt(j) == ' ' && Labelstr.charAt(j+1) != ' ')
    		 {
    			 ErrorArr[errorindex] = "\t"+"'Illegal format in Label Field'";
				 errorindex++;    	
				 criticalerror = 1;
			 }
    	 }
    	 
    	 for(int j=0; j<Opcode.length()-1; j++)
    	 {
    		 if(Opcode.charAt(j) == ' ' && Opcode.charAt(j+1) != ' ')
    		 {
    			 ErrorArr[errorindex] = "\t"+"error [02] : 'missing or misplaced operation mnemonic '";
				 errorindex++;
    		 }
    	 }
    	 
    	 for(int j=0; j<operandsstr.length()-1; j++)
    	 {
    		 if(operandsstr.charAt(j) == ' ' && operandsstr.charAt(j+1) != ' ')
    		 {
    			 ErrorArr[errorindex] = "\t"+"'Illegal format in operands Field'";
				 errorindex++;
				 criticalerror = 1;
    		 }
    	 }
    	 
    	 Label[index] = Labelstr;
    	 opCode[index] = Opcode;
    	 operands[index] = operandsstr;
    	 index++;
    } 
    
    public void ValidateInstruction(String labelarr[], String opcodeArr[], String operandsArr[])
	{
		for(int i=0; i< index; i++)
		{
			if(i==index-1)
				endstatment(opcodeArr[index-1]);
			
			errorindex=0;
			
			if (opCode[i].equalsIgnoreCase("org")||opCode[i].equalsIgnoreCase("base"))
			{
			ErrorArr[errorindex] ="\t"+ "error [05] : 'this statement can�t have a label '";
		   errorindex++;
		   }
			
			ValidateLabel(labelarr,index,i);
			ValidateOpcode(opcodeArr[i]);			
		    ValidateOperands(operandsArr[i],opcodeArr[i]);	
		
			writeToFile(labelarr[i],opcodeArr[i],operandsArr[i], ErrorArr, i);
			ErrorArr = new String[50];
		    errorindex=0;
			PC=PC+3;
		}
	}
    
    public void ValidateLabel(String label[], int size,int index)
	{		
	        int i=index; 
	        int same=0;
	        
            String test = label[index]	;
	       	String space = "        ";
	       	if(index==0)
	       	{
	       		return;
	       	}
	       	    	    	
	      	for (int j = 0 ; j < index; j++)  	           				
	       	{
	       	    int compare2 = label[i].compareTo(space);
	       	    		
	       	    if (Label[i].equalsIgnoreCase(label[j]) && compare2 !=0 )         	 	        
	       	    {
	       	    	if(test.compareTo(label[i]) == 0)
	       	    		same++;
	       	        System.out.println("Repeated Elements are :");        	    			
	       	    	System.out.println(label[i]);
	       	    			   
	      		}     	    			       	    		
	   	   	} 	       	    	
				
	       	if(same!=0)
	       	{
	       		ErrorArr[errorindex] ="\t"+ "error [04] : 'duplicate label definition '";
				errorindex++;
	       	}
     }
    
    public void ValidateOpcode(String opcode)
	{
		int j=0;
		int found=0;	
		
			for(int i=0; i<25; i++)
			{
				if(opcode.replaceAll(" ", "").compareToIgnoreCase(opcodeList[i]) == 0)
				{
					found = 1;
				}
			}
			
			for(int i=0; i<9; i++)
			{
				if(opcode.replaceAll(" ", "").compareToIgnoreCase(directivesList[i]) == 0)
				{
					found = 1;
				}
			}
			
			if(found == 0 && !opcode.replaceAll(" ", "").equals(""))
			{
				ErrorArr[errorindex] = "\t"+"error [08] : 'unrecognized operation code '";
				errorindex++;
			}
			
	}
	
	public void endstatment(String opcode)
	{
		
		if (!opcode.replaceAll(" ", "").equalsIgnoreCase("end") || opcode.replaceAll(" ", "") == "")
			{
			  ErrorArr[errorindex] = "\t"+"error [13] : ' missing END statement '";
			  errorindex++;
			}
			
	}
	
	public void ValidateOperands(String operand, String opcode)
	{
		if(PC<=0 &&  criticalerror == 0) {
			PC =Integer.parseInt(operands[0]);
		}
		int foundop1 = 0;
		int foundop2 = 0;
		String registerList[] = {"A","B","S","T","X"};
		
		String[] op = operand.split(",");
		if(op.length == 1 && criticalerror == 0) {
			String operand1 = op[0];
			System.out.println("ONE");
		}
		else if( criticalerror == 0)
		{
			System.out.println("TWO");
			String operand2= op[1];
			String operand1 = op[0];
		}
		
		if(opcode.equalsIgnoreCase("addr") || opcode.equalsIgnoreCase("subr") || opcode.equalsIgnoreCase("comr") || opcode.equalsIgnoreCase("rmo"))
		{
			if(op.length == 1)
			{
				ErrorArr[errorindex] = "\t"+"error [03] : 'missing or misplaced operand field '";
				errorindex++;
			}
			else {
				for(i=0;i<registerList.length;i++)
				{
					if (op[0].equalsIgnoreCase(registerList[i]) )
					{
						foundop1 = 1;
					}
					
					if (op[1].equalsIgnoreCase(registerList[i]))
					{
						foundop2 = 1;
					}
				}
				
				if(foundop1==0 || foundop2==0)
				{
					ErrorArr[errorindex] = "\t"+"error [12] : 'illegal address for a register '";
					errorindex++;
				}
					
				
			}
		}
		else if(opcode.equalsIgnoreCase("tixr")) 
		{
			if(op.length != 1)
			{
				ErrorArr[errorindex] = "\t"+"error [03] : 'missing or misplaced operand field '";
				errorindex++;
			}
			else
			{
				for(i=0;i<registerList.length;i++)
				{
					if (operand.equalsIgnoreCase((registerList[i])))
					{
						foundop1 = 1;
					}
					
				}
				if(foundop1 == 0)
				{
					ErrorArr[errorindex] = "\t"+"error [12] : 'illegal address for a register '";
					errorindex++;
				}
			}
			
		}
		else
		{
			if(op.length != 1)
			{
				ErrorArr[errorindex] = "\t"+"error [03] : 'missing or misplaced operand field '";
				errorindex++;
			}
		}
		
   }
	
	public void writeToFile(String label, String opcode, String operands, String Error[], int indx)
	{
		
		String Inst;	
		String PCcount = Integer.toHexString(PC).toUpperCase();
			  Inst = PCcount+"\t"+label +"      "+ opcode +"\t\t" + operands + "\t"  ;
			  
			  for(int i=0; i<Error.length; i++)
			  {
				  if(Error.length > 1)
				  {
					  if(Error[i] != null)		 
					  {
						  Inst = Inst +"\n"+ Error[i]+ "\n" ;
				      }
				  }else
				  {
					  if(Error[i] != null)		 
					  {
						  Inst = Inst +"\n"+ Error[i] ;
				      }
				  }
			  }

		 try
		 { 
			 BufferedWriter bw = new BufferedWriter(new FileWriter(new File("ListFile-Fixed.txt"), true)); 
			 System.out.println(Inst);
	           
	           if(indx == 0)
	           {
	        	   PrintWriter pw = new PrintWriter("ListFile-Fixed.txt");
	        	   pw.close();
	        	   bw.write("  **** SIC Assembler ****"); 
	        	   bw.newLine();bw.newLine();
	        	   bw.write(Inst);
	        	   bw.newLine();	        	   
	           }	      
	           else 
	           {
	        	   
	        	   bw.write(Inst);
	        	   bw.newLine();
	           }
	           
	           if(indx+1 == index)
	           {
	        	  bw.newLine();
	        	  bw.write("  **** END OF PASS 1 ****"); 
	           }
	           
	           bw.close();
	      }
		 catch(Exception e)
		 {
			 e.printStackTrace();
	     }
	}
	
}

