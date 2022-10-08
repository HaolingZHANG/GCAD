SET PATH=C:\Keil\ARM\BIN40\;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;d:\Cadence\SPB_16.3\tools\pcb\bin;d:\Cadence\SPB_16.3\tools\bin;d:\Cadence\SPB_16.3\tools\libutil\bin;d:\Cadence\SPB_16.3\tools\fet\bin;d:\Cadence\SPB_16.3\tools\specctra\bin;d:\Cadence\SPB_16.3\tools\PSpice;d:\Cadence\SPB_16.3\tools\PSpice\Library;d:\Cadence\SPB_16.3\tools\Capture;d:\Cadence\SPB_16.3\OpenAccess\bin\win32\opt
SET ARMCC41INC=C:\Keil\ARM\RV31\INC
SET ARMCC41LIB=C:\Keil\ARM\RV31\LIB
SET CPU_TYPE=STM32F103C8
SET CPU_VENDOR=STMicroelectronics
SET UV2_TARGET=Target 1
SET CPU_CLOCK=0x00000000
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\main.__i"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\stm32f10x_it.__i"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\serial.__i"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\sys_init.__i"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\delay.__i"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\infrared.__i"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\dataprotocol.__i"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\pitchrollyaw.__i"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\iic1_leftleg.__i"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\iic2_rightleg.__i"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\iic3_body.__i"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\iic4_foot.__i"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\prdsensordeal.__i"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\motionsta.__i"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\xyz_algo.__i"
"C:\Keil\ARM\BIN40\ArmAsm" --Via ".\obj\startup_stm32f10x_md._ia"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\core_cm3.__i"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\system_stm32f10x.__i"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\misc.__i"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\stm32f10x_adc.__i"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\stm32f10x_gpio.__i"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\stm32f10x_i2c.__i"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\stm32f10x_rcc.__i"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\stm32f10x_usart.__i"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\stm32f10x_dma.__i"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\stm32f10x_flash.__i"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\inv_mpu.__i"
"C:\Keil\ARM\BIN40\ArmCC" --Via ".\obj\inv_mpu_dmp_motion_driver.__i"
"C:\Keil\ARM\BIN40\ArmLink" --Via ".\obj\STM32F103C8T6.lnp"
C:\Keil\ARM\BIN40\fromelf.exe ".\obj\STM32F103C8T6.axf" --i32combined --output ".\obj\STM32F103C8T6.hex"
