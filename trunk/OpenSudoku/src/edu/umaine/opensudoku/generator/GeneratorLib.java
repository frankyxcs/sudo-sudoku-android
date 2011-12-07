package edu.umaine.opensudoku.generator;

public class GeneratorLib {
	
	static {
		System.loadLibrary("sudogen");
	}
	
	public static native int[] generateSudoku(int nGivens);
}
