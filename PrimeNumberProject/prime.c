#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>

int *candidate;
int *prime;
int prime_cnt = 0;
int *p_sieves;
int sieve_upper_bound;

int calculate_block_size(int, int, int);
void find_sieves ( int);
int isPrime(int);
void cross_out_multiples (int, int);

static volatile int sieve_count = 1;
static volatile int sieve_synchro = 0;
static volatile int all_sieves_found = 0; 

int N;
int P;

int* block_index;
int* block_size;

pthread_mutex_t mutex1;
pthread_mutex_t mutex2;

void *worker(void *threadid)
{
    int tid;
    tid = (int)threadid;
    int k = 0;

	//int local_sieve_count = 1;
	//int local_index;
	
    printf("Launched Thread %d\n", tid+1);

    while (all_sieves_found == 0 || k < sieve_count)
    {
		if (tid == sieve_synchro)
		{
			find_sieves(tid);
		    //local_sieve_count = sieve_count;

			for (k = 1; k < sieve_count; k++) // after a thread finds prime sieves
			{ 	
				printf("(%d) Crossing out multiples of %d\n", tid+1, candidate[p_sieves[k]]);
				cross_out_multiples (tid, k);   // check their block
			}	
		}
   	
		else
		{
			for (k = 1; k < sieve_count; k++)
			{
				printf("(%d) Crossing out multiples of %d\n", tid+1, candidate[p_sieves[k]]);
				cross_out_multiples (tid, k);
			}
		}
    }
	
	printf("Thread %d done...\n", tid+1);
   if ( tid > 0)
		pthread_exit(NULL);
   
   return 0;
}

int main (int argc, char *argv[])
{
    int selfcheck = 0;

    if (argc < 2){
		printf("./usage <range> <#threads> <optional_selfcheck> \n");
		return -1;
    }
	
    if( argc >= 2)
	{
		N = atoi(argv[1]); // range of numbers to work on
	}
	if( argc >= 3)
		{
			P = atoi(argv[2]); // number of threads to be launched
		}
	else
	{
		P = 1; // defaults to 1 thread if not specified
	}
    int num_threads = P;
	
	// enables self check
	if (argc == 4)
		selfcheck = 1;

    sieve_upper_bound = ceil (sqrt (( double) N) );

	block_index = (int*) malloc (sizeof(int)*P);
	block_size = (int*)  malloc (sizeof(int)*P);
	
	int block_sum = 1;
	// assign each thread it's own start and end
	
	for ( int i= 0; i<P; i++)
	{	
		int block;
		block = calculate_block_size( i, N, num_threads);
		block_index[i] = block_sum;
		block_size[i] = block - 1;
		block_sum += block;
	}
	printf("Main thread has range %d -- %d\n", 2, block_index[0] + block_size[0]);
	
    for ( int i=1; i<P; i++)
			printf("Thread %d has range %d -- %d\n", i+1, block_index[i], block_index[i] + block_size[i]);
			
    printf("Prime Number program working on range %d to %d with %d threads\n", 2, N, num_threads);
    printf("Seives are at and below sqrt(%d) -- %d\n", N, sieve_upper_bound );  
 
    candidate = (int *) malloc(sizeof(int)*((N+1)/2));
    prime = (int *) malloc(sizeof(int)*((N+1)/2));

    p_sieves = (int *) calloc( sizeof (int) , sieve_upper_bound);
     
    //fill up array with 2, 3, 5, 7, 9....
    candidate[0] = 2;
    int j = 3;
    for (int i = 1; i < (N+1)/2; i++)
	{
		candidate[i] = j;
		j += 2;
    }

    // defaults all numbers to primes
    for (int i = 0; i< (N+1)/2; i++)
	{
		prime[i] = 1;
    }
    int t;
    void *status;
      /* Initialize and set thread detached attribute */
	pthread_t threads[P -1];
    pthread_attr_t attr;  
    pthread_attr_init(&attr);
    pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_JOINABLE);
    pthread_attr_setscope(&attr, PTHREAD_SCOPE_SYSTEM);
   
   // Main thread starts launching threads
   for(t=1; t< num_threads; t++)
   {
		pthread_create(&threads[t-1], &attr, worker, (void *)t);
   }
	// Once all threads are spawned have main thread find the prime sieves
	worker(0);
	
   /* Free attribute and wait for the other threads */
   pthread_attr_destroy(&attr);

   // Join threads together
   for(t=0; t< num_threads -1; t++)
   {
		pthread_join(threads[t], &status);
   }
   printf("Threads joined\n");   

   // traverse the prime array and count all the numbers that are left prime
   for ( int i =0; i< (N+1)/2; i++)
   {
		if(prime[i] == 1)
		{
			prime_cnt++;
		}	
   }
   
   printf("%d prime numbers found!\n", prime_cnt);
   
   if (selfcheck)
   {
		char input;
		printf("Found %d prime numbers, print all (y/n?)\n", prime_cnt);
		scanf ("%c", &input);
		if (input =='y' || input == 'Y')
		{
			for ( int i =0; i< (N+1)/2; i++)
			{
				if(prime[i] == 1)
				printf("%d, ", candidate[i]);
			}
		}
		printf("\n");
	}
	
   sieve_count = 1;
   sieve_synchro = 0;
   all_sieves_found = 0; 
   
   free(candidate);
   free(prime);
   free(p_sieves);
   
   return 0; 
}

/////////////////////////////////////////////////////////
///// ********** UTILITY FUNCTIONS ********** ///////////

int calculate_block_size(int id, int range, int threads)
{
    int base  = range / threads;
    int extra = range % threads;
      
    return (id < extra) ? ++base : base;
}
void find_sieves(int id)
{ 
    	static int count = 1;  
		static int i = 1; 
		
		printf("Thread %d finding sieves... i is %d and count is %d\n" , id+1, i, count);
		
		int end = (block_index[id] + block_size[id]);

	    	while ( (candidate[i] <= sieve_upper_bound) && (candidate[i] <= end) )
    		{
				if(isPrime(i) == 1)
				{
					p_sieves[count] = i;	
					sieve_count++;
					count++;
					printf("Found prime sieve - %d\n", candidate[i]);
				}
			i++;
    		}
			
		if (candidate[i] >= end && candidate[i] <= sieve_upper_bound)
		{
			printf("Have next thread help find the remaining sieves\n");
			sieve_synchro++;
		}
		else
		{
			all_sieves_found = 1;		
			printf("All prime sieves found!\n");
		}
}

int isPrime(int index_of_prime)
{
	int number = candidate[index_of_prime];
   // printf("checking %d if prime...\n", number);
   
    for (int j = 2; j < number/2; j++)
    {
		if ( (number % j) == 0)
		{
			prime[index_of_prime] = 0;
			//printf("\t%d is not prime!\n", number);
			break; // if we found a factor no need to keep iterating through the looop
		}
    } 
    if ( prime[index_of_prime] == 1);
		//printf("\t%d is prime!\n", number);
 
    return prime[index_of_prime]; //returns 0 or 1
}

void cross_out_multiples (int id, int n) //sieve index is just 1 2 3 4...
{
    int start = block_index[id];
    int end   = start + block_size[id]; 
    int index = ( start%2 == 0) ? start/2: (start-1)/2;
	int last_index = (end%2 == 0 )? (end-1)/2: (end)/2;
	// if the index is returned is lower than the sieve  
	//increment the index - we don't want to cross it out
	while(p_sieves[n] ==0) printf("waiting...\n");
	
    if (index <= p_sieves[n]){
		index = p_sieves[n] + 1;
	//printf("(%d) index adjusted to ... %d\n", id+1, index);
    }
	// tells us which thread is working on which prime sieve	
    while (index <= last_index)
    {
		if (candidate[index] % candidate[p_sieves[n]] == 0)
		{
			//printf("(%d) crossed out %d using %d, index is %d \n", id+1, candidate[index], candidate[p_sieves[n]], index);
			prime[index] = 0;
			index+=candidate[p_sieves[n]];
			break;
		}
		else
		index++;
    }	
	while (index <= last_index)
	{
			//printf("(%d) crossed out %d using %d, index is %d \n", id+1, candidate[index], candidate[p_sieves[n]], index);
			prime[index] = 0;
			index+=candidate[p_sieves[n]];
    }	
}

