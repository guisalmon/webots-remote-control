package org.black_mesa.webots_remote_control.utils;

import java.util.Timer;
import java.util.TimerTask;

import org.black_mesa.webots_remote_control.listeners.CameraTouchHandlerListener;

import android.os.SystemClock;
import android.view.MotionEvent;

public class CameraTouchHandler {
	private static final int TIMER_TICK = 32;
	/*
	 * Current state of the handler.
	 */
	private State s = State.INIT;

	/*
	 * Main pointer id and last coordinates. Valid in states DOUBLE_CENTRAL,
	 * SINGLE_CENTRAL, SINGLE_SIDE.
	 */
	private int p1;
	private float x1;
	private float y1;

	/*
	 * Second pointer id and last coordinates. Valid in state DOUBLE_CENTRAL.
	 */
	private int p2;
	private float x2;
	private float y2;

	/*
	 * Timer for SINGLE_SIDE.
	 */
	private Timer timer;

	/*
	 * Timestamp of the last event that has been consumed (uptime millis). Valid
	 * in state SINGLE_SIDE.
	 */
	private long t;

	/*
	 * Information about the touching surface.
	 */
	private float xMin;
	private float yMin;
	private float xMax;
	private float yMax;

	/*
	 * Listener for the generated events
	 */
	CameraTouchHandlerListener l;

	public CameraTouchHandler(float xMin, float yMin, float xMax, float yMax, CameraTouchHandlerListener l) {
		this.xMin = xMin;
		this.yMin = yMin;
		this.xMax = xMax;
		this.yMax = yMax;
		this.l = l;
	}

	public boolean onTouch(MotionEvent event) {
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
		return true;
	}

	private void moveHandler(MotionEvent event) {
		switch (s) {
		case DOUBLE_CENTRAL:
			float prevDistance = distance(x1, y1, x2, y2);
			float newDistance = distance(event.getX(event.findPointerIndex(p1)),
					event.getY(event.findPointerIndex(p1)), event.getX(event.findPointerIndex(p2)),
					event.getY(event.findPointerIndex(p2)));
			float res = (newDistance - prevDistance) / distance(xMin, yMin, xMax, yMax);
			l.moveForward(res);
			break;
		case INIT:
			throw new RuntimeException();
		case SINGLE_CENTRAL:
			float dX = (event.getX() - x1) / (xMax - xMin);
			float dY = (event.getY() - y1) / (yMax - yMin);
			l.turnPitch(dX, dY);
			break;
		case SINGLE_SIDE:
			break;
		}
	}

	private void pointerUpHandler(MotionEvent event) {
		switch (s) {
		case DOUBLE_CENTRAL:
			int id = event.getPointerId(event.getActionIndex());
			int count = event.getPointerCount();
			if (count == 2) {
				if (id == p1) {
					p1 = p2;
				}
				s = State.SINGLE_CENTRAL;
			} else if (count > 2) {
				if (id == p1) {
					for (int i = 0; i < count; i++) {
						int candidate = event.getPointerId(i);
						if (candidate != id && candidate != p2) {
							p1 = candidate;
							break;
						}
					}
				} else if (id == p2) {
					for (int i = 0; i < count; i++) {
						int candidate = event.getPointerId(i);
						if (candidate != id && candidate != p1) {
							p2 = candidate;
							break;
						}
					}
				}
				s = State.DOUBLE_CENTRAL;
			} else {
				throw new RuntimeException();
			}
			break;
		case INIT:
			throw new RuntimeException();
		case SINGLE_CENTRAL:
			throw new RuntimeException();
		case SINGLE_SIDE:
			id = event.getPointerId(event.getActionIndex());
			if (id == p1) {
				for (int i = 0; i < event.getPointerCount(); i++) {
					int candidate = event.getPointerId(i);
					if (candidate != id) {
						p1 = candidate;
						break;
					}
				}
			}
			s = State.SINGLE_SIDE;
			break;
		}
	}

	private void pointerDownHandler(MotionEvent event) {
		switch (s) {
		case DOUBLE_CENTRAL:
			s = State.DOUBLE_CENTRAL;
			break;
		case INIT:
			throw new RuntimeException();
		case SINGLE_CENTRAL:
			p2 = event.getPointerId(event.getActionIndex());
			s = State.DOUBLE_CENTRAL;
			break;
		case SINGLE_SIDE:
			s = State.SINGLE_SIDE;
			break;
		}
	}

	private void upHandler(MotionEvent event) {
		switch (s) {
		case DOUBLE_CENTRAL:
			throw new RuntimeException();
		case INIT:
			throw new RuntimeException();
		case SINGLE_CENTRAL:
			s = State.INIT;
			break;
		case SINGLE_SIDE:
			timer.cancel();
			s = State.INIT;
			break;
		}
	}

	private void cancelHandler(MotionEvent event) {
		switch (s) {
		case DOUBLE_CENTRAL:
			s = State.INIT;
			break;
		case INIT:
			throw new RuntimeException();
		case SINGLE_CENTRAL:
			s = State.INIT;
			break;
		case SINGLE_SIDE:
			timer.cancel();
			s = State.INIT;
			break;
		}
	}

	private void downHandler(MotionEvent event) {
		switch (s) {
		case DOUBLE_CENTRAL:
			throw new RuntimeException();
		case INIT:
			p1 = event.getPointerId(event.getActionIndex());
			if (isCenter(event.getX(), event.getY())) {
				s = State.SINGLE_CENTRAL;
			} else {
				timer = new Timer();
				TimerTask task = new TimerTask() {

					@Override
					public void run() {
						timerHandler();
					}
				};
				timer.schedule(task, 0, TIMER_TICK);
				s = State.SINGLE_SIDE;
			}
			break;
		case SINGLE_CENTRAL:
			throw new RuntimeException();
		case SINGLE_SIDE:
			throw new RuntimeException();
		}
	}

	private void timerHandler() {
		switch (s) {
		case DOUBLE_CENTRAL:
			throw new RuntimeException();
		case INIT:
			throw new RuntimeException();
		case SINGLE_CENTRAL:
			throw new RuntimeException();
		case SINGLE_SIDE:
			float resX = (x1 - (xMax - xMin) / 2) / (xMax - xMin);
			float resY = (y1 - (yMax - yMin) / 2) / (yMax - yMin);
			long now = SystemClock.uptimeMillis();
			long time = now - t;
			l.moveSide(resX, resY, time);
			t = now;
			break;
		}
	}

	private void update(MotionEvent event) {
		switch (s) {
		case DOUBLE_CENTRAL:
			x1 = event.getX(event.findPointerIndex(p1));
			y1 = event.getY(event.findPointerIndex(p1));
			x2 = event.getX(event.findPointerIndex(p2));
			y2 = event.getY(event.findPointerIndex(p2));
			break;
		case INIT:
			break;
		case SINGLE_CENTRAL:
			x1 = event.getX(event.findPointerIndex(p1));
			y1 = event.getY(event.findPointerIndex(p1));
			break;
		case SINGLE_SIDE:
			x1 = event.getX(event.findPointerIndex(p1));
			y1 = event.getY(event.findPointerIndex(p1));
			t = event.getEventTime();
			break;
		}
	}

	private boolean isCenter(float x, float y) {
		float u = (xMax - xMin) / 2;
		float v = (yMax - yMin) / 2;
		float a = (xMax - xMin) / 3;
		float b = (yMax - yMin) / 3;
		float t = (x - u) * (x - u) / (a * a) + (y - v) * (y - v) / (b * b);
		return t <= 1;
	}

	private float distance(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	private enum State {
		DOUBLE_CENTRAL, INIT, SINGLE_CENTRAL, SINGLE_SIDE
	}
}