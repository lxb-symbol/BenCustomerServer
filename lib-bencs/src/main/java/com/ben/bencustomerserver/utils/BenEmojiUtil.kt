package com.ben.bencustomerserver.utils

import android.content.Context
import android.net.Uri
import android.text.Spannable
import android.text.TextUtils
import android.text.style.ImageSpan
import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.model.BenEmojiEntity
import java.io.File
import java.util.regex.Pattern

object BenEmojiUtil {

    val emojiEntities = mutableListOf<BenEmojiEntity>()

    val emoticons: MutableMap<Pattern, Any> = HashMap()

    const val NAME_0 = "face[微笑]"
    const val NAME_1 = "face[嘻嘻]"
    const val NAME_2 = "face[哈哈]"
    const val NAME_3 = "face[可爱]"
    const val NAME_4 = "face[可怜]"
    const val NAME_5 = "face[挖鼻]"
    const val NAME_6 = "face[吃惊]"
    const val NAME_7 = "face[害羞]"
    const val NAME_8 = "face[挤眼]"
    const val NAME_9 = "face[闭嘴]"

    const val NAME_10 = "face[鄙视]"
    const val NAME_11 = "face[爱你]"
    const val NAME_12 = "face[泪]"
    const val NAME_13 = "face[偷笑]"
    const val NAME_14 = "face[亲亲]"
    const val NAME_15 = "face[生病]"
    const val NAME_16 = "face[太开心]"
    const val NAME_17 = "face[白眼]"
    const val NAME_18 = "face[右哼哼]"
    const val NAME_19 = "face[左哼哼]"

    const val NAME_20 = "face[嘘]"
    const val NAME_21 = "face[衰]"
    const val NAME_22 = "face[委屈]"
    const val NAME_23 = "face[吐]"
    const val NAME_24 = "face[哈欠]"
    const val NAME_25 = "face[抱抱]"
    const val NAME_26 = "face[怒]"
    const val NAME_27 = "face[疑问]"
    const val NAME_28 = "face[馋嘴]"
    const val NAME_29 = "face[拜拜]"

    const val NAME_30 = "face[思考]"
    const val NAME_31 = "face[汗]"
    const val NAME_32 = "face[困]"
    const val NAME_33 = "face[睡]"
    const val NAME_34 = "face[钱]"
    const val NAME_35 = "face[失望]"
    const val NAME_36 = "face[酷]"
    const val NAME_37 = "face[色]"
    const val NAME_38 = "face[哼]"
    const val NAME_39 = "face[鼓掌]"

    const val NAME_40 = "face[晕]"
    const val NAME_41 = "face[悲伤]"
    const val NAME_42 = "face[抓狂]"
    const val NAME_43 = "face[黑线]"
    const val NAME_44 = "face[阴险]"
    const val NAME_45 = "face[怒骂]"
    const val NAME_46 = "face[互粉]"
    const val NAME_47 = "face[心]"
    const val NAME_48 = "face[伤心]"
    const val NAME_49 = "face[猪头]"

    const val NAME_50 = "face[熊猫]"
    const val NAME_51 = "face[兔子]"
    const val NAME_52 = "face[ok]"
    const val NAME_53 = "face[耶]"
    const val NAME_54 = "face[good]"
    const val NAME_55 = "face[NO]"
    const val NAME_56 = "face[赞]"
    const val NAME_57 = "face[来]"
    const val NAME_58 = "face[弱]"
    const val NAME_59 = "face[草泥马]"

    const val NAME_60 = "face[神马]"
    const val NAME_61 = "face[囧]"
    const val NAME_62 = "face[浮云]"
    const val NAME_63 = "face[给力]"
    const val NAME_64 = "face[围观]"
    const val NAME_65 = "face[威武]"
    const val NAME_66 = "face[奥特曼]"
    const val NAME_67 = "face[礼物]"
    const val NAME_68 = "face[钟]"
    const val NAME_69 = "face[话筒]"

    const val NAME_70 = "face[蜡烛]"
    const val NAME_71 = "face[蛋糕]"


    val ID_EMOJI_0 = R.drawable.image_0
    val ID_EMOJI_1 = R.drawable.image_1
    val ID_EMOJI_2 = R.drawable.image_2
    val ID_EMOJI_3 = R.drawable.image_3
    val ID_EMOJI_4 = R.drawable.image_4
    val ID_EMOJI_5 = R.drawable.image_5
    val ID_EMOJI_6 = R.drawable.image_6
    val ID_EMOJI_7 = R.drawable.image_7
    val ID_EMOJI_8 = R.drawable.image_8
    val ID_EMOJI_9 = R.drawable.image_9

    val ID_EMOJI_10 = R.drawable.image_10
    val ID_EMOJI_11 = R.drawable.image_11
    val ID_EMOJI_12 = R.drawable.image_12
    val ID_EMOJI_13 = R.drawable.image_13
    val ID_EMOJI_14 = R.drawable.image_14
    val ID_EMOJI_15 = R.drawable.image_15
    val ID_EMOJI_16 = R.drawable.image_16
    val ID_EMOJI_17 = R.drawable.image_17
    val ID_EMOJI_18 = R.drawable.image_18
    val ID_EMOJI_19 = R.drawable.image_19

    val ID_EMOJI_20 = R.drawable.image_20
    val ID_EMOJI_21 = R.drawable.image_21
    val ID_EMOJI_22 = R.drawable.image_22
    val ID_EMOJI_23 = R.drawable.image_23
    val ID_EMOJI_24 = R.drawable.image_24
    val ID_EMOJI_25 = R.drawable.image_25
    val ID_EMOJI_26 = R.drawable.image_26
    val ID_EMOJI_27 = R.drawable.image_27
    val ID_EMOJI_28 = R.drawable.image_28
    val ID_EMOJI_29 = R.drawable.image_29

    val ID_EMOJI_30 = R.drawable.image_30
    val ID_EMOJI_31 = R.drawable.image_31
    val ID_EMOJI_32 = R.drawable.image_32
    val ID_EMOJI_33 = R.drawable.image_33
    val ID_EMOJI_34 = R.drawable.image_34
    val ID_EMOJI_35 = R.drawable.image_35
    val ID_EMOJI_36 = R.drawable.image_36
    val ID_EMOJI_37 = R.drawable.image_37
    val ID_EMOJI_38 = R.drawable.image_38
    val ID_EMOJI_39 = R.drawable.image_39

    val ID_EMOJI_40 = R.drawable.image_40
    val ID_EMOJI_41 = R.drawable.image_41
    val ID_EMOJI_42 = R.drawable.image_42
    val ID_EMOJI_43 = R.drawable.image_43
    val ID_EMOJI_44 = R.drawable.image_44
    val ID_EMOJI_45 = R.drawable.image_45
    val ID_EMOJI_46 = R.drawable.image_46
    val ID_EMOJI_47 = R.drawable.image_47
    val ID_EMOJI_48 = R.drawable.image_48
    val ID_EMOJI_49 = R.drawable.image_49

    val ID_EMOJI_50 = R.drawable.image_50
    val ID_EMOJI_51 = R.drawable.image_51
    val ID_EMOJI_52 = R.drawable.image_52
    val ID_EMOJI_53 = R.drawable.image_53
    val ID_EMOJI_54 = R.drawable.image_54
    val ID_EMOJI_55 = R.drawable.image_55
    val ID_EMOJI_56 = R.drawable.image_56
    val ID_EMOJI_57 = R.drawable.image_57
    val ID_EMOJI_58 = R.drawable.image_58
    val ID_EMOJI_59 = R.drawable.image_59

    val ID_EMOJI_60 = R.drawable.image_60
    val ID_EMOJI_61 = R.drawable.image_61
    val ID_EMOJI_62 = R.drawable.image_62
    val ID_EMOJI_63 = R.drawable.image_63
    val ID_EMOJI_64 = R.drawable.image_64
    val ID_EMOJI_65 = R.drawable.image_65
    val ID_EMOJI_66 = R.drawable.image_66
    val ID_EMOJI_67 = R.drawable.image_67
    val ID_EMOJI_68 = R.drawable.image_68
    val ID_EMOJI_69 = R.drawable.image_69

    val ID_EMOJI_70 = R.drawable.image_70
    val ID_EMOJI_71 = R.drawable.image_71


    const val URL_EMOJI_0 = "/static/common/images/face/0.gif"
    const val URL_EMOJI_1 = "/static/common/images/face/1.gif"
    const val URL_EMOJI_2 = "/static/common/images/face/2.gif"
    const val URL_EMOJI_3 = "/static/common/images/face/3.gif"
    const val URL_EMOJI_4 = "/static/common/images/face/4.gif"
    const val URL_EMOJI_5 = "/static/common/images/face/5.gif"
    const val URL_EMOJI_6 = "/static/common/images/face/6.gif"
    const val URL_EMOJI_7 = "/static/common/images/face/7.gif"
    const val URL_EMOJI_8 = "/static/common/images/face/8.gif"
    const val URL_EMOJI_9 = "/static/common/images/face/9.gif"

    const val URL_EMOJI_10 = "/static/common/images/face/10.gif"
    const val URL_EMOJI_11 = "/static/common/images/face/11.gif"
    const val URL_EMOJI_12 = "/static/common/images/face/12.gif"
    const val URL_EMOJI_13 = "/static/common/images/face/13.gif"
    const val URL_EMOJI_14 = "/static/common/images/face/14.gif"
    const val URL_EMOJI_15 = "/static/common/images/face/15.gif"
    const val URL_EMOJI_16 = "/static/common/images/face/16.gif"
    const val URL_EMOJI_17 = "/static/common/images/face/17.gif"
    const val URL_EMOJI_18 = "/static/common/images/face/18.gif"
    const val URL_EMOJI_19 = "/static/common/images/face/19.gif"

    const val URL_EMOJI_20 = "/static/common/images/face/20.gif"
    const val URL_EMOJI_21 = "/static/common/images/face/21.gif"
    const val URL_EMOJI_22 = "/static/common/images/face/22.gif"
    const val URL_EMOJI_23 = "/static/common/images/face/23.gif"
    const val URL_EMOJI_24 = "/static/common/images/face/24.gif"
    const val URL_EMOJI_25 = "/static/common/images/face/25.gif"
    const val URL_EMOJI_26 = "/static/common/images/face/26.gif"
    const val URL_EMOJI_27 = "/static/common/images/face/27.gif"
    const val URL_EMOJI_28 = "/static/common/images/face/28.gif"
    const val URL_EMOJI_29 = "/static/common/images/face/29.gif"

    const val URL_EMOJI_30 = "/static/common/images/face/30.gif"
    const val URL_EMOJI_31 = "/static/common/images/face/31.gif"
    const val URL_EMOJI_32 = "/static/common/images/face/32.gif"
    const val URL_EMOJI_33 = "/static/common/images/face/33.gif"
    const val URL_EMOJI_34 = "/static/common/images/face/34.gif"
    const val URL_EMOJI_35 = "/static/common/images/face/35.gif"
    const val URL_EMOJI_36 = "/static/common/images/face/36.gif"
    const val URL_EMOJI_37 = "/static/common/images/face/37.gif"
    const val URL_EMOJI_38 = "/static/common/images/face/38.gif"
    const val URL_EMOJI_39 = "/static/common/images/face/39.gif"

    const val URL_EMOJI_40 = "/static/common/images/face/40.gif"
    const val URL_EMOJI_41 = "/static/common/images/face/41.gif"
    const val URL_EMOJI_42 = "/static/common/images/face/42.gif"
    const val URL_EMOJI_43 = "/static/common/images/face/43.gif"
    const val URL_EMOJI_44 = "/static/common/images/face/44.gif"
    const val URL_EMOJI_45 = "/static/common/images/face/45.gif"
    const val URL_EMOJI_46 = "/static/common/images/face/46.gif"
    const val URL_EMOJI_47 = "/static/common/images/face/47.gif"
    const val URL_EMOJI_48 = "/static/common/images/face/48.gif"
    const val URL_EMOJI_49 = "/static/common/images/face/49.gif"

    const val URL_EMOJI_50 = "/static/common/images/face/50.gif"
    const val URL_EMOJI_51 = "/static/common/images/face/51.gif"
    const val URL_EMOJI_52 = "/static/common/images/face/52.gif"
    const val URL_EMOJI_53 = "/static/common/images/face/53.gif"
    const val URL_EMOJI_54 = "/static/common/images/face/54.gif"
    const val URL_EMOJI_55 = "/static/common/images/face/55.gif"
    const val URL_EMOJI_56 = "/static/common/images/face/56.gif"
    const val URL_EMOJI_57 = "/static/common/images/face/57.gif"
    const val URL_EMOJI_58 = "/static/common/images/face/58.gif"
    const val URL_EMOJI_59 = "/static/common/images/face/59.gif"

    const val URL_EMOJI_60 = "/static/common/images/face/60.gif"
    const val URL_EMOJI_61 = "/static/common/images/face/61.gif"
    const val URL_EMOJI_62 = "/static/common/images/face/62.gif"
    const val URL_EMOJI_63 = "/static/common/images/face/63.gif"
    const val URL_EMOJI_64 = "/static/common/images/face/64.gif"
    const val URL_EMOJI_65 = "/static/common/images/face/65.gif"
    const val URL_EMOJI_66 = "/static/common/images/face/66.gif"
    const val URL_EMOJI_67 = "/static/common/images/face/67.gif"
    const val URL_EMOJI_68 = "/static/common/images/face/68.gif"
    const val URL_EMOJI_69 = "/static/common/images/face/69.gif"

    const val URL_EMOJI_70 = "/static/common/images/face/70.gif"
    const val URL_EMOJI_71 = "/static/common/images/face/71.gif"

    /**
     * 名字
     */
    val emojiNames = arrayOf(
        NAME_0,
        NAME_1,
        NAME_2,
        NAME_3,
        NAME_4,
        NAME_5,
        NAME_6,
        NAME_7,
        NAME_8,
        NAME_9,

        NAME_10,
        NAME_11,
        NAME_12,
        NAME_13,
        NAME_14,
        NAME_15,
        NAME_16,
        NAME_17,
        NAME_18,
        NAME_19,

        NAME_20,
        NAME_21,
        NAME_22,
        NAME_23,
        NAME_24,
        NAME_25,
        NAME_26,
        NAME_27,
        NAME_28,
        NAME_29,

        NAME_30,
        NAME_31,
        NAME_32,
        NAME_33,
        NAME_34,
        NAME_35,
        NAME_36,
        NAME_37,
        NAME_38,
        NAME_39,

        NAME_40,
        NAME_41,
        NAME_42,
        NAME_43,
        NAME_44,
        NAME_45,
        NAME_46,
        NAME_47,
        NAME_48,
        NAME_49,

        NAME_50,
        NAME_51,
        NAME_52,
        NAME_53,
        NAME_54,
        NAME_55,
        NAME_56,
        NAME_57,
        NAME_58,
        NAME_59,

        NAME_60,
        NAME_61,
        NAME_62,
        NAME_63,
        NAME_64,
        NAME_65,
        NAME_66,
        NAME_67,
        NAME_68,
        NAME_69,
        NAME_70,
        NAME_71,
    )

    val emojiIds = arrayOf(
        ID_EMOJI_0,
        ID_EMOJI_1,
        ID_EMOJI_2,
        ID_EMOJI_3,
        ID_EMOJI_4,
        ID_EMOJI_5,
        ID_EMOJI_6,
        ID_EMOJI_7,
        ID_EMOJI_8,
        ID_EMOJI_9,

        ID_EMOJI_10,
        ID_EMOJI_11,
        ID_EMOJI_12,
        ID_EMOJI_13,
        ID_EMOJI_14,
        ID_EMOJI_15,
        ID_EMOJI_16,
        ID_EMOJI_17,
        ID_EMOJI_18,
        ID_EMOJI_19,

        ID_EMOJI_20,
        ID_EMOJI_21,
        ID_EMOJI_22,
        ID_EMOJI_23,
        ID_EMOJI_24,
        ID_EMOJI_25,
        ID_EMOJI_26,
        ID_EMOJI_27,
        ID_EMOJI_28,
        ID_EMOJI_29,

        ID_EMOJI_30,
        ID_EMOJI_31,
        ID_EMOJI_32,
        ID_EMOJI_33,
        ID_EMOJI_34,
        ID_EMOJI_35,
        ID_EMOJI_36,
        ID_EMOJI_37,
        ID_EMOJI_38,
        ID_EMOJI_39,

        ID_EMOJI_40,
        ID_EMOJI_41,
        ID_EMOJI_42,
        ID_EMOJI_43,
        ID_EMOJI_44,
        ID_EMOJI_45,
        ID_EMOJI_46,
        ID_EMOJI_47,
        ID_EMOJI_48,
        ID_EMOJI_49,

        ID_EMOJI_50,
        ID_EMOJI_51,
        ID_EMOJI_52,
        ID_EMOJI_53,
        ID_EMOJI_54,
        ID_EMOJI_55,
        ID_EMOJI_56,
        ID_EMOJI_57,
        ID_EMOJI_58,
        ID_EMOJI_59,

        ID_EMOJI_60,
        ID_EMOJI_61,
        ID_EMOJI_62,
        ID_EMOJI_63,
        ID_EMOJI_64,
        ID_EMOJI_65,
        ID_EMOJI_66,
        ID_EMOJI_67,
        ID_EMOJI_68,
        ID_EMOJI_69,

        ID_EMOJI_70,
        ID_EMOJI_71,
    )

    val emojiURL = arrayOf(
        URL_EMOJI_0,
        URL_EMOJI_1,
        URL_EMOJI_2,
        URL_EMOJI_3,
        URL_EMOJI_4,
        URL_EMOJI_5,
        URL_EMOJI_6,
        URL_EMOJI_7,
        URL_EMOJI_8,
        URL_EMOJI_9,

        URL_EMOJI_10,
        URL_EMOJI_11,
        URL_EMOJI_12,
        URL_EMOJI_13,
        URL_EMOJI_14,
        URL_EMOJI_15,
        URL_EMOJI_16,
        URL_EMOJI_17,
        URL_EMOJI_18,
        URL_EMOJI_19,

        URL_EMOJI_20,
        URL_EMOJI_21,
        URL_EMOJI_22,
        URL_EMOJI_23,
        URL_EMOJI_24,
        URL_EMOJI_25,
        URL_EMOJI_26,
        URL_EMOJI_27,
        URL_EMOJI_28,
        URL_EMOJI_29,

        URL_EMOJI_30,
        URL_EMOJI_31,
        URL_EMOJI_32,
        URL_EMOJI_33,
        URL_EMOJI_34,
        URL_EMOJI_35,
        URL_EMOJI_36,
        URL_EMOJI_37,
        URL_EMOJI_38,
        URL_EMOJI_39,

        URL_EMOJI_40,
        URL_EMOJI_41,
        URL_EMOJI_42,
        URL_EMOJI_43,
        URL_EMOJI_44,
        URL_EMOJI_45,
        URL_EMOJI_46,
        URL_EMOJI_47,
        URL_EMOJI_48,
        URL_EMOJI_49,

        URL_EMOJI_50,
        URL_EMOJI_51,
        URL_EMOJI_52,
        URL_EMOJI_53,
        URL_EMOJI_54,
        URL_EMOJI_55,
        URL_EMOJI_56,
        URL_EMOJI_57,
        URL_EMOJI_58,
        URL_EMOJI_59,

        URL_EMOJI_60,
        URL_EMOJI_61,
        URL_EMOJI_62,
        URL_EMOJI_63,
        URL_EMOJI_64,
        URL_EMOJI_65,
        URL_EMOJI_66,
        URL_EMOJI_67,
        URL_EMOJI_68,
        URL_EMOJI_69,
        URL_EMOJI_70,

        URL_EMOJI_70,
        URL_EMOJI_71,
    )


    init {
        for (i in emojiNames.indices) {
            emojiEntities.add(BenEmojiEntity(emojiNames[i], emojiURL[i], emojiIds[i]))
        }
        for (emoji in emojiEntities) {
            addPattern(emoji.name, emoji.id)
        }
    }


    /**
     * add text and icon to the map
     * @param emojiText-- text of emoji
     * @param icon -- resource id or local path
     */
    fun addPattern(emojiText: String, icon: Any) {
        emoticons[Pattern.compile(Pattern.quote(emojiText))] = icon
    }


    /**
     * 通过名字获取表情 ID
     */
    fun getEmojiIdByName(name: String): Int {
        for (emoji in emojiEntities) {
            if (TextUtils.equals(name, emoji.name)) return emoji.id
        }
        return -1
    }

    /**
     * 通过 url 获取 表情 ID
     */
    fun getEmojiIdByUrl(url: String): Int {
        for (emoji in emojiEntities) {
            if (TextUtils.equals(url, emoji.name)) return emoji.id
        }
        return -1
    }


    /**
     * replace existing spannable with smiles
     * @param context
     * @param spannable
     * @return
     */
    private fun addSmiles(context: Context?, spannable: Spannable): Boolean {
        var hasChanges = false
        for ((key, value) in emoticons) {
            val matcher = key.matcher(spannable)
            while (matcher.find()) {
                var set = true
                for (span in spannable.getSpans(
                    matcher.start(),
                    matcher.end(), GlideImageSpan::class.java
                )) if (spannable.getSpanStart(span) >= matcher.start()
                    && spannable.getSpanEnd(span) <= matcher.end()
                ) spannable.removeSpan(span) else {
                    set = false
                    break
                }
                if (set) {
                    hasChanges = true
                    if (value is String && !value.startsWith("http")) {
                        val file = File(value)
                        if (!file.exists() || file.isDirectory) {
                            return false
                        }
                        spannable.setSpan(
                            ImageSpan(context!!, Uri.fromFile(file)),
                            matcher.start(), matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    } else {
                        spannable.setSpan(
                            ImageSpan(context!!, (value as Int)),
                            matcher.start(), matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
            }
        }
        return hasChanges
    }

    @JvmStatic
    fun getSmiledText(context: Context?, text: CharSequence?): Spannable {
        val spannable = Spannable.Factory.getInstance().newSpannable(text)
        addSmiles(context, spannable)
        return spannable
    }


}