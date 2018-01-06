package com.benbeehler.bsharp.runtime.io;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class STDIO {

	//public static final Scanner STDIN = new Scanner(System.in);
	public static final Executor EXECUTOR = Executors.newCachedThreadPool();
	
}
