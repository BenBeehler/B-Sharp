package com.benbeehler.bsharp.syntax;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.benbeehler.bsharp.Console;
import com.benbeehler.bsharp.runtime.Runtime;
import com.benbeehler.bsharp.runtime.objects.AccessModifier;
import com.benbeehler.bsharp.runtime.objects.BFunction;
import com.benbeehler.bsharp.runtime.objects.BType;
import com.benbeehler.bsharp.runtime.objects.BVariable;

/*
 * complete syntax parser
 */

public class SyntaxManager {
	
	public static String _PARAMETER_SPLIT_COMMA = "psc_runt_splitv001";
	public static String _OPENPB = "open_pb_runt_splitv001";
	public static String _CLOSEPB = "close_pb_runt_splitv001";
	public static String _STRSPLIT = "str_splitv0001spl";
	public static String _EQUAL = "valcompar_equal0001splstr";
	public static String _NOTEQUAL = "valcompar_NOTequal0001splstr";
	public static String _LISTSPLIT = "__arrayvalidentsplit00v001splstr";
	public static String _LAMBDA = "__lambdaexpresstringparserintsplstrv001";
	public static String _COLON = "____cololoopparsestrfunctionalitystringparserintsplstrv001";
	
	public static String getStringUntilChar(String string, char ch) {
		return string.split(String.valueOf(ch))[0];
	}
	
	public static String[] split(String string, String[] delimeters) {
		if(delimeters.length > 1) {
			for(String delimeter : delimeters) {
				string = string.replace(delimeter, delimeters[0]);
			}
			
			return string.split(delimeters[0]);
		} else {
			return string.split(delimeters[0]);
		}
	}
	
	public static String raw(String string) {
		string = string.replaceAll(SyntaxManager._PARAMETER_SPLIT_COMMA, ",")
				.replaceAll(_OPENPB, "(")
				.replaceAll(_CLOSEPB, ")")
				.replaceAll(_STRSPLIT, "&")
				.replaceAll(_EQUAL, "==")
				.replaceAll(_NOTEQUAL, "!=")
				.replaceAll(_LISTSPLIT, ";")
				.replaceAll(_LAMBDA, "=>")
				.replaceAll(_COLON, ":");
		return string;
	}
	
	public static String[] splitByMiddle(String string, String delimeter) {
		int[] indexes = getIndexes(string, delimeter); 
		int middle = indexes[(indexes.length/2)-1];
		
		String[] result = new String[2];
		
		ArrayList<String> first = new ArrayList<>();
		String[] split = string.split("");
		for(int i = 0; i < middle; i++) {
			first.add(split[i]);
		}
		
		String one = convert(first.toArray(new String[first.size()]));
		String[] twoArray = string.replaceFirst(one, "").split("");
		String two = convert(twoArray);
		two = two.replaceFirst(delimeter, "");
		result[0] = one;
		result[1] = two;
		return result;
	}
	
	public static int[] getIndexes(String word, String split) {
		int index = word.indexOf(split);
		ArrayList<Integer> list = new ArrayList<>();
		list.add(index);
		while(index >= 0) {
		   index = word.indexOf(split, index+1);
		   list.add(index);
		}
		
		Object[] oarray = list.toArray(new Object[list.size()]);
		int[] result = new int[oarray.length];
		for(int i = 0; i < oarray.length; i++) {
			result[i] = Integer.parseInt(oarray[i].toString());
		}
		
		return result;
	}
	
	public static String getStringUntilString(String string, String str) {
		return string.split(str)[0];
	}
	
	public static String convert(String[] str) {
		StringBuilder sb = new StringBuilder();
		
		for(String s : str) {
			sb.append(s);
		}
		
		return sb.toString();
	}
	
	public static String convert(String[] str, String del) {
		StringBuilder sb = new StringBuilder();
		
		for(String s : str) {
			sb.append(s + del);
		}
		
		return sb.toString();
	}
	
	public static String reverse(String string) {
		StringBuilder sb = new StringBuilder(string);
		sb = sb.reverse();
		return sb.toString();
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
	
	public static String replaceFirstStrict(String inputString, String stringToReplace,
	        String stringToReplaceWith) {
		
	    int length = stringToReplace.length();
	    int inputLength = inputString.length();

	    int startingIndexofTheStringToReplace = inputString.indexOf(stringToReplace);

	    String finalString = inputString.substring(0, startingIndexofTheStringToReplace) + stringToReplaceWith
	            + inputString.substring(startingIndexofTheStringToReplace + length, inputLength);

	    return finalString;

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
	
	
	public static Object solveArithmeticFromString(String ar) throws ScriptException {
        /*
         * This code is garbage
         */
		ar = Runtime.replaceVariableNamesWithValues(ar);
		
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        return engine.eval(ar.replaceAll(" ", ""));
    }
	
	public static void parseVariable(String line, Parser parser) {
		//var variable string = ""
		BVariable variable;
		variable = new BVariable(null, null, null, null);
		variable.setFunction(parser.getCurrentFunction());
		
		line = line.replaceFirst("var", "")
				.trim();
		
		String[] split = 
				line.split(" ");
		
		if(split[0].equalsIgnoreCase("mutable")) {
			variable.setMutable(true);
			split[0] = "";
		}
		
		line = convert(split, " ").trim();
		split = line.split(" ");
		
		if(split.length >= 3) {
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
				if(Runtime.containsTypeByName(line)) {
					BType t = Runtime.getType(line);
					Object v = Runtime.getValue(line, parser);
					
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
					
					Runtime.variables.add(variable);
				} else {
					Object v = Runtime.getValue(line, parser);
					BType t = Runtime.getType(v.toString());
					
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
					
					Runtime.variables.add(variable);
				}
			} else {
				if (Runtime.containsTypeByName(type)) {
					BType typ = 
							Runtime.getTypeFromName(type);
				
					variable.setType(typ);
				
					line = line.replaceFirst(type, "")
							.trim();
				
					split = line.split(" ");
				
					if(split[0].equals("=")) {
						line = line.replaceFirst("=", "")
								.trim();
					
						split = line.split(" ");
					
						//Get all primitive types
						
						if(typ.equals(Runtime.getTypeFromName("byte"))) {
							
						} else if(typ == Runtime.getTypeFromName("object")) {
							Object value = Runtime.getValue(line, parser);
						
							variable.setValue(value);
							variable.setFile(parser.getFile());
							variable.setType(Runtime.getTypeFromName("string"));
							variable.setFunction(parser.getCurrentFunction());
							
							if(variable.getName().startsWith("!")) {
								variable.setAccessModifier(AccessModifier.UNIVERSAL);
							} else if(variable.getFunction() != null) {
								variable.setAccessModifier(AccessModifier.GLOBAL);
							} else {
								variable.setAccessModifier(AccessModifier.RESTRICTED);
							}
							
							Runtime.addVariable(variable);
						} else if(typ == Runtime.getTypeFromName("string")) {
							String value = Runtime.getValue(line, parser).toString();
							
							variable.setValue(value);
							variable.setFile(parser.getFile());
							variable.setType(Runtime.getTypeFromName("string"));
							variable.setFunction(parser.getCurrentFunction());
							
							if(variable.getName().startsWith("!")) {
								variable.setAccessModifier(AccessModifier.UNIVERSAL);
							} else if(variable.getFunction() != null) {
								variable.setAccessModifier(AccessModifier.GLOBAL);
							} else {
								variable.setAccessModifier(AccessModifier.RESTRICTED);
							}
							
							Runtime.addVariable(variable);
						} else if(typ == Runtime.getTypeFromName("integer")) {
							String value = Runtime.getValue(line, parser).toString();
							
							if(Runtime.isInteger(value)) {
								variable.setValue(Runtime.getInteger(value));
								variable.setFile(parser.getFile());
								variable.setType(Runtime.getTypeFromName("integer"));
								variable.setFunction(parser.getCurrentFunction());
							
								if(variable.getName().startsWith("!")) {
									variable.setAccessModifier(AccessModifier.UNIVERSAL);
								} else if(variable.getFunction() != null) {
									variable.setAccessModifier(AccessModifier.GLOBAL);
								} else {
									variable.setAccessModifier(AccessModifier.RESTRICTED);
								}
							
								Runtime.addVariable(variable);
							} else if(value.toString().equals("null")) {
								variable.setValue("null");
								variable.setFile(parser.getFile());
								variable.setType(Runtime.getTypeFromName("integer"));
								variable.setFunction(parser.getCurrentFunction());
							
								if(variable.getName().startsWith("!")) {
									variable.setAccessModifier(AccessModifier.UNIVERSAL);
								} else if(variable.getFunction() != null) {
									variable.setAccessModifier(AccessModifier.GLOBAL);
								} else {
									variable.setAccessModifier(AccessModifier.RESTRICTED);
								}
							
								Runtime.addVariable(variable);
							} else {
								Console.E("Internal Error: Invalid value for given type...");
							}
						} else if(typ == Runtime.getTypeFromName("double")) {
							String value = Runtime.getValue(line, parser).toString();
						
							if(Runtime.isDouble(value) || Runtime.isInteger(value)) {
								variable.setValue(Runtime.getDouble(value));
								variable.setFile(parser.getFile());
								variable.setType(Runtime.getTypeFromName("double"));
								variable.setFunction(parser.getCurrentFunction());
							
								if(variable.getName().startsWith("!")) {
									variable.setAccessModifier(AccessModifier.UNIVERSAL);
								} else if(variable.getFunction() != null) {
									variable.setAccessModifier(AccessModifier.GLOBAL);
								} else {
									variable.setAccessModifier(AccessModifier.RESTRICTED);
								}
							
								Runtime.addVariable(variable);
							} else if(value.toString().equals("null")) {
								variable.setValue("null");
								variable.setFile(parser.getFile());
								variable.setType(Runtime.getTypeFromName("double"));
								variable.setFunction(parser.getCurrentFunction());
							
								if(variable.getName().startsWith("!")) {
									variable.setAccessModifier(AccessModifier.UNIVERSAL);
								} else if(variable.getFunction() != null) {
									variable.setAccessModifier(AccessModifier.GLOBAL);
								} else {
									variable.setAccessModifier(AccessModifier.RESTRICTED);
								}
							
								Runtime.addVariable(variable);
							} else {
								Console.E("Internal Error: Invalid value for given type...");
							}
						
						} else if(typ == Runtime.getTypeFromName("boolean")) {
							Object value = Runtime.getValue(line, parser);
						
							if(Runtime.isBoolean(value.toString())) {
								variable.setValue(Runtime.getBoolean(value.toString()));
								variable.setFile(parser.getFile());
								variable.setType(Runtime.getTypeFromName("boolean"));
								variable.setFunction(parser.getCurrentFunction());
							
								if(variable.getName().startsWith("!")) {
									variable.setAccessModifier(AccessModifier.UNIVERSAL);
								} else if(variable.getFunction() != null) {
									variable.setAccessModifier(AccessModifier.GLOBAL);
								} else {
									variable.setAccessModifier(AccessModifier.RESTRICTED);
								}
							
								Runtime.addVariable(variable);
							} else {
								Console.E("Internal Error: Invalid value for given type...");
							}
						} else if(typ == Runtime.getTypeFromName("list")) {
							Object value = Runtime.getValue(line, parser);
							
							if(Runtime.isArray(value.toString())) {
								variable.setValue(Runtime.getList(value.toString()));
								variable.setFile(parser.getFile());
								variable.setType(Runtime.getTypeFromName("list"));
								variable.setFunction(parser.getCurrentFunction());
							
								if(variable.getName().startsWith("!")) {
									variable.setAccessModifier(AccessModifier.UNIVERSAL);
								} else if(variable.getFunction() != null) {
									variable.setAccessModifier(AccessModifier.GLOBAL);
								} else {
									variable.setAccessModifier(AccessModifier.RESTRICTED);
								}
							
								Runtime.addVariable(variable);
							} else if(value.toString().equals("null")) {
								variable.setValue("null");
								variable.setFile(parser.getFile());
								variable.setType(Runtime.getTypeFromName("list"));
								variable.setFunction(parser.getCurrentFunction());
							
								if(variable.getName().startsWith("!")) {
									variable.setAccessModifier(AccessModifier.UNIVERSAL);
								} else if(variable.getFunction() != null) {
									variable.setAccessModifier(AccessModifier.GLOBAL);
								} else {
									variable.setAccessModifier(AccessModifier.RESTRICTED);
								}
							
								Runtime.addVariable(variable);
							} else {
								Console.E("Internal Error: Invalid value for given type...");
							}
						} else {
							//CHECK VARIABLE'S VALUE TO DETERMINE TYPE
							Object v = Runtime.getValue(line, parser);
							BType t = Runtime.getType(v.toString());
							
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
							
							Runtime.variables.add(variable);
						}
					} else {
						Console.E("Malformed Syntax: Variable declaration is malformed...");
					}
				} else {
					Console.E("Malformed Syntax: Type never found in variable declaration...");
				}
			}
		} else {
			Console.E("Variable declaration is malformed.. (" + raw(line) + ")");
		}
	}
	
	public static void remapMutable(String line, Parser parser) {
		//var variable string = ""
		String[] split = line.split(" ");
		String name = split[0];
		BVariable variable = Runtime.getVariableFromName(name);
		
		if(!variable.isMutable()) {
			Console.E(name + " is not mutable!");
		}
		
		if(split.length >= 3) {
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
				if(Runtime.containsTypeByName(line)) {
					BType t = Runtime.getType(line);
					Object v = Runtime.getValue(line, parser);
				
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
					
					Runtime.variables.add(variable);
				} else {
					Object v = Runtime.getValue(line, parser);
					BType t = Runtime.getType(v.toString());
					
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
					
					Runtime.variables.add(variable);
				}
			} else {
				if (Runtime.containsTypeByName(type)) {
					BType typ = 
							Runtime.getTypeFromName(type);
				
					variable.setType(typ);
				
					line = line.replaceFirst(type, "")
							.trim();
				
					split = line.split(" ");
				
					if(split[0].equals("=")) {
						line = line.replaceFirst("=", "")
								.trim();
					
						split = line.split(" ");
					
						//Get all primitive types
						
						if(typ.equals(Runtime.getTypeFromName("byte"))) {
							
						} else if(typ == Runtime.getTypeFromName("object")) {
							Object value = Runtime.getValue(line, parser);
						
							variable.setValue(value);
							variable.setFile(parser.getFile());
							variable.setType(Runtime.getTypeFromName("string"));
							variable.setFunction(parser.getCurrentFunction());
							
							if(variable.getName().startsWith("!")) {
								variable.setAccessModifier(AccessModifier.UNIVERSAL);
							} else if(variable.getFunction() != null) {
								variable.setAccessModifier(AccessModifier.GLOBAL);
							} else {
								variable.setAccessModifier(AccessModifier.RESTRICTED);
							}
							
							Runtime.addVariable(variable);
						} else if(typ == Runtime.getTypeFromName("string")) {
							String value = Runtime.getValue(line, parser).toString();
							
							variable.setValue(value);
							variable.setFile(parser.getFile());
							variable.setType(Runtime.getTypeFromName("string"));
							variable.setFunction(parser.getCurrentFunction());
							
							if(variable.getName().startsWith("!")) {
								variable.setAccessModifier(AccessModifier.UNIVERSAL);
							} else if(variable.getFunction() != null) {
								variable.setAccessModifier(AccessModifier.GLOBAL);
							} else {
								variable.setAccessModifier(AccessModifier.RESTRICTED);
							}
							
							Runtime.addVariable(variable);
						} else if(typ == Runtime.getTypeFromName("integer")) {
							String value = Runtime.getValue(line, parser).toString();
							
							if(Runtime.isInteger(value)) {
								variable.setValue(Runtime.getInteger(value));
								variable.setFile(parser.getFile());
								variable.setType(Runtime.getTypeFromName("integer"));
								variable.setFunction(parser.getCurrentFunction());
							
								if(variable.getName().startsWith("!")) {
									variable.setAccessModifier(AccessModifier.UNIVERSAL);
								} else if(variable.getFunction() != null) {
									variable.setAccessModifier(AccessModifier.GLOBAL);
								} else {
									variable.setAccessModifier(AccessModifier.RESTRICTED);
								}
							
								Runtime.addVariable(variable);
							} else if(value.toString().equals("null")) {
								variable.setValue("null");
								variable.setFile(parser.getFile());
								variable.setType(Runtime.getTypeFromName("integer"));
								variable.setFunction(parser.getCurrentFunction());
							
								if(variable.getName().startsWith("!")) {
									variable.setAccessModifier(AccessModifier.UNIVERSAL);
								} else if(variable.getFunction() != null) {
									variable.setAccessModifier(AccessModifier.GLOBAL);
								} else {
									variable.setAccessModifier(AccessModifier.RESTRICTED);
								}
							
								Runtime.addVariable(variable);
							} else {
								Console.E("Internal Error: Invalid value for given type...");
							}
						} else if(typ == Runtime.getTypeFromName("double")) {
							String value = Runtime.getValue(line, parser).toString();
						
							if(Runtime.isDouble(value) || Runtime.isInteger(value)) {
								variable.setValue(Runtime.getDouble(value));
								variable.setFile(parser.getFile());
								variable.setType(Runtime.getTypeFromName("double"));
								variable.setFunction(parser.getCurrentFunction());
							
								if(variable.getName().startsWith("!")) {
									variable.setAccessModifier(AccessModifier.UNIVERSAL);
								} else if(variable.getFunction() != null) {
									variable.setAccessModifier(AccessModifier.GLOBAL);
								} else {
									variable.setAccessModifier(AccessModifier.RESTRICTED);
								}
							
								Runtime.addVariable(variable);
							} else if(value.toString().equals("null")) {
								variable.setValue("null");
								variable.setFile(parser.getFile());
								variable.setType(Runtime.getTypeFromName("double"));
								variable.setFunction(parser.getCurrentFunction());
							
								if(variable.getName().startsWith("!")) {
									variable.setAccessModifier(AccessModifier.UNIVERSAL);
								} else if(variable.getFunction() != null) {
									variable.setAccessModifier(AccessModifier.GLOBAL);
								} else {
									variable.setAccessModifier(AccessModifier.RESTRICTED);
								}
							
								Runtime.addVariable(variable);
							} else {
								Console.E("Internal Error: Invalid value for given type...");
							}
						
						} else if(typ == Runtime.getTypeFromName("boolean")) {
							Object value = Runtime.getValue(line, parser);
						
							if(Runtime.isBoolean(value.toString())) {
								variable.setValue(Runtime.getBoolean(value.toString()));
								variable.setFile(parser.getFile());
								variable.setType(Runtime.getTypeFromName("boolean"));
								variable.setFunction(parser.getCurrentFunction());
							
								if(variable.getName().startsWith("!")) {
									variable.setAccessModifier(AccessModifier.UNIVERSAL);
								} else if(variable.getFunction() != null) {
									variable.setAccessModifier(AccessModifier.GLOBAL);
								} else {
									variable.setAccessModifier(AccessModifier.RESTRICTED);
								}
							
								Runtime.addVariable(variable);
							} else {
								Console.E("Internal Error: Invalid value for given type...");
							}
						} else if(typ == Runtime.getTypeFromName("list")) {
							Object value = Runtime.getValue(line, parser);
							
							if(Runtime.isArray(value.toString())) {
								variable.setValue(Runtime.getList(value.toString()));
								variable.setFile(parser.getFile());
								variable.setType(Runtime.getTypeFromName("list"));
								variable.setFunction(parser.getCurrentFunction());
							
								if(variable.getName().startsWith("!")) {
									variable.setAccessModifier(AccessModifier.UNIVERSAL);
								} else if(variable.getFunction() != null) {
									variable.setAccessModifier(AccessModifier.GLOBAL);
								} else {
									variable.setAccessModifier(AccessModifier.RESTRICTED);
								}
							
								Runtime.addVariable(variable);
							} else if(value.toString().equals("null")) {
								variable.setValue("null");
								variable.setFile(parser.getFile());
								variable.setType(Runtime.getTypeFromName("list"));
								variable.setFunction(parser.getCurrentFunction());
							
								if(variable.getName().startsWith("!")) {
									variable.setAccessModifier(AccessModifier.UNIVERSAL);
								} else if(variable.getFunction() != null) {
									variable.setAccessModifier(AccessModifier.GLOBAL);
								} else {
									variable.setAccessModifier(AccessModifier.RESTRICTED);
								}
							
								Runtime.addVariable(variable);
							} else {
								Console.E("Internal Error: Invalid value for given type...");
							}
						} else {
							//CHECK VARIABLE'S VALUE TO DETERMINE TYPE
							Object v = Runtime.getValue(line, parser);
							BType t = Runtime.getType(v.toString());
							
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
							
							Runtime.variables.add(variable);
						}
					} else {
						Console.E("Malformed Syntax: Variable declaration is malformed...");
					}
				} else {
					Console.E("Malformed Syntax: Type never found in variable declaration...");
				}
			}
		} else {
			Console.E("Variable declaration is malformed.. (" + raw(line) + ")");
		}
	}
	
	public static void parseFunction(String line, Parser parser) {
		//function main() {
		BFunction function = 
				new BFunction(null, null, null, null);
		
		Parser p = new Parser(parser.getFile());
		function.setParser(p);
		
		line = line.replaceFirst("func", "")
				.trim();
		
		function.setFile(parser.getFile());
		function.setAccessModifier(AccessModifier.GLOBAL);
		String name = SyntaxManager.getStringUntilString(line, 
				SyntaxManager._OPENPB).trim();
		function.setName(name);
		
		line = replaceFirstStrict(line, name, " ").trim();
		String pbrackets = (SyntaxManager.getStringUntilString(line, SyntaxManager._CLOSEPB)
				+ SyntaxManager._CLOSEPB).trim();
		line = line.replace(pbrackets, "").trim();
		
		//System
		
		if(pbrackets.startsWith(SyntaxManager._OPENPB) &&
				pbrackets.endsWith(SyntaxManager._CLOSEPB)) {
			pbrackets = pbrackets.replaceFirst(SyntaxManager._OPENPB, "").trim();
			pbrackets = pbrackets.replaceAll(SyntaxManager._CLOSEPB, "").trim();
			
			String[] comma = pbrackets.split(SyntaxManager
					._PARAMETER_SPLIT_COMMA);
			if(comma.length != 0) {
				for(String parameter : comma) {
					parameter = parameter.trim();
					String[] pDetails = parameter.split(" ");
					if(pDetails.length >= 2) {
						String vName = pDetails[0];
						String vType = pDetails[1];
						
						BType type = Runtime.getTypeFromName(vType);
						
						BVariable variable = new BVariable(vName,
								Runtime.getVariableFromName("nil").getValue(),
								parser.getFile(), 
								AccessModifier.RESTRICTED);
						
						variable.setFunction(function);
						variable.setType(type);
						
						//Runtime.addVariable(variable);
				
						ArrayList<BVariable> parameters = function.getParameters();
						parameters.add(variable);
						function.setParameters(parameters);
					}
				}
			} else {
				ArrayList<BVariable> parameters = new ArrayList<>();
				function.setParameters(parameters);
			}
		}
		
		if(line.trim().equals("{")) {
			parser.setCurrentFunction(function);
			
			function.setNative(false);
			Runtime.addFunction(function);
		} else {
			
		}
	}
	
	public static void checkFunction(String line, Parser parser) {
		//function main() {
		BFunction function = 
				new BFunction(null, null, null, null);
		
		Parser p = new Parser(parser.getFile());
		function.setParser(p);
		
		line = line.replaceFirst("func", "")
				.trim();
		
		function.setFile(parser.getFile());
		function.setAccessModifier(AccessModifier.GLOBAL);
		String name = SyntaxManager.getStringUntilString(line, 
				SyntaxManager._OPENPB).trim();
		function.setName(name);
		
		line = replaceFirstStrict(line, name, " ").trim();
		String pbrackets = (SyntaxManager.getStringUntilString(line, SyntaxManager._CLOSEPB)
				+ SyntaxManager._CLOSEPB).trim();
		line = line.replace(pbrackets, "").trim();
		
		//System
		
		if(pbrackets.startsWith(SyntaxManager._OPENPB) &&
				pbrackets.endsWith(SyntaxManager._CLOSEPB)) {
			pbrackets = pbrackets.replaceFirst(SyntaxManager._OPENPB, "").trim();
			pbrackets = pbrackets.replaceAll(SyntaxManager._CLOSEPB, "").trim();
			
			String[] comma = pbrackets.split(SyntaxManager
					._PARAMETER_SPLIT_COMMA);
			if(comma.length != 0) {
				for(String parameter : comma) {
					parameter = parameter.trim();
					String[] pDetails = parameter.split(" ");
					if(pDetails.length >= 2) {
						String vName = pDetails[0];
						String vType = pDetails[1];
						
						BType type = Runtime.getTypeFromName(vType);
						
						BVariable variable = new BVariable(vName,
								Runtime.getVariableFromName("nil").getValue(),
								parser.getFile(), 
								AccessModifier.RESTRICTED);
						
						variable.setFunction(function);
						variable.setType(type);
						
						//Runtime.addVariable(variable);
				
						ArrayList<BVariable> parameters = function.getParameters();
						parameters.add(variable);
						function.setParameters(parameters);
					}
				}
			} else {
				ArrayList<BVariable> parameters = new ArrayList<>();
				function.setParameters(parameters);
			}
		}
		
		if(line.trim().equals("{")) {
			parser.setCurrentFunction(function);
			
			function.setNative(false);
			//Runtime.addFunction(function);
		} else {
			
		}
	}
	
	public static void callFunction(String line, Parser parser) {
		line = line.trim();
		
		String name = SyntaxManager.getStringUntilString(line, SyntaxManager._OPENPB)
				.trim();
		
		if(!Runtime.containsFunctionByName(name)) Console.E("specified function does not exist (" + name + ")");
		BFunction function = Runtime.getFunctionFromName(name);
		
		line = line.replaceFirst(name, "")
				.trim();
		
		if(line.startsWith(SyntaxManager._OPENPB)) {
			if(line.endsWith(SyntaxManager._CLOSEPB)) {
				line = line.replaceFirst(SyntaxManager._OPENPB, "");
				line = reverse(line);
				line = line.replaceFirst(reverse(SyntaxManager._CLOSEPB), "");
				line = reverse(line);
				
				line = line.trim();
				
				String[] spl =
						line.split(SyntaxManager._PARAMETER_SPLIT_COMMA);
				
				ArrayList<Object> values = new ArrayList<Object>();
				
				if(spl.length != 0) {
					for(String entry : spl) {
						entry = entry.trim();
						
						values.add(Runtime.getValue(entry, parser));
					}
				}
				
				if(function.getParameters() != null) {
					if(function.getParameters().size() != 0) {
						if(values.size() == function.getParameters().size()) {
							for(int i = 0; i < function.getParameters().size(); i++) {
								BVariable var = function.getParameters().get(i);
						
								if(Runtime.containsVariableByName(values.get(i).toString())) {
									var.inherit(Runtime.getVariableFromName(values.get(i).toString()), parser);
								}
								
								var.setValue(values.get(i));
							}
						
							function.execute(); //All parameters added... call function
						} else {
							Console.E("Malformed Syntax: Not enough given parameters for function...");
						}
					} else {
						function.execute();
					}
				} else {
					Console.E("severe error loading function parameters into memory, internal error");
				}
			} else {
				Console.E("Malformed Syntax: Parameter list is malformed... (" + raw(line) + ")");
			}
		} else {
			Console.E("Malformed Syntax: Parameter list is malformed... (" + raw(line) + ")");
		}
	}
	
	public static void callFunction(String line) {
		line = line.trim();
		
		String name = SyntaxManager.getStringUntilString(line, SyntaxManager._OPENPB)
				.trim();
		
		if(!Runtime.containsFunctionByName(name)) Console.E("specified function does not exist (" + name + ")");
		BFunction function = Runtime.getFunctionFromName(name);
		
		line = line.replaceFirst(name, "")
				.trim();
		
		if(line.startsWith(SyntaxManager._OPENPB)) {
			if(line.endsWith(SyntaxManager._CLOSEPB)) {
				line = line.replaceFirst(SyntaxManager._OPENPB, "");
				line = reverse(line);
				line = line.replaceFirst(reverse(SyntaxManager._CLOSEPB), "");
				line = reverse(line);
				
				line = line.trim();
				
				String[] spl =
						line.split(SyntaxManager._PARAMETER_SPLIT_COMMA);
				
				ArrayList<Object> values = new ArrayList<Object>();
				
				if(spl.length != 0) {
					for(String entry : spl) {
						entry = entry.trim();
						
						values.add(Runtime.getValue(entry));
					}
				}
				
				if(function.getParameters() != null) {
					if(function.getParameters().size() != 0) {
						if(values.size() == function.getParameters().size()) {
							for(int i = 0; i < function.getParameters().size(); i++) {
								BVariable var = function.getParameters().get(i);
								
								var.setValue(values.get(i));
							}
						
							function.execute(); //All parameters added... call function
						} else {
							Console.E("Malformed Syntax: Not enough given parameters for function...");
						}
					} else {
						function.execute();
					}
				} else {
					Console.E("severe error loading function parameters into memory, internal error");
				}
			} else {
				Console.E("Malformed Syntax: Parameter list is malformed... (" + raw(line) + ")");
			}
		} else {
			Console.E("Malformed Syntax: Parameter list is malformed... (" + raw(line) + ")");
		}
	}
	
	public static void parseIfStatement(String line, Parser parser) {
		line = line.replaceFirst("if", "")
				.trim();
			
			if(!line.contains(SyntaxManager._LAMBDA)) {
				Console.E("malformed syntax: if statement requires lambda (=>)");
			}
					
			String[] split = line.split(SyntaxManager._LAMBDA);
			String val = split[0];
			line = split[1];
					
			val = val.replaceFirst(SyntaxManager._OPENPB, "");
			val = reverse(val);
			val = val.replaceFirst(reverse(SyntaxManager._CLOSEPB), "");
			val = reverse(val);
			boolean bool = Runtime.getBoolean(val);
						
			line = line.replace(val, "")
					.trim();
				
			if(bool) {
				for(String str : line.split(_COLON)) {
					Parser.evaluate(str);
				}
					
				bool = Runtime.getBoolean(val);
			}
	}
	
	public static void parseListLoop(String line, Parser parser) {
		if(line.startsWith("(")) {
			
		} else {
			
		}
	}
	
	public static void parseLoopStatement(String line, Parser parser) {
		line = line.replaceFirst("loop", "")
			.trim();
		
		if(!line.contains(SyntaxManager._LAMBDA)) {
			Console.E("malformed syntax: loop statement requires lambda (=>)");
		}
				
		String[] split = line.split(SyntaxManager._LAMBDA);
		String val = split[0];
		line = split[1];
				
		val = val.replaceFirst(SyntaxManager._OPENPB, "");
		val = reverse(val);
		val = val.replaceFirst(reverse(SyntaxManager._CLOSEPB), "");
		val = reverse(val);
				
		Object value = Runtime.getValue(val, parser);
			
		if(Runtime.isInteger(value.toString())) {
			int v = Runtime.getInteger(value.toString());
					
			line = line.replace(val, "")
						.trim();
					
			for(int i = 0; i < v; i++) {
				for(String str : line.split(_COLON)) {
					Parser.evaluate(str);
				}
			}
		} else {
			boolean bool = Runtime.getBoolean(val);
					
			line = line.replace(val, "")
					.trim();
			
			while(bool) {
				for(String str : line.split(_COLON)) {
					Parser.evaluate(str);
				}
				
				bool = Runtime.getBoolean(val);
			}
		}
	}
	
	public static void parseCategory(String line, Parser parser) {
		
	}
	
	public static void importCategory(String string, Parser parser) {
		string = string.replaceFirst("cimport", "");
		
		String category = string.trim();
		if(Runtime.categories.contains(category)) {
			for(BFunction function : Runtime.functions) {
				if(function.getCategory().equals(category)) {
					function.setName(function.getName().replaceFirst(category + "::", "").trim());
				}
 			}
		} else {
			Console.E("failed to import category: does not exist");
		}
	}
	
	public static void importNativeFunction(String string, Parser parser) { 
		string = string.replaceFirst("nimport", "");
		
		String function = string.trim();
		Runtime.importNative(function);
	}
	
	public static void parseReturn(String string, Parser parser) {
		string = string.replaceFirst("return", "").trim();
		Object value = Runtime.getValue(string, parser);
		
		parser.getCurrentFunction().setReturnValue(value);
	}
	
	public static void parseType(String line, Parser parser) {
		line = line.replaceFirst("type", "").trim();
		String name = line.replaceAll(Pattern.quote("{"), "").trim();
		BType type = new BType(name, parser.getFile(), AccessModifier.UNIVERSAL);
		Runtime.addType(type);
	}
	
	public static void parseThread(String line, Parser parser) {
		final String newline = line.replaceFirst("thread::", "").trim();
		
		new Thread(() -> {
			SyntaxManager.callFunction(newline, parser);
		}).start();
	}
	
	public static void parseTypeVar(String line, Parser parser) {
		String tstring = SyntaxManager
				.getStringUntilString(line, "=>");
		String tName = tstring.trim();
		if(!Runtime.containsTypeByName(tName)) {
			Console.E("specified type being binded does not exist");
		}
		
		BType type = Runtime.getType(tName);
		
		line = line.replaceFirst(tName, "").replaceFirst("=>", "")
				.trim();
		String[] split = line.split(" ");
		if(!(split.length == 2)) {
			Console.E("specified expression binding type has invalid variable and type counts");
		} 
		
		String vName = split[0];
		String vTName = split[1];
		
		if(!Runtime.containsTypeByName(tName)) {
			Console.E("specified variable type in binding does exist");
		}
		
		BType vType = Runtime.getTypeFromName(vTName);
		BVariable var = new BVariable(vName, Runtime.getVariableFromName("nil"),
				parser.getFile(), AccessModifier.RESTRICTED);
		var.setType(vType);
		for(BVariable v : type.getVariables()) {
			var.inherit(v, parser);
		}
		
		type.addVariable(var);
	}
	
	public static void parseImport(String line) {
		line = line.trim();
		
		line = line.replace("import", "");
		line = line.trim();
		
		String value = Runtime.getStringValue(line)
				.trim();
		
		if(value.equals("windows*")) {
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
