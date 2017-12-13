package com.bsharp.ben;

public class Console {

	/*
	 * Console, used for logging, returned as a string
	 */
	
	public static String log(String log) {
		log = "@"
				.replace("@", log);
		
		System.out.println(log);
		
		return log;
		
		/*
		 * Log
		 */
	}
	
	public static void E(String error) {
		error = "@"
				.replace("@", error);
		
		System.out.println(error);
		
		moderateError(); 
		
		/*
		 * Error
		 */
	}
	
	public static String W(String warning) {
		warning = "@"
				.replace("@", warning);
		
		System.out.println(warning);
		
		return warning;
		
		/*
		 * Warning
		 */
	}
	
	public static String P(String pure) {
		pure = "@"
				.replace("@", pure);
		
		System.out.println(pure);
		
		return pure;
		
		/*
		 * Pure log
		 */
	}
	
	public static void moderateError() {
		System.exit(0);
	}
}
