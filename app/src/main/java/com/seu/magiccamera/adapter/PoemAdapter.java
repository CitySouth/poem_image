package com.seu.magiccamera.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seu.magiccamera.R;
import com.seu.magiccamera.activity.ProcessalbumActivity;

import java.util.ArrayList;

/**
 * Created by luoyin on 2017/5/26.
 */

public class PoemAdapter extends RecyclerView.Adapter<PoemAdapter.Holder> {
    private static ListItemClickListener mOnClickListener;


    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }


    @NonNull private ArrayList<String> concepts = new ArrayList<>();
    public PoemAdapter setData(@NonNull ArrayList<String> concepts,ListItemClickListener listener) {
        this.concepts = concepts;
        mOnClickListener = listener;
        notifyDataSetChanged();
        return this;
    }

    @Override public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_concept, parent, false));
    }

    @Override public void onBindViewHolder(Holder holder, int position) {

        holder.tv.setText(concepts.get(position).toString());
    }

    @Override public int getItemCount() {
        return concepts.size();
    }

    static final class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
     TextView tv;

//    @BindView(R.id.probability) TextView probability;

        public Holder(View root) {
            super(root);
            root.setOnClickListener(this);
            tv = (TextView) root.findViewById(R.id.label);
        }
    }
}
