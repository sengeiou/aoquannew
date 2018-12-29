package com.likeits.simple.adapter.div_provider.home;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chaychan.adapter.BaseItemProvider;
import com.likeits.simple.R;
import com.likeits.simple.network.model.home.MainHomeListmenuModel;
import com.likeits.simple.utils.StringUtil;

import java.util.List;

public class MainListMenuItemProvider extends BaseItemProvider<MainHomeListmenuModel, BaseViewHolder> {
    MainHomeListmenuModel.StyleBean styleBean;

    @Override
    public int viewType() {
        return MainHomeAdapter.TYPE_LISTMENU;
    }

    @Override
    public int layout() {
        return R.layout.main_home_listmenu;
    }

    @Override
    public void convert(BaseViewHolder helper, final MainHomeListmenuModel data, int position) {
        styleBean = data.getStyle();
        RecyclerView mRecyclerView = helper.getView(R.id.mRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        MainListMenuAdapter mAdapter = new MainListMenuAdapter(R.layout.main_home_listmenu_items, data.getData());
        mRecyclerView.setAdapter(mAdapter);
    }

    public class MainListMenuAdapter extends BaseQuickAdapter<MainHomeListmenuModel.DataBean, BaseViewHolder> {
        public MainListMenuAdapter(int layoutResId, List<MainHomeListmenuModel.DataBean> data) {
            super(R.layout.main_home_listmenu_items, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, MainHomeListmenuModel.DataBean item) {
            LinearLayout linearLayout = helper.getView(R.id.listmenu_bg);
            Typeface iconTypeface = Typeface.createFromAsset(mContext.getAssets(), "iconfont.ttf");
            TextView tvLeftPic = helper.getView(R.id.tv_left_pic);
            TextView tvLeftTitle = helper.getView(R.id.tv_left_title);
            TextView tvRightTitle = helper.getView(R.id.tv_right_title);
            tvLeftPic.setTypeface(iconTypeface);
            if ("".equals(item.getIconclasscode())) {
                tvLeftPic.setText("");
            } else {
                tvLeftPic.setText(StringUtil.decode("\\u" + item.getIconclasscode()));
            }
            tvLeftPic.setTextColor(Color.parseColor(styleBean.getIconcolor()));
            tvLeftTitle.setText(item.getText());
            tvRightTitle.setText(item.getRemark());
            tvLeftTitle.setTextColor(Color.parseColor(styleBean.getTextcolor()));
            tvRightTitle.setTextColor(Color.parseColor(styleBean.getRemarkcolor()));
            linearLayout.setBackgroundColor(Color.parseColor(styleBean.getBackground()));

        }
    }

}
