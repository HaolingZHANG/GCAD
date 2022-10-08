#ifndef __OPTION_H
#define __OPTION_H
#include "stm32f10x.h"
#include "stdio.h"
#include "string.h"
#include "serial.h"
#include "delay.h"
#include "infrared.h"
#include "dataprotocol.h"
#include "mpu9250.h"
#include <math.h>

#include "inv_mpu.h"
#include "inv_mpu_dmp_motion_driver.h"
#include "dmpKey.h"
#include "dmpmap.h"
#include "pitchRollYaw.h"
#include "prdSensorDeal.h"

#define DEBUG_MSG_CLASS0                    0  //һ���ֽھ����
#define DEBUG_MSG_CLASS1                    0  //�յ�һ����������Ϣ�����������Ϣ�Դ�
#define DEBUG_MSG_CLASS2                    0  //
#define DEBUG_MSG_SEND                      0
#define DEBUG_ADC_INFRAED                   0
#define DEBUG_MPU_COLLECT                   1
#define DEBUG_CLOSE_VERIFY                  1  //�ر����У��

#define CONFIG_SERIAL_USART1_BAURATE 115200
#define CONFIG_SERIAL_USART2_BAURATE 9600
#define PrintChar  printf

extern u8 errCodeMpu,errCodeInfrared;
#endif
