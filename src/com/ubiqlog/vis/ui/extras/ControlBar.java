package com.ubiqlog.vis.ui.extras;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.R.drawable;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.ImageView.ScaleType;

import com.ubiqlog.ui.R;
import com.ubiqlog.vis.ui.extras.DateTimeSelector.DateTimeIntervalSelector;
import com.ubiqlog.vis.ui.extras.DateTimeSelector.DateTimePickerDialog.Type;

/**
 * A linearlayout that contains a Player and a DateTimeIntervalSelector
 * 
 * @author Victor Gugonatu
 * @date 10.2010
 * @version 1.0
 */
public class ControlBar extends LinearLayout {

	/**
	 * The callback used to indicate the state of the Player has been changed. *
	 * Refers to {@link Player Player}
	 */
	public interface OnStateChangedListener {

		public abstract void onStateChanged(SeekBar seekBar, int step);
	}

	/**
	 * The callback used to indicate that the Player is empty and needs data *
	 * Refers to {@link Player Player}
	 */
	public interface OnNeedDataListener {

		public abstract void OnNeedData();
	}

	/**
	 * A framelayout that represents a player
	 * 
	 * @author Victor Gugonatu
	 * @date 10.2010
	 * @version 1.0
	 */
	class Player extends FrameLayout {

		public class State {
			public static final int END = -1;
			public static final int START = -2;
			public static final int ERROR = -3;
			public static final int PLAY = -4;
			public static final int PAUSE = -5;
		}

		private ImageButton playPause_btn = null;
		private ImageButton previous_btn = null;
		private ImageButton next_btn = null;
		private SeekBar seekBar = null;
		private int _sceneDuration = 0;
		private ArrayList<Timer> timers = null;
		private int state = State.START;
		private int playPauseState = State.PAUSE;
		private int lastState = 0;
		private OnStateChangedListener _stateChanged = null;
		private Boolean _invalid = false;

		/**
		 * @param context
		 *            The context the Player is to run in.
		 * @param maxProgress
		 *            The number of scenes.
		 * @param sceneDuration
		 *            How long should a scene last (ms).
		 * @param stateChanged
		 *            How the parent is notified that the state has been
		 *            changed.
		 * @param isInvalid
		 *            The initial state of the Player.
		 */
		public Player(Context context, int maxProgress, int sceneDuration,
				OnStateChangedListener stateChanged, Boolean isInvalid) {

			super(context);

			_invalid = isInvalid;
			_stateChanged = stateChanged;

			seekBar = new SeekBar(context);
			seekBar.setKeyProgressIncrement(1);
			seekBar.setMax(maxProgress);
			seekBar.setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: {
						if (getPlayPauseState() == State.PLAY) {
							pause();
							lastState = State.PLAY;
						} else {
							lastState = State.PAUSE;
						}
					}
						return false;
					case MotionEvent.ACTION_UP: {
						if (lastState == State.PLAY) {
							play();
						}
						lastState = 0;
					}
						return false;
					default:
						return false;
					}

				}
			});

			_sceneDuration = sceneDuration;

			timers = new ArrayList<Timer>();

			Display display = ((Activity) context).getWindowManager()
					.getDefaultDisplay();

			playPause_btn = new ImageButton(context);
			playPause_btn.setImageResource(drawable.ic_media_play);

			playPause_btn.setScaleType(ScaleType.FIT_CENTER);

			playPause_btn.setOnClickListener(mPlayListener);
			playPause_btn.setLayoutParams(new LayoutParams(
					display.getWidth() / 3, LayoutParams.WRAP_CONTENT));
			previous_btn = new ImageButton(context);
			previous_btn.setImageResource(drawable.ic_media_previous);
			previous_btn.setScaleType(ScaleType.FIT_CENTER);

			previous_btn.setOnClickListener(mPreviousListener);
			previous_btn.setLayoutParams(new LayoutParams(
					display.getWidth() / 3, LayoutParams.WRAP_CONTENT));
			next_btn = new ImageButton(context);
			next_btn.setImageResource(drawable.ic_media_next);
			next_btn.setScaleType(ScaleType.FIT_CENTER);

			next_btn.setLayoutParams(new LayoutParams(display.getWidth() / 3,
					LayoutParams.WRAP_CONTENT));
			next_btn.setOnClickListener(mForwardListener);

			seekBar.setPadding(10, 0, 10, 5);
			seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
			LinearLayout layout = new LinearLayout(context);
			layout.setOrientation(LinearLayout.HORIZONTAL);

			layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			layout.setGravity(Gravity.CENTER);
			layout.addView(previous_btn);
			layout.addView(playPause_btn);
			layout.addView(next_btn);
			layout.setPadding(0, 5, 0, 5);
			LinearLayout layoutV = new LinearLayout(context);
			layoutV.setOrientation(LinearLayout.VERTICAL);
			layoutV.addView(layout);
			layoutV.addView(seekBar, new LayoutParams(
					android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.FILL_PARENT,
					Gravity.CENTER));

			addView(layoutV);
		}

		public void setProgress(int progress) {
			if (seekBar != null) {
				seekBar.setProgress(progress);
			}
		}

		public void setMax(int max) {
			if (seekBar != null) {
				seekBar.setMax(max);
			}
		}

		public void setIsInvalid(Boolean invalid) {
			_invalid = invalid;
			if (_invalid) {
				seekBar.setProgress(0);
				seekBar.setMax(0);
				playPause_btn.setImageResource(drawable.ic_media_play);
				playPause_btn.setOnClickListener(mPlayListener);
			}
		}

		public Boolean isInvalid() {
			return _invalid;
		}

		private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				pause();
			}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					_stateChanged.onStateChanged(seekBar, progress);
				}
			}
		};

		private View.OnClickListener mPlayListener = new View.OnClickListener() {
			public void onClick(View v) {
				play();

			}
		};

		private View.OnClickListener mPreviousListener = new View.OnClickListener() {
			public void onClick(View v) {
				pause();
				seekBar.setProgress(0);
				play();

			}
		};

		private View.OnClickListener mForwardListener = new View.OnClickListener() {
			public void onClick(View v) {
				pause();
				seekBar.setProgress(seekBar.getMax());
				play();
			}
		};

		private View.OnClickListener mPauseListener = new View.OnClickListener() {
			public void onClick(View v) {

				pause();
			}
		};

		public int stepForward() {

			if ((seekBar.getMax() - seekBar.getProgress()) == 0) {
				state = State.ERROR;
				pause();
			} else {

				_stateChanged
						.onStateChanged(seekBar, seekBar.getProgress() + 1);
				seekBar.setProgress(seekBar.getProgress() + 1);

				if (seekBar.getProgress() == seekBar.getMax()) {
					state = State.END;
					pause();

				} else {
					state = seekBar.getProgress();
				}

			}

			return state;
		}

		public int getState() {
			return state;
		}

		public void play() {
			if (_invalid) {
				_stateChanged.onStateChanged(seekBar, -1);
			} else {
				playPause_btn.setImageResource(drawable.ic_media_pause);
				playPause_btn.setOnClickListener(mPauseListener);
				playPauseState = State.PLAY;

				_stateChanged.onStateChanged(seekBar, seekBar.getProgress());

				Timer timer = new Timer();
				TimerTask task = new TimerTask() {

					@Override
					public void run() {
						handler.sendEmptyMessage(0);
					}

					private Handler handler = new Handler() {

						@Override
						public void handleMessage(Message msg) {
							stepForward();
						}
					};
				};
				timer.schedule(task, _sceneDuration, _sceneDuration);
				timers.add(timer);
			}
		}

		public void pause() {
			playPause_btn.setImageResource(drawable.ic_media_play);
			playPause_btn.setOnClickListener(mPlayListener);

			playPauseState = State.PAUSE;

			for (Timer t : timers) {
				t.cancel();
				t.purge();
			}
			timers = new ArrayList<Timer>();

		}

		public int getPlayPauseState() {
			return playPauseState;
		}

	}

	private LinearLayout layout = null;
	private DateTimeIntervalSelector _selector = null;
	private Player _player = null;
	private Date _startDate = null;
	private Date _endDate = null;

	/**
	 * @param context
	 *            The context the ControlBar is to run in.
	 * @param maxProgress
	 *            The number of scenes.
	 * @param sceneDuration
	 *            How long should a scene last (ms).
	 * @param stateChanged
	 *            How the parent is notified that the state has been changed.
	 * @param toChanged
	 *            How the parent is notified that the to Date has been changed.
	 * @param showRefresh
	 *            hide or shows the refresh button
	 * @param showPlayer
	 *            hide or shows the refresh button
	 */
	public ControlBar(Context context, int maxProgress, int sceneDuration,
			OnStateChangedListener stateChanged,
			View.OnClickListener toChanged, boolean showRefresh,
			boolean showPlayer) {
		super(context);

		layout = new TableLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);

		// initialise the from and to date with the current date
		Date now = new Date();

		_startDate = new Date(now.getYear(), now.getMonth(), now.getDate(), 0,
				0);
		_endDate = new Date(now.getYear(), now.getMonth(), now.getDate(), 23,
				59);

		_selector = new DateTimeIntervalSelector(context, (Date) _startDate
				.clone(), (Date) _endDate.clone(), Type.DATETIME, dataChanged,
				toChanged, context.getString(R.string.Vis_StartDateTime),
				context.getString(R.string.Vis_EndDateTime).toString(), true,
				showRefresh);

		if (showPlayer) {

			_player = new Player(context, maxProgress, sceneDuration,
					stateChanged, true);

			layout.addView(_player, new TableLayout.LayoutParams(
					android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.FILL_PARENT));
		}
		layout.addView(_selector, new TableLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		layout.setPadding(0, 0, 0, 0);
		this.addView(layout);

	}

	public void setPause() {
		if (_player != null) {
			_player.pause();
		}
	}

	public void setValidAndPlay() {
		if (_player != null) {
			_player.setIsInvalid(false);
			_player.play();
		}
	}

	public Boolean isPlayerInvalid() {
		if (_player != null)
			return _player.isInvalid();
		return false;

	}

	DateTimeIntervalSelector.OnDataChangedListener dataChanged = new DateTimeIntervalSelector.OnDataChangedListener() {

		public void onDataChanged(Date startDate, Date endDate, Type type) {
			if (_player != null) {
				if (startDate != _startDate || endDate != _endDate) {
					_player.setIsInvalid(true);
				}
			}
			_startDate = (Date) startDate.clone();
			_endDate = (Date) endDate.clone();
		}
	};

	public Date getStartDate() {
		return _startDate;
	}

	public Date getEndDate() {
		return _endDate;
	}

	public void setProgress(int progress) {
		if (_player != null) {
			_player.setProgress(progress);
		}
	}

	public void setMax(int max) {
		if (_player != null) {
			_player.setMax(max);
		}
	}

}
