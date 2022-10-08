package com.crazyforcode.oakhouse.gcad.kernel.tools.mapSet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MapCutting {

    private static int piece = 0;

    public static void split(InputStream stream, String projectPath) throws IOException {
        BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(stream, false);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        int width = options.outWidth;
        int height = options.outHeight;

        piece = (int)(Math.max(width, height) / 500.0);

        if(piece * 500 < Math.max(width, height))
            piece++;

        int pieceWidth = (int)(width / (float)piece);
        int pieceHeight = (int)(height / (float)piece);

        for(int row = 0; row < piece; row++) {
            for(int col = 0; col < piece; col++) {
                int xValue = col * pieceWidth;
                int yValue = row * pieceHeight;
                Bitmap temp = decoder.decodeRegion(new Rect(xValue, yValue, xValue + pieceWidth, yValue + pieceHeight), options);
                writeToFile(temp, projectPath + "/Map/Chips/" + String.valueOf(row * piece + col) + ".jpeg");
            }
        }
        decoder.recycle();
    }

    private static void writeToFile(Bitmap bitmap, String path) throws FileNotFoundException {
        FileOutputStream output = new FileOutputStream(new File(path));
        Log.i("chips path", path);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
    }

    public static int getPiece() {
        return piece;
    }
}

