package com.example.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.myapplication.R;
import com.example.util.StringUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.daoexample.Record;

/**
 * Created by Administrator on 2016/2/15.
 */
public class RecordViewHolder extends BaseViewHolder<Record> {
    @InjectView(R.id.type)
    TextView type;
    @InjectView(R.id.add_time)
    TextView addTime;
    @InjectView(R.id.content)
    TextView content;
    @InjectView(R.id.image)
    ImageView image;

    public RecordViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_record);
        ButterKnife.inject(this, itemView);
    }

    @Override
    public void setData(Record data) {
        switch(data.getType()){
            case "post":
                type.setHint("作品");
                break;
            case "comment":
                type.setHint("评论");
                break;
            case "reply":
                type.setHint("回复");
                break;
        }
        addTime.setHint(StringUtils.friendly_time(data.getAdd_time()));
        content.setText(data.getContent());
        if(data.getImage()!=null)
            imageLoader.displayImage(data.getImage(),image);
    }
}
