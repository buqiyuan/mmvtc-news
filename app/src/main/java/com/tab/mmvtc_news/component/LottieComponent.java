package com.tab.mmvtc_news.component;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blog.www.guideview.Component;
import com.tab.mmvtc_news.R;
/**
 * Created by 卜启缘 on 2019/10/8.
 */
public class LottieComponent implements Component {

  @Override public View getView(LayoutInflater inflater) {

    LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.layer_lottie, null);
    ll.setOnClickListener(view -> Toast.makeText(view.getContext(), "滑动白色高亮处试试！", Toast.LENGTH_SHORT).show());
    return ll;
  }

  @Override public int getAnchor() {
    return Component.ANCHOR_TOP;
  }

  @Override public int getFitPosition() {
    return Component.FIT_CENTER;
  }

  @Override public int getXOffset() {
    return 0;
  }

  @Override public int getYOffset() {
    return -14;
  }
}
