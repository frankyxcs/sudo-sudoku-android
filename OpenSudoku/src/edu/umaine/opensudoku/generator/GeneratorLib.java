//GeneratorLib.java
//Kevin Hanselman, Carl Ericson
//ECE 471 Fall 2011 Project
//University of Maine, Orono

package edu.umaine.opensudoku.generator;

public class GeneratorLib {
	
	static {
		System.loadLibrary("sudogen");
	}
	
	public static native int[] generateSudoku(int nGivens);
}
