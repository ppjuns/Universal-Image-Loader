
package com.exe.android_universal_image_loader.widget;

import com.exe.android_universal_image_loader.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

public class YWListView extends ListView implements OnScrollListener {

	private float mLastY = -1;
	private Scroller mScroller;
	private OnScrollListener mScrollListener;

	private IXListViewListener mListViewListener;

	private YWListViewHeader mHeaderView;
	private RelativeLayout mHeaderViewContent;
	private TextView mHeaderTimeView;
	private int mHeaderViewHeight;
	private boolean mEnablePullRefresh = true;
	private boolean mPullRefreshing = false; // 是否正在刷新�?

	private YWListViewFooter mFooterView;
	private boolean mEnablePullLoad;	
	private boolean mPullLoading;		//是否正在加载更多
	private boolean mIsFooterReady = false;

	private int mTotalItemCount;

	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;
	private final static int SCROLLBACK_FOOTER = 1;

	private final static int SCROLL_DURATION = 400; 
	private final static int PULL_LOAD_MORE_DELTA = 50; 
	
	private final static float OFFSET_RADIO = 1.8f;
													
	
	
	public YWListView(Context context) {
		super(context);
		initWithContext(context);
	}

	public YWListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}

	public YWListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}

	private void initWithContext(Context context) {
		
		mScroller = new Scroller(context, new DecelerateInterpolator());
		
		super.setOnScrollListener(this);

		// 初始化头�?
		mHeaderView = new YWListViewHeader(context);
		mHeaderViewContent = (RelativeLayout) mHeaderView.findViewById(R.id.ywlistview_header_content);
		mHeaderTimeView = (TextView) mHeaderView.findViewById(R.id.ywlistview_header_time);
		addHeaderView(mHeaderView);

		// 初始化尾�?
		mFooterView = new YWListViewFooter(context);

		// 初始化头部的�?
		mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
				
			new OnGlobalLayoutListener() {
				@SuppressWarnings("deprecation")
				@Override
				public void onGlobalLayout() {
					mHeaderViewHeight = mHeaderViewContent.getHeight();
					getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
			});
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		// 确保加载了Footer,并只能加载一次make 
		if (mIsFooterReady == false) {
			mIsFooterReady = true;
			addFooterView(mFooterView);
		}
		super.setAdapter(adapter);
	}

	//设置是否�?启下拉刷新，默认�?�?
	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		
		if (!mEnablePullRefresh) 
			mHeaderViewContent.setVisibility(View.INVISIBLE);
		else 
			mHeaderViewContent.setVisibility(View.VISIBLE);
		
	}

	//设置是否�?启上拉下载更多，默认关闭，需要手动开�?
	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;
		
		if (!mEnablePullLoad) {
			mFooterView.hide();
			mFooterView.setOnClickListener(null);
		} else {
			mPullLoading = false;
			mFooterView.show();
			mFooterView.setState(YWListViewFooter.STATE_NORMAL);
			
			// 上拉和点击事件都可以触发下载更多事件
			mFooterView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}

	//刷新完成，重设header视图
	public void stopRefresh() {
		if (mPullRefreshing == true) {
			mPullRefreshing = false;
			resetHeaderHeight();
		}
	}

	//加载完成，重设footer视图
	public void stopLoadMore() {
		if (mPullLoading == true) {
			mPullLoading = false;
			mFooterView.setState(YWListViewFooter.STATE_NORMAL);
		}
	}

	//设置上次刷新时间
	public void setRefreshTime(String time) {
		mHeaderTimeView.setText(time);
	}

	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) mScrollListener;
			l.onXScrolling(this);
		}
	}

	private void updateHeaderHeight(float delta) {
		mHeaderView.setVisiableHeight((int) delta + mHeaderView.getVisiableHeight());
		
		if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
			if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
				mHeaderView.setState(YWListViewHeader.STATE_READY);
			} else {
				mHeaderView.setState(YWListViewHeader.STATE_NORMAL);
			}
		}
		
		setSelection(0); //没次刷新后都定位到第�?条item
	}

	private void resetHeaderHeight() {
		int height = mHeaderView.getVisiableHeight();
		
		if (height == 0) return;
		
		if (mPullRefreshing && height <= mHeaderViewHeight)
			return;
		
		int finalHeight = 0; 
		
		if (mPullRefreshing && height > mHeaderViewHeight) {
			finalHeight = mHeaderViewHeight;
		}
		
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);
		invalidate();
	}

	private void updateFooterHeight(float delta) {
		int height = mFooterView.getBottomMargin() + (int) delta;
		
		if (mEnablePullLoad && !mPullLoading) {
			if (height > PULL_LOAD_MORE_DELTA) {
				mFooterView.setState(YWListViewFooter.STATE_READY);
			} else {
				mFooterView.setState(YWListViewFooter.STATE_NORMAL);
			}
		}
		mFooterView.setBottomMargin(height);

		// setSelection(mTotalItemCount - 1); // 滚动到底�?
	}

	private void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();
		
		if (bottomMargin > 0) {
			mScrollBack = SCROLLBACK_FOOTER;
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin, SCROLL_DURATION);
			invalidate();
		}
		
	}

	//�?始下载更�?
	private void startLoadMore() {
		mPullLoading = true;
		mFooterView.setState(YWListViewFooter.STATE_LOADING);
		
		if (mListViewListener != null) {
			mListViewListener.onLoadMore();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;
			
		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - mLastY;
			mLastY = ev.getRawY();
			
			if (getFirstVisiblePosition() == 0 && (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
				// 第一条item处于显示状�?�，header已经显示或�?�已经被拉下
				updateHeaderHeight(deltaY / OFFSET_RADIO);
				invokeOnScrolling();
			} else if (getLastVisiblePosition() == mTotalItemCount - 1 && (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
				// �?后一条item显示，处于上拉或将被上拉状�??
				updateFooterHeight(-deltaY / OFFSET_RADIO);
			}
			break;
			
		default:
			mLastY = -1; 
			
			if (getFirstVisiblePosition() == 0) {
				
				if (mEnablePullRefresh && mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
					mPullRefreshing = true;
					mHeaderView.setState(YWListViewHeader.STATE_REFRESHING);
					if (mListViewListener != null) {
						mListViewListener.onRefresh();
					}
				}
				
				resetHeaderHeight();
			}
			
			if (getLastVisiblePosition() == mTotalItemCount - 1) {
				
				if (mEnablePullLoad && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
					startLoadMore();
				}
				
				resetFooterHeight();
			}
			break;
		}
		
		return super.onTouchEvent(ev);
	}

	@Override
	public void computeScroll() {
		
		if (mScroller.computeScrollOffset()) {
			
			if (mScrollBack == SCROLLBACK_HEADER) {
				mHeaderView.setVisiableHeight(mScroller.getCurrY());
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}
			
			postInvalidate();
			invokeOnScrolling();
		}
		
		super.computeScroll();
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		mTotalItemCount = totalItemCount;
		
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	}

	public void setXListViewListener(IXListViewListener l) {
		mListViewListener = l;
	}

	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	// 继承改接口，监听刷新和下载更多事�?
	public interface IXListViewListener {
		public void onRefresh();

		public void onLoadMore();
	}
}
