/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sum_of_powers;

/**
 *
 * @author Karl
 */
public class Sum_of_powers 
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        // TODO code application logic here   
        boolean proven = true;
                
        for(int i = 2; i<=10;i++)
        {     
            for(int j = 1; j<10;j++)
            {            
                int base = i;
                int exponent = j;
                
                // compute power
                int result = calculate_power(base,exponent);

                System.out.print("base: " + base + "\n");
                System.out.print("exponent: " + exponent + "\n");

                System.out.print("power result: " + result + "\n");

                // check result with Math Libary
                if(result != Math.pow(base,exponent))
                {                                    
                    proven = false;                    
                    System.out.print("Algorithm Failed!\n");   
                    break;                    
                }
                else    
                    System.out.print("Computed the power correctly!\n");                

                int row = exponent;
                
                // compute summed result
                result = sum_of_powers(base, row);

                int check_sum = 0;

                // check with Math Libary
                for(int n=0; n<=exponent; n++)
                {
                    check_sum += Math.pow(base, n);
                }                
                System.out.print("sum result: " + result + "\n");

                if(check_sum != result)
                {
                    proven = false;                    
                    System.out.print("Algorithm Failed!\n");   
                    break;                    
                }
                else
                    System.out.print("sum result matches!\n");   
            }
        }
        if(proven)
            System.out.print("It Works! You Win. Perfect\n");
    }
    public static int sum_of_powers(int base, int n)
    {        
            if(n==0)
                return 1;
            else
                return (calculate_power(base,n) + sum_of_powers(base,n-1));
    }
    public static int calculate_power(int base, int exponent)
    {
        if(exponent == 0)
            return 1;
        else
            return (base * calculate_power(base,exponent-1));
    }
}
