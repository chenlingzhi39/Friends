package com.example.widget.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.example.adapter.RecyclerArrayAdapter;
import com.example.administrator.myapplication.R;
import com.example.refreshlayout.RefreshLayout;


public class EasyRecyclerView extends FrameLayout {

    protected RecyclerView mRecycler;
    protected FrameLayout mProgressView;
    protected FrameLayout mEmptyView;
    protected FrameLayout mErrorView;
    private int mProgressId;
    private int mEmptyId;
    private int mErrorId;

    protected boolean mClipToPadding;
    protected int mPadding;
    protected int mPaddingTop;
    protected int mPaddingBottom;
    protected int mPaddingLeft;
    protected int mPaddingRight;
    protected int mScrollbarStyle;


    protected RecyclerView.OnScrollListener mInternalOnScrollListener;
    protected RecyclerView.OnScrollListener mExternalOnScrollListener;

    protected RefreshLayout mPtrLayout;
    protected FastScroller fastScroller;
    protected boolean is_refresh=true;

    public RefreshLayout getSwipeToRefresh() {
        return mPtrLayout;
    }

    public RecyclerView getRecyclerView() {
        return mRecycler;
    }

    public EasyRecyclerView(Context context) {
        super(context);
        initView();
    }

    public EasyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initView();
    }

    public EasyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(attrs);
        initView();
    }

    protected void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.superrecyclerview);
        try {
            mClipToPadding = a.getBoolean(R.styleable.superrecyclerview_recyclerClipToPadding, false);
            mPadding = (int) a.getDimension(R.styleable.superrecyclerview_recyclerPadding, -1.0f);
            mPaddingTop = (int) a.getDimension(R.styleable.superrecyclerview_recyclerPaddingTop, 0.0f);
            mPaddingBottom = (int) a.getDimension(R.styleable.superrecyclerview_recyclerPaddingBottom, 0.0f);
            mPaddingLeft = (int) a.getDimension(R.styleable.superrecyclerview_recyclerPaddingLeft, 0.0f);
            mPaddingRight = (int) a.getDimension(R.styleable.superrecyclerview_recyclerPaddingRight, 0.0f);
            mScrollbarStyle = a.getInt(R.styleable.superrecyclerview_scrollbarStyle, -1);
            mEmptyId = a.getResourceId(R.styleable.superrecyclerview_layout_empty, 0);
            mProgressId = a.getResourceId(R.styleable.superrecyclerview_layout_progress, 0);
            mErrorId = a.getResourceId(R.styleable.superrecyclerview_layout_error, 0);

        } finally {
            a.recycle();
        }
    }

    private void initView() {
        if (isInEditMode()) {
            return;
        }
        //生成主View
        View v = LayoutInflater.from(getContext()).inflate(R.layout.layout_progress_recyclerview, this);
        mPtrLayout = (RefreshLayout) v.findViewById(R.id.ptr_layout);
        mProgressView = (FrameLayout) v.findViewById(R.id.progress);
        if (mProgressId != 0) {
            LayoutInflater.from(getContext()).inflate(mProgressId, mProgressView, true);
        }
        mEmptyView = (FrameLayout) v.findViewById(R.id.empty);
        if (mEmptyId != 0) LayoutInflater.from(getContext()).inflate(mEmptyId, mEmptyView, true);
        mErrorView = (FrameLayout) v.findViewById(R.id.error);
        if (mErrorId != 0) LayoutInflater.from(getContext()).inflate(mErrorId, mErrorView, true);
        initRecyclerView(v);
    }



    public void setRecyclerPadding(int left, int top, int right, int bottom) {
        this.mPaddingLeft = left;
        this.mPaddingTop = top;
        this.mPaddingRight = right;
        this.mPaddingBottom = bottom;
        mRecycler.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
    }

    public void setEmptyView(View emptyView) {
        mEmptyView.removeAllViews();
        mEmptyView.addView(emptyView);
    }

    public void setProgressView(View progressView) {
        mProgressView.removeAllViews();
        mProgressView.addView(progressView);
    }

    public void setErrorView(View errorView) {
        mErrorView.removeAllViews();
        mErrorView.addView(errorView);
    }

    public void setEmptyView(int emptyView) {
        mEmptyView.removeAllViews();
        LayoutInflater.from(getContext()).inflate(emptyView, mEmptyView, true);
    }

    public void setProgressView(int progressView) {
        mProgressView.removeAllViews();
        LayoutInflater.from(getContext()).inflate(progressView, mProgressView, true);
    }

    public void setErrorView(int errorView) {
        mErrorView.removeAllViews();
        LayoutInflater.from(getContext()).inflate(errorView, mErrorView, true);
    }

    public void scrollToPosition(int position) {
        getRecyclerView().scrollToPosition(position);
    }

    /**
     * Implement this method to customize the AbsListView
     */
    protected void initRecyclerView(View view) {
        mRecycler = (RecyclerView) view.findViewById(android.R.id.list);
        fastScroller = (FastScroller) view.findViewById(R.id.fast_scroller);


     /*   if (mRecycler != null) {
            mRecycler.setHasFixedSize(true);
            mRecycler.setClipToPadding(mClipToPadding);
            mInternalOnScrollListener = new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (mExternalOnScrollListener != null)
                        mExternalOnScrollListener.onScrolled(recyclerView, dx, dy);

                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (mExternalOnScrollListener != null)
                        mExternalOnScrollListener.onScrollStateChanged(recyclerView, newState);

                }
            };
            mRecycler.addOnScrollListener(mInternalOnScrollListener);

            if (mPadding != -1.0f) {
                mRecycler.setPadding(mPadding, mPadding, mPadding, mPadding);
            } else {
                mRecycler.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
            }

            if (mScrollbarStyle != -1) {
                mRecycler.setScrollBarStyle(mScrollbarStyle);
            }
        }*/

    }
public void setFastScroller(){
    fastScroller.attachToRecyclerView(mRecycler);
}

    /**
     * Set the layout manager to the recycler
     *
     * @param manager
     */
    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        mRecycler.setLayoutManager(manager);
    }

    /**
     * 设置适配器，关闭所有副view。展示recyclerView
     * 适配器有更新，自动关闭所有副view。根据条数判断是否展示EmptyView
     *
     * @param adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        showRecycler();
        mRecycler.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                update();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                update();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                update();
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                update();
            }

            @Override
            public void onChanged() {
                super.onChanged();
                update();
            }

            private void update() {
                if (mRecycler.getAdapter() instanceof RecyclerArrayAdapter) {
                    if (((RecyclerArrayAdapter) mRecycler.getAdapter()).getCount() == 0)
                        showEmpty();
                    else showRecycler();
                } else {
                    if (mRecycler.getAdapter().getItemCount() == 0) showEmpty();
                    else showRecycler();
                }
            }
        });
        if (adapter == null || adapter.getItemCount() == 0) {
            showEmpty();
        }
    }

    /**
     * 设置适配器，关闭所有副view。展示进度条View
     * 适配器有更新，自动关闭所有副view。根据条数判断是否展示EmptyView
     *
     * @param adapter
     */
    public void setAdapterWithProgress(RecyclerView.Adapter adapter) {
        if (adapter instanceof RecyclerArrayAdapter) {
            if (((RecyclerArrayAdapter) adapter).getCount() == 0) showProgress();
            else showRecycler();
        } else {
            if (adapter.getItemCount() == 0) showProgress();
            else showRecycler();
        }

        mRecycler.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                update();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                update();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                update();
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                update();
            }

            @Override
            public void onChanged() {
                super.onChanged();
                update();
            }

            private void update() {
                Log.i("recycler", "update");
                if (mRecycler.getAdapter().getItemCount() == 0) {
                    showEmpty();
                } else {
                    showRecycler();
                }
            }
        });
    }

    /**
     * Remove the adapter from the recycler
     */
    public void clear() {
        mRecycler.setAdapter(null);
    }


    private void hideAll() {
        mEmptyView.setVisibility(View.GONE);
        mProgressView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
        mPtrLayout.setVisibility(View.GONE);
        mRecycler.setVisibility(View.GONE);
    }

    public void setRefreshEnabled(boolean is_refresh) {
        this.is_refresh = is_refresh;
        mPtrLayout.setEnabled(is_refresh);
    }

    public void setHeaderEnabled(boolean headerEnabled) {
        mPtrLayout.setHeaderEnable(headerEnabled);
    }

    public void setFooterEnabled(boolean footerEnabled) {
        mPtrLayout.setFooterEnable(footerEnabled);
    }

    public void showError() {
        hideAll();
        mErrorView.setVisibility(View.VISIBLE);

    }

    public void showEmpty() {
        hideAll();
        mEmptyView.setVisibility(View.VISIBLE);

    }


    public void showProgress() {
        hideAll();
        mProgressView.setVisibility(View.VISIBLE);

    }


    public void showRecycler() {
        hideAll();
        mRecycler.setVisibility(View.VISIBLE);
        mPtrLayout.setVisibility(View.VISIBLE);
        mPtrLayout.setEnabled(is_refresh);
    }

    public void setHeaderRefreshing(boolean header) {
        mPtrLayout.setHeaderRefreshing(header);
    }

    public void setFooterRefreshing(boolean footer) {
        mPtrLayout.setFooterRefreshing(footer);
    }

    /**
     * Set the listener when refresh is triggered and enable the SwipeRefreshLayout
     *
     * @param listener
     */
    public void setRefreshListener(RefreshLayout.OnRefreshListener listener) {
        mPtrLayout.setEnabled(true);
        mPtrLayout.setOnRefreshListener(listener);
    }

    /**
     * Set the colors for the SwipeRefreshLayout states
     *
     * @param colRes1
     * @param colRes2
     * @param colRes3
     * @param colRes4
     */
    public void setHeaderRefreshingColorResources(@ColorRes int colRes1, @ColorRes int colRes2, @ColorRes int colRes3, @ColorRes int colRes4) {
        mPtrLayout.setHeaderColorSchemeResources(colRes1, colRes2, colRes3, colRes4);
    }

    public void setFooterRefrehingColorResources(@ColorRes int colRes1, @ColorRes int colRes2, @ColorRes int colRes3, @ColorRes int colRes4) {
        mPtrLayout.setFooterColorSchemeResources(colRes1, colRes2, colRes3, colRes4);
    }

    /**
     * Set the colors for the SwipeRefreshLayout states
     *
     * @param col1
     * @param col2
     * @param col3
     * @param col4
     */
    public void setHeaderRefreshingColor(int col1, int col2, int col3, int col4) {
        mPtrLayout.setHeaderColorSchemeColors(col1, col2, col3, col4);
    }

    public void setFooterRefreshingColor(int col1, int col2, int col3, int col4) {
        mPtrLayout.setFooterColorSchemeColors(col1, col2, col3, col4);
    }

    /**
     * Set the scroll listener for the recycler
     *
     * @param listener
     */
    public void setOnScrollListener(RecyclerView.OnScrollListener listener) {
       // mExternalOnScrollListener = listener;
        mRecycler.addOnScrollListener(listener);
    }

    /**
     * Add the onItemTouchListener for the recycler
     *
     * @param listener
     */
    public void addOnItemTouchListener(RecyclerView.OnItemTouchListener listener) {
        mRecycler.addOnItemTouchListener(listener);
    }

    /**
     * Remove the onItemTouchListener for the recycler
     *
     * @param listener
     */
    public void removeOnItemTouchListener(RecyclerView.OnItemTouchListener listener) {
        mRecycler.removeOnItemTouchListener(listener);
    }

    /**
     * @return the recycler adapter
     */
    public RecyclerView.Adapter getAdapter() {
        return mRecycler.getAdapter();
    }


    public void setOnTouchListener(OnTouchListener listener) {
        mRecycler.setOnTouchListener(listener);
    }

    public void setItemAnimator(RecyclerView.ItemAnimator animator) {
        mRecycler.setItemAnimator(animator);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecycler.addItemDecoration(itemDecoration);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration, int index) {
        mRecycler.addItemDecoration(itemDecoration, index);
    }

    public void removeItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecycler.removeItemDecoration(itemDecoration);
    }


    /**
     * @return inflated progress view or null
     */
    public View getProgressView() {
        return mProgressView;
    }


    /**
     * @return inflated empty view or null
     */
    public View getEmptyView() {
        return mEmptyView;
    }


    public static enum LAYOUT_MANAGER_TYPE {
        LINEAR,
        GRID,
        STAGGERED_GRID
    }

}
