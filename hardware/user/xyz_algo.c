															   //
//  test1.c
//  gcadtest
//
//  Created by Dolia on 9/18/16.
//  Copyright ? 2016 Dolia. All rights reserved.
//
#include "option.h"

struct SysVector {
	float north;
	float east;
	float sky;
};
#define PI 3.14159265358979323846

/**
* 鞋子部分的长宽高 单位为毫米
* SHOES_LONG 鞋子部分 长
* SHOES_WIDE 鞋子部分 宽
* SHOES_HEIGHT 鞋子部分 高
*/
float SHOES_LONG = 250;
float SHOES_WIDE = 50;
float SHOES_HEIGHT = 100;

/**
* 腰部分固体长宽高 固定为毫米
* WAIST_WIDE 腰部分 宽
* WAIST_HEIGHT 腰部分 高 （用不上 因为左右的高抵消了）
*/
float WAIST_WIDE = 50;
//#define WAIST_HEIGHT = 20;
float data[3] = { 0 };

struct SysVector initVector(struct pitchRollYaw_st p, int chooser) {
	struct SysVector vector;
	switch (chooser) {
	case 1:
		vector.north = (float)cos(p.fYaw   * 180.0 / PI);
		vector.east = (float)-sin(p.fYaw   * 180.0 / PI);
		vector.sky = (float)tan(p.fPitch * 180.0 / PI);
		break;
	case 2:
		vector.north = (float)(sin(p.fYaw * 180.0 / PI)
			- cos(p.fYaw * 180.0 / PI)
			* sin(p.fPitch * 180.0 / PI)
			* tan(p.fRoll * 180.0 / PI));
		vector.east = (float)(cos(p.fYaw * 180.0 / PI)
			+ sin(p.fYaw * 180.0 / PI)
			* sin(p.fPitch * 180.0 / PI)
			* tan(p.fRoll * 180.0 / PI));
		vector.sky = (float)(cos(p.fPitch * 180.0 / PI)
			* tan(p.fRoll * 180.0 / PI));
		break;
	case 3:
		vector.north = (float)(-sin(p.fYaw * 180.0 / PI)
			- cos(p.fYaw * 180.0 / PI)
			* sin(p.fPitch * 180.0 / PI)
			/ tan(p.fRoll * 180.0 / PI));
		vector.east = (float)(-cos(p.fYaw * 180.0 / PI)
			+ sin(p.fYaw * 180.0 / PI)
			* sin(p.fPitch * 180.0 / PI)
			/ tan(p.fRoll * 180.0 / PI));
		vector.sky = (float)(cos(p.fPitch * 180.0 / PI)
			/ tan(p.fRoll * 180.0 / PI));
		break;
	}
	return vector;
}

/**
* 计算脚部三维变量
* @param pitchRollYaw_st 对应脚所得姿
* @param isLeft 是否为左脚
* @return 脚部的东北天坐标系的长度分量
*/

float *getFoot(struct pitchRollYaw_st pitchRollYaw_st, int isLeft)
{
	//    float *data = (float *) new float[] {0,0,0};

	struct SysVector vectorNorth;
	struct SysVector vectorEast;
	struct SysVector vectorSky;
	// 计算鞋长部分
	vectorNorth = initVector(pitchRollYaw_st, 1);
	data[0] += SHOES_LONG * vectorNorth.north;
	data[1] += SHOES_LONG * vectorNorth.east;
	data[2] += SHOES_LONG * vectorNorth.sky;

	// 计算鞋宽部分
	vectorEast = initVector(pitchRollYaw_st, 2);
	data[0] += SHOES_WIDE * vectorEast.north;
	data[2] += SHOES_WIDE * vectorEast.sky;
	if (isLeft)
		data[1] += SHOES_WIDE * vectorEast.east;
	else
		data[1] -= SHOES_WIDE * vectorEast.east;

	// 计算鞋高部分 鞋高垂直于鞋长
	vectorSky = initVector(pitchRollYaw_st, 3);
	data[0] += SHOES_HEIGHT * vectorSky.north;
	data[1] += SHOES_HEIGHT * vectorSky.east;
	data[2] += SHOES_HEIGHT * vectorSky.sky;

	return data;
}
/**
* 计算腿部三维变量 共4个部分 左右大腿 左右小腿
* @param pitchRollYaw_st 对应腿部分所得姿态
* @param length 红外测量长度
* @return 腿部的东北天坐标系的长度分量
*/
float *getLeg(struct pitchRollYaw_st pitchRollYaw_st, float length) {
	struct SysVector vectorSky = initVector(pitchRollYaw_st, 3);

	data[0] = length * vectorSky.north;
	data[1] = length * vectorSky.east;
	data[2] = length * vectorSky.sky;

	return data;
}

/**
* 计算腰部三维变量
* @param pitchRollYaw_st 腰所得姿态
* @param length 总长度
* @return 腰部的东北天坐标系的长度分量
*/
float *getWaist(struct pitchRollYaw_st pitchRollYaw_st, float length) {
	struct SysVector vectorNorth;
	struct SysVector vectorEast;
	// 计算腰宽部分
	vectorNorth = initVector(pitchRollYaw_st, 1);
	data[0] = WAIST_WIDE * vectorNorth.north;
	data[1] = WAIST_WIDE * vectorNorth.east;
	data[2] = WAIST_WIDE * vectorNorth.sky;

	// 计算腰长部分
	vectorEast = initVector(pitchRollYaw_st, 2);
	data[0] += length * vectorEast.north;
	data[1] += length * vectorEast.east;
	data[2] += length * vectorEast.sky;
	// 腰高部分左右抵消 所以不用计算

	return data;
}

/**
* 求得东北天坐标系上的分量
* @param measureLens 实际长度 顺序0腰部 1右大腿 2右小腿 3左大腿 4左小腿
* @param astPRDpry 部件姿态 0腰部 1右腿 2右小腿 3左腿 4左小腿 5右脚 6左脚
* @param xyz 计算好的东北天坐标系值 x--天方向 y--东向量 z--天向量
* @param isLeft 触及脚是否为左脚
* @return 是否成功 0 成功 1 失败
*/
int pitchrollyawToPRDxyz(float *measureLens, struct pitchRollYaw_st *astPRDpry, float *xyz, int isLeft)
{
    int i;
	xyz[0] = 0;
	xyz[1] = 0;
	xyz[2] = 0;

	if (isLeft) {
		//左大腿
		float *temp = getLeg(astPRDpry[3], measureLens[3]);
		for (i = 0; i < 3; i++)
			xyz[i] += temp[i];
		//左小腿
		temp = getLeg(astPRDpry[4], measureLens[4]);
		for (i = 0; i < 3; i++)
			xyz[i] += temp[i];
		//左脚
		temp = getFoot(astPRDpry[6], 1);
		for (i = 0; i < 3; i++)
			xyz[i] += temp[i];
		//右大腿
		temp = getLeg(astPRDpry[1], measureLens[1]);
		for (i = 0; i < 3; i++)
			xyz[i] -= temp[i];
		//右小腿
		temp = getLeg(astPRDpry[2], measureLens[2]);
		for (i = 0; i < 3; i++)
			xyz[i] -= temp[i];
		//右脚
		temp = getFoot(astPRDpry[5], 1);
		for (i = 0; i < 3; i++)
			xyz[i] -= temp[i];
		//腰部
		temp = getWaist(astPRDpry[0], measureLens[0]);
		for (i = 0; i < 3; i++)
			xyz[i] -= temp[i];
	}
	else {
		//右大腿
		float *temp = getLeg(astPRDpry[1], measureLens[1]);
		for (i = 0; i < 3; i++)
			xyz[i] += temp[i];
		//右小腿
		temp = getLeg(astPRDpry[2], measureLens[2]);
		for (i = 0; i < 3; i++)
			xyz[i] += temp[i];
		//右脚
		temp = getFoot(astPRDpry[5], 0);
		for (i = 0; i < 3; i++)
			xyz[i] += temp[i];
		//左大腿
		temp = getLeg(astPRDpry[3], measureLens[3]);
		for (i = 0; i < 3; i++)
			xyz[i] -= temp[i];
		//左小腿
		temp = getLeg(astPRDpry[4], measureLens[4]);
		for (i = 0; i < 3; i++)
			xyz[i] -= temp[i];
		//左脚
		temp = getFoot(astPRDpry[6], 1);
		for (i = 0; i < 3; i++)
			xyz[i] -= temp[i];
		//腰部
		temp = getWaist(astPRDpry[0], measureLens[0]);
		for (i = 0; i < 3; i++)
			xyz[i] += temp[i];
	}

	return 0;
}
