package com.youdeyi.recyclerviewanalysisapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.youdeyi.recyclerviewanalysisapplication.widget.GridLayoutManager;
import com.youdeyi.recyclerviewanalysisapplication.widget.LinearLayoutManager;
import com.youdeyi.recyclerviewanalysisapplication.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new TestLayoutManager(5));
        mRecyclerView.setAdapter(new Adapter());
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            PentagonView pentagonView = new PentagonView(MainActivity.this);
            pentagonView.setGravity(Gravity.CENTER);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            pentagonView.setLayoutParams(params);
            return new ViewHolder(pentagonView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ((TextView)holder.itemView).setText(String.valueOf(position+1));
        }

        @Override
        public int getItemCount() {
            return 50;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}