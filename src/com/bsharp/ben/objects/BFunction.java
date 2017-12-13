package com.bsharp.ben.objects;

import java.io.File;
import java.util.ArrayList;

import com.bsharp.ben.core.Core;
import com.bsharp.ben.syntax.Parser;
import com.bsharp.ben.syntax.SyntaxManager;

public class BFunction implements BObject {

	private String name;
	private Object value;
	private File file;
	private AccessModifier access;
	private NativeFunction nativefunction;
	private boolean nativ;
	private BType type;
	private Object returnValue;
	
	private ArrayList<BVariable> parameters;
	
	private ArrayList<String> lines = 
			new ArrayList<> ();
	
	public BFunction(String name, Object value, File file, AccessModifier access) {
		this.name = name;
		this.value = value;
		this.file = file;
		this.access = access;
		
		this.parameters = new ArrayList<>();
	}
	
	
	public NativeFunction getNativeFunction() {
		return this.nativefunction;
	}
	
	public boolean usesNative() {
		return this.nativ;
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Object getValue() {
		return this.value;
	}

	@Override
	public File getFile() {
		return this.file;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	public void setNative(boolean nativ) {
		this.nativ = nativ;
	}
	
	public void setNativeFunction(NativeFunction nativefunction) {
		this.nativefunction = nativefunction;
	}

	@Override
	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public void setFile(File file) {
		this.file = file;
	}
	
	public ArrayList<String> getLines () {
		return this.lines;
	}

	@Override
	@Deprecated
	public void complete() {
		//Core.functions.add(this);
	}

	@Override
	public AccessModifier getAccess() {
		// TODO Auto-generated method stub
		return this.access;
	}

	@Override
	public void setAccessModifier(AccessModifier access) {
		this.access = access;
	}
	
	@Override
	public void setType(BType type) {
		this.type = type;
	}
	
	@Override
	public BType getType() {
		return this.type;
	}
	
	public void setParameters(ArrayList<BVariable> list) {
		this.parameters = list;
	}
	
	public ArrayList<BVariable> getParameters() {
		return this.parameters;
	}
	
	public void setReturnValue(Object value) {
		this.returnValue = value;
	}
	
	public Object getReturnValue() {
		if(this.nativ == true) {
			this.setReturnValue(this.nativefunction.returnValue());
		}
		
		return this.returnValue;
	}
	
	public void execute() {
		if(this.usesNative()) {
			this.nativefunction.setParameters(this.parameters);
			this.nativefunction.execute();
		} else {
			this.executeLines();
		}
	}
	
	public void executeLines() {
		for(String line : this.lines) {
			line = line
				.trim();
			String[] split = line.split(" ");
			String first = split[0]
					.trim();
			
			if (first.equals("var")) {
				SyntaxManager.parseVariable(line, new Parser(this.getFile()));
			} else if (first.equals("type")) {
				//TODO
			} else if (first.equals("if")) {
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
			}
		}
	}
	
	public void addLine(String line) {
		this.lines.add(line);
	}
}
