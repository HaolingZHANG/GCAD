/* Includes ------------------------------------------------------------------*/
#include "option.h"


#define PRINT_ACCEL     (0x01)
#define PRINT_GYRO      (0x02)
#define PRINT_QUAT      (0x04)

#define ACCEL_ON        (0x01)
#define GYRO_ON         (0x02)

#define MOTION          (0)
#define NO_MOTION       (1)

/* Starting sampling rate. */
#define DEFAULT_MPU_HZ  (50)

#define FLASH_SIZE      (512)
#define FLASH_MEM_START ((void*)0x1800)

#define q30           1073741824.0f
#define degreeechopi  57.295828

float q0=1.0f,q1=0.0f,q2=0.0f,q3=0.0f;


u8 show_tab[]={"0123456789"};

float Pitch,Roll,Yaw;
int temp;


struct rx_s
{
    unsigned char header[3];
    unsigned char cmd;
};


struct hal_s
{
    unsigned char sensors;
    unsigned char dmp_on;
    unsigned char wait_for_tap;
    volatile unsigned char new_gyro;
    unsigned short report;
    unsigned short dmp_features;
    unsigned char motion_int_mode;
    struct rx_s rx;
};
extern void needChangeSlaveAddr(unsigned char sAddr);
extern short g_mag_sens_adj[7][3];
static struct hal_s hal = {0};

/* USB RX binary semaphore. Actually, it's just a flag. Not included in struct
 * because it's declared extern elsewhere.
 */
volatile unsigned char rx_new;

/* The sensors can be mounted onto the board in any orientation. The mounting
 * matrix seen below tells the MPL how to rotate the raw data from thei
 * driver(s).
 * TODO: The following matrices refer to the configuration on an internal test
 * board at Invensense. If needed, please modify the matrices to match the
 * chip-to-body matrix for your particular set up.
 */
static signed char gyro_orientation[9] = {
    -1, 0, 0,
    0,-1, 0,
    0, 0, 1
};


enum packet_type_e
{
    PACKET_TYPE_ACCEL,
    PACKET_TYPE_GYRO,
    PACKET_TYPE_QUAT,
    PACKET_TYPE_TAP,
    PACKET_TYPE_ANDROID_ORIENT,
    PACKET_TYPE_PEDO,
    PACKET_TYPE_MISC
};



/* These next two functions converts the orientation matrix (see
 * gyro_orientation) to a scalar representation for use by the DMP.
 * NOTE: These functions are borrowed from Invensense's MPL.
 */
static  unsigned short inv_row_2_scale(const signed char *row)
{
    unsigned short b;

    if (row[0] > 0)
        b = 0;
    else if (row[0] < 0)
        b = 4;
    else if (row[1] > 0)
        b = 1;
    else if (row[1] < 0)
        b = 5;
    else if (row[2] > 0)
        b = 2;
    else if (row[2] < 0)
        b = 6;
    else
        b = 7;      // error
    return b;
}



static  unsigned short inv_orientation_matrix_to_scalar(const signed char *mtx)
{
    unsigned short scalar;

    /*
       XYZ  010_001_000 Identity Matrix
       XZY  001_010_000
       YXZ  010_000_001
       YZX  000_010_001
       ZXY  001_000_010
       ZYX  000_001_010
     */

    scalar = inv_row_2_scale(mtx);
    scalar |= inv_row_2_scale(mtx + 3) << 3;
    scalar |= inv_row_2_scale(mtx + 6) << 6;


    return scalar;
}

 /*自检函数*/
static void run_self_test(void)
{
    int result;

    long gyro[3], accel[3];

    result = mpu_run_self_test(gyro, accel);
    if (result == 0x7)
    {
        /* Test passed. We can trust the gyro data here, so let's push it down
         * to the DMP.
         */
        float sens;
        unsigned short accel_sens;
        mpu_get_gyro_sens(&sens);
        gyro[0] = (long)(gyro[0] * sens);
        gyro[1] = (long)(gyro[1] * sens);
        gyro[2] = (long)(gyro[2] * sens);
        dmp_set_gyro_bias(gyro);
        mpu_get_accel_sens(&accel_sens);
        accel[0] *= accel_sens;
        accel[1] *= accel_sens;
        accel[2] *= accel_sens;
        dmp_set_accel_bias(accel);
        printf("setting bias succesfully ......\r\n");
    }
    else
    {
        printf("bias has not been modified ......\r\n");
    }

}

//声明相关变量
unsigned long sensor_timestamp;
short gyro[3], accel[3], sensors;
unsigned char more;
long quat[4];

struct sensor_i2cList{
    u8 slaveaddr;
    u8 usrCompareFlag;
    int8_t (*i2cwrite)(uint8_t addr, uint8_t reg, uint8_t len, uint8_t * data);
    int8_t (*i2cread)(uint8_t addr, uint8_t reg, uint8_t len, uint8_t * data);
};

struct sensor_i2cList mpu9250_iic_SensorList[MPU9250_SENSOR_NUM] = {
   {0x69,1,i2c3writeBody,       i2c3readBody    },
   {0x68,0,i2c2writeRightLeg, i2c2readRightLeg},//腿部传感器
   {0x69,0,i2c2writeRightLeg, i2c2readRightLeg},//脚部
   {0x68,0,i2c1writeLeftLeg,   i2c1readLeftLeg },//腿部
   {0x69,0,i2c1writeLeftLeg,   i2c1readLeftLeg },//脚步
   {0x68,0,i2c4write_foot,      i2c4read_foot   },//右脚
   {0x69,0,i2c4write_foot,      i2c4read_foot   }//左脚
};

//误差纠正
float Pitch_error[MPU9250_SENSOR_NUM] = {0.0,0.0,0.0,0.0,0.0,0.0,0.0};
float Roll_error[MPU9250_SENSOR_NUM]  = {0.0,0.0,0.0,0.0,0.0,0.0,0.0};
float Yaw_error[MPU9250_SENSOR_NUM]   = {0.0,0.0,0.0,0.0,0.0,0.0,0.0};

void xdelay_us(u32 n)
{
    u8 j;
    while(n--)
    for(j=0;j<10;j++);
}

void xdelay_ms(u32 n)
{
    while(n--)
    xdelay_us(1000);
}
void mpu_iic_test(void)
{
    struct sensor_i2cList * pSensorIIC;
    u8 DatBuf[6];
    u8 tmp[2];
    u8 i;
    for(i=0;i<MPU9250_SENSOR_NUM;i++)
    {
        DatBuf[0]=0;DatBuf[1]=0;DatBuf[2]=0;
        pSensorIIC = &mpu9250_iic_SensorList[i];
        tmp[0]=0x06;
        tmp[1]=0x18;
        pSensorIIC->i2cwrite(pSensorIIC->slaveaddr,0x1A,2,tmp);
        pSensorIIC->i2cread(pSensorIIC->slaveaddr,0x75, 1,DatBuf);
        pSensorIIC->i2cread(pSensorIIC->slaveaddr,0x1A, 3,DatBuf+1);
        printf("chn %d:slave addr %x,%x,%x,%x\r\n",i,pSensorIIC->slaveaddr,DatBuf[1],DatBuf[2],DatBuf[3]);
    }
}
/* Private macro -------------------------------------------------------------*/
/* Private variables ---------------------------------------------------------*/
void mpu_dmp_init(u8 SensorID)
{
    int result;
    struct sensor_i2cList * pSensorIIC;

     if(SensorID<MPU9250_SENSOR_NUM)
     {
        pSensorIIC = &mpu9250_iic_SensorList[SensorID];
        i2c_write = pSensorIIC->i2cwrite;
        i2c_read = pSensorIIC->i2cread;
     }
     else
        return ;
    result = mpu_init(pSensorIIC->slaveaddr,SensorID);//初始化
    if(result)
    {
        printf("mpu %d slaveaddr 0x%02x init failed...\r\n", SensorID,pSensorIIC->slaveaddr);
        //delay_ms(2000);
        errCodeMpu |= 0x01 << SensorID;
        return ;
    }
    if(!result)   //返回0代表初始化成功
      {
        //mpu_set_sensor
        if(!mpu_set_sensors(INV_XYZ_GYRO | INV_XYZ_ACCEL |INV_XYZ_COMPASS))//
        {
            ;//printf("mpu_set_sensor complete ......\r\n");
        }
        else
        {
           ;// printf("mpu_set_sensor come across error ......\r\n");
        }

        //mpu_configure_fifo
        if(!mpu_configure_fifo(INV_XYZ_GYRO | INV_XYZ_ACCEL))
        {
            ;//printf("mpu_configure_fifo complete ......\r\n");
        }
        else
        {
            printf("mpu_configure_fifo come across error ......\r\n");
        }

        //mpu_set_sample_rate
        if(!mpu_set_sample_rate(DEFAULT_MPU_HZ))
        {
           ;//printf("mpu_set_sample_rate complete ......\r\n");
        }
        else
        {
            printf("mpu_set_sample_rate error ......\r\n");
        }

        //dmp_load_motion_driver_firmvare
        if(!dmp_load_motion_driver_firmware())
        {
            ;//printf("dmp_load_motion_driver_firmware complete ......\r\n");
        }
        else
        {
            printf("dmp_load_motion_driver_firmware come across error ......\r\n");
        }

        //dmp_set_orientation
        if(!dmp_set_orientation(inv_orientation_matrix_to_scalar(gyro_orientation)))
        {
            ;//printf("dmp_set_orientation complete ......\r\n");
        }
        else
        {
            printf("dmp_set_orientation come across error ......\r\n");
        }

        //dmp_enable_feature
        if(!dmp_enable_feature(DMP_FEATURE_6X_LP_QUAT | DMP_FEATURE_TAP |
            DMP_FEATURE_ANDROID_ORIENT | DMP_FEATURE_SEND_RAW_ACCEL | DMP_FEATURE_SEND_CAL_GYRO |
            DMP_FEATURE_GYRO_CAL))
        {
            ;//printf("dmp_enable_feature complete ......\r\n");
        }
        else
        {
            printf("dmp_enable_feature come across error ......\r\n");
        }

        //dmp_set_fifo_rate
        if(!dmp_set_fifo_rate(DEFAULT_MPU_HZ))
        {
            ;//printf("dmp_set_fifo_rate complete ......\r\n");
        }
        else
        {
            printf("dmp_set_fifo_rate come across error ......\r\n");
        }

        run_self_test();

        if(!mpu_set_dmp_state(1))
        {
            printf("mpu %d slaveaddr 0x%02x set_dmp_state complete ......\r\n", SensorID,pSensorIIC->slaveaddr);
        }
        else
        {
            printf("mpu %d slaveaddr 0x%02x set_dmp_state come across error ......\r\n", SensorID,pSensorIIC->slaveaddr);
        }
     }
}

void mpu_dmp_init_all(void)
{
    int i;
    struct sensor_i2cList * pSensorIIC;
    for(i=0;i<(MPU9250_SENSOR_NUM);i++)
    {
        mpu_dmp_init(i);
    }
    for(i=0;i<(MPU9250_SENSOR_NUM);i++)
    {

        pSensorIIC = &mpu9250_iic_SensorList[i];
        i2c_write = pSensorIIC->i2cwrite;
        i2c_read = pSensorIIC->i2cread;
        needChangeSlaveAddr( pSensorIIC->slaveaddr);
        mpu_reset_fifo();
   }
    //mpu_init_foot_sensor();
}
void mpu_init_foot_sensor(void)
{
    u8 SensorID;
    struct sensor_i2cList * pSensorIIC;
    u8 initData[]={0x00,0x07,0x06,0x18,0x01};
    SensorID = MPU9250_SENSOR_NUM-2;
    pSensorIIC = &mpu9250_iic_SensorList[SensorID];
    pSensorIIC->i2cwrite(pSensorIIC->slaveaddr,(u8)PWR_MGMT_1,1,initData);       //
    pSensorIIC->i2cwrite(pSensorIIC->slaveaddr,(u8)SMPLRT_DIV,4,&initData[1]);

    SensorID = MPU9250_SENSOR_NUM-1;
    pSensorIIC = &mpu9250_iic_SensorList[SensorID];
    pSensorIIC->i2cwrite(pSensorIIC->slaveaddr,(u8)PWR_MGMT_1,1,initData);         //
    pSensorIIC->i2cwrite(pSensorIIC->slaveaddr,(u8)SMPLRT_DIV,4,&initData[1]);
}
void calculatedDMP(u8 SensorID,struct pitchRollYaw_st *pstPRY)
{
    struct sensor_i2cList * pSensorIIC;
    short DatBuf[3];
    int ret;
    float Yh,Xh;
    if((errCodeMpu&(0x01<<SensorID)))
        return;

    if(SensorID<MPU9250_SENSOR_NUM)
    {
        pSensorIIC = &mpu9250_iic_SensorList[SensorID];
        i2c_write = pSensorIIC->i2cwrite;
        i2c_read = pSensorIIC->i2cread;
     }
     else
        return ;

     needChangeSlaveAddr( pSensorIIC->slaveaddr);
     dmp_read_fifo(gyro, accel, quat, &sensor_timestamp, &sensors, &more);
     ret = mpu_get_compass_reg(SensorID,DatBuf, 0);
     //readYawPassThrough(SensorID,DatBuf);
     if(ret <0)
     printf("get compass date faile id %d,ret %d\r\n", SensorID, ret);
//   else
//   printf("get compass date id %d,%d,%d %d\r\n", SensorID, DatBuf[0],DatBuf[1],DatBuf[2]);
    /*四元数解姿态*/
    if (sensors & INV_WXYZ_QUAT )
    {
        q0 = quat[0] / q30;
        q1 = quat[1] / q30;
        q2 = quat[2] / q30;
        q3 = quat[3] / q30;

        pstPRY->fPitch  = asin(-2 * q1 * q3 + 2 * q0* q2); // pitch
        pstPRY->fRoll = atan2(2 * q2 * q3 + 2 * q0 * q1, -2 * q1 * q1 - 2 * q2* q2 + 1); // roll
        //pstPRY->fYaw = atan2(2*(q1*q2 + q0*q3),q0*q0+q1*q1-q2*q2-q3*q3) * degreeechopi + Yaw_error;
//      short xx;
//      xx = DatBuf[0];
//      DatBuf[0] = - DatBuf[1];
//      DatBuf[1] = - xx;
        Xh = DatBuf[0]*cos(pstPRY->fPitch)+DatBuf[1]*sin(pstPRY->fRoll)*sin(pstPRY->fPitch)-DatBuf[2]*cos(pstPRY->fRoll)*sin(pstPRY->fPitch);
        Yh = DatBuf[2]*sin(pstPRY->fRoll)+DatBuf[1]*cos(pstPRY->fRoll);
        pstPRY->fPitch = pstPRY->fPitch * degreeechopi + Pitch_error[SensorID];
        pstPRY->fRoll   = pstPRY->fRoll * degreeechopi + Roll_error[SensorID];
        pstPRY->fYaw = atan2(Yh,Xh)*degreeechopi + Yaw_error[SensorID];
    }
      /*printf("dmp num %d:pitch %f roll %f yaw %f\r\n", SensorID,pstPRY->fPitch,
                                                       pstPRY->fRoll,
                                                      pstPRY->fYaw);*/
}

void calculatedDMPAll(struct pitchRollYaw_st *pstPRY)
{
    int i = 4;
    for(i=0; i<MPU9250_SENSOR_NUM; i++)
    {
            calculatedDMP(i,pstPRY+i);
    }
}

int checkFootAccel(float *acceel_right,float *accel_left)
{
    struct sensor_i2cList * pSensorIIC;
    u8 DatBuf[6];
    s16 x,y,z;
    int ret = 0;

    pSensorIIC = &mpu9250_iic_SensorList[MPU9250_SENSOR_NUM-2];
    ret = pSensorIIC->i2cread(pSensorIIC->slaveaddr,ACCEL_XOUT_H,6,DatBuf);
    if(ret == 1)
        return ret;
    x = (DatBuf[0]<<8)|DatBuf[1];
    y = (DatBuf[2]<<8)| DatBuf[3];
    z = (DatBuf[4]<<8)| DatBuf[5];
    acceel_right[0] =19.56*(x/32768.0);
    acceel_right[1] =19.56*(y/32768.0);
    acceel_right[2] =19.56*(z/32768.0);

    pSensorIIC = &mpu9250_iic_SensorList[MPU9250_SENSOR_NUM-1];
    ret = pSensorIIC->i2cread(pSensorIIC->slaveaddr,ACCEL_XOUT_H, 6,DatBuf);
    if(ret == 0)
    {
           x = (DatBuf[0]<<8)| DatBuf[1];
        y = (DatBuf[2]<<8)| DatBuf[3];
        z = (DatBuf[4]<<8)| DatBuf[5];
        accel_left[0] =19.56*(x/32768.0);
        accel_left[1] =19.56*(y/32768.0);
        accel_left[2] =19.56*(z/32768.0);
    }
    return ret;
}

void readYawPassThrough(u8 id,short *data)
{
    struct sensor_i2cList * pSensorIIC;
    u8 tmp[8],cc=0x11;
    int ret = 0;

    pSensorIIC = &mpu9250_iic_SensorList[id];
    ret = pSensorIIC->i2cread(0x0c,0x03, 6,tmp);
    if(ret!=0)
        {
        printf("jinggxx \r\n");
    }
    ret = pSensorIIC->i2cwrite(0x0c,0x0a, 1,&cc);
    if(ret!=0){
        printf("sdfsdfsf \r\n");
    }
//   printf("id %d yaw data:0x%02x,0x%02x,0x%02x,0x%02x,0x%02x,0x%02x\r\n",id,tmp[0],tmp[1],tmp[2],tmp[3],tmp[4],tmp[5]);
    data[0] = (tmp[1] << 8) | tmp[0];
    data[1] = (tmp[3] << 8) | tmp[2];
    data[2] = (tmp[5] << 8) | tmp[4];
//    printf("%04x, %04x %04x\r\n" , data[0],data[1],data[2]);
    data[0] = ((long)data[0] * g_mag_sens_adj[id][0]) >> 8;
    data[1] = ((long)data[1] * g_mag_sens_adj[id][1]) >> 8;
    data[2] = ((long)data[2] * g_mag_sens_adj[id][2]) >> 8;

/*
    short DatBuf[3];
    needChangeSlaveAddr( pSensorIIC->slaveaddr);
    i2c_write = pSensorIIC->i2cwrite;
    i2c_read = pSensorIIC->i2cread;
    ret = mpu_get_compass_reg(0,DatBuf, 0);
    du = atan((DatBuf[1]*1.0)/(DatBuf[0]*1.0))*57.3;
    printf("ret %d,yaw data:0x%04x,0x%04x,0x%04x,%.2f\r\n",ret,DatBuf[0],DatBuf[1],DatBuf[2],du);*/
}
