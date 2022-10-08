#ifndef __DELAY_H
#define __DELAY_H
//使用SysTick的普通计数模式对延迟进行管理
void delay_init(u8 SYSCLK);
void delay_ms(u16 nms);
void delay_us(u32 nus);
void Delay_s(u32 s);
void Time_Update(void);
void Delay(vu32 nCount);
void Delayms(vu32 m);
void get_ms(unsigned long *time);
void I2C_delay(void);

void delay5ms(void);

extern void DelayLED(unsigned int uiDly);
extern __IO uint32_t LocalTime;

#define delay_ms    Delayms
#define get_ms      get_ms
#endif
