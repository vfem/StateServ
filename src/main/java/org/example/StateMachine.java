package org.example;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class StateMachine {

	private static Logger log;

	static {
		try (InputStream loggingInputStream = StateMachine.class.getClassLoader()
				.getResourceAsStream("logging.properties")) {
			LogManager.getLogManager().readConfiguration(loggingInputStream);
			log = Logger.getLogger(StateMachine.class.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private State state;
	private final Lock lock;
	private long workDuration;
	private long sendDuration;


	public StateMachine() {

		Properties prop = new Properties();
		String propFileName = "machine.properties";
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName)) {

			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
			workDuration = Long.parseLong(prop.getProperty("work.duration", "2")) * 1000;
			sendDuration = Long.parseLong(prop.getProperty("send.duration", "1")) * 1000;
			log.log(Level.INFO, String.format("workDuration setted to %dms and sendDuration setted to %dms",
					workDuration, sendDuration));
		} catch (IOException e) {
			log.log(Level.SEVERE, "an exception was thrown", e);
			workDuration = 2000;
			sendDuration = 1000;
			log.log(Level.SEVERE, "workDuration setted to 2000ms and sendDuration setted to 1000ms");
		}

		lock = new ReentrantLock();
		state = State.IDLE;
	}

	public boolean submitWork(String inputData) {

		if (lock.tryLock()) {

			try {
				ResultContainer resultContainer = new ResultContainer(null, false);

				if (state == State.IDLE) {
					log.log(Level.INFO, "State was IDLE, now RUNNING...");
					state = State.RUNNING;
					resultContainer = doWork(inputData);
					log.log(Level.INFO, "Input data was processed");
				}
				if (state == State.RUNNING && resultContainer.isSuccessful()) {
					log.log(Level.INFO, "State was RUNNING, now SENDING...");
					state = State.SENDING;
					resultContainer = doSend(resultContainer.getResult());
					log.log(Level.INFO, "Processed data was sended");
				}
				if (state != State.IDLE) {
					log.log(Level.INFO, "Machine is returning in initial IDLE state...");
					state = State.IDLE;
				}
				log.log(Level.INFO, String.format("Work isSuccessful - %s", resultContainer.isSuccessful()));
				return resultContainer.isSuccessful();

			} finally {
				lock.unlock();
			}

		} else {
			log.log(Level.INFO, "Object is busy, work not started");
			return false;
		}

	}


	private ResultContainer doWork(String inputData) {

		try {
			Thread.sleep(workDuration);
			String result = inputData + " - data modified";
			return new ResultContainer(result, true);
		} catch (InterruptedException e) {
			log.log(Level.SEVERE, "an exception was thrown", e);
			return new ResultContainer(inputData, false);
		}


	}

	private ResultContainer doSend(String result) {

		try {
			Thread.sleep(sendDuration);
			return new ResultContainer(result, true);
		} catch (InterruptedException e) {
			log.log(Level.SEVERE, "an exception was thrown", e);
			return new ResultContainer(result, false);
		}
	}

}
