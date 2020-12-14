package com.zjw.apps3pluspro.module.device.dfurtk;


public class RtkConstant {

    //=============================
    //OTA_UUID
    public static final String OTA_UUID = "0000d0ff-3c17-d293-8e48-14fe2e4da212";
    //aesKey
    public static final String AES_KEY = "4E46F8C5092B29E29A971A0CD1F610FB1F6763DF807A7E70960D4CD3118E601A";
    //获取文件后缀
    public static final String FILE_SUFFIX = "bin";

    public static final boolean IS_Upload_File_Prompt_Enabled = false;

    //=============================
    //是否已启用Dfu断点恢复
    public static final boolean IS_Dfu_Breakpoint_Resume_Enabled = true;

    //Dfu是否自动激活
    public static final boolean IS_Dfu_Automatic_Active_Enabled = true;

    //Dfu电池是否已检查
    public static final boolean IS_Dfu_Battery_CheckE_nabled = true;

    //获取Dfu低电池阈值
    public static final int Dfu_Low_Battery_Threshold = 30;

    //获取Dfu电池电量格式
    public static final int Dfu_Battery_Level_Format = 0;

    //是DFU版本检查
    public static final boolean IS_Dfu_Version_Check_Enabled = false;

    //是DFU芯片类型的检查
    public static final boolean IS_DfuChip_Type_Check_Enabled = true;

    //是否启用Dfu图像节大小检查
    public static final boolean IS_Dfu_Image_Section_Size_Check_Enabled = false;

    //是否启用Dfu吞吐量
    public static final boolean IS_Dfu_Throughput_Enabled = false;

    //MTU更新启用
    public static final boolean IS_Dfu_Mtu_Update_Enabled = false;

    //Dfu是否激活并重新设置Ack
    public static final boolean IS_Dfu_Active_And_Reset_Ack_Enabled = false;

    //是否启用了Dfu连接参数延迟
    public static final boolean IS_Dfu_Connection_Parameter_Latency_Enabled = false;

    //获取文件位置
    public static final int File_Location = 0;

    //是否已启用Dfu错误操作断开连接
    public static final boolean IS_Dfu_Error_Action_Disconnect_Enabled = true;

    //是否启用了Dfu错误操作刷新设备
    public static final boolean IS_DfuErrorActionRefreshDeviceEnabled = false;

    //Dfu是否已启用完全操作移除绑定
    public static final boolean IS_Dfu_Complete_Action_Remove_Bond_Enabled = false;

    //用于调试日志
    public static final boolean IS_Dfu_Debug_Enabled = true;

    //缓冲区检查级别
    public static final int Dfu_Buffer_Check_Level = 16;

    //获取Dfu速度控制级别
    public static final int Dfu_Speed_Control_Level = 0;

    //工作模式
    public static final boolean Work_Mode_Prompt_Enabled = false;


}
