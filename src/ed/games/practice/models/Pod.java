package ed.games.practice.models;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import ed.games.practice.models.components.Speed;


public class Pod {
	private Bitmap mBitmap;
	private int	mX;
	private int mY;
	private Speed mSpeed;
	public static final float ROLL_LEFT = -0.23f;
	public static final float ROLL_RIGHT = 0.23f;
	
	public Pod(Bitmap bitmap, int x, int y){
		mSpeed = new Speed();
		mBitmap = bitmap;
		mX = x;
		mY = y;		
	}
	
	public Bitmap getBitmap(){
		return mBitmap;
	}
	
	public void setBitmap(Bitmap b){
		mBitmap = b;
	}
	
	public int getX(){
		return mX;
	}
	
	public void setX(int x){
		mX = x;
	}
	
	public int getY(){
		return mY;		
	}
	
	public void setY(int y){
		mY = y;
	}
	
	
	
	public Speed getSpeed(){
		return mSpeed;
	}
	
	public void draw(Canvas c){
		c.drawBitmap(mBitmap, mX - (mBitmap.getWidth()/2), c.getHeight() - (mBitmap.getHeight() + 20), null);
	}
	
	
	public void handleRoll(float roll){
		if(roll >= ROLL_RIGHT){
			mSpeed.setXDirection(Speed.DIRECTION_RIGHT);
		}
		else if(roll <= ROLL_LEFT){
			mSpeed.setXDirection(Speed.DIRECTION_LEFT);
		}
		else if(mSpeed.getXDirection() != Speed.DIRECTION_STILL){
			mSpeed.setXDirection(Speed.DIRECTION_STILL);
		}
	}
	
	public void update(){
		mX += (mSpeed.getXV() * mSpeed.getXDirection());
		mY += (mSpeed.getYV() * mSpeed.getYDirection());
	}
	

}
