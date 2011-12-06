/*
 * Kevin Hanselman and Carl Ericson
 */
package cz.romario.opensudoku.gui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import cz.romario.opensudoku.R;

public class SudokuGeneratorActivity extends Activity implements OnClickListener {

	private int[][] board;
	private Button btnGen;
	private Spinner spinDiff;
	private ProgressBar progBarGen;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.generator);
		spinDiff = (Spinner) findViewById(R.id.spinDiff);
		btnGen = (Button) findViewById(R.id.btnGen);
		progBarGen = (ProgressBar) findViewById(R.id.progBarGen);
	}

	@Override
	public void onClick(View v) {
		if(v == this.btnGen) {
			String item = spinDiff.getSelectedItem().toString();
			int nGivens = 0;
			if(item.equalsIgnoreCase("Easy")) {
				nGivens = 40;
			} else if (item.equalsIgnoreCase("Medium")) {
				nGivens = 32;
			} else if (item.equalsIgnoreCase("Hard")) {
				nGivens = 28;
			}
			else {
				// TODO: print an error or something
				return;
			}
			if(nGivens > 0) {
				board = generateSudoku(nGivens);
			}
		}
	}
		
	static {
		//System.loadLibrary("name-of-the-c-source-file");
	}
	
	public native int[][] generateSudoku(int nGivens);

}


