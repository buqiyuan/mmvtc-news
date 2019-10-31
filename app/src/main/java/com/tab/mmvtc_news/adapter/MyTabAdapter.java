package com.tab.mmvtc_news.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;

import com.tab.mmvtc_news.MyLibrary.verticaltablayout.QTabView;
import com.tab.mmvtc_news.MyLibrary.verticaltablayout.TabAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author 卜启缘
 * @version 1.0
 * @date 2019/10/31 21:22
 */
public class MyTabAdapter implements TabAdapter {

    private final Context context;
   private List<String> titles;

    public MyTabAdapter(Context context,  List<String> titles) {
        this.context = context;
        this.titles = titles;
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public int getBadge(int position) {
        if (position == titles.size()) return position;
        return 0;
    }

    @Override
    public QTabView.TabIcon getIcon(int position) {
        return null;
    }

    @Override
    public QTabView.TabTitle getTitle(int position) {
        return new QTabView.TabTitle.Builder(context)
                .setContent(titles.get(position))
                .setTextColor(Color.BLUE, Color.BLACK)
                .build();
    }

    @Override
    public int getBackground(int position) {
        return 0;
    }
}