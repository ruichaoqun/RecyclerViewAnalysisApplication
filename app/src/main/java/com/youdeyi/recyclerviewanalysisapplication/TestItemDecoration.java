package com.youdeyi.recyclerviewanalysisapplication;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.youdeyi.recyclerviewanalysisapplication.widget.RecyclerView;

/**
 * @author Rui Chaoqun
 * @date :2020/7/8 11:50
 * description:
 */
public class TestItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int childWidth;
    private int childHeight;
    private int divider;

    public TestItemDecoration(int spanCount,int childWidth,int childHeight,int divider) {
        this.spanCount = spanCount;
        this.childWidth = childWidth;
        this.childHeight = childHeight;
        this.divider = divider;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int index = parent.getChildAdapterPosition(view);
        if(index < spanCount){
            if(index % 2 == 0){
                outRect.top = divider+ (childHeight + divider)/2;
            }else{
                outRect.top = divider;
            }
        }else{
            if(index % spanCount % 2 == 1){
                outRect.top = -(childHeight - divider)/2;
            }else{
                outRect.top = divider;
            }
        }

        if(index % 5 == 0){
            outRect.left = 0;
        }else{
            outRect.left =(int) (-childWidth/4 + Math.sin(Math.PI*2/3)*5);
        }
    }
}

