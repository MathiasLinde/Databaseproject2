package org.example;

import java.io.IOException;
import java.util.List;

public class LoaderExample {

/**
 * This is just the example provided as part of the assignment run the main method in Startprogram
 */
	public static void main(String[] args) {
		FootagesAndReportersLoader loader = new FootagesAndReportersLoader();
		try {
			System.out.println("loading from " + args[0]);
			List<FootageAndReporter> footagesAndReporters = loader.loadFootagesAndReporters(args[0]);
			for(FootageAndReporter footageAndReporter : footagesAndReporters) {
				System.out.print("\tFootage: " + footageAndReporter.getFootage());
				System.out.println("\tReporter: " + footageAndReporter.getReporter());

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}


