package edu.umaine.opensudoku.generator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import cz.romario.opensudoku.R;
import cz.romario.opensudoku.gui.SudokuImportActivity;

public class Generator {

	private static final int[] givensByDiff = {40, 32, 28};

	public static void generateAndImport(int itemIndex, Context context) {
		
		String difficulty = null;
		int nGivens = 0;
		int[] arr = new int[81];
		
		String[] diffs = context.getResources().getStringArray(R.array.game_levels);
		difficulty = diffs[itemIndex];
		nGivens = givensByDiff[itemIndex];
		/*switch(itemIndex) {
		case 0:
			nGivens = 40;
			break;
		case 1:
			nGivens = 32;
			break;
		case 2:
			nGivens = 28;
			break;			
		}*/

		if( nGivens < 0 ) {
			Log.e("Generator", "Could not parse difficulty index from dialog");
			return;
		}

		arr = GeneratorLib.generateSudoku(nGivens);
		//Toast.makeText(context, "first element is: " + arr[0], 1).show();
		Toast.makeText(context, "Number of Givens: " + nGivens, 1).show();

		// Open the ImportSudokuActivity with Extras from out Generator
		String strGame = gameFromArr(arr);
		Intent i = new Intent(context, SudokuImportActivity.class);
		i.putExtra("FOLDER_NAME","Generated - " + difficulty);
		i.putExtra("GAMES",strGame);
		i.putExtra("APPEND_TO_FOLDER", true);
		//i.setData(null); // Uri should be null
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // required for running an Activity from a non-Activity context (dialog)
		context.startActivity(i);
	}
	
	private static String gameFromArr(int[] arr) {
		String str = "";
		str = Arrays.toString(arr);
		str = str.replace(", ", ""); // take out comma separation
		str = str.replaceAll("[\\[\\]]","") + "\n";
		return str;
	}

}
