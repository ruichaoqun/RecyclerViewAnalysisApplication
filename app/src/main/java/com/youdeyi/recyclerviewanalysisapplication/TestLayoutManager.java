package com.youdeyi.recyclerviewanalysisapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.youdeyi.recyclerviewanalysisapplication.widget.GridLayoutManager;
import com.youdeyi.recyclerviewanalysisapplication.widget.LinearLayoutManager;
import com.youdeyi.recyclerviewanalysisapplication.widget.RecyclerView;

/**
 * @author Rui Chaoqun
 * @date :2020/7/8 9:35
 * description:
 */
public class TestLayoutManager extends RecyclerView.LayoutManager {

    private int mSpanCount = 5;
    private int mScrollingOffset;

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

    }
}

