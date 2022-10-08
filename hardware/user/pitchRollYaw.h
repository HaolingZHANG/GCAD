#ifndef __PITCHROLLYAW_H
#define __PITCHROLLYAW_H

struct pitchRollYaw_st{
    float fPitch;
    float fRoll;
    float fYaw;
};

extern struct sensor_i2cList mpu9250_iic_SensorList[MPU9250_SENSOR_NUM];
void mpu_init_foot_sensor(void);
void mpu_dmp_init(u8 SensorID);
void mpu_dmp_init_all(void);
void calculatedDMP(u8 SensorID,struct pitchRollYaw_st *pstPRY);
void calculatedDMPAll(struct pitchRollYaw_st *pstPRY);
int checkFootAccel(float *acceel_right,float *accel_left);
void mpu_iic_test(void);
void readYawPassThrough(u8 id,short *data);
extern int8_t (*i2c_write)(uint8_t addr, uint8_t reg, uint8_t len, uint8_t * data);
extern int8_t (*i2c_read)(uint8_t addr, uint8_t reg, uint8_t len, uint8_t * data);
#endif
