package com.ben.bencustomerserver.adapter;

import android.view.ViewGroup;
import android.widget.TextView;

import com.ben.bencustomerserver.R;
import com.ben.bencustomerserver.delegate.EaseMessageAdapterDelegate;
import com.ben.bencustomerserver.listener.MessageListItemClickListener;
import com.ben.bencustomerserver.model.BaseMessageModel;
import com.ben.bencustomerserver.model.Direct;


/**
 * 做为对话列表的adapter，继承自{@link EaseBaseDelegateAdapter}
 */
public class EaseMessageAdapter extends EaseBaseDelegateAdapter<BaseMessageModel> {
    public MessageListItemClickListener itemClickListener;

    public EaseMessageAdapter() {}

    @Override
    public int getEmptyLayoutId() {
        return R.layout.ease_layout_empty_list_invisible;
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        EaseAdapterDelegate delegate = getAdapterDelegate(viewType);
        
        if(delegate instanceof EaseMessageAdapterDelegate) {

            ((EaseMessageAdapterDelegate) delegate).setListItemClickListener(itemClickListener);
        }
        return super.getViewHolder(parent, viewType);
    }

    /**
     * 为每个delegate添加item listener和item style
     * @param delegate
     * @return
     */
    @Override
    public EaseBaseDelegateAdapter addDelegate(EaseAdapterDelegate delegate) {
        EaseAdapterDelegate clone = null;
        try {
            clone = (EaseAdapterDelegate) delegate.clone();
            clone.setTag(Direct.RECEIEVE.name());
            //设置点击事件
            if(clone instanceof EaseMessageAdapterDelegate) {
                ((EaseMessageAdapterDelegate) clone).setListItemClickListener(itemClickListener);
            }
            super.addDelegate(clone);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        delegate.setTag(Direct.SEND.name());
        //设置点击事件
        if(delegate instanceof EaseMessageAdapterDelegate) {
            ((EaseMessageAdapterDelegate) delegate).setListItemClickListener(itemClickListener);
        }
        return super.addDelegate(delegate);
    }

    @Override
    public EaseBaseDelegateAdapter setFallbackDelegate(EaseAdapterDelegate delegate) {
        EaseAdapterDelegate clone = null;
        try {
            clone = (EaseAdapterDelegate) delegate.clone();
            clone.setTag(Direct.RECEIEVE.name());
            //设置点击事件
            if(clone instanceof EaseMessageAdapterDelegate) {
                ((EaseMessageAdapterDelegate) clone).setListItemClickListener(itemClickListener);
            }
            super.setFallbackDelegate(clone);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        delegate.setTag(Direct.SEND.name());
        //设置点击事件
        if(delegate instanceof EaseMessageAdapterDelegate) {
            ((EaseMessageAdapterDelegate) delegate).setListItemClickListener(itemClickListener);
        }
        return super.setFallbackDelegate(delegate);
    }

    /**
     * get item message
     * @param position
     * @return
     */
    private BaseMessageModel getItemMessage(int position) {
        if(mData != null && !mData.isEmpty()) {
            return mData.get(position);
        }
        return null;
    }



    /**
     * set item click listener
     * @param itemClickListener
     */
    public void setListItemClickListener(MessageListItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

}
