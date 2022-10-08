#include "option.h"
#include "Kalman.h"
#include <math.h>

#define RESTRICT_PITCH // Comment out to restrict roll to ±90deg instead - please read: http://www.freescale.com/files/sensors/doc/app_note/AN3461.pdf

struct Kalman kalmanX, kalmanY, kalmanZ; // Create the Kalman instances

/* IMU Data MPU6050 AND HMC5883 Data*/
int accX, accY, accZ;
int gyroX, gyroY, gyroZ;
int magX, magY, magZ;


double roll, pitch, yaw; // Roll and pitch are calculated using the accelerometer while yaw is calculated using the magnetometer

double gyroXangle, gyroYangle, gyroZangle; // Angle calculate using the gyro only 只用陀螺仪计算角度
double compAngleX, compAngleY, compAngleZ; // Calculated angle using a complementary filter  用电磁计计算角度
double kalAngleX, kalAngleY, kalAngleZ; // Calculated angle using a Kalman filter    用kalman计算角度

//uint32_t timer,micros; //上一次时间与当前时间
uint8_t i2cData[14]; // Buffer for I2C data

#define MAG0MAX 603
#define MAG0MIN -578

#define MAG1MAX 542
#define MAG1MIN -701

#define MAG2MAX 547
#define MAG2MIN -556

#define RAD_TO_DEG 57.295779513082320876798154814105  // 弧度转角度的转换率
#define DEG_TO_RAD 0.01745329251994329576923690768489 // 角度转弧度的转换率

float magOffset[3] = { (MAG0MAX + MAG0MIN) / 2, (MAG1MAX + MAG1MIN) / 2, (MAG2MAX + MAG2MIN) / 2 };
double magGain[3];

extern struct mpu9250_data g_SensorMem[MPU9250_SENSOR_NUM];

void updateYaw(struct mpu9250_data*ptrSensor1);
void updatePitchRoll(struct mpu9250_data*ptrSensor1);

void InitAll()
{
    /* Set Kalman and gyro starting angle */
    updataSensorAccGyto(GYRO_ADDRESS,&g_SensorMem[0],&g_SensorMem[1]);
    updateSensorMAG(MAG_ADDRESS,&g_SensorMem[0]);
    updatePitchRoll(&g_SensorMem[0]);
    updateYaw(&g_SensorMem[0]);

    setAngle(&kalmanX,roll); // First set roll starting angle
    gyroXangle = roll;
    compAngleX = roll;

    setAngle(&kalmanY,pitch); // Then pitch
    gyroYangle = pitch;
    compAngleY = pitch;

    setAngle(&kalmanZ,yaw); // And finally yaw
    gyroZangle = yaw;
    compAngleZ = yaw;

//    timer = micros; // Initialize the timer
}
//****************************************
//根据加速计刷新Pitch和Roll数据
//这里采用两种方法计算roll和pitch，如果最上面没有#define RESTRICT_PITCH就采用第二种计算方法
//****************************************
void updatePitchRoll(struct mpu9250_data*ptrSensor1) {
    // Source: http://www.freescale.com/files/sensors/doc/app_note/AN3461.pdf eq. 25 and eq. 26
    // atan2 outputs the value of -π to π (radians) - see http://en.wikipedia.org/wiki/Atan2
    // It is then converted from radians to degrees
    #ifdef RESTRICT_PITCH // Eq. 25 and 26
    roll = atan2(ptrSensor1->accY,ptrSensor1->accZ) * RAD_TO_DEG;
    pitch = atan(-ptrSensor1->accX / sqrt(ptrSensor1->accY * ptrSensor1->accY + ptrSensor1->accZ * ptrSensor1->accZ)) * RAD_TO_DEG;
    #else // Eq. 28 and 29
    roll = atan(ptrSensor1->accY / sqrt(ptrSensor1->accX * ptrSensor1->accX + ptrSensor1->accZ * ptrSensor1->accZ)) * RAD_TO_DEG;
    pitch = atan2(-ptrSensor1->accX, ptrSensor1->accZ) * RAD_TO_DEG;
    #endif
}
//****************************************
//根据磁力计刷新Yaw角
//****************************************
void updateYaw(struct mpu9250_data*ptrSensor1) { // See: http://www.freescale.com/files/sensors/doc/app_note/AN4248.pdf
    double rollAngle,pitchAngle,Bfy,Bfx;

    ptrSensor1->magX *= -1; // Invert axis - this it done here, as it should be done after the calibration
    ptrSensor1->magZ *= -1;

    ptrSensor1->magX *= magGain[0];
    ptrSensor1->magY *= magGain[1];
    ptrSensor1->magZ *= magGain[2];

    ptrSensor1->magX -= magOffset[0];
    ptrSensor1->magY -= magOffset[1];
    ptrSensor1->magZ -= magOffset[2];


    rollAngle  = kalAngleX * DEG_TO_RAD;
    pitchAngle = kalAngleY * DEG_TO_RAD;

    Bfy = ptrSensor1->magZ * sin(rollAngle) - ptrSensor1->magY * cos(rollAngle);
    Bfx = ptrSensor1->magX * cos(pitchAngle) + ptrSensor1->magY * sin(pitchAngle) * sin(rollAngle) + ptrSensor1->magZ * sin(pitchAngle) * cos(rollAngle);
    yaw = atan2(-Bfy, Bfx) * RAD_TO_DEG;

    yaw *= -1;
}
void func(u8 slaveaddr,struct mpu9250_data* ptrSensor1)
{
    double gyroXrate,gyroYrate,gyroZrate,dt=0.01;
    /* Update all the IMU values */
    updataSensorAccGyto(slaveaddr,ptrSensor1,&g_SensorMem[1]);
    updateSensorMAG(MAG_ADDRESS,&g_SensorMem[0]);

//    dt = (double)(micros - timer) / 1000; // Calculate delta time
//    timer = micros;
//    if(dt<0)dt+=(1<<20);    //时间是周期性的，有可能当前时间小于上次时间，因为这个周期远大于两次积分时间，所以最多相差1<<20

    /* Roll and pitch estimation */
    updatePitchRoll(ptrSensor1);             //用采集的加速计的值计算roll和pitch的值
    gyroXrate = ptrSensor1->gyroX / 131.0;     // Convert to deg/s    把陀螺仪的角加速度按照当初设定的量程转换为°/s
    gyroYrate = ptrSensor1->gyroY / 131.0;     // Convert to deg/s

    #ifdef RESTRICT_PITCH        //如果上面有#define RESTRICT_PITCH就采用这种方法计算，防止出现-180和180之间的跳跃
    // This fixes the transition problem when the accelerometer angle jumps between -180 and 180 degrees
    if ((roll < -90 && kalAngleX > 90) || (roll > 90 && kalAngleX < -90)) {
        setAngle(&kalmanX,roll);
        compAngleX = roll;
        kalAngleX = roll;
        gyroXangle = roll;
    } else
    kalAngleX = getAngle(&kalmanX, roll, gyroXrate, dt); // Calculate the angle using a Kalman filter

    if (fabs(kalAngleX) > 90)
        gyroYrate = -gyroYrate; // Invert rate, so it fits the restricted accelerometer reading
    kalAngleY = getAngle(&kalmanY,pitch, gyroYrate, dt);
    #else
    // This fixes the transition problem when the accelerometer angle jumps between -180 and 180 degrees
    if ((pitch < -90 && kalAngleY > 90) || (pitch > 90 && kalAngleY < -90)) {
        kalmanY.setAngle(pitch);
        compAngleY = pitch;
        kalAngleY = pitch;
        gyroYangle = pitch;
    } else
    kalAngleY = getAngle(&kalmanY, pitch, gyroYrate, dt); // Calculate the angle using a Kalman filter

    if (abs(kalAngleY) > 90)
        gyroXrate = -gyroXrate; // Invert rate, so it fits the restricted accelerometer reading
    kalAngleX = getAngle(&kalmanX, roll, gyroXrate, dt); // Calculate the angle using a Kalman filter
    #endif


    /* Yaw estimation */
    updateYaw(ptrSensor1);
    gyroZrate = ptrSensor1->gyroZ / 131.0; // Convert to deg/s
    // This fixes the transition problem when the yaw angle jumps between -180 and 180 degrees
    if ((yaw < -90 && kalAngleZ > 90) || (yaw > 90 && kalAngleZ < -90)) {
        setAngle(&kalmanZ,yaw);
        compAngleZ = yaw;
        kalAngleZ = yaw;
        gyroZangle = yaw;
    } else
    kalAngleZ = getAngle(&kalmanZ, yaw, gyroZrate, dt); // Calculate the angle using a Kalman filter


    /* Estimate angles using gyro only */
    gyroXangle += gyroXrate * dt; // Calculate gyro angle without any filter
    gyroYangle += gyroYrate * dt;
    gyroZangle += gyroZrate * dt;
    //gyroXangle += kalmanX.getRate() * dt; // Calculate gyro angle using the unbiased rate from the Kalman filter
    //gyroYangle += kalmanY.getRate() * dt;
    //gyroZangle += kalmanZ.getRate() * dt;

    /* Estimate angles using complimentary filter */
    compAngleX = 0.93 * (compAngleX + gyroXrate * dt) + 0.07 * roll; // Calculate the angle using a Complimentary filter
    compAngleY = 0.93 * (compAngleY + gyroYrate * dt) + 0.07 * pitch;
    compAngleZ = 0.93 * (compAngleZ + gyroZrate * dt) + 0.07 * yaw;

    // Reset the gyro angles when they has drifted too much
    if (gyroXangle < -180 || gyroXangle > 180)
        gyroXangle = kalAngleX;
    if (gyroYangle < -180 || gyroYangle > 180)
        gyroYangle = kalAngleY;
    if (gyroZangle < -180 || gyroZangle > 180)
        gyroZangle = kalAngleZ;


    printf("roll %f,pitch %f,yaw %f\n",roll,pitch,yaw);
//    send(roll,pitch,yaw);
//    send(gyroXangle,gyroYangle,gyroZangle);
//    send(compAngleX,compAngleY,compAngleZ);
//    send(kalAngleX,kalAngleY,kalAngleZ);
//    send(kalAngleY,compAngleY,gyroYangle);
}
