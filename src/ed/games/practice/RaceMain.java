package ed.games.practice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

public class RaceMain extends Activity{
	
	public static final String TAG = RaceMain.class.getSimpleName();
	private RacePanel mPanel;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //request to turn title OFF
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //make it full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);        
        // set the Game Panel as the view
        mPanel = new RacePanel(this);
        setContentView(mPanel);
        Log.d(TAG, "View Added");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);        
        
    }
    
    @Override
    protected void onPause(){
    	super.onPause();
    	mPanel.pause();
    }
    
    @Override
    protected void onResume(){
    	super.onResume();
    	mPanel.resume();
    }

	@Override
	protected void onDestroy() {
		Log.d(TAG, "Destroying...");
		super.onDestroy();
	}


	@Override
	protected void onStop() {
		Log.d(TAG, "Stopping...");
		super.onStop();
	}
    
	protected void onDone(int time){
		Log.d(TAG, "ALL DONE!!!!");
		Intent resultIntent  = new Intent(this, MainMenu.class);
		resultIntent.putExtra(RacePanel.RACE_RESULT_TIME, time);
		setResult(Activity.RESULT_OK, resultIntent);		
		finish();
	}
    
}