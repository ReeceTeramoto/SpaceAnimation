package edu.up.cs301.spaceanimation;

import edu.up.cs301.animation.AnimationSurface;
import edu.up.cs301.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * SpaceActivity class that uses SpaceAnimator
 * 
 * @author Reece Teramoto
 * @version October 16, 2014
 * 
 */


public class SpaceActivity extends Activity {

	SeekBar ballSpeedSeeker;
	SeekBar sunGravitySeeker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_space);
		
		// Connect the animation surface with the animator
		AnimationSurface mySurface = (AnimationSurface) this.findViewById(R.id.animationSurface);

		final Animator anim = new SpaceAnimator();
		mySurface.setAnimator(anim);
		
		//landscape orientation
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		//ball speed seek bar
		ballSpeedSeeker = (SeekBar) findViewById(R.id.ballSpeedSeeker);
		ballSpeedSeeker.setMax(100);
		ballSpeedSeeker.setProgress(0);
		ballSpeedSeeker.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				//change ball velocity
				((SpaceAnimator) anim).changeBallVelocity(progress);
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{
				//nothing needed
			}
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				//nothing needed				
			}
			
		});
		
		
		//sun gravity seek bar
		sunGravitySeeker = (SeekBar) findViewById(R.id.sunGravitySeeker);
		sunGravitySeeker.setMax(100);
		sunGravitySeeker.setProgress(0);
		sunGravitySeeker.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				//change sun gravities
				((SpaceAnimator) anim).changeSunGravities(progress);	
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{
				//nothing needed
			}
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// nothing needed
			}

		});
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.space, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
