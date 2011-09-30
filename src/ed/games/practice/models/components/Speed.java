package ed.games.practice.models.components;

public class Speed {
	public static final int DIRECTION_LEFT = -7;
	public static final int DIRECTION_RIGHT = 7;
	public static final int DIRECTION_UP = 1;
	public static final int DIRECTION_DOWN = -1;
	public static final int DIRECTION_STILL = 0;
	
	private float mXV = 1;
	private float mYV = 4;
	
	private int mxDir = DIRECTION_STILL;
	private int myDir = DIRECTION_UP;
	
	public Speed(){
		this.mXV = 1;
		this.mYV = 4;
	}
	
	public Speed(float xv, float yv){
		this.mXV = xv;
		this.mYV = yv;
	}
	
	public float getXV(){
		return mXV;
	}
	
	public void setXV(float xv){
		mXV = xv;
	}
	
	public float getYV(){
		return mYV;
	}
	
	public void setYV(float yv){	
		mYV = yv;
	}
	
	public int getXDirection(){
		return mxDir;
	}
	
	public void setXDirection(int dir){
		mxDir = dir;
	}
	
	public int getYDirection(){
		return myDir;
	}
	
	public void setYDirection(int dir){
		myDir = dir;
	}
	
	public void toggleXDir(){
		mxDir = mxDir*-1;
	}
	
	public void toggleYDir(){
		myDir = myDir*-1;
	}
	
}
