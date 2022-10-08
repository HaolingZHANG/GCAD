#include "option.h"

//static UartReceiveCallBack gUart1ReceiveCallBack, gUart2ReceiveCallBack, gUart3ReceiveCallBack, gUart4ReceiveCallBack;
void Serial_init(void)
{
    USART_InitTypeDef USART_InitStructure;
    USART_InitStructure.USART_BaudRate = CONFIG_SERIAL_USART1_BAURATE;
    USART_InitStructure.USART_WordLength = USART_WordLength_8b;
    USART_InitStructure.USART_StopBits = USART_StopBits_1;
    USART_InitStructure.USART_Parity = USART_Parity_No;
    USART_InitStructure.USART_HardwareFlowControl = USART_HardwareFlowControl_None;
    USART_InitStructure.USART_Mode = USART_Mode_Rx | USART_Mode_Tx;
    USART_Init(USART1,&USART_InitStructure);

    USART_ITConfig(USART1,USART_IT_RXNE,ENABLE);
    //USART_ITConfig(USART1, USART_IT_TXE, ENABLE);
    USART_Cmd(USART1, ENABLE);
    /* Output a message on Hyperterminal using printf function */
    SerialWrite(USART1,"USART1 INIT SUCCEED!\r\n",strlen("USART1 INIT SUCCEED!\r\n"));


    USART_InitStructure.USART_BaudRate = CONFIG_SERIAL_USART2_BAURATE;
    USART_InitStructure.USART_WordLength = USART_WordLength_8b;
    USART_InitStructure.USART_StopBits = USART_StopBits_1;
    USART_InitStructure.USART_Parity = USART_Parity_No;
    USART_InitStructure.USART_HardwareFlowControl = USART_HardwareFlowControl_None;
    USART_InitStructure.USART_Mode = USART_Mode_Rx | USART_Mode_Tx;
    USART_Init(USART2, &USART_InitStructure);
       USART_ITConfig(USART2,USART_IT_RXNE,ENABLE);
    //USART_ITConfig(USART2, USART_IT_TXE, ENABLE);
    USART_Cmd(USART2, ENABLE);/*vv      */

}

void SerialWrite(USART_TypeDef* USARTx, char *ch, char lenth)
{
    unsigned int tmp;

    while(USART_GetFlagStatus(USARTx,USART_FLAG_TXE)==RESET);
    for(tmp=0;tmp<lenth;)
    {
            USART_SendData(USARTx, ch[tmp++]);
            while(USART_GetFlagStatus(USARTx, USART_FLAG_TC) == RESET)
            {
            }
    }
}

int fputc(int ch, FILE *f)
 {
     USART1->DR = (u8) ch;

     /* Loop until the end of transmission */
     while(USART_GetFlagStatus(USART1, USART_FLAG_TXE) == RESET)
     {
     }

     return ch;
 }

 int fgetc(FILE *f)
 {

    while (USART_GetFlagStatus(USART1, USART_FLAG_RXNE) == RESET);

    return (int)USART_ReceiveData(USART1);
 }



void UartSendSomeBytes(USART_TypeDef* Uartx, uint8_t *data, uint16_t dataLen)
{
    uint16_t i = 0;
    for(i = 0; i < dataLen; i++)
    {
        while( USART_GetFlagStatus(Uartx, USART_FLAG_TC) == RESET );
        USART_SendData(Uartx, data[i]);
        while( USART_GetFlagStatus(Uartx, USART_FLAG_TC) == RESET );
    }
}

void Uart1IRQHandler(void)
{
    uint16_t temp = 0x11;
    //UartSendSomeBytes(USART1, &temp, 1);
     if(USART_GetITStatus(USART1, USART_IT_RXNE) != RESET)
    {
        temp = USART_ReceiveData(USART1);
        UartSendSomeBytes(USART1, (uint8_t *)&temp, 1);
//      UartSendSomeBytes(USART2, (uint8_t *)&temp, 1);
        //gUart1ReceiveCallBack(temp);
    }
}

void Uart2IRQHandler(void)
{
    uint16_t temp = 0;

    if(USART_GetITStatus(USART2, USART_IT_RXNE) != RESET)
    {
        temp = USART_ReceiveData(USART2);
        //UartSendSomeBytes(USART1, (uint8_t *)&temp, 1);
        receveGoingInData((uint8_t)temp);
        //gUart1ReceiveCallBack(temp);
    }
}
