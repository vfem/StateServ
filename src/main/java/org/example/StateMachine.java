package org.example;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StateMachine {
	private volatile State state;
	private final Lock lock;

	public StateMachine() {

		lock = new ReentrantLock();
		state = State.IDLE;
	}

	public void submitWork(String inputData) {

		lock.lock();
		try {
			if (state == State.IDLE) {
				doWork(inputData);
			}
		} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error while working");
				state = State.IDLE;
		} finally {
				lock.unlock();
		}
	}


	private void doWork(String inputData) {

		try {
			System.out.println("Start working - " + Thread.currentThread().getName());
			state = State.RUNNING;
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			state = State.IDLE;
		}
		String result = inputData + " - data modified " + Thread.currentThread().getName();
		System.out.println("End working - " + Thread.currentThread().getName());
		doSending(result);
	}

	private void doSending(String result) {

		try {
			System.out.println("Start sending - " + Thread.currentThread().getName());
			state = State.SENDING;
			Thread.sleep(1000);
			System.out.println("End sending - " + Thread.currentThread().getName());
			state = State.IDLE;
			System.out.println("Now idle  - " + Thread.currentThread().getName());
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Error while sending");
			state = State.IDLE;
		}
	}
}
