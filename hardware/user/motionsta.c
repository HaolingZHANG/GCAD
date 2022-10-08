//
//  test.c
//  gcadtest
//
//  Created by Dolia on 9/15/16.
//  Copyright © 2016 Dolia. All rights reserved.
//

//#include <math.h>
#include <option.h>


#define ARRAY_LENGTH 200 //���鳤��
#define HIGHER_LIMIT 9.8
#define LOWER_LIMIT 7.5

int8_t statusLeft = -1;
int8_t statusRight = -1;
//-1 ���һ�βɼ�����һ�ε�������ֵ
// 0 ��һ�ε�������ֵ����һ�θ�������ֵ
// 1 ��һ�θ�������ֵ����������ֵ
// 2 ��������ֵ
// 3 �ڶ��θ�������ֵ
int8_t flagLeft = 0;
int8_t flagRight = 0;

float lastAccelRight = -1;
float lastAccelLeft  = -1;
/**
 * return '
 */
unsigned char footAccelToMontionSta(float *accel_right, float *accel_left, int *piIsLeft)
{
    *piIsLeft = 0;  //����Ĭ��ֵΪ0
    switch (statusLeft) {
        case -1:
            if (accel_left[2] < LOWER_LIMIT)
            {
                statusLeft++;
            }
            break;
        case 0:
            if (accel_left[2] > HIGHER_LIMIT)
            {
                statusLeft++;
            }
            break;
        case 1:
            if (accel_left[2] < HIGHER_LIMIT)
            {
                if (flagLeft && accel_left[2] < lastAccelLeft)//��һ���ɼ���
                {
                    flagLeft = 1;
                    *piIsLeft = 1;
                    return '1';
                }
                statusLeft++;
            }
            break;
        case 2:
            if (accel_left[2] > HIGHER_LIMIT)
            {
                statusLeft++;
            }
            break;
        case 3:
            if (accel_left[2] < lastAccelLeft)//��һ���ɼ���
            {
                statusLeft = -1;
                *piIsLeft = 1;
                return '2';
            }
            break;

        default:
            break;
    }
    switch (statusRight) {
        case -1:
            if (accel_right[2] < LOWER_LIMIT)
            {
                statusRight++;
            }
            break;
        case 0:
            if (accel_right[2] > HIGHER_LIMIT)
            {
                statusRight++;
            }
            break;
        case 1:
            if (accel_right[2] < HIGHER_LIMIT)
            {
                if (!flagRight && accel_right[2] < lastAccelRight)//��һ���ɼ���
                {
                    flagRight = 1;
                    return '1';
                }
                statusRight++;
            }
            break;
        case 2:
            if (accel_right[2] > HIGHER_LIMIT)
            {
                statusRight++;
            }
            break;
        case 3:
            if (accel_right[2] < lastAccelRight)//�ڶ����ɼ���
            {
                statusRight = -1;
                return '2';
            }
            break;

        default:
            break;
    }
    lastAccelRight = accel_right[2];
    lastAccelLeft = accel_left[2];
    return 0;
}
