package com.seu.magiccamera.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.seu.magiccamera.R;

/**
 * Created by nan on 2017/5/27.
 */

public class TextCustomMenuAdapter extends RecyclerView.Adapter<TextCustomMenuAdapter.Holder> {
    private OnMenuChangeLister onMenuChangeLister;
    public interface OnMenuChangeLister {
        void onMenuChanged();
    }
    public void setOnMenuChangeLister(OnMenuChangeLister onMenuChangeLister) {
        this.onMenuChangeLister = onMenuChangeLister;
    }
    @Override
    public TextCustomMenuAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TextCustomMenuAdapter.Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.textcustom_menu_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(TextCustomMenuAdapter.Holder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class Holder extends RecyclerView.ViewHolder {

        ImageView colorimg;

        public Holder(View itemView) {
            super(itemView);
        }

    }
}
