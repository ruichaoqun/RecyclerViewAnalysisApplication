package com.youdeyi.recyclerviewanalysisapplication;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.youdeyi.recyclerviewanalysisapplication.widget.RecyclerView;

import java.util.HashMap;

/**
 * @author Rui Chaoqun
 * @date :2020/7/8 9:35
 * description:
 */
public class TestLayoutManager extends RecyclerView.LayoutManager {

    private int mSpanCount = 5;
    private int mItemDecoration = 5;
    /**
     * 当前需要填充的高度
     */
    private int mAvailable;
    private int mTotalScrollOffset;
    private boolean isFillTopToBottom = true;
    private int childWidth;
    private int childHeight;
    private HashMap<Integer, Rect> mChildRect = new HashMap<>();
    private float k;
    private float j;

    public TestLayoutManager(int spanCount) {
        mSpanCount = spanCount;
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
        childWidth = (int) ((width + (mSpanCount-1)*mItemDecoration*Math.sin(Math.PI/3))/((3*mSpanCount-1)/4f));
        childHeight = (int) (childWidth*Math.sin(Math.PI/3));

        k = (float) (childWidth + Math.sin(Math.PI/3)*mItemDecoration+childWidth/2);
        j = (float) (childWidth*3f/4f+Math.sin(Math.PI/3)*mItemDecoration);
//        Log.w("AAAAA",childWidth+"   "+childHeight);
//        Log.w("AAAAA",k+"   "+j);
        for (int i = 0; i < getItemCount(); i++) {
            calculateRect(i);
        }
    }

    private Rect calculateRect(int position) {
        if(mChildRect.containsKey(position)){
            return mChildRect.get(position);
        }
        Rect rect = new Rect();
        int m = position/mSpanCount;
        int n = position%mSpanCount;

        rect.left = (int) (getPaddingLeft()+k*(n/2)+(n%2==0?0:j));
        rect.right = rect.left+childWidth;
        rect.top = getPaddingTop() + m * (childHeight + mItemDecoration) + (n % 2 == 0?(childHeight + mItemDecoration)/2:0);
        rect.bottom = rect.top + childHeight;
        mChildRect.put(position,rect);
//        Log.w("AAAAA",position+"   "+rect.left+"   "+rect.right+"   "+rect.top+"    "+rect.bottom);
        return rect;
    }

    private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //从上往下布局，item逐渐增加
        if(isFillTopToBottom){
//            int position = 0;
//            if(getChildCount() != 0){
//                position = getPosition(getChildAt(getChildCount()-1))+1;
//            }
//            while (mAvailable > 0 && position < getItemCount()){
//
//                View view = recycler.getViewForPosition(position);
//                addView(view);
//                measureChild(view,childWidth,childHeight);
//                Rect rect = calculateRect(position);
//                view.layout(rect.left,rect.top - mTotalScrollOffset,rect.right,rect.bottom - mTotalScrollOffset);
//                if(position % mSpanCount == mSpanCount-1){
//                    mAvailable -= childHeight + mItemDecoration;
//                }
//                position++;
//            }
            for (int i = 0; i < 5; i++) {
                View view = recycler.getViewForPosition(i);
                addView(view);
                measureChild(view,childWidth,childHeight);
                Rect rect = calculateRect(i);
                view.layout(rect.left,rect.top ,rect.right,rect.bottom );
                Log.w("AAAA",view.getMeasuredWidth()+"  "+view.getMeasuredHeight());
            }
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
        return super.scrollVerticallyBy(dy, recycler, state);
    }
}

