#include "option.h"

#define USERINFO_FLASH_ADDR     (__IO uint32_t*) (0x0800f000)
#define BANK1_WRITE_START_ADDR  ((uint32_t)0x0800f000)
#define BANK1_WRITE_END_ADDR    ((uint32_t)0x0800f800)
#define FLASH_PAGE_SIZE         ((uint16_t)0x400)

#define MSG_HEADER_LEN       12
#define MSG_LEN_MIN          17
#define MSG_GCAD_ID0         0x04
#define MSG_GCAD_ID1         0x93
#define MSG_PRD_ID0          0x02
#define MSG_PRD_ID1          0x43
#define MSG_HEADTOBADY_LEN   14

#define MSG_BUF_LINE_MAX     10
#define MSG_BUF_LEN_MAX      50
#define SEND_MSG_BUF_LINE    50

struct MsgCtrl {
    u8  MsgFlag;
    u8  msgBufLine;
    u8  count;
    u8  get;
};
struct UsrInfo {
    u8 phoneNum[11];
    u8 passwad[6];
};

struct SendMsgQueue {
    u8 get;
    u8 put;
    u8 sendBuf[SEND_MSG_BUF_LINE][MSG_BUF_LEN_MAX];
};

u8                  g_usrLoginFlag = 0;
u8                  msgBuf[MSG_BUF_LINE_MAX][MSG_BUF_LEN_MAX] = { 0 };
u16                 msgBufSize[MSG_BUF_LINE_MAX] = {0};
u32                 g_msgG = 0,g_msgS = 0;

struct UsrInfo      g_usrInfo = {0};
struct MsgCtrl      g_msgCtrl = {0,0,0,0};
struct SendMsgQueue g_sendMsgQueue;

void usrInfoGetFromFlase(struct UsrInfo * ptr);

void msgCommunicateInit(void)
{
     memset(&g_msgCtrl,0,sizeof(g_msgCtrl));
     memset(msgBuf,0,sizeof(msgBuf));
     memset(&g_usrInfo,0,sizeof(g_usrInfo));
     memset(&g_sendMsgQueue, 0,sizeof(g_sendMsgQueue));
     usrInfoGetFromFlase(&g_usrInfo);
}

void usrInfoGetFromFlase(struct UsrInfo * ptr)
{
    u8  tmp[12];

    struct UsrInfo *p = (struct UsrInfo *)USERINFO_FLASH_ADDR;
    strncpy((char*)ptr, (char*)p, sizeof(struct UsrInfo));

    printf("check usr info:");
    strncpy((char*)tmp, (char*)ptr, 11);

    if(tmp[0]!=0x00 && tmp[0]!=0xff)
    {
        tmp[11] = 0;
        printf("phone:%s,passwd:%s\r\n",tmp,ptr->passwad);
    }
    else
    {
         printf("need init usrinfo\r\n");
    }
}
void usrInfoSaveToFlase(struct UsrInfo * ptr)
{
    int *p = (int *)ptr;
    uint32_t EraseCounter = 0x0, Address = 0x0;
    uint32_t NbrOfPage;
    uint32_t len = 0;
    volatile FLASH_Status FLASHStatus = FLASH_COMPLETE;

    FLASH_UnlockBank1();
    /* Define the number of page to be erased */
    NbrOfPage = (BANK1_WRITE_END_ADDR - BANK1_WRITE_START_ADDR) / FLASH_PAGE_SIZE;

    /* Clear All pending flags */
    FLASH_ClearFlag(FLASH_FLAG_EOP | FLASH_FLAG_PGERR | FLASH_FLAG_WRPRTERR);

    /* Erase the FLASH pages */
    for(EraseCounter = 0; (EraseCounter < NbrOfPage) && (FLASHStatus == FLASH_COMPLETE); EraseCounter++)
    {
    FLASHStatus = FLASH_ErasePage(BANK1_WRITE_START_ADDR + (FLASH_PAGE_SIZE * EraseCounter));
    }

    /* Program Flash Bank1 */
    Address = BANK1_WRITE_START_ADDR;
    len =  sizeof(struct UsrInfo);
    while((Address < (BANK1_WRITE_START_ADDR+len)) && (FLASHStatus == FLASH_COMPLETE))
    {
    FLASHStatus = FLASH_ProgramWord(Address, *p);
    Address = Address + 4;
    p ++;
    }

    FLASH_LockBank1();
}
void usrInfoFlashErase(void)
{
    uint32_t EraseCounter = 0x0;
    uint32_t NbrOfPage;
    volatile FLASH_Status FLASHStatus = FLASH_COMPLETE;

    FLASH_UnlockBank1();
    /* Define the number of page to be erased */
    NbrOfPage = (BANK1_WRITE_END_ADDR - BANK1_WRITE_START_ADDR) / FLASH_PAGE_SIZE;

    /* Clear All pending flags */
    FLASH_ClearFlag(FLASH_FLAG_EOP | FLASH_FLAG_PGERR | FLASH_FLAG_WRPRTERR);

    /* Erase the FLASH pages */
    for(EraseCounter = 0; (EraseCounter < NbrOfPage) && (FLASHStatus == FLASH_COMPLETE); EraseCounter++)
    {
        FLASHStatus = FLASH_ErasePage(BANK1_WRITE_START_ADDR + (FLASH_PAGE_SIZE * EraseCounter));
    }
    FLASH_LockBank1();
}
void msgPrintOut(u8 * pBuf,u16 len)
{
    UartSendSomeBytes(USART1, pBuf, len);
}
int receveGoingInData(u8 byte)
{
#if DEBUG_MSG_CLASS0
    msgPrintOut (&byte, 1);
#endif
    if(0 == g_msgCtrl.MsgFlag)
    {
        if(byte == MSG_GCAD_ID0 && 0 == g_msgCtrl.count)
        {
            g_msgCtrl.count ++;
            msgBuf[g_msgCtrl.msgBufLine][0] = byte;
        }
        else if(byte == MSG_GCAD_ID1 && 1 == g_msgCtrl.count)
        {
            g_msgCtrl.count ++;
            g_msgCtrl.MsgFlag = 1;
            msgBuf[g_msgCtrl.msgBufLine][1] = byte;
        }
        else if(byte == MSG_GCAD_ID0 && 1 == g_msgCtrl.count)
        {
            msgBuf[g_msgCtrl.msgBufLine][0] = byte;
        }
        else
        {
             g_msgCtrl.count = 0;
        }
    }
    else
    {
        if(g_msgCtrl.count > (MSG_BUF_LEN_MAX-2))
        {
             msgBuf[g_msgCtrl.msgBufLine][0] = 0;
             g_msgCtrl.MsgFlag = 0;
             g_msgCtrl.count = 0;
             return -1;
        }
        msgBuf[g_msgCtrl.msgBufLine][g_msgCtrl.count ++] = byte;

        if(byte == '\n' && '\r' == msgBuf[g_msgCtrl.msgBufLine][g_msgCtrl.count-2] && g_msgCtrl.count >= MSG_LEN_MIN )
        {
#if DEBUG_MSG_CLASS1
              msgPrintOut(msgBuf[g_msgCtrl.msgBufLine], g_msgCtrl.count);
#endif
              msgBuf[g_msgCtrl.msgBufLine][g_msgCtrl.count] = 0;
              msgBufSize[g_msgCtrl.msgBufLine] = g_msgCtrl.count;
              g_msgCtrl.MsgFlag = 0;
              g_msgCtrl.count = 0;
              g_msgCtrl.msgBufLine ++;
              if(g_msgCtrl.msgBufLine==MSG_BUF_LINE_MAX)
              {
                     g_msgCtrl.msgBufLine = 0;
              }
        }
    }
    return 0;
}

u8 calculatedVerify(u8* pBuf, u16 len)
{
    u8 msgVrf = pBuf[0];
    u16 i=1;
    while(i<len)
    {
        msgVrf ^= pBuf[i++];
    }
    return msgVrf;
}
u16 checkVerifyData(u8* pBuf,u16 size)
{
   u16 msgLen = 0;
   if(size < MSG_LEN_MIN)
   {
        return 0;
   }
   msgLen = (0x0ff&(pBuf[10]<<8)) | pBuf[11];
   if (size != (msgLen+MSG_HEADER_LEN+2))
   {
        printf("size err,total len %d,msgLen %d\r\n", size,msgLen);
        return 0;
   }
#if !DEBUG_CLOSE_VERIFY
   u8 msgVrf;
   msgVrf = calculatedVerify(pBuf+MSG_HEADER_LEN, msgLen);
   if(msgVrf != pBuf[size-3])
   {
        return 0;
   }
#endif
   return msgLen;
}

int checkLostData(u32 msgG,u32 msgS)
{
     g_msgG ++;
     if(g_msgG == msgS)
     {
           return 0;
     }
     return -1;
}

int recMsgUsrInfoInit(u8* pBuf, u16 size)
{
    if(size != 20)
    {
        printf("error:size is %d\r\n",size);
        return -1;
    }
    if(size == 20 && (g_usrInfo.phoneNum[0] == 0 || g_usrInfo.phoneNum[0] == 0xff))
    {
        strncpy((char *)&g_usrInfo.phoneNum,(char *)pBuf,11);
        strncpy((char *)&g_usrInfo.passwad,(char *)(pBuf+11),6);
        usrInfoSaveToFlase(&g_usrInfo);
    }
    else
    {
        printf("error: maybe have init\r\n");
        return -2;
    }
    return 0;
}

int recMsgUsrLogin(u8* pBuf, u16 size)
{
    int ret = 0;
    if(size != 20)
    {
        printf("error:size is %d\r\n",size);
        return -1;
    }

    if(0 == strncmp((char *)pBuf,(char *)&g_usrInfo.phoneNum,11))
    {
          if(0 == strncmp((char *)(pBuf+11),(char *)&g_usrInfo.passwad,6))
          {
               g_usrLoginFlag = 1;
               ret = 0;
          }
          else
          {
            printf("passwad is err\r\n");
            ret = -2;
          }
    }
    else
    {
        printf("usr phone number is err\r\n");
        ret = -3;
    }

    return ret;
}

int recMsgUsrPasswadModify(u8* pBuf, u16 size)
{
    int ret;
    if(size != 26)
    {
        printf("error:size is %d\r\n",size);
        return -1;
    }
    if(0 == strncmp((char *)pBuf,(char *)&g_usrInfo.phoneNum,11))
    {
          if(0 == strncmp((char *)(pBuf+11),(char *)&g_usrInfo.passwad,6))
          {
               strncpy((char *)&g_usrInfo.passwad,(char *)(pBuf+17),6);
               usrInfoSaveToFlase(&g_usrInfo);
               ret = 0;
          }
          else
          {
            printf("passwad is err\r\n");
            ret = -2;
          }

    }
    else
    {
        printf("usr phone number is err\r\n");
        ret = -3;
    }

    return ret;
}
int recMsgDelUsrInfo(u8* pBuf, u16 size)
{
    int ret = 0;

    if(size == 3 && g_usrLoginFlag==1 )
    {
        g_usrLoginFlag = 0;
        memset(&g_usrInfo, 0, sizeof(g_usrInfo));
        usrInfoFlashErase();
    }
    else
    {
           ret = -1;
    }
    return ret;
}
int recMsgDeviceUnlink(u8* pBuf, u16 size)
{
    int ret = 0;
    if(size != 3)
    {
        ret = -1;
    }
    else
    {
        g_usrLoginFlag = 0;
    }
    return ret;
}
int recMsgNeedRetransMsg(u8* pBuf, u16 size)
{
    return 0;
}
int toPrdDataProcess(u8* pBuf, u16 size)
{
    u32 msgG;
    u32 msgS;
    u16 msgLen = 0;
    u16 msgCmd;
    int ret  = 0;
    msgLen = checkVerifyData(pBuf, size);
    if(0 == msgLen)
    {
        pBuf[0] = 0;
        return -1;
    }

    msgG = (pBuf[2]<<24)|(pBuf[3]<<16)|(pBuf[4]<<16)|(pBuf[5]);
    msgS = (pBuf[6]<<24)|(pBuf[7]<<16)|(pBuf[8]<<16)|(pBuf[9]);
    (void)checkLostData(msgG, msgS);
    msgCmd = (0xff00&(pBuf[12]<<8)) | pBuf[13];
    if((msgCmd > 1)  &&  g_usrLoginFlag == 0)
    {
         sendMsgNeedLogin();
    }
    switch(msgCmd)
    {
         case 0x0000:
               ret = recMsgUsrInfoInit(pBuf+MSG_HEADTOBADY_LEN, msgLen);
             (0 == ret) ? sendMsgInitSucc() : sendMsgInitFail();
               break;
         case 0x0001:
                ret = recMsgUsrLogin(pBuf+MSG_HEADTOBADY_LEN, msgLen);
             (0 == ret) ? sendMsgLoginSucc() : sendMsgLoginFail();
                break;
         case 0x0002:
               recMsgUsrPasswadModify(pBuf+MSG_HEADTOBADY_LEN, msgLen);
               (0 == ret) ? sendMsgPwdModifySucc() : sendMsgPwdModifyFail();
               break;
          case 0x0003:
               ret = recMsgDelUsrInfo(pBuf+MSG_HEADTOBADY_LEN, msgLen);
            (0 == ret) ?  sendMsgUnlinkSucc(): sendMsgUnlinkFail();
               break;
         case 0x0004:
               ret = recMsgDeviceUnlink(pBuf+MSG_HEADTOBADY_LEN, msgLen);
               if(0 == ret) { sendMsgNeedLogin(); };
               break;
         case 0x00AB: sendMsgDataOneStep(25.24,45.345,86.472); break;
         default:break;
    }
    if(ret != 0)
    printf("\r\nmsgG %d msgS %d msgCmd %04x, msgLen %d,err %d\r\n",msgG, msgS,msgCmd,msgLen,ret);
    return ret;
}
void checkRecMsgFromBuf(void)
{
    while(g_msgCtrl.get != g_msgCtrl.msgBufLine)
    {
#if DEBUG_MSG_CLASS2
        msgPrintOut(msgBuf[g_msgCtrl.get],msgBufSize[g_msgCtrl.get]);
#else
        toPrdDataProcess(msgBuf[g_msgCtrl.get],msgBufSize[g_msgCtrl.get]);
#endif

       g_msgCtrl.get ++;
       if(g_msgCtrl.get == MSG_BUF_LINE_MAX)
       {
           g_msgCtrl.get = 0;
       }
    }
}


u8* getSendMsgSavePtr(void)
{
     u8* ptr;

     ptr =  g_sendMsgQueue.sendBuf[g_sendMsgQueue.put++];
     if(g_sendMsgQueue.put == SEND_MSG_BUF_LINE)
     {
          g_sendMsgQueue.put = 0;
     }
     return ptr;
}
void packageSendMsgHeader(u8* ptr)
{
    ++ g_msgS;
    ptr[0] = MSG_PRD_ID0;
    ptr[1] = MSG_PRD_ID1;
    ptr[2] = (u8)(0x0ff & (g_msgG >> 24));
    ptr[3] = (u8)(0x0ff & (g_msgG >>16 ));
    ptr[4] = (u8)(0x0ff & (g_msgG >>8 ));
    ptr[5] = (u8)(0x0ff &  g_msgG);
    ptr[6] = (u8)(0x0ff & (g_msgS >> 24));
    ptr[7] = (u8)(0x0ff & (g_msgS >> 16));
    ptr[8] = (u8)(0x0ff & (g_msgS >> 8));
    ptr[9] = (u8)(0x0ff &  g_msgS);
}
void packageSendMsgTail(u8* ptrBody, u16 bodysize)
{
    ptrBody[bodysize-1] = calculatedVerify(ptrBody, bodysize-1);
    ptrBody[bodysize]   = '\r';
    ptrBody[bodysize+1] = '\n';
}
void floatTochar(u8* ptr,double x)
{
    u32 a;
    a = (u32)x;
    ptr[0] = (u8) (0x0ff&(a>>24));
    ptr[1] = (u8) (0x0ff&(a>>16));
    ptr[2] = (u8) (0x0ff&(a>>8));
    ptr[3] = (u8) (0x0ff&a);

    a =(u32) (x*10000.0);
    a = a%10000;
    ptr[4] = (u8) (0x0ff&(a>>24));
    ptr[5] = (u8) (0x0ff&(a>>16));
    ptr[6] = (u8) (0x0ff&(a>>8));
    ptr[7] = (u8) (0x0ff&a);
}
int packageSendMsgNoBodyData(u16 msgCmd)
{
    u8* ptrBuf;
    ptrBuf = getSendMsgSavePtr();
    ptrBuf[0] =  MSG_HEADER_LEN + 5;
    ptrBuf ++;
    packageSendMsgHeader(ptrBuf);
    ptrBuf[10] = 0x00;
    ptrBuf[11] = 0x03;
    ptrBuf[12] = (u8)(0x0ff & (msgCmd >> 8));
    ptrBuf[13] = (u8)(0x0ff & msgCmd);
    packageSendMsgTail(ptrBuf+MSG_HEADER_LEN, 3);
    return 0;
}
int sendMsgSensorFault(void)
{
    u8* ptrBuf;
    u16 bodylen = 5;
    ptrBuf = getSendMsgSavePtr();
    ptrBuf[0] =  MSG_HEADER_LEN + bodylen + 2;
    ptrBuf ++;
    packageSendMsgHeader(ptrBuf);
    ptrBuf[10] = (u8)(0x0ff & (bodylen >> 8));
    ptrBuf[11] = (u8)(0x0ff & bodylen );
    ptrBuf[12] = 0x00;
    ptrBuf[13] = 0xAC;
    ptrBuf[14] = errCodeInfrared;
    ptrBuf[15] = errCodeMpu;
    packageSendMsgTail(ptrBuf+MSG_HEADER_LEN, bodylen);
    return 0;
}
int sendMsgDataAll(void)
{
    u8* ptrBuf;
    u16 bodylen = 27;
    ptrBuf = getSendMsgSavePtr();
    packageSendMsgHeader(ptrBuf);
    ptrBuf[10] = (u8)(0x0ff & (bodylen >> 8));
    ptrBuf[11] = (u8)(0x0ff & bodylen );
    ptrBuf[12] = 0x00;
    ptrBuf[13] = 0x2A;
    floatTochar(&ptrBuf[14],36.24);
    floatTochar(&ptrBuf[22],32.82);
    floatTochar(&ptrBuf[30],52.974);
      packageSendMsgTail(ptrBuf+MSG_HEADER_LEN, bodylen);
    return 0;
}

int sendMsgDataOneStep(float x,float y,float z)
{
    u8* ptrBuf;
    u16 bodylen = 27;
    ptrBuf = getSendMsgSavePtr();
    ptrBuf[0] =  MSG_HEADER_LEN + bodylen + 2;
    ptrBuf ++;
    packageSendMsgHeader(ptrBuf);
    ptrBuf[10] = (u8)(0x0ff & (bodylen >> 8));
    ptrBuf[11] = (u8)(0x0ff & bodylen );
    ptrBuf[12] = 0x00;
    ptrBuf[13] = 0x2D;
    floatTochar(&ptrBuf[14],x);
    floatTochar(&ptrBuf[22],y);
    floatTochar(&ptrBuf[30],z);
       packageSendMsgTail(ptrBuf+MSG_HEADER_LEN, bodylen);
    return 0;
}
int sendMsgInitSucc(void)
{
    packageSendMsgNoBodyData(0x00A1);
    return 0;
}
int sendMsgInitFail(void)
{
    packageSendMsgNoBodyData(0x00A2);
    return 0;
}

int sendMsgNeedLogin(void)
{
    packageSendMsgNoBodyData(0x00A3);
    return 0;
}
int sendMsgLoginSucc(void)
{
    packageSendMsgNoBodyData(0x00A4);
    if(errCodeMpu != 0 || errCodeInfrared != 0)
    {
        sendMsgSensorFault();
    }
    return 0;
}
int sendMsgLoginFail(void)
{
    packageSendMsgNoBodyData(0x00A5);
    return 0;
}

int sendMsgPwdModifySucc(void)
{
    packageSendMsgNoBodyData(0x00A6);
    return 0;
}
int sendMsgPwdModifyFail(void)
{
     packageSendMsgNoBodyData(0x00A7);
     return 0;
}
int sendMsgUnlinkSucc(void)
{
    packageSendMsgNoBodyData(0x00A8);
    return 0;
}
int sendMsgUnlinkFail(void)
{
    packageSendMsgNoBodyData(0x00A9);
    return 0;
}
int sendMsgLossPosition(void)
{
/*  u8* ptrBuf;
    u16 bodylen = 3;
    ptrBuf = getSendMsgSavePtr();
    packageSendMsgHeader(ptrBuf);
    ptrBuf[10] = (u8)(0x0ff & (bodylen >> 8));
    ptrBuf[11] = (u8)(0x0ff & bodylen );
    ptrBuf[12] = 0x00;
    ptrBuf[13] = 0xAA;
    packageSendMsgTail(ptrBuf+MSG_HEADER_LEN, bodylen);  */
     return 0;
}
int sendMsgLossData(void)
{
    u8  len,get;
    u8* ptr;
    if(g_sendMsgQueue.get==0)
         get = SEND_MSG_BUF_LINE - 1;
    else
         get =  g_sendMsgQueue.get - 1;
    ptr = g_sendMsgQueue.sendBuf[get];
    len = ptr[0];
#if DEBUG_MSG_SEND
    printf("sedmsg put %d,get %d,len %d\r\n",g_sendMsgQueue.put,g_sendMsgQueue.get,len);
#endif
    if(len !=0 && ptr[1]== 0x02)
    {
        UartSendSomeBytes(USART2,ptr+1, len);
    }
    else
    {
        printf("sent loss data failure\r\n");
        return -1;
    }
     return 0;
}
int sendMsgtoGCAD(void)
{
    u8  len;
    u8* ptr;
     while(g_sendMsgQueue.put != g_sendMsgQueue.get)
     {

        ptr = g_sendMsgQueue.sendBuf[g_sendMsgQueue.get];
        len = ptr[0];
#if DEBUG_MSG_SEND
        printf("sedmsg put %d,get %d,len %d\r\n",g_sendMsgQueue.put,g_sendMsgQueue.get,len);
#endif
        UartSendSomeBytes(USART2,ptr+1, len);
        g_sendMsgQueue.get ++;
        if(g_sendMsgQueue.get == SEND_MSG_BUF_LINE)
        {
             g_sendMsgQueue.get = 0;
        }
     }
     return 0;
}
