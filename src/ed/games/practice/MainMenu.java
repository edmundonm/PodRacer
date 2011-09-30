package ed.games.practice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainMenu extends Activity implements OnClickListener{
	public static final String TAG = MainMenu.class.getSimpleName();
	public static final int ACTIVITY_RACE = 0;	

	private Button mButton;
	private TextView mText;
	/**
	 * Simple on create method for the main menu. Called when first created.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//request to turn title OFF
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        //make it full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);        
        setContentView(R.layout.main_menu);
        
        mButton = (Button)findViewById(R.id.button1);
        mButton.setOnClickListener(this);
        mText = (TextView)findViewById(R.id.race_result);        
        Log.d(TAG, "heres the line where we check if mtext is still null");
	}
	
	/**
	 * For now, starts the RaceMain activity for result.
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent i = new Intent(this, RaceMain.class);
		startActivityForResult(i, ACTIVITY_RACE);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		int raceTime = data.getIntExtra(RacePanel.RACE_RESULT_TIME, 0);
		String result = (raceTime <= 0)?"No Race Time Available":timeString(raceTime);
		if(mText != null) mText.setText(result);
	}
	
	private String timeString(int millis){
		/*
		int minutes = millis/(60*60*1000);
		int seconds = millis/(60*1000);
		int hundreths = millis%(1000);
		String i = Integer.toString(minutes);
		i  = i + ":" + Integer.toString(seconds);
		i  = i + ":" + Integer.toString(hundreths);		
		*/
		return "Nice to meet chu!";
	}
	

}
