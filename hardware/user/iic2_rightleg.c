#include "option.h"

/*******************************************************************************
* Function Name  : I2C2_Start
* Description    : Master Start Simulation IIC Communication
* Input          : None
* Output         : None
* Return         : Wheather  Start
****************************************************************************** */
char I2C2_Start(void)
{
    IIC2_SDA_H;
    IIC2_SCL_H;
    I2C_delay();
    if(!IIC2_SDA_read)return FALSE; //SDA线为低电平则总线忙,退出
    IIC2_SDA_L;
    I2C_delay();
    if(IIC2_SDA_read) return FALSE; //SDA线为高电平则总线出错,退出
    IIC2_SDA_L;
    I2C_delay();
    return TRUE;
}
/*******************************************************************************
* Function Name  : I2C2_Stop
* Description    : Master Stop Simulation IIC Communication
* Input          : None
* Output         : None
* Return         : None
****************************************************************************** */
void I2C2_Stop(void)
{
    IIC2_SCL_L;
    I2C_delay();
    IIC2_SDA_L;
    I2C_delay();
    IIC2_SCL_H;
    I2C_delay();
    IIC2_SDA_H;
    I2C_delay();
}
/*******************************************************************************
* Function Name  : I2C2_Ack
* Description    : Master Send Acknowledge Single
* Input          : None
* Output         : None
* Return         : None
****************************************************************************** */
void I2C2_Ack(void)
{
    IIC2_SCL_L;
    I2C_delay();
    IIC2_SDA_L;
    I2C_delay();
    IIC2_SCL_H;
    I2C_delay();
    IIC2_SCL_L;
    I2C_delay();
}
/*******************************************************************************
* Function Name  : I2C2_NoAck
* Description    : Master Send No Acknowledge Single
* Input          : None
* Output         : None
* Return         : None
****************************************************************************** */
void I2C2_NoAck(void)
{
    IIC2_SCL_L;
    I2C_delay();
    IIC2_SDA_H;
    I2C_delay();
    IIC2_SCL_H;
    I2C_delay();
    IIC2_SCL_L;
    I2C_delay();
}
/*******************************************************************************
* Function Name  : I2C2_WaitAck
* Description    : Master Reserive Slave Acknowledge Single
* Input          : None
* Output         : None
* Return         : Wheather  Reserive Slave Acknowledge Single
****************************************************************************** */
char I2C2_WaitAck(void)      //返回为:=1有ACK,=0无ACK
{
    IIC2_SCL_L;
    I2C_delay();
    IIC2_SDA_H;
    I2C_delay();
    IIC2_SCL_H;
    I2C_delay();
    if(IIC2_SDA_read)
    {
      IIC2_SCL_L;
      I2C_delay();
      return FALSE;
    }
    IIC2_SCL_L;
    I2C_delay();
    return TRUE;
}
/*******************************************************************************
* Function Name  : I2C2_SendByte
* Description    : Master Send a Byte to Slave
* Input          : Will Send Date
* Output         : None
* Return         : None
****************************************************************************** */
void I2C2_SendByte(u8 SendByte) //数据从高位到低位//
{
    u8 i=8;
    while(i--)
    {
        IIC2_SCL_L;
        I2C_delay();
      if(SendByte&0x80)
        IIC2_SDA_H;
      else
        IIC2_SDA_L;
        SendByte<<=1;
        I2C_delay();
        IIC2_SCL_H;
        I2C_delay();
    }
    IIC2_SCL_L;
}
unsigned char I2C2_WriteBuffer(uint8_t addr, uint8_t reg, uint8_t len, uint8_t * data)
{
    int i;
    if (!I2C2_Start())
        return false;
    I2C2_SendByte(addr);
    if (!I2C2_WaitAck()) {
        I2C2_Stop();
        return false;
    }
    I2C2_SendByte(reg);
    if (!I2C2_WaitAck()) {
        I2C2_Stop();
        return false;
    }
    for (i = 0; i < len; i++) {
        I2C2_SendByte(data[i]);
        if (!I2C2_WaitAck()) {
            I2C2_Stop();
            return false;
        }
    }
    I2C2_Stop();
    return true;
}
static uint8_t I2C2_ReceiveByte(void)
{
    uint8_t i = 8;
    uint8_t byte = 0;

    IIC2_SDA_H;
    while (i--) {
        byte <<= 1;
        IIC2_SCL_L;
        I2C_delay();
        IIC2_SCL_H;
        I2C_delay();
        if (IIC2_SDA_read) {
            byte |= 0x01;
        }
    }
    IIC2_SCL_L;
    return byte;
}
unsigned char I2C2_ReadBuffer(uint8_t addr, uint8_t reg, uint8_t len, uint8_t *buf)
{
    if (!I2C2_Start())
        return false;
    I2C2_SendByte(addr);
    if (!I2C2_WaitAck()) {
        I2C2_Stop();
        return false;
    }
    I2C2_SendByte(reg);
    I2C2_WaitAck();
    I2C2_Start();
    I2C2_SendByte(addr +1);
    I2C2_WaitAck();
    while (len) {
        *buf = I2C2_ReceiveByte();
        if (len == 1)
            I2C2_NoAck();
        else
            I2C2_Ack();
        buf++;
        len--;
    }
    I2C2_Stop();
    return true;
}

int8_t i2c2writeRightLeg(uint8_t addr, uint8_t reg, uint8_t len, uint8_t * data)
{
    if(I2C2_WriteBuffer(addr<<1,reg,len,data))
    {
        return 0;
    }
    else
    {
        return -1;
    }
}
int8_t i2c2readRightLeg(uint8_t addr, uint8_t reg, uint8_t len, uint8_t *buf)
{
    if(I2C2_ReadBuffer(addr<<1,reg,len,buf))
    {
        return 0;
    }
    else
    {
        return -1;
    }

}


