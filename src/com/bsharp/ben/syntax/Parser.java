package com.bsharp.ben.syntax;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import com.bsharp.ben.core.Core;
import com.bsharp.ben.objects.BFunction;

public class Parser {

	private File file;
	private BFunction currentFunction;
	private int scanTime;
	
	private ArrayList<String> body;
	
	public Parser(File file) {
		this.currentFunction = Core.main;
		this.file = file;
		this.setScanTime(0);
	}
	
	public Parser(ArrayList<String> lines) {
		this.body = lines;
	}
	
	public void start () {
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while (scanner.hasNext()) {
			String line = scanner.nextLine()
					.trim();
			String[] split = line.split(" ");
			String first = split[0]
					.trim();
			
			if(this.getScanTime() == 0) {
				if(currentFunction == Core.main) {
					 if (first.equals("var")) {
						SyntaxManager.parseVariable(line, this);
					} else if (first.equals("type")) {
						//TODO
					} else if (Core.containsVariableByName(first)) {
						SyntaxManager.reinstantiateVariable(line, this);
					} else if (first.equals("function")) {
						SyntaxManager.parseFunction(line, this);
					} else if (first.equals("import")) {
						SyntaxManager.parseImport(line);
					} else {
					}
				} else {
					if(line.trim().equals("}")) {
						this.currentFunction = Core.main;
					} else {
						currentFunction.addLine(line);
					}
				}
			} else if (this.getScanTime() == 1) {
				if(currentFunction == Core.main) {	
					if (first.equals("if")) {
						SyntaxManager.parseIfStatement(line);
					} else if (first.equals("!if")) {
						SyntaxManager.parseNotIfStatement(line);
					} else if (first.equals("while")) {
						SyntaxManager.parseWhileStatement(line);
					} else if (first.equals("!while")) {
						SyntaxManager.parseWhileNotStatement(line);
					} else if (first.equals("loop")) {
						SyntaxManager.parseLoopStatement(line);
					} else if(Core.containsFunctionByName(first)) {
						SyntaxManager.callFunction(line); //Function called
					} else {
						//TODO
					}
				} else {
					//NULL
				}
			}
		}
		
		scanner.close();
		
		if(this.getScanTime() == 0) {
			this.setScanTime(1);
			start();
		}
	}
	
	public File getFile () {
		return this.file;
	}
	
	public BFunction getCurrentFunction() {
		return this.currentFunction;
	}
	
	public void setCurrentFunction(BFunction function) {
		this.currentFunction = function;
	}
	
	public void setScanTime(int scanTime) {
		this.scanTime = scanTime;
	}
	
	public int getScanTime() {
		return this.scanTime;
	}
	 
	public ArrayList<String> getBody() {
		return this.body;
	}
	
	/*
	 * 				if (first.equals("var")) {
					if(this.getScanTime() == 0) {
						SyntaxManager.parseVariable(line, this);
					}
				} else if (first.equals("type")) {
					if(this.getScanTime() == 0) {
						//TODO types
					}
				} else if (first.equals("function")) {
					if(this.getScanTime() == 0) {
						SyntaxManager.parseFunction(line, this);
					}
				} else if (first.equals("import")) {
					if(this.getScanTime() == 0) {
						SyntaxManager.parseImport(line);
					}	
				} else if (first.equals("if")) {
					if(this.getScanTime() == 1) {
						SyntaxManager.parseIfStatement(line);
					}
				} else if (first.equals("!if")) {
					if(this.getScanTime() == 1) {
						SyntaxManager.parseNotIfStatement(line);
					}
				} else if (first.equals("while")) {
					if(this.getScanTime() == 1) {
						SyntaxManager.parseWhileStatement(line);
					}
				} else if (first.equals("!while")) {
					if(this.getScanTime() == 1) {
						SyntaxManager.parseWhileNotStatement(line);
					}
				} else if (first.equals("loop")) {
					if(this.getScanTime() == 1) {
						SyntaxManager.parseLoopStatement(line);
					}
				} else if(Core.containsFunctionByName(first)) {
					if(this.getScanTime() == 1) {
						SyntaxManager.callFunction(line); //Function called
					}
				} else {
					//TODO
				}
	 */
}
