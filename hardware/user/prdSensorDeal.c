/* Includes ------------------------------------------------------------------*/
#include "option.h"

/******************������ز���**********************/
extern uint32_t averageADC[INFRARED_ADC_CHANNEL_NUM];
extern int pitchrollyawToPRDxyz(float *measureLens, struct pitchRollYaw_st *astPRDpry, float *xyz, int isLeft);
/******************�������ȳ� ��λmm**********************/
float measureLengths[INFRARED_ADC_CHANNEL_NUM] = {0.0};//1���� 2�Ҵ��� 3��С�� 4����� 5��С��
float lenFixVal[INFRARED_ADC_CHANNEL_NUM]={20,20,20,20,20}; //���� �ȳ��Ĺ̶�ֵ������������

/*******************������****************************/
struct pitchRollYaw_st astPRDpry[MPU9250_SENSOR_NUM];

//0��ʾ����ɼ����ݣ�1��ʾ̧�Ųɼ����ݣ�2��ʾ��Ųɼ�����
extern unsigned char footAccelToMontionSta(float *acceel_right,float *accel_left,int *piIsLeft);

//����ֵ ��ȷΪ0 ������󷵻�ֵΪbit0~4�ֱ��Ӧ������1~5���ĸ����������λ��1
unsigned char adcToWaistAndLegLengths(float* adc, float* measureLens)
{
    char result = 0;
    int i;
    for(i = 0; i < INFRARED_ADC_CHANNEL_NUM; i++) {
        measureLens[i] = 8.7328 * pow(adc[i], -0.912) + 0.25;    //0.25�ǵ���ֵ�����ݲ��������������Ӧ��ֵ

        if(measureLens[i] < 10 || measureLens[i] > 50)
            result |= 0x01<<i;//TODO measureLens[i]Ӧ����10~50֮�� �������
        measureLens[i] += lenFixVal[i]; //���϶���
    }

    return result;
}

void CalculatedLegAndWaistLength(void)
{
    int i;
    u8  ret;
    float adc[INFRARED_ADC_CHANNEL_NUM];

    printf("average adc chn 1~5: 0x%04x,0x%04x,0x%04x,0x%04x,0x%04x\r\n",averageADC[0],
                                                averageADC[1],
                                                averageADC[2],
                                                averageADC[3],
                                                averageADC[4]);
    for(i=0; i<INFRARED_ADC_CHANNEL_NUM; i++)
    {
        adc[i] = 3.3*averageADC[i]/2048;//5�����������
    }
    /*********������Ӹ���adc�����е�ֵ�����ȳ���������ȵĴ���***************/
    /*********������˳��1���� 2�Ҵ��� 3��С�� 4����� 5��С��***************/
    //����ֵ ��ȷΪ0 ������󷵻�ֵΪbit0~4�ֱ��Ӧ������1~5���ĸ����������λ��1
	ret = adcToWaistAndLegLengths(adc,measureLengths);
    if(ret != 0)
    {
        errCodeInfrared = ret;
        printf("please check infrared,error code��0x%02x\r\n",ret);
    }
}

int checkFootMotionState(int *piIsLeft)
{
    u8 stateFlag = 0;
    int ret;
       float accel_right[3]={0.0,0.0,0.0},accel_left[3]={0.0,0.0,0.0};
    static u8 timecount = 0;
    ret = checkFootAccel(accel_right,accel_left);

#if DEBUG_MPU_COLLECT
    timecount ++;
    if(timecount % 10 == 0)
     {
        timecount = 0;
        printf("right accel:%.2f  %.2f  %.2f\r\n",accel_right[0],accel_right[1],accel_right[2]);
        printf("left  accel:%.2f  %.2f  %.2f\r\n",accel_left[0],accel_left[1],accel_left[2]);
        //readYawPassThrough();
    }
#endif

        /***������Ӹ��ݽ������������ǵĽ��ٶ�ֵ************************************
        ****�����Ƿ���7�������������ݲɼ��߼�   ************************************/
       /*****************************start***************************************************************/
       if(ret == 0)
       {   
           //0��ʾ����ɼ����ݣ�1��ʾ̧�Ųɼ�����   ��2��ʾ��Ųɼ�����
           stateFlag = footAccelToMontionSta(accel_right,accel_left,piIsLeft);  
       }
       else
       {
           stateFlag = 0;//1����ʵ��Ӧ����0
       }
       /*****************************end***************************************************************/
    return stateFlag;
}


void CalculatedPRDxyz(u8 stateFlag,int iIsLeft)
{
    static u8 timecount = 0;
    s32 i,ret;
    float xyz[3];
    calculatedDMPAll(astPRDpry);

#if DEBUG_MPU_COLLECT
    timecount ++;
    if(timecount % 10 == 0)
     {
        timecount = 0;
        for(i=0; i<MPU9250_SENSOR_NUM; i++)
        {
            if(0 == (errCodeMpu&(0x01<<i)))
            printf("dmp num %d:pitch %.2f roll %.2f yaw %.2f\r\n", i,astPRDpry[i].fPitch,
                                                                astPRDpry[i].fRoll,
                                                                astPRDpry[i].fYaw);
       }
    }
#endif

    /*********��������ϴ���GCAD��xyz ����***************/
    /*********�����Ǳ��˳��1���� 2���� 3�ҽŸ� 4���� 5��Ÿ� 6�ҽ� 7���******/
    /*****************************start*****************************************************************/
    ret = pitchrollyawToPRDxyz(measureLengths, astPRDpry, xyz, iIsLeft);

   /*****************************end*****************************************************************/
   if(stateFlag == 2 && ret == 0)
   {
        sendMsgDataOneStep(xyz[0],xyz[1],xyz[2]);
   }
}
