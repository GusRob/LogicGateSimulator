#target entry to build all files
all: src/Main.java
	javac src/*.java
	java src/Main.java
