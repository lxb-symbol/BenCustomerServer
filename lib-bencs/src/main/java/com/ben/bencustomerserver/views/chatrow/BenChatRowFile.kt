package com.ben.bencustomerserver.views.chatrow

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.TextView
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.model.BaseMessageModel
import com.ben.bencustomerserver.model.Direct
import com.ben.bencustomerserver.model.FileMessage
import com.ben.bencustomerserver.model.MessageStatus
import com.ben.bencustomerserver.utils.BenFileUtils
import com.ben.bencustomerserver.utils.TextFormater

/**
 * file for row
 */
open class BenChatRowFile : BenChatRow {
    /**
     * file name
     */
     var fileNameView: TextView? = null

    /**
     * file's size
     */
    protected var fileSizeView: TextView? = null

    /**
     * file state
     */
    protected var fileStateView: TextView? = null

    override var percentageView:TextView?=null

    constructor(context: Context?, isSender: Boolean) : super(context!!, isSender)
    constructor(
        context: Context?,
        message: BaseMessageModel?,
        position: Int,
        adapter: Any?
    ) : super(
        context!!, message, position, adapter
    )

    override fun onInflateView() {
        inflater.inflate(
            if (!isSender) R.layout.ben_row_received_file else R.layout.ben_row_sent_file,
            this
        )
    }

    override fun onFindViewById() {
        fileNameView = findViewById<View>(R.id.tv_file_name) as TextView
//        fileSizeView = findViewById<View>(R.id.tv_file_size) as TextView
        fileStateView = findViewById<View>(R.id.tv_file_state) as TextView
//        percentageView = findViewById<View>(R.id.percentage) as TextView
    }

    override fun onSetUpView() {
        val innerMsg = message?.innerMessage as FileMessage
        val netUrl = innerMsg.netPath
        val localPath = innerMsg.localPath
        val size = innerMsg.fileSize
        val name = innerMsg.name
        fileSizeView?.visibility = View.INVISIBLE
        fileNameView?.text = "" + name
        fileSizeView?.text = (TextFormater.getDataSize(size))
        if (message?.direct === Direct.SEND) {
            message!!.status === MessageStatus.SUCCESS
        }
        if (message!!.direct === Direct.RECEIEVE) {
//                fileStateView?.setText(R.string.have_downloaded)
//            if (BenFileUtils.isFileExistByUri(context, (Uri.parse(localPath)))) {
//            } else {
//                fileStateView?.setText(R.string.did_not_download)
//            }
        }
    }

    override fun onMessageCreate() {
        super.onMessageCreate()
        progressBar!!.visibility = VISIBLE
        if (percentageView != null) percentageView!!.visibility = INVISIBLE
        if (statusView != null) statusView!!.visibility = INVISIBLE
    }

    override fun onMessageSuccess() {
        super.onMessageSuccess()
        progressBar!!.visibility = INVISIBLE
        if (percentageView != null) percentageView!!.visibility = INVISIBLE
        if (statusView != null) statusView!!.visibility = INVISIBLE
        if (message!!.direct === Direct.SEND) if (fileStateView != null) {
            fileStateView?.setText(R.string.have_uploaded)
        }
    }

    override fun onMessageError() {
        super.onMessageError()
        progressBar!!.visibility = INVISIBLE
        if (percentageView != null) percentageView!!.visibility = INVISIBLE
        if (statusView != null) statusView!!.visibility = VISIBLE
    }

    override fun onMessageInProgress() {
        super.onMessageInProgress()
        if (progressBar!!.visibility != VISIBLE) {
            progressBar!!.visibility = VISIBLE
        }
        if (percentageView != null) {
            percentageView!!.visibility = VISIBLE
            percentageView?.text = ("${message?.progress} %")
        }
        if (statusView != null) statusView!!.visibility = INVISIBLE
    }

    companion object {
        private val TAG = BenChatRowFile::class.java.simpleName
    }
}