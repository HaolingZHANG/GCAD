package com.crazyforcode.oakhouse.gcad.bluetooth;

public class ByteUtil {

    public static void putShort(byte[] data, short res, int index) {
        data[index + 1] = (byte) (res >> 8);
        data[index] = (byte) (res);
    }

    public static short getShort(byte[] data, int index) {
        return (short) ((data[index + 1] << 8) | data[index] & 0xff);
    }

    public static void putInt(byte[] data, int res, int index) {
        data[index + 3] = (byte) (res >> 24);
        data[index + 2] = (byte) (res >> 16);
        data[index + 1] = (byte) (res >> 8);
        data[index] = (byte) (res);
    }

    public static int getInt(byte[] data, int index) {
        return ((data[index + 3] & 0xff) << 24)
                | ((data[index + 2] & 0xff) << 16)
                | ((data[index + 1] & 0xff) << 8)
                | ((data[index] & 0xff));
    }

    public static void putLong(byte[] data, long res, int index) {
        data[index + 7] = (byte) (res >> 56);
        data[index + 6] = (byte) (res >> 48);
        data[index + 5] = (byte) (res >> 40);
        data[index + 4] = (byte) (res >> 32);
        data[index + 3] = (byte) (res >> 24);
        data[index + 2] = (byte) (res >> 16);
        data[index + 1] = (byte) (res >> 8);
        data[index] = (byte) (res);
    }

    public static long getLong(byte[] data, int index) {
        return    (((long) data[index + 7] & 0xff) << 56)
                | (((long) data[index + 6] & 0xff) << 48)
                | (((long) data[index + 5] & 0xff) << 40)
                | (((long) data[index + 4] & 0xff) << 32)
                | (((long) data[index + 3] & 0xff) << 24)
                | (((long) data[index + 2] & 0xff) << 16)
                | (((long) data[index + 1] & 0xff) << 8)
                | (((long) data[index] & 0xff));
    }

    public static void putChar(byte[] data, char res, int index) {
        int temp = (int) res;
        for (int i = 0; i < 2; i ++ ) {
            // 将最高位保存在最低位
            data[index + i] = Integer.valueOf(temp & 0xff).byteValue();
            // 向右移8位
            temp = temp >> 8;
        }
    }

    public static char getChar(byte[] data, int index) {
        int pro = 0;
        if (data[index + 1] > 0)
            pro += data[index + 1];
        else
            pro += 256 + data[index];
        pro *= 256;
        if (data[index] > 0)
            pro += data[index + 1];
        else
            pro += 256 + data[index];
        return (char) pro;
    }

    public static void putString(byte[] data, String res, int index) {
        byte[] targets = res.getBytes();
        System.arraycopy(targets, 0, data, index, targets.length);
    }

    public static String getString(byte[] data, int index, int length) {
        StringBuilder builder = new StringBuilder();
        for(int i = index; i < length; i += 2) {
            byte[] temp = new byte[2];
            temp[0] = data[i];
            temp[1] = data[i + 1];
            builder.append(getChar(temp, 0));
        }
        return builder.toString();
    }

    public static void putFloat(byte[] data, float res, int index) {
        int pro = Float.floatToIntBits(res);
        for (int i = 0; i < 4; i++) {
            data[index + i] = Integer.valueOf(pro).byteValue();
            pro = pro >> 8;
        }
    }

    public static float getFloat(byte[] data, int index) {
        int pro;
        pro = data[index];
        pro &= 0xff;
        pro |= ((long) data[index + 1] << 8);
        pro &= 0xffff;
        pro |= ((long) data[index + 2] << 16);
        pro &= 0xffffff;
        pro |= ((long) data[index + 3] << 24);
        return Float.intBitsToFloat(pro);
    }

    public static void putDouble(byte[] data, double res, int index) {
        long pro = Double.doubleToLongBits(res);
        for (int i = 0; i < 4; i++) {
            data[index + i] = Long.valueOf(pro).byteValue();
            pro = pro >> 8;
        }
    }

    public static double getDouble(byte[] data, int index) {
        long pro;
        pro = data[index];
        pro &= 0xff;
        pro |= ((long) data[index + 1] << 8);
        pro &= 0xffff;
        pro |= ((long) data[index + 2] << 16);
        pro &= 0xffffff;
        pro |= ((long) data[index + 3] << 24);
        pro &= 0xffffffffL;
        pro |= ((long) data[index + 4] << 32);
        pro &= 0xffffffffffL;
        pro |= ((long) data[index + 5] << 40);
        pro &= 0xffffffffffffL;
        pro |= ((long) data[index + 6] << 48);
        pro &= 0xffffffffffffffL;
        pro |= ((long) data[index + 7] << 56);
        return Double.longBitsToDouble(pro);
    }
}
