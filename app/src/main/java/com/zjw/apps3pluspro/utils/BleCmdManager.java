package com.zjw.apps3pluspro.utils;

public class BleCmdManager {
    private static BleCmdManager bleCmdManager;

    public static BleCmdManager getInstance() {
        if (bleCmdManager == null) {
            bleCmdManager = new BleCmdManager();
        }
        return bleCmdManager;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public byte[] appStartCmd(int pack) {
        byte[] valueByte = new byte[6];
        valueByte[0] = (byte) 0;
        valueByte[1] = (byte) 0;
        valueByte[2] = (byte) 0;
        valueByte[3] = (byte) 0;
        valueByte[4] = (byte) pack;
        valueByte[5] = (byte) 0;
        return valueByte;
    }

    public byte[] isBindDevice() {
        byte[] valueByte = new byte[6];
        valueByte[0] = (byte) 1;
        valueByte[1] = (byte) 0;
        valueByte[2] = (byte) 8;
        valueByte[3] = (byte) 1;
        valueByte[4] = (byte) 1;
        valueByte[5] = (byte) 0;
        return valueByte;
    }

    public byte[] deviceStartCmd() {
        byte[] valueByte = new byte[6];
        valueByte[0] = (byte) 0;
        valueByte[1] = (byte) 0;
        valueByte[2] = (byte) 1;
        valueByte[3] = (byte) 1;
        valueByte[4] = (byte) 0;
        valueByte[5] = (byte) 0;
        return valueByte;
    }

    public byte[] appConfirm() {
        byte[] valueByte = new byte[6];
        valueByte[0] = (byte) 0;
        valueByte[1] = (byte) 0;
        valueByte[2] = (byte) 1;
        valueByte[3] = (byte) 0;
        valueByte[4] = (byte) 0;
        valueByte[5] = (byte) 0;
        return valueByte;
    }

    public byte[] sendBindKey() {
        String strCmd[];
        strCmd = "01-00-08-01-10-01-1A-1C-12-1A-34-70-48-61-46-62-67-70-50-47-4E-78-7A-6F-36-57-5F-68-65-61-6C-74-68-61-70-70".split("-");
        byte[] valueByte = new byte[strCmd.length];
        for (int i = 0; i < strCmd.length; i++) {
            valueByte[i] = (byte) Integer.parseInt(strCmd[i], 16);
        }
        return valueByte;
    }

    public byte[] sendUserId() {
        String strCmd[];
        strCmd = "01-00-08-01-10-02-1A-31-2A-2F-0A-0A-31-30-35-37-31-39-30-36-32-37-12-18-36-45-30-37-41-35-32-43-32-33-44-42-38-38-46-30-42-32-42-44-41-31-31-46-1A-07-08-00-15-00-00-E8-41".split("-");
        byte[] valueByte = new byte[strCmd.length];
        for (int i = 0; i < strCmd.length; i++) {
            valueByte[i] = (byte) Integer.parseInt(strCmd[i], 16);
        }
        return valueByte;
    }



    public byte[] sendThemePiece(int index, int curPiece) {
        byte[] valueByte1;
        if (index == 1) {
            valueByte1 = new byte[6];
            valueByte1[0] = (byte) index;
            valueByte1[1] = (byte) 0;
            valueByte1[2] = (byte) (ThemeManager.getInstance().dataPackTotalPieceLength & 0xFF);
            valueByte1[3] = (byte) ((ThemeManager.getInstance().dataPackTotalPieceLength >> 8) & 0xFF);
            valueByte1[4] = (byte) (curPiece & 0xFF);
            valueByte1[5] = (byte) ((curPiece >> 8) & 0xFF);
        } else {
            valueByte1 = new byte[2];
            valueByte1[0] = (byte) index;
            valueByte1[1] = (byte) 0;
        }
        byte[] valueByte;
        int srcPos;
        if (curPiece < ThemeManager.getInstance().dataPackTotalPieceLength) {
            if (index == 1) {
                valueByte = new byte[ThemeManager.getInstance().onePackMaxDataLength];
                srcPos = (curPiece - 1) * ThemeManager.getInstance().dataPieceTotalByteLength;
                System.arraycopy(ThemeManager.getInstance().dataByte, srcPos, valueByte, 0, valueByte.length);
            } else {
                valueByte = new byte[ThemeManager.getInstance().onePackMaxDataLength + 4];
                srcPos = (curPiece - 1) * ThemeManager.getInstance().dataPieceTotalByteLength + ThemeManager.getInstance().onePackMaxDataLength + (index - 2) * (ThemeManager.getInstance().onePackMaxDataLength + 4);
                System.arraycopy(ThemeManager.getInstance().dataByte, srcPos, valueByte, 0, valueByte.length);
            }
        } else {
            if (index == 1) {
                int remainByteLength = ThemeManager.getInstance().dataByteLength - (curPiece - 1) * ThemeManager.getInstance().dataPieceTotalByteLength;
                if (ThemeManager.getInstance().dataPieceEndPack == index) {
                    valueByte = new byte[remainByteLength];
                } else {
                    valueByte = new byte[ThemeManager.getInstance().onePackMaxDataLength];
                }
                srcPos = (curPiece - 1) * ThemeManager.getInstance().dataPieceTotalByteLength;
                System.arraycopy(ThemeManager.getInstance().dataByte, srcPos, valueByte, 0, valueByte.length);
            } else {
                srcPos = (curPiece - 1) * ThemeManager.getInstance().dataPieceTotalByteLength + ThemeManager.getInstance().onePackMaxDataLength + (index - 2) * (ThemeManager.getInstance().onePackMaxDataLength + 4);
                int remainByteLength = ThemeManager.getInstance().dataByteLength - srcPos;
                if (ThemeManager.getInstance().dataPieceEndPack == index) {
                    valueByte = new byte[remainByteLength];
                } else {
                    valueByte = new byte[ThemeManager.getInstance().onePackMaxDataLength + 4];
                }
                System.arraycopy(ThemeManager.getInstance().dataByte, srcPos, valueByte, 0, valueByte.length);
            }
        }

        byte[] byte_3 = new byte[valueByte1.length + valueByte.length];
        System.arraycopy(valueByte1, 0, byte_3, 0, valueByte1.length);
        System.arraycopy(valueByte, 0, byte_3, valueByte1.length, valueByte.length);
        return byte_3;

    }

    public void aaa() {
//        String cmd = "08 02 10 01 22 08 12 06 0A 04 08 16 10 00";
//        String[] strCmd = cmd.split(" ");
//        byte[] valueByte = new byte[strCmd.length];
//        for (int i = 0; i < strCmd.length; i++) {
//            valueByte[i] = (byte) Integer.parseInt(strCmd[i], 16);
//        }
//        String analysisProtoData = AnalysisProtoData.getInstance().analysisData(valueByte);
//        Log.i("sss", analysisProtoData + "");
    }

}
