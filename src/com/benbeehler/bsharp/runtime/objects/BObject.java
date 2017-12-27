package com.benbeehler.bsharp.runtime.objects;

import java.io.File;

public interface BObject {
	
	//standard inheritance for objects
	
	public String getName();
	public Object getValue();
	public File getFile();
	public AccessModifier getAccess();
	public BType getType();
	
	public void setName(String name);
	public void setValue(Object value);
	public void setFile(File file);
	public void setAccessModifier(AccessModifier access);
	public void setType(BType type);
	
	public void complete(); //<- Critical, adds this to variable list
}
