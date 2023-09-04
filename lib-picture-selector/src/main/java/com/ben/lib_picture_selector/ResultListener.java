package com.ben.lib_picture_selector;

import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

public interface ResultListener {
    void onResult(List<LocalMedia> medias);

    void onCancel();
}
