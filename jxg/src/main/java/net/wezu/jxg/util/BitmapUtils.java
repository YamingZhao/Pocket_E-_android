package net.wezu.jxg.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图片工具类
 *
 * Created by snox on 2016/4/18.
 */
public class BitmapUtils {

    /**
     * 等比例获取缩略图
     * @param filePath 图片路径
     * @param width 缩放的宽度范围
     * @param height 缩放的高度范围
     * @param flag 以较大缩放比例缩放图片为true，反之为false
     * @return 缩放后的图片
     */
    public static Bitmap getThumbnail(String filePath, int width, int height, boolean flag) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        float realHeight = options.outHeight;
        float realWidth = options.outWidth;

        int scale = (int) (realHeight / (float)height);
        int scale1 = (int) (realWidth / (float)width);
        if (scale1 >= scale && flag) {
            scale = scale1;
        } else if (scale1 < scale && !flag) {
            scale = scale1;
        }
        if (scale <= 0) {
            scale = 1;
        }
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 保存bitmap图片，PNG格式
     * @param bitmap image
     * @param filePath 保存路径
     * @throws Exception
     */
    public static void saveBitmap(Bitmap bitmap, int quality, String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fos = new FileOutputStream(file);

        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
        bitmap.recycle();
        bitmap = null;
    }

    public static void saveBitmap(String path, int width, int height, int quality, boolean flag, String outpath) throws IOException {
        Bitmap bitmap = getThumbnail(path, width, height, flag);

        saveBitmap(bitmap, quality, outpath);
    }
}
