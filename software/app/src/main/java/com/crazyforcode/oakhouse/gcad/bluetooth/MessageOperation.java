package com.crazyforcode.oakhouse.gcad.bluetooth;

import android.util.Log;

import com.crazyforcode.oakhouse.gcad.kernel.tools.GCADApplication;
import com.crazyforcode.oakhouse.gcad.kernel.tools.toast.TextToast;

import static com.crazyforcode.oakhouse.gcad.bluetooth.MessageConstant.CommandWord.*;

public class MessageOperation {

    public static void operation(Message message) {
        switch (message.getCommandWord()) {
            case data:
                Log.i("数据传输", "获取XYZ相对分量");
                byte[] body = message.getBody();
                double x = ByteUtil.getDouble(body, 0);
                double y = ByteUtil.getDouble(body, message.getLength() / 3);
                double z = ByteUtil.getDouble(body, message.getLength() / 3 * 2);
                //TODO x y z operation
                if(PositionDeal.isInit()) {
                    float[] data3D = new float[]{(float) x, (float) y, (float) z};
                    PositionDeal.changeToCurrentPosition(data3D);
                }
                break;
            case init_success:
                Log.i("初始化", "成功");
                TextToast.showTextToast("用户初始化成功", GCADApplication.getContext());
                break;
            case init_fail:
                Log.i("初始化", "失败");
                TextToast.showTextToast("用户初始化失败", GCADApplication.getContext());
                break;
            case id_password:
                Log.i("用户身份识别", "获取信息");
                //TODO 用户身份识别
                break;
            case change_success:
                Log.i("用户密码更改", "成功");
                TextToast.showTextToast("用户密码更改成功", GCADApplication.getContext());
                break;
            case change_fail:
                Log.i("用户密码更改", "失败");
                TextToast.showTextToast("用户密码更改失败", GCADApplication.getContext());
                break;
//            case loss_data:
//                Log.i("数据传输", "丢失");
//                int pgt = message.getLastGetDate();
//                if(Decoder.isLossSend(pgt))
//                    Decoder.setLossSend(pgt);
//                break;
        }
    }
}
