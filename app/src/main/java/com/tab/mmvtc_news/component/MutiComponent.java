package com.tab.mmvtc_news.component;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blog.www.guideview.Component;
import com.tab.mmvtc_news.R;
/**
 * Created by 卜启缘 on 2019/10/8.
 */
public class MutiComponent implements Component {

    @Override
    public View getView(LayoutInflater inflater) {
        LinearLayout ll = new LinearLayout(inflater.getContext());
        LinearLayout.LayoutParams param =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(param);
        TextView textView = new TextView(inflater.getContext());
        textView.setTextColor(inflater.getContext().getResources().getColor(R.color.white));
        textView.setText("点击相应的课程可查看详细的课程详情哦！");
        textView.setTextSize(20);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//        textView.setEms(1);
        ImageView imageView = new ImageView(inflater.getContext());
        imageView.setImageResource(R.mipmap.arrow);
        ll.removeAllViews();
        ll.addView(textView);
        ll.addView(imageView);
        ll.setOnClickListener(view -> Toast.makeText(view.getContext(), "引导层被点击了", Toast.LENGTH_SHORT).show());
        return ll;
    }

    @Override
    public int getAnchor() {
        return Component.ANCHOR_BOTTOM;
    }

    @Override
    public int getFitPosition() {
        return Component.FIT_CENTER;
    }

    @Override
    public int getXOffset() {
        return 0;
    }

    @Override
    public int getYOffset() {
        return 0;
    }
}
