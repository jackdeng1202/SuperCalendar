package com.jackdeng.supercalendar;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.widget.AbsListView.CHOICE_MODE_MULTIPLE;
import static android.widget.AbsListView.CHOICE_MODE_SINGLE;

/**
 * 日历显示activity
 *
 * @author jack
 * @Email 1032938431@qq.com
 * 
 */
public class CalendarActivity extends Activity implements View.OnClickListener {

	private GestureDetector gestureDetector = null;
	private CalendarAdapter calV = null;
	private ViewFlipper flipper = null;
	private GridView gridView = null;
	private static int jumpMonth = 0; // 每次滑动，增加或减去一个月,默认为0（即显示当前月）
	private static int jumpYear = 0; // 滑动跨越一年，则增加或者减去一年,默认为0(即当前年)
	private int year_c = 0;
	private int month_c = 0;
	private int day_c = 0;
	private String currentDate = "";
	/** 每次添加gridview到viewflipper中时给的标记 */
	private int gvFlag = 0;
	/** 当前的年月，现在日历顶端 */
	private TextView currentMonth;
	/** 上个月 */
	private ImageView prevMonth;
	/** 下个月 */
	private ImageView nextMonth;
	private boolean isShowPreNextMonthDays;
	private boolean isShowLunar;
	private TextView mCanshowother;
	private String[] specialDay;
	private int CHOOSE_MODE = CHOICE_MODE_SINGLE;
	private ArrayList<String> mutiChooseDatas= new ArrayList<>();
	private TextView mChosemode;
	private TextView mOkBt;
	private TextView mCancelBt;

	public CalendarActivity() {

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
		currentDate = sdf.format(date); // 当期日期
		year_c = Integer.parseInt(currentDate.split("-")[0]);
		month_c = Integer.parseInt(currentDate.split("-")[1]);
		day_c = Integer.parseInt(currentDate.split("-")[2]);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);
		currentMonth = (TextView) findViewById(R.id.currentMonth);
		prevMonth = (ImageView) findViewById(R.id.prevMonth);
		nextMonth = (ImageView) findViewById(R.id.nextMonth);
		mCanshowother = (TextView) findViewById(R.id.canshowother);
		mChosemode = (TextView) findViewById(R.id.chosemode);
		mOkBt = (TextView) findViewById(R.id.tv_okbt);
		mCancelBt = (TextView) findViewById(R.id.tv_cancelbt);
		setListener();

		isShowPreNextMonthDays = false;
		isShowLunar = false;

		specialDay = new String[]{"20170909,110", "20170911,220"};
		gestureDetector = new GestureDetector(this, new MyGestureListener());
		flipper = (ViewFlipper) findViewById(R.id.flipper);
		flipper.removeAllViews();
		calV = new CalendarAdapter(this, getResources(),isShowPreNextMonthDays,isShowLunar,specialDay,jumpMonth, jumpYear, year_c, month_c, day_c);
		addGridView();
		gridView.setAdapter(calV);
		flipper.addView(gridView, 0);
		addTextToTopTextView(currentMonth);

/*		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < calV.getCount(); i++) {
					ViewGroup child = (ViewGroup) gridView.getChildAt(i);
					CheckedTextView textView = (CheckedTextView) child.getChildAt(0);
					String date = textView.getText().toString();
					if (date.equals("9")){
						textView.setChecked(true);
						return;
					}
				}
			}
		},100);*/
	}

	private class MyGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			int gvFlag = 0; // 每次添加gridview到viewflipper中时给的标记
			if (e1.getX() - e2.getX() > 120) {
				// 像左滑动
				enterNextMonth(gvFlag);
				return true;
			} else if (e1.getX() - e2.getX() < -120) {
				// 向右滑动
				enterPrevMonth(gvFlag);
				return true;
			}
			return false;
		}
	}

	/**
	 * 移动到下一个月
	 * 
	 * @param gvFlag
	 */
	private void enterNextMonth(int gvFlag) {
		addGridView(); // 添加一个gridView
		jumpMonth++; // 下一个月

		calV = new CalendarAdapter(this, this.getResources(),isShowPreNextMonthDays,isShowLunar, specialDay,jumpMonth, jumpYear, year_c, month_c, day_c);
		gridView.setAdapter(calV);
		addTextToTopTextView(currentMonth); // 移动到下一月后，将当月显示在头标题中
		gvFlag++;
		flipper.addView(gridView, gvFlag);
		flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
		flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
		flipper.showNext();
		flipper.removeViewAt(0);
	}

	/**
	 * 移动到上一个月
	 * 
	 * @param gvFlag
	 */
	private void enterPrevMonth(int gvFlag) {
		addGridView(); // 添加一个gridView
		jumpMonth--; // 上一个月

		calV = new CalendarAdapter(this, this.getResources(),isShowPreNextMonthDays,isShowLunar,specialDay, jumpMonth, jumpYear, year_c, month_c, day_c);
		gridView.setAdapter(calV);
		gvFlag++;
		addTextToTopTextView(currentMonth); // 移动到上一月后，将当月显示在头标题中
		flipper.addView(gridView, gvFlag);

		flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
		flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
		flipper.showPrevious();
		flipper.removeViewAt(0);
	}

	/**
	 * 添加头部的年份 闰哪月等信息
	 * 
	 * @param view
	 */
	public void addTextToTopTextView(TextView view) {
		StringBuffer textDate = new StringBuffer();
		// draw = getResources().getDrawable(R.drawable.top_day);
		// view.setBackgroundDrawable(draw);
		textDate.append(calV.getShowYear()).append("年").append(calV.getShowMonth()).append("月").append("\t");
		view.setText(textDate);
	}

	private void addGridView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		// 取得屏幕的宽度和高度
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int Width = display.getWidth();
		int Height = display.getHeight();

		gridView = new GridView(this);
		gridView.setNumColumns(7);
		gridView.setColumnWidth(40);
		gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		if (Width == 720 && Height == 1280) {
			gridView.setColumnWidth(40);
		}
		gridView.setGravity(Gravity.CENTER_VERTICAL);
		gridView.setChoiceMode(CHOOSE_MODE);
		// 去除gridView边框
		gridView.setVerticalSpacing(5);
		gridView.setHorizontalSpacing(5);
		gridView.setOnTouchListener(new OnTouchListener() {
			// 将gridview中的触摸事件回传给gestureDetector

			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return CalendarActivity.this.gestureDetector.onTouchEvent(event);
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// TODO Auto-generated method stub
				// 点击任何一个item，得到这个item的日期(排除点击的是上月和下月(点击不响应))
				int startPosition = calV.getStartPositon();
				int endPosition = calV.getEndPosition();
				if (startPosition <= position + 7 && position <= endPosition - 7) {
					String scheduleDay = calV.getDateByClickItem(position).split("\\.")[0]; // 这一天的阳历
					String scheduleYear = calV.getShowYear();
					String scheduleMonth = calV.getShowMonth();

					if (CHOOSE_MODE == CHOICE_MODE_SINGLE){
						unCheckAllItem();

						Toast.makeText(CalendarActivity.this, scheduleYear + "-" + scheduleMonth + "-" + scheduleDay, Toast.LENGTH_SHORT).show();
					}else {
						mutiChooseDatas.add(scheduleYear + "-" + scheduleMonth + "-" + scheduleDay);
					}

					((CheckedTextView)((LinearLayout)arg1).getChildAt(0)).setChecked(true);
				}
			}
		});
		gridView.setLayoutParams(params);
	}

	private void setListener() {
		prevMonth.setOnClickListener(this);
		nextMonth.setOnClickListener(this);
		mCanshowother.setOnClickListener(this);
		mChosemode.setOnClickListener(this);
		mOkBt.setOnClickListener(this);
		mCancelBt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.nextMonth: // 下一个月
			enterNextMonth(gvFlag);
			break;
		case R.id.prevMonth: // 上一个月
			enterPrevMonth(gvFlag);
			break;
		case R.id.canshowother: // 是否显示其他月
			isShowPreNextMonthDays = !isShowPreNextMonthDays;
			calV.setShowOthersMonthDays(isShowPreNextMonthDays);
			break;
		case R.id.chosemode:
			if (CHOOSE_MODE == CHOICE_MODE_SINGLE){
				CHOOSE_MODE = CHOICE_MODE_MULTIPLE;
				mOkBt.setVisibility(View.VISIBLE);
				mCancelBt.setVisibility(View.VISIBLE);
				mutiChooseDatas.clear();
				unCheckAllItem();
			}else {
				CHOOSE_MODE = CHOICE_MODE_SINGLE;
				mOkBt.setVisibility(View.GONE);
				mCancelBt.setVisibility(View.GONE);
			}
			gridView.setChoiceMode(CHOOSE_MODE);
			break;
		case R.id.tv_okbt:
			String temp="";
			for (String str : mutiChooseDatas) {
				temp = temp + str + "\n";
			}
			if (!TextUtils.isEmpty(temp))
				Toast.makeText(CalendarActivity.this, temp.subSequence(0,temp.lastIndexOf("\n")), Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(CalendarActivity.this, "请选择日期", Toast.LENGTH_SHORT).show();

			break;
		case R.id.tv_cancelbt:
			mutiChooseDatas.clear();
			unCheckAllItem();
			break;
		default:
			break;
		}
	}

	private void unCheckAllItem() {
		for (int i = 0; i < calV.getCount(); i++) {
            ViewGroup child = (ViewGroup) gridView.getChildAt(i);
            CheckedTextView textView = (CheckedTextView) child.getChildAt(0);
            textView.setChecked(false);
        }
	}

}