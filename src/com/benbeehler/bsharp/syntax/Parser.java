package com.benbeehler.bsharp.syntax;

import java.io.File;
import java.util.ArrayList;

import com.benbeehler.bsharp.runtime.Runtime;
import com.benbeehler.bsharp.runtime.objects.BFunction;
import com.benbeehler.bsharp.runtime.objects.BType;

/*
 * standard parser
 * operates in 2 iterating modes
 * 1. variable objects are loaded into memory
 * 2. functions are executed based on the predefined values
 * these modes are ruled by scantime
 * 
 * TODO:
 * optimize 1-2 modes
 */
public class Parser {

	private File file;
	private BFunction currentFunction;
	private int scanTime;
	private boolean isFunction;
	private BType currentType;
	
	private ArrayList<String> body;
	
	public Parser(File file) {
		this.currentFunction = Runtime.main;
		this.file = file;
		this.setScanTime(0);
	}
	
	public Parser(ArrayList<String> lines) {
		this.body = lines;
	}
	
	public void start () {
		Tokenizer tokenizer = new Tokenizer(file);
		
		for(String line : tokenizer.getOutput()) {
			line = line.trim();
			String[] split = line.split(" ");
			String first = split[0].trim();
			String f = SyntaxManager.getStringUntilString(line, SyntaxManager
					._OPENPB);
			
			if(this.getScanTime() == 0) {
				if(currentFunction.equals(Runtime.main)) {
					 if (first.equals("var")) {
						SyntaxManager.parseVariable(line, this);
					} else if (first.equals("type")) {
						SyntaxManager.parseType(line, this);
					} else if (first.equals("func")) {
						SyntaxManager.parseFunction(line, this);
					} else if (first.equals("import")) {
						SyntaxManager.parseImport(line);
					} else {
					}
				} else {
					if(line.trim().equals("}")) {
						this.currentFunction = Runtime.main;
					} else {
						currentFunction.addLine(line);
					}
				}
				if(currentFunction == Runtime.main) {	
					if (f.equals("if")) {
						SyntaxManager.parseIfStatement(line, this);
					} else if (f.equals("!if")) {
						SyntaxManager.parseNotIfStatement(line, this);
					} else if (f.equals("loop")) {
						SyntaxManager.parseLoopStatement(line, this);
					} else if(Runtime.containsFunctionByName(f)) {
						SyntaxManager.callFunction(line, this); //Function called
					} else if(f.equals("func")) {
						SyntaxManager.checkFunction(line, this);
					} else if(line.startsWith("thread::")) {
						SyntaxManager.parseThread(line, this);
					} else {
					}
				} else {
					//NULL
				}
			}
		}
		
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

	public boolean isFunction() {
		return isFunction;
	}

	public void setFunction(boolean isFunction) {
		this.isFunction = isFunction;
	}

	public BType getCurrentType() {
		return currentType;
	}

	public void setCurrentType(BType currentType) {
		this.currentType = currentType;
	}
}
