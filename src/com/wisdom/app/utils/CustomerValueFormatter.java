package com.wisdom.app.utils;

import java.text.DecimalFormat;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class CustomerValueFormatter implements ValueFormatter{
	private DecimalFormat mFormat;

    public CustomerValueFormatter() {
        //�˴�����ʾ���ݵķ�ʽ����ʾ���ͻ���С������С��λ���Լ�����ȷ��
        mFormat = new DecimalFormat("0.0000");
    }

	@Override
	public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
		if(value==0)
			return "";
		else
		return mFormat.format(value);//����ǰ���ߺ�ɸ����Լ���Ҫ��ʾ�ķ�ʽ���
	}

}
