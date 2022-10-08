package com.crazyforcode.oakhouse.gcad.bluetooth;

public class Message {

    //运输层部分
    private int date;
    private int lastGetDate;
    private int lastSendDate;
    private int length;

    //业务层部分
    private short commandWord;
    private byte[] body;

    public int getDate() {
        return date;
    }

    public void setDate() {
        this.date = (int)(System.currentTimeMillis() / 1000);
    }

    public int getLastGetDate() {
        return lastGetDate;
    }

    public void setLastGetDate(int lastGetDate) {
        this.lastGetDate = lastGetDate;
    }

    public int getLastSendDate() {
        return lastSendDate;
    }

    public void setLastSendDate(int lastSendDate) {
        this.lastSendDate = lastSendDate;
    }

    public int getLength() {
        return body == null? 0 : body.length;
    }

    public short getCommandWord() {
        return commandWord;
    }

    public void setCommandWord(short commandWord) {
        this.commandWord = commandWord;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public byte[] getByByteArray() {
        byte[] data = new byte[getTransportLayer().length + getBusinessLayer().length + 1];

        int i = 0;

        for (byte aByte : getTransportLayer())
            data[i++] = aByte;

        for (byte aByte : getBusinessLayer())
            data[i++] = aByte;

        byte checkByte = getCheckByte();

        data[data.length - 1] = checkByte;

        return data;
    }

    public void setByByteArray(byte[] array) {
        byte[] data = new byte[array.length - 1];
        System.arraycopy(array, 0, data, 0, data.length);

        //获取外设名称
        short device = ByteUtil.getShort(data, 0);

        switch(device) {
            case MessageConstant.Equipment.PRD:
                //获取命令字
                setCommandWord(ByteUtil.getShort(data, 16));
                //获取消息体长度
                length = ByteUtil.getShort(data, 14);
                //获取消息体
                if(length > 0) {
                    body = new byte[length];
                    System.arraycopy(data, 0, body, 18, length);
                }
                //TODO 获取并处理时间
                setLastGetDate(Decoder.getLastGetTime());
                setLastSendDate(Encoder.getLastSendTime());
                break;
        }
    }

    public static boolean judgeCheckByte(byte[] data) {
        byte[] messageData = new byte[data.length - 1];
        byte checkByte = data[data.length - 1];

        System.arraycopy(data, 0, messageData, 0, data.length - 1);

        byte currentCheckByte = 0;

        for (byte aByte : messageData)
            currentCheckByte ^= aByte;

        return checkByte == currentCheckByte;
    }

    private byte[] getTransportLayer() {
        byte[] data = new byte[16];

        ByteUtil.putShort(data, MessageConstant.Equipment.GCAD, 0);
        ByteUtil.putInt(data, date, 2);
        ByteUtil.putInt(data, lastGetDate, 6);
        ByteUtil.putInt(data, lastSendDate, 10);
        ByteUtil.putShort(data, (short)length, 14);

        return data;
    }

    private byte[] getBusinessLayer() {
        byte[] data = new byte[2 + getLength()];

        ByteUtil.putShort(data, getCommandWord(), 0);
        System.arraycopy(getBody(), 0, data, 2, body.length);

        return data;
    }

    private byte getCheckByte() {
        byte checkByte = 0;
        for (byte aByte : getTransportLayer())
            checkByte ^= aByte;    //逐字异或

        for (byte aByte : getBusinessLayer())
            checkByte ^= aByte;    //逐字异或

        return checkByte;
    }
}
