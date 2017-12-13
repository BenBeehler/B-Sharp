package com.bsharp.ben.objects;

import java.io.File;

public class BVariable implements BObject {
	
	private String name;
	private Object value;
	private File file;
	private BFunction function;
	private BType type;
	
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

}
