# Parallel Programming

## :pencil: Introduction
The problem proposed for resolution was the use of parallel threads and semaphores for control, aiming at the implementation of an algorithm that simulates the production and consumption of a bar, where each customer and each waiter are threads that can perform tasks in parallel. The solving strategy of the algorithm, made in Java, is to use a state machine to the maintenance of each thread, so that they run in parallel accessing the methods that a class control provides (Pub class). The Pub class only serves to support competition from attributes between threads in parallel. When there is concurrency from some attribute or method of the class Pub, there is a semaphore to control the critical section correctly.

## :joystick: How to use
To compile and run the code follow the steps:
- To compile, access the path: "Parallel Programming/src" and open the terminal inside it;
- Run the command: **javac core/*.java**

- To run, use the same build directory;
- Run the command: **java core.Main 'customers' 'waiters' 'waiters capacity' 'rounds'**

Note: ignore the quotes ('') of the command above.
Note 2: if you want, the passing of arguments is not necessary, the code itself will ask the values if this occurs.

Example of input for execution: **java core.Main 10 2 4 3**

Example of input for execution: **java core.Main**
