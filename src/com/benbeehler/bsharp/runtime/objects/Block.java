package com.benbeehler.bsharp.runtime.objects;

import java.util.ArrayList;

import com.benbeehler.bsharp.syntax.Parser;


public class Block {
	
	/*
	 * As of right now, this class has not merit on the program and is deprecated
	 */
	
	private boolean executes = false;
	private ArrayList<String> lines = 
			new ArrayList<String>();
	
	
	public Block() {
		//Nothing here, blank constructor
	}
	
	public Block(ArrayList<String> lines) {
		this.lines = lines;
	}
	
	public void execute() {
		Parser parser = new Parser(lines);
		parser.start();
	}
	
	public void addLine(String line) {
		this.getLines().add(line);
	}
	
	public ArrayList<String> getLines() {
		return this.lines;
	}

	public boolean doesExecute() {
		return executes;
	}

	public void setExecute(boolean executes) {
		this.executes = executes;
	}
}
