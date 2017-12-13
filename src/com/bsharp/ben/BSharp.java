package com.bsharp.ben;

import com.bsharp.ben.core.Core;

/*
 * Started @ 3/18/17
 * Ended @ Never (hopefully)
 */


public class BSharp {

	public static Runtime runtime;
	
	public static void main(String[] args) {
		if(args.length >= 2) {
			try {
				if(args[0].equals("-i")) {
					runtime = new Runtime(args[1]);
					runtime.begin();
				} else if(args[0].equals("-d")) {
					long start = System.nanoTime();
					runtime = new Runtime(args[1]);
					runtime.begin();
					long end = System.nanoTime();
					
					long full = (end-start)/1000000;
					
					Core.print_debug();
					
					System.out.println("Program execution completed in " + full + "ms");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("B# 0.1.3");
		}
	}
	
	public static String getArguments(String[] args) {
		/*
		 * The goal of this is to get all of the command line
		 * arguments so we can compile the specified program
		 */
		
		StringBuilder fullargs = new StringBuilder();
		
		for (int i = 0; i < args.length; i++) {
			fullargs.append(" " + args[i]);
		}
		
		String arguments = fullargs.toString()
				.trim();
		
		//YOU NEED TO FIX THIS ONLY FOR TESTING
		return arguments;
	}
}
