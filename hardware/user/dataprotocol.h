#ifndef __DATAPROTOCOL_H
#define __DATAPROTOCOL_H

#ifdef __cplusplus
 extern "C" {
#endif

extern void msgCommunicateInit(void);
extern u8 calculatedVerify(u8* pBuf, u16 len);
extern u16 checkVerifyData(u8* pBuf,u16 size);
extern int checkLostData(u32 msgG,u32 msgS);
extern void msgPrintOut(u8 * pBuf,u16 len);
extern int receveGoingInData(u8 byte);
int recMsgUsrInfoInit(u8* pBuf, u16 size);
int recMsgUsrLogin(u8* pBuf, u16 size);
int recMsgUsrPasswadModify(u8* pBuf, u16 size);
int recMsgDeviceUnlink(u8* pBuf, u16 size);
int recMsgNeedRetransMsg(u8* pBuf, u16 size);
int sendMsgSensorFault(void);
int toPrdDataProcess(u8* pBuf, u16 size);
void checkRecMsgFromBuf(void);

int sendMsgDataAll(void);
int sendMsgDataOneStep(float x,float y,float z);
int sendMsgInitSucc(void);
int sendMsgInitFail(void);
int sendMsgNeedLogin(void);
int sendMsgLoginSucc(void);
int sendMsgLoginFail(void);
int sendMsgPwdModifySucc(void);
int sendMsgPwdModifyFail(void);
int sendMsgUnlinkSucc(void);
int sendMsgUnlinkFail(void);
int sendMsgLossPosition(void);
int sendMsgLossData(void);
int sendMsgtoGCAD(void);

#ifdef __cplusplus
}
#endif
#endif
