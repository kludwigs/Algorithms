CC=g++

FLAGS=-lpthread -Wall -o0

all: prime_number

prime_number: prime.c
	$(CC) $(FLAGS) $< -o $@

clean:
	rm -f prime_number
