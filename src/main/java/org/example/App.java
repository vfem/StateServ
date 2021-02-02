package org.example;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) throws InterruptedException {

        final StateMachine machine = new StateMachine();

        Thread thread1 = new Thread(() -> machine.submitWork("Work1"));
        Thread thread2 = new Thread(() -> machine.submitWork("Work2"));
        Thread thread3 = new Thread(() -> machine.submitWork("Work3"));

        thread1.start();
        thread2.start();
        thread3.start();

        thread1.join();
        thread2.join();
        thread3.join();

    }
}
