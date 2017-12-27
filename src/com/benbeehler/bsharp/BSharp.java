package com.benbeehler.bsharp;

import java.io.File;
import com.benbeehler.bsharp.runtime.Runtime;
import com.benbeehler.bsharp.runtime.io.STDIO;;

public class BSharp {

	public static String VERSION = "0.1.5";
	
	public static long START = System.nanoTime();
	
	public static void main(String[] args) {
		if(args.length >= 2) {
			String command = args[0];
			File file = new File(args[1]);
			
			if(file.exists()) {
				if(command.equals("-i")) {
					Runtime.init(file);
				} else if(command.equals("-d")) {
					Runtime.init(file);
					Runtime.print_debug();
					long end = System.nanoTime();
					
					long elapsed = (end-START)/1000000;
					Console.log("Finished execution in " + elapsed + "ms");
				} else if(command.equals("-v")) {
					Console.log("B# V." + VERSION);
				} else {
					Console.E(command + " was not recognized as a valid command");
				}
			} else {
				Console.E(file.getName() + " does not exist");
			}
		} else {
			Console.log("B# V" + VERSION);
		}
		
		STDIO.STDIN.close();
	}
	
}
