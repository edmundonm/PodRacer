package ed.games.practice.models;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Track {
	private Bitmap[] mBitmaps;	
	private int mX;
	private int mLength;
	private int finishLine = 4000;
	
	public Track(Bitmap a, Bitmap b, int screenWidth){
		mBitmaps = new Bitmap[2];
		mBitmaps[0] = a;
		mBitmaps[1] = b;
		mX = -(mBitmaps[0].getWidth() - screenWidth)/2;			
		mLength = mBitmaps[0].getHeight()*2;
	}
	
	public void draw(Canvas c, int podPosition){
		int currPos = (podPosition % mLength);		
		
		int currBm = (currPos*2 > mLength)?1:0;
		
		currPos = (podPosition%c.getHeight()) - mLength/2;		
		
		while(currPos < c.getHeight()){
			c.drawBitmap(mBitmaps[currBm], mX, currPos, null);		
			currPos = currPos + mLength/2;
			currBm = (1 + currBm)%2;
		}
	}
	

	
	public void setDimensions(int screenWidth){
		mX = -(mBitmaps[0].getWidth() - screenWidth)/2;			
	}
	
	public boolean isDone(int position){
		return position >= finishLine?true:false;
	}

}
