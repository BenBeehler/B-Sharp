# B-Sharp
An open source, statically typed programming language.<br>

# Distribution
You can build this program from source or download a pre-compiled SDK.<br>
SDK: https://www.dropbox.com/s/88mviu1g7o780xn/B%23.zip?dl=0

# Introduction
This language has 2 primary command usages:<br>
- b# -i "filePath"<br>
- b# -d "filePath"<br><br>
-i is the interpreter command, this is the standard command for execution<br>
-d is the debugger command, this prints out all objects and their respective values<br><br>

# Example
<h3>Basic Hello World</h3>
native::println("Hello World")<br>
<br>
<h3>This program receives input from the standard input stream and prints it in a loop, until it is interrupted</h3>
func iterate() {<br>
	var data string = native::stdin()<br>
	native::println(data)<br>
}<br>
<br>
loop(true) iterate()<br>


