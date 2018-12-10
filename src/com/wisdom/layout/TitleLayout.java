package com.wisdom.layout;

import com.wisdom.app.activity.R;

import android.R.color;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TitleLayout extends LinearLayout {
	private TextView titleText;

	public TitleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.top_item, this);
		titleText = (TextView) findViewById(R.id.title_text);
		titleText.setTextColor(Color.BLACK);
	}

	public void setTitleText(String text) {
		titleText.setText(text);
	}
}
