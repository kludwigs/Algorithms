package pathfinder;
import java.util.Random;
/**
 *
 * @author Karl Ludwigsen
 */
public class Pathfinder {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        /*int [][]sample = { 
            {0,2,5}, 
            {1,1,3},
            {2,1,1}
        };
        
        */
        /*
        int money = 25;        
        
        int [][]sample = {
          */
/* 4x4*/
            /*
{0,2,3,4},
{1,2,9,4},
{1,2,7,4},
{0,1,2,4}
            */
/*     
        };  
        */
        
        /* Grid Size must be smaller than 17x17 */
        /* Code currently doesn't accomodate larger */
        
        
        /* 
        Premise: Starting in the upper left hand corner we can move across the
        grid by either moving downward or to the right until we reach the lower
        right hand corner. There are no moves such as to the left and up. For 
        each spot on the grid there is a number. That number is the cost to 
        land on that spot and the cost will be subtracted from a variable called
        money. The goal is to use as much money as possible without going 
        negative until you reach the end. 
                
        e.g. for the following grid
            {0,2,5}, 
            {1,1,3},
            {2,1,1}
        
        1211, 2531, 1111 and 1131 are some valid paths to take. If money is 6 
        the function answer will return 0 since path 1131 is equal to 6. 2531 
        is too large and will try a new path once the 3 is landed on. 
        
        If money is 3 the answer function will return -1 since all paths are too 
        expensive.       
                
        If money is 13, 2531 is the most suitable path and thus the answer 
        function will return 2 dollars.
        */
        
        int GridSize = 5; // can be up to 17
                
        int money  = ((GridSize - 1) * 2) * 5;
        
        int [][]grid = GenerateGrid(GridSize);
                                
        System.out.print("Money = " + money + "\n");
        
        PrintGrid(grid);
        
        int retVal = answer(money, grid);
        
        System.out.print("output " + retVal +"\n");                     
    }
    public static int [][] GenerateGrid(int GridSize)
    {
       int [][]newgrid =  new int[GridSize][GridSize];                            
       Random r = new Random();
       
       for(int i =0; i< newgrid.length; i++)
       {
            for(int j = 0; j< newgrid.length; j++)
            {                                                
                newgrid[i][j] = (r.nextInt(8) + 1);
            }
       }
       /* starting spot is always 0 */
       newgrid[0][0] = 0;
       
       return newgrid;
    }
    public static void PrintGrid(int [][]grid)
    {
       System.out.print("\n");
       for(int i =0; i< grid.length; i++)
       {
            for(int j = 0; j< grid.length; j++)
            {                                                
                System.out.print(grid[i][j]);
                if(j < grid.length - 1)
                    System.out.print(", ");
            }
            System.out.print("\n");
       }                
       System.out.print("\n");
    }
    
    public static int answer(int money, int [][]grid)
    {
        int n = grid[0].length; 
                
        int numMoves = (n - 1) *2; 
        
        System.out.print("Number of Moves is "  + numMoves + "\n" );
                        
        int score = 200;
        
        /* ******************************************************************/
        
       /* 
         e.g. if 
        If we have 4x4 grid we need to make 6 moves to reach the end.
        
        0 will indicate moving downward and a 1 means a move to the right.
       
       	a valid path will have three 0's and three 1's 
       
       	e.g. 000111, 101010, 110001 etc.
        
        We split the number of moves in lower and upper
       	such as this
        
        000 | 111
        
        With the help of pascal's triangle we can generate the list of 6 choose 3
        permutations (20). We only need to count 2^(-numMoves/2) of the total 
        number of paths (invalid or not).
        
        The row we want will be this
        
        1 3 3 1 
        
        this will tell us there's 1 way to choose no 1's (e.g. 000)
        and 3 ways to choose a single 1 (001, 010, 100) and so on.
        
        We end up pairing no 1's with three 1's which is 000 | 1111
        
        for a single 1's we pair each one with from the set of two 1's
        
        so 001 will match with 110, 101, 011 as will 100. 
        
        once a valid path is constructed we traverse the grid to see how
        it will do. 
        
        */
 
        int number_of_moves = (n-1)*2;  
        int number_of_rows = n;

        long[] pascalarray = GetPascalsRowArray(number_of_rows);                     

        long [][] patharray  = new long [number_of_rows][];

        for (int i = 0; i < pascalarray.length; i++)
        {
                patharray[i] = new long[(int)pascalarray[i]];  

        }
        //long binomial_coefficient = centralbinom(N-1);
  
        int numbers  = (int)PowerOfTwo(number_of_moves/2); 
        int []counters = new int[patharray.length];
        
        System.out.print("number range = " + numbers + "\n");
        for(int i = 0; i<numbers; i++)
        {
                int val = OneBitCounter(i, numbers);                    
                patharray[val][counters[val]] = i;
                counters[val] +=1;    
        }

		//long []paths = new long[(int)binomial_coefficient];
		//int counter = 0;  
		
        for (int i = 0; i<patharray.length; i++)
        {
            int arraylength = patharray.length;

            for(int j = 0; j< patharray[i].length;j++)
            {
            	/* grab the set  and push into upper bits */
                long num1 = patharray[i][j];
                num1 <<= number_of_moves/2;

                for(int k = 0;k<patharray[i].length;k++)
                {
        	    /*	pair with appropriate match into lower bits*/
                    int tmp = arraylength - (i +1 ); 
                    long thePath = (patharray[tmp][k] + num1);

                    // Path is constructed. Evaulate Path                         
                    int moneyleft = evaulatePath(numMoves, thePath,money, grid);

                    if(moneyleft < score && moneyleft > -1)
                    {         

                        if(moneyleft == 0)
                        {
                            System.out.print("\nFound it! Dollars left = " + moneyleft + "!\n");
                            return 0;                        
                        }
                        score = moneyleft;                                                
                    }
                }              
            }
        }
        return score;                 
        /***********************************************************************/                    
    }   
    public static int evaulatePath(int numMoves, long moves,int moneyleft, int [][]grid)
    {                        
        int y = 0;
        int z = 0;
        int counter = 0;
        System.out.print("move list - ");
        for (int w = 0; w < numMoves; w++)
        {
            if( (moves & 0x1) == 1)                    
            {
                moneyleft -= grid[++y][z];
                System.out.print(grid[y][z]);
            }                 
            else
            {
                moneyleft -= grid[y][++z];
                System.out.print(grid[y][z]);
            }
            if(moneyleft < 0)
            {
                System.out.print("\nwallet emptied, try next path\n");
                return -1;
            }                
            moves >>=1; 
            counter++;
        }  
        System.out.print("\n");
        System.out.print("path completed with " + moneyleft + " dollars left " + counter + " moves\n");           
        return moneyleft;
    }
        
    public static int OneBitCounter(int number, int length)
    {
        //System.out.print("testing " + Integer.toString(number,2) + "\n");
                
        int i = 0;
        int count = 0;
        
        for(i = 0; i<length; i++)
        {
            if( (number & 0x1) == 1)
                count+= 1;
            
            number >>=1;                                               
        }        
        return count;        
    }    
    public static long factorial(long n)
    {
        long result= 1;

        for(long i = 2; i <= n; i++) result *= i;
            return result;
    } 
    
    public static long centralbinom(long n)
    {
        return factorial(2*n) / (factorial(n) * factorial(n));
    }                         
    
    public static long [] GetPascalsRowArray(int num_rows)
    {
      long [][] master_list2 = new long[num_rows][];  
        
      int i, k;
      long num_entries = 1;
    
      System.out.print("Row "+ num_rows + " \n");
      for (i = 0; i < num_rows; i++)       
      { // Step through the master list.              
        master_list2[i] = new long[(int)num_entries++];  
        
        System.out.print("\n");  
        for (k = 0; k <= i; k++)         
        { // Step through each sub-array. The length of the array is i.
    
          if (k == 0 || k == i) // Current item is first or last in the row.
          {
           
            master_list2[i][k] = 1;
              System.out.print(master_list2[i][k]);
              
          }
          else
          {
           
            master_list2[i][k] = master_list2[i-1][k-1] + master_list2[i-1][k];
              System.out.print(master_list2[i][k]);
          }                       
          if(k < i)
          {
              System.out.print(",");
          }
        }      
        System.out.print("\n");          
      }
      return master_list2[(num_rows-1)];
    }    
    public static long PowerOfTwo(int n)
    {
        if(n == 0)
            return 1;
        else
            return 1 << n;       
    }    
    
}
