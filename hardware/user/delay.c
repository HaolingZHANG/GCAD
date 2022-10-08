#include "option.h"

#define SYSTEMTICK_PERIOD_MS  10
__IO uint32_t LocalTime = 0; /* this variable is used to create a time reference incremented by 10ms */

//ʹ��SysTick����ͨ����ģʽ���ӳٽ��й���
//�������ж��е��ó�����ѭ���Ĵ���
//��ֹ��ʱ��׼ȷ,����do while�ṹ!

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
** ������: delay_ms
** ��������: ��ʱnms
** �������: nms
** �������: ��
** ˵����SysTick->LOADΪ24λ�Ĵ���,����,�����ʱΪ:
        nms<=0xffffff*8*1000/SYSCLK
        SYSCLK��λΪHz,nms��λΪms
        ��72M������,nms<=1864
***********************************************************/
/*void delay_ms(u16 nms)
{
    u32 temp;
    SysTick->LOAD=(u32)nms*fac_ms;//ʱ�����(SysTick->LOADΪ24bit)
    SysTick->VAL =0x00;           //��ռ�����
    SysTick->CTRL=0x01 ;          //��ʼ����
    do
    {
        temp=SysTick->CTRL;
    }
    while(temp&0x01&&!(temp&(1<<16)));//�ȴ�ʱ�䵽��
    SysTick->CTRL=0x00;       //�رռ�����
    SysTick->VAL =0X00;       //��ռ�����
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
** �������� �� Delay(vu32 nCount)
** �������� �� ��ʱ����
** ��    ��   �� ��
** ��    ��   �� ��
** ��    ��   �� ��
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
** �������� �� void Delayms(vu32 m)
** �������� �� ����ʱ����  m=1,��ʱ1ms
** ��    ��   �� ��
** ��    ��   �� ��
** ��    ��   �� ��
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

   u8 i=30; //��������Ż��ٶ�  ����������͵�5����д��
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
