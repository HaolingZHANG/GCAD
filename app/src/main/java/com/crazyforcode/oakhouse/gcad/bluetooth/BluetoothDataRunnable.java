package com.crazyforcode.oakhouse.gcad.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MHandler;
import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MRunnable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import static com.crazyforcode.oakhouse.gcad.bluetooth.Encoder.encoderData;

public class BluetoothDataRunnable extends MRunnable {

    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private static OutputStream outputStream;

    public BluetoothDataRunnable(MHandler handler, BluetoothDevice device) {
        super(handler);

        try {
            this.bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(getClientUUID());
            bluetoothSocket.connect();
            inputStream = bluetoothSocket.getInputStream();
            outputStream = bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void running() {
        read();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void read() {
        byte[] buffer = new byte[1024];

        while (true) try {
            inputStream.read(buffer);
            Decoder.decoderData(buffer);
        } catch (IOException e) {
            Log.i("数据传输给软件的过程", "失误");
            break;
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static void write(byte[] message) {
        try {
            outputStream.write(message);
            outputStream.flush();
        } catch (IOException e) {
            Log.i("数据传输给硬件的过程", "失误");
        }
    }

    public static void write(ArrayList<byte[]> messages) {
        try {
            for(byte[] message : messages)
                outputStream.write(message);
        } catch (IOException e) {
            Log.i("数据传输给硬件的过程", "失误");
        }
    }

    private UUID getClientUUID() {
        UUID uuid = UUID.randomUUID();
        Log.d("UUID", uuid.toString());
        return uuid;
    }
}
