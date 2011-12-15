package edu.umaine.imageproc;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;


public class SudokuParseProcess {
	
	private Point corners[]=new Point[4];
	private static final int TOP_LEFT=0;
	private static final int TOP_RIGHT=1;
	private static final int BOTTOM_LEFT=2;
	private static final int BOTTOM_RIGHT=3;
	
	private Bitmap b;
	private int height, width;
	private Bitmap cells[][]=new Bitmap[9][9];
	
	public SudokuParseProcess(Bitmap m_b)
	{	
		b=m_b;
		width=b.getWidth();
		height=b.getHeight();
		corners[TOP_LEFT]=new Point(0,0);
		corners[TOP_RIGHT]=new Point(width,0);
		corners[BOTTOM_LEFT]=new Point(0,height);
		corners[BOTTOM_RIGHT]=new Point(width,height);
		//FindPuzzleCorners();
		//determine location of cells from corners, assumes flat top and bottom
		//but accounts for angled left and right sides
		//generateCells();
	}
	
	public Bitmap getCell(int row, int col)
	{
		return cells[row][col];
	}
	
	public Bitmap[][] getCells()
	{
		return cells;
	}
	
	public void generateCells()
	{
		findPuzzleCorners();
		int i,j;
		int x,y,h,w; //coordinates
		int xmin,xmax; //used for calculating cell locations
		int s_h; //sudoku height
		FileOutputStream out;
		s_h=corners[BOTTOM_LEFT].y-corners[TOP_LEFT].y;
		for (i=0;i<9;i++)
		{
			for (j=0;j<9;j++)
			{
				//Add a 10% buffer on each side of each cell
				y=(s_h*j/9+corners[TOP_LEFT].y+s_h/90);
				h=(s_h*8/90);
				xmin=(corners[BOTTOM_LEFT].x-corners[TOP_LEFT].x)*j/9+corners[TOP_LEFT].x;
				xmax=(corners[BOTTOM_RIGHT].x-corners[TOP_RIGHT].x)*j/9+corners[TOP_RIGHT].x;
				x=(xmax-xmin)*i/9+xmin+(xmax-xmin)/90;
				w=(xmax-xmin)*8/90;
				cells[j][i]=Bitmap.createBitmap(b,x,y,w,h);
				/*
				try {
					out = new FileOutputStream("/mnt/sdcard/newcell"+j+i+".jpg");
					cells[i][j].compress(Bitmap.CompressFormat.JPEG, 90, out);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}*/
			}
		}
	}
	
	private void findPuzzleCorners()
	{
		int i,j,I=0,Il=0,Ih=0,lastI=256;
		//I is intensity, Ih and Il correspond to the intensities of 
		//"black" and "white" in the image
		
		//get "white" value for Ih
		Ih=getRowIntensity(0);
		
		//Scan specified area to find top of sudoku
		for (i=2;i<height;i+=2)
		{
			
			//find average intensity of row
			I=getRowIntensity(i);
			
			//If this line is darker than "white" by more than 20%, top is found
			if (((double)(Ih-I)/((double)Ih))>.2)
			{ 
				Il=I;
				corners[TOP_LEFT].y=i;
				corners[TOP_RIGHT].y=i;
				break;
			}
		}
		lastI=Il;
		
		//move top downward until it stops decreasing in intensity
		while (lastI>=I)
		{
			lastI=I;
			corners[TOP_LEFT].y=i;
			corners[TOP_RIGHT].y=i;
			i+=2;
			
			//find average intensity of row
			I=getRowIntensity(i);
		}
		
		//find left side of top
		for (j=0;j<width;j+=2)
		{
			I=getIntensity(b.getPixel(j,i));
			if (Math.abs(I-Il)<Math.abs(Ih-I))
			{
				corners[TOP_LEFT].x=j;
				break;
			}
		}
		
		//find right side of top
		for (j=width-1;j>0;j-=2)
		{
			I=getIntensity(b.getPixel(j,i));
			if (Math.abs(I-Il)<Math.abs(Ih-I))
			{
				corners[TOP_RIGHT].x=j;
				break;
			}
		}
		
		//Reset white value for Ih to the bottom of the image
		Ih=getRowIntensity(height-1);
		
		//Scan specified area to find bottom of sudoku
		for (i=height-3;i>0;i-=2)
		{	
			//find average intensity of row
			I=getRowIntensity(i);
			
			//If this line is darker than "white" by more than 20%, bottom is found
			if (((double)(Ih-I)/((double)Ih))>.2)
			{ 
				Il=I;
				corners[BOTTOM_LEFT].y=i;
				corners[BOTTOM_RIGHT].y=i;
				break;
			}
		}
		lastI=256;
		
		//move the bottom up until the intensity no longer decreases
		while (lastI>=I)
		{
			lastI=I;
			corners[BOTTOM_LEFT].y=i;
			corners[BOTTOM_RIGHT].y=i;
			i-=2;
			
			//find average intensity of row
			I=getRowIntensity(i);
		}
		
		//find right side of bottom
		for (j=0;j<width;j+=2)
		{
			I=getIntensity(b.getPixel(j,corners[BOTTOM_LEFT].y));
			if (Math.abs(I-Il)<Math.abs(Ih-I))
			{
				corners[BOTTOM_LEFT].x=j;
				break;
			}
		}
		
		//find left side of bottom
		for (j=width-1;j>0;j-=2)
		{
			I=getIntensity(b.getPixel(j,corners[BOTTOM_LEFT].y));
			if (Math.abs(I-Il)<Math.abs(Ih-I))
			{
				corners[BOTTOM_RIGHT].x=j;
				break;
			}
		}
	}
	
	private int getIntensity(int c)
	{
		return (Color.red(c)+Color.green(c)+Color.blue(c))/3;
	}
	
	private int getRowIntensity(int row)
	{
		int j,c,total=0, count=0;
		for (j=0;j<width;j+=2)
		{
			total+=getIntensity(b.getPixel(j,row));
			count++;
		}
		return total/count;
	}

	// /mnt/sdcard/download/[file]
}
