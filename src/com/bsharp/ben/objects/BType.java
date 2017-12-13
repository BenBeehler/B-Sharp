package com.bsharp.ben.objects;

import java.io.File;
import java.util.ArrayList;

public class BType implements BObject {

	private String name;
	private ArrayList <BVariable> variables =
			new ArrayList <BVariable> ();
	private Object value;
	private File file;
	private AccessModifier access;
	
	public BType (String name, File file, AccessModifier access) {
		this.name = name;
		this.file = file;
		this.access = access;
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	@Deprecated
	public Object getValue() {
		// TODO Auto-generated method stub
		return value;
	}
	
	public ArrayList<BVariable> getVariables () {
		return this.variables;
	}

	@Override
	public File getFile() {
		// TODO Auto-generated method stub
		return file;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name = name;
	}

	@Override
	@Deprecated
	public void setValue(Object value) {
		// TODO Auto-generated method stub
		this.value = value;
	}

	@Override
	public void setFile(File file) {
		// TODO Auto-generated method stub
		this.file = file;
	}
	
	public void setVariables (ArrayList<BVariable> list) {
		this.variables.clear();
		this.variables = list;
	}

	@Override
	@Deprecated
	public void complete() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	@Deprecated
	public void setType(BType type) {
		
	}
	
	@Override
	public BType getType() {
		return this;
	}

	@Override
	public AccessModifier getAccess() {
		return access;
	}

	@Override
	public void setAccessModifier(AccessModifier access) {
		this.access = access;
	}

}
