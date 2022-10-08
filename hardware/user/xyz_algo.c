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
* Ь�Ӳ��ֵĳ���� ��λΪ����
* SHOES_LONG Ь�Ӳ��� ��
* SHOES_WIDE Ь�Ӳ��� ��
* SHOES_HEIGHT Ь�Ӳ��� ��
*/
float SHOES_LONG = 250;
float SHOES_WIDE = 50;
float SHOES_HEIGHT = 100;

/**
* �����ֹ��峤��� �̶�Ϊ����
* WAIST_WIDE ������ ��
* WAIST_HEIGHT ������ �� ���ò��� ��Ϊ���ҵĸߵ����ˣ�
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
* ����Ų���ά����
* @param pitchRollYaw_st ��Ӧ��������
* @param isLeft �Ƿ�Ϊ���
* @return �Ų��Ķ���������ϵ�ĳ��ȷ���
*/

float *getFoot(struct pitchRollYaw_st pitchRollYaw_st, int isLeft)
{
	//    float *data = (float *) new float[] {0,0,0};

	struct SysVector vectorNorth;
	struct SysVector vectorEast;
	struct SysVector vectorSky;
	// ����Ь������
	vectorNorth = initVector(pitchRollYaw_st, 1);
	data[0] += SHOES_LONG * vectorNorth.north;
	data[1] += SHOES_LONG * vectorNorth.east;
	data[2] += SHOES_LONG * vectorNorth.sky;

	// ����Ь����
	vectorEast = initVector(pitchRollYaw_st, 2);
	data[0] += SHOES_WIDE * vectorEast.north;
	data[2] += SHOES_WIDE * vectorEast.sky;
	if (isLeft)
		data[1] += SHOES_WIDE * vectorEast.east;
	else
		data[1] -= SHOES_WIDE * vectorEast.east;

	// ����Ь�߲��� Ь�ߴ�ֱ��Ь��
	vectorSky = initVector(pitchRollYaw_st, 3);
	data[0] += SHOES_HEIGHT * vectorSky.north;
	data[1] += SHOES_HEIGHT * vectorSky.east;
	data[2] += SHOES_HEIGHT * vectorSky.sky;

	return data;
}
/**
* �����Ȳ���ά���� ��4������ ���Ҵ��� ����С��
* @param pitchRollYaw_st ��Ӧ�Ȳ���������̬
* @param length �����������
* @return �Ȳ��Ķ���������ϵ�ĳ��ȷ���
*/
float *getLeg(struct pitchRollYaw_st pitchRollYaw_st, float length) {
	struct SysVector vectorSky = initVector(pitchRollYaw_st, 3);

	data[0] = length * vectorSky.north;
	data[1] = length * vectorSky.east;
	data[2] = length * vectorSky.sky;

	return data;
}

/**
* ����������ά����
* @param pitchRollYaw_st ��������̬
* @param length �ܳ���
* @return �����Ķ���������ϵ�ĳ��ȷ���
*/
float *getWaist(struct pitchRollYaw_st pitchRollYaw_st, float length) {
	struct SysVector vectorNorth;
	struct SysVector vectorEast;
	// ����������
	vectorNorth = initVector(pitchRollYaw_st, 1);
	data[0] = WAIST_WIDE * vectorNorth.north;
	data[1] = WAIST_WIDE * vectorNorth.east;
	data[2] = WAIST_WIDE * vectorNorth.sky;

	// ������������
	vectorEast = initVector(pitchRollYaw_st, 2);
	data[0] += length * vectorEast.north;
	data[1] += length * vectorEast.east;
	data[2] += length * vectorEast.sky;
	// ���߲������ҵ��� ���Բ��ü���

	return data;
}

/**
* ��ö���������ϵ�ϵķ���
* @param measureLens ʵ�ʳ��� ˳��0���� 1�Ҵ��� 2��С�� 3����� 4��С��
* @param astPRDpry ������̬ 0���� 1���� 2��С�� 3���� 4��С�� 5�ҽ� 6���
* @param xyz ����õĶ���������ϵֵ x--�췽�� y--������ z--������
* @param isLeft �������Ƿ�Ϊ���
* @return �Ƿ�ɹ� 0 �ɹ� 1 ʧ��
*/
int pitchrollyawToPRDxyz(float *measureLens, struct pitchRollYaw_st *astPRDpry, float *xyz, int isLeft)
{
    int i;
	xyz[0] = 0;
	xyz[1] = 0;
	xyz[2] = 0;

	if (isLeft) {
		//�����
		float *temp = getLeg(astPRDpry[3], measureLens[3]);
		for (i = 0; i < 3; i++)
			xyz[i] += temp[i];
		//��С��
		temp = getLeg(astPRDpry[4], measureLens[4]);
		for (i = 0; i < 3; i++)
			xyz[i] += temp[i];
		//���
		temp = getFoot(astPRDpry[6], 1);
		for (i = 0; i < 3; i++)
			xyz[i] += temp[i];
		//�Ҵ���
		temp = getLeg(astPRDpry[1], measureLens[1]);
		for (i = 0; i < 3; i++)
			xyz[i] -= temp[i];
		//��С��
		temp = getLeg(astPRDpry[2], measureLens[2]);
		for (i = 0; i < 3; i++)
			xyz[i] -= temp[i];
		//�ҽ�
		temp = getFoot(astPRDpry[5], 1);
		for (i = 0; i < 3; i++)
			xyz[i] -= temp[i];
		//����
		temp = getWaist(astPRDpry[0], measureLens[0]);
		for (i = 0; i < 3; i++)
			xyz[i] -= temp[i];
	}
	else {
		//�Ҵ���
		float *temp = getLeg(astPRDpry[1], measureLens[1]);
		for (i = 0; i < 3; i++)
			xyz[i] += temp[i];
		//��С��
		temp = getLeg(astPRDpry[2], measureLens[2]);
		for (i = 0; i < 3; i++)
			xyz[i] += temp[i];
		//�ҽ�
		temp = getFoot(astPRDpry[5], 0);
		for (i = 0; i < 3; i++)
			xyz[i] += temp[i];
		//�����
		temp = getLeg(astPRDpry[3], measureLens[3]);
		for (i = 0; i < 3; i++)
			xyz[i] -= temp[i];
		//��С��
		temp = getLeg(astPRDpry[4], measureLens[4]);
		for (i = 0; i < 3; i++)
			xyz[i] -= temp[i];
		//���
		temp = getFoot(astPRDpry[6], 1);
		for (i = 0; i < 3; i++)
			xyz[i] -= temp[i];
		//����
		temp = getWaist(astPRDpry[0], measureLens[0]);
		for (i = 0; i < 3; i++)
			xyz[i] += temp[i];
	}

	return 0;
}
