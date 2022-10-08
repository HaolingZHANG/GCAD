package com.crazyforcode.oakhouse.gcad.others.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.crazyforcode.oakhouse.gcad.others.surfaces.SearchAllProjects;

@SuppressWarnings("deprecation")
public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private Bitmap[] mImageIdsBitmap = null;
    private ImageView[] mImages;

    public ImageAdapter(Context c, Bitmap[] ImageIds) {
        mContext = c;
        mImageIdsBitmap = ImageIds;
        mImages = new ImageView[mImageIdsBitmap.length];
    }

    public boolean createReflectedImages() {
        final int reflectionGap = 4;
        int index = 0;

        for (Bitmap imageId : mImageIdsBitmap) {
            Bitmap originalImage = Bitmap.createBitmap(imageId);
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            Matrix matrix = new Matrix();
            matrix.preScale(1, -1);

            Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height / 2, width, height / 2, matrix, false);

            Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmapWithReflection);

            canvas.drawBitmap(originalImage, 0, 0, null);

            Paint defaultPaint = new Paint();
            canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);

            canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

            Paint paint = new Paint();
            LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0, bitmapWithReflection.getHeight()
                    + reflectionGap, 0x70ffffff, 0x00ffffff, Shader.TileMode.CLAMP);

            paint.setShader(shader);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

            canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);

            ImageView imageView = new ImageView(mContext);
            imageView.setImageBitmap(bitmapWithReflection);
            imageView.setLayoutParams(new Gallery.LayoutParams(250, 340));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            mImages[index++] = imageView;
        }
        return true;
    }

    public int getCount() {
            return mImageIdsBitmap.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        SearchAllProjects.setIntroduce(position);
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        return mImages[position];
    }
}