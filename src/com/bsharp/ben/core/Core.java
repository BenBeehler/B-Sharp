package com.bsharp.ben.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import javax.script.ScriptException;

import com.bsharp.ben.Console;
import com.bsharp.ben.objects.AccessModifier;
import com.bsharp.ben.objects.BFunction;
import com.bsharp.ben.objects.BType;
import com.bsharp.ben.objects.BVariable;
import com.bsharp.ben.objects.NativeFunction;
import com.bsharp.ben.syntax.Parser;
import com.bsharp.ben.syntax.SyntaxManager;

public class Core {
	
	public static File file;
	
	public static ArrayList <File> files = 
			new ArrayList <> ();
	
	public static ArrayList <BVariable> variables = 
			new ArrayList <> ();
	
	public static ArrayList <BFunction> functions = 
			new ArrayList <> ();
	
	public static ArrayList <NativeFunction> nativeFunctions = 
			new ArrayList <> ();
	
	public static ArrayList <BType> types = 
			new ArrayList<> ();
	
	public static BFunction main;
	
	public static void init(File file, ExecutionType type) {
		Core.file = file;
		files.add(file);
		
		Core.typeInit();
		Core.nativeInit();
		
		main = new BFunction("main", null, null, AccessModifier.UNIVERSAL);
		Core.addFunction(main);
		
		if (type.equals(ExecutionType.INTERPRET)) {
			interpret();
		}
	}
	
	public static void print_debug() {
	 	Core.files.stream().forEach(file -> {
			System.out.println("File : " + file.getName());
		});
		System.out.println();
		
		Core.functions.stream().forEach(file -> {
			System.out.println("Function : " + file.getName() + "{ " + file.getLines().toString() + " }");
		});
		System.out.println();
		
		Core.nativeFunctions.stream().forEach(file -> {
			System.out.println("Native Function : " + file.getName());
		});
		System.out.println();
		
		Core.types.stream().forEach(file -> {
			System.out.println("Type : " + file.getName());
		});
		System.out.println();
		
		for(BVariable v : Core.variables) {
			if(v.getValue() != null) {
				System.out.println("Variable : " + v.getName() + " : { " + v.getValue().toString() + " }");
			} else {
				System.out.println("Variable : " + v.getName());
			}
		}
		System.out.println();
	}
	
	public static void interpret () {
		Parser parser = new Parser (Core.file);
		parser.start();
	}
	
	public static void addVariable (BVariable variable) {
		Core.variables.add(variable);
	}
	
	public static void addType (BType type) {
		types.add(type);
	}
	
	public static void addFunction (BFunction function) {
		functions.add(function);
	}
	
	public static String replaceVariableNamesWithValues (String string) {
		for (BVariable var : Core.variables) {
			if (string.contains(var.getName())) {
				string = string.replace(var.getName(), var.getValue().toString());
			}
		}
		
		return string;
	}
	
	public static void typeInit () {
		BType string = new BType ("string", null, AccessModifier.UNIVERSAL);
		BType integer = new BType ("integer", null, AccessModifier.UNIVERSAL);
		BType doubl = new BType ("double", null, AccessModifier.UNIVERSAL);
		BType bool = new BType ("boolean", null, AccessModifier.UNIVERSAL);
		
		BType list = new BType ("list", null, AccessModifier.UNIVERSAL);
		BType object = new BType ("object", null, AccessModifier.UNIVERSAL);
		
		Core.addType(string);
		Core.addType(integer);
		Core.addType(doubl);
		Core.addType(bool);
		Core.addType(list);
		Core.addType(object);
	}
	
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
		if (Core.containsVariableByName(name)) {
			return Core.variables.stream()
					.filter(e -> e.getName().equals(name))
					.findFirst()
					.get(); 
			
		} else {
			return null;
		}
	}
	
	public static BType getTypeFromName(String name) {
		if (Core.containsTypeByName(name)) {
			return Core.types.stream()
					.filter(e -> e.getName().equals(name))
					.findFirst()
					.get(); 
			
		} else {
			return null;
		}
	}
	
	public static BFunction getFunctionFromName(String name) {
		if (Core.containsFunctionByName(name)) {
			return Core.functions.stream()
					.filter(e -> e.getName().equals(name))
					.findFirst()
					.get(); 
			
		} else {
			return null;
		}
	}
	
	public static NativeFunction getNativeFunctionByName(String name) {
		if (Core.containsNativeFunctionByName(name)) {
			return Core.nativeFunctions.stream()
					.filter(e -> e.getName().equals(name))
					.findFirst()
					.get(); 
			
		} else {
			return null;
		}
	}
	
	public static boolean containsVariableByName(String name) {
		return Core.variables.stream()
				.filter(e -> e.getName().equals(name))
				.findAny()
				.isPresent();
	}
	
	public static boolean containsTypeByName(String name) {
		return Core.types.stream()
				.filter(e -> e.getName().trim().equals(name.trim()))
				.findAny()
				.isPresent();
	}
	
	public static boolean containsFunctionByName(String name) {
		return Core.functions.stream()
				.filter(e -> e.getName().equals(name))
				.findAny()
				.isPresent();
	}
	
	public static boolean containsNativeFunctionByName(String name) {
		return Core.nativeFunctions.stream()
				.filter(e -> e.getName().equals(name))
				.findAny()
				.isPresent();
	}
	
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
				if(Core.isInteger(entry.trim())) {
					try {
						builder.append(SyntaxManager.solveArithmeticFromString(entry.trim()).toString());
					} catch (ScriptException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (Core.containsVariableByName(entry)) {
					builder.append(Core.getVariableFromName(entry.trim()).getValue().toString());
				} else {
					builder.append(" " + entry);
				}
			}
		}
		
		return builder
				.toString()
				.replaceAll("\"", "");
	}
	
	public static int getInteger(String line) {
		line = Core.replaceVariableNamesWithValues(line);
		
		if(Core.isInteger(line)) {
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
		line = Core.replaceVariableNamesWithValues(line);
		
		if(Core.isDouble(line)) {
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
		
		return 0.1;
	}
	
	@SuppressWarnings("unchecked")
	public static void nativeInit() {
		//Println Native
		NativeFunction println = new NativeFunction("println", null);
		println.setRunnable(() -> {
			Console.log(println.getParameters().get(0).getValue().toString().trim());
		});
		
		Core.nativeFunctions.add(println);
		
		//Print Native
		NativeFunction print = new NativeFunction("print", null);
		print.setRunnable(() -> {
			System.out.print(print.getParameters().get(0).getValue().toString().trim());
		});
		
		Core.nativeFunctions.add(print);
		
		/*//Pull Native
		NativeFunction pull = new NativeFunction("pull", null);
		pull.setRunnable(() -> {
			String host = pull.getParameters().get(0).getValue().toString().trim();
			int port = Integer
					.parseInt(pull.getParameters().get(1).getValue().toString());
			
			String password = pull.getParameters().get(2).getValue().toString().trim();
			String path = pull.getParameters().get(3).getValue().toString().trim();
			
			DBCAPI.start(host, port);
			pull.setReturnValue(DBCAPI.pullValue(path, password));
		});
		
		Core.nativeFunctions.add(pull);
		
		NativeFunction update = new NativeFunction("update", null);
		update.setRunnable(() -> {
			String host = pull.getParameters().get(0).getValue().toString();
			int port = Integer
					.parseInt(pull.getParameters().get(1).getValue().toString());
			
			String password = pull.getParameters().get(2).getValue().toString();
			String path = pull.getParameters().get(3).getValue().toString();
			Object value = pull.getParameters().get(4).getValue().toString();
			
			DBCAPI.start(host, port);
			DBCAPI.update(path, value.toString(), password);
		});
		
		Core.nativeFunctions.add(update);*/
		
		//Add-To-List Native
		NativeFunction addValue = new NativeFunction("addValue", null);
		addValue.setRunnable(() -> {
			if(addValue.getParameters().get(0).getValue() instanceof ArrayList<?>) {
				ArrayList<Object> list = (ArrayList<Object>) addValue.getParameters().get(0).getValue();
				list.add(addValue.getParameters().get(1).getValue());
				
				addValue.setReturnValue(list);
			}
		});
		
		Core.nativeFunctions.add(addValue);
		
		//Add-To-List Native
		NativeFunction writeToFile = new NativeFunction("writeF", null);
		writeToFile.setRunnable(() -> {
			File file = new File(writeToFile.getParameters().get(0).getValue().toString());
			String data = writeToFile.getParameters().get(1).getValue().toString();
			
			if(file.exists()) {
				try {
					Scanner scanner = new Scanner(file);
					
					StringBuilder builder = new StringBuilder();
					
					while(scanner.hasNext()) {
						builder.append(scanner.nextLine() + "\n");
					}
					
					scanner.close();
					
					PrintStream stream = new PrintStream(file);
					
					stream.print(builder.toString() + data + "\n");
					
					stream.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				Console.E("Interal Error: The file you wanted to write does not exist...");
			}
		});
		
		Core.nativeFunctions.add(writeToFile);
		
		/*
		 */
		
		NativeFunction debugger = new NativeFunction("db", null);
		debugger.setRunnable(() -> {
			print_debug();
		});
		
		Core.nativeFunctions.add(debugger);
		
		NativeFunction input = new NativeFunction("input", null);
		input.setRunnable(() -> {
			System.out.print(input.getParameters().get(0).getValue().toString());
			
			BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
			String line;
			
			StringBuilder builder = new StringBuilder();
			
			try {
				while ((line = r.readLine()) != null) {
				    builder.append(line);
				    break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			input.setReturnValue(builder.toString()
					.trim());
		});
		
		Core.nativeFunctions.add(input);
		
		NativeFunction ts = new NativeFunction("toString", null);
		ts.setRunnable(() -> {
			Object string = ts.getParameters().get(0).getValue();
			ts.setReturnValue(string.toString());
		});
		
		Core.nativeFunctions.add(ts);
		
		NativeFunction ti = new NativeFunction("toInteger", null);
		ti.setRunnable(() -> {
			Object p1 = ti.getParameters().get(0).getValue();
			
			if(Core.getValue(p1.toString()) instanceof Integer) {
				ti.setReturnValue(Core.getValue(p1.toString()));
			} else {
				Console.log("Internal Error: Cannot convert string to integer...");
			}
		});
		
		Core.nativeFunctions.add(ti);
		
		NativeFunction td = new NativeFunction("toDouble", null);
		td.setRunnable(() -> {
			Object p1 = td.getParameters().get(0).getValue();
			
			if(Core.getValue(p1.toString()) instanceof Integer) {
				td.setReturnValue(Core.getValue(p1.toString()));
			} else {
				Console.log("Internal Error: Cannot convert string to double...");
			}
		});
		
		Core.nativeFunctions.add(td);
		
		NativeFunction equals = new NativeFunction("equals", null);
		equals.setRunnable(() -> {
			Object one = equals.getParameters().get(0).getValue();
			Object two = equals.getParameters().get(1).getValue();
			
			if(Core.compareToValues(one.toString(), two.toString())) {
				equals.setReturnValue(true);
			} else {
				equals.setReturnValue(false);
			}
			
			//Deprecated below
			/*if(Core.getValue(one.toString()).equals(Core.getValue(two.toString()))) {
				equals.setReturnValue(true);
			} else {
				equals.setReturnValue(false);
			}*/ 
		});
		
		Core.nativeFunctions.add(equals);
		
		NativeFunction quit = new NativeFunction("quit", null);
		quit.setRunnable(() -> {
			int status = Integer.parseInt(quit.getParameters().get(0).getValue().toString());
			
			System.exit(status);
		});
		
		Core.nativeFunctions.add(quit);
		
		NativeFunction throwError = new NativeFunction("throwError", null);
		throwError.setRunnable(() -> {
			Console.E("Internal Error: There was a critical error thrown...");
		});
		
		Core.nativeFunctions.add(throwError);
		
		NativeFunction removeValue = new NativeFunction("removeValue", null);
		removeValue.setRunnable(() -> {
			if(addValue.getParameters().get(0).getValue() instanceof ArrayList<?>) {
				ArrayList<Object> list = (ArrayList<Object>) addValue.getParameters().get(0).getValue();
				list.remove(addValue.getParameters().get(1).getValue());
				
				addValue.setReturnValue(list);
			}
		});
		
		Core.nativeFunctions.add(removeValue);
		
		NativeFunction createFile = new NativeFunction("createFile", null);
		createFile.setRunnable(() -> {
			File file = new File(createFile.getParameters().get(0).getValue().toString().trim());
			
			if(!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					
				}
			}
		});
		
		Core.nativeFunctions.add(createFile);
		
		NativeFunction split = new NativeFunction("split", null);
		split.setRunnable(() -> {
			String string = split.getParameters().get(0).getValue().toString();
			String regex = split.getParameters().get(1).getValue().toString()
					.trim();
			
			ArrayList<String> list = new ArrayList<>();
			
			for(String entry : string.split(regex)) {
				list.add(entry);
			}
			
			split.setReturnValue(list);
		});
		
		Core.nativeFunctions.add(split);
		
		NativeFunction index = new NativeFunction("index", null);
		index.setRunnable(() -> {
			String string = split.getParameters().get(0).getValue().toString();
			int ind = (int) split.getParameters().get(1).getValue();
			
			index.setReturnValue(SyntaxManager.getCharFromPosition(string, ind));
		});
		
		Core.nativeFunctions.add(index);
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
			Object val = Core.getValue(entry.trim());
			
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
		
		String[] spl = str.split(" ");
		
		if(Core.isArrayList(value)) {
			return Core.getList(value);
		} else if(Core.containsFunctionByName(spl[0])) {
			SyntaxManager.callFunction(str);
			
			BFunction function = Core.getFunctionFromName(spl[0]);
			
			Object returned = function.getReturnValue();
			
			Object val = getValue(returned
					.toString());
			
			return val;
		} else if(Core.containsVariableByName(str)) {
			return Core.getVariableFromName(str).getValue();
		} else if(Core.isBoolean(str)) {
			return Core.getBoolean(str);
		} else if (Core.isDouble(str)) {
			try {
				return Double.parseDouble(SyntaxManager
						.solveArithmeticFromString(str)
						.toString());
			} catch (NumberFormatException | ScriptException e) {
				// TODO Auto-generated catch block
				return value;
			}
		} else if (Core.isInteger(str)) {
			try {
				return Integer.parseInt(SyntaxManager
						.solveArithmeticFromString(str)
						.toString());
			} catch (NumberFormatException | ScriptException e) {
				return value;
			}
		} else if (Core.isArray(str)) {
			ArrayList<String> list = new ArrayList<>();
			
			str = str.replace("{", "")
					.replace("}", "")
					.trim();
			
			String[] split = str.split(";");
			
			for(String entry : split) {
				list.add(entry);
			}
			
			return list;
		} else if (Core.isString(str)) {
			return Core.getStringValue(str);
		} else {
			return value;
		}
	}
	
	public static BType getType(String string) {
		string = string.trim();
		
		if(Core.containsVariableByName(string)) {
			return Core.getVariableFromName(string)
					.getType();
		} else if (Core.isString(string)) {
			return Core.getTypeFromName("string");
		} else if (Core.isDouble(string)) {
			return Core.getTypeFromName("double");
		} else if (Core.isInteger(string)) {
			return Core.getTypeFromName("integer");
		} else if (Core.isBoolean(string)) {
			return Core.getTypeFromName("boolean");
		} else if (Core.isArray(string)) {
			return Core.getTypeFromName("list");
		} else {
			return Core.getTypeFromName("object");
		}
	}
	
	public static boolean compareToValues(String value1, String value2) {
		Object one = Core.getValue(value1
				.trim());
		Object two = Core.getValue(value2
				.trim());
		
		if(one == two) {
			return true;
		} else {
			return false;
		}
	}
	
	
	public static BFunction getMainFunction() {
		return Core.main;
	}
	
}
