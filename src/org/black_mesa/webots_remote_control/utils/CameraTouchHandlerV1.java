package org.black_mesa.webots_remote_control.utils;

import java.util.Timer;
import java.util.TimerTask;

import org.black_mesa.webots_remote_control.listeners.CameraTouchListenerV1;

import android.os.SystemClock;
import android.view.MotionEvent;

/**
 * Handles the touch events on a CameraView. Interaction mode: oval, pinch sets forward position.
 * 
 * @author Ilja Kroonen
 */
public class CameraTouchHandlerV1 {
	private static final int TIMER_TICK = 32;
	/*
	 * Current state of the handler.
	 */
	private State mState = State.INIT;

	/*
	 * Main pointer id and last coordinates. Valid in states DOUBLE_CENTRAL, SINGLE_CENTRAL, SINGLE_SIDE.
	 */
	private int mP1;
	private float mX1;
	private float mY1;

	/*
	 * Second pointer id and last coordinates. Valid in state DOUBLE_CENTRAL.
	 */
	private int mP2;
	private float mX2;
	private float mY2;

	/*
	 * Timer for SINGLE_SIDE.
	 */
	private Timer mTimer;

	/*
	 * Timestamp of the last event that has been consumed (uptime millis). Valid in state SINGLE_SIDE.
	 */
	private long mTimestamp;

	/*
	 * Information about the touching surface.
	 */
	private float mXMin;
	private float mYMin;
	private float mXMax;
	private float mYMax;

	/*
	 * Listener for the generated events
	 */
	private CameraTouchListenerV1 mListener;

	/**
	 * Instantiates the CameraTouchHandler.
	 * 
	 * @param xMin
	 *            Start of the x axis of the window.
	 * @param yMin
	 *            Start of the y axis of the window.
	 * @param xMax
	 *            End of the x axis of the window.
	 * @param yMax
	 *            End of the y axis of the window.
	 * @param l
	 *            Listener that will be notified of the actions that need to be performed on the camera.
	 */
	public CameraTouchHandlerV1(final float xMin, final float yMin, final float xMax, final float yMax,
			final CameraTouchListenerV1 l) {
		mXMin = xMin;
		mYMin = yMin;
		mXMax = xMax;
		mYMax = yMax;
		mListener = l;
	}

	/**
	 * Has to be called at each touch event on the view.
	 * 
	 * @param event
	 *            MotionEvent that was generated by Android.
	 */
	public final void onTouch(final MotionEvent event) {
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			downHandler(event);
			break;
		case MotionEvent.ACTION_CANCEL:
			cancelHandler(event);
			break;
		case MotionEvent.ACTION_UP:
			upHandler(event);
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			pointerDownHandler(event);
			break;
		case MotionEvent.ACTION_POINTER_UP:
			pointerUpHandler(event);
			break;
		case MotionEvent.ACTION_MOVE:
			moveHandler(event);
			break;
		}
		update(event);
	}

	private void moveHandler(final MotionEvent event) {
		switch (mState) {
		case DOUBLE_CENTRAL:
			float prevDistance = distance(mX1, mY1, mX2, mY2);
			float newDistance =
					distance(event.getX(event.findPointerIndex(mP1)), event.getY(event.findPointerIndex(mP1)),
							event.getX(event.findPointerIndex(mP2)), event.getY(event.findPointerIndex(mP2)));
			float res = (newDistance - prevDistance) / distance(mXMin, mYMin, mXMax, mYMax);
			mListener.moveForward(res);
			break;
		case INIT:
			throw new RuntimeException();
		case SINGLE_CENTRAL:
			float dX = (event.getX() - mX1) / (mXMax - mXMin);
			float dY = (event.getY() - mY1) / (mYMax - mYMin);
			mListener.turnPitch(dX, dY);
			break;
		case SINGLE_SIDE:
			break;
		case DOUBLE_SIDE:
			prevDistance = distance(mX1, mY1, mX2, mY2);
			newDistance =
					distance(event.getX(event.findPointerIndex(mP1)), event.getY(event.findPointerIndex(mP1)),
							event.getX(event.findPointerIndex(mP2)), event.getY(event.findPointerIndex(mP2)));
			res = (newDistance - prevDistance) / distance(mXMin, mYMin, mXMax, mYMax);
			mListener.moveForward(res);
			break;
		}
	}

	private void pointerUpHandler(final MotionEvent event) {
		switch (mState) {
		case DOUBLE_CENTRAL:
			int id = event.getPointerId(event.getActionIndex());
			int count = event.getPointerCount();
			if (count == 2) {
				if (id == mP1) {
					mP1 = mP2;
				}
				mState = State.SINGLE_CENTRAL;
			} else if (count > 2) {
				if (id == mP1) {
					for (int i = 0; i < count; i++) {
						int candidate = event.getPointerId(i);
						if (candidate != id && candidate != mP2) {
							mP1 = candidate;
							break;
						}
					}
				} else if (id == mP2) {
					for (int i = 0; i < count; i++) {
						int candidate = event.getPointerId(i);
						if (candidate != id && candidate != mP1) {
							mP2 = candidate;
							break;
						}
					}
				}
				mState = State.DOUBLE_CENTRAL;
			} else {
				throw new RuntimeException();
			}
			break;
		case INIT:
			throw new RuntimeException();
		case SINGLE_CENTRAL:
			throw new RuntimeException();
		case SINGLE_SIDE:
			throw new RuntimeException();
		case DOUBLE_SIDE:
			id = event.getPointerId(event.getActionIndex());
			count = event.getPointerCount();
			if (count == 2) {
				if (id == mP1) {
					mP1 = mP2;
				}
				mTimer = new Timer();
				TimerTask task = new TimerTask() {

					@Override
					public void run() {
						timerHandler();
					}
				};
				mTimer.schedule(task, 0, TIMER_TICK);
				mState = State.SINGLE_SIDE;
			} else if (count > 2) {
				if (id == mP1) {
					for (int i = 0; i < count; i++) {
						int candidate = event.getPointerId(i);
						if (candidate != id && candidate != mP2) {
							mP1 = candidate;
							break;
						}
					}
				} else if (id == mP2) {
					for (int i = 0; i < count; i++) {
						int candidate = event.getPointerId(i);
						if (candidate != id && candidate != mP1) {
							mP2 = candidate;
							break;
						}
					}
				}
				mState = State.DOUBLE_SIDE;
			} else {
				throw new RuntimeException();
			}
			break;
		}
	}

	private void pointerDownHandler(final MotionEvent event) {
		switch (mState) {
		case DOUBLE_CENTRAL:
			mState = State.DOUBLE_CENTRAL;
			break;
		case INIT:
			throw new RuntimeException();
		case SINGLE_CENTRAL:
			mP2 = event.getPointerId(event.getActionIndex());
			mState = State.DOUBLE_CENTRAL;
			break;
		case SINGLE_SIDE:
			mTimer.cancel();
			mP2 = event.getPointerId(event.getActionIndex());
			mState = State.DOUBLE_SIDE;
			break;
		case DOUBLE_SIDE:
			mState = State.DOUBLE_SIDE;
			break;
		}
	}

	private void upHandler(final MotionEvent event) {
		switch (mState) {
		case DOUBLE_CENTRAL:
			throw new RuntimeException();
		case INIT:
			throw new RuntimeException();
		case SINGLE_CENTRAL:
			mState = State.INIT;
			break;
		case SINGLE_SIDE:
			mTimer.cancel();
			mState = State.INIT;
			break;
		case DOUBLE_SIDE:
			throw new RuntimeException();
		}
	}

	private void cancelHandler(final MotionEvent event) {
		switch (mState) {
		case DOUBLE_CENTRAL:
			mState = State.INIT;
			break;
		case INIT:
			throw new RuntimeException();
		case SINGLE_CENTRAL:
			mState = State.INIT;
			break;
		case SINGLE_SIDE:
			mTimer.cancel();
			mState = State.INIT;
			break;
		case DOUBLE_SIDE:
			mState = State.INIT;
			break;
		}
	}

	private void downHandler(final MotionEvent event) {
		switch (mState) {
		case DOUBLE_CENTRAL:
			throw new RuntimeException();
		case INIT:
			mP1 = event.getPointerId(event.getActionIndex());
			if (isCenter(event.getX(), event.getY())) {
				mState = State.SINGLE_CENTRAL;
			} else {
				mTimer = new Timer();
				TimerTask task = new TimerTask() {

					@Override
					public void run() {
						timerHandler();
					}
				};
				mTimer.schedule(task, 0, TIMER_TICK);
				mState = State.SINGLE_SIDE;
			}
			break;
		case SINGLE_CENTRAL:
			throw new RuntimeException();
		case SINGLE_SIDE:
			throw new RuntimeException();
		case DOUBLE_SIDE:
			throw new RuntimeException();
		}
	}

	private void timerHandler() {
		switch (mState) {
		case DOUBLE_CENTRAL:
			throw new RuntimeException();
		case INIT:
			throw new RuntimeException();
		case SINGLE_CENTRAL:
			throw new RuntimeException();
		case SINGLE_SIDE:
			float resX = (mX1 - (mXMax - mXMin) / 2) / (mXMax - mXMin);
			float resY = (mY1 - (mYMax - mYMin) / 2) / (mYMax - mYMin);
			long now = SystemClock.uptimeMillis();
			long time = now - mTimestamp;
			mListener.moveSide(resX, resY, time);
			mTimestamp = now;
			break;
		case DOUBLE_SIDE:
			throw new RuntimeException();
		}
	}

	private void update(final MotionEvent event) {
		switch (mState) {
		case DOUBLE_CENTRAL:
			mX1 = event.getX(event.findPointerIndex(mP1));
			mY1 = event.getY(event.findPointerIndex(mP1));
			mX2 = event.getX(event.findPointerIndex(mP2));
			mY2 = event.getY(event.findPointerIndex(mP2));
			break;
		case INIT:
			break;
		case SINGLE_CENTRAL:
			mX1 = event.getX(event.findPointerIndex(mP1));
			mY1 = event.getY(event.findPointerIndex(mP1));
			break;
		case SINGLE_SIDE:
			mX1 = event.getX(event.findPointerIndex(mP1));
			mY1 = event.getY(event.findPointerIndex(mP1));
			mTimestamp = event.getEventTime();
			break;
		case DOUBLE_SIDE:
			mX1 = event.getX(event.findPointerIndex(mP1));
			mY1 = event.getY(event.findPointerIndex(mP1));
			mX2 = event.getX(event.findPointerIndex(mP2));
			mY2 = event.getY(event.findPointerIndex(mP2));
			break;
		}
	}

	private boolean isCenter(final float x, final float y) {
		// We have to detect if the zone that has been touched is the "center".
		// Here, we defined the center as an oval covering 2/3 of the view.
		float u = (mXMax - mXMin) / 2;
		float v = (mYMax - mYMin) / 2;
		float a = (mXMax - mXMin) / 3;
		float b = (mYMax - mYMin) / 3;
		float t = (x - u) * (x - u) / (a * a) + (y - v) * (y - v) / (b * b);
		return t <= 1;
	}

	private float distance(final float x1, final float y1, final float x2, final float y2) {
		return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	private enum State {
		DOUBLE_CENTRAL, DOUBLE_SIDE, INIT, SINGLE_CENTRAL, SINGLE_SIDE
	}
}