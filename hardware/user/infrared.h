#ifndef __INFRARED_H
#define __INFRARED_H
//使用SysTick的普通计数模式对延迟进行管理

#define INFRARED_ADC_CHANNEL_NUM            5
#define INFRARED_SAMPLE_NUM_EACH_CHANNEL    10


void infrared_adc_init(void);
void check_Infrared_distance(void);
void infrared_adc_sample_close(void);
void  CalculatedAverageADC(void);

extern __IO uint16_t ADC1ConvertedValue [INFRARED_SAMPLE_NUM_EACH_CHANNEL] [INFRARED_ADC_CHANNEL_NUM];
extern uint32_t      averageADC[INFRARED_ADC_CHANNEL_NUM];

#endif
