package edu.up.cs301.spaceanimation;

import java.util.ArrayList;

import edu.up.cs301.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.view.MotionEvent;
import android.widget.TextView;


/**
 * class that animates a ball repeatedly moving diagonally on
 * simple background
 * 
 * @author Reece Teramoto
 * @version 14 Oct 2014
 * 
 * ENHANCEMENTS:
 * -have multiple suns, with varying sizes, colors, and gravities
 * -have the stars twinkle
 * -allow an arbitrary number of balls to be in space at once
 * -allow the user to control the speed at which balls are shot (slider)
 * -two balls will destroy each other when they collide 
 * 	(SET THE INSTANCE VARIABLE twoBallsWillDestroyEachOtherWhenTheyCollide TO true TO SEE THIS)
 * 		(but note that when it is enabled, the game is not as fun since you can't 
 * 		shoot the balls quickly since they will be too close together and essentially destroy
 * 		each other)
 * -allow the user to change the gravity of the suns (slider)
 * -animate the explosion of a ball when it hits a sun (balls turn red and then disappear)
 * 
 * 
 * Describe how to use the app here:
 * -the gun moves automatically (top left of screen)
 * -to shoot a ball, tap on the screen
 * -use the "Ball Speed" and "Gravity of Suns" sliders to increase or decrease
 * 		the ball speed or sun gravities
 * -to allow two balls to destroy each other when they collide, see above
 * -note that because I did the enhancements, I disabled the destroying of balls when
 * 		then balls go off the screen, since the gravity of the suns will bring them back.
 * 		The exact part of the code is commented out in the tick method
 * 
 */
public class SpaceAnimator implements Animator {
	//set this to true to allow 2 balls to destroy each other when they collide,
	//but note that it's easier to see the ball speed and other aspects of the game 
	//when this is set to false since this prevents you from being able to shoot the balls
	//out quickly since they will essentially collide with each other upon launch
	private boolean twoBallsWillDestroyEachOtherWhenTheyCollide = false;
	
	// constants
	private static final int FRAME_INTERVAL = 20; // animation-frame interval, in milliseconds
	private static final int BACKGROUND_COLOR = Color.BLACK; // background color
	
	// paint objects
	private Paint redPaint;
	private Paint starPaint;
	private Paint sunPaint;
	private Paint gunPaint;
	private Paint ballPaint;
	
	//define an Array of points to contain the position of the stars
	private PointF[] starPoints;
	//number of stars
	private int NUM_STARS = 75;
	
	//screen width and height
	private int SCREEN_WIDTH = 1280;
	private int SCREEN_HEIGHT = 800;
	
	//sun positions and radii
	private ArrayList<PointF> sunPositions = new ArrayList<PointF>();
	private ArrayList<Integer> sunRadii = new ArrayList<Integer>();
	private ArrayList<Float> sunGravities = new ArrayList<Float>();
	private ArrayList<Float> origSunGravities = new ArrayList<Float>();
	private ArrayList<Paint> sunColors = new ArrayList<Paint>();
	//point that represents the position of the center of the sun
	PointF sunCenter;
	private int SUN_X = 400;
	private int SUN_Y = 500;
	//radius of sun
	private int SUN_RADIUS = 75;
	//gravity of the sun
	private final float SUN_GRAVITY = 5f;
	
	//gun dimensions
	private final float GUN_LENGTH = 150;
	private final float GUN_HEIGHT = 50;
	//rectangle that represents the gun
	private RectF gun = new RectF(-GUN_HEIGHT, 0, GUN_LENGTH, GUN_HEIGHT);
	//angle of the gun
	private float gunAngle = 1;
	//boolean indicating the direction that the gun is moving
	private boolean gunMovingClockwise = true;
	//point indicating the tip of the gun
	private PointF gunTip;
	
	//shooting ball
	//array list of ball positions
	private ArrayList<PointF> ballPositions = new ArrayList<PointF>();
	//balls that are dead; animated to explode
	private ArrayList<PointF> deadBallPositions = new ArrayList<PointF>();
	private ArrayList<PointF> deadBallVelocities = new ArrayList<PointF>();
	//array list of ball velocities
	private ArrayList<PointF> ballVelocities = new ArrayList<PointF>();
	//current ball position and velocity (null = no ball)
	private PointF currentBallPosition = null;
	private PointF defaultBallVelocity = new PointF(0.2f,0.2f);
	private PointF origBallVelocity = new PointF(0.2f,0.2f);
	//ball radius
	private final int BALL_RADIUS = 10;
	
	/**
	 * Constructor
	 */
	public SpaceAnimator() {		
		// create the paint objects
		redPaint = new Paint();
		redPaint.setColor(Color.RED);
		starPaint = new Paint();
		starPaint.setColor(Color.WHITE);
		sunPaint = new Paint();
		sunPaint.setColor(Color.YELLOW);
		gunPaint = new Paint();
		gunPaint.setColor(Color.GRAY);
		ballPaint = new Paint();
		ballPaint.setColor(Color.BLUE);
		
		//set the stars
		starPoints = new PointF[NUM_STARS];
		
		//sets the center of the sun
		sunCenter = new PointF(SUN_X, SUN_Y);
		PointF sunCenter2 = new PointF(800, 200);
		int sunRadius2 = 50;
		float sunGravity2 = 10f;
		Paint sunPaint2 = new Paint();
		sunPaint2.setColor(Color.MAGENTA);
		PointF sunCenter3 = new PointF(1000, 500);
		int sunRadius3 = 40;
		float sunGravity3 = 7f;
		Paint sunPaint3 = new Paint();
		sunPaint3.setColor(Color.WHITE);
		
		//add the sun positions, radii, gravities, and colors
		sunPositions.add(sunCenter);
		sunRadii.add(SUN_RADIUS);
		sunGravities.add(SUN_GRAVITY);
		sunColors.add(sunPaint);
		
		sunPositions.add(sunCenter2);
		sunRadii.add(sunRadius2);
		sunGravities.add(sunGravity2);
		sunColors.add(sunPaint2);
		
		sunPositions.add(sunCenter3);
		sunRadii.add(sunRadius3);
		sunGravities.add(sunGravity3);
		sunColors.add(sunPaint3);
		
		gunTip = new PointF();
		
		//create the star-position ArrayList and initialize it with 
		//random star positions
		for (int i = 0; i < starPoints.length; ++i)
		{
			float x = (float) (Math.random()*(SCREEN_WIDTH));
			float y = (float) (Math.random()*(SCREEN_HEIGHT));
			
			PointF test = new PointF(x, y);
			starPoints[i] = test;
		}		
		
		//save the original sun gravities to allow them to be changed
		for (int i = 0; i < sunGravities.size(); ++i)
		{
			origSunGravities.add(sunGravities.get(i));
		}
	}
	
	/**
	 * Interval between animation frames
	 * 
	 * @return the time interval between frames, in milliseconds.
	 */
	public int interval() {
		return FRAME_INTERVAL;
	}

	/**
	 * The background color.
	 * 
	 * @return the background color onto which we will draw the image.
	 */
	public int backgroundColor() {
		// create/return the background color
		return BACKGROUND_COLOR;
	}

	/**
	 * Action to perform on clock tick
	 * 
	 * @param g the canvas object on which to draw
	 */
	public void tick(Canvas g) {	
		//traverse the array to paint each star in its proper location
		for (int i = 0; i < starPoints.length; ++i)
		{
			float x = starPoints[i].x;
			float y = starPoints[i].y;
			int radius;
			
			//randomize the radius to make the stars twinkle
			double random = Math.random();
			if (random < 0.5)
				{radius = 3;}
			else {radius = 5;}
			
			g.drawCircle(x, y, radius, starPaint);
		}
		
		//paint the sun(s)
		for (int i = 0; i < sunPositions.size(); ++i)
		{
			g.drawCircle(sunPositions.get(i).x, sunPositions.get(i).y, sunRadii.get(i), sunColors.get(i));
		}
		
		if (gunMovingClockwise) //gun is moving clockwise
		{
			//draw the gun, rotated clockwise by 1 degree
			g.save();
			gunAngle = gunAngle + 1;
			g.rotate(gunAngle);
			g.drawRect(gun, gunPaint);
			g.restore();
		}
		else //the gun is moving counterclockwise, so rotate by -1 degree
		{
			g.save();
			gunAngle = gunAngle - 1;
			g.rotate(gunAngle);
			g.drawRect(gun, gunPaint);
			g.restore();
		}
		
		//angle to radian conversion
		double theta = (double) gunAngle * 0.01745329;
		
		//save the tip of the gun
		double gunEndX = Math.abs((GUN_LENGTH*Math.cos(theta)));
		double gunEndY = Math.abs((GUN_LENGTH*Math.sin(theta)));
		gunTip.set((float)gunEndX, (float)gunEndY);
		
		//reverse direction if gun is vertical or horizontal
		if (gunAngle == 1 || gunAngle == 89) 
		{	gunMovingClockwise = !gunMovingClockwise;	}
		
		//////////////////////////////////////////////////////////////////////////
		//The following snipet of commented out code is what would allow the balls
		//to be destroyed when they move off the screen. This was not implemented since
		//the multiple suns will pull the balls back with gravity.
		//////		boolean ballIsOffScreen = ((currentBallPosition.x + BALL_RADIUS < 0) || (currentBallPosition.y + BALL_RADIUS < 0)
		//////		|| (ballPositions.get(i).x - BALL_RADIUS > SCREEN_WIDTH) ||
		//////		(ballPositions.get(i).y - BALL_RADIUS > SCREEN_HEIGHT));
		//////
		//////		if the ball is touching a sun or off the screen
		//////		if ((distanceBetweenBallAndSun < sumOfRadii) || ballIsOffScreen)
		//////		
		//////////////////////////////////////////////////////////////////////////	
		
		
		//if there is a ball (ballPositions array list is not empty)
		if (!ballPositions.isEmpty())
		{
			for (int i = 0; i < sunPositions.size(); ++i)
			{
				int numBalls = ballPositions.size();
				for (int j = 0; j < numBalls; ++j)
				{
					if (areOverlapping(sunPositions.get(i), sunRadii.get(i),
							ballPositions.get(j), BALL_RADIUS))
					{
						//add the "dead balls" to the according array list
						deadBallPositions.add(ballPositions.get(j));
						deadBallVelocities.add(ballVelocities.get(j));
						//remove them from the normal array list
						ballPositions.remove(j);
						ballVelocities.remove(j);
						//need to re-check since we removed balls from the list
						--j;
						--numBalls;
					}
					else
					{
						//sum up the direction velocities from the gravity of each sun
						float sumX = 0;
						float sumY = 0;
						for (int k = 0; k < sunPositions.size(); ++k)
						{
							//gravity of each sun times the direction of each sun
							float gravDirX = sunGravities.get(k) * 
									directionPointF(ballPositions.get(j), sunPositions.get(k)).x;
							//divided by the square of the distance between the ball and each sun
							float distance = (distanceBetweenPoints(ballPositions.get(j), 
									sunPositions.get(k)));
							gravDirX = gravDirX / (float) Math.pow(distance, 2);
							float gravDirY = sunGravities.get(k) * 
									directionPointF(ballPositions.get(j), sunPositions.get(k)).y;
							gravDirY = gravDirY / (float) Math.pow(distance, 2);
							sumX = sumX + gravDirX;
							sumY = sumY + gravDirY;
						}
												
						//updating the ball velocities
						float currentXVel = ballVelocities.get(j).x;
						float currentYVel = ballVelocities.get(j).y;
						ballVelocities.get(j).set(currentXVel + sumX, currentYVel + sumY);
						
						//updating the ball positions
						ballPositions.get(j).set(ballPositions.get(j).x + ballVelocities.get(j).x, 
								ballPositions.get(j).y + ballVelocities.get(j).y);
						
						//draw the balls
						g.drawCircle(ballPositions.get(j).x, ballPositions.get(j).y, BALL_RADIUS, ballPaint);
					}
				}			
			}
			
			//need to draw the "dead" balls (the ones that have crashed into a sun)
			if (!deadBallPositions.isEmpty())
			{
				for (int i = 0; i < deadBallPositions.size(); ++i)
				{
					//sum up the direction velocities from the gravity of each sun
					float sumX = 0;
					float sumY = 0;
					for (int k = 0; k < sunPositions.size(); ++k)
					{
						//gravity of each sun times the direction of each sun
						float gravDirX = sunGravities.get(k) * 
								directionPointF(deadBallPositions.get(i), sunPositions.get(k)).x;
						//divided by the square of the distance between the ball and each sun
						float distance = (distanceBetweenPoints(deadBallPositions.get(i), 
								sunPositions.get(k)));
						gravDirX = gravDirX / (float) Math.pow(distance, 2);
						float gravDirY = sunGravities.get(k) * 
								directionPointF(deadBallPositions.get(i), sunPositions.get(k)).y;
						gravDirY = gravDirY / (float) Math.pow(distance, 2);
						sumX = sumX + gravDirX;
						sumY = sumY + gravDirY;
					}
					
					//update the dead ball velocities
					float currentXVel = deadBallVelocities.get(i).x;
					float currentYVel = deadBallVelocities.get(i).y;
					deadBallVelocities.get(i).set(currentXVel + sumX, currentYVel + sumY);

					//update the dead ball positions
					deadBallPositions.get(i).set(deadBallPositions.get(i).x + deadBallVelocities.get(i).x, 
							deadBallPositions.get(i).y + deadBallVelocities.get(i).y);
					
					//draw the dead balls as red to simulate an explosion
					g.drawCircle(deadBallPositions.get(i).x, deadBallPositions.get(i).y, BALL_RADIUS, redPaint);
				}
			}
			
			//remove all the dead balls
			deadBallPositions.clear();
			deadBallVelocities.clear();

			
		}
	
		//update the ball positions
		if (!ballPositions.isEmpty())
		{
			for (int i = 0; i < ballPositions.size(); ++i)
			{
				ballPositions.get(i).set(ballPositions.get(i).x + ballVelocities.get(i).x,
						ballPositions.get(i).y + ballVelocities.get(i).y);
			}
		}
		
		//have two balls destroy each other if they collide
		if (twoBallsWillDestroyEachOtherWhenTheyCollide) //only if boolean is enabled
		{
			int numBalls = ballPositions.size();
			//create a temporary array list of balls to be removed
			ArrayList<PointF> tempPos = new ArrayList<PointF>();
			ArrayList<PointF> tempVel = new ArrayList<PointF>();

			//iterate through
			for (int i = 0; i < numBalls; ++i)
			{
				for (int j = i + 1; j < numBalls - 1; ++j)
				{
					//if any of the balls are overlapping, add them to the temp list
					if (ballPositions.get(i) != null && ballPositions.get(j) != null)
						if (areOverlapping(ballPositions.get(i), BALL_RADIUS,
								ballPositions.get(j), BALL_RADIUS))
						{
							tempPos.add(ballPositions.get(i));
							tempVel.add(ballVelocities.get(i));
							tempPos.add(ballPositions.get(j));
							tempVel.add(ballVelocities.get(i));
						}
				}
			}
			
			//now, remove them from the temp list
			for (int i = 0; i < tempPos.size(); ++i)
			{
				ballPositions.remove(tempPos.get(i));
				ballVelocities.remove(tempVel.get(i));
			}
			
			//clear the temp array lists
			tempPos.clear();
			tempVel.clear();
		}
	}

	/**
	 * Tells that we never pause.
	 * 
	 * @return indication of whether to pause
	 */
	public boolean doPause() {
		return false;
	}

	/**
	 * Tells that we never stop the animation.
	 * 
	 * @return indication of whether to quit.
	 */
	public boolean doQuit() {
		return false;
	}

	/**
	 * callback method, run when when surface is touched
	 */
	public void onTouch(MotionEvent event) {
		//create a ball whose position is the end of the gun
		currentBallPosition = new PointF(gunTip.x, gunTip.y);
		//add it to the array of balls
		ballPositions.add(currentBallPosition);
		//velocity is the "normal" shooting speed

		//need to create a unit vector of the direction of the gun			
		float xMult = gunTip.x / GUN_LENGTH;
		float yMult = gunTip.y / GUN_LENGTH;
		float xVelocity = defaultBallVelocity.x * xMult;
		float yVelocity = defaultBallVelocity.y * yMult;

		//add the newly created velocity to the ballVelocities array list
		PointF tempVelocity = new PointF(xVelocity, yVelocity);
		ballVelocities.add(tempVelocity);
	}

	/**
	 * Takes 2 PointF's and returns the distance between them as a float
	 * 
	 * @param p1
	 * @param p2
	 * @return distance between the two points
	 */
	private float distanceBetweenPoints(PointF p1, PointF p2)
	{
		float deltaX = Math.abs(p2.x - p1.x);
		float deltaY = Math.abs(p2.y - p1.y);
		return (float) Math.abs(Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)));
	}
	
	/**
	 * Takes 2 circles (center and their radii) and returns true if the circles are overlapping
	 * 
	 * @param p1
	 * @param radius1
	 * @param p2
	 * @param radius2
	 * @return true if they are overlapping
	 */
	private boolean areOverlapping(PointF p1, int radius1, PointF p2, int radius2)
	{
		if (p1 != null && p2 != null && radius1 != 0 && radius2 != 0)
		{
			//if the ball is touching a sun or off the screen, null out the
			//currentBallPosition variable
			double deltaX = Math.abs(p1.x - p2.x);
			double deltaY = Math.abs(p1.y - p2.y);
			double distanceBetweenBallAndSun = 
					Math.sqrt((Math.pow(deltaX, 2) + Math.pow(deltaY, 2)));
			double sumOfRadii = radius1 + radius2;

			//if the two are overlapping
			if ((distanceBetweenBallAndSun < sumOfRadii))
			{
				return true;
			}
			else return false;
		}
		return false; //if we are passed in something null or something with a radius of 0
	}
	
	/**
	 * Takes 2 points (p1 and p2) and returns a direction vector from p1 to p2
	 * 
	 * @param p1
	 * @param p2
	 * @return directionVector from p1 to p2
	 */
	private PointF directionPointF(PointF p1, PointF p2)
	{
		//simple math, self explanatory
		float deltaX = p2.x - p1.x;
		float deltaY = p2.y - p1.y;
		PointF rtn = new PointF(deltaX, deltaY);
		return rtn;
	}
	
	/**
	 * Changes the ball's velocity
	 * @param seekBarVal
	 */
	public void changeBallVelocity(int seekBarVal)
	{
		if (seekBarVal != 0)
		{
			//multiplying the original velocity by a change factor, depending on what 
			//the seekbar is on
			float velX = (float) (origBallVelocity.x * seekBarVal * 0.5);
			float velY = (float) (origBallVelocity.y * seekBarVal * 0.5);
			defaultBallVelocity.set(velX, velY);
		}
	}
	
	/**
	 * Changes the gravity of the suns
	 * @param seekBarVal
	 */
	public void changeSunGravities(int seekBarVal)
	{
		if (seekBarVal != 0)
		{
			//this is the factor the gravity will change by, based on what the seekbar is on
			float changeFactor = seekBarVal * 0.05f;
			for (int i = 0; i < origSunGravities.size(); ++i)
			{
				float oldGrav = origSunGravities.get(i);
				//the new gravity is the old gravity times the change factor
				float newGrav = oldGrav * changeFactor;
				sunGravities.set(i, newGrav);
			}
		}
	}
}//class SpaceAnimator
