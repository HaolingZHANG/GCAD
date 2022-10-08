#include "option.h"


unsigned char TX_DATA[4];    //显示据缓存区
unsigned char SensorDatBuf[20];       //接收数据缓存区
char  g_test=0;                  //IIC用到
short T_X,T_Y,T_Z,T_T;       //X,Y,Z轴，温度

int SensorRawData[63];//以自身为参考对象
struct mpu9250_data g_SensorMem[MPU9250_SENSOR_NUM] = {0};

/*******************************************************************************
* Function Name  : I2C_Start
* Description    : Master Start Simulation IIC Communication
* Input          : None
* Output         : None
* Return         : Wheather  Start
****************************************************************************** */
char I2C_Start(void)
{
    SDA_H;
    SCL_H;
    I2C_delay();
    if(!SDA_read)return FALSE;  //SDA线为低电平则总线忙,退出
    SDA_L;
    I2C_delay();
    if(SDA_read) return FALSE;  //SDA线为高电平则总线出错,退出
    SDA_L;
    I2C_delay();
    return TRUE;
}
/*******************************************************************************
* Function Name  : I2C_Stop
* Description    : Master Stop Simulation IIC Communication
* Input          : None
* Output         : None
* Return         : None
****************************************************************************** */
void I2C_Stop(void)
{
    SCL_L;
    I2C_delay();
    SDA_L;
    I2C_delay();
    SCL_H;
    I2C_delay();
    SDA_H;
    I2C_delay();
}
/*******************************************************************************
* Function Name  : I2C_Ack
* Description    : Master Send Acknowledge Single
* Input          : None
* Output         : None
* Return         : None
****************************************************************************** */
void I2C_Ack(void)
{
    SCL_L;
    I2C_delay();
    SDA_L;
    I2C_delay();
    SCL_H;
    I2C_delay();
    SCL_L;
    I2C_delay();
}
/*******************************************************************************
* Function Name  : I2C_NoAck
* Description    : Master Send No Acknowledge Single
* Input          : None
* Output         : None
* Return         : None
****************************************************************************** */
void I2C_NoAck(void)
{
    SCL_L;
    I2C_delay();
    SDA_H;
    I2C_delay();
    SCL_H;
    I2C_delay();
    SCL_L;
    I2C_delay();
}
/*******************************************************************************
* Function Name  : I2C_WaitAck
* Description    : Master Reserive Slave Acknowledge Single
* Input          : None
* Output         : None
* Return         : Wheather  Reserive Slave Acknowledge Single
****************************************************************************** */
char I2C_WaitAck(void)   //返回为:=1有ACK,=0无ACK
{
    SCL_L;
    I2C_delay();
    SDA_H;
    I2C_delay();
    SCL_H;
    I2C_delay();
    if(SDA_read)
    {
      SCL_L;
      I2C_delay();
      return FALSE;
    }
    SCL_L;
    I2C_delay();
    return TRUE;
}
/*******************************************************************************
* Function Name  : I2C_SendByte
* Description    : Master Send a Byte to Slave
* Input          : Will Send Date
* Output         : None
* Return         : None
****************************************************************************** */
void I2C_SendByte(u8 SendByte) //数据从高位到低位//
{
    u8 i=8;
    while(i--)
    {
        SCL_L;
        I2C_delay();
      if(SendByte&0x80)
        SDA_H;
      else
        SDA_L;
        SendByte<<=1;
        I2C_delay();
        SCL_H;
        I2C_delay();
    }
    SCL_L;
}
/*******************************************************************************
* Function Name  : I2C_ReadByte
* Description    : Master Reserive a Byte From Slave
* Input          : None
* Output         : None
* Return         : Date From Slave
****************************************************************************** */
void I2C_ReadByte(u8* pRecBuf)  //数据从高位到低位//
{
    u8 i=8;
    u8 ReceiveByte1=0;
    u8 ReceiveByte2=0;

    SDA_H;
    while(i--)
    {
        ReceiveByte1<<=1;
        ReceiveByte2<<=1;
        SCL_L;
        I2C_delay();
        SCL_H;
        I2C_delay();
        if(IIC1_SDA_read)
        {
        ReceiveByte1 |= 0x01;
        }
        if(IIC2_SDA_read)
        {
        ReceiveByte2 |= 0x01;
        }
    }
    pRecBuf[0] = ReceiveByte1;
    pRecBuf[1] = ReceiveByte2;
    SCL_L;
}
//ZRX
//单字节写入*******************************************

char Single_Write(unsigned char SlaveAddress,unsigned char REG_Address,unsigned char REG_data)           //void
{
    if(!I2C_Start())return FALSE;
    I2C_SendByte(SlaveAddress);   //发送设备地址+写信号//I2C_SendByte(((REG_Address & 0x0700) >>7) | SlaveAddress & 0xFFFE);//设置高起始地址+器件地址
    if(!I2C_WaitAck()){I2C_Stop(); return FALSE;}
    I2C_SendByte(REG_Address );   //设置低起始地址
    I2C_WaitAck();
    I2C_SendByte(REG_data);
    I2C_WaitAck();
    I2C_Stop();
    delay5ms();
    return TRUE;
}
unsigned char i2cWriteBuffer(uint8_t addr, uint8_t reg, uint8_t len, uint8_t * data)
{
    int i;
    if (!I2C_Start())
        return false;
    I2C_SendByte(addr);
    if (!I2C_WaitAck()) {
        I2C_Stop();
        return false;
    }
    I2C_SendByte(reg);
    I2C_WaitAck();
    for (i = 0; i < len; i++) {
        I2C_SendByte(data[i]);
        if (!I2C_WaitAck()) {
            I2C_Stop();
            return false;
        }
    }
    I2C_Stop();
    return true;
}
//单字节读取*****************************************
unsigned char Single_Read(unsigned char SlaveAddress,unsigned char REG_Address, unsigned char* REG_data)
{
    if(!I2C_Start())return FALSE;
    I2C_SendByte(SlaveAddress); //I2C_SendByte(((REG_Address & 0x0700) >>7) | REG_Address & 0xFFFE);//设置高起始地址+器件地址
    if(!I2C_WaitAck()){I2C_Stop();g_test=1; return FALSE;}
    I2C_SendByte((u8) REG_Address);   //设置低起始地址
    I2C_WaitAck();
    I2C_Start();
    I2C_SendByte(SlaveAddress+1);
    I2C_WaitAck();

    I2C_ReadByte(REG_data);
    I2C_NoAck();
    I2C_Stop();
    return TRUE;
}
static uint8_t I2C_ReceiveByte(void)
{
    uint8_t i = 8;
    uint8_t byte = 0;

    SDA_H;
    while (i--) {
        byte <<= 1;
        SCL_L;
        I2C_delay();
        SCL_H;
        I2C_delay();
        if (IIC2_SDA_read) {
            byte |= 0x01;
        }
    }
    SCL_L;
    return byte;
}
unsigned char i2cRead(uint8_t addr, uint8_t reg, uint8_t len, uint8_t *buf)
{
    if (!I2C_Start())
        return false;
    I2C_SendByte(addr);
    if (!I2C_WaitAck()) {
        I2C_Stop();
        return false;
    }
    I2C_SendByte(reg);
    I2C_WaitAck();
    I2C_Start();
    I2C_SendByte(addr +1);
    I2C_WaitAck();
    while (len) {
        *buf = I2C_ReceiveByte();
        if (len == 1)
            I2C_NoAck();
        else
            I2C_Ack();
        buf++;
        len--;
    }
    I2C_Stop();
    return true;
}
//多字节读取*****************************************
unsigned char Multi_Byte_Read(unsigned char SlaveAddress,unsigned char REG_Address,unsigned char * pDat,unsigned char len)
{   unsigned char i=1;
     unsigned char dat[2];
    if(!I2C_Start())return FALSE;
    I2C_SendByte(SlaveAddress); //I2C_SendByte(((REG_Address & 0x0700) >>7) | REG_Address & 0xFFFE);//设置高起始地址+器件地址
    if(!I2C_WaitAck()){I2C_Stop();g_test=1; return FALSE;}
    I2C_SendByte((u8) REG_Address);   //设置低起始地址
    I2C_WaitAck();
    I2C_Start();
    I2C_SendByte(SlaveAddress+1);
    I2C_WaitAck();

    I2C_ReadByte(dat);
    *pDat = dat[0];
    *(pDat+len) = dat[1];
    while(i<len)
    {
        I2C_Ack();
        I2C_ReadByte(dat);
        *(pDat+i)= dat[0];
        *(pDat+len+i)= dat[1];
              i ++;
    }
    I2C_NoAck();
    I2C_Stop();
    return TRUE;
}
int8_t i2cwrite(uint8_t addr, uint8_t reg, uint8_t len, uint8_t * data)
{
    if(i2cWriteBuffer(addr,reg,len,data))
    {
        return 0;
    }
    else
    {
        return -1;
    }
}
int8_t i2cread(uint8_t addr, uint8_t reg, uint8_t len, uint8_t *buf)
{
    if(i2cRead(addr,reg,len,buf))
    {
        return 0;
    }
    else
    {
        return -1;
    }
    //return FALSE;
}
void Init_MPU9250(void)
{
    u8 tmp[2];
    u8 tmp1[4];
/*
   Single_Write(GYRO_ADDRESS,PWR_M, 0x80);   //
   Single_Write(GYRO_ADDRESS,SMPL, 0x07);    //
   Single_Write(GYRO_ADDRESS,DLPF, 0x1E);    //±2000°
   Single_Write(GYRO_ADDRESS,INT_C, 0x00 );  //
   Single_Write(GYRO_ADDRESS,PWR_M, 0x00);   //
*/
/*    Single_Write(GYRO_ADDRESS,PWR_MGMT_1, 0x00);  //解除休眠状态
    Single_Write(GYRO_ADDRESS,SMPLRT_DIV, 0x07);
    Single_Write(GYRO_ADDRESS,CONFIG, 0x06);
    Single_Write(GYRO_ADDRESS,GYRO_CONFIG, 0x18);
    Single_Write(GYRO_ADDRESS,ACCEL_CONFIG, 0x01);
*/
    //Single_Read(ACCEL_ADDRESS,MPU9250_WHO_AM_I,tmp);
/*  tmp[0]=0x06;
    tmp[1]=0x18;
    i2cwrite(0xd2,0x1A,2,tmp);
    i2cread(0xd2,0x1A,2,tmp1);
    //i2cread(0xd0,0x75,1,tmp);
    printf("check i2c connet:%x,%x,%x,%x\r\n",tmp1[0],tmp1[1],tmp1[2],tmp1[3]);
    if(tmp[0] == tmp1[2] && tmp[1]==tmp1[3])
    {
        printf("normal\r\n");
    }
    else
    {
        printf("abnormal\r\n");
    }   */
  //----------------
//  Single_Write(GYRO_ADDRESS,0x6A,0x00);//close Master Mode

}
/*
//读取MPU9250数据****************************************
void READ_MPU9250_ACCEL(void)
{

   Single_Read(ACCEL_ADDRESS,ACCEL_XOUT_L,&SensorDatBuf[0]);
   Single_Read(ACCEL_ADDRESS,ACCEL_XOUT_H,&SensorDatBuf[1]);
   T_X= (SensorDatBuf[1]<<8)|SensorDatBuf[0];
   T_X/=164;                           //读取计算X轴数据

   Single_Read(ACCEL_ADDRESS,ACCEL_YOUT_L,&SensorDatBuf[2]);
   Single_Read(ACCEL_ADDRESS,ACCEL_YOUT_H,&SensorDatBuf[3]);
   T_Y= (SensorDatBuf[3]<<8)|SensorDatBuf[2];
   T_Y/=164;                           //读取计算Y轴数据
   Single_Read(ACCEL_ADDRESS,ACCEL_ZOUT_L,&SensorDatBuf[4]);
   Single_Read(ACCEL_ADDRESS,ACCEL_ZOUT_H,&SensorDatBuf[5]);
   T_Z= (SensorDatBuf[5]<<8)|SensorDatBuf[4];
   T_Z/=164;                           //读取计算Z轴数据


}

void READ_MPU9250_GYRO(void)
{

   Single_Read(GYRO_ADDRESS,GYRO_XOUT_L,&SensorDatBuf[0]);
   Single_Read(GYRO_ADDRESS,GYRO_XOUT_H,&SensorDatBuf[1]);
   T_X= (SensorDatBuf[1]<<8)|SensorDatBuf[0];
   T_X/=16.4;                          //读取计算X轴数据

   Single_Read(GYRO_ADDRESS,GYRO_YOUT_L,&SensorDatBuf[2]);
   Single_Read(GYRO_ADDRESS,GYRO_YOUT_H,&SensorDatBuf[3]);
   T_Y= (SensorDatBuf[3]<<8)|SensorDatBuf[2];
   T_Y/=16.4;                          //读取计算Y轴数据
   Single_Read(GYRO_ADDRESS,GYRO_ZOUT_L,&SensorDatBuf[4]);
   Single_Read(GYRO_ADDRESS,GYRO_ZOUT_H,&SensorDatBuf[5]);
   T_Z= (SensorDatBuf[5]<<8)|SensorDatBuf[4];
   T_Z/=16.4;                          //读取计算Z轴数据


  // SensorDatBuf[6]=Single_Read(GYRO_ADDRESS,TEMP_OUT_L);
  // SensorDatBuf[7]=Single_Read(GYRO_ADDRESS,TEMP_OUT_H);
  // T_T=(SensorDatBuf[7]<<8)|SensorDatBuf[6];
  // T_T = 35+ ((double) (T_T + 13200)) / 280;// 读取计算出温度
}


void READ_MPU9250_MAG(void)
{
   Single_Write(GYRO_ADDRESS,0x37,0x02);//turn on Bypass Mode
   Delayms(10);
   Single_Write(MAG_ADDRESS,0x0A,0x01);
   Delayms(10);
   Single_Read (MAG_ADDRESS,MAG_XOUT_L,&SensorDatBuf[0]);
   Single_Read (MAG_ADDRESS,MAG_XOUT_H,&SensorDatBuf[1]);
   T_X=(SensorDatBuf[1]<<8)|SensorDatBuf[0];

   Single_Read(MAG_ADDRESS,MAG_YOUT_L,&SensorDatBuf[2]);
   Single_Read(MAG_ADDRESS,MAG_YOUT_H,&SensorDatBuf[3]);
   T_Y= (SensorDatBuf[3]<<8)|SensorDatBuf[2];
                           //读取计算Y轴数据

   Single_Read(MAG_ADDRESS,MAG_ZOUT_L,&SensorDatBuf[4]);
   Single_Read(MAG_ADDRESS,MAG_ZOUT_H,&SensorDatBuf[5]);
   T_Z= (SensorDatBuf[5]<<8)|SensorDatBuf[4];
                           //读取计算Z轴数据
}
void DATA_printf(u8 *s,short temp_data)
{
    if(temp_data<0){
    temp_data=-temp_data;
    *s='-';
    }
    else *s=' ';
    *++s =temp_data/100+0x30;
    temp_data=temp_data%100;     //取余运算
    *++s =temp_data/10+0x30;
    temp_data=temp_data%10;      //取余运算
    *++s =temp_data+0x30;
}

void uartoutputmp9250data(void)
{
     READ_MPU9250_ACCEL();  //加速度
    DATA_printf(TX_DATA,T_X);//转换X轴数据到数组
    printf("Ax:%d,%d,%d,%d\n",TX_DATA[0],TX_DATA[1],TX_DATA[2],TX_DATA[3]);          //发送X轴数
    DATA_printf(TX_DATA,T_Y);//转换Y轴数据到数组
    printf("Ay:%d,%d,%d,%d\n",TX_DATA[0],TX_DATA[1],TX_DATA[2],TX_DATA[3]);              //发送Y轴数
    DATA_printf(TX_DATA,T_Z);//转换Z轴数据到数组
    printf("Az:%d,%d,%d,%d\n",TX_DATA[0],TX_DATA[1],TX_DATA[2],TX_DATA[3]);              //发送Z轴数

    READ_MPU9250_GYRO();      //陀螺
    DATA_printf(TX_DATA,T_X);//转换X轴数据到数组
    printf("Gx:%d,%d,%d,%d\n",TX_DATA[0],TX_DATA[1],TX_DATA[2],TX_DATA[3]);          //发送X轴数
    DATA_printf(TX_DATA,T_Y);//转换Y轴数据到数组
    printf("Gy:%d,%d,%d,%d\n",TX_DATA[0],TX_DATA[1],TX_DATA[2],TX_DATA[3]);          //发送Y轴数
    DATA_printf(TX_DATA,T_Z);//转换Z轴数据到数组
    printf("Gz:%d,%d,%d,%d\n",TX_DATA[0],TX_DATA[1],TX_DATA[2],TX_DATA[3]);          //发送Z轴数

    READ_MPU9250_MAG();       //磁场
       DATA_printf(TX_DATA,T_X);//转换X轴数据到数组
    printf("Mx:%d,%d,%d,%d\n",TX_DATA[0],TX_DATA[1],TX_DATA[2],TX_DATA[3]);          //发送X轴数
    DATA_printf(TX_DATA,T_Y);//转换Y轴数据到数组
    printf("My:%d,%d,%d,%d\n",TX_DATA[0],TX_DATA[1],TX_DATA[2],TX_DATA[3]);          //发送Y轴数
    DATA_printf(TX_DATA,T_Z);//转换Z轴数据到数组
    printf("Mz:%d,%d,%d,%d\n",TX_DATA[0],TX_DATA[1],TX_DATA[2],TX_DATA[3]);          //发送Z轴数

    Delayms(10);                 //延时
}

void debug_mutil_read_test(void)
{
    memset(SensorDatBuf,0,12);
    Multi_Byte_Read(GYRO_ADDRESS,GYRO_XOUT_L,SensorDatBuf,6);
    SensorRawData[0]=   (SensorDatBuf[1]<<8)|SensorDatBuf[0];
    SensorRawData[1]=   (SensorDatBuf[3]<<8)|SensorDatBuf[2];
    SensorRawData[2]=   (SensorDatBuf[4]<<8)|SensorDatBuf[5];
    SensorRawData[3]=   (SensorDatBuf[6]<<8)|SensorDatBuf[7];
    SensorRawData[4]=   (SensorDatBuf[8]<<8)|SensorDatBuf[9];
    SensorRawData[5]=   (SensorDatBuf[10]<<8)|SensorDatBuf[11];
    printf("mutil read1:0x%04x 0x%04x 0x%04x\n", SensorRawData[0],SensorRawData[1],SensorRawData[2]);
    printf("mutil read2:0x%04x 0x%04x 0x%04x\n", SensorRawData[3],SensorRawData[4],SensorRawData[5]);
}
*/
void updataSensorAccGyto(u8 slaveaddr,struct mpu9250_data*ptrSensor1,struct mpu9250_data*ptrSensor2)
{
    Multi_Byte_Read(slaveaddr,ACCEL_XOUT_H,SensorDatBuf,6);
    ptrSensor1->accX   =   (SensorDatBuf[0]<<8)|SensorDatBuf[1];
    ptrSensor1->accY   =    -(SensorDatBuf[2]<<8)|SensorDatBuf[3];
    ptrSensor1->accZ   =   (SensorDatBuf[4]<<8)|SensorDatBuf[5];
    ptrSensor2->accX   =      (SensorDatBuf[6]<<8)|SensorDatBuf[7];
    ptrSensor2->accY   = -(SensorDatBuf[8]<<8)|SensorDatBuf[9];
    ptrSensor2->accZ   =      (SensorDatBuf[10]<<8)|SensorDatBuf[11];
//  printf("mutil read1:0x%04x 0x%04x 0x%04x\n", ptrSensor1->accX,ptrSensor1->accY,ptrSensor1->accZ);
//  printf("mutil read2:0x%04x 0x%04x 0x%04x\n", ptrSensor2->accX,ptrSensor2->accY,ptrSensor2->accZ);
    Multi_Byte_Read(slaveaddr,GYRO_XOUT_H,SensorDatBuf,6);
    ptrSensor1->gyroX=  -(SensorDatBuf[0]<<8)|SensorDatBuf[1];
    ptrSensor1->gyroY=    (SensorDatBuf[2]<<8)|SensorDatBuf[3];
    ptrSensor1->gyroZ=  -(SensorDatBuf[4]<<8)|SensorDatBuf[5];
    ptrSensor2->gyroX=  -(SensorDatBuf[6]<<8)|SensorDatBuf[7];
    ptrSensor2->gyroY=    (SensorDatBuf[8]<<8)|SensorDatBuf[9];
    ptrSensor2->gyroZ=  -(SensorDatBuf[10]<<8)|SensorDatBuf[11];
//  printf("mutil read1:0x%04x 0x%04x 0x%04x\n", ptrSensor1->gyroX,ptrSensor1->gyroY,ptrSensor1->gyroZ);
//  printf("mutil read2:0x%04x 0x%04x 0x%04x\n", ptrSensor2->gyroX,ptrSensor2->gyroY,ptrSensor2->gyroZ);
}

void updateSensorMAG(u8 slaveaddr,struct mpu9250_data*ptrSensor1)
{
       Multi_Byte_Read(slaveaddr,MAG_XOUT_L,SensorDatBuf,6);
    ptrSensor1->magX=   (SensorDatBuf[1]<<8)|SensorDatBuf[0];
    ptrSensor1->magY=   -(SensorDatBuf[3]<<8)|SensorDatBuf[2];
    ptrSensor1->magZ=   (SensorDatBuf[5]<<8)|SensorDatBuf[4];
//  printf("mutil read1:0x%04x 0x%04x 0x%04x\n", ptrSensor1->magX,ptrSensor1->magY,ptrSensor1->magZ);
}
