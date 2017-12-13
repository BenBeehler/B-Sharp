package com.bsharp.ben;

import java.io.File;
import java.util.ArrayList;

import com.bsharp.ben.core.Core;
import com.bsharp.ben.core.ExecutionType;

public class Runtime {

	public ArrayList <File> files = 
			new ArrayList <> ();
	
	String args;
	
	public Runtime(String args) throws Exception {
		this.args = args;
	}
	
	public void begin() {
		String filename = this.args;
		
		File file = new File(filename);
		
		Core.init(file, ExecutionType.INTERPRET);
	}
}
