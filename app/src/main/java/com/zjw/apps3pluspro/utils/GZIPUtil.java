package com.zjw.apps3pluspro.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIPUtil {

    /**
     * 对byte[]进行GZIP解压
     *
     * @return 压缩后的数据
     */
    public static byte[] UnCompress(byte[] data) {
        GZIPInputStream gzip = null;
        ByteArrayInputStream bais = null;
        ByteArrayOutputStream baos = null;
        byte[] newData = null;

        try {
            baos = new ByteArrayOutputStream();
            bais = new ByteArrayInputStream(data);
            gzip = new GZIPInputStream(bais);
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = gzip.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }

            newData = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                gzip.close();
                baos.close();
                bais.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return newData;
    }

    /**
     * 对byte[]进行GZIP压缩
     *
     * @return 压缩后的数据
     */
    public static byte[] Compress(byte[] data) {
        GZIPOutputStream gzip = null;
        ByteArrayOutputStream baos = null;
        byte[] newData = null;

        try {
            baos = new ByteArrayOutputStream();
            gzip = new GZIPOutputStream(baos);

            gzip.write(data);
            gzip.flush();
            gzip.finish();

            newData = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                gzip.close();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return newData;
    }


    /***
     * 解压GZip
     *
     * @param data
     * @return
     */
    public static byte[] MyunGZip(byte[] data) {
        byte[] b = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            GZIPInputStream gzip = new GZIPInputStream(bis);
            byte[] buf = new byte[1024];
            int num = -1;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((num = gzip.read(buf, 0, buf.length)) != -1) {
                baos.write(buf, 0, num);
            }
            b = baos.toByteArray();
            baos.flush();
            baos.close();
            gzip.close();
            bis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }
}
