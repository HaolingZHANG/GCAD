/* Includes ------------------------------------------------------------------*/
#include "option.h"

/******************红外相关参数**********************/
extern uint32_t averageADC[INFRARED_ADC_CHANNEL_NUM];
extern int pitchrollyawToPRDxyz(float *measureLens, struct pitchRollYaw_st *astPRDpry, float *xyz, int isLeft);
/******************腰部，腿长 单位mm**********************/
float measureLengths[INFRARED_ADC_CHANNEL_NUM] = {0.0};//1腰部 2右大腿 3右小腿 4左大腿 5左小腿
float lenFixVal[INFRARED_ADC_CHANNEL_NUM]={20,20,20,20,20}; //腰宽 腿长的固定值依次填在这里

/*******************陀螺仪****************************/
struct pitchRollYaw_st astPRDpry[MPU9250_SENSOR_NUM];

//0表示无需采集数据，1表示抬脚采集数据，2表示落脚采集数据
extern unsigned char footAccelToMontionSta(float *acceel_right,float *accel_left,int *piIsLeft);

//返回值 正确为0 计算错误返回值为bit0~4分别对应红外编号1~5，哪个红外错误哪位置1
unsigned char adcToWaistAndLegLengths(float* adc, float* measureLens)
{
    char result = 0;
    int i;
    for(i = 0; i < INFRARED_ADC_CHANNEL_NUM; i++) {
        measureLens[i] = 8.7328 * pow(adc[i], -0.912) + 0.25;    //0.25是调整值，根据测距情况自行添加相应的值

        if(measureLens[i] < 10 || measureLens[i] > 50)
            result |= 0x01<<i;//TODO measureLens[i]应该在10~50之间 否则错误
        measureLens[i] += lenFixVal[i]; //加上定长
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
        adc[i] = 3.3*averageADC[i]/2048;//5个红外的数据
    }
    /*********下面添加根据adc数组中的值计算腿长和腰部宽度的代码***************/
    /*********红外编号顺序1腰部 2右大腿 3右小腿 4左大腿 5左小腿***************/
    //返回值 正确为0 计算错误返回值为bit0~4分别对应红外编号1~5，哪个红外错误哪位置1
	ret = adcToWaistAndLegLengths(adc,measureLengths);
    if(ret != 0)
    {
        errCodeInfrared = ret;
        printf("please check infrared,error code：0x%02x\r\n",ret);
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

        /***下面添加根据脚上两个陀螺仪的角速度值************************************
        ****计算是否做7个陀螺仪做数据采集逻辑   ************************************/
       /*****************************start***************************************************************/
       if(ret == 0)
       {   
           //0表示无需采集数据，1表示抬脚采集数据   ，2表示落脚采集数据
           stateFlag = footAccelToMontionSta(accel_right,accel_left,piIsLeft);  
       }
       else
       {
           stateFlag = 0;//1这里实际应该填0
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

    /*********下面添加上传给GCAD的xyz 数据***************/
    /*********陀螺仪编号顺序1腰部 2右腿 3右脚跟 4左腿 5左脚跟 6右脚 7左脚******/
    /*****************************start*****************************************************************/
    ret = pitchrollyawToPRDxyz(measureLengths, astPRDpry, xyz, iIsLeft);

   /*****************************end*****************************************************************/
   if(stateFlag == 2 && ret == 0)
   {
        sendMsgDataOneStep(xyz[0],xyz[1],xyz[2]);
   }
}
