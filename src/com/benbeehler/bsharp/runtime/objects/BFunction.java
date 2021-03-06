package com.benbeehler.bsharp.runtime.objects;

import java.io.File;
import java.util.ArrayList;

import com.benbeehler.bsharp.runtime.Runtime;
import com.benbeehler.bsharp.syntax.Parser;
import com.benbeehler.bsharp.syntax.SyntaxManager;

public class BFunction implements BObject {

	//function class, below are attributes and values
	private String name;
	private Object value;
	private File file;
	private AccessModifier access;
	private boolean nativ;
	private BType type;
	private Object returnValue;
	private Runnable nativeRunnable;
	private Parser parser;
	
	private String category = "";
	
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
		//Runtime.functions.add(this);
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
		return this.returnValue;
	}
	
	public void execute() {
		if(this.usesNative()) {
			this.getNativeRunnable().run();
		} else {
			this.executeLines();
		}
		
		/*List<BVariable> remove = new ArrayList<>();
		
		for(BVariable var : Runtime.variables) {
			if(var != null || var.getFunction() != null || this != null) {
				if(var.getFunction().equals(this)) {
					if(!this.getParameters().contains(var)) {
						remove.add(var);
					}
				}
			}
		}
		
		for(BVariable var : remove) {
			Runtime.variables.remove(var);
		}*/
	}
	
	public void executeLines() {
		if(parser == null) {
			parser = new Parser(Runtime.file);
		}
		
		parser.setCurrentFunction(this);
		
		for(String line : this.lines) {
			line = line.trim();
			String[] split = line.split(" ");
			String first = split[0].trim();
			String f = SyntaxManager.getStringUntilString(line, SyntaxManager._OPENPB).trim();
			
			if (first.equals("var")) {
				SyntaxManager.parseVariable(line, parser);
			} else if (f.equals("if")) {
				SyntaxManager.parseIfStatement(line, parser);
			} else if (f.equals("loop")) {
				SyntaxManager.parseLoopStatement(line, parser);
			} else if(Runtime.containsFunctionByName(f)) {
				SyntaxManager.callFunction(line, parser); //Function called
			} else if(first.equals("return")) {
				SyntaxManager.parseReturn(line, parser);
			} else if(line.startsWith("thread::")) {
				SyntaxManager.parseThread(line, parser);
			} else if(Runtime.containsVariableByName(first)) {
				SyntaxManager.remapMutable(line, parser);
			} else {
				
			}
		}
	}
	
	public void addLine(String line) {
		this.lines.add(line);
	}

	
	public boolean containsParameterByName(String name) {
		return this.getParameters().stream().filter(var ->
				var.getName().equals(name))
				.findAny()
				.isPresent();
	}

	public BVariable getParameterFromName(String name) {
		if(this.containsParameterByName(name)) {
			return this.getParameters().stream().filter(var -> var.getName().equals(name))
					.findAny()
					.get();
		} 
		
		return Runtime.getVariableFromName("nil");
	}
	
	public Runnable getNativeRunnable() {
		return nativeRunnable;
	}


	public void setNativeRunnable(Runnable nativeRunnable) {
		this.nativeRunnable = nativeRunnable;
	}
	
	public void addParameter(BVariable var) {
		ArrayList<BVariable> list = getParameters();
		list.add(var);
		setParameters(list);
	}

	public Parser getParser() {
		return parser;
	}

	public void setParser(Parser parser) {
		this.parser = parser;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
		this.name = category + "::" + name;
	}
}
