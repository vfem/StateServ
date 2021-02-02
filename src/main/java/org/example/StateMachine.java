package org.example;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StateMachine {
	private State state;
	private final Lock lock;
	private long workDuration;
	private long sendDuration;

	public StateMachine() {

		Properties prop = new Properties();
		String propFileName = "machine.properties";
		try {
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
			workDuration = Long.parseLong(prop.getProperty("work.duration", "2")) * 1000;
			sendDuration = Long.parseLong(prop.getProperty("send.duration", "1")) * 1000;
		} catch (IOException e) {
			e.printStackTrace();
			workDuration = 2000;
			sendDuration = 1000;
		}

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
			state = State.RUNNING;
			System.out.println("Start working - " + Thread.currentThread().getName());
			Thread.sleep(workDuration);
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
			state = State.SENDING;
			System.out.println("Start sending - " + Thread.currentThread().getName());
			Thread.sleep(sendDuration);
			System.out.println("End sending - " + Thread.currentThread().getName());
			System.out.println("Now idle  - " + Thread.currentThread().getName());
			state = State.IDLE;
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Error while sending");
			state = State.IDLE;
		}
	}
}
