package com.benbeehler.bshsarp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import com.benbeehler.bsharp.runtime.io.STDIO;

public class Utils {

	public static String[] getContent(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		ArrayList<String> content = new ArrayList<>();
		
		String line="";
		while((line = br.readLine()) != null) {
			content.add(line);
		}
		
		br.close();
		
		return content.toArray(new String[content.size()]);
	}
	
	public static String getSpecializedInput() {
		return STDIO.STDIN.nextLine();
	}
	
	public static String getInput() throws IOException {
		Scanner scanner = new Scanner(System.in);
		
		String out = "";
		while(scanner.hasNextLine()) {
			out = scanner.nextLine();
			break;
		}
		scanner.close();
		
		return out;
	}
}
