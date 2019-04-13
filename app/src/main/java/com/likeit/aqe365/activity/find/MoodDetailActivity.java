package com.likeit.aqe365.activity.find;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.elvishew.xlog.XLog;
import com.likeit.aqe365.R;
import com.likeit.aqe365.base.BaseActivity;
import com.likeit.aqe365.fragment.find.CommentDialogFragment;
import com.likeit.aqe365.fragment.find.MoodCommentFragment;
import com.likeit.aqe365.network.model.BaseResponse;
import com.likeit.aqe365.network.model.EmptyEntity;
import com.likeit.aqe365.network.model.find.MoodDetailsModel;
import com.likeit.aqe365.network.util.RetrofitUtil;
import com.likeit.aqe365.utils.SharedPreferencesUtils;
import com.likeit.aqe365.view.BorderRelativeLayout;
import com.likeit.aqe365.view.BorderTextView;
import com.likeit.aqe365.view.CircleImageView;
import com.likeit.aqe365.view.IconfontTextView;
import com.likeit.aqe365.view.RatioImageView;
import com.likeit.aqe365.view.photoview.ViewPagerActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscriber;

public class MoodDetailActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener, OnItemClickListener, MoodCommentFragment.MyDialogFragment_Listener {
    private int pageNum = 1;
    private static final int PAGE_SIZE = 1;//为什么是6呢？
    private boolean isErr = true;
    private int mCurrentCounter = 0;
    int TOTAL_COUNTER = 0;

    @BindView(R.id.RecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.SwipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.tv_isgood)
    IconfontTextView tv_isgood;
    @BindView(R.id.tv_views)
    IconfontTextView tv_views;
    @BindView(R.id.tv_iscollect)
    IconfontTextView tv_iscollect;
    @BindView(R.id.tv_back)
    BorderRelativeLayout tv_back;
    @BindView(R.id.tv_share)
    BorderRelativeLayout tv_share;
    private String id;
    private List<MoodDetailsModel.PostCommentBean> data;
    private MoodDetailsModel moodDetailsModel;
    private PostDeatilAdapter mAdapter;
    private ConvenientBanner mBanner;
    private CircleImageView iv_avatar;
    private TextView tv_name, tv_content, tv_title, tv_content01, tv_hospitalName, tv_hospital_title, tv_hospital_content;
    private BorderTextView tv_attention;
    private RatioImageView iv_hospital_pic;
    private String isuser;
    private String bid;
    private String pid;
    private String iscollect;
    private MoodCommentFragment dialog;
    private LinearLayout ll_userInfo;
    private TextView tv_comment_num;
    private View header;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_detail);
        id = getIntent().getExtras().getString("id");
        initUI();
    }

    private void initUI() {
        data = new ArrayList<>();
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initAdapter();
    }

    private void initAdapter() {
        header = LayoutInflater.from(this).inflate(R.layout.postdetails_header, null);
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int h_screen = dm.heightPixels;
        int w_screen = dm.widthPixels;
        mBanner = header.findViewById(R.id.banner);
        mBanner.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, w_screen));
        mBanner.startTurning(4000);
        iv_avatar = header.findViewById(R.id.iv_avatar);
        ll_userInfo = header.findViewById(R.id.ll_userInfo);
        tv_name = header.findViewById(R.id.tv_name);
        tv_content = header.findViewById(R.id.tv_content);
        tv_title = header.findViewById(R.id.tv_title);
        tv_content01 = header.findViewById(R.id.tv_content01);
        tv_hospitalName = header.findViewById(R.id.tv_hospitalName);
        tv_hospital_title = header.findViewById(R.id.tv_hospital_title);
        tv_hospital_content = header.findViewById(R.id.tv_hospital_content);
        iv_hospital_pic = header.findViewById(R.id.iv_hospital_pic);
        tv_attention = header.findViewById(R.id.tv_attention);
        tv_comment_num = header.findViewById(R.id.tv_comment_num);
        mAdapter = new PostDeatilAdapter(R.layout.postdetails_comment_items, data);
        mAdapter.setOnLoadMoreListener(this, mRecyclerView);
        mAdapter.addHeaderView(header);
        mAdapter.setHeaderAndEmpty(true);
        mAdapter.setHeaderFooterEmpty(true, true);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.disableLoadMoreIfNotFullPage();
        initData(pageNum, false);
        mCurrentCounter = mAdapter.getData().size();
        tv_isgood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_isgood.setText(getResources().getString(R.string.ic_good) + "  " + (Integer.valueOf(moodDetailsModel.getPost().getLikenum()) + 1));
                tv_isgood.setTextColor(Color.parseColor("#ff424d"));
                bid = moodDetailsModel.getPost().getId();
                pid = "";
                toLike(pid, bid);
            }
        });

        tv_iscollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ("0".equals(iscollect)) {
                    tv_iscollect.setText(getResources().getString(R.string.ic_collect));
                    tv_iscollect.setTextColor(Color.parseColor("#FFCC00"));
                    iscollect = "1";
                } else {
                    tv_iscollect.setText(getResources().getString(R.string.ic_iscollect));
                    tv_iscollect.setTextColor(Color.parseColor("#656565"));
                    iscollect = "0";
                }
                collectpost();
            }


        });
        tv_views.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = moodDetailsModel.getPost().getId();
                String bid = moodDetailsModel.getPost().getBid();
                String rpid = "";
                String mpid = "";
                dialog = new MoodCommentFragment();
                Bundle bundle = new Bundle();
                bundle.putString("bid", bid);
                bundle.putString("pid", id);
                bundle.putString("rpid", rpid);
                bundle.putString("mpid", mpid);
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "tag");
            }
        });
        ll_userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("id", moodDetailsModel.getPost().getMemberid());
                bundle.putString("isuser", moodDetailsModel.getPost().getIsuser());
                bundle.putString("avatar", moodDetailsModel.getPost().getAvatar());
                bundle.putString("name", moodDetailsModel.getPost().getNickname());

                SharedPreferencesUtils.put(mContext, "avatar", moodDetailsModel.getPost().getAvatar());
                SharedPreferencesUtils.put(mContext, "name", moodDetailsModel.getPost().getNickname());
                toActivity(UserInfoActivity.class, bundle);
            }
        });
    }


    private void initData(int pageNum, final boolean isloadmore) {
        //   id = "20";

        RetrofitUtil.getInstance().mooddetails(openid, id, String.valueOf(pageNum), new Subscriber<BaseResponse<MoodDetailsModel>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(BaseResponse<MoodDetailsModel> baseResponse) {
                XLog.e("data" + baseResponse.getData().getPost().getNickname());
                XLog.e("getLikenum" + (Integer.valueOf(baseResponse.getData().getPost().getLikenum()) + 1));
                if (baseResponse.code == 200) {
                    moodDetailsModel = baseResponse.getData();
                    initHeader();
                    if ("1".equals(moodDetailsModel.getPost().getIslike())) {
                        tv_isgood.setText(getResources().getString(R.string.ic_good) + " " + moodDetailsModel.getPost().getLikenum());
                        tv_isgood.setTextColor(Color.parseColor("#ff424d"));
                        tv_isgood.setClickable(false);
                    } else {
                        tv_isgood.setText(getResources().getString(R.string.ic_isgood) + " " + moodDetailsModel.getPost().getLikenum());
                        tv_isgood.setTextColor(Color.parseColor("#656565"));
                        tv_isgood.setClickable(true);
                    }
                    tv_views.setText(getResources().getString(R.string.ic_comment) + " " + moodDetailsModel.getPost().getReplycount());
                    tv_views.setTextColor(Color.parseColor("#656565"));
                    tv_comment_num.setText(moodDetailsModel.getPost().getReplycount() + "条");
                    iscollect = moodDetailsModel.getPost().getIscollect();
                    if ("1".equals(iscollect)) {
                        tv_iscollect.setText(getResources().getString(R.string.ic_collect));
                        tv_iscollect.setTextColor(Color.parseColor("#FFCC00"));
                    } else if ("0".equals(iscollect)) {
                        tv_iscollect.setText(getResources().getString(R.string.ic_iscollect));
                        tv_iscollect.setTextColor(Color.parseColor("#656565"));
                    }

                    List<MoodDetailsModel.PostCommentBean> list = moodDetailsModel.getPostComment();

                    if (list != null && list.size() > 0) {
                        if (!isloadmore) {
                            data = list;
                        } else {
                            data.addAll(list);
                        }
                        TOTAL_COUNTER = Integer.valueOf(list.size());
                        mAdapter.setNewData(data);
                        mAdapter.notifyDataSetChanged();
                    } else {

                        mAdapter.setEmptyView(R.layout.notdata_view);
                    }

                } else {
                    showProgress(baseResponse.getMsg());
                }
            }
        });
    }

    /**
     * 头部
     * private BorderRelativeLayout tv_back, tv_share;
     * private CircleImageView iv_avatar;
     * private TextView tv_name, tv_content, tv_title, tv_content01, tv_hospitalName, tv_hospital_title, tv_hospital_content;
     * private BorderTextView tv_attention;
     * private RotateImageView iv_hospital_pic;
     */
    private void initHeader() {
        initBanner();
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare("");
            }
        });

        ImageLoader.getInstance().displayImage(moodDetailsModel.getPost().getAvatar(), iv_avatar);
        tv_name.setText(moodDetailsModel.getPost().getNickname());
        tv_content.setText(moodDetailsModel.getPost().getCity() + " " + moodDetailsModel.getPost().getCreatetime());
        tv_title.setText(moodDetailsModel.getPost().getTitle());
        tv_content01.setText(moodDetailsModel.getPost().getContent());
        tv_hospitalName.setText("测试医院");
        tv_hospital_title.setText("服务标题");
        tv_hospital_content.setText("¥ " + 65.00);
        // ImageLoader.getInstance().displayImage(iv_hospital_pic,getResources().getDrawable(R.mipmap.icon_default_picture));
        isuser = moodDetailsModel.getPost().getIsuser();

        if ("0".equals(isuser)) {
            tv_attention.setContentColorResource01(Color.parseColor(theme_bg_tex));
            tv_attention.setStrokeColor01(Color.parseColor(theme_bg_tex));
            tv_attention.setTextColor(Color.parseColor("#ffffff"));
            tv_attention.setText("+ 关注");
        } else {
            tv_attention.setContentColorResource01(Color.parseColor("#FFFFFF"));
            tv_attention.setStrokeColor01(Color.parseColor("#DBDBDB"));
            tv_attention.setTextColor(Color.parseColor("#DBDBDB"));
            tv_attention.setText("已关注");
        }
        tv_attention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("0".equals(isuser)) {
                    isuser = "1";
                    tv_attention.setContentColorResource01(Color.parseColor("#FFFFFF"));
                    tv_attention.setStrokeColor01(Color.parseColor("#DBDBDB"));
                    tv_attention.setTextColor(Color.parseColor("#DBDBDB"));
                    tv_attention.setText("已关注");
                } else {
                    isuser = "0";
                    tv_attention.setContentColorResource01(Color.parseColor(theme_bg_tex));
                    tv_attention.setStrokeColor01(Color.parseColor(theme_bg_tex));
                    tv_attention.setTextColor(Color.parseColor("#ffffff"));
                    tv_attention.setText("+ 关注");
                }
                attention();
            }
        });
    }

    List<String> adList;
    ArrayList<String> items = new ArrayList<>();

    private void initBanner() {
        XLog.e("image:" + moodDetailsModel.getPost().getImages().get(0));
        adList = moodDetailsModel.getPost().getImages();
        if (adList != null && adList.size() > 0) {
            for (int i = 0; i < adList.size(); i++) {
                items.add(adList.get(i));
            }
        }
        mBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        }, adList).setPageIndicator(new int[]{R.drawable.indicator_gray, R.drawable.indicator_red}).setOnItemClickListener(this).setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL).setScrollDuration(1500);

    }

    @Override
    public void getDataFrom_DialogFragment(String Data01) {
        onRefresh();
    }

    public class NetworkImageHolderView implements Holder<String> {
        private RatioImageView imageView;

        @Override
        public View createView(Context context) {

            //找到布局填充器
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //找到整个xml布局
            LinearLayout rl = (LinearLayout) inflater.inflate(R.layout.banner_item, null);
            //通过找到xml布局来找控件
            imageView = (RatioImageView) rl.findViewById(R.id.iv_banner);
            // imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return rl;
        }

        @Override
        public void UpdateUI(Context context, int position, String data) {
            ImageLoader.getInstance().displayImage(data, imageView);
            //  Glide.with(mContext).load(data).into(imageView);
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(mContext, ViewPagerActivity.class);
        intent.putStringArrayListExtra("items", items);
        intent.putExtra("currentPosition", position);
        mContext.startActivity(intent);

    }

    @Override
    public void onRefresh() {
        mAdapter.setEnableLoadMore(false);//禁止加载
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // mAdapter.setNewData(data);
                isErr = false;
                mCurrentCounter = PAGE_SIZE;
                pageNum = 1;//页数置为1 才能继续重新加载
                initData(pageNum, false);
                mSwipeRefreshLayout.setRefreshing(false);
                mAdapter.setEnableLoadMore(true);//启用加载
            }
        }, 2000);
    }

    @Override
    public void onLoadMoreRequested() {

        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mCurrentCounter >= TOTAL_COUNTER) {
                    //数据全部加载完毕
                    mAdapter.loadMoreEnd();
                } else {
                    if (isErr) {
                        //成功获取更多数据
                        //  mQuickAdapter.addData(DataServer.getSampleData(PAGE_SIZE));
                        pageNum += 1;
                        initData(pageNum, true);
                        mCurrentCounter = mAdapter.getData().size();
                        mAdapter.loadMoreComplete();
                    } else {
                        //获取更多数据失败
                        isErr = true;
                        mAdapter.loadMoreFail();

                    }
                }
            }

        }, 3000);
    }

    public class PostDeatilAdapter extends BaseQuickAdapter<MoodDetailsModel.PostCommentBean, BaseViewHolder> {

        public PostDeatilAdapter(int layoutResId, List<MoodDetailsModel.PostCommentBean> data) {
            super(R.layout.postdetails_comment_items, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, final MoodDetailsModel.PostCommentBean item) {
            ImageLoader.getInstance().displayImage(item.getAvatar(), (CircleImageView) helper.getView(R.id.iv_avatar));
            final IconfontTextView tv_isgood = helper.getView(R.id.tv_isgood);
            name=item.getNickname();
            helper.setText(R.id.tv_title, item.getNickname());
            helper.setText(R.id.tv_content, item.getContent());
            helper.setText(R.id.tv_time, item.getCreatetime());
            if ("1".equals(item.getIsgood())) {
                tv_isgood.setText(getResources().getString(R.string.ic_good) + "  " + item.getLikenum());
                tv_isgood.setTextColor(Color.parseColor("#ff424d"));
                tv_isgood.setClickable(false);
            } else {
                tv_isgood.setText(getResources().getString(R.string.ic_isgood) + "  " + item.getLikenum());
                tv_isgood.setTextColor(Color.parseColor("#cccccc"));
                tv_isgood.setClickable(true);
            }
            IconfontTextView tv_views = helper.getView(R.id.tv_views);
            tv_views.setText("回复");

            RecyclerView mRecycleView = helper.getView(R.id.mRecyclerView);
            PostDetailCommentAdapter mAdapter = new PostDetailCommentAdapter(R.layout.postdetails_comment02_items, item.getParent());
            mRecycleView.setLayoutManager(new LinearLayoutManager(mContext));
            mRecycleView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            tv_isgood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_isgood.setText(getResources().getString(R.string.ic_good) + "  " + (Integer.valueOf(item.getLikenum()) + 1));
                    tv_isgood.setTextColor(Color.parseColor("#ff424d"));
                    /**
                     * 点赞
                     */
                    pid = item.getId(); //话题id
                    bid = moodDetailsModel.getPost().getId();//帖子id
                    toLike(pid, bid);
                }
            });
            tv_views.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = item.getId();
                    String bid = item.getBid();
                    String pid = moodDetailsModel.getPost().getId();
                    String rpid = "";
                    String mpid = "";
                    dialog = new MoodCommentFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("bid", bid);
                    bundle.putString("pid", pid);
                    bundle.putString("rpid", rpid);
                    bundle.putString("mpid", mpid);
                    dialog.setArguments(bundle);
                    dialog.show(getSupportFragmentManager(), "tag");
                }
            });
        }
    }


    public class PostDetailCommentAdapter extends BaseQuickAdapter<MoodDetailsModel.PostCommentBean.ParentBean, BaseViewHolder> {
        public PostDetailCommentAdapter(int layoutResId, List<MoodDetailsModel.PostCommentBean.ParentBean> data) {
            super(R.layout.postdetails_comment02_items, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, final MoodDetailsModel.PostCommentBean.ParentBean item) {
            ImageLoader.getInstance().displayImage(item.getAvatar(), (CircleImageView) helper.getView(R.id.iv_avatar));
            final IconfontTextView tv_isgood = helper.getView(R.id.tv_isgood);
            helper.setText(R.id.tv_title, item.getNickname()+"  回复  "+name);
            helper.setText(R.id.tv_content, item.getContent());
            helper.setText(R.id.tv_time, item.getCreatetime());

            if ("1".equals(item.getIsgood())) {
                tv_isgood.setText(getResources().getString(R.string.ic_good) + "  " + item.getLikenum());
                tv_isgood.setTextColor(Color.parseColor("#ff424d"));
                tv_isgood.setClickable(false);
            } else {
                tv_isgood.setText(getResources().getString(R.string.ic_isgood) + "  " + item.getLikenum());
                tv_isgood.setTextColor(Color.parseColor("#cccccc"));
                tv_isgood.setClickable(true);
            }
            tv_isgood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_isgood.setText(getResources().getString(R.string.ic_good) + "  " + (Integer.valueOf(item.getLikenum()) + 1));
                    tv_isgood.setTextColor(Color.parseColor("#ff424d"));
                    /**
                     * 点赞
                     */
                    pid = item.getId(); //话题id
                    bid = moodDetailsModel.getPost().getId();//帖子id
                    toLike(pid, bid);
                }
            });
            IconfontTextView tv_views = helper.getView(R.id.tv_views);
            tv_views.setText("回复");
            tv_views.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String bid = item.getId();
                    String pid = moodDetailsModel.getPost().getId();
                    String rpid = item.getRpid();
                    String mpid = item.getMpid();
                    dialog = new MoodCommentFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("bid", bid);
                    bundle.putString("pid", pid);
                    bundle.putString("rpid", rpid);
                    bundle.putString("mpid", mpid);
                    dialog.setArguments(bundle);
                    dialog.show(getSupportFragmentManager(), "tag");
                }
            });
        }
    }

    /**
     * 关注
     */
    private void attention() {
        RetrofitUtil.getInstance().Followmember(openid, moodDetailsModel.getPost().getMemberid(), new Subscriber<BaseResponse<EmptyEntity>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(BaseResponse<EmptyEntity> baseResponse) {
                if (baseResponse.getCode() == 200) {
                    showToast(baseResponse.getMsg());
                } else {
                    showToast(baseResponse.getMsg());
                }
            }
        });
    }

    /**
     * 点赞
     *
     * @param pid
     * @param bid
     */
    private void toLike(String pid, String bid) {
        RetrofitUtil.getInstance().moodlike(openid, bid, pid, new Subscriber<BaseResponse<EmptyEntity>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(BaseResponse<EmptyEntity> baseResponse) {
                if (baseResponse.getCode() == 200) {
                    showToast(baseResponse.getMsg());
                }
            }
        });
    }

    /**
     * 收藏
     */
    private void collectpost() {
        RetrofitUtil.getInstance().collectmood(openid, moodDetailsModel.getPost().getId(), new Subscriber<BaseResponse<EmptyEntity>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(BaseResponse<EmptyEntity> baseResponse) {
                if (baseResponse.getCode() == 200) {
                    showToast(baseResponse.getMsg());
                } else {
                    showToast(baseResponse.getMsg());
                }
            }
        });
    }
}
