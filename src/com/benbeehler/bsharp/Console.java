package com.benbeehler.bsharp;

public class Console {

	/*
	 * Console, used for logging, returned as a string for reference
	 */
	
	public static String log(String log) {
		System.out.println(log);
		return log;
	}
	
	public static void E(String error) {
		System.out.println("bsharp.fatal_error " + error);
		moderateError(); 
	}
	
	public static String W(String warning) {
		System.out.println("bsharp.warning " + warning);
		return warning;
	}
	
	public static String P(String pure) {
		System.out.println(pure);
		return pure;
	}
	
	public static void moderateError() {
		System.exit(1);
	}
}
