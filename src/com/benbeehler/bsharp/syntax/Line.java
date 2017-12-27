package com.benbeehler.bsharp.syntax;

@Deprecated
public class Line {

	private boolean contin; //continue through the block chain
	private String line;
	
	public boolean isContin() {
		return contin;
	}
	
	public void setContin(boolean contin) {
		this.contin = contin;
	}
	
	public String getLine() {
		return line;
	}
	
	public void setLine(String line) {
		this.line = line;
	}
}
