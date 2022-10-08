package com.crazyforcode.oakhouse.gcad.bluetooth;

public final class MessageConstant {

    final class Equipment {
        public static final short GCAD = 0x0000;
        public static final short PRD =0x0001;
    }

    final class CommandWord {
//        用于初始化外设用户信息
        public static final short init = 0x0000;
//        用于外设开始时的用户登陆
        public static final short land = 0x0001;
//        用于更改外设用户密码
        public static final short change = 0x0002;
//        用于外设解绑成为初始化状态
        public static final short unlink = 0x0003;

//        用于外设被他人使用时短信提示外设所在用户（第二版本使用）
        public static final short loss = 0x0010;

//        用于外设传递每步数据
        public static final short data = 0x002D;

//        用于检测外设是否损坏
        public static final short test = 0x0030;

//        用于表示外设初始化成功
        public static final short init_success = 0x00A1;
//        用于表示外设初始化失败
        public static final short init_fail = 0x00A2;
//        用于外设提出登陆校验
        public static final short id_password = 0x00A3;

//        用于表示外设密码更改成功
        public static final short change_success = 0x00A4;
//        用于表示外设密码更改失败
        public static final short change_fail = 0x00A5;

//        用于表示丢失数据包需要重传
        public static final short loss_data = 0x00A6;
//        用于表示外设已经损坏（第二版本使用）
        public static final short device_damage = 0x00A7;
//        用于表示外设没有损坏（第二版本使用）
        public static final short device_intact = 0x00A8;
    }
}
