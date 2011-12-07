//Instructions: Download this picture from the Internet onto the AVD:
//http://fc.umit.maine.edu/~Carleton_Ericson/2011-11-22_23-37-43_218.jpg
//Then run program to determine the corners of the sudoku

package edu.umaine.imageproc;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.widget.TextView;


public class ImageProcActivity extends Activity {
	
	private Point corners[]=new Point[4];
	private final int startx=1149, starty=343, endx=2200, endy=1370;
	//these coordinates represent what the user would have cropped around
	//the sudoku
	TextView tv;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		String str="";
		int height, width;
		int i,j;
		int x,y,h,w,xmin,xmax; //coordinates
		int s_h; //sudoku height, top width, and bottom width
		FileOutputStream out;
		tv= (TextView) findViewById(R.id.tv1);
		Bitmap b=BitmapFactory.decodeFile("/mnt/sdcard/download/2011-11-22_23-37-43_218.jpg");
		Bitmap cells[][]=new Bitmap[9][9];
		
		width=b.getWidth();
		height=b.getHeight();
		corners[0]=new Point(0,0);
		corners[1]=new Point(width,0);
		corners[2]=new Point(0,height);
		corners[3]=new Point(width,height);
		FindPuzzleCorners(b);
		//determine location of cells from corners, assumes flat top and bottom
		//but accounts for angled left and right sides
		s_h=corners[2].y-corners[0].y;
		for (i=0;i<9;i++)
		{
			for (j=0;j<9;j++)
			{
				//Add a 10% buffer on each side of each cell
				y=(s_h*j/9+corners[0].y+s_h/90);
				h=(s_h*8/90);
				xmin=(corners[2].x-corners[0].x)*j/9+corners[0].x;
				xmax=(corners[3].x-corners[1].x)*j/9+corners[1].x;
				x=(xmax-xmin)*i/9+xmin+(xmax-xmin)/90;
				w=(xmax-xmin)*8/90;
				cells[i][j]=Bitmap.createBitmap(b,x,y,w,h);
				try {
					out = new FileOutputStream("/mnt/sdcard/download/cell"+j+i+".jpg");
					cells[i][j].compress(Bitmap.CompressFormat.JPEG, 90, out);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					str="Failed file write";
				}
			}
		}
		tv.setText(tv.getText()+"\nDone writing files ... "+str);
	}
	
	private void FindPuzzleCorners(Bitmap b)
	{
		int i,j,total=0,count=0,I=0,Il=0,Ih=0,c,lastI=256;
		//I is intensity, Ih and Il correspond to the intensities of 
		//"black" and "white" in the image
		String str="";
		for (j=startx;j<endx;j+=2) //get "white" value for Ih
		{
			c=b.getPixel(j,starty);
			total+=(Color.red(c)+Color.green(c)+Color.blue(c))/3;
			count++;
		}
		Ih=total/count;
		for (i=starty+2;i<endy;i+=2) //Scan specified area to find top of sudoku
		{
			count=total=0;
			for (j=startx;j<endx;j+=2) //find average intensity of row
			{
				c=b.getPixel(j,i);
				total+=(Color.red(c)+Color.green(c)+Color.blue(c))/3;
				count++;
			}
			I=total/count;
			//If this line is darker than "white" by more than 20%, top is found
			if (((double)(Ih-I)/((double)Ih))>.2)
			{ 
				Il=I;
				corners[0].set(corners[0].x, i);
				corners[1].set(corners[1].x, i);
				break;
			}
		}
		lastI=Il;
		while (lastI>=I) //move top downward until it stops decreasing in intensity
		{
			lastI=I;
			corners[0].set(corners[0].x, i);
			corners[1].set(corners[1].x, i);
			i+=2;
			count=total=0;
			for (j=startx;j<endx;j+=2) //find average intensity of row
			{
				c=b.getPixel(j,i);
				total+=(Color.red(c)+Color.green(c)+Color.blue(c))/3;
				count++;
			}
			I=total/count;
		}
		str+=(",y->"+i);
		for (j=startx;j<endx;j+=2) //find left side of top
		{
			c=b.getPixel(j,i);
			I=(Color.red(c)+Color.green(c)+Color.blue(c))/3;
			if (Math.abs(I-Il)<Math.abs(Ih-I))
			{
				corners[0].set(j,corners[0].y);
				//str+=(",x1="+j);
				break;
			}
		}
		for (j=endx;j>startx;j-=2) //find right side of top
		{
			c=b.getPixel(j,i);
			I=(Color.red(c)+Color.green(c)+Color.blue(c))/3;
			if (Math.abs(I-Il)<Math.abs(Ih-I))
			{
				corners[1].set(j,corners[1].y);
				//str+=(",x2="+j);
				break;
			}
		}
		count=total=0;
		for (j=startx;j<endx;j+=2) //Reset white value for Ih to the bottom of the image
		{
			c=b.getPixel(j,endy);
			total+=(Color.red(c)+Color.green(c)+Color.blue(c))/3;
			count++;
		}
		Ih=total/count;
		for (i=endy-2;i>starty;i-=2) //Scan specified area to find bottom of sudoku
		{
			count=total=0;
			for (j=startx;j<endx;j+=2) //find average intensity of row
			{
				c=b.getPixel(j,i);
				total+=(Color.red(c)+Color.green(c)+Color.blue(c))/3;
				count++;
			}
			I=total/count;
			//If this line is darker than "white" by more than 20%, bottom is found
			if (((double)(Ih-I)/((double)Ih))>.2)
			{ 
				Il=I;
				str+=(",y->"+i);
				corners[2].set(corners[2].x, i);
				corners[3].set(corners[3].x, i);
				break;
			}
		}
		lastI=256;
		while (lastI>=I) //move the bottom up until the intensity no longer decreases
		{
			lastI=I;
			corners[2].set(corners[2].x, i);
			corners[3].set(corners[3].x, i);
			i-=2;
			count=total=0;
			for (j=startx;j<endx;j+=2) //find average intensity of row
			{
				c=b.getPixel(j,corners[2].y);
				total+=(Color.red(c)+Color.green(c)+Color.blue(c))/3;
				count++;
			}
			I=total/count;
			str+=("\ni="+i+",I="+I);
		}
		for (j=startx;j<endx;j+=2) //find right side of bottom
		{
			c=b.getPixel(j,corners[2].y);
			I=(Color.red(c)+Color.green(c)+Color.blue(c))/3;
			if (Math.abs(I-Il)<Math.abs(Ih-I))
			{
				corners[2].set(j,corners[2].y);
				str+=(",x1="+j);
				break;
			}
		}
		for (j=endx;j>startx;j-=2) //find left side of bottom
		{
			c=b.getPixel(j,corners[2].y);
			I=(Color.red(c)+Color.green(c)+Color.blue(c))/3;
			if (Math.abs(I-Il)<Math.abs(Ih-I))
			{
				corners[3].set(j,corners[3].y);
				str+=(",x2="+j);
				break;
			}
		}
		str="Upper left: ("+corners[0].x+","+corners[0].y+")\n";
		str+=("Upper right: ("+corners[1].x+","+corners[1].y+")\n");
		str+=("Lower left: ("+corners[2].x+","+corners[2].y+")\n");
		str+=("Lower right: ("+corners[3].x+","+corners[3].y+")\n");				
		tv.setText(str);
	}

	// /mnt/sdcard/download/[file]
}