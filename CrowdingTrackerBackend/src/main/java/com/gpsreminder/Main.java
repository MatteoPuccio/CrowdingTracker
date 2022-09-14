package com.gpsreminder;

import java.util.concurrent.ThreadLocalRandom;


public class Main {
	public static void main(String[] args) {
		System.out.println(ThreadLocalRandom.current().nextLong(0, (long)Math.pow(10, 8) + 1));
	}
}