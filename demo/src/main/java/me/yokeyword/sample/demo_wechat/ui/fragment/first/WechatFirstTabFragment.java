package me.yokeyword.sample.demo_wechat.ui.fragment.first;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.eventbusactivityscope.EventBusActivityScope;
import me.yokeyword.sample.R;
import me.yokeyword.sample.demo_wechat.adapter.ChatAdapter;
import me.yokeyword.sample.demo_wechat.adapter.TickerAdapter;
import me.yokeyword.sample.demo_wechat.base.BaseMainFragment;
import me.yokeyword.sample.demo_wechat.entity.Chat;
import me.yokeyword.sample.demo_wechat.entity.Ticker;
import me.yokeyword.sample.demo_wechat.event.TabSelectedEvent;
import me.yokeyword.sample.demo_wechat.listener.OnItemClickListener;
import me.yokeyword.sample.demo_wechat.ui.fragment.MainFragment;

/**
 * Created by YoKeyword on 16/6/30.
 */
public class WechatFirstTabFragment extends BaseMainFragment implements SwipeRefreshLayout.OnRefreshListener {
    private Toolbar mToolbar;
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecy;

    private boolean mInAtTop = true;
    private int mScrollTotal;

    private TickerAdapter mAdapter;

    public static WechatFirstTabFragment newInstance() {

        Bundle args = new Bundle();

        WechatFirstTabFragment fragment = new WechatFirstTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wechat_fragment_tab_first, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        mRecy = (RecyclerView) view.findViewById(R.id.recy);

        EventBusActivityScope.getDefault(_mActivity).register(this);

        mToolbar.setTitle(R.string.btc);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        mRefreshLayout.setOnRefreshListener(this);

        mRecy.setLayoutManager(new LinearLayoutManager(_mActivity));
        mRecy.setHasFixedSize(true);
        final int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, getResources().getDisplayMetrics());
        mRecy.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 0, space);
            }
        });

        //TODO:修改成我的适配器
        mAdapter = new TickerAdapter(_mActivity);
        mRecy.setAdapter(mAdapter);

        mRecy.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mScrollTotal += dy;
                if (mScrollTotal <= 0) {
                    mInAtTop = true;
                } else {
                    mInAtTop = false;
                }
            }
        });

//        mAdapter.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
//                // 因为启动的MsgFragment是MainFragment的兄弟Fragment,所以需要MainFragment.start()
//
//                // 也可以像使用getParentFragment()的方式,拿到父Fragment来操作 或者使用 EventBusActivityScope
//              ((MainFragment) getParentFragment()).startBrotherFragment(MsgFragment.newInstance(mAdapter.getMsg(position)));
//            }
//        });

        List<Ticker> tickerList = initDatas();
        mAdapter.setDatas(tickerList);
    }

    private List<Ticker> initDatas() {
        List<Ticker> msgList = new ArrayList<>();

        String[] name = new String[]{"Bitfinex", "OKCoin", "OKEX", "OKEX本周", "OKEX下周", "OKEX季度"};
        double[] volume = new double[]{1,2,3,4,5};
        double[] last = new double[]{1000,2000,3000,4000,5000};
        double[] low = new double[]{10,20,30,40,50};
        double[] high = new double[]{100,200,300,400,500};


        for (int i = 0; i < 6; i++) {
            int index = (int) (Math.random() * 5);
            Ticker ticker = new Ticker();
            ticker.ticker_name = name[i];
            ticker.ticker_volume = volume[index];
            ticker.ticker_last = last[index];
            ticker.ticker_high = high[index];
            ticker.ticker_low = low[index];
            msgList.add(ticker);
        }
        return msgList;
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                //TODO:这里添加更新数据，模拟
                List<Ticker> tickerList = initDatas();
                mAdapter.setDatas(tickerList);

                mRefreshLayout.setRefreshing(false);
            }
        }, 1500);
    }



    /**
     * Reselected Tab
     */
    @Subscribe
    public void onTabSelectedEvent(TabSelectedEvent event) {
        if (event.position != MainFragment.FIRST) return;

        if (mInAtTop) {
            mRefreshLayout.setRefreshing(true);
            onRefresh();
        } else {
            scrollToTop();
        }
    }

    private void scrollToTop() {
        mRecy.smoothScrollToPosition(0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBusActivityScope.getDefault(_mActivity).unregister(this);
    }
}
