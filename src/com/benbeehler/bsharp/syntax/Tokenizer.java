package com.benbeehler.bsharp.syntax;

import java.io.File;
import java.io.IOException;

import com.benbeehler.bsharp.Console;
import com.benbeehler.bshsarp.util.Utils;

/*
 * tokenizer scans character by character whilst replacing certain chars for tokens
 */

public class Tokenizer {

	private String[] input;
	private String[] output;
	
	public Tokenizer(String[] input) {
		out(input);
	}
	
	public Tokenizer(File file) {
		try {
			out(Utils.getContent(file));
		} catch (IOException e) {
			Console.E("io Error - failed to import file into tokenizer");
		}
	}
	
	public void out(String[] input) {
		this.input = input;
		output = new String[input.length];
		
		for(int i = 0; i < input.length; i++) {
			String str = input[i];
			boolean string = false;
			String[] array = str.split("");
			
			for(int x = 0; x < array.length; x++) {
				String ch = array[x];
				if(ch.equals("\"")) {
					if(string) {
						string = false;
					} else {
						string = true;
					}
				}
				
				if(!string) {
					if(ch.equals(",")) {
						array[x] = SyntaxManager._PARAMETER_SPLIT_COMMA;
					} else if(ch.equals("(")) {
						array[x] = SyntaxManager._OPENPB;
					} else if(ch.equals(")")) {
						array[x] = SyntaxManager._CLOSEPB;
					}
				}
			}
			
			output[i] = SyntaxManager.constructStringFromArray(array);
		}
	}

	public String[] getOutput() {
		return output;
	}

	public void setOutput(String[] output) {
		this.output = output;
	}

	public String[] getInput() {
		return input;
	}
}
