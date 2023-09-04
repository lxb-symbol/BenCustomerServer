package com.ben.lib_picture_selector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.luck.picture.lib.basic.PictureSelectionModel;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectorConfig;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.engine.CropEngine;
import com.luck.picture.lib.engine.CropFileEngine;
import com.luck.picture.lib.engine.ImageEngine;
import com.luck.picture.lib.engine.UriToFileTransformEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.style.SelectMainStyle;
import com.luck.picture.lib.style.TitleBarStyle;
import com.luck.picture.lib.utils.DateUtils;
import com.luck.picture.lib.utils.SandboxTransformUtils;
import com.luck.picture.lib.utils.StyleUtils;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropImageEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnNewCompressListener;
import top.zibin.luban.OnRenameListener;

/**
 * 简单封装下
 */
public class PictureSelectUtil {

    private PictureSelectUtil() {
    }

    public static PictureSelectUtil get() {
        return Holder.instance;
    }

    /**
     * 打开相机
     *
     * @param context
     * @param listener
     */
    public void takePicture(Context context, ResultListener listener) {
        if (listener == null) return;
        PictureSelector.create(context)
                .openCamera(SelectMimeType.ofImage())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        listener.onResult(result);
                    }

                    @Override
                    public void onCancel() {
                        listener.onCancel();
                    }
                });
    }


    /**
     * 选择 图片
     *
     * @param context
     * @param listener
     */
    public void selectImages(Context context, ResultListener listener) {
        if (listener == null) return;
        PictureSelector.create(context)
                .openGallery(SelectMimeType.ofImage())
                .setSelectorUIStyle(getDefaultUIStyle())
                .setMaxSelectNum(9)
                .setImageEngine(getImageEngine())
                .setCropEngine(getCropFileEngine(context))
                .setCompressEngine(getCompressFileEngine())
                .setSandboxFileEngine(new MeSandboxFileEngine()).forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        listener.onResult(result);
                    }

                    @Override
                    public void onCancel() {
                        listener.onCancel();
                    }
                });
    }


    /**
     * 选择图片和最多数量
     *
     * @param context
     * @param maxNum
     * @param listener
     */
    public void selectImages(Context context, int maxNum, ResultListener listener) {
        if (listener == null) return;
        PictureSelector.create(context)
                .openGallery(SelectMimeType.ofImage())
                .setSelectorUIStyle(getDefaultUIStyle())
                .setMaxSelectNum(maxNum)
                .setImageEngine(getImageEngine())
                .setCropEngine(getCropFileEngine(context))
                .setCompressEngine(getCompressFileEngine())
                .setSandboxFileEngine(new MeSandboxFileEngine()).forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        listener.onResult(result);
                    }

                    @Override
                    public void onCancel() {
                        listener.onCancel();
                    }
                });
    }

    public void selectImages(Context context, List<LocalMedia> lastSelected, ResultListener listener) {
        if (listener == null) return;
        PictureSelector.create(context)
                .openGallery(SelectMimeType.ofImage())
                .setSelectorUIStyle(getDefaultUIStyle())
                .setMaxSelectNum(9)
                .setSelectedData(lastSelected)
                .setImageEngine(getImageEngine())
                .setCropEngine(getCropFileEngine(context))
                .setCompressEngine(getCompressFileEngine())
                .setSandboxFileEngine(new MeSandboxFileEngine()).forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        listener.onResult(result);
                    }

                    @Override
                    public void onCancel() {
                        listener.onCancel();
                    }
                });
    }


    /**
     * 选择图片和最多数量
     *
     * @param context
     * @param maxNum
     * @param listener
     */
    public void selectImagesUnCut(Context context, int maxNum, ResultListener listener) {
        if (listener == null) return;
        PictureSelector.create(context)
                .openGallery(SelectMimeType.ofImage())
                .setSelectorUIStyle(getDefaultUIStyle())
                .setMaxSelectNum(maxNum)
                .setImageEngine(getImageEngine())

                .setCompressEngine(getCompressFileEngine())
                .setSandboxFileEngine(new MeSandboxFileEngine()).forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        listener.onResult(result);
                    }

                    @Override
                    public void onCancel() {
                        listener.onCancel();
                    }
                });
    }


    public void selectImages(Context context, int max, List<LocalMedia> lastSelected, ResultListener listener) {
        if (listener == null) return;
        PictureSelector.create(context)
                .openGallery(SelectMimeType.ofImage())
                .setSelectorUIStyle(getDefaultUIStyle())
                .setMaxSelectNum(max)
                .setSelectedData(lastSelected)
                .setImageEngine(getImageEngine())
                .setCropEngine(getCropFileEngine(context))
                .setCompressEngine(getCompressFileEngine())
                .setSandboxFileEngine(new MeSandboxFileEngine())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        listener.onResult(result);
                    }

                    @Override
                    public void onCancel() {
                        listener.onCancel();
                    }
                });
    }


    public void selectImagesUnCut(Context context, int max, List<LocalMedia> lastSelected, ResultListener listener) {
        if (listener == null) return;
        PictureSelector.create(context)
                .openGallery(SelectMimeType.ofImage())
                .setSelectorUIStyle(getDefaultUIStyle())
                .setMaxSelectNum(max)
                .setSelectedData(lastSelected)
                .setImageEngine(getImageEngine())
                .setCompressEngine(getCompressFileEngine())
                .setSandboxFileEngine(new MeSandboxFileEngine())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        listener.onResult(result);
                    }

                    @Override
                    public void onCancel() {
                        listener.onCancel();
                    }
                });
    }




    /**
     * 选择视频最多 9 个
     *
     * @param context
     */
    public void selectVideos(Context context, ResultListener listener) {
        if (listener == null) return;
        PictureSelector.create(context)
                .openGallery(SelectMimeType.ofVideo())
                .setSelectorUIStyle(getDefaultUIStyle())
                .setMaxSelectNum(9)
                .setImageEngine(getImageEngine())
                .setCropEngine(getCropFileEngine(context))
                .setCompressEngine(getCompressFileEngine())
                .setSandboxFileEngine(new MeSandboxFileEngine())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        listener.onResult(result);
                    }

                    @Override
                    public void onCancel() {
                        listener.onCancel();
                    }
                });
    }


    /**
     * 选择视频最多 9 个
     *
     * @param context
     */
    public void selectVideos(Context context, int maxNum, ResultListener listener) {
        if (listener == null) return;
        PictureSelector.create(context)
                .openGallery(SelectMimeType.ofVideo())
                .setSelectorUIStyle(getDefaultUIStyle())
                .setMaxSelectNum(maxNum)
                .setImageEngine(getImageEngine())
                .setCropEngine(getCropFileEngine(context))
                .setCompressEngine(getCompressFileEngine())
                .setSandboxFileEngine(new MeSandboxFileEngine())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        listener.onResult(result);
                    }

                    @Override
                    public void onCancel() {
                        listener.onCancel();
                    }
                });
    }


    public void selectVideos(Context context, int maxNum, List<LocalMedia> last, ResultListener listener) {
        if (listener == null) return;
        PictureSelector.create(context)
                .openGallery(SelectMimeType.ofVideo())
                .setSelectorUIStyle(getDefaultUIStyle())
                .setMaxSelectNum(maxNum)
                .setSelectedData(last)
                .setImageEngine(getImageEngine())
                .setCropEngine(getCropFileEngine(context))
                .setCompressEngine(getCompressFileEngine())
                .setSandboxFileEngine(new MeSandboxFileEngine())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        listener.onResult(result);
                    }

                    @Override
                    public void onCancel() {
                        listener.onCancel();
                    }
                });
    }

    public void selectVideos(Context context, List<LocalMedia> last, ResultListener listener) {
        if (listener == null) return;
        PictureSelector.create(context)
                .openGallery(SelectMimeType.ofVideo())
                .setSelectorUIStyle(getDefaultUIStyle())
                .setMaxSelectNum(9)
                .setSelectedData(last)
                .setImageEngine(getImageEngine())
                .setCropEngine(getCropFileEngine(context))
                .setCompressEngine(getCompressFileEngine())
                .setSandboxFileEngine(new MeSandboxFileEngine())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        listener.onResult(result);
                    }

                    @Override
                    public void onCancel() {
                        listener.onCancel();
                    }
                });
    }


    /**
     * 选择所有 音视频和图片
     *
     * @param context
     */
    public void selectAll(Context context, ResultListener listener) {
        if (listener == null) return;
        PictureSelector.create(context)
                .openGallery(SelectMimeType.ofAll())
                .setSelectorUIStyle(getDefaultUIStyle())
                .setMaxSelectNum(9)
                .setImageEngine(getImageEngine())
                .setCropEngine(getCropFileEngine(context))
                .setCompressEngine(getCompressFileEngine())
                .setSandboxFileEngine(new MeSandboxFileEngine())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        listener.onResult(result);
                    }

                    @Override
                    public void onCancel() {
                        listener.onCancel();
                    }
                });
    }

    /**
     * 选择所有 音视频和图片
     *
     * @param context
     */
    public void selectAll(Context context, List<LocalMedia> last, ResultListener listener) {
        if (listener == null) return;
        PictureSelector.create(context)
                .openGallery(SelectMimeType.ofAll())
                .setSelectorUIStyle(getDefaultUIStyle())
                .setImageEngine(getImageEngine())
                .setSelectedData(last)
                .setCropEngine(getCropFileEngine(context))
                .setCompressEngine(getCompressFileEngine())
                .setSandboxFileEngine(new MeSandboxFileEngine())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        listener.onResult(result);
                    }

                    @Override
                    public void onCancel() {
                        listener.onCancel();
                    }
                });
    }


    private ImageEngine getImageEngine() {
        return GlideEngine.createGlideEngine();
    }

    /**
     * 裁剪引擎
     *
     * @return
     */
    private ImageFileCropEngine getCropFileEngine(Context context) {
        return new ImageFileCropEngine(context);
    }

    /**
     * 压缩引擎
     *
     * @return
     */
    private ImageFileCompressEngine getCompressFileEngine() {
        return new ImageFileCompressEngine();
    }

    /**
     * 获取默认样式
     *
     * @return
     */
    private PictureSelectorStyle getDefaultUIStyle() {
        return new PictureSelectorStyle();
    }

    /**
     * 配制UCrop，可根据需求自我扩展
     *
     * @return
     */
    private UCrop.Options buildDefaultCropOptions(Context context) {
        UCrop.Options options = new UCrop.Options();
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false);
        options.setShowCropFrame(true);
        options.setShowCropGrid(false);
        options.setCircleDimmedLayer(false);
        options.setCropOutputPathDir(getSandboxPath(context));
        options.isCropDragSmoothToCenter(false);
        options.isForbidCropGifWebp(true);
        options.isForbidSkipMultipleCrop(true);
        options.setMaxScaleMultiplier(100);
        if (getDefaultUIStyle().getSelectMainStyle().getStatusBarColor() != 0) {
            SelectMainStyle mainStyle = getDefaultUIStyle().getSelectMainStyle();
            boolean isDarkStatusBarBlack = mainStyle.isDarkStatusBarBlack();
            int statusBarColor = mainStyle.getStatusBarColor();
            options.isDarkStatusBarBlack(isDarkStatusBarBlack);
            if (StyleUtils.checkStyleValidity(statusBarColor)) {
                options.setStatusBarColor(statusBarColor);
                options.setToolbarColor(statusBarColor);
            } else {
                options.setStatusBarColor(ContextCompat.getColor(context, R.color.ps_color_grey));
                options.setToolbarColor(ContextCompat.getColor(context, R.color.ps_color_grey));
            }
            TitleBarStyle titleBarStyle = getDefaultUIStyle().getTitleBarStyle();
            if (StyleUtils.checkStyleValidity(titleBarStyle.getTitleTextColor())) {
                options.setToolbarWidgetColor(titleBarStyle.getTitleTextColor());
            } else {
                options.setToolbarWidgetColor(ContextCompat.getColor(context, R.color.ps_color_white));
            }
        } else {
            options.setStatusBarColor(ContextCompat.getColor(context, R.color.ps_color_grey));
            options.setToolbarColor(ContextCompat.getColor(context, R.color.ps_color_grey));
            options.setToolbarWidgetColor(ContextCompat.getColor(context, R.color.ps_color_white));
        }
        return options;
    }

    /**
     * 创建自定义输出目录
     *
     * @return
     */
    private String getSandboxPath(Context context) {
        File externalFilesDir = context.getExternalFilesDir("");
        File customFile = new File(externalFilesDir.getAbsolutePath(), "Sandbox");
        if (!customFile.exists()) {
            customFile.mkdirs();
        }
        return customFile.getAbsolutePath() + File.separator;
    }

    /**
     * 自定义沙盒文件处理
     */
    private static class MeSandboxFileEngine implements UriToFileTransformEngine {

        @Override
        public void onUriToFileAsyncTransform(Context context, String srcPath, String mineType, OnKeyValueResultCallbackListener call) {
            if (call != null) {
                call.onCallback(srcPath, SandboxTransformUtils.copyPathToSandbox(context, srcPath, mineType));
            }
        }
    }

    /**
     * 自定义压缩
     */
    private static class ImageFileCompressEngine implements CompressFileEngine {

        @Override
        public void onStartCompress(Context context, ArrayList<Uri> source, OnKeyValueResultCallbackListener call) {
            Luban.with(context).load(source).ignoreBy(100).setRenameListener(filePath -> {
                int indexOf = filePath.lastIndexOf(".");
                String postfix = indexOf != -1 ? filePath.substring(indexOf) : ".jpg";
                return DateUtils.getCreateFileName("CMP_") + postfix;
            }).filter(path -> {
                if (PictureMimeType.isUrlHasImage(path) && !PictureMimeType.isHasHttp(path)) {
                    return true;
                }
                return !PictureMimeType.isUrlHasGif(path);
            }).setCompressListener(new OnNewCompressListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess(String source, File compressFile) {
                    if (call != null) {
                        call.onCallback(source, compressFile.getAbsolutePath());
                    }
                }

                @Override
                public void onError(String source, Throwable e) {
                    if (call != null) {
                        call.onCallback(source, null);
                    }
                }
            }).launch();
        }
    }

    private static class Holder {
        private static final PictureSelectUtil instance = new PictureSelectUtil();
    }

    /**
     * 自定义裁剪
     */
    private class ImageFileCropEngine implements CropFileEngine {

        Context context;

        public ImageFileCropEngine() {

        }

        public ImageFileCropEngine(Context context) {
            this.context = context;
        }

        @Override
        public void onStartCrop(Fragment fragment, Uri srcUri, Uri destinationUri, ArrayList<String> dataSource, int requestCode) {
            UCrop.Options options = buildDefaultCropOptions(context);
            UCrop uCrop = UCrop.of(srcUri, destinationUri, dataSource);
            uCrop.withOptions(options);
            uCrop.setImageEngine(new UCropImageEngine() {
                @Override
                public void loadImage(Context context, String url, ImageView imageView) {
                    if (!ImageLoaderUtils.assertValidRequest(context)) {
                        return;
                    }
                    Glide.with(context).load(url).override(180, 180).into(imageView);
                }

                @Override
                public void loadImage(Context context, Uri url, int maxWidth, int maxHeight, OnCallbackListener<Bitmap> call) {
                    Glide.with(context).asBitmap().load(url).override(maxWidth, maxHeight).into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            if (call != null) {
                                call.onCall(resource);
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            if (call != null) {
                                call.onCall(null);
                            }
                        }
                    });
                }
            });
            uCrop.start(fragment.requireActivity(), fragment, requestCode);
        }
    }


}
