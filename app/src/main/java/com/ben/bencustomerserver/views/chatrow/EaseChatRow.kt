package com.ben.bencustomerserver.views.chatrow;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ben.bencustomerserver.R;
import com.ben.bencustomerserver.listener.MessageListItemClickListener;
import com.ben.bencustomerserver.model.BaseMessageModel;
import com.ben.bencustomerserver.model.Direct;

import java.util.Date;

/**
 * base chat row view
 */
public abstract class EaseChatRow extends LinearLayout {
    protected static final String TAG = EaseChatRow.class.getSimpleName();

    protected LayoutInflater inflater;
    protected Context context;
    /**
     * ListView's adapter or RecyclerView's adapter
     */
    protected Object adapter;
    protected BaseMessageModel message;
    /**
     * message's position in list
     */
    protected int position;

    /**
     * timestamp
     */
    protected TextView timeStampView;
    /**
     * avatar
     */
    protected ImageView userAvatarView;
    /**
     * bubble
     */
    protected View bubbleLayout;
    /**
     * nickname
     */
    protected TextView usernickView;
    /**
     * percent
     */
    protected TextView percentageView;
    /**
     * progress
     */
    protected ProgressBar progressBar;
    /**
     * status
     */
    protected ImageView statusView;
    /**
     * if asked
     */
    protected TextView ackedView;
    /**
     * if delivered
     */
    protected TextView deliveredView;
    /**
     * if is sender
     */
    protected boolean isSender;
    /**
     * normal along with {@link #isSender}
     */
    protected boolean showSenderType;
    /**
     * chat message callback
     */
    protected EaseChatCallback chatCallback;
    /**
     * switch to main thread
     */
    private Handler mainThreadHandler;

    protected MessageListItemClickListener itemClickListener;
    private EaseChatRowActionCallback itemActionCallback;
    private EaseReactionView reactionView;

    public EaseChatRow(Context context, boolean isSender) {
        super(context);
        this.context = context;
        this.isSender = isSender;
        this.inflater = LayoutInflater.from(context);

        initView();
    }

    public EaseChatRow(Context context, BaseMessageModel message, int position, Object adapter) {
        super(context);
        this.context = context;
        this.message = message;
        this.isSender = message.getDirect() == Direct.SEND ;
        this.position = position;
        this.adapter = adapter;
        this.inflater = LayoutInflater.from(context);

        initView();
    }

    @Override
    protected void onDetachedFromWindow() {
        itemActionCallback.onDetachedFromWindow();
        super.onDetachedFromWindow();
    }

    private void initView() {
        showSenderType = isSender;

        onInflateView();
        //添加reacionView
        addReactionView();
        //添加threadView
        addThreadView();

        timeStampView = (TextView) findViewById(R.id.timestamp);
        userAvatarView = (ImageView) findViewById(R.id.iv_userhead);
        bubbleLayout = findViewById(R.id.bubble);
        usernickView = (TextView) findViewById(R.id.tv_userid);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        statusView = (ImageView) findViewById(R.id.msg_status);
        ackedView = (TextView) findViewById(R.id.tv_ack);
        deliveredView = (TextView) findViewById(R.id.tv_delivered);

        reactionView = findViewById(R.id.reaction_view);

//        setLayoutStyle();

        mainThreadHandler = new Handler(Looper.getMainLooper());
        onFindViewById();
    }

    private void addThreadView() {
        View view = LayoutInflater.from(context).inflate(R.layout.circle_message_thread, this, false);
        addView(view);
    }

    private void addReactionView() {
        setOrientation(VERTICAL);
        View view = LayoutInflater.from(context).inflate(R.layout.circle_message_reaction, this, false);
        addView(view);
    }

    public void resetViewState() {
        if (null != progressBar) {
            progressBar.setVisibility(GONE);
        }
        if (null != statusView) {
            statusView.setVisibility(GONE);
        }
        if (null != ackedView) {
            ackedView.setVisibility(GONE);
        }
        if (null != deliveredView) {
            deliveredView.setVisibility(GONE);
        }
    }

//    protected void setLayoutStyle() {
//        EaseChatItemStyleHelper helper = getItemStyleHelper();
//        if (helper != null) {
//            EaseChatSetStyle itemStyle = helper.getStyle(context);
//            if (bubbleLayout != null) {
//                try {
//                    if (isSender()) {
//                        Drawable senderBgDrawable = itemStyle.getSenderBgDrawable();
//                        if (senderBgDrawable != null) {
//                            bubbleLayout.setBackground(senderBgDrawable.getConstantState().newDrawable());
//                        }
//                    } else {
//                        Drawable receiverBgDrawable = itemStyle.getReceiverBgDrawable();
//                        if (receiverBgDrawable != null) {
//                            bubbleLayout.setBackground(receiverBgDrawable.getConstantState().newDrawable());
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            if (timeStampView != null) {
//                if (itemStyle.getTimeBgDrawable() != null) {
//                    timeStampView.setBackground(itemStyle.getTimeBgDrawable().getConstantState().newDrawable());
//                }
//                if (itemStyle.getTimeTextSize() != 0) {
//                    timeStampView.setTextSize(TypedValue.COMPLEX_UNIT_PX, itemStyle.getTimeTextSize());
//                }
//                if (itemStyle.getTimeTextColor() != 0) {
//                    timeStampView.setTextColor(itemStyle.getTimeTextColor());
//                }
//            }
//            TextView content = findViewById(R.id.tv_chatcontent);
//            if (content != null) {
//                if (itemStyle.getTextSize() != 0) {
//                    content.setTextSize(TypedValue.COMPLEX_UNIT_PX, itemStyle.getTextSize());
//                }
//                if (itemStyle.getTextColor() != 0) {
//                    content.setTextColor(itemStyle.getTextColor());
//                }
//            }
//        }
//    }

    /**
     * update view
     *
     * @param msg
     */
    public void updateView(final BaseMessageModel msg) {
        if (chatCallback == null) {
            chatCallback = new EaseChatCallback();
        }
        msg.setMessageStatusCallback(chatCallback);
        onViewUpdate(msg);
    }

    /**
     * set property according message and position
     * the method should be called by child
     *
     * @param message
     * @param position
     */
    public void setUpView(BaseMessageModel message, int position,
                          MessageListItemClickListener itemClickListener,
                          EaseChatRowActionCallback itemActionCallback) {
        this.message = message;
        this.position = position;
        this.itemClickListener = itemClickListener;
        this.itemActionCallback = itemActionCallback;

        setUpBaseView();
        onSetUpView();
        onSetUpReactionView();
        onSetupThreadView();
        //setLayoutStyle();
        setClickListener();
    }

    private void onSetupThreadView() {
        if(threadRegion != null) {
            if(shouldShowThreadRegion()) {
                threadRegion.setVisibility(VISIBLE);
                threadRegion.setThreadInfo(message.getChatThread());
            }else {
                threadRegion.setVisibility(GONE);
            }
        }

    }
    /**
     * If need to show thread region
     * @return
     */
    public boolean shouldShowThreadRegion() {
        return message != null && message.getChatThread() != null;
    }

    protected void onSetUpReactionView() {
        if (null == message || null == reactionView) {
            Log.e(TAG, "view is null, don't setup reaction view");
            return;
        }


        reactionView.updateMessageInfo(message);
        reactionView.setOnReactionItemListener(new EaseReactionView.OnReactionItemListener() {
            @Override
            public void removeReaction(EaseReactionEmojiconEntity reactionEntity) {
                if (itemClickListener != null) {
                    itemClickListener.onRemoveReaction(message, reactionEntity);
                }
            }

            @Override
            public void addReaction(EaseReactionEmojiconEntity reactionEntity) {
                if (itemClickListener != null) {
                    itemClickListener.onAddReaction(message, reactionEntity);
                }
            }
        });
        reactionView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (itemClickListener != null) {
                    itemClickListener.onBubbleLongClick(view, message);
                }
            }
        });
    }

    /**
     * set timestamp, avatar, nickname and so on
     */
    private void setUpBaseView() {
        TextView timestamp = (TextView) findViewById(R.id.timestamp);
        if (timestamp != null) {
            setTimestamp(timestamp);
        }
        setItemStyle();
        if (userAvatarView != null) {
            setAvatarAndNick();
        }
        if (EMClient.getInstance().getOptions().getRequireDeliveryAck()) {
            if (deliveredView != null && isSender()) {
                if (message.isDelivered()) {
                    deliveredView.setVisibility(View.VISIBLE);
                } else {
                    deliveredView.setVisibility(View.INVISIBLE);
                }
            }
        }
        if (EMClient.getInstance().getOptions().getRequireAck()) {
            if (ackedView != null && isSender()) {
                if (message.isAcked()) {
                    if (deliveredView != null) {
                        deliveredView.setVisibility(View.INVISIBLE);
                    }
                    ackedView.setVisibility(View.VISIBLE);
                } else {
                    ackedView.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /**
     * set item's style by easeMessageListItemStyle
     */
    private void setItemStyle() {
        EaseChatItemStyleHelper helper = getItemStyleHelper();
        if (helper != null) {
            EaseChatSetStyle itemStyle = helper.getStyle(context);
            if (userAvatarView != null) {
                setAvatarOptions(itemStyle);
            }
            if (usernickView != null) {
                //如果在同一侧展示，则需要显示昵称
                if (itemStyle.getItemShowType() == 1 || itemStyle.getItemShowType() == 2) {
                    usernickView.setVisibility(VISIBLE);
                } else {
                    //如果不在同一侧的话，则根据判断是否显示昵称
                    usernickView.setVisibility((itemStyle.isShowNickname() && message.direct() == Direct.RECEIVE) ? VISIBLE : GONE);
                }
            }
            if (bubbleLayout != null) {
                if (message.getType() == BaseMessageModel.Type.TXT) {
                    if (itemStyle.getItemMinHeight() != 0) {
                        bubbleLayout.setMinimumHeight(itemStyle.getItemMinHeight());
                    }
                }
            }
        }
    }

    private EaseChatItemStyleHelper getItemStyleHelper() {
        return EaseChatItemStyleHelper.getInstance();
    }

    /**
     * set avatar options
     *
     * @param itemStyle
     */
    protected void setAvatarOptions(EaseChatSetStyle itemStyle) {
        if (itemStyle.isShowAvatar()) {
            userAvatarView.setVisibility(View.VISIBLE);
            if (userAvatarView instanceof EaseImageView) {
                EaseImageView avatarView = (EaseImageView) userAvatarView;
                if (itemStyle.getAvatarDefaultSrc() != null) {
                    avatarView.setImageDrawable(itemStyle.getAvatarDefaultSrc());
                }
                avatarView.setShapeType(itemStyle.getShapeType());
                if (itemStyle.getAvatarSize() != 0) {
                    ViewGroup.LayoutParams params = avatarView.getLayoutParams();
                    params.width = (int) itemStyle.getAvatarSize();
                    params.height = (int) itemStyle.getAvatarSize();
                }
                if (itemStyle.getBorderWidth() != 0) {
                    avatarView.setBorderWidth((int) itemStyle.getBorderWidth());
                }
                if (itemStyle.getBorderColor() != 0) {
                    avatarView.setBorderColor(itemStyle.getBorderColor());
                }
                if (itemStyle.getAvatarRadius() != 0) {
                    avatarView.setRadius((int) itemStyle.getAvatarRadius());
                }
            }
            EaseAvatarOptions avatarOptions = provideAvatarOptions();
            if (avatarOptions != null && userAvatarView instanceof EaseImageView) {
                EaseImageView avatarView = ((EaseImageView) userAvatarView);
                if (avatarOptions.getAvatarShape() != 0)
                    avatarView.setShapeType(avatarOptions.getAvatarShape());
                if (avatarOptions.getAvatarBorderWidth() != 0)
                    avatarView.setBorderWidth(avatarOptions.getAvatarBorderWidth());
                if (avatarOptions.getAvatarBorderColor() != 0)
                    avatarView.setBorderColor(avatarOptions.getAvatarBorderColor());
                if (avatarOptions.getAvatarRadius() != 0)
                    avatarView.setRadius(avatarOptions.getAvatarRadius());
            }
        } else {
            userAvatarView.setVisibility(View.GONE);
        }
    }

    /**
     * @return
     */
    protected EaseAvatarOptions provideAvatarOptions() {
        return EaseIM.getInstance().getAvatarOptions();
    }

    /**
     * 是否是发送者
     *
     * @return
     */
    public boolean isSender() {
        return isSender;
    }

    /**
     * set avatar and nickname
     */
    protected void setAvatarAndNick() {
        if (isSender()) {
            EaseUserUtils.setUserAvatar(context, EMClient.getInstance().getCurrentUser(), userAvatarView);
            //只要不是常规布局形式，就展示昵称
            if (EaseChatItemStyleHelper.getInstance().getStyle(context).getItemShowType() != EaseChatMessageListLayout.ShowType.NORMAL.ordinal()) {
                EaseUserUtils.setUserNick(message.getFrom(), usernickView);
            }
        } else {
            EaseUserUtils.setUserAvatar(context, message.getFrom(), userAvatarView);
            EaseUserUtils.setUserNick(message.getFrom(), usernickView);
        }
    }

    /**
     * set timestamp
     *
     * @param timestamp
     */
    protected void setTimestamp(TextView timestamp) {
        if (adapter != null) {
            timestamp.setText(EaseDateUtils.getTimestampString(getContext(), new Date(message.getMsgTime())));
            timestamp.setVisibility(View.VISIBLE);
            // show time stamp if interval with last message is > 30 seconds
            BaseMessageModel prevMessage = null;
            if (adapter instanceof BaseAdapter) {
                prevMessage = (BaseMessageModel) ((BaseAdapter) adapter).getItem(position - 1);
            }
            if (adapter instanceof EaseBaseAdapter) {
                prevMessage = (BaseMessageModel) ((EaseBaseAdapter) adapter).getItem(position - 1);
            }

//            if (prevMessage != null && EaseDateUtils.isCloseEnough(message.getMsgTime(), prevMessage.getMsgTime())) {
//                timestamp.setVisibility(View.GONE);
//            } else {
//                timestamp.setText(EaseDateUtils.getTimestampString(getContext(), new Date(message.getMsgTime())));
//                timestamp.setVisibility(View.VISIBLE);
//            }
        }
    }

    public void setTimestamp(BaseMessageModel preMessage) {
//        if (position == 0) {
//            timeStampView.setText(EaseDateUtils.getTimestampString(getContext(), new Date(message.getMsgTime())));
//            timeStampView.setVisibility(View.VISIBLE);
//        } else {
//            if (preMessage != null && EaseDateUtils.isCloseEnough(message.getMsgTime(), preMessage.getMsgTime())) {
//                timeStampView.setVisibility(View.GONE);
//            } else {
//                timeStampView.setText(EaseDateUtils.getTimestampString(getContext(), new Date(message.getMsgTime())));
//                timeStampView.setVisibility(View.VISIBLE);
//            }
//        }
        timeStampView.setText(EaseDateUtils.getTimestampString(getContext(), new Date(message.getMsgTime())));
        timeStampView.setVisibility(View.VISIBLE);
    }

    /**
     * set click listener
     */
    private void setClickListener() {
        chatCallback = new EaseChatCallback();
        if (bubbleLayout != null) {
            bubbleLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null && itemClickListener.onBubbleClick(message)) {
                        return;
                    }
                    if (itemActionCallback != null) {
                        itemActionCallback.onBubbleClick(message);
                    }
                }
            });

            bubbleLayout.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener != null) {
                        return itemClickListener.onBubbleLongClick(v, message);
                    }
                    return false;
                }
            });
        }

        if (statusView != null) {
            statusView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null && itemClickListener.onResendClick(message)) {
                        return;
                    }
                    if (itemActionCallback != null) {
                        itemActionCallback.onResendClick(message);
                    }
                }
            });
        }

        if (userAvatarView != null) {
            userAvatarView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        if (message.direct() == Direct.SEND) {
                            itemClickListener.onUserAvatarClick(EMClient.getInstance().getCurrentUser());
                        } else {
                            itemClickListener.onUserAvatarClick(message.getFrom());
                        }
                    }
                }
            });
            userAvatarView.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener != null) {
                        if (message.direct() == Direct.SEND) {
                            itemClickListener.onUserAvatarLongClick(EMClient.getInstance().getCurrentUser());
                        } else {
                            itemClickListener.onUserAvatarLongClick(message.getFrom());
                        }
                        return true;
                    }
                    return false;
                }
            });
        }

    }

    /**
     * refresh view when message status change
     */
    protected void onViewUpdate(BaseMessageModel msg) {
        switch (msg.status()) {
            case CREATE:
                onMessageCreate();
                if (itemClickListener != null) {
                    itemClickListener.onMessageCreate(msg);
                }
                break;
            case SUCCESS:
                onMessageSuccess();
                break;
            case FAIL:
                onMessageError();
                break;
            case INPROGRESS:
                onMessageInProgress();
                break;
            default:
                Log.i(TAG, "default");
                break;
        }
    }

    private class EaseChatCallback implements EMCallBack {

        @Override
        public void onSuccess() {
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    onMessageSuccess();
                    if (itemClickListener != null) {
                        itemClickListener.onMessageSuccess(message);
                    }
                }
            });
        }

        @Override
        public void onError(int code, String error) {
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    onMessageError();
                    if (itemClickListener != null) {
                        itemClickListener.onMessageError(message, code, error);
                    }
                }
            });
        }

        @Override
        public void onProgress(int progress, String status) {
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    onMessageInProgress();
                    if (itemClickListener != null) {
                        itemClickListener.onMessageInProgress(message, progress);
                    }
                }
            });
        }
    }

    /**
     * message create status
     */
    protected void onMessageCreate() {
        Log.i(TAG, "onMessageCreate");
    }

    /**
     * message success status
     */
    protected void onMessageSuccess() {
        Log.i(TAG, "onMessageSuccess");
    }

    /**
     * message fail status
     */
    protected void onMessageError() {
        if (ackedView != null) {
            ackedView.setVisibility(INVISIBLE);
        }
        if (deliveredView != null) {
            deliveredView.setVisibility(INVISIBLE);
        }
        Log.e(TAG, "onMessageError");
    }

    /**
     * message in progress status
     */
    protected void onMessageInProgress() {
        Log.i(TAG, "onMessageInProgress");
    }

    /**
     * inflate view, child should implement it
     */
    protected abstract void onInflateView();

    /**
     * find view by id
     */
    protected abstract void onFindViewById();

    /**
     * setup view
     */
    protected abstract void onSetUpView();

    /**
     * row action call back
     */
    public interface EaseChatRowActionCallback {
        /**
         * click resend action
         *
         * @param message
         */
        void onResendClick(BaseMessageModel message);

        /**
         * click bubble layout
         *
         * @param message
         */
        void onBubbleClick(BaseMessageModel message);

        /**
         * when view detach from window
         */
        void onDetachedFromWindow();
    }
}
