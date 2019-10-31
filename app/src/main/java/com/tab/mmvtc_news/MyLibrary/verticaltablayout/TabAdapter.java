package com.tab.mmvtc_news.MyLibrary.verticaltablayout;


/**
 * Created by 卜启缘 on 2019/10/30.
 */
public interface TabAdapter {
    int getCount();

    int getBadge(int position);

    QTabView.TabIcon getIcon(int position);

    QTabView.TabTitle getTitle(int position);

    int getBackground(int position);
}
