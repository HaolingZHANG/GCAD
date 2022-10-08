package com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.Log;

import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MHandler;
import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MRunnable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CopyFileRunnable extends MRunnable {

    private File sourceFile;
    private static File targetFile;

    public CopyFileRunnable(MHandler handler, File sourceFile, File target) {
        super(handler);
        this.sourceFile = sourceFile;
        targetFile = target;
    }

    @Override
    public void running() {
        Log.i("copy file", "copy totalMap in project");
        Message message;
        try {
//            新建文件输入流并对它进行缓冲
            BufferedInputStream input = new BufferedInputStream(new FileInputStream(sourceFile));
//            新建文件输出流并对它进行缓冲
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(targetFile));

//            缓冲数组
            byte[] bytes = new byte[1024 * 5];
            int len;
            while ((len = input.read(bytes)) != -1)
                output.write(bytes, 0, len);

//            刷新此缓冲的输出流
            output.flush();
//            关闭流
            input.close();
            output.close();

            saveThumbnail();
            message = getMHandler().obtainMessage(MHandler.COPY_FILE_INIT, MHandler.SUCCESS, 0);
            getMHandler().sendMessageAtTime(message, 0);

        } catch (IOException e) {
            message = getMHandler().obtainMessage(MHandler.COPY_FILE_INIT, MHandler.FAILURE, 0);
            getMHandler().sendMessageAtTime(message, 0);
        }
    }

    public static File getProjectFile() {
        return targetFile;
    }

    private static void saveThumbnail() throws IOException {
        String path = targetFile.getPath();

        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;

        int compress = (int)(options.outHeight / (float)200);
        if(compress <= 0)
            compress = 1;

        options.inSampleSize = compress;
        Bitmap thumbnail = BitmapFactory.decodeFile(path, options);

        path = path.substring(0, path.lastIndexOf('/') + 1);
        path += "Thumbnail.gcadP";

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        FileOutputStream os = new FileOutputStream(new File(path));
        os.write(stream.toByteArray());
        os.close();
    }
}
