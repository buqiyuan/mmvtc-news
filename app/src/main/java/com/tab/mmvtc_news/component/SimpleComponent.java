package com.tab.mmvtc_news.component;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blog.www.guideview.Component;
import com.tab.mmvtc_news.R;

public class SimpleComponent implements Component {

  @Override public View getView(LayoutInflater inflater) {

    LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.layer_frends, null);
    ll.setOnClickListener(view -> Toast.makeText(view.getContext(), "你知道就好，哼~", Toast.LENGTH_SHORT).show());
    return ll;
  }

  @Override public int getAnchor() {
    return Component.ANCHOR_BOTTOM;
  }

  @Override public int getFitPosition() {
    return Component.FIT_END;
  }

  @Override public int getXOffset() {
    return 220;
  }

  @Override public int getYOffset() {
    return 10;
  }
}
