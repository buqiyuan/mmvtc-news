package com.tab.mmvtc_news.LostAndFound.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.tab.mmvtc_news.LostAndFound.bean.LostInfomationReq;
import com.tab.mmvtc_news.R;

import java.util.List;

/**
 * Created by 卜启缘 on 2019/10/19.
 */

public class LostAndFoundAdapter extends RecyclerView.Adapter<LostAndFoundAdapter.LostAndFoundHolder> {
    private Context mContext;
    private List<LostInfomationReq> lostInfosData;
    private ItemClickListener mItemClickListener;
    public final static int EDIT_CODE = 998;
    public final static int DELETE_CODE = 997;
private String name = "";
    public LostAndFoundAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<LostInfomationReq> data) {
        this.lostInfosData = data;
    }

    @Override
    public LostAndFoundHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        name = mContext.getSharedPreferences("user", mContext.MODE_PRIVATE).getString("name", "");
        View view = LayoutInflater.from(mContext).inflate(R.layout.lost_item, parent, false);
        LostAndFoundHolder lostAndFoundHolder = new LostAndFoundHolder(view);
        return lostAndFoundHolder;
    }

    @Override
    public void onBindViewHolder(final LostAndFoundHolder holder, final int position) {
        if (lostInfosData != null) {
            LostInfomationReq lostInfomationReq = lostInfosData.get(position);
            holder.title.setText("标题："+lostInfomationReq.getTitle());
            holder.desc.setText("描述："+lostInfomationReq.getDesc());
            holder.phoneNum.setText("联系方式："+lostInfomationReq.getPhoneNum());
            holder.time.setText("时间："+lostInfomationReq.getCreatedAt());
            holder.tvWho.setText(TextUtils.isEmpty(lostInfomationReq.getUsername()) ? "" : "我");

            holder.llItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //mLongClickListener.onLongClick(position);
//                    判断是否是当前登录用户
                    if (!TextUtils.isEmpty(name) && String.valueOf(lostInfomationReq.getUsername()).equals(name)) {
                        showWindow(holder, position);
                    }
                    return false;
                }
            });
        }
    }

    private void showWindow(LostAndFoundHolder holder, final int pos) {
        //加载布局文件
        View contentview = LayoutInflater.from(mContext).inflate(R.layout.pop_window_view, null);
        final PopupWindow popupWindow = new PopupWindow(contentview, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        //设置焦点
        popupWindow.setFocusable(true);
        //触摸框外
        popupWindow.setOutsideTouchable(true);
        //点击空白处的时候让PopupWindow消失
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        //设置偏移量
        popupWindow.showAsDropDown(holder.time, 300, -100);

        //showAsDropDown(View anchor)：相对某个控件的位置（正左下方），无偏移
        // showAsDropDown(View anchor, int xoff, int yoff)：相对某个控件的位置，有偏移
        //showAtLocation(View parent, int gravity, int x, int y)：相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移

        //点击编辑按钮
        contentview.findViewById(R.id.edit_btn).setOnClickListener(v -> {
            //回调给主界面，进行数据操作
            mItemClickListener.onEditOrDeleteClick(pos, EDIT_CODE);
            //销毁弹出框
            popupWindow.dismiss();
        });

        //点击删除按钮
        contentview.findViewById(R.id.delete_btn).setOnClickListener(v -> {
            //回调给主界面，进行数据操作
            mItemClickListener.onEditOrDeleteClick(pos, DELETE_CODE);
            //销毁弹出框
            popupWindow.dismiss();
        });
    }

    @Override
    public int getItemCount() {
        return lostInfosData.size() == 0 ? 0 : lostInfosData.size();
    }

    public class LostAndFoundHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView desc;
        private TextView time;
        private TextView phoneNum;
        private TextView tvWho;
        private LinearLayout llItem;

        public LostAndFoundHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            desc = (TextView) itemView.findViewById(R.id.tv_desc);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            phoneNum = (TextView) itemView.findViewById(R.id.tv_num);
            tvWho = (TextView) itemView.findViewById(R.id.tv_who);
            llItem = (LinearLayout) itemView.findViewById(R.id.ll_item);
        }
    }

    //设置长按事件
    public void setLongClickListener(ItemClickListener clickListener) {
        this.mItemClickListener = clickListener;
    }

    public interface ItemClickListener {
        void onEditOrDeleteClick(int position, int code);
    }
}
