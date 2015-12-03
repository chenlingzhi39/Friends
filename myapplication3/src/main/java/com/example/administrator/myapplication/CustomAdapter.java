package com.example.administrator.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2015/9/10.
 */
public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> dataList;
    private OnItemPressedListener onItemPressedListener;
    private static final int VIEW_HEADER = 0;
    private static final int VIEW_ITEM = 1;

    public CustomAdapter(List<String> dataList) {
        this.dataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.radio_button, parent, false);
            return new VHHolder(v);
        } else if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_item, parent, false);

            return new ViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).getButton().setText(getItem(position));
        }
        if (holder instanceof VHHolder) {

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return VIEW_HEADER;
        return VIEW_ITEM;
    }

    private String getItem(int position) {
        return dataList.get(position - 1);
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Button button;

        public ViewHolder(View itemView) {
            super(itemView);
            button = (Button) itemView.findViewById(R.id.item_text);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemPressedListener != null)
                        onItemPressedListener.onItemClick(getPosition());
                }

            });
            button.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemPressedListener.OnItemLongClick(getPosition());
                    return false;
                }
            });
        }

        public Button getButton() {
            return button;
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setOnItemPressedListener(
            OnItemPressedListener onItemPressedListener) {
        this.onItemPressedListener = onItemPressedListener;
    }

    public class VHHolder extends RecyclerView.ViewHolder {
        private RadioButton linear, grid;

        public VHHolder(View itemView) {
            super(itemView);
            linear = (RadioButton) itemView.findViewById(R.id.linear);
            grid = (RadioButton) itemView.findViewById(R.id.grid);
            linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemPressedListener.onLinearClick();
                }
            });
            grid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemPressedListener.onGridClick();
                }
            });
        }

    }

    protected static interface OnItemPressedListener {
        void onItemClick(int position);

        boolean OnItemLongClick(int position);

        void onLinearClick();

        void onGridClick();
    }

}

