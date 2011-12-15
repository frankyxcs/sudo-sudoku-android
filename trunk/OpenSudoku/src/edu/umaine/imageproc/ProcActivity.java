package edu.umaine.imageproc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import cz.romario.opensudoku.R;

/**
 * Activity for choosing a photo or taking a photo, and cropping it.
 * 
 * Original author: Lorensius W. L. T <lorenz@londatiga.net>
 * 
 * Modified for OpenSudoku by Kevin Hanselman <kevin.hanselman@umit.maine.edu>
 */
public class ProcActivity extends Activity {
	private static final CharSequence[] rotateChoices = {"None", "90 degrees CW", "90 degrees CCW", "180 degrees"};
	private static int[] rotates = {0, 90, -90, 180};
	private Uri mImageCaptureUri;
	private ImageView[][] mCellImages;
	protected int mRotation = 0;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	private static final String TAG = "ImageProc";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.imageproc);

		final String [] items			= new String [] {"Take from camera", "Select from gallery"};				
		ArrayAdapter<String> adapter	= new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
		AlertDialog.Builder builder		= new AlertDialog.Builder(this);

		builder.setTitle("Select Image");
		builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialog, int item ) { //pick from camera
				if (item == 0) {
					Intent intent 	 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
							"tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

					try {
						intent.putExtra("return-data", true);

						startActivityForResult(intent, PICK_FROM_CAMERA);
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
					}
				} else { //pick from file
					Intent intent = new Intent();

					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);

					startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
				}
			}
		} );

		final AlertDialog dialog = builder.create();

		Button button 	= (Button) findViewById(R.id.btn_crop);

		mCellImages = new ImageView[3][3];
		mCellImages[0][0] = (ImageView) findViewById(R.id.c00);
		mCellImages[0][1] = (ImageView) findViewById(R.id.c01);
		mCellImages[0][2] = (ImageView) findViewById(R.id.c02);
		mCellImages[1][0] = (ImageView) findViewById(R.id.c10);
		mCellImages[1][1] = (ImageView) findViewById(R.id.c11);
		mCellImages[1][2] = (ImageView) findViewById(R.id.c12);
		mCellImages[2][0] = (ImageView) findViewById(R.id.c20);
		mCellImages[2][1] = (ImageView) findViewById(R.id.c21);
		mCellImages[2][2] = (ImageView) findViewById(R.id.c22);

		button.setOnClickListener(new View.OnClickListener() {	
			public void onClick(View v) {
				dialog.show();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) return;

		switch (requestCode) {
		case PICK_FROM_CAMERA:
			doCrop();

			break;

		case PICK_FROM_FILE: 
			mImageCaptureUri = data.getData();

			doCrop();

			break;	    	

		case CROP_FROM_CAMERA:	    	
			Bundle extras = data.getExtras();

			if (extras != null) {	        	
				Bitmap photo = extras.getParcelable("data");
				Log.e(TAG,"Got bitmap");

				Log.e(TAG,"promptAndRotate()");
				new AlertDialog.Builder(this) 
				.setTitle("Rotate the image:")
				.setItems(rotateChoices, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int idx) {
				    	mRotation = rotates[idx];
				    }
				})
				.create()
				.show();
				
				photo = rotateBitmap(photo, mRotation);

				processBitmap(photo);

				/*
		            FileOutputStream out;
					try {
						String file = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"mytest.jpg";
						Log.e("$$$$$$$$$", "Attempting to write " + file);
						out = new FileOutputStream(file);
				        photo.compress(Bitmap.CompressFormat.JPEG, 90, out);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}*/


			}

			//File f = new File(mImageCaptureUri.getPath()); 
			//if (f.exists()) f.delete();

			break;

		}
	}

	private Bitmap rotateBitmap(Bitmap b, int rotation) {
		Log.e(TAG,"rotateBitmap()");
		// rotates bitmap clockwise in degrees
		if (rotation!=0) {
			//create a rotated version of the bitmap
			Matrix mat = new Matrix();
			mat.postRotate(rotation);
			return Bitmap.createBitmap(b,0,0,b.getWidth(),b.getHeight(), mat, true);
		}
		return b;
	}

	private void doCrop() {
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");

		List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, 0 );

		int size = list.size();

		if (size == 0) {	        
			Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();

			return;
		} else {
			intent.setData(mImageCaptureUri);

			intent.putExtra("outputX", 200);
			intent.putExtra("outputY", 200);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", true);

			if (size == 1) {
				Intent i 		= new Intent(intent);
				ResolveInfo res	= list.get(0);

				i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

				startActivityForResult(i, CROP_FROM_CAMERA);
			} else {
				for (ResolveInfo res : list) {
					final CropOption co = new CropOption();

					co.title 	= getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
					co.icon		= getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
					co.appIntent= new Intent(intent);

					co.appIntent.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

					cropOptions.add(co);
				}

				CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Choose Crop App");
				builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
					public void onClick( DialogInterface dialog, int item ) {
						startActivityForResult( cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
					}
				});

				builder.setOnCancelListener( new DialogInterface.OnCancelListener() {
					public void onCancel( DialogInterface dialog ) {

						if (mImageCaptureUri != null ) {
							getContentResolver().delete(mImageCaptureUri, null, null );
							mImageCaptureUri = null;
						}
					}
				} );

				AlertDialog alert = builder.create();

				alert.show();
			}
		}
	}

	private void processBitmap(Bitmap b) {
		SudokuParseProcess spp = new SudokuParseProcess(b);
		Log.e(TAG,"Create SudokuParseProcess");
		spp.generateCells();
		Log.e(TAG,"generateCells()");
		Bitmap[][] cells = spp.getCells();
		Log.e(TAG,"getCells()");

		for(int r=0; r<3; r++)
			for(int c=0; c<3; c++) {
				mCellImages[r][c].setImageBitmap(cells[r][c]);
				mCellImages[r][c].setVisibility(View.VISIBLE);
			}
		findViewById(R.id.textSudokuSquare).setVisibility(View.VISIBLE);
		Log.e(TAG,"setImageBitap()");
	}

}