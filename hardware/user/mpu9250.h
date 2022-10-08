#ifndef __MPU9250_H
#define __MPU9250_H

// ����MPU9250�ڲ���ַ
//****************************************
#define SMPLRT_DIV      0x19    //�����ǲ����ʣ�����ֵ��0x07(125Hz)
#define CONFIG          0x1A    //��ͨ�˲�Ƶ�ʣ�����ֵ��0x06(5Hz)
#define GYRO_CONFIG     0x1B    //�������Լ켰������Χ������ֵ��0x18(���Լ죬2000deg/s)
#define ACCEL_CONFIG    0x1C    //���ټ��Լ졢������Χ����ͨ�˲�Ƶ�ʣ�����ֵ��0x01(���Լ죬2G��5Hz)

#define ACCEL_XOUT_H    0x3B
#define ACCEL_XOUT_L    0x3C
#define ACCEL_YOUT_H    0x3D
#define ACCEL_YOUT_L    0x3E
#define ACCEL_ZOUT_H    0x3F
#define ACCEL_ZOUT_L    0x40

#define TEMP_OUT_H      0x41
#define TEMP_OUT_L      0x42

#define GYRO_XOUT_H     0x43
#define GYRO_XOUT_L     0x44
#define GYRO_YOUT_H     0x45
#define GYRO_YOUT_L     0x46
#define GYRO_ZOUT_H     0x47
#define GYRO_ZOUT_L     0x48


#define MAG_XOUT_L      0x03
#define MAG_XOUT_H      0x04
#define MAG_YOUT_L      0x05
#define MAG_YOUT_H      0x06
#define MAG_ZOUT_L      0x07
#define MAG_ZOUT_H      0x08


#define PWR_MGMT_1               0x6B   //��Դ��������ֵ��0x01(��������)
#define MPU9250_WHO_AM_I          0x75  //IIC��ַ�Ĵ���(Ĭ����ֵ0x71��ֻ��)
#define   EXT_SENS_DATA_00           0x49


//****************************

#define GYRO_ADDRESS   0xD0   //���ݵ�ַ
#define MAG_ADDRESS    0x18   //�ų���ַ
#define ACCEL_ADDRESS  0xD0

//************************************
/*ģ��IIC 1�˿�������붨��*/
#define IIC1_SCL_H         GPIOB->BSRR = GPIO_Pin_6
#define IIC1_SCL_L         GPIOB->BRR  = GPIO_Pin_6

#define IIC1_SDA_H         GPIOB->BSRR = GPIO_Pin_7
#define IIC1_SDA_L         GPIOB->BRR  = GPIO_Pin_7

#define IIC1_SCL_read      GPIOB->IDR  & GPIO_Pin_6
#define IIC1_SDA_read      GPIOB->IDR  & GPIO_Pin_7

/*ģ��IIC 2�˿�������붨��*/
#define IIC2_SCL_H         GPIOB->BSRR = GPIO_Pin_8
#define IIC2_SCL_L         GPIOB->BRR  = GPIO_Pin_8

#define IIC2_SDA_H         GPIOB->BSRR = GPIO_Pin_9
#define IIC2_SDA_L         GPIOB->BRR  = GPIO_Pin_9

#define IIC2_SCL_read      GPIOB->IDR  & GPIO_Pin_8
#define IIC2_SDA_read      GPIOB->IDR  & GPIO_Pin_9

/*ģ��IIC 3�˿�������붨��*/
#define IIC3_SCL_H         GPIOB->BSRR = GPIO_Pin_10
#define IIC3_SCL_L         GPIOB->BRR  = GPIO_Pin_10

#define IIC3_SDA_H         GPIOB->BSRR = GPIO_Pin_11
#define IIC3_SDA_L         GPIOB->BRR  = GPIO_Pin_11

#define IIC3_SCL_read      GPIOB->IDR  & GPIO_Pin_10
#define IIC3_SDA_read      GPIOB->IDR  & GPIO_Pin_11

/*ģ��IIC 4�˿�������붨��*/
#define IIC4_SCL_H         GPIOB->BSRR = GPIO_Pin_14
#define IIC4_SCL_L         GPIOB->BRR  = GPIO_Pin_14

#define IIC4_SDA_H         GPIOB->BSRR = GPIO_Pin_15
#define IIC4_SDA_L         GPIOB->BRR  = GPIO_Pin_15

#define IIC4_SCL_read      GPIOB->IDR  & GPIO_Pin_14
#define IIC4_SDA_read      GPIOB->IDR  & GPIO_Pin_15

//************************************
/*ģ��IIC�˿�������붨��*/
#define SCL_H         GPIOB->BSRR =GPIO_Pin_6 | GPIO_Pin_10
#define SCL_L         GPIOB->BRR  = GPIO_Pin_6 | GPIO_Pin_10

#define SDA_H         GPIOB->BSRR = GPIO_Pin_7 |  GPIO_Pin_11
#define SDA_L         GPIOB->BRR  = GPIO_Pin_7 | GPIO_Pin_11

#define SCL_read      GPIOB->IDR  &  GPIO_Pin_10
#define SDA_read      GPIOB->IDR  &  (GPIO_Pin_7 |  GPIO_Pin_11)


#define MPU9250_SENSOR_NUM     7
struct mpu9250_data{
    int   accX;
    int   accY;
    int   accZ;
    int   gyroX;
    int   gyroY;
    int   gyroZ;
    int   magX;
    int   magY;
    int   magZ;
};
extern struct mpu9250_data g_SensorMem[MPU9250_SENSOR_NUM];
//void uartoutputmp9250data(void);
//void debug_mutil_read_test(void);
//void updataSensorAccGyto(u8 slaveaddr,struct mpu9250_data*ptrSensor1,struct mpu9250_data*ptrSensor2);
//void updateSensorMAG(u8 slaveaddr,struct mpu9250_data*ptrSensor1);
//int8_t i2write(uint8_t addr, uint8_t reg, uint8_t len, uint8_t * data);
//int8_t i2cread(cuint8_t addr, uint8_t reg, uint8_t len, uint8_t *buf);

#define TRUE   1
#define FALSE 0
#define true   1
#define false 0

int8_t i2c1writeLeftLeg(uint8_t addr, uint8_t reg, uint8_t len, uint8_t * data);
int8_t i2c1readLeftLeg(uint8_t addr, uint8_t reg, uint8_t len, uint8_t *buf);
int8_t i2c2writeRightLeg(uint8_t addr, uint8_t reg, uint8_t len, uint8_t * data);
int8_t i2c2readRightLeg(uint8_t addr, uint8_t reg, uint8_t len, uint8_t *buf);
int8_t i2c3writeBody(uint8_t addr, uint8_t reg, uint8_t len, uint8_t * data);
int8_t i2c3readBody(uint8_t addr, uint8_t reg, uint8_t len, uint8_t *buf);
int8_t i2c4write_foot(uint8_t addr, uint8_t reg, uint8_t len, uint8_t * data);
int8_t i2c4read_foot(uint8_t addr, uint8_t reg, uint8_t len, uint8_t *buf);
#endif

