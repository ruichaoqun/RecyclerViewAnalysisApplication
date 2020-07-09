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
        if(childWidth == 0){
            calculateChildData();
        }
        fill(recycler,state);
    }

    private void calculateChildData() {
        mAvailable = getHeight() - getPaddingBottom() - getPaddingTop();
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        double decorationX = mItemDecoration*Math.sin(Math.PI/3);
        childWidth = (int) (width/4f-decorationX);
        childHeight = (int) (childWidth*Math.sin(Math.PI/3));
        mLefts = new ArrayList<>();
        mLefts.add(getPaddingLeft());
        mLefts.add((int) (childWidth*3/4+decorationX+mLefts.get(0)));
        mLefts.add((int) (childWidth*3/4+decorationX+mLefts.get(1)));
        mLefts.add((int) (childWidth*3/4+decorationX+mLefts.get(2)));
        mLefts.add((int) (childWidth*3/4+decorationX+mLefts.get(3)));
        for (int i = 0; i < getItemCount(); i++) {
            calculateRect(i);
        }
    }

    private Rect calculateRect(int position) {
        if(mChildRect.containsKey(position)){
            return mChildRect.get(position);
        }
        Rect rect = new Rect();
        int m = position/5;
        int n = position%5;
        rect.left = mLefts.get(n);
        rect.right = rect.left+childWidth;
        rect.top = getPaddingTop() + m * (childHeight + mItemDecoration) + (n % 2 == 0?(childHeight + mItemDecoration)/2:0);
        rect.bottom = rect.top + childHeight;
        mChildRect.put(position,rect);
        return rect;
    }

    private int fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //从上往下布局，item逐渐增加
        mTotalScrollOffset = 0;
        int preAvailable = mAvailable;
        if(getChildCount() > 0){
            View view = getChildAt(0);
            int position = getPosition(view);
            Rect rect = calculateRect(position);
            mTotalScrollOffset =  rect.top - view.getTop();
        }
        recyclerChild(recycler,state);
        if(isFillTopToBottom){
            int position = 0;
            if(getChildCount() != 0){
                position = getPosition(getChildAt(getChildCount()-1))+1;
            }
            while (mAvailable > 0 && position < getItemCount()){
                for (int i = 0; i < 5; i++) {
                    if(position+i >= getItemCount()){
                        break;
                    }
                    View view = recycler.getViewForPosition(position+i);
                    addView(view);
                    measureChild(view,childWidth,childHeight);
                    Rect rect = calculateRect(position+i);
                    view.layout(rect.left,rect.top - mTotalScrollOffset,rect.right,rect.bottom - mTotalScrollOffset);
                }
                mAvailable -= (childHeight + mItemDecoration);
                position+=5;
            }
        }
        if(mAvailable < 0){
            return 0;
        }
        return preAvailable-mAvailable;
    }

    /**
     * 该方法执行添加布局前的回收不可见View功能
     * @param recycler
     * @param state
     */
    private void recyclerChild(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //如果是向上滑，需要从顶部进行view回收
        if(isFillTopToBottom){
            while (getChildCount() > 0 && getChildAt(0).getBottom() - getPaddingTop() < offset){
                //一次性回收第0个到第4个child，
                for (int i = 4; i >= 0; i--) {
                    if(i >= getChildCount()){
                        continue;
                    }
                    removeAndRecycleViewAt(i,recycler);
                }
            }
        }else{
            //如果是向上滑，需要从底部进行view回收
        }
    }

    @Override
    public void measureChild(@NonNull View child, int widthUsed, int heightUsed) {
        child.measure(View.MeasureSpec.makeMeasureSpec(widthUsed, View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(heightUsed, View.MeasureSpec.EXACTLY));
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
        int consumed = fill(recycler,state);
        if(consumed < 0){
            consumed = 0;
        }
        int scroll = delta > consumed?consumed:delta;
        offsetChildrenVertical(-scroll);
        return scroll;
    }

    private void updateLayoutState(int delta, RecyclerView.State state) {
        //上滑
        if(delta > 0){
            int lastPosition = getChildCount() - 1;
            View view = getChildAt(lastPosition);
            //获取最后一个View的bottom
            int center = (view.getBottom() + view.getTop())/2;
            mAvailable = getHeight() - getPaddingBottom() - center + delta;
            isFillTopToBottom = true;
            offset = delta;
//            Log.w("AAAA","mAvailable-->"+mAvailable);
        }else{
        }
    }
}

