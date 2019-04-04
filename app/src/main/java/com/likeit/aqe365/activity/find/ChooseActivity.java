package com.likeit.aqe365.activity.find;

import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.likeit.aqe365.R;
import com.likeit.aqe365.adapter.find.AllFind02Adapter;
import com.likeit.aqe365.base.BaseActivity;
import com.likeit.aqe365.network.model.BaseResponse;
import com.likeit.aqe365.network.model.find.FollowlistModel;
import com.likeit.aqe365.network.model.find.MyDiaryListModel;
import com.likeit.aqe365.network.util.RetrofitUtil;
import com.likeit.aqe365.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscriber;

public class ChooseActivity extends BaseActivity {
    @BindView(R.id.ll_new_riji)
    LinearLayout ll_new_riji;

    private int pageNum = 1;
    private static final int PAGE_SIZE = 1;//为什么是6呢？
    private boolean isErr = true;
    private boolean mLoadMoreEndGone = false; //是否加载更多完毕
    private int mCurrentCounter = 0;
    int TOTAL_COUNTER = 0;
    @BindView(R.id.RecyclerView)
    RecyclerView mRecyclerView;
    //    @BindView(R.id.SwipeRefreshLayout)
//    SwipeRefreshLayout mSwipeRefreshLayout;
    private List<MyDiaryListModel.ListBean> data;
    private MyDiaryListAdatpter mAdapter;
    private MyDiaryListModel myDiaryListModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        setBackView();
        setTitle("选择日记本");
        initData1();
        //  initUI();
    }

    private void initData1() {
        RetrofitUtil.getInstance().MyDiary(openid, String.valueOf(pageNum), new Subscriber<BaseResponse<MyDiaryListModel>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                LoaddingDismiss();
            }

            @Override
            public void onNext(BaseResponse<MyDiaryListModel> baseResponse) {
                LoaddingDismiss();
                if (baseResponse.code == 200) {
                    myDiaryListModel = baseResponse.getData();
                    List<MyDiaryListModel.ListBean> list = myDiaryListModel.getList();
                    data = list;
                    initUI();
                } else {
                    mAdapter.setEmptyView(R.layout.notdata_view);
                }

            }
        });


    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        onRefresh();
//    }

    private void initUI() {
        //    mSwipeRefreshLayout.setOnRefreshListener(this);
        //  mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MyDiaryListAdatpter(R.layout.mydiary_listitem, data);
        // mAdapter.setOnLoadMoreListener(this, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setNewData(data);
        mAdapter.notifyDataSetChanged();

        // initAdapter();
        ll_new_riji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtils.put(mContext,"postFlag","0");
                toActivity(EditDiaryActivity.class);
            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String type = data.get(position).getType();
                String diaryid = data.get(position).getId();
                Bundle bundle = new Bundle();
                SharedPreferencesUtils.put(mContext,"postFlag","1");
                bundle.putString("type", type);
                bundle.putString("diaryid", diaryid);
                toActivity(EditDiary01Activity.class, bundle);
            }
        });
    }

    private void initAdapter() {
        mAdapter = new MyDiaryListAdatpter(R.layout.mydiary_listitem, data);
        // mAdapter.setOnLoadMoreListener(this, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.disableLoadMoreIfNotFullPage();
        //initData(pageNum, false);
        // mCurrentCounter = mAdapter.getData().size();
    }

    private void initData(int pageNum, final boolean isloadmore) {
        LoaddingShow();
        RetrofitUtil.getInstance().MyDiary(openid, String.valueOf(pageNum), new Subscriber<BaseResponse<MyDiaryListModel>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                LoaddingDismiss();
            }

            @Override
            public void onNext(BaseResponse<MyDiaryListModel> baseResponse) {
                LoaddingDismiss();
                if (baseResponse.code == 200) {
                    myDiaryListModel = baseResponse.getData();
                    List<MyDiaryListModel.ListBean> list = myDiaryListModel.getList();
                    if (list != null && list.size() > 0) {
                        if (!isloadmore) {
                            data = list;
                        } else {
                            data.addAll(list);
                        }
                        mAdapter.setNewData(data);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mAdapter.setEmptyView(R.layout.notdata_view);
                    }

                } else {
                    showToast(baseResponse.getMsg());
                }
            }
        });
    }

//    @Override
//    public void onRefresh() {
//        mAdapter.setEnableLoadMore(false);//禁止加载
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // mAdapter.setNewData(data);
//                isErr = false;
//                mCurrentCounter = PAGE_SIZE;
//                pageNum = 1;//页数置为1 才能继续重新加载
//                initData(pageNum, false);
//                mSwipeRefreshLayout.setRefreshing(false);
//                mAdapter.setEnableLoadMore(true);//启用加载
//            }
//        }, 2000);
//    }
//
//    @Override
//    public void onLoadMoreRequested() {
//        TOTAL_COUNTER = Integer.valueOf(myDiaryListModel.getTotal());
//        mRecyclerView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (mCurrentCounter >= TOTAL_COUNTER) {
//                    //数据全部加载完毕
//                    mAdapter.loadMoreEnd();
//                } else {
//                    if (isErr) {
//                        //成功获取更多数据
//                        //  mQuickAdapter.addData(DataServer.getSampleData(PAGE_SIZE));
//                        pageNum += 1;
//                        initData(pageNum, true);
//                        mCurrentCounter = mAdapter.getData().size();
//                        mAdapter.loadMoreComplete();
//                    } else {
//                        //获取更多数据失败
//                        isErr = true;
//                        mAdapter.loadMoreFail();
//
//                    }
//                }
//            }
//
//        }, 3000);
//    }

    public class MyDiaryListAdatpter extends BaseQuickAdapter<MyDiaryListModel.ListBean, BaseViewHolder> {
        public MyDiaryListAdatpter(int layoutResId, List<MyDiaryListModel.ListBean> data) {
            super(R.layout.mydiary_listitem, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, MyDiaryListModel.ListBean item) {
            helper.setText(R.id.tv_title, item.getBooktitle());
            helper.setText(R.id.tv_num, item.getNum() + "篇");
        }
    }

}
