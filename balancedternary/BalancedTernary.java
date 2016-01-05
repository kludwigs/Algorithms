/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package balancedternary;

/**
 *
 * @author Karl
 */
public class BalancedTernary {
    /**
     * @param args the command line arguments
     * 
     *  Compute the balanced ternary of any given number
     * 
     * e.g.
     *  8 is 22 in ternary but is -0+ in balanced (-1 + 0 + 9 ) 
     *  20 is 202 in ternary but is -+-+ in balanced (-1 + 3 - 9 + 27)
     */
    public static void main(String[] args) 
    {
        // TODO code application logic here                          
        int[]numArray = GenerateNumberList(-1000,1000);
        
        boolean proven = true;
        
        for(int i =0; i<numArray.length;i++)
        {        
            int testnum = numArray[i];

            String t = ComputeIt(testnum);        
            System.out.print(t+"\n");

            int testnum2 = ReadExpression(t);        
            System.out.print(testnum2 + "\n");

            if(testnum != testnum2)            
            {
                System.out.print("Error in algorithm!\n");   
                proven = false;
                break;
            }
            else
                System.out.print("They match!\n");                         
        }   
        if(proven)
            System.out.print("You Win. Perfect.\n");
    }
    public static String ComputeIt(int num)
    {
        System.out.print(num + "\n");
        int n = num;
        
        String computeString  = "";
        int carry = 0;
                        
        // begins with LSB to MSB        
        while(n !=0 || carry!=0)
        {           
            //System.out.print(n + "\n");
            int mod = n%3;
            n/=3;
                                             
            mod +=carry;            
            
            if(mod > 1)
                carry = 1;
            else if(mod < -1)
                carry = -1;                         
                                    
            //System.out.print(mod);
            switch (mod) 
            {
                case 1:
                case -2:
                    computeString = computeString.concat("+");
                    break;
                case 2:
                case -1:
                    computeString = computeString.concat("-");
                    break;
                case 0:
                case 3:
                case -3:
                    computeString = computeString.concat("0");
                    break;
                default:
                    break;
            }
           if(mod == 1 || mod == -1)
               carry = 0;
        }     
        //System.out.print("\n");
        return computeString;
    }
    public static int ReadExpression(String expression)
    {
        char [] mychar = expression.toCharArray();
        
        int counter = 1;
        int num = 0;
        for(int i=0; i< mychar.length; i++)
        {
            if(mychar[i] == '+')
            {
                num += counter;
            }
            else if(mychar[i] == '-')
            {
                num += ((-1) *counter);
            }                          
            counter*=3;
        }
        return num;
    }
    public static int[] GenerateNumberList(int min, int max)
    {        
        int n = (max - min) +1;
        
        if(n < 1)
            return null;
        
        int[]numArray = new int[n];
        
        for(int i = 0; i<numArray.length; i++)
        {
            numArray[i] = min++;            
        }        
        return numArray;
    }
}
