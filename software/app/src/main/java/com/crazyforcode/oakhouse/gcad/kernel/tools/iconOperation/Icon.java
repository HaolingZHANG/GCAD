package com.crazyforcode.oakhouse.gcad.kernel.tools.iconOperation;


public class Icon {

    private int picRes;
    private int operateCode;
    private int notesRes;

    public Icon(int picRes, int operateCode, int notesRes) {
        this.picRes = picRes;
        this.operateCode = operateCode;
        this.notesRes = notesRes;
    }

    public int getLegendRes() {
        return this.picRes;
    }

    public int getOperateCode() {
        return this.operateCode;
    }

    public int getNoteRes() {
        return this.notesRes;
    }
}
