package com.benbeehler.bsharp.runtime.objects;

import java.io.File;
import java.util.ArrayList;
import com.benbeehler.bsharp.runtime.Runtime;

import com.benbeehler.bsharp.syntax.Parser;

public class BVariable implements BObject {
	
	//standard variable object
	
	private String name;
	private Object value;
	private File file;
	private BFunction function;
	private BType type;
	
	private ArrayList<BVariable> subVariables = new ArrayList<BVariable>();
	
	private AccessModifier access;
	
	public BVariable (String name, Object value, File file, AccessModifier access) {
		this.name = name;
		this.value = value;
		this.file = file;
		this.access = access;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public void setFile(File file) {
		this.file = file;
	}

	@Override
	@Deprecated
	public void complete() {
		// TODO Auto-generated method stub
	}

	public void inherit(BVariable var, Parser parser) {
		if(var.getSubVariables() != null) {
			for(BVariable inh : var.getSubVariables()) {
				addSubVariable(inh);
				
				BVariable newInh = new BVariable(this.getName() + "->" + inh.getName(), inh.getValue(),
						parser.getFile(), inh.getAccess());
				Runtime.addVariable(newInh);
			}
		}
	}
	
	@Override
	public AccessModifier getAccess() {
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
	
	public BFunction getFunction() {
		return this.function;
	}

	public void setFunction(BFunction function) {
		this.function = function;
	}

	public ArrayList<BVariable> getSubVariables() {
		return subVariables;
	}

	public void setSubVariables(ArrayList<BVariable> subVariables) {
		this.subVariables = subVariables;
	}
	
	public void addSubVariable(BVariable variable) {
		ArrayList<BVariable> list = new ArrayList<>();
		list.add(variable);
		setSubVariables(list);
	}
}
