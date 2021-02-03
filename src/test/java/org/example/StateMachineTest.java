package org.example;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class StateMachineTest {
	private StateMachine machine;

	@Before
	public void setUp() {
		machine = new StateMachine();
	}


	@Test
	public void submitWorkTest() {
		//Тест проверяет, что машина недоступна при вызове из нескольких источников
		Thread thread1 = new Thread(() -> assertTrue(machine.submitWork("Work1")));
		Thread thread2 = new Thread(() -> assertFalse(machine.submitWork("Work2")));
		Thread thread3 = new Thread(() -> assertFalse(machine.submitWork("Work3")));

		try {
			thread1.start();
			//ждём чтобы первый поток точно захватил lock
			Thread.sleep(50);
			thread2.start();
			thread3.start();
			thread1.join();
			thread2.join();
			thread3.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}

	}

}