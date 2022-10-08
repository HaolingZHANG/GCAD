package com.crazyforcode.oakhouse.gcad.draw.legendPaint;

import android.graphics.Paint;
import android.graphics.Path;

import com.crazyforcode.oakhouse.gcad.draw.assist.AlgoPoint;
import com.crazyforcode.oakhouse.gcad.draw.assist.CurveControl;

/**
 * Created by Master_Jedi on 2016/03/29.
 */
public abstract class EdgeObject extends DrawObject {
    public CurveControl curve = null;

    //    Line Object
    protected Path pathMain;

    protected Paint paintMain;
    protected Paint paintAuxiliary;


    //only for non-straight object, curve is the key judge is a object straight or curve(/free);
    public void initCurve() {
        if (curve == null)
            curve = new CurveControl();
        else/*(!curve.isEmpty())*/
            curve.clean();


        AlgoPoint bis = null;
        float distan1 = 0, distan2 = 0;
        for (int i = 1; i + 1 < getSize(); i++) {
            if (i == 1) {
                curve.addOneSideBezier(getPoint(i).x, getPoint(i).y, CurveControl.Direction.TO);
                curve.addOneSideBezier(getPoint(i).x, getPoint(i).y, CurveControl.Direction.FROM);
                continue;
            }

            bis = AlgoPoint.bisector(getPoint(i - 1), getPoint(i), getPoint(i + 1));
            distan1 = (float) AlgoPoint.distance(getPoint(i - 1), getPoint(i));
            distan2 = (float) AlgoPoint.distance(getPoint(i), getPoint(i + 1));
            if (i - 1 == 0) {
                curve.addOneSideBezier(getPoint(i - 1).x
                                - distan1 * bis.y,
                        getPoint(i - 1).y
                                - distan1 * bis.x,
                        CurveControl.Direction.FROM);
                curve.addOneSideBezier(getPoint(i - 1).x,
                        getPoint(i - 1).y,
                        CurveControl.Direction.TO);
            }

            curve.addOneSideBezier(getPoint(i).x - distan1 * bis.y,
                    getPoint(i).y - distan1 * bis.x,
                    CurveControl.Direction.TO);
            curve.addOneSideBezier(getPoint(i).x + distan2 * bis.y,
                    getPoint(i).y + distan2 * bis.y,
                    CurveControl.Direction.FROM);

            if (i + 1 == getSize() - 1) {
                curve.addOneSideBezier(getPoint(i + 1).x - distan2 * bis.y,
                        getPoint(i).y - distan2 * bis.x,
                        CurveControl.Direction.TO);
                curve.addOneSideBezier(getPoint(i + 1).x,
                        getPoint(i + 1).y,
                        CurveControl.Direction.FROM);
            }
        }
    }


    public Path getPathMain() {
        return pathMain;
    }

    @Override
    public Paint getPaintMain() {
        return paintMain;
    }

    @Override
    public Paint getPaintAuxiliary() {
        return paintAuxiliary;
    }

    protected Path getMapPath() {
        if (getSize() <= 0)
            return pathMain;

        Path mapPath = new Path();

        mapPath.moveTo(getPoint(0).x, getPoint(0).y);
        for (int i = 0; i + 1 < getSize(); i++) {
            if (getPoint(i + 1).isHead()) {
                mapPath.moveTo(getPoint(i + 1).x, getPoint(i + 1).y);
                continue;
            }

            if (curve != null)/**is curve edge*/ {
                 mapPath.cubicTo(curve.getControlPoint(i, CurveControl.Direction.FROM).x,
                        curve.getControlPoint(i, CurveControl.Direction.FROM).y,
                        curve.getControlPoint(i + 1, CurveControl.Direction.TO).x,
                        curve.getControlPoint(i + 1, CurveControl.Direction.TO).y,
                        getPoint(i + 1).x, getPoint(i + 1).y);
            }else {
                mapPath.lineTo(getPoint(i + 1).x, getPoint(i + 1).y);
            }
        }

        return mapPath;
    }
}
