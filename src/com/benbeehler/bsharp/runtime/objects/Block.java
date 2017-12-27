package com.benbeehler.bsharp.runtime.objects;

import java.util.ArrayList;


@Deprecated
public class Block {
	
	/*
	 * As of right now, this class has not merit on the program and is deprecated
	 */
	
	private ArrayList<String> lines = 
			new ArrayList<String>();
	
	
	public Block() {
		//Nothing here, blank constructor
	}
	
	public Block(ArrayList<String> lines) {
		this.lines = lines;
	}
	
	public void execute() {
		this.lines.stream().forEach(e -> {
			//TODO e is the line
		});
	}
	
	public void addLine(String line) {
		this.getLines().add(line);
	}
	
	public ArrayList<String> getLines() {
		return this.lines;
	}
}
