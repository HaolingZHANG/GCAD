package com.crazyforcode.oakhouse.gcad.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MHandler;
import com.crazyforcode.oakhouse.gcad.kernel.tools.threadPool.MThreadPool;
import com.crazyforcode.oakhouse.gcad.kernel.tools.toast.TextToast;

import java.util.Set;

public class SetBlueTooth {

    private static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static boolean isConnected = false;
    private static String clientDeviceName;

    public static void initBlueTooth(Context context) {

        if(bluetoothAdapter.isEnabled()) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    Log.d("pairedDevices", device.getName());
                    if(device.getName().equals(clientDeviceName)) {
                        isConnected = true;
                        TextToast.showTextToast("已连接设备 ： " + device.getName(), context);

//                        设立新的线程
                        MHandler mHandler = new MHandler(context);
                        BluetoothDataRunnable bluetoothGetDataRunnable =
                                new BluetoothDataRunnable(mHandler, device);

//                        将该线程放入线程池并执行
                        MThreadPool.addTask(bluetoothGetDataRunnable);
                        MThreadPool.start();
                    }
                }
                if(!isConnected)
                    TextToast.showTextToast("未连接设备", context);

            } else
                    TextToast.showTextToast("未连接设备", context);
        } else
                TextToast.showTextToast("未连接设备", context);
    }

    public static void deleteWhenQuit() {
        if(bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        }
    }

    public static void setBluetoothDeviceName(String name) {
        clientDeviceName = name;
    }
}
