#ifndef __SERIAL_H
#define __SERIAL_H

void Serial_init(void);
void UartSendSomeBytes(USART_TypeDef* Uartx, uint8_t *data, uint16_t dataLen);
void SerialWrite(USART_TypeDef* USARTx, char *ch, char lenth);
void Uart1IRQHandler(void);
void Uart2IRQHandler(void);

#endif
