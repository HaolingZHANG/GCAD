#include "option.h"

/*******************************************************************************
* Function Name  : I2C1_Start
* Description    : Master Start Simulation IIC Communication
* Input          : None
* Output         : None
* Return         : Wheather  Start
****************************************************************************** */
char I2C1_Start(void)
{
    IIC1_SDA_H;
    IIC1_SCL_H;
    I2C_delay();
    if(!IIC1_SDA_read)return FALSE; //SDA线为低电平则总线忙,退出
    IIC1_SDA_L;
    I2C_delay();
    if(IIC1_SDA_read) return FALSE; //SDA线为高电平则总线出错,退出
    IIC1_SDA_L;
    I2C_delay();
    return TRUE;
}
/*******************************************************************************
* Function Name  : I2C1_Stop
* Description    : Master Stop Simulation IIC Communication
* Input          : None
* Output         : None
* Return         : None
****************************************************************************** */
void I2C1_Stop(void)
{
    IIC1_SCL_L;
    I2C_delay();
    IIC1_SDA_L;
    I2C_delay();
    IIC1_SCL_H;
    I2C_delay();
    IIC1_SDA_H;
    I2C_delay();
}
/*******************************************************************************
* Function Name  : I2C1_Ack
* Description    : Master Send Acknowledge Single
* Input          : None
* Output         : None
* Return         : None
****************************************************************************** */
void I2C1_Ack(void)
{
    IIC1_SCL_L;
    I2C_delay();
    IIC1_SDA_L;
    I2C_delay();
    IIC1_SCL_H;
    I2C_delay();
    IIC1_SCL_L;
    I2C_delay();
}
/*******************************************************************************
* Function Name  : I2C1_NoAck
* Description    : Master Send No Acknowledge Single
* Input          : None
* Output         : None
* Return         : None
****************************************************************************** */
void I2C1_NoAck(void)
{
    IIC1_SCL_L;
    I2C_delay();
    IIC1_SDA_H;
    I2C_delay();
    IIC1_SCL_H;
    I2C_delay();
    IIC1_SCL_L;
    I2C_delay();
}
/*******************************************************************************
* Function Name  : I2C1_WaitAck
* Description    : Master Reserive Slave Acknowledge Single
* Input          : None
* Output         : None
* Return         : Wheather  Reserive Slave Acknowledge Single
****************************************************************************** */
char I2C1_WaitAck(void)      //返回为:=1有ACK,=0无ACK
{
    IIC1_SCL_L;
    I2C_delay();
    IIC1_SDA_H;
    I2C_delay();
    IIC1_SCL_H;
    I2C_delay();
    if(IIC1_SDA_read)
    {
      IIC1_SCL_L;
      I2C_delay();
      return FALSE;//0
    }
    IIC1_SCL_L;
    I2C_delay();
    return TRUE;// 1
}
/*******************************************************************************
* Function Name  : I2C1_SendByte
* Description    : Master Send a Byte to Slave
* Input          : Will Send Date
* Output         : None
* Return         : None
****************************************************************************** */
void I2C1_SendByte(u8 SendByte) //数据从高位到低位//
{
    u8 i=8;
    while(i--)
    {
        IIC1_SCL_L;
        I2C_delay();
      if(SendByte&0x80)
        IIC1_SDA_H;
      else
        IIC1_SDA_L;
        SendByte<<=1;
        I2C_delay();
        IIC1_SCL_H;
        I2C_delay();
    }
    IIC1_SCL_L;
}
unsigned char I2C1_WriteBuffer(uint8_t addr, uint8_t reg, uint8_t len, uint8_t * data)
{
    int i;
    if (!I2C1_Start())
        return false;
    I2C1_SendByte(addr);
    if (!I2C1_WaitAck()) {
        I2C1_Stop();
        return false;
    }
    I2C1_SendByte(reg);
     if (!I2C1_WaitAck()) {
        I2C1_Stop();
        return false;
    }
    for (i = 0; i < len; i++) {
        I2C1_SendByte(data[i]);
        if (!I2C1_WaitAck()) {
            I2C1_Stop();
            return false;
        }
    }
    I2C1_Stop();
    return true;
}
static uint8_t I2C1_ReceiveByte(void)
{
    uint8_t i = 8;
    uint8_t byte = 0;

    IIC1_SDA_H;
    while (i--) {
        byte <<= 1;
        IIC1_SCL_L;
        I2C_delay();
        IIC1_SCL_H;
        I2C_delay();
        if (IIC1_SDA_read) {
            byte |= 0x01;
        }
    }
    IIC1_SCL_L;
    return byte;
}
unsigned char I2C1_ReadBuffer(uint8_t addr, uint8_t reg, uint8_t len, uint8_t *buf)
{
    if (!I2C1_Start())
        return false;
    I2C1_SendByte(addr);
    if (!I2C1_WaitAck()) {
        I2C1_Stop();
        return false;
    }
    I2C1_SendByte(reg);
    I2C1_WaitAck();
    I2C1_Start();
    I2C1_SendByte(addr +1);
    I2C1_WaitAck();
    while (len) {
        *buf = I2C1_ReceiveByte();
        if (len == 1)
            I2C1_NoAck();
        else
            I2C1_Ack();
        buf++;
        len--;
    }
    I2C1_Stop();
    return true;
}

int8_t i2c1writeLeftLeg(uint8_t addr, uint8_t reg, uint8_t len, uint8_t * data)
{
    if(I2C1_WriteBuffer(addr<<1,reg,len,data))
    {
        return 0;
    }
    else
    {
        return -1;
    }
}
int8_t i2c1readLeftLeg(uint8_t addr, uint8_t reg, uint8_t len, uint8_t *buf)
{
    if(I2C1_ReadBuffer(addr<<1,reg,len,buf))
    {
        return 0;
    }
    else
    {
        return -1;
    }
    //return FALSE;
}


