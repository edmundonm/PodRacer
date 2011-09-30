package ed.games.practice;

import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import ed.games.practice.models.Pod;
import ed.games.practice.models.Track;
import ed.games.practice.models.components.Speed;


public class RacePanel extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener{
	public static final String TAG = RacePanel.class.getSimpleName();
	public static final int EVENT_ROLL = 0;
	public static final int EVENT_TOUCH = 1;	
	public static final String RACE_RESULT_TIME = "time";
	private GameThread mThread;
	private Pod mPod;
	private Track mTrack;
	private SensorManager mManager;
	private Sensor mAccelerometer;
	private Sensor mMagnetometer;
	private float[] mGravity = null;
	private float[] mMagnet = null;

	public RacePanel(Context context) {
		super(context);
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);
		
		// create a pod and load its bitmap
		mPod = new Pod(BitmapFactory.decodeResource(getResources(),R.drawable.spod), 200, 0);

		Bitmap a = BitmapFactory.decodeResource(getResources(),R.drawable.test);
		Bitmap b = BitmapFactory.decodeResource(getResources(),R.drawable.test2);
		mTrack = new Track(a,b, 0);
		
		// create game loop thread
		mThread = new GameThread(getHolder(), this);
		
		// get a sensor manager
		mManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mMagnetometer = mManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		
		// make the game panel focusable so it can handle events, not sure this is needed
		// because our app will be all touch and no keyboard
		setFocusable(true);
		//		setFocusableInTouchMode(true);
	}
		
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		mPod.setX(getWidth()/2);
		mPod.setY(getHeight() - mPod.getBitmap().getHeight());
		mTrack.setDimensions(getWidth());
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		mThread.setRunning(true);
		mThread.start();
		mPod.setX(getWidth());
		mPod.setY(0);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		Log.d(TAG, "surface destroyed!!!");
		boolean retry = true;
		while(retry){
			try{
				mThread.join();
				retry = false;
			} catch(InterruptedException e){
				//try again shutting down the thread
			}
		}
	}
	
	public void resume(){
		Log.d(TAG, "Called resume() on RacePanel!!!");
		mManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
		mManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
	}
	
	public void pause(){
		Log.d(TAG, "Called pause() on RacePanel");
		mManager.unregisterListener(this);
	}

	
	protected void render(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.drawColor(Color.BLACK);
		mPod.draw(canvas);
	}

		
	private void checkBounds(){
		// check collision if heading right
		if(mPod.getSpeed().getXDirection() == Speed.DIRECTION_RIGHT	&& mPod.getX() + mPod.getBitmap().getWidth()/2 >= getWidth()){
			//should only pass events to thread -fix
			mPod.getSpeed().setXDirection(Speed.DIRECTION_STILL);
		}
		// check collision if heading left
		if(mPod.getSpeed().getXDirection() == Speed.DIRECTION_LEFT && mPod.getX() - mPod.getBitmap().getWidth()/2 <= 0){
			//should only pass events to thread- fix
			mPod.getSpeed().setXDirection(Speed.DIRECTION_STILL);
		}
	}
	
	private void endActivity(int time){
		((RaceMain)getContext()).onDone(time);
	}
	
	/**
	 * Called when the screen is touched.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			Log.d(TAG, "Coords: x=" + event.getX() + ",y=" + event.getY());
			// if click on the bottom, exit
			if(event.getY() < 50){				
				if(mThread.getThreadState() == GameThread.STATE_RUNNING){
					Log.d(TAG, "NOT Pausing thread after click on top");
					mThread.pauseThread();					
				} else {
					Log.d(TAG, "Resuming thread after click on top");
					mThread.resumeThread();
				}
			} else if(event.getY() > getHeight() - 50){
				Log.d(TAG, "Clicked the bottom of the screen. Called finish()");				
				((Activity)getContext()).finish();
			}
		}
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		Log.d(TAG, sensor.getType() + ": "+ Integer.toString(accuracy));
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
			mGravity = event.values.clone();
		}
		if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
			mMagnet = event.values.clone();
		}
		if(mGravity != null && mMagnet != null){
			float R[] = new float[9];
			float I[] = new float[9];
			boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mMagnet);
			if(success){
				float orientation[] = new float[3];
				SensorManager.getOrientation(R, orientation);
				mThread.onRollEvent(new RollEvent(orientation[2]));
			} 
		}
	}
	
	class GameEvent {
		public int mType;
		
		public GameEvent(int type){
			mType = type;
		}
	}
	
	class RollEvent extends GameEvent{
		public float mRoll;
		public RollEvent(float r){
			super(EVENT_ROLL);
			mRoll = r;
		}
	}
	
	class TouchEvent extends GameEvent{
		public int mX;
		public int mY;
		public TouchEvent(int x, int y){
			super(EVENT_TOUCH);
			mX = x;
			mY = y;
		}		
	}
	
	class GameThread extends Thread{

		private final static int MAX_FPS = 50;
		private final static int MAX_FRAME_SKIPS = 5;
		private final static int FRAME_PERIOD  = 1000/MAX_FPS;
		private final static int STATE_PAUSED = 0;
		private final static int STATE_RUNNING = 1;
		
		//flag to hold game state
		private boolean isRunning;
		private int mState;

		private SurfaceHolder 	mHolder;
		private RacePanel 		mPanel;		
		
		private ConcurrentLinkedQueue<GameEvent> mEventQueue;
		
		public GameThread(SurfaceHolder h, RacePanel p){
			super();
			mHolder = h;
			mPanel = p;
			mEventQueue = new ConcurrentLinkedQueue<RacePanel.GameEvent>();
			mState = STATE_PAUSED;
		}
		
		public void setRunning(boolean run){
			mState = run?STATE_RUNNING:STATE_PAUSED;
			this.isRunning = run;
		}
		
		@Override
		public void run() {
			Canvas canvas;
			Log.d(TAG, "Starting game loop.");
			
			long beginTime;
			long timeDiff;
			int sleepTime;
			int framesSkipped;
			
			sleepTime = 0;

			while(isRunning){
				canvas = null;
				//try locking the canvas
				canvas = this.mHolder.lockCanvas();
				try{	
					synchronized(mHolder){
						beginTime = System.currentTimeMillis(); // initialize counter					
						framesSkipped = 0;	// reset frames
						// update game state
						updateGameState();
						// draws the canvas on the panel
						draw(canvas);
						// calculate how much time elapsed
						timeDiff = System.currentTimeMillis() - beginTime;
						// calculate difference from ideal time
						sleepTime = (int)(FRAME_PERIOD - timeDiff);
						if(sleepTime > 0){	
							// if sleep time positive then we're okay
							try{
								// wait until next cycle
								Thread.sleep(sleepTime);
							} catch (Exception e){
								Log.e(TAG, e.toString());
							}
						}
						while(sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS){
							// catch up here
							// only update without render
							updateGameState();
							// add FramePeriod to see if we're in the next frame already
							sleepTime += FRAME_PERIOD;
							framesSkipped++;
							Log.d(TAG, "Catching Up!!!");
						}
					}
				} finally {
					// in case of exception, surface is not in inconsistent state
					if(canvas != null){
						mHolder.unlockCanvasAndPost(canvas);
					}
				}
				// update game state
				
				// render state to screen			
			}
		}

		private void updateGameState(){
			if(mState != STATE_RUNNING) return;
			while(true){
				GameEvent next = mEventQueue.poll();
				if(next == null){
					break;
				} else {
					if(next instanceof RollEvent){
						mPod.handleRoll(((RollEvent) next).mRoll);
					} 
					else if (next instanceof TouchEvent){
					}
				}
				
			}
			checkBounds();
			mPod.update();
			if(mTrack.isDone(mPod.getY())){
				isRunning = false;
				Log.d(TAG, "ALL FINISHED!!!!!!!!!!!");
				endActivity(123456);
			}

		}
		
		private void draw(Canvas c){
			mTrack.draw(c, mPod.getY());			
			mPod.draw(c);
		}
		
		public void onRollEvent(RollEvent e){
			mEventQueue.add(e);
		}
		
		public void onTouchEvent(TouchEvent e){
			mEventQueue.add(e);
		}

		public void pauseThread(){
			Log.d(TAG, "Thread Pausing");
			synchronized(mHolder){
				mState = STATE_PAUSED;
			}
		}
		
		public int getThreadState(){
			synchronized(mHolder){
				return mState;
			}
		}
		
		public void resumeThread(){
			Log.d(TAG, "resuming thread");
			synchronized(mHolder){
				mEventQueue.clear();
				mState = STATE_RUNNING;
			}
		}
	}
	
	

}
