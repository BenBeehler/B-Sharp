package com.bsharp.ben.objects;

import java.util.ArrayList;

public class NativeFunction {
	
	private String name;
	private Runnable runnable;
	private ArrayList <BVariable> parameters =
			new ArrayList <> ();
	
	private Object returnValue;
	
	public NativeFunction(String name, Runnable runnable) {
		this.name = name;
		this.runnable = runnable;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setRunnable(Runnable runnable) {
		this.runnable = runnable;
	}
	
	public void setParameters(ArrayList<BVariable> parameters) {
		this.parameters = parameters;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Runnable getRunnable() {
		return this.runnable;
	}
	
	public ArrayList<BVariable> getParameters() {
		return this.parameters;
	}
	
	public Object returnValue() {
		return this.returnValue;
	}
	
	public void setReturnValue(Object value) {
		this.returnValue = value;
	}
	
	public void execute() {
		this.runnable.run();
	}
}
