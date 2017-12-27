package com.benbeehler.bsharp.runtime;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.script.ScriptException;

import com.benbeehler.bsharp.Console;
import com.benbeehler.bsharp.runtime.objects.AccessModifier;
import com.benbeehler.bsharp.runtime.objects.BFunction;
import com.benbeehler.bsharp.runtime.objects.BType;
import com.benbeehler.bsharp.runtime.objects.BVariable;
import com.benbeehler.bsharp.syntax.Parser;
import com.benbeehler.bsharp.syntax.SyntaxManager;
import com.benbeehler.bshsarp.util.Utils;


public class Runtime {
	
	/*
	 * This class is dedicated to the runtime components of B#
	 * stores functions, variables, file data, and native code
	 */
	
	public static File file;
	
	public static ArrayList <File> files = 
			new ArrayList <> ();
	
	public static ArrayList <BVariable> variables = 
			new ArrayList <> ();
	
	public static ArrayList <BFunction> functions = 
			new ArrayList <> ();
	
	public static ArrayList <BType> types = 
			new ArrayList<> ();
	
	public static ArrayList<String> comments =
			new ArrayList<>();
	
	public static BFunction main;
	
	public static void init(File file) {
		//initialize file, create standard executable function - main
		Runtime.file = file;
		files.add(file);
		
		main = new BFunction("main", null, null, AccessModifier.UNIVERSAL);
		Runtime.addFunction(main);
		
		initPrimitiveVariables();
		Runtime.typeInit();
		primitiveInit();
		
		interpret();
	}
	
	public static void initPrimitiveVariables() {
		BVariable nil = new BVariable("nil", "nil", file, AccessModifier.UNIVERSAL);
		nil.setFunction(main);
		addVariable(nil);
	}
	
	public static void primitiveInit() {
		
		//Multithreading
		BFunction sleep = new BFunction("thread::sleep", null, file,
				AccessModifier.UNIVERSAL);
		
		sleep.setNative(true);
		BVariable s_1 = new BVariable("thrsleep_1", null, null, AccessModifier.RESTRICTED);
		sleep.addParameter(s_1);
		sleep.setNativeRunnable(() -> {
			if(sleep.getParameters().size() == 1) {
				String timeString = sleep.getParameters().get(0).getValue().toString();
				
				if(isInteger(timeString)) {
					int value = Integer.parseInt(timeString);
					try {
						Thread.sleep(value);
					} catch(InterruptedException e) {
						Console.E("failed to sleep thread... interrupted..");
					}
				} else {
					Console.E("specify the milliseconds to which the thread will sleep");
				}
			} else {
				Console.E("invalid parameter count");
			}
		});
		
		addFunction(sleep);
		
		BFunction thread = new BFunction("native::thread", null, file,
				AccessModifier.UNIVERSAL);
		
		thread.setNative(true);
		BVariable thr_1 = new BVariable("thr_1", null, null, AccessModifier.RESTRICTED);
		thread.addParameter(thr_1);
		thread.setNativeRunnable(() -> {
			if(thread.getParameters().size() == 1) {
				String threadName = thread.getParameters().get(0).getValue().toString();
				new Thread(() -> {
					
					SyntaxManager.callFunction(threadName, new Parser(file));
				}).start();
			} else {
				Console.E("invalid parameter count");
			}
		});
		
		addFunction(thread);
		
		//standard primitive type functions
		BFunction tostring = new BFunction("native::toString", null, file,
				AccessModifier.UNIVERSAL);
		
		tostring.setNative(true);
		BVariable tostring_1 = new BVariable("tostring_1", null, null, AccessModifier.RESTRICTED);
		tostring.addParameter(tostring_1);
		tostring.setNativeRunnable(() -> {
			if(tostring.getParameters().size() == 1) {
				String str_val = tostring.getParameters().get(0).getValue().toString();
				tostring.setReturnValue(str_val);
			} else {
				Console.E("invalid parameter count");
			}
		});
		
		addFunction(tostring);
		
		BFunction todouble = new BFunction("native::toDouble", null, file,
				AccessModifier.UNIVERSAL);
		
		todouble.setNative(true);
		BVariable todouble_1 = new BVariable("todouble_1", null, null, AccessModifier.RESTRICTED);
		todouble.addParameter(todouble_1);
		todouble.setNativeRunnable(() -> {
			if(todouble.getParameters().size() == 1) {
				String str_val = todouble.getParameters().get(0).getValue().toString();
				todouble.setReturnValue(Double.parseDouble(str_val));
			} else {
				Console.E("invalid parameter count");
			}
		});
		
		addFunction(todouble);
		
		BFunction toint = new BFunction("native::toInteger", null, file,
				AccessModifier.UNIVERSAL);
		
		toint.setNative(true);
		BVariable toint_1 = new BVariable("toint_1", null, null, AccessModifier.RESTRICTED);
		toint.addParameter(toint_1);
		toint.setNativeRunnable(() -> {
			if(toint.getParameters().size() == 1) {
				String str_val = toint.getParameters().get(0).getValue().toString();
				toint.setReturnValue(Integer.parseInt(str_val));
			} else {
				Console.E("invalid parameter count");
			}
		});
		
		addFunction(toint);
		
		BFunction rand = new BFunction("native::rand", null, file,
				AccessModifier.UNIVERSAL);
		
		rand.setNative(true);
		rand.setNativeRunnable(() -> {
			rand.setReturnValue(new Random().nextInt(Integer.MAX_VALUE));
		});
		
		addFunction(rand);
		
		BFunction gc = new BFunction("native::gc", null, file,
				AccessModifier.UNIVERSAL);
		
		gc.setNative(true);
		gc.setNativeRunnable(() -> {
			java.lang.Runtime.getRuntime().gc();
		});
		
		addFunction(gc);
		
		BFunction pdb = new BFunction("native::print_debug", null, file,
				AccessModifier.UNIVERSAL);
		
		pdb.setNative(true);
		pdb.setNativeRunnable(() -> {
			Runtime.print_debug();
		});
		
		addFunction(pdb);
		
		BFunction print = new BFunction("native::print", null, file,
				AccessModifier.UNIVERSAL);
		
		print.setNative(true);
		BVariable p_1 = new BVariable("print_p1", null, null, AccessModifier.RESTRICTED);
		print.addParameter(p_1);
		print.setNativeRunnable(() -> {
			if(print.getParameters().size() == 1) {
				System.out.print(print.getParameters().get(0).getValue());
			} else {
				Console.E("invalid parameter count");
			}
		});
		
		addFunction(print);
		
		BFunction println = new BFunction("native::println", null, file,
				AccessModifier.UNIVERSAL);
		
		println.setNative(true);
		BVariable pln_1 = new BVariable("println_p1", null, null, AccessModifier.RESTRICTED);
		println.addParameter(pln_1);
		println.setNativeRunnable(() -> {
			if(println.getParameters().size() == 1) {
				System.out.println(println.getParameters().get(0).getValue());
			} else {
				Console.E("invalid parameter count");
			}
		});
		
		addFunction(println);
		
		BFunction stdin = new BFunction("native::stdin", null, file,
				AccessModifier.UNIVERSAL);
		
		stdin.setNative(true);
		stdin.setNativeRunnable(() -> {
			stdin.setReturnValue(Utils.getSpecializedInput());
		});
		
		addFunction(stdin);
	}
	
	public static void print_debug() {
		/*
		 * prints debugger
		 */
	 	Runtime.files.stream().forEach(file -> {
			System.out.println("File : " + file.getName());
		});
		System.out.println();
		
		Runtime.functions.stream().forEach(file -> {
			System.out.println("Function : " + file.getName() + "{ " + file.getLines().toString() + " }");
		});
		System.out.println();
		
		System.out.println();
		
		Runtime.types.stream().forEach(file -> {
			System.out.println("Type : " + file.getName());
		});
		System.out.println();
		
		for(BVariable v : Runtime.variables) {
			if(v.getValue() != null) {
				System.out.println("Variable : " + v.getName() + " : { " + v.getValue().toString() + " }");
			} else {
				System.out.println("Variable : " + v.getName());
			}
		}
		System.out.println();
	}
	
	public static void interpret() {
		/*
		 * commence interpreting of file
		 */
		Parser parser = new Parser(Runtime.file);
		parser.start();
	}
	
	public static void addVariable (BVariable variable) {
		Runtime.variables.add(variable);
	}
	
	public static void addType (BType type) {
		types.add(type);
	}
	
	public static void addFunction (BFunction function) {
		functions.add(function);
	}
	
	//good for replacing variables in strings
	public static String replaceVariableNamesWithValues (String string) {
		for (BVariable var : Runtime.variables) {
			if (string.contains(var.getName())) {
				string = string.replace(var.getName(), var.getValue().toString());
			}
		}
		
		return string;
	}
	
	//load primitive types into memory
	public static void typeInit() {
		BType string = new BType ("string", null, AccessModifier.UNIVERSAL);
		BType integer = new BType ("integer", null, AccessModifier.UNIVERSAL);
		BType doubl = new BType ("double", null, AccessModifier.UNIVERSAL);
		BType bool = new BType ("boolean", null, AccessModifier.UNIVERSAL);
		
		BType list = new BType ("list", null, AccessModifier.UNIVERSAL);
		BType object = new BType ("object", null, AccessModifier.UNIVERSAL);
		
		Runtime.addType(string);
		Runtime.addType(integer);
		Runtime.addType(doubl);
		Runtime.addType(bool);
		Runtime.addType(list);
		Runtime.addType(object);
	}
	
	// this section is designed for strict value parsing and conversion
	public static boolean isInteger(String string) {
		try {
			if(!string.contains(".")) {
				Integer.parseInt(SyntaxManager.solveArithmeticFromString(string).toString());
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	public static boolean isDouble(String string) {
		try {
			if(string.contains(".")) {
				Double.parseDouble(SyntaxManager.solveArithmeticFromString(string).toString());
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	public static boolean isString(String string) {
		string = string.trim();
		
		if(string.startsWith("\"")) {
			if(string.endsWith("\"")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public static boolean isArray(String string) {
		if(string.startsWith("{")) {
			if(string.endsWith("}")) {
				if(string.contains(";")) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public static BVariable getVariableFromName(String name) {
		if (Runtime.containsVariableByName(name)) {
			return Runtime.variables.stream()
					.filter(e -> e.getName().equals(name))
					.findFirst()
					.get(); 
			
		} else {
			return null;
		}
	}
	
	public static BType getTypeFromName(String name) {
		if (Runtime.containsTypeByName(name)) {
			return Runtime.types.stream()
					.filter(e -> e.getName().equals(name))
					.findFirst()
					.get(); 
			
		} else {
			return null;
		}
	}
	
	public static BFunction getFunctionFromName(String name) {
		if (Runtime.containsFunctionByName(name)) {
			return Runtime.functions.stream()
					.filter(e -> e.getName().equals(name))
					.findFirst()
					.get(); 
			
		} else {
			return null;
		}
	}
	
	public static boolean containsVariableByName(String name) {
		return Runtime.variables.stream()
				.filter(e -> e.getName().equals(name))
				.findAny()
				.isPresent();
	}
	
	public static boolean containsTypeByName(String name) {
		return Runtime.types.stream()
				.filter(e -> e.getName().trim().equals(name.trim()))
				.findAny()
				.isPresent();
	}
	
	public static boolean containsFunctionByName(String name) {
		return Runtime.functions.stream()
				.filter(e -> e.getName().equals(name))
				.findAny()
				.isPresent();
	}
	
	//this method checks if two variables can be used in the same instance/scenario
	public static boolean canTwoVariablesBeUsedInInstance(BVariable one, BVariable two) {
		if(one.getAccess() == AccessModifier.GLOBAL) {
			if(two.getFile() == one.getFile()) {
				return true;
			} 
		} else if(two.getAccess() == AccessModifier.GLOBAL) {
			if(two.getFile() == one.getFile()) {
				return true;
			} 
		} else if(one.getAccess() == AccessModifier.UNIVERSAL) {
			return true;
		} else if(two.getAccess() == AccessModifier.UNIVERSAL) {
			return true;
		} else if(one.getFile() == two.getFile()) {
			if(one.getFunction() == two.getFunction()) {
				return true;
			}
		} else {
			return false;
		}
		
		return false;
	}
	
	public static String getStringValue(String string) {
		String[] split = string.split(" ");
		
		//String getString("Burf")
		
		StringBuilder builder = new StringBuilder();
		
		if(string.startsWith("\"") &&
				string.endsWith("\"")) {
		
			string = string.replace("\"", "");
			
			for(String entry : split) {
				if(Runtime.isInteger(entry.trim())) {
					try {
						builder.append(SyntaxManager.solveArithmeticFromString(entry.trim()).toString());
					} catch (ScriptException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (Runtime.containsVariableByName(entry)) {
					builder.append(Runtime.getVariableFromName(entry.trim()).getValue().toString());
				} else {
					builder.append(" " + entry);
				}
			}
		}
		
		return builder
				.toString()
				.replaceAll("\"", "").replaceFirst(" ", "");
	}
	
	public static int getInteger(String line) {
		line = Runtime.replaceVariableNamesWithValues(line);
		
		if(Runtime.isInteger(line)) {
			Object x;
			
			x = 0;
			
			try {
				x = SyntaxManager.solveArithmeticFromString(line);
			} catch (ScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			int value = Integer.parseInt(x.toString());
			
			return value;
		}
		
		return 0;
	}
	
	public static boolean isBoolean(String line) {
		line = line.trim();
		
		if(line.equals("true")) {
			return true;
		} else if(line.equals("false")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean getBoolean(String line) {
		line = line.trim();
		
		if(line.equals("true")) {
			return true;
		} else if(line.equals("false")) {
			return false;
		} else {
			return false;
		}
	}
	
	public static Double getDouble(String line) {
		line = Runtime.replaceVariableNamesWithValues(line);
		
		if(Runtime.isDouble(line)) {
			Object x;
			
			x = 0;
			
			try {
				x = SyntaxManager.solveArithmeticFromString(line);
			} catch (ScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Double value = Double.parseDouble(x.toString());
			
			return value;
		}
		
		return null;
	}
	
	public static ArrayList<Object> getList(Object object) {
		String value = object.toString();
		
		String[] v = value.split("");
		
		if(v[0].equals("[")) {
			if(v[v.length - 1].equals("]")) {
				v[0] = "";
				v[v.length - 1] = "";
			}
		}
		
		value = v.toString();
		
		String[] split = value.split(",");
		ArrayList<Object> list = new ArrayList<>();
		for(String entry : split) {
			Object val = Runtime.getValue(entry.trim());
			
			list.add(val);
		}
		
		return list;
	}
	
	public static boolean isArrayList(Object object) {
		if(object instanceof ArrayList) {
			return true;
		} else {
			String value = object.toString();
			
			if(value.startsWith("[")) {
				if(value.startsWith("]")) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}
	
	public static Object getValue(Object value) {
		String str = value.toString()
				.trim();
		
		String f = SyntaxManager.getStringUntilString(str, SyntaxManager._OPENPB).trim();
		
		if(Runtime.isArrayList(value)) {
			return Runtime.getList(value);
		} else if(Runtime.containsFunctionByName(f)) {
			SyntaxManager.callFunction(str, new Parser(Runtime.file));
			
			BFunction function = Runtime.getFunctionFromName(f);
			
			Object returned = function.getReturnValue();
			
			Object val = getValue(returned
					.toString());
			
			return val;
		} else if(Runtime.containsVariableByName(str)) {
			return Runtime.getVariableFromName(str).getValue();
		} else if(Runtime.isBoolean(str)) {
			return Runtime.getBoolean(str);
		} else if (Runtime.isDouble(str)) {
			try {
				return Double.parseDouble(SyntaxManager
						.solveArithmeticFromString(str)
						.toString());
			} catch (NumberFormatException | ScriptException e) {
				return value;
			}
		} else if (Runtime.isInteger(str)) {
			try {
				return Integer.parseInt(SyntaxManager
						.solveArithmeticFromString(str)
						.toString());
			} catch (NumberFormatException | ScriptException e) {
				return value;
			}
		} else if (Runtime.isArray(str)) {
			ArrayList<String> list = new ArrayList<>();
			
			str = str.replace("{", "")
					.replace("}", "")
					.trim();
			
			String[] split = str.split(";");
			
			for(String entry : split) {
				list.add(entry);
			}
			
			return list;
		} else if (Runtime.isString(str)) {
			return Runtime.getStringValue(str);
		} else {
			return value;
		}
	}
	
	public static BType getType(String string) {
		string = string.trim();
		
		if(Runtime.containsVariableByName(string)) {
			return Runtime.getVariableFromName(string)
					.getType();
		} else if (Runtime.isString(string)) {
			return Runtime.getTypeFromName("string");
		} else if (Runtime.isDouble(string)) {
			return Runtime.getTypeFromName("double");
		} else if (Runtime.isInteger(string)) {
			return Runtime.getTypeFromName("integer");
		} else if (Runtime.isBoolean(string)) {
			return Runtime.getTypeFromName("boolean");
		} else if (Runtime.isArray(string)) {
			return Runtime.getTypeFromName("list");
		} else {
			return Runtime.getTypeFromName("object");
		}
	}
	
	public static boolean compareToValues(String value1, String value2) {
		Object one = Runtime.getValue(value1
				.trim());
		Object two = Runtime.getValue(value2
				.trim());
		
		if(one == two) {
			return true;
		} else {
			return false;
		}
	}
	
	//Main function, all code here executes on the start
	public static BFunction getMainFunction() {
		return Runtime.main;
	}
	
}
