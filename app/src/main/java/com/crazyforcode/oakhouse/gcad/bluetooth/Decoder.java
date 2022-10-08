package com.crazyforcode.oakhouse.gcad.bluetooth;

import android.util.Log;

public class Decoder {

    private static int lastGetTime = 0;

    public static void decoderData(byte[] buffer) {
        if(Message.judgeCheckByte(buffer)) {
            Message message = new Message();
            message.setByByteArray(buffer);
            lastGetTime = message.getDate();
            checkLoss(message.getLastGetDate(), message.getLastSendDate());
            if(message.getCommandWord() != MessageConstant.CommandWord.loss_data)
                MessageOperation.operation(message);
        } else {
            Log.i("数据传输", "错误");
            setLossGet();
        }
    }

    private static void checkLoss(int pgt, int pst) {
        if(isLossGet(pst))
            setLossGet();

        if(isLossSend(pgt))
            setLossSend(pgt);
    }

    //上次外设发给GCAD时刻的蓝牙时间
    public static int getLastGetTime() {
        return lastGetTime;
    }

    private static boolean isLossGet(int pst) {
        return lastGetTime < pst;
    }

    //GCAD未成功获取信息
    private static void setLossGet() {
        Message loss = new Message();
        loss.setDate();
        loss.setLastSendDate(Encoder.getLastSendTime());
        loss.setLastGetDate(getLastGetTime());
        loss.setCommandWord(MessageConstant.CommandWord.loss);

        BluetoothDataRunnable.write(Encoder.encoderData(loss));
    }

    public static boolean isLossSend(int pgt) {
        return Encoder.getLastSendTime() > pgt;
    }

    //GCAD未成功发送信息
    public static void setLossSend(int pgt) {
        Encoder.encoderLoseData(pgt);
    }
}
