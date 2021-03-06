package com.benbeehler.bsharp.runtime;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
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
	
	public static ArrayList<String> categories = 
			new ArrayList<>();
	
	public static BFunction main;
	
	public static void init(File file) {
		//initialize file, create standard executable function - main
		Runtime.file = file;
		files.add(file);
		
		main = new BFunction("main", null, null, AccessModifier.UNIVERSAL);
		Runtime.addFunction(main);
		
		initCategories();
		initPrimitiveVariables();
		Runtime.typeInit();
		//primitiveInit();
		
		interpret();
	}
	
	public static void initCategories() {
		Runtime.categories.add("native");
	}
	
	public static void initPrimitiveVariables() {
		BVariable nil = new BVariable("nil", "nil", file, AccessModifier.UNIVERSAL);
		nil.setFunction(main);
		addVariable(nil);
		
		BVariable stdin = new BVariable("stdin", System.in, file, AccessModifier.UNIVERSAL);
		stdin.setFunction(Runtime.getFunctionFromName("main"));
		addVariable(stdin);
		
		BVariable stdout = new BVariable("stdout", System.out, file, AccessModifier.UNIVERSAL);
		stdout.setFunction(Runtime.getFunctionFromName("main"));
		addVariable(stdout);
	}
	
	public static void importNative(String function) {
		//Multithreading
		if(function.equals("thread")) {
			BFunction sleep = new BFunction("sleep", null, file,
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
			
			BFunction thread = new BFunction("thread", null, file,
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
		} else if(function.equals("list")) {
			BFunction index = new BFunction("index", null, file,
					AccessModifier.UNIVERSAL);
			
			index.setNative(true);
			BVariable index_1 = new BVariable("index_1", null, null, AccessModifier.RESTRICTED);
			BVariable index_2 = new BVariable("index_2", null, null, AccessModifier.RESTRICTED);
			index.addParameter(index_1);
			index.addParameter(index_2);
			index.setNativeRunnable(() -> {
				if(index.getParameters().size() == 2) {
					Object value = index.getParameters().get(0).getValue();
					int ind = Integer.parseInt(index.getParameters().get(1).getValue().toString());
					
					if(value instanceof ArrayList) {
						@SuppressWarnings("unchecked")
						ArrayList<Object> list = (ArrayList<Object>) value;
						index.setReturnValue(list.get(ind));
					} else {
						Console.E("given value is not recognized as a list (" + value + ")");
					}
				} else {
					Console.E("invalid parameter count");
				}
			});
			
			Runtime.functions.add(index);
			
			BFunction ladd = new BFunction("ladd", null, file,
					AccessModifier.UNIVERSAL);
			
			ladd.setNative(true);
			BVariable ladd_1 = new BVariable("ladd_1", null, null, AccessModifier.RESTRICTED);
			BVariable ladd_2 = new BVariable("ladd_2", null, null, AccessModifier.RESTRICTED);
			ladd.addParameter(ladd_1);
			ladd.addParameter(ladd_2);
			ladd.setNativeRunnable(() -> {
				if(ladd.getParameters().size() == 2) {
					Object value = ladd.getParameters().get(0).getValue();
					Object newVal = ladd.getParameters().get(1).getValue();
					
					if(value instanceof ArrayList) {
						@SuppressWarnings("unchecked")
						ArrayList<Object> list = (ArrayList<Object>) value;
						list.add(newVal);
						ladd.setReturnValue(list);
						System.out.println(ladd.getValue() instanceof ArrayList);
					} else {
						Console.E("given value is not recognized as a list");
					}
				} else {
					Console.E("invalid parameter count");
				}
			});
			
			Runtime.functions.add(ladd);
		} else if(function.equals("boolean")) {
			BFunction equals = new BFunction("equals", null, file,
					AccessModifier.UNIVERSAL);
			
			equals.setNative(true);
			BVariable equals_1 = new BVariable("equals_1", null, null, AccessModifier.RESTRICTED);
			BVariable equals_2 = new BVariable("equals_2", null, null, AccessModifier.RESTRICTED);
			equals.addParameter(equals_1);
			equals.addParameter(equals_2);
			equals.setNativeRunnable(() -> {
				if(equals.getParameters().size() == 2) {
					Object one = equals.getParameters().get(0).getValue();
					Object two = equals.getParameters().get(1).getValue();
					
					//System.out.println(one + " : " + two);
					
					equals.setReturnValue(one.equals(two));
				} else {
					Console.E("invalid parameter count");
				}
			});
			
			Runtime.functions.add(equals);
			
			BFunction greater = new BFunction("greater", null, file,
					AccessModifier.UNIVERSAL);
			
			greater.setNative(true);
			BVariable greater_1 = new BVariable("greater_1", null, null, AccessModifier.RESTRICTED);
			BVariable greater_2 = new BVariable("greater_2", null, null, AccessModifier.RESTRICTED);
			greater.addParameter(greater_1);
			greater.addParameter(greater_2);
			greater.setNativeRunnable(() -> {
				if(greater.getParameters().size() == 2) {
					double one = Double.parseDouble(greater.getParameters().get(0).getValue().toString());
					double two = Double.parseDouble(greater.getParameters().get(1).getValue().toString());
					
					greater.setReturnValue(one > two);
				} else {
					Console.E("invalid parameter count");
				}
			});
			
			Runtime.functions.add(greater);
		} else if(function.equals("system")) {
			BFunction end = new BFunction("destroy", null, file,
					AccessModifier.UNIVERSAL);
			
			end.setNative(true);
			
			end.setNativeRunnable(() -> {
				System.exit(0);
			});
			
			addFunction(end);
		} else if(function.equals("type")) {
			BFunction tostring = new BFunction("toString", null, file,
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
			
			BFunction todouble = new BFunction("toDouble", null, file,
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
			
			BFunction toint = new BFunction("toInteger", null, file,
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
		} else if(function.equals("runtime")) {
			BFunction rand = new BFunction("rand", null, file,
					AccessModifier.UNIVERSAL);
			
			BVariable rand_1 = new BVariable("rand_1", null, null, AccessModifier.RESTRICTED);
			rand.addParameter(rand_1);
			
			rand.setNative(true);
			rand.setNativeRunnable(() -> {
				if(rand.getParameters().size() == 1) {
					int limit = Integer.parseInt(rand.getParameters().get(0).getValue().toString());
					rand.setReturnValue(new Random().nextInt(limit));
				} else {
					Console.E("invalid parameter count");
				}
			});
			
			addFunction(rand);
			
			BFunction gc = new BFunction("gc", null, file,
					AccessModifier.UNIVERSAL);
			
			gc.setNative(true);
			gc.setNativeRunnable(() -> {
				java.lang.Runtime.getRuntime().gc();
			});
			
			addFunction(gc);
			
			BFunction pdb = new BFunction("print_debug", null, file,
					AccessModifier.UNIVERSAL);
			
			pdb.setNative(true);
			pdb.setNativeRunnable(() -> {
				Runtime.print_debug();
			});
			
			addFunction(pdb);
		} else if(function.equals("io")) {
			BFunction puts = new BFunction("puts", null, file,
					AccessModifier.UNIVERSAL);
			
			puts.setNative(true);
			BVariable puts_1 = new BVariable("puts_1", null, null, AccessModifier.RESTRICTED);
			BVariable puts_2 = new BVariable("puts_2", null, null, AccessModifier.RESTRICTED);
			puts.addParameter(puts_1);
			puts.addParameter(puts_2);
			puts.setNativeRunnable(() -> {
				if(puts.getParameters().size() == 2) {
					Object value = puts.getParameters().get(0).getValue();
					String data = puts.getParameters().get(1).getValue().toString();
					
					if(value instanceof PrintStream) {
						@SuppressWarnings("resource")
						PrintStream stream = (PrintStream) value;
						stream.printf(data);
						stream.flush();
					} else {
						Console.E("given value is not recognized as an output stream ("+value+")");
					}
				} else {
					Console.E("invalid parameter count");
				}
			});
			
			Runtime.functions.add(puts);
			
			BFunction outputFile = new BFunction("ostream", null, file,
					AccessModifier.UNIVERSAL);
			
			outputFile.setNative(true);
			BVariable outputf_1 = new BVariable("outputf_1", null, null, AccessModifier.RESTRICTED);
			outputFile.addParameter(outputf_1);
			outputFile.setNativeRunnable(() -> {
				if(outputFile.getParameters().size() == 1) {
					Object parameter = outputFile.getParameters().get(0).getValue();
					String fPath = parameter.toString();
					File file = new File(fPath);
					if(file.exists()) {
						try {
							PrintStream stream = new PrintStream(file);
							outputFile.setReturnValue(stream);
						} catch (FileNotFoundException e) {
							Console.E("failed to create output to file: specified does not exist " + file.getPath());
						}
					} else if(parameter instanceof Socket) {
						@SuppressWarnings("resource")
						Socket socket = (Socket) parameter;
						
						try {
							PrintStream stream = new PrintStream(socket.getOutputStream());
							outputFile.setReturnValue(stream);
						} catch (FileNotFoundException e) {
							Console.E("failed to create output to file: specified does not exist " + file.getPath());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						Console.E("failed to create output to file: specified does not exist " + file.getPath());
					}
				} else {
					Console.E("invalid parameter count");
				}
			});
			
			addFunction(outputFile);
			
			BFunction accept_socket = new BFunction("saccept", null, file,
					AccessModifier.UNIVERSAL);
			
			accept_socket.setNative(true);
			BVariable as_1 = new BVariable("accept_sock_1", null, null, AccessModifier.RESTRICTED);
			accept_socket.addParameter(as_1);
			accept_socket.setNativeRunnable(() -> {
				Object parameter = accept_socket.getParameters().get(0).getValue();
				
				if(parameter instanceof ServerSocket) {
					@SuppressWarnings("resource")
					ServerSocket server = (ServerSocket) parameter;
					try {
						accept_socket.setReturnValue(server.accept());
					} catch (IOException e) {
						Console.E("failed to accept socket");
					}
				} else {
					Console.E("parameter must be serversocket");
				}
			});
			
			Runtime.addFunction(accept_socket);
			
			/*BFunction httpserver = new BFunction("httpServer", null, file, AccessModifier.UNIVERSAL);
			httpserver.setNative(true);
			httpserver.setNativeRunnable(() -> {
		        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		        server.createContext("/test", new MyHandler());
		        server.setExecutor(null); // creates a default executor
		        server.start();
			});*/
			
			BFunction readFile = new BFunction("istream", null, file,
					AccessModifier.UNIVERSAL);
			
			readFile.setNative(true);
			BVariable readfile_1 = new BVariable("readf_1", null, null, AccessModifier.RESTRICTED);
			readFile.addParameter(readfile_1);
			readFile.setNativeRunnable(() -> {
				if(readFile.getParameters().size() == 1) {
					Object parameter = readFile.getParameters().get(0).getValue();
					String fPath = parameter.toString();
					File file = new File(fPath);
					if(file.exists()) {
						try {
							FileInputStream fstream = new FileInputStream(file);
							BufferedInputStream stream = new BufferedInputStream(fstream);
							readFile.setReturnValue(stream);
						} catch (FileNotFoundException e) {
							Console.E("failed to read file: specified does not exist");
						}
					} else if(parameter instanceof Socket) {
						@SuppressWarnings("resource")
						Socket socket = (Socket) parameter;
						try {
							InputStream stream = socket.getInputStream();
							readFile.setReturnValue(stream);
						} catch (IOException e) {
							Console.E("failed to retrieve input stream for socket");
						}
					} else {
						Console.E("failed to read file: specified does not exist");
					}
				} else {
					Console.E("invalid parameter count");
				}
			});
			
			addFunction(readFile);
			
			BFunction read = new BFunction("gets", null, file,
					AccessModifier.UNIVERSAL);
			
			read.setNative(true);
			BVariable read_p1 = new BVariable("read_p1", System.in, null, AccessModifier.RESTRICTED);
			read.addParameter(read_p1);
			read.setNativeRunnable(() -> {
				if(read.getParameters().size() == 1) {
					Object parameter = read.getParameters().get(0).getValue();
					
					if(parameter instanceof InputStream) {
						InputStream stream = (InputStream) parameter;
						try {
							String result = Utils.getInput(stream);
							read.setReturnValue(result);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						Console.E("given value is not recognized as an input stream");
					}
				} else {
					Console.E("invalid parameter count");
				}
			});
			
			addFunction(read);

			BFunction socket = new BFunction("socket", null, file,
					AccessModifier.UNIVERSAL);
			
			socket.setNative(true);
			BVariable socket_1 = new BVariable("socket_1", System.in, null, AccessModifier.RESTRICTED);
			BVariable socket_2 = new BVariable("socket_2", System.in, null, AccessModifier.RESTRICTED);
			socket.addParameter(socket_1);
			socket.addParameter(socket_2);
			socket.setNativeRunnable(() -> {
				if(socket.getParameters().size() == 2) {
					String host = socket.getParameters().get(0).getValue().toString();
					Object portParameter = socket.getParameters().get(1).getValue().toString();
					int port = Integer.parseInt(portParameter.toString());
					try {
						Socket s = new Socket(host, port);
						socket.setReturnValue(s);
					} catch (IOException e) {
						Console.E("socket IO error: server could not found on specified address and port");
					}
				} else {
					Console.E("invalid parameter count");
				}
			});
			
			addFunction(socket);
			
			BFunction server = new BFunction("serversocket", null, file,
					AccessModifier.UNIVERSAL);
			
			server.setNative(true);
			BVariable server_1 = new BVariable("server_1", System.in, null, AccessModifier.RESTRICTED);
			server.addParameter(server_1);
			server.setNativeRunnable(() -> {
				if(server.getParameters().size() == 1) {
					Object portParameter = server.getParameters().get(0).getValue().toString();
					int port = Integer.parseInt(portParameter.toString());
					try {
						ServerSocket result = new ServerSocket(port);
						server.setReturnValue(result);
					} catch (IOException e) {
						Console.E("specified port has already been binded to another program");
					}
				} else {
					Console.E("invalid parameter count");
				}
			});
			
			addFunction(server);
			
			BFunction readLine = new BFunction("getsln", null, file,
					AccessModifier.UNIVERSAL);
			
			readLine.setNative(true);
			BVariable stdinln_1 = new BVariable("readlinein_p1", System.in, null, AccessModifier.RESTRICTED);
			readLine.addParameter(stdinln_1);
			readLine.setNativeRunnable(() -> {
				if(readLine.getParameters().size() == 1) {
					Object parameter = readLine.getParameters().get(0).getValue();
					
					if(parameter instanceof InputStream) {
						InputStream stream = (InputStream) parameter;
						try {
							String result = Utils.getInputLine(stream);
							readLine.setReturnValue(result);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						Console.E("given value is not recognized as an input stream");
					}
				} else {
					Console.E("invalid parameter count");
				}
			});
			
			addFunction(readLine);
		} else {
			Console.E("unrecognized native import: " + function);
		}
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
			print_debug_type_variables(file);
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
	
	public static void print_debug_type_variables(BType type) {
		for(BVariable variable : type.getVariables()) {
			printSubVariables(variable);
			System.out.println(type.getName() + "->" + variable.getName());
		}
	}
	
	public static void printSubVariables(BVariable variable) {
		if(variable.getSubVariables().size() != 0) {
			for(BVariable var : variable.getSubVariables()) {
				//System.out.println(variable.getName() + "->" + var.getName());
				printSubVariables(var);
			}
		}
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
	public static String replaceVariableNamesWithValues(String string) {
		for (BVariable var : Runtime.variables) {
			if (string.contains(var.getName())) {
				string = string.replaceAll(var.getName(), var.getValue().toString());
			}
		}
		
		return string;
	}
	
	//load primitive types into memory
	public static void typeInit() {
		BType string = new BType ("string", file, AccessModifier.UNIVERSAL);
		BType integer = new BType ("integer", file, AccessModifier.UNIVERSAL);
		BType doubl = new BType ("double", file, AccessModifier.UNIVERSAL);
		BType bool = new BType ("boolean", file, AccessModifier.UNIVERSAL);
		BType byt = new BType ("byte", file, AccessModifier.UNIVERSAL);
		BType ch = new BType ("char", file, AccessModifier.UNIVERSAL);
		
		BType list = new BType ("list", file, AccessModifier.UNIVERSAL);
		BType object = new BType ("object", file, AccessModifier.UNIVERSAL);
		BType socket = new BType ("socket", file, AccessModifier.UNIVERSAL);
		
		Runtime.addType(string);
		Runtime.addType(integer);
		Runtime.addType(doubl);
		Runtime.addType(bool);
		Runtime.addType(list);
		Runtime.addType(object);
		Runtime.addType(byt);
		Runtime.addType(ch);
		Runtime.addType(socket);
	}
	
	// this section is designed for strict value parsing and conversion
	public static boolean isInteger(Object object) {
		if(object instanceof Integer) {
			return true;
		} else {
			Character[] valid = new Character[]{ 
					'0',
					'1',
					'2',
					'3',
					'4',
					'5',
					'6',
					'7',
					'8',
					'9'};
			
			List<Character> list = Arrays.asList(valid);
			
			String string = "";
			try {
				string = SyntaxManager.solveArithmeticFromString(object.toString()).toString();
			} catch (ScriptException e) {
				Console.log("Failed to process arithmetic: script failed");
			}
			char[] characters = string.toCharArray();
			
			for(char c : characters) {
				if(!list.contains(c)) return false;
			}
		}
 
		return true;
	}
	
	public static boolean isDouble(Object object) {
		if(object instanceof Integer) {
			return true;
		} else {
			Character[] valid = new Character[]{ 
					'0',
					'1',
					'2',
					'3',
					'4',
					'5',
					'6',
					'7',
					'8',
					'9',
					'.'};
			
			List<Character> list = Arrays.asList(valid);
			
			String string = "";
			try {
				string = SyntaxManager.solveArithmeticFromString(object.toString()).toString();
			} catch (ScriptException e) {
				Console.log("Failed to process arithmetic: script failed");
			}
			char[] characters = string.toCharArray();
			
			for(char c : characters) {
				if(!list.contains(c)) return false;
			}
		}
 
		return true;
	}
	
	public static boolean isString(String string) {
		string = string.trim();
		if(string.contains(SyntaxManager._EQUAL)) 
			return false;
		if((string.startsWith("\"") && string.endsWith("\"")) ||
				string.contains(SyntaxManager._STRSPLIT)) 
			return true;
		return false;
	}
	
	public static boolean isArray(String string) {
		if(string.startsWith("{")) {
			if(string.endsWith("}")) {
				if(string.contains(SyntaxManager._LISTSPLIT)) {
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
		StringBuilder sb = new StringBuilder();
		if(isString(string)) {
			String[] split = string.split(SyntaxManager
					._STRSPLIT);
			
			for(String str : split) {
				//str = str.trim();
				if(Runtime.isFunction(str.trim())) {
					sb.append(getValue(str.trim(), new Parser(file)));
				} else if(str.trim().startsWith("\"")
						&& str.trim().endsWith("\"")) {
					sb.append(str.replaceAll("\"", ""));
				} else {
					sb.append(getValue(str, new Parser(file)));
				}
			}
		}
		
		return sb.toString();
	}
	
	public static int getInteger(String line) {
		line = Runtime.replaceVariableNamesWithValues(SyntaxManager.raw(line));
		
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
		
		if(line.equals("true") 
				|| line.equals("false")) 
			return true;
		else
			if(line.startsWith("!")) return true;
		else 
			if(line.contains(SyntaxManager._EQUAL)) 
				return true;
			else
				return false;
	}
	
	public static boolean getBoolean(Object l) {
		String line = l.toString().trim();
		
		if(line.trim().equals("true")) {
			return true;
		} else if(line.equals("false")) {
			return false;
		} else {
			if(line.startsWith("!")) {
				if(line.equals("true")) return false;
				if(line.equals("false")) return true;
				line = line
						.replaceFirst("!", "");
				return !getBoolean(line
						.replaceFirst("!", ""));
			} else if(Runtime.isFunction(line)) {
				String f = SyntaxManager.getStringUntilString(line, SyntaxManager._OPENPB).trim();
				
				Parser parser = new Parser(Runtime.file);
				SyntaxManager.callFunction(line, parser);
				BFunction function = Runtime.getFunctionFromName(f);
				Object returned = function.getReturnValue();
				Object val = getValue(returned, parser);
				return getBoolean(val);
			} else if(Runtime.containsVariableByName(line.trim())) {
				return getBoolean(Runtime.getVariableFromName(line).getValue());
			}
		}
		return false;
	}
	
	public static boolean getBoolean(Object l, Parser parser) {
		String line = l.toString().trim();
		
		if(line.trim().equals("true")) {
			return true;
		} else if(line.equals("false")) {
			return false;
		} else {
			if(line.startsWith("!")) {
				if(line.equals("true")) return true;
				if(line.equals("false")) return false;
				line = line
						.replaceFirst("!", "");
				return !getBoolean(line
						.replaceFirst("!", ""));
			} else if(Runtime.isFunction(line)) {
				String f = SyntaxManager.getStringUntilString(line, SyntaxManager._OPENPB).trim();
				
				SyntaxManager.callFunction(line, parser);
				BFunction function = Runtime.getFunctionFromName(f);
				Object returned = function.getReturnValue();
				Object val = getValue(returned, parser);
				return getBoolean(val);
			} else if(Runtime.containsVariableByName(line.trim())) {
				return getBoolean(Runtime.getVariableFromName(line).getValue());
			}
		}
		
		return false;
	}
	
	public static Double getDouble(String line) {
		line = Runtime.replaceVariableNamesWithValues(SyntaxManager.raw(line));
		
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
			Object val = Runtime.getValue(entry.trim(),
					new Parser(file));
			
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
	
	public static Object getValue(Object value, Parser parser) {
		String str = value.toString()
				.trim();
		
		String f = SyntaxManager.getStringUntilString(str, SyntaxManager._OPENPB).trim();
		
		if(Runtime.isArrayList(value)) {
			return Runtime.getList(value);
		} else if (Runtime.isInteger(SyntaxManager.raw(str))) {
			return getInteger(SyntaxManager.raw(str));
		} else if (Runtime.isDouble(SyntaxManager.raw(str))) {
			try {
				return getDouble(SyntaxManager.raw(str));
			} catch (NumberFormatException e) {
				return value;
			}
		} else if (Runtime.isArray(str)) {
			ArrayList<String> list = new ArrayList<>();
			
			String[] array = str.split("");
			array[0] = "";
			array[array.length-1] = "";
			str = SyntaxManager.convert(array);
			
			String[] split = str.split(SyntaxManager._LISTSPLIT);
			
			for(String entry : split) {
				list.add(entry);
			}
			
			return list;
		} else if(Runtime.isString(str)) {
			return Runtime.getStringValue(str);
		} else if(Runtime.containsFunctionByName(f)) {
			SyntaxManager.callFunction(str, parser);
					
			BFunction function = Runtime.getFunctionFromName(f);
					
			Object returned = function.getReturnValue();
					
			Object val = getValue(returned, parser);
					
			return val;
		} else if(Runtime.containsVariableByName(str)) {
			return Runtime.getVariableFromName(str).getValue();
		} else if(parser.getCurrentFunction().containsParameterByName(str)) {
			return parser.getCurrentFunction().getParameterFromName(str).getValue();
		} else if(isBoolean(str)) {
			return getBoolean(str, parser);
		} else {
			Console.E("unrecognized expression: " + SyntaxManager.raw(str));
			return value;
		}
	}
	
	public static Object getValue(Object value) {
		String str = value.toString()
				.trim();
		
		String f = SyntaxManager.getStringUntilString(str, SyntaxManager._OPENPB).trim();
		
		if(Runtime.isArrayList(value)) {
			return Runtime.getList(value);
		} else if (Runtime.isInteger(str)) {
			return getInteger(str);
		} else if (Runtime.isDouble(str)) {
			try {
				return getDouble(value.toString());
			} catch (NumberFormatException e) {
				return value;
			}
		} else if (Runtime.isArray(str)) {
			ArrayList<String> list = new ArrayList<>();
			
			String[] array = str.split("");
			array[0] = "";
			array[array.length-1] = "";
			str = SyntaxManager.convert(array);
			
			String[] split = str.split(SyntaxManager._LISTSPLIT);
			
			for(String entry : split) {
				list.add(entry);
			}
			
			return list;
		} else if(Runtime.isString(str)) {
			return Runtime.getStringValue(str);
		} else if(Runtime.containsFunctionByName(f)) {
			SyntaxManager.callFunction(str);
					
			BFunction function = Runtime.getFunctionFromName(f);
					
			Object returned = function.getReturnValue();
					
			Object val = getValue(returned);
					
			return val;
		} else if(Runtime.containsVariableByName(str)) {
			return Runtime.getVariableFromName(str).getValue();
		} else if(isBoolean(str)) {
			return getBoolean(str);
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
	
	public static boolean isFunction(String string) {
		String name = SyntaxManager.getStringUntilString(string, SyntaxManager._OPENPB)
				.trim();
		
		return Runtime.containsFunctionByName(name);
	}
	
	public static boolean compareToValues(String value1, String value2) {
		Object one = Runtime.getValue(value1
				.trim(), new Parser(file));
		Object two = Runtime.getValue(value2
				.trim(), new Parser(file));
		
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
