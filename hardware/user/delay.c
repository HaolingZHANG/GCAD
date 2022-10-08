#include "option.h"

#define SYSTEMTICK_PERIOD_MS  10
__IO uint32_t LocalTime = 0; /* this variable is used to create a time reference incremented by 10ms */

//使用SysTick的普通计数模式对延迟进行管理
//修正了中断中调用出现死循环的错误
//防止延时不准确,采用do while结构!

static u8   ii = 0;
void Time_Update(void)
{
  LocalTime += SYSTEMTICK_PERIOD_MS;
  if(0 == LocalTime%1000)
  {
    if(ii == 0)
    {
        GPIO_SetBits(GPIOC, GPIO_Pin_13);
        ii = 1;
    }
    else
    {
        GPIO_ResetBits(GPIOC, GPIO_Pin_13);
        ii = 0;
    }
  }
}

/**********************************************************
** 函数名: delay_ms
** 功能描述: 延时nms
** 输入参数: nms
** 输出参数: 无
** 说明：SysTick->LOAD为24位寄存器,所以,最大延时为:
        nms<=0xffffff*8*1000/SYSCLK
        SYSCLK单位为Hz,nms单位为ms
        对72M条件下,nms<=1864
***********************************************************/
/*void delay_ms(u16 nms)
{
    u32 temp;
    SysTick->LOAD=(u32)nms*fac_ms;//时间加载(SysTick->LOAD为24bit)
    SysTick->VAL =0x00;           //清空计数器
    SysTick->CTRL=0x01 ;          //开始倒数
    do
    {
        temp=SysTick->CTRL;
    }
    while(temp&0x01&&!(temp&(1<<16)));//等待时间到达
    SysTick->CTRL=0x00;       //关闭计数器
    SysTick->VAL =0X00;       //清空计数器
}
*/



void Delay_s(u32 s)
{
    u32 n;
    for(n=0;n<s;n++)
    {
        delay_ms(1000);
    }
}

void DelayLED(unsigned int uiDly)
{
     while(uiDly--);
}

/*
********************************************************************************
** 函数名称 ： Delay(vu32 nCount)
** 函数功能 ： 延时函数
** 输    入   ： 无
** 输    出   ： 无
** 返    回   ： 无
********************************************************************************
*/
 void Delay(vu32 nCount)
{
  for(; nCount != 0; nCount--);
}
void get_ms(unsigned long *time)
{

}
/*
********************************************************************************
** 函数名称 ： void Delayms(vu32 m)
** 函数功能 ： 长延时函数  m=1,延时1ms
** 输    入   ： 无
** 输    出   ： 无
** 返    回   ： 无
********************************************************************************
*/
 void Delayms(vu32 m)
{
  u32 i;

  for(; m != 0; m--)
       for (i=0; i<50000; i++);
}

void I2C_delay(void)
{

   u8 i=30; //这里可以优化速度  ，经测试最低到5还能写入
   while(i)
   {
     i--;
   }
}

void delay5ms(void)
{

   int i=5000;
   while(i)
   {
     i--;
   }
}
