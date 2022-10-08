#include "option.h"
/* Private typedef -----------------------------------------------------------*/
/* Private define ------------------------------------------------------------*/
__IO uint16_t ADC1ConvertedValue [INFRARED_SAMPLE_NUM_EACH_CHANNEL] [INFRARED_ADC_CHANNEL_NUM] ={ 0};
uint32_t    averageADC[INFRARED_ADC_CHANNEL_NUM]={0,0,0,0,0};

void infrared_adc_init(void)
{
    ADC_SoftwareStartConvCmd(ADC1, ENABLE);
       DMA_Cmd(DMA1_Channel1, ENABLE);
}

void check_Infrared_distance(void)
{
    int i,j;

    for(i=0;i<INFRARED_ADC_CHANNEL_NUM; i++)
    {
        printf("adc channel%d: ",i);
        for(j=0;j<INFRARED_SAMPLE_NUM_EACH_CHANNEL;j++)
                 printf(" 0x%04x",  ADC1ConvertedValue[j][i]);
        printf("\n");
    }
}

void infrared_adc_sample_close(void)
{
    ADC_SoftwareStartConvCmd(ADC1,DISABLE);
       DMA_Cmd(DMA1_Channel1, DISABLE);
}

void  CalculatedAverageADC(void)
{
    int i,j;
    uint32_t val = 0;

    for(i=0;i<INFRARED_ADC_CHANNEL_NUM; i++)
    {
        for(j=0;j<INFRARED_SAMPLE_NUM_EACH_CHANNEL;j++)
        {
            val += ADC1ConvertedValue[j][i];
        }
        averageADC[i] = val / INFRARED_SAMPLE_NUM_EACH_CHANNEL;
        val = 0;
        printf("average adc chn %d: 0x%04x\r\n", i+1,averageADC[i]);
    }
}
