/* Includes ------------------------------------------------------------------*/
#include "option.h"

/* Private functions ---------------------------------------------------------*/
extern void Sys_init(void);

u8 errCodeMpu = 0,errCodeInfrared = 0;
/**
  * @brief  Main program.
  * @param  None
  * @retval None
  */
int main(void)
{
    u32  timepre = 0;
    u8 stateFlag = 0;
    int isLeft;
    Sys_init();

    GPIO_ResetBits(GPIOC, GPIO_Pin_13);
#if DEBUG_ADC_INFRAED
    check_Infrared_distance();
#endif
    CalculatedAverageADC();
    infrared_adc_sample_close();
    mpu_iic_test();
    mpu_dmp_init_all();
//  DelayLED(0x54321);
    while (1)
    {
        checkRecMsgFromBuf();
        sendMsgtoGCAD();
        if((LocalTime - timepre) > 20)
        {
             timepre = LocalTime;
             stateFlag = checkFootMotionState(&isLeft);
             if(stateFlag)
                 CalculatedPRDxyz(stateFlag,isLeft);
        }
    }
}

/**
  * @brief  Retargets the C library printf function to the USART.
  * @param  None
  * @retval None
  */

#ifdef  USE_FULL_ASSERT

/**
  * @brief  Reports the name of the source file and the source line number
  *         where the assert_param error has occurred.
  * @param  file: pointer to the source file name
  * @param  line: assert_param error line source number
  * @retval None
  */
void assert_failed(uint8_t* file, uint32_t line)
{
  /* User can add his own implementation to report the file name and line number,
     ex: printf("Wrong parameters value: file %s on line %d\r\n", file, line) */

  /* Infinite loop */
  while (1)
  {
  }
}
#endif

/**
  * @}
  */


/******************* (C) COPYRIGHT 2011 STMicroelectronics *****END OF FILE****/
