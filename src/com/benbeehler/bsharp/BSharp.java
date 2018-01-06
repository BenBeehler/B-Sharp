package com.benbeehler.bsharp;

import java.io.File;
import java.io.IOException;

import javax.script.ScriptException;

import com.benbeehler.bsharp.runtime.Runtime;
import com.benbeehler.bsharp.runtime.io.STDIO;
import com.benbeehler.bsharp.syntax.Tokenizer;
import com.benbeehler.bshsarp.util.Utils;

public class BSharp {

	public static String VERSION = "0.1.7.5";
	
	public static long START = System.nanoTime();
	
	public static void main(String[] args) throws ScriptException {
		try {
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
						try {
							String[] list = Utils.getContent(file);
							for(String string : list) {
								System.out.println(string);
							}
						} catch (IOException e) {
							Console.E("Specified file does not exist");
						}
					} else if(command.equals("-v")) {
						Console.log("B# V." + VERSION);
					} else {
						Console.E(command + " was not recognized as a valid command");
					}
				} else if(command.equals("-e")) {
					StringBuilder builder = new StringBuilder();
					
					for(int i = 1; i < args.length; i++) {
						builder.append(args[i]);
					}
					
					String evaluation = builder.toString().trim();
					
					Console.log("Evaluating expression: " + evaluation);
					
					Runtime.typeInit();
					Runtime.initPrimitiveVariables();
					Runtime.importNative("runtime");
					Runtime.importNative("io");
					Runtime.importNative("type");
					Runtime.importNative("boolean");
					Runtime.importNative("system");
					
					String[] array = new String[] { evaluation };
					Tokenizer tokenizer = new Tokenizer(array);
					
					evaluation = tokenizer.getOutput()[0];
					Console.log("Tokenized Output: " + evaluation);
					Console.log("");
					Console.log(Runtime.getValue(evaluation).toString());
				} else {
					Console.E(file.getName() + " does not exist");
				}
			} else {
				Console.log("B# V" + VERSION);
			}
		} catch(StackOverflowError e) {
			Console.E("critical program failure, stack overflow error");
		}
	}
	
}
