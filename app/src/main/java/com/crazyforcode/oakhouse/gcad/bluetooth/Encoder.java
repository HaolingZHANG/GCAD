package com.crazyforcode.oakhouse.gcad.bluetooth;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Encoder {

    private static List<Message> lastMessages = new LinkedList<>();

    public static byte[] encoderData(Message message) {
        saveLast(message);
        return encoderLastData();
    }

    public static byte[] encoderLastData() {
        return getLastMessage().getByByteArray();
    }

    public static ArrayList<byte[]> encoderLoseData(long lastGetDate) {
        ArrayList<byte[]> messages = new ArrayList<>();

        for (Message message : lastMessages)
            if (message.getLastSendDate() >= lastGetDate)
                messages.add(message.getByByteArray());

        return messages;
    }

    //保存近50条已发送的message
    private static void saveLast(Message message) {
        if(lastMessages.size() > 50)
            lastMessages.remove(0);

        lastMessages.add(message);
    }

    private static Message getLastMessage() {
        return lastMessages.get(lastMessages.size() - 1);
    }

    //上次GCAD发给外设时刻的蓝牙时间
    public static int getLastSendTime() {
        return lastMessages.size() == 0 ? 0 : lastMessages.get(lastMessages.size() - 1).getDate();
    }
}
