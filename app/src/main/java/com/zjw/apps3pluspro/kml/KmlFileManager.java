package com.zjw.apps3pluspro.kml;


import com.zjw.apps3pluspro.utils.SysUtils;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by android
 * on 2020/5/8.
 */
public class KmlFileManager {
    private static final String TAG = KmlFileManager.class.getSimpleName();

    private static KmlFileManager kmlFileManager;

    public static KmlFileManager getInstance() {
        if (kmlFileManager == null) {
            kmlFileManager = new KmlFileManager();
        }
        return kmlFileManager;
    }

    private KmlFileManager() {

    }

    private List<TrackPoint> tps = new ArrayList<>();

    public void addData(TrackPoint tp) {
        tps.add(tp);
    }

    public void clearData() {
        tps.clear();
    }

    public void createKml(String filePath) {
        SysUtils.logContentW(TAG, " kml created start =" + tps.size());

        Element root = DocumentHelper.createElement("kml");  //根节点是kml
        Document document = DocumentHelper.createDocument(root);
        document.setXMLEncoding("UTF-8");
        //给根节点kml添加属性
        root.addNamespace("xmlns", "http://www.opengis.net/kml/2.2");
        root.addNamespace("gx", "http://www.google.com/kml/ext/2.2");
        //给根节点kml添加子节点  Document
        Element documentElement = root.addElement("Document");
        Element folderDe = documentElement.addElement("Folder");
        Element PlacemarkDe = folderDe.addElement("Placemark");

        Element styleE = PlacemarkDe.addElement("Style");
        Element LineStyle = styleE.addElement("LineStyle");
        LineStyle.addElement("color").addText("ed0000ff");
        LineStyle.addElement("width").addText("5");

        Element trackE = PlacemarkDe.addElement("gx:Track");

        for (TrackPoint tp : tps) {
            trackE.addElement("gx:coord").addText(tp.getLongitude() + " " + tp.getLatitude() + " " + tp.getAltitude());
            trackE.addElement("when").setText(specialDateString(tp.getTime()));
        }
        try {
            Writer fileWriter = new FileWriter(filePath);
            //换行
            OutputFormat format = new OutputFormat();
            format.setEncoding("UTF-8");
            format.setNewlines(true);
            // 生成缩进
            format.setIndent(true);
            //dom4j提供了专门写入文件的对象XMLWriter
            XMLWriter xmlWriter = new XMLWriter(fileWriter, format);
            xmlWriter.write(document);
            xmlWriter.flush();
            xmlWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        clearData();
        SysUtils.logContentW(TAG, " kml created over");
    }
    public static String specialDateString(long time) {
        String date = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z' ");
        date = sdf.format(time);
        return date;
    }
}
