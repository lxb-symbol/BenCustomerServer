package com.ben.bencustomerserver.delegate;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.ben.bencustomerserver.adapter.EaseAdapterDelegate;
import com.ben.bencustomerserver.listener.MessageListItemClickListener;
import com.ben.bencustomerserver.model.Direct;
import com.ben.bencustomerserver.viewholder.EaseChatRowViewHolder;
import com.ben.bencustomerserver.views.chatrow.EaseChatRow;


/**
 * 本类设计的目的是做为对话消息代理类的基类，添加了对话代理类特有的方法
 * @param <T>
 * @param <VH>
 */
public abstract class EaseMessageAdapterDelegate<T, VH extends EaseChatRowViewHolder> extends EaseAdapterDelegate<T, VH> {
    private MessageListItemClickListener mItemClickListener;

    public EaseMessageAdapterDelegate() {}

    public EaseMessageAdapterDelegate(MessageListItemClickListener itemClickListener) {
        this();
        this.mItemClickListener = itemClickListener;
    }





    @Override
    public VH onCreateViewHolder(ViewGroup parent, String tag) {
        EaseChatRow view = getEaseChatRow(parent, isSender(tag));
        return createViewHolder(view, mItemClickListener);
    }

    private boolean isSender(String tag) {
        return !TextUtils.isEmpty(tag) && TextUtils.equals(tag, Direct.SEND.toString());
    }

    protected abstract EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender);

    protected abstract VH createViewHolder(View view, MessageListItemClickListener itemClickListener);

    public void setListItemClickListener(MessageListItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

}
