package com.youdeyi.recyclerviewanalysisapplication;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.youdeyi.recyclerviewanalysisapplication.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author Rui Chaoqun
 * @date :2020/7/8 9:35
 * description:
 */
public class TestLayoutManager extends RecyclerView.LayoutManager {

    private int mItemDecoration = 10;
    /**
     * 当前需要填充的高度
     */
    private int mAvailable;
    private int offset;
    private int mTotalScrollOffset;
    private boolean isFillTopToBottom = true;
    private int childWidth;
    private int childHeight;
    private HashMap<Integer, Rect> mChildRect = new HashMap<>();
    private List<Integer> mLefts;

    public TestLayoutManager() {

    }

    @Override
    public boolean isAutoMeasureEnabled() {
        return true;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }
        if (childWidth == 0) {
            calculateChildData();
        }
        fill(recycler, state);
    }

    private void calculateChildData() {
        mAvailable = getHeight() - getPaddingBottom() - getPaddingTop();
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        double decorationX = mItemDecoration * Math.sin(Math.PI / 3);
        childWidth = (int) (width / 4f - decorationX);
        childHeight = (int) (childWidth * Math.sin(Math.PI / 3));
        mLefts = new ArrayList<>();
        mLefts.add(getPaddingLeft());
        mLefts.add((int) (childWidth * 3 / 4 + decorationX + mLefts.get(0)));
        mLefts.add((int) (childWidth * 3 / 4 + decorationX + mLefts.get(1)));
        mLefts.add((int) (childWidth * 3 / 4 + decorationX + mLefts.get(2)));
        mLefts.add((int) (childWidth * 3 / 4 + decorationX + mLefts.get(3)));
        for (int i = 0; i < getItemCount(); i++) {
            calculateRect(i);
        }
    }

    private Rect calculateRect(int position) {
        if (mChildRect.containsKey(position)) {
            return mChildRect.get(position);
        }
        Rect rect = new Rect();
        int m = position / 5;
        int n = position % 5;
        rect.left = mLefts.get(n);
        rect.right = rect.left + childWidth;
        rect.top = getPaddingTop() + m * (childHeight + mItemDecoration) + (n % 2 == 0 ? (childHeight + mItemDecoration) / 2 : 0);
        rect.bottom = rect.top + childHeight;
        mChildRect.put(position, rect);
        return rect;
    }

    private int fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
        mTotalScrollOffset = 0;
        if (getChildCount() > 0) {
            View view = getChildAt(0);
            int position = getPosition(view);
            Rect rect = calculateRect(position);
            mTotalScrollOffset = rect.top - view.getTop();
        }
        recyclerChild(recycler, state);
        //从上往下布局，item的position逐渐增加
        if (isFillTopToBottom) {
            int position = 0;
            if (getChildCount() != 0) {
                //获取最后一个View
                position = getPosition(getChildAt(getChildCount() - 1)) + 1;
            }
            while (mAvailable > 0 && position < getItemCount()) {
                for (int i = position % 5; i < 5; i++) {
                    if (position + i >= getItemCount()) {
                        break;
                    }
                    View view = recycler.getViewForPosition(position + i);
                    addView(view);
                    measureChild(view, childWidth, childHeight);
                    Rect rect = calculateRect(position + i);
                    view.layout(rect.left, rect.top - mTotalScrollOffset, rect.right, rect.bottom - mTotalScrollOffset);
                }
                if (position % 5 == 0) {
                    mAvailable -= (childHeight + mItemDecoration);
                }
                position += 5;
            }
        }else{
            //从下往上布局，item的position逐渐减少
            int position = -1;
            if(getChildCount() != 0){
                position = getPosition(getChildAt(0)) - 1;
            }
            while (mAvailable > 0 && position >= 0){
                //逆序添加View,假设当前第一个View对应的是item中第10个item
                //依次加入第9.8.7.6.5item
                for (int i = 0; i < 5; i++) {
                    View view = recycler.getViewForPosition(position-i);
                    //从前面加入
                    addView(view,0);
                    measureChild(view, childWidth, childHeight);
                    Rect rect = calculateRect(position - i);
                    view.layout(rect.left, rect.top - mTotalScrollOffset, rect.right, rect.bottom - mTotalScrollOffset);
                }
                mAvailable -= (childHeight + mItemDecoration);
                position += 5;
            }
        }
//        Log.w("AAAAAAA", mAvailable + "    " + offset);
        return mAvailable;
    }

    /**
     * 该方法执行添加布局前的回收不可见View功能
     *
     * @param recycler
     * @param state
     */
    private void recyclerChild(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //如果是向上滑，需要从顶部进行view回收
        if (isFillTopToBottom) {
            //回收最顶部数据时，我们拿第0个View做判断依据，如果第0个View可以回收。则从后往前回收正派View
            while (getChildCount() > 0 && getChildAt(0).getBottom() - getPaddingTop() < offset) {
                //一次性回收第0个到第4个child，
                for (int i = 4; i >= 0; i--) {
                    if (i >= getChildCount()) {
                        continue;
                    }
                    removeAndRecycleViewAt(i, recycler);
                }
            }
        } else {
            //如果是向下滑，需要从底部进行view回收
            //回收最底部View时，我们拿最后一排第0个View做判断依据，如果第0个数据可以回收，则从后往前回收整排
            while (getChildCount() - 1 >= 0 && getChildAt(5*((getChildCount() - 1)/5)).getTop() - (getHeight() - getPaddingBottom()) - (childHeight/2+mItemDecoration) > offset) {
                //一次性回收第0个到第4个child
                //回收lastChildPosition所在的一排的View，从后往前回收
                int lastPosition = getChildCount() - 1;
                Log.w("AAAAAAAAA",lastPosition+"    lastPosition");
                for (int i = lastPosition; i >= 5*(lastPosition/5); i--) {
                    Log.w("AAAAAAAAA",i+"    ");
                    removeAndRecycleViewAt(i, recycler);
                }
            }
        }
    }

    @Override
    public void measureChild(@NonNull View child, int widthUsed, int heightUsed) {
        child.measure(View.MeasureSpec.makeMeasureSpec(widthUsed, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(heightUsed, View.MeasureSpec.EXACTLY));
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return scrollBy(dy, recycler, state);
    }

    private int scrollBy(int delta, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getChildCount() == 0 || delta == 0) {
            return 0;
        }
        updateLayoutState(delta, state);
        int consumed = fill(recycler, state);

        if (consumed < 0) {
            consumed = 0;
        }
        int scroll = 0;
        if(delta > 0){
            scroll = delta - consumed;
            if (consumed >= mAvailable) {
                scroll = consumed < (childHeight / 2 + mItemDecoration) ? delta : delta - (consumed - (childHeight / 2 + mItemDecoration));
            }
        }else{
            scroll = delta + consumed;
            Log.w("AAAAA",delta+"    "+consumed+"    "+mTotalScrollOffset);
            if(mTotalScrollOffset + scroll < 0){
                scroll = -mTotalScrollOffset;
            }
        }

        offsetChildrenVertical(-scroll);
        return scroll;
    }

    private void updateLayoutState(int delta, RecyclerView.State state) {
        //上滑
        if (delta > 0) {
            int lastPosition = getChildCount() - 1;
            //qu最后一个View的对应一排的最左边position
            int lastLeftPosition = 5 * (lastPosition / 5);
            View view = getChildAt(lastLeftPosition);
            //获取最后一个View的bottom
            int center = (view.getBottom() + view.getTop()) / 2;
            mAvailable = getHeight() - getPaddingBottom() - center + delta;
            isFillTopToBottom = true;
            offset = delta;
        } else {
            //下滑
            //获取第0个子View
            View view = getChildAt(0);
            mAvailable = view.getTop() - getPaddingTop() - mItemDecoration - delta;
            isFillTopToBottom = false;
            offset = delta;
        }
    }
}

