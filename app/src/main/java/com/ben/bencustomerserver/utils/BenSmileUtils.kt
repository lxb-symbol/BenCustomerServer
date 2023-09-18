/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ben.bencustomerserver.utils

import android.content.Context
import android.net.Uri
import android.text.Spannable
import android.text.style.ImageSpan
import com.ben.bencustomerserver.model.BenDefaultEmojiconDatas.data
import com.ben.bencustomerserver.provider.BenEmojiconInfoProvider
import com.ben.bencustomerserver.views.BenEmojicon
import java.io.File
import java.util.regex.Pattern

object BenSmileUtils {
    const val DELETE_KEY = "em_delete_delete_expression"
    const val ee_1 = "[):]"
    const val ee_2 = "[:D]"
    const val ee_3 = "[;)]"
    const val ee_4 = "[:-o]"
    const val ee_5 = "[:p]"
    const val ee_6 = "[(H)]"
    const val ee_7 = "[:@]"
    const val ee_8 = "[:s]"
    const val ee_9 = "[:$]"
    const val ee_10 = "[:(]"
    const val ee_11 = "[:'(]"
    const val ee_12 = "[:|]"
    const val ee_13 = "[(a)]"
    const val ee_14 = "[8o|]"
    const val ee_15 = "[8-|]"
    const val ee_16 = "[+o(]"
    const val ee_17 = "[<o)]"
    const val ee_18 = "[(U)]"
    const val ee_19 = "[|-)]"
    const val ee_20 = "[*-)]"
    const val ee_21 = "[:-#]"
    const val ee_22 = "[:-*]"
    const val ee_23 = "[^o)]"
    const val ee_24 = "[8-)]"
    const val ee_25 = "[(|)]"
    const val ee_26 = "[(u)]"
    const val ee_27 = "[(S)]"
    const val ee_28 = "[(*)]"
    const val ee_29 = "[(#)]"
    const val ee_30 = "[(R)]"
    const val ee_31 = "[({)]"
    const val ee_32 = "[(})]"
    const val ee_33 = "[(k)]"
    const val ee_34 = "[(F)]"
    const val ee_35 = "[(Z)]"
    const val ee_36 = "[(W)]"
    const val ee_37 = "[(D)]"
    const val ee_38 = "[(E)]"
    const val ee_39 = "[(T)]"
    const val ee_40 = "[(G)]"
    const val ee_41 = "[(Y)]"
    const val ee_42 = "[(I)]"
    const val ee_43 = "[(K)]"
    const val ee_44 = "[(L)]"
    const val ee_45 = "[(M)]"
    const val ee_46 = "[(N)]"
    const val ee_47 = "[(O)]"
    private val spannableFactory = Spannable.Factory
        .getInstance()
    private val emoticons: MutableMap<Pattern, Any> = HashMap()

    init {
        val emojicons = data
        for (emojicon in emojicons) {
            addPattern(emojicon!!.emojiText, emojicon.icon)
        }
        val emojiconInfoProvider = emojiconInfoProvider
        if (emojiconInfoProvider?.getTextEmojiconMapping() != null) {
            for ((key, value) in emojiconInfoProvider.getTextEmojiconMapping()!!.entries) {
                if (value != null) {
                    addPattern(key, value)
                }
            }
        }
    }

    private val emojiconInfoProvider: BenEmojiconInfoProvider
        /**
         * todo symbol 待完善
         * @return
         */
        private get() = object : BenEmojiconInfoProvider {
            override fun getEmojiconInfo(emojiconIdentityCode: String?): BenEmojicon? {
                return null
            }

            override fun getTextEmojiconMapping(): Map<String?, Any?>? {
                return null
            }
        }

    /**
     * add text and icon to the map
     * @param emojiText-- text of emoji
     * @param icon -- resource id or local path
     */
    fun addPattern(emojiText: String?, icon: Any) {
        emoticons[Pattern.compile(Pattern.quote(emojiText))] = icon
    }

    /**
     * replace existing spannable with smiles
     * @param context
     * @param spannable
     * @return
     */
    fun addSmiles(context: Context?, spannable: Spannable): Boolean {
        var hasChanges = false
        for ((key, value) in emoticons) {
            val matcher = key.matcher(spannable)
            while (matcher.find()) {
                var set = true
                for (span in spannable.getSpans(
                    matcher.start(),
                    matcher.end(), ImageSpan::class.java
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
        val spannable = spannableFactory.newSpannable(text)
        addSmiles(context, spannable)
        return spannable
    }

    fun containsKey(key: String?): Boolean {
        var b = false
        for ((key1) in emoticons) {
            val matcher = key1.matcher(key)
            if (matcher.find()) {
                b = true
                break
            }
        }
        return b
    }

    fun getSmilesSize(): Int {
        return emoticons.size
    }
}