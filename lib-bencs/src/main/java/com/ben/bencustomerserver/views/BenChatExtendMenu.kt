package com.ben.bencustomerserver.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.ben.bencustomerserver.utils.DensityUtil.dp2px
import com.ben.bencustomerserver.adapter.BenChatExtendMenuAdapter
import com.ben.bencustomerserver.adapter.BenChatExtendMenuIndicatorAdapter
import com.ben.bencustomerserver.listener.BenChatExtendMenuItemClickListener
import com.ben.bencustomerserver.listener.IChatExtendMenu
import com.ben.bencustomerserver.listener.OnItemClickListener
import com.ben.bencustomerserver.views.chatextend.HorizontalPageLayoutManager
import com.ben.bencustomerserver.views.chatextend.PagingScrollHelper
import com.ben.bencustomerserver.views.chatextend.PagingScrollHelper.onPageChangeListener
import com.ben.bencustommerserver.R
import java.util.Collections
import java.util.Objects
import kotlin.math.ceil

/**
 * Extend menu when user want send image, voice clip, etc
 *
 * 发送 地址，文件，图片，等功能的扩展
 */
class BenChatExtendMenu
    : FrameLayout, onPageChangeListener,
    IChatExtendMenu,
    OnItemClickListener {
    constructor(context: Context?) : this(context!!, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context!!, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, def: Int) : super(context!!, attrs, def) {
        myInit(context, attrs)
    }


    private val itemModels: MutableList<ChatMenuItemModel> = ArrayList()
    private val itemMap: MutableMap<Int?, ChatMenuItemModel?> = HashMap()
    private val itemStrings = intArrayOf(
        R.string.attach_take_pic,
        R.string.attach_picture,
        R.string.attach_video,
        R.string.attach_file,
    )
    private val itemdrawables = intArrayOf(
        R.drawable.ben_chat_takepic_selector,
        R.drawable.ben_chat_image_selector,
        R.drawable.em_chat_video_selector,
        R.drawable.em_chat_file_selector,
    )
    private val itemIds = intArrayOf(
        R.id.extend_item_take_picture,
        R.id.extend_item_picture,
        R.id.extend_item_video,
        R.id.extend_item_file
    )
    private var rvExtendMenu: RecyclerView? = null
    private var rvIndicator: RecyclerView? = null
    private var adapter: BenChatExtendMenuAdapter? = null
    private var numColumns = 0
    private var numRows = 0
    private var currentPosition = 0
    private var helper: PagingScrollHelper? = null
    private var indicatorAdapter: BenChatExtendMenuIndicatorAdapter? = null
    private var itemListener: BenChatExtendMenuItemClickListener? = null

    private fun myInit(context: Context?, attrs: AttributeSet?) {
        initAttr(context!!, attrs)
        initLayout()
    }

    private fun initAttr(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.BenChatExtendMenu)
        numColumns = ta.getInt(R.styleable.BenChatExtendMenu_numColumns, 4)
        numRows = ta.getInt(R.styleable.BenChatExtendMenu_numRows, 1)
        ta.recycle()
    }

    private fun initLayout() {
        LayoutInflater.from(context).inflate(R.layout.ben_layout_chat_extend_menu, this)
        rvExtendMenu = findViewById(R.id.rv_extend_menu)
        rvIndicator = findViewById(R.id.rv_indicator)
    }

    /**
     * init
     */
    fun init() {
        initChatExtendMenu()
        initChatExtendMenuIndicator()
        addDefaultData()
    }

    private fun initChatExtendMenu() {
        val manager = HorizontalPageLayoutManager(numRows, numColumns)
        manager.setItemHeight(dp2px(context!!, 90f).toInt())
        rvExtendMenu!!.layoutManager = manager
        rvExtendMenu!!.setHasFixedSize(true)
        val concatAdapter = ConcatAdapter()
        adapter = BenChatExtendMenuAdapter()
        concatAdapter.addAdapter(adapter!!)
        rvExtendMenu!!.adapter = concatAdapter
        adapter!!.setData(itemModels)
        helper = PagingScrollHelper()
        helper?.let {
            it.setUpRecycleView(rvExtendMenu)
            it.updateLayoutManger()
            it.scrollToPosition(0)
            it.setOnPageChangeListener(this@BenChatExtendMenu)
        }
        isHorizontalFadingEdgeEnabled = true
        adapter!!.setOnItemClickListener(this)
    }

    private fun initChatExtendMenuIndicator() {
        indicatorAdapter = BenChatExtendMenuIndicatorAdapter()
        rvIndicator!!.adapter = indicatorAdapter
        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
        Objects.requireNonNull(
            ResourcesCompat.getDrawable(
                resources, R.drawable.ben_chat_extend_menu_indicator_divider, context!!.theme
            )
        ).let {
            it?.let {
                itemDecoration.setDrawable(it)
            }

        }
        rvIndicator!!.addItemDecoration(itemDecoration)
        indicatorAdapter!!.setSelectedPosition(currentPosition)
    }

    private fun addDefaultData() {
        for (i in itemStrings.indices) {
            registerMenuItem(itemStrings[i], itemdrawables[i], itemIds[i], null)
        }
    }

    /**
     * 清空数据
     */
    override fun clear() {
        itemModels.clear()
        itemMap.clear()
        adapter!!.notifyDataSetChanged()
        indicatorAdapter!!.setPageCount(0)
    }

    override fun setMenuOrder(itemId: Int, order: Int) {
        if (itemMap.containsKey(itemId)) {
            val model = itemMap[itemId]
            if (model != null) {
                model.order = order
                sortByOrder(itemModels)
                adapter!!.notifyDataSetChanged()
            }
        }
    }


    /**
     * register menu item
     *
     * @param name        item name
     * @param drawableRes background of item
     * @param itemId      id
     * @param listener    on click event of item
     */
    fun registerMenuItem(
        name: String? = "",
        drawableRes: Int = 0,
        itemId: Int = 0,
        listener: BenChatExtendMenuItemClickListener? = null
    ) {
        if (!itemMap.containsKey(itemId)) {
            val item = ChatMenuItemModel()
            item.name = name
            item.image = drawableRes
            item.id = itemId
            item.clickListener = listener
            itemMap[itemId] = item
            itemModels.add(item)
            adapter!!.notifyItemInserted(itemModels.size - 1)
            //设置需要显示的indicator的个数
            indicatorAdapter!!.setPageCount(
                ceil((itemModels.size * 1.0f / (numColumns * numRows)).toDouble()).toInt()
            )
        }
    }

    /**
     * register menu item
     *
     * @param name        item name
     * @param drawableRes background of item
     * @param itemId      id
     * @param order       order by
     * @param listener    on click event of item
     */
    fun registerMenuItem(
        name: String?,
        drawableRes: Int,
        itemId: Int,
        order: Int,
        listener: BenChatExtendMenuItemClickListener?
    ) {
        if (!itemMap.containsKey(itemId)) {
            val item = ChatMenuItemModel()
            item.name = name
            item.image = drawableRes
            item.id = itemId
            item.order = order
            item.clickListener = listener
            itemMap[itemId] = item
            itemModels.add(item)
            sortByOrder(itemModels)
            adapter!!.notifyDataSetChanged()
            //设置需要显示的indicator的个数
            indicatorAdapter!!.setPageCount(
                ceil((itemModels.size * 1.0f / (numColumns * numRows)).toDouble()).toInt()
            )
            Log.e("symbol: chatExtendsMenu size:","${itemModels.size}")
        }
    }

    /**
     * register menu item
     *
     * @param nameRes     resource id of item name
     * @param drawableRes background of item
     * @param itemId      id
     * @param listener    on click event of item
     */
    fun registerMenuItem(
        nameRes: Int,
        drawableRes: Int,
        itemId: Int,
        listener: BenChatExtendMenuItemClickListener?
    ) {
        registerMenuItem(context!!.getString(nameRes), drawableRes, itemId, listener)
    }

    /**
     * register menu item
     *
     * @param nameRes     resource id of item name
     * @param drawableRes background of item
     * @param itemId      id
     * @param order       order by
     * @param listener    on click event of item
     */
    fun registerMenuItem(
        nameRes: Int,
        drawableRes: Int,
        itemId: Int,
        order: Int,
        listener: BenChatExtendMenuItemClickListener?
    ) {
        registerMenuItem(context!!.getString(nameRes), drawableRes, itemId, order, listener)
    }

    private fun sortByOrder(itemModels: List<ChatMenuItemModel>) {
        Collections.sort(itemModels) { o1, o2 ->
            val `val` = o1.order - o2.order
            if (`val` > 0) {
                1
            } else if (`val` == 0) {
                0
            } else {
                -1
            }
        }
    }

    override fun onPageChange(index: Int) {
        currentPosition = index
        //设置选中的indicator
        indicatorAdapter!!.setSelectedPosition(index)
    }

    override fun onItemClick(view: View?, position: Int) {
        val itemModel = itemModels[position]
        if (itemListener != null) {
            itemListener!!.onChatExtendMenuItemClick(itemModel.id, view)
        }
    }

    override fun registerMenuItem(name: String?, drawableRes: Int, itemId: Int, orderInt: Int) {
        registerMenuItem(name, drawableRes, itemId, itemListener)
    }


    override fun setBenChatExtendMenuItemClickListener(listener: BenChatExtendMenuItemClickListener?) {
        itemListener = listener
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (helper != null && rvExtendMenu != null) {
            helper!!.scrollToPosition(0)
            helper!!.checkCurrentStatus()
        }
    }

    class ChatMenuItemModel {
        /**
         * 条目名称
         */
        var name: String? = null

        /**
         * 条目图标
         */
        var image = 0

        /**
         * 条目id
         */
        var id = 0

        /**
         * 用作排序
         */
        var order = 0
        var clickListener: BenChatExtendMenuItemClickListener? = null
    }

    internal inner class ChatMenuItem : LinearLayout {
        private var imageView: ImageView? = null
        private var textView: TextView? = null

        constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : this(context, attrs)
        constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
            init(context, attrs)
        }

        constructor(context: Context?) : super(context) {
            init(context, null)
        }

        private fun init(context: Context?, attrs: AttributeSet?) {
            LayoutInflater.from(context).inflate(R.layout.ben_chat_menu_item, this)
            imageView = findViewById(R.id.image)
            textView = findViewById(R.id.text)
        }

        fun setImage(resid: Int) {
            imageView!!.setBackgroundResource(resid)
        }

        fun setText(resid: Int) {
            textView!!.setText(resid)
        }

        fun setText(text: String?) {
            textView!!.text = text
        }
    }
}