package com.ben.bencustomerserver.model

import com.ben.bencustomerserver.R
import com.ben.bencustomerserver.views.EaseEmojicon

object EaseMessageMenuData {
    private val REACTION_ICONS = intArrayOf(
        R.drawable.ee_1,
        R.drawable.ee_2,
        R.drawable.ee_3,
        R.drawable.ee_4,
        R.drawable.ee_5,
        R.drawable.ee_6,
        R.drawable.ee_7,
        R.drawable.ee_8,
        R.drawable.ee_9,
        R.drawable.ee_10,
        R.drawable.ee_11,
        R.drawable.ee_12,
        R.drawable.ee_13,
        R.drawable.ee_14,
        R.drawable.ee_15,
        R.drawable.ee_16,
        R.drawable.ee_17,
        R.drawable.ee_18,
        R.drawable.ee_19,
        R.drawable.ee_20,
        R.drawable.ee_21,
        R.drawable.ee_22,
        R.drawable.ee_23,
        R.drawable.ee_24,
        R.drawable.ee_25,
        R.drawable.ee_26,
        R.drawable.ee_27,
        R.drawable.ee_28,
        R.drawable.ee_29,
        R.drawable.ee_30,
        R.drawable.ee_31,
        R.drawable.ee_32,
        R.drawable.ee_33,
        R.drawable.ee_34,
        R.drawable.ee_35,
        R.drawable.ee_36,
        R.drawable.ee_37,
        R.drawable.ee_38,
        R.drawable.ee_39,
        R.drawable.ee_40,
        R.drawable.ee_41,
        R.drawable.ee_42,
        R.drawable.ee_43,
        R.drawable.ee_44,
        R.drawable.ee_45,
        R.drawable.ee_46,
        R.drawable.ee_47
    )
    var REACTION_FREQUENTLY_ICONS_IDS = arrayOf(
        emojis[40],
        emojis[43],
        emojis[37],
        emojis[36],
        emojis[15],
        emojis[10]
    )
    val MENU_ITEM_IDS = intArrayOf(
        R.id.action_chat_copy,
        R.id.action_chat_thread,
        R.id.action_chat_reply,
        R.id.action_chat_delete,
        R.id.action_chat_recall
    )
    val MENU_TITLES = intArrayOf(
        R.string.ease_action_copy,
        R.string.ease_action_thread,
        R.string.ease_action_reply,
        R.string.ease_action_delete,
        R.string.ease_action_recall
    )
    val MENU_ICONS = intArrayOf(
        R.drawable.ease_menu_copy,
        R.drawable.ease_menu_thread,
        R.drawable.ease_chat_item_menu_reply,
        R.drawable.ease_chat_item_menu_delete,
        R.drawable.ease_menu_recall
    )
     const val EMOTICON_MORE_IDENTITY_CODE = "emoji_more"
    val reactionMore = createMoreEmoticon()
    private fun createMoreEmoticon(): EaseEmojicon {
        val data = EaseEmojicon()
        data.identityCode = EMOTICON_MORE_IDENTITY_CODE
        data.icon = R.drawable.ee_reaction_more
        return data
    }

    val reactionDataMap = createReactionDataMap()
    private fun createReactionDataMap(): Map<String, EaseEmojicon> {
        val emojiconsMap: MutableMap<String, EaseEmojicon> = HashMap(REACTION_ICONS.size)
        var emojicon: EaseEmojicon
        var id: String
        for (i in REACTION_ICONS.indices) {
            emojicon = EaseEmojicon(REACTION_ICONS[i], "", EaseEmojicon.Type.NORMAL)
            id = emojis[i]
            emojicon.identityCode = id
            emojiconsMap[id] = emojicon
        }
        return emojiconsMap
    }
}