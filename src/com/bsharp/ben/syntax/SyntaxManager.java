package com.bsharp.ben.syntax;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.bsharp.ben.Console;
import com.bsharp.ben.core.Core;
import com.bsharp.ben.objects.AccessModifier;
import com.bsharp.ben.objects.BFunction;
import com.bsharp.ben.objects.BType;
import com.bsharp.ben.objects.BVariable;
import com.bsharp.ben.objects.NativeFunction;

public class SyntaxManager {
	
	public static String getStringUntilChar(String string, char ch) {
		StringBuilder s = new StringBuilder();
		
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			
			if (c == ch) {
				break;
			} else {
				String[] str = string.split("");
				
				s.append(str[i]);
			}
		}
		
		return s.toString();
	}
	
	public static String getStringBetweenTwoChars(String string, char one, char two) {
		int o = indexCharInString(string, one);
		int t = indexCharInString(string, two);
		
		StringBuilder s = new StringBuilder();
		
		while (o > t) {
			s.append(string.charAt(0));
			o++;
		}
		
		return s.toString();
	}
	
	public static String getStringBetweenTwoIndexes(String string, int one, int two) {
		int o = one;
		int t = two;
		
		StringBuilder s = new StringBuilder();
		
		while (o > t) {
			s.append(string.charAt(0));
			o++;
		}
		
		return s.toString();
	}
	
	public static int indexCharInString (String string, char ch) {
		int i = 0;
		
		while (i < string.length()) {
			char c = string.charAt(i);
			
			if (c == ch) {
				break;
			}
			
			i++;
		}
		
		return i;
	}
	
	public static int getCharFromPosition (String string, int pos) {
		return string.charAt(pos);
	}
	
	
	public static Object solveArithmeticFromString (String ar) throws ScriptException {
        /*
         * This code is really trashy
         */

		ar = Core.replaceVariableNamesWithValues(ar);
		
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        return engine.eval(ar.replace(" ", "")); 
    }
	
	
	public static void parseVariable (String line, Parser parser) {
		//var variable string = ""
		BVariable variable;
		variable = new BVariable(null, null, null, null);
		
		line = line.replaceFirst("var", "")
				.trim();
		
		String[] split = 
				line.split(" ");
		
		if (split.length >= 3) {
			String name = split[0];
			
			variable.setName(name);
			
			line = line.replaceFirst(name, "")
					.trim();
			
			split = line.split(" ");
			//string = ""
			
			String type = split[0].trim();
			
			if(type.equals("=")) {
				line = line.replaceFirst(type, "")
						.trim();
				
				//CHECK VARIABLE'S VALUE TO DETERMINE TYPE
				Object v = Core.getValue(line);
				BType t = Core.getType(v.toString());
			
				variable.setType(t);
				variable.setValue(v);
				variable.setFile(parser.getFile());
				variable.setFunction(parser.getCurrentFunction());
				
				if(name.startsWith("!")) {
					variable.setAccessModifier(AccessModifier.UNIVERSAL);
				} else if (parser.getCurrentFunction() == null) {
					variable.setAccessModifier(AccessModifier.GLOBAL);
				} else {
					variable.setAccessModifier(AccessModifier.RESTRICTED);
				}
				
				Core.variables.add(variable);
			} else {
				if (Core.containsTypeByName(type)) {
					BType typ = 
							Core.getTypeFromName(type);
				
					variable.setType(typ);
				
					line = line.replaceFirst(type, "")
							.trim();
				
					split = line.split(" ");
				
					if(split[0].equals("=")) {
						line = line.replaceFirst("=", "")
								.trim();
					
						split = line.split(" ");
					
						//Get all primitive types
					
						if(typ == Core.getTypeFromName("object")) {
							Object value = Core.getValue(line);
						
							variable.setValue(value);
							variable.setFile(parser.getFile());
							variable.setType(Core.getTypeFromName("string"));
							variable.setFunction(parser.getCurrentFunction());
							
							if(variable.getName().startsWith("!")) {
								variable.setAccessModifier(AccessModifier.UNIVERSAL);
							} else if(variable.getFunction() != null) {
								variable.setAccessModifier(AccessModifier.GLOBAL);
							} else {
								variable.setAccessModifier(AccessModifier.RESTRICTED);
							}
							
							Core.addVariable(variable);
						} else if(typ == Core.getTypeFromName("string")) {
							String value = Core.getValue(line).toString();
							
							variable.setValue(value);
							variable.setFile(parser.getFile());
							variable.setType(Core.getTypeFromName("string"));
							variable.setFunction(parser.getCurrentFunction());
							
							if(variable.getName().startsWith("!")) {
								variable.setAccessModifier(AccessModifier.UNIVERSAL);
							} else if(variable.getFunction() != null) {
								variable.setAccessModifier(AccessModifier.GLOBAL);
							} else {
								variable.setAccessModifier(AccessModifier.RESTRICTED);
							}
							
							Core.addVariable(variable);
						} else if(typ == Core.getTypeFromName("integer")) {
							String value = Core.getValue(line).toString();
						
							if(Core.isInteger(value)) {
								variable.setValue(Core.getInteger(value));
								variable.setFile(parser.getFile());
								variable.setType(Core.getTypeFromName("integer"));
								variable.setFunction(parser.getCurrentFunction());
							
								if(variable.getName().startsWith("!")) {
									variable.setAccessModifier(AccessModifier.UNIVERSAL);
								} else if(variable.getFunction() != null) {
									variable.setAccessModifier(AccessModifier.GLOBAL);
								} else {
									variable.setAccessModifier(AccessModifier.RESTRICTED);
								}
							
								Core.addVariable(variable);
							} else {
								Console.E("Internal Error: Invalid value for given type...");
							}
						} else if(typ == Core.getTypeFromName("double")) {
							String value = Core.getValue(line).toString();
						
							if(Core.isDouble(value)) {
								variable.setValue(Core.getDouble(value));
								variable.setFile(parser.getFile());
								variable.setType(Core.getTypeFromName("double"));
								variable.setFunction(parser.getCurrentFunction());
							
								if(variable.getName().startsWith("!")) {
									variable.setAccessModifier(AccessModifier.UNIVERSAL);
								} else if(variable.getFunction() != null) {
									variable.setAccessModifier(AccessModifier.GLOBAL);
								} else {
									variable.setAccessModifier(AccessModifier.RESTRICTED);
								}
							
								Core.addVariable(variable);
							} else {
								Console.E("Internal Error: Invalid value for given type...");
							}
						
						} else if(typ == Core.getTypeFromName("boolean")) {
							Object value = Core.getValue(line);
						
							if(Core.isBoolean(value.toString())) {
								variable.setValue(Core.getBoolean(value.toString()));
								variable.setFile(parser.getFile());
								variable.setType(Core.getTypeFromName("boolean"));
								variable.setFunction(parser.getCurrentFunction());
							
								if(variable.getName().startsWith("!")) {
									variable.setAccessModifier(AccessModifier.UNIVERSAL);
								} else if(variable.getFunction() != null) {
									variable.setAccessModifier(AccessModifier.GLOBAL);
								} else {
									variable.setAccessModifier(AccessModifier.RESTRICTED);
								}
							
								Core.addVariable(variable);
							} else {
								Console.E("Internal Error: Invalid value for given type...");
							}
						} else if(typ == Core.getTypeFromName("list")) {
							
						} else {
							//CHECK VARIABLE'S VALUE TO DETERMINE TYPE
							Object v = Core.getValue(line);
							BType t = Core.getType(v.toString());
							
							variable.setType(t);
							variable.setValue(v);
							variable.setFile(parser.getFile());
							variable.setFunction(parser.getCurrentFunction());
							
							if(name.startsWith("!")) {
								variable.setAccessModifier(AccessModifier.UNIVERSAL);
							} else if (parser.getCurrentFunction() == null) {
								variable.setAccessModifier(AccessModifier.GLOBAL);
							} else {
								variable.setAccessModifier(AccessModifier.RESTRICTED);
							}
							
							//TODO
							
							Core.variables.add(variable);
						}
					} else {
						Console.E("Malformed Syntax: Variable declaration is malformed...");
					}
				} else {
					Console.E("Malformed Syntax: Type never found in variable declaration...");
				}
			}
		} else {
			Console.E("Variable declaration is malformed..");
		}
	}
	
	public static void reinstantiateVariable (String line, Parser parser) {
		//var variable string = ""
		BVariable variable;
		variable = new BVariable(null, null, null, null);
		
		String[] split = 
				line.split(" ");
		
		if (split.length >= 2) {
			String name = split[0];
			
			if(!Core.containsVariableByName(name)) {
				Console.E("Internal Error: " + name + " does not exist!");
			}
			
			Core.variables.remove(Core.getVariableFromName(name));
			
			variable.setName(name);
			
			line = line.replaceFirst(name, "")
					.trim();
			
			split = line.split(" ");
			//string = ""
			
			String type = split[0].trim();
			
			if(type.equals("=")) {
				line = line.replaceFirst(type, "")
						.trim();
				
				//CHECK VARIABLE'S VALUE TO DETERMINE TYPE
				Object v = Core.getValue(line);
				BType t = Core.getType(v.toString());
			
				variable.setType(t);
				variable.setValue(v);
				variable.setFile(parser.getFile());
				variable.setFunction(parser.getCurrentFunction());
				
				if(name.startsWith("!")) {
					variable.setAccessModifier(AccessModifier.UNIVERSAL);
				} else if (parser.getCurrentFunction() == null) {
					variable.setAccessModifier(AccessModifier.GLOBAL);
				} else {
					variable.setAccessModifier(AccessModifier.RESTRICTED);
				}
				
				Core.variables.add(variable);
			} else {
				if (Core.containsTypeByName(type)) {
					BType typ = 
							Core.getTypeFromName(type);
				
					variable.setType(typ);
				
					line = line.replaceFirst(type, "")
							.trim();
				
					split = line.split(" ");
				
					if(split[0].equals("=")) {
						line = line.replaceFirst("=", "")
								.trim();
					
						split = line.split(" ");
					
						//Get all primitive types
					
						if(typ == Core.getTypeFromName("object")) {
							Object value = Core.getValue(line);
						
							variable.setValue(value);
							variable.setFile(parser.getFile());
							variable.setType(Core.getTypeFromName("string"));
							variable.setFunction(parser.getCurrentFunction());
							
							if(variable.getName().startsWith("!")) {
								variable.setAccessModifier(AccessModifier.UNIVERSAL);
							} else if(variable.getFunction() != null) {
								variable.setAccessModifier(AccessModifier.GLOBAL);
							} else {
								variable.setAccessModifier(AccessModifier.RESTRICTED);
							}
							
							Core.addVariable(variable);
						} else if(typ == Core.getTypeFromName("string")) {
							String value = Core.getValue(line).toString();
							
							variable.setValue(value);
							variable.setFile(parser.getFile());
							variable.setType(Core.getTypeFromName("string"));
							variable.setFunction(parser.getCurrentFunction());
							
							if(variable.getName().startsWith("!")) {
								variable.setAccessModifier(AccessModifier.UNIVERSAL);
							} else if(variable.getFunction() != null) {
								variable.setAccessModifier(AccessModifier.GLOBAL);
							} else {
								variable.setAccessModifier(AccessModifier.RESTRICTED);
							}
							
							Core.addVariable(variable);
						} else if(typ == Core.getTypeFromName("integer")) {
							String value = Core.getValue(line).toString();
						
							if(Core.isInteger(value)) {
								variable.setValue(Core.getInteger(value));
								variable.setFile(parser.getFile());
								variable.setType(Core.getTypeFromName("integer"));
								variable.setFunction(parser.getCurrentFunction());
							
								if(variable.getName().startsWith("!")) {
									variable.setAccessModifier(AccessModifier.UNIVERSAL);
								} else if(variable.getFunction() != null) {
									variable.setAccessModifier(AccessModifier.GLOBAL);
								} else {
									variable.setAccessModifier(AccessModifier.RESTRICTED);
								}
							
								Core.addVariable(variable);
							} else {
								Console.E("Internal Error: Invalid value for given type...");
							}
						} else if(typ == Core.getTypeFromName("double")) {
							String value = Core.getValue(line).toString();
						
							if(Core.isDouble(value)) {
								variable.setValue(Core.getDouble(value));
								variable.setFile(parser.getFile());
								variable.setType(Core.getTypeFromName("double"));
								variable.setFunction(parser.getCurrentFunction());
							
								if(variable.getName().startsWith("!")) {
									variable.setAccessModifier(AccessModifier.UNIVERSAL);
								} else if(variable.getFunction() != null) {
									variable.setAccessModifier(AccessModifier.GLOBAL);
								} else {
									variable.setAccessModifier(AccessModifier.RESTRICTED);
								}
							
								Core.addVariable(variable);
							} else {
								Console.E("Internal Error: Invalid value for given type...");
							}
						
						} else if(typ == Core.getTypeFromName("boolean")) {
							Object value = Core.getValue(line);
						
							if(Core.isBoolean(value.toString())) {
								variable.setValue(Core.getBoolean(value.toString()));
								variable.setFile(parser.getFile());
								variable.setType(Core.getTypeFromName("boolean"));
								variable.setFunction(parser.getCurrentFunction());
							
								if(variable.getName().startsWith("!")) {
									variable.setAccessModifier(AccessModifier.UNIVERSAL);
								} else if(variable.getFunction() != null) {
									variable.setAccessModifier(AccessModifier.GLOBAL);
								} else {
									variable.setAccessModifier(AccessModifier.RESTRICTED);
								}
							
								Core.addVariable(variable);
							} else {
								Console.E("Internal Error: Invalid value for given type...");
							}
						} else if(typ == Core.getTypeFromName("list")) {
							Object o = Core.getValue(line);
							
							ArrayList<?> val = (ArrayList<?>) o;
							
							variable.setValue(val);
							variable.setFile(parser.getFile());
							variable.setType(Core.getTypeFromName("list"));
							variable.setFunction(parser.getCurrentFunction());
							
							if(variable.getName().startsWith("!")) {
								variable.setAccessModifier(AccessModifier.UNIVERSAL);
							} else if(variable.getFunction() != null) {
								variable.setAccessModifier(AccessModifier.GLOBAL);
							} else {
								variable.setAccessModifier(AccessModifier.RESTRICTED);
							}
							
							Core.addVariable(variable);
							
							//cycle through every value and add it
							
							for(int i = 0; i < val.size(); i++) {
								Object newV = Core.getValue(val.get(i).toString());
								BType newT = Core.getType(newV.toString());
								
								BVariable l = new BVariable(null, null, null, null);
								
								l.setValue(newV);
								l.setName(name + "[" + i + "]");
								l.setFile(parser.getFile());
								l.setType(newT);
								l.setFunction(parser.getCurrentFunction());
								l.setAccessModifier(variable.getAccess());
								
								Core.addVariable(l);
							}
						} else {
							//CHECK VARIABLE'S VALUE TO DETERMINE TYPE
							Object v = Core.getValue(line);
							BType t = Core.getType(v.toString());
						
							variable.setType(t);
							variable.setValue(v);
							variable.setFile(parser.getFile());
							variable.setFunction(parser.getCurrentFunction());
							
							if(name.startsWith("!")) {
								variable.setAccessModifier(AccessModifier.UNIVERSAL);
							} else if (parser.getCurrentFunction() == null) {
								variable.setAccessModifier(AccessModifier.GLOBAL);
							} else {
								variable.setAccessModifier(AccessModifier.RESTRICTED);
							}
							
							//TODO
							
							Core.variables.add(variable);
						}
					} else {
						Console.E("Malformed Syntax: Variable declaration is malformed...");
					}
				} else {
					Console.E("Malformed Syntax: Type never found in variable declaration...");
				}
			}
		} else {
			Console.E("Variable declaration is malformed..");
		}
	}
	
	public static void parseFunction(String line, Parser parser) {
		BFunction function = 
				new BFunction(null, null, null, null);
		
		line = line.replaceFirst("function", "")
				.trim();
		
		String[] split = line.split(" ");
		
		String name = split[0];
		function.setName(name);
		function.setFile(parser.getFile());
		
		line = line.replaceFirst(name, "").trim();
		
		String lin = SyntaxManager.getStringUntilCharacter(line, ')');
		
		if(lin.startsWith("(")) {
			if(lin.endsWith(")")) {
				lin = lin.replace("(", "");
				lin = lin.replace(")", "");
				lin = lin.trim();
				
				String[] spl = lin.split("");
				spl[0] = "";
				spl[spl.length-1] = "";
				
				String[] comma = lin.split(",");
				
				if(comma.length != 0) {
					for(int i = 0; i < comma.length; i++) {
						String string = comma[i];
					
						string = string.trim();
						
						if(!string.trim().equals("")) {
							String[] spli = string.split(" ");
					
							String typ = spli[1];
							String n = spli[0];
					
							if(Core.containsTypeByName(typ)) {
								BType type = Core.getTypeFromName(typ);
						
								BVariable variable = new BVariable(n,
										null,
										parser.getFile(), 
										AccessModifier.RESTRICTED);
						
								variable.setType(type);
						
								ArrayList<BVariable> parameters = function.getParameters();
								parameters.add(variable);
								function.setParameters(parameters);
								
								Core.variables.add(variable);
							} else {
								Console.E("Malformed Syntax: Invalid parameter on function delcaration " + name);
							}
						}
					}
				}
				
				line = line.replace(lin, "")
						.replace("(", "")
						.replace(")", "")
						.trim();
				
				if(line.equals("{")) {
					function.setNative(false);
					function.setNativeFunction(null);
					
					Core.addFunction(function);
					parser.setCurrentFunction(function);
				} else {
					split = line.split(" ");
					
					if(split[0].trim().equals("nativ:")) {
						if(split.length == 2) {
							String nativ = split[1]
									.trim();
							
							if(Core.containsNativeFunctionByName(nativ)) {
								NativeFunction nf = Core.getNativeFunctionByName(nativ);
								
								function.setNative(true);
								function.setNativeFunction(nf);
								
								Core.functions.add(function);
							} else {
								Console.E("Malformed Syntax: The native function you specified in " + name + " could not be found!");
							}
						} else {
							Console.E("Malformed Syntax: Native function needs last parameter in " + name);
						}
					} else {
						Console.E("Malformed Syntax: Function " + name + " is malformed....");
					}
				}
				
			} else {
				//TODO parameter starting with ( is malformed
			}
		} else {
			//TODO parameter ending with ) is malformed
		}
	}
	
	public static void callFunction(String line) {
		line = line.trim();
		
		String[] split = line.split(" ");
		
		String name = split[0];
		
		BFunction function = Core.getFunctionFromName(name);
		
		line = line.replace(name, "")
				.trim();
		
		if(line.contains("(")) {
			if(line.contains(")")) {
				line = line.replace("(", "");
				line = line.replace(")", "");
				
				line = line.trim();
				
				String[] spl =
						line.split(",");
				
				ArrayList<Object> values = new ArrayList<Object>();
				
				if(spl.length != 0) {
					for(String entry : spl) {
						entry = entry.trim();
					
						values.add(Core.getValue(entry));
					}
				}
				
				if(function.getParameters().size() != 0) {
					if(values.size() == function.getParameters().size()) {
						for(int i = 0; i < function.getParameters().size(); i++) {
							BVariable var = function.getParameters().get(i);
					
							var.setValue(values.get(i));
						}
					
						function.execute();//All parameters added... call function
					} else {
						Console.E("Malformed Syntax: Not enough given parameters to call function...");
					}
				}
			} else {
				Console.E("Malformed Syntax: Parameter list is malformed...");
			}
		} else {
			Console.E("Malformed Syntax: Parameter list is malformed...");
		}
	}
	
	public static void parseIfStatement(String line) {
		line = line.replaceFirst("if", "")
				.trim();
		
		if(line.startsWith("(")) {
			String lin = SyntaxManager.getStringUntilCharacter(line, ')');
			
			String[] b = lin.split("");
			b[0] = "";
			if(b[b.length - 1].equals(")")) {
				b[b.length - 1] = "";
			
				String val = SyntaxManager.constructStringFromArray(b)
						.trim();
			
				Object value = Core.getValue(val);
				
				if(Core.isBoolean(value.toString())) {
					boolean bool = Core.getBoolean(value.toString());
					
					line = line.replace(lin, "")
							.trim();
					
					if(bool == true) {
						SyntaxManager.callFunction(line);
					}
				} else {
					Console.E("Malformed Syntax: If value must be a boolean and cannot be a returnable function");
				}
			} else {
				Console.E("Malformed Syntax: If statement is malformed...");
			}
		} else {
			Console.E("Malformed Syntax: If statement is malformed...");
		}
	}
	
	public static void parseWhileStatement(String line) {
		line = line.replaceFirst("while", "")
				.trim();
		
		if(line.startsWith("(")) {
			String lin = SyntaxManager.getStringUntilCharacter(line, ')');
			
			String[] b = lin.split("");
			b[0] = "";
			if(b[b.length - 1].equals(")")) {
				b[b.length - 1] = "";
			
				String val = SyntaxManager.constructStringFromArray(b)
						.trim();
			
				Object value = Core.getValue(val);
				
				if(Core.isBoolean(value.toString())) {
					boolean bool = Core.getBoolean(value.toString());
					
					line = line.replace(lin, "")
							.trim();
					
					while(bool) {
						SyntaxManager.callFunction(line);
					}
				} else {
					Console.E("Malformed Syntax: While value must be a boolean and cannot be a returnable function");
				}
			} else {
				Console.E("Malformed Syntax: If statement is malformed...");
			}
		} else {
			Console.E("Malformed Syntax: If statement is malformed...");
		}
	}
	
	public static void parseWhileNotStatement(String line) {
		line = line.replaceFirst("!while", "")
				.trim();
		
		if(line.startsWith("(")) {
			String lin = SyntaxManager.getStringUntilCharacter(line, ')');
			
			String[] b = lin.split("");
			b[0] = "";
			if(b[b.length - 1].equals(")")) {
				b[b.length - 1] = "";
			
				String val = SyntaxManager.constructStringFromArray(b)
						.trim();
			
				Object value = Core.getValue(val);
				
				if(Core.isBoolean(value.toString())) {
					boolean bool = Core.getBoolean(value.toString());
					
					line = line.replace(lin, "")
							.trim();
					
					while(!bool) {
						SyntaxManager.callFunction(line);
					}
				} else {
					Console.E("Malformed Syntax: !While value must be a boolean and cannot be a returnable function");
				}
			} else {
				Console.E("Malformed Syntax: If statement is malformed...");
			}
		} else {
			Console.E("Malformed Syntax: If statement is malformed...");
		}
	}
	
	public static void parseNotIfStatement(String line) {
		line = line.replaceFirst("!if", "")
				.trim();
		
		if(line.startsWith("(")) {
			String lin = SyntaxManager.getStringUntilCharacter(line, ')');
			
			String[] b = lin.split("");
			b[0] = "";
			if(b[b.length - 1].equals(")")) {
				b[b.length - 1] = "";
			
				String val = SyntaxManager.constructStringFromArray(b)
						.trim();
			
				Object value = Core.getValue(val);
				
				if(Core.isBoolean(value.toString())) {
					boolean bool = Core.getBoolean(value.toString());
					
					line = line.replace(lin, "")
							.trim();
					
					if(bool == false) {
						SyntaxManager.callFunction(line);
					}
				} else {
					Console.E("Malformed Syntax: !If value must be a boolean and cannot be a returnable function");
				}
			} else {
				Console.E("Malformed Syntax: If statement is malformed...");
			}
		} else {
			Console.E("Malformed Syntax: If statement is malformed...");
		}
	}
	
	public static void parseLoopStatement(String line) {
		line = line.replaceFirst("loop", "")
				.trim();
		
		if(line.startsWith("(")) {
			String lin = SyntaxManager.getStringUntilCharacter(line, ')');
			
			String[] b = lin.split("");
			b[0] = "";
			if(b[b.length - 1].equals(")")) {
				b[b.length - 1] = "";
			
				String val = SyntaxManager.constructStringFromArray(b)
						.trim();
			
				Object value = Core.getValue(val);
				
				if(Core.isInteger(value.toString())) {
					int v = Core.getInteger(value.toString());
					
					line = line.replace(lin, "")
							.trim();
					
					for(int i = 0; i < v; i++) {
						SyntaxManager.callFunction(line);
					}
				} else {
					Console.E("Malformed Syntax: loop value must be an integer and cannot be a returnable function");
				}
			} else {
				Console.E("Malformed Syntax: If statement is malformed...");
			}
		} else {
			Console.E("Malformed Syntax: If statement is malformed...");
		}
	}
	
	public static void parseImport(String line) {
		line = line.trim();
		
		line = line.replace("import", "");
		line = line.trim();
		
		String value = Core.getStringValue(line)
				.trim();
		
		if(value.equals("*")) {
			File f = new File("C:/Program Files (x86)/B#/Library");
			
			if(f.isDirectory()) {
				ArrayList<File> files = scanThroughPaths(f);
				
				files.stream().forEach(fi -> {
					Parser parser = new Parser(fi);
					
					if(fi.exists()) {
						parser.start();
					}
				});
			}
		} else {		
			File file = new File(value);
			Parser parser = new Parser(file);
		
			parser.start();
		}
	}
	
	public static String getStringUntilCharacter(String string, char ch) {
		String[] split = string.split("");
		
		StringBuilder builder = new StringBuilder();
		
		for(String entry : split) {
			char cha = entry.charAt(0);
			
			builder.append(cha);
			
			if(cha == ch) {
				return builder.toString();
			}
		}
		
		return string;
	}
	
	public static String constructStringFromArray(String[] array) {
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < array.length; i++) {
			builder.append(array[i]);
		}
		
		return builder.toString();
	}
	
	public static String constructStringFromArray(char[] array) {
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < array.length; i++) {
			builder.append(array[i]);
		}
		
		return builder.toString();
	}
	
	public static ArrayList<File> scanThroughPaths(File file) {
		ArrayList<File> list = 
				new ArrayList<>();
		
		for(File f : file.listFiles()) {
			if(f.isDirectory()) {
				ArrayList<File> l = scanThroughPaths(f);
				list.addAll(l);
			} else {
				list.add(f);
			}
		}
		
		return list;
	}
}
