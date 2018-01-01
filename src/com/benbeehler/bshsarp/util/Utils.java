package com.benbeehler.bshsarp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.benbeehler.bsharp.runtime.io.STDIO;

public class Utils {

	public static String[] getContent(File file) throws IOException {
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader(file));
		ArrayList<String> content = new ArrayList<>();
		
		String line="";
		while((line = br.readLine()) != null) {
			content.add(line);
		}
		
		return content.toArray(new String[content.size()]);
	}
	
	public static String getSpecializedInput() {
		return STDIO.STDIN.nextLine();
	}
	
	public static String getInput(InputStream stream) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		StringBuilder builder = new StringBuilder();
		
		String line="";
		while((line = br.readLine()) != null) {
			builder.append(line + "%n");
		}
		
		return builder.toString();
	}
	
	public static String getInputLine(InputStream stream) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String line=br.readLine();
		
		return line;
	}
}
