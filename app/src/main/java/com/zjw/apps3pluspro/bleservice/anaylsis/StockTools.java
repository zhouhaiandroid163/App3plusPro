package com.zjw.apps3pluspro.bleservice.anaylsis;

import com.xiaomi.wear.protobuf.StockProtos;
import com.xiaomi.wear.protobuf.WearProtos;

public class StockTools {

    static final int StockInfo = 1; //存储信息
    static final int StockInfoList = 2; //存储信息 列表
    static final int symbol = 3; //符号？
    static final int StockSymbolList = 4; //存储符号？ 列表

    public static String analysisStock(WearProtos.WearPacket wear) {

        StockProtos.Stock stock = wear.getStock();
        int id = wear.getId();
        int pos = stock.getPayloadCase().getNumber();

        String result_str = "";

        System.out.println("数据封装 = " + "wear/type = " + wear.getType());
        System.out.println("数据封装 = " + "wear/id = " + wear.getId());
        System.out.println("数据封装 = " + "pos = " + pos);

        result_str += "wear/type = " + wear.getType() + "\n";
        result_str += "wear/id = " + wear.getId() + "\n";
        result_str += "pos = " + pos + "\n";


        switch (pos) {
            case StockInfo:
                System.out.println("数据封装 存储信息");
                result_str += "数据封装 存储信息" + "\n";

                StockProtos.StockInfo stock_info = stock.getStockInfo();

                System.out.println("数据封装 = " + "stock/" + "/stock_info===========");
                System.out.println("数据封装 = " + "stock/" + "/stock_info/symbol = " + stock_info.getSymbol());
                System.out.println("数据封装 = " + "stock/" + "/stock_info/market = " + stock_info.getMarket());
                System.out.println("数据封装 = " + "stock/" + "/stock_info/name = " + stock_info.getName());
                System.out.println("数据封装 = " + "stock/" + "/stock_info/latest_price = " + stock_info.getLatestPrice());
                System.out.println("数据封装 = " + "stock/" + "/stock_info/pre_close = " + stock_info.getPreClose());
                System.out.println("数据封装 = " + "stock/" + "/stock_info/halted = " + stock_info.getHalted());
                System.out.println("数据封装 = " + "stock/" + "/stock_info/timestamp = " + stock_info.getTimestamp());
                System.out.println("数据封装 = " + "stock/" + "/stock_info/delay_mintue = " + stock_info.getDelayMintue());

                result_str += "stock/" + "/stock_info===========" + "\n";
                result_str += "stock/" + "/stock_info/symbol = " + stock_info.getSymbol() + "\n";
                result_str += "stock/" + "/stock_info/market = " + stock_info.getMarket() + "\n";
                result_str += "stock/" + "/stock_info/name = " + stock_info.getName() + "\n";
                result_str += "stock/" + "/stock_info/latest_price = " + stock_info.getLatestPrice() + "\n";
                result_str += "stock/" + "/stock_info/pre_close = " + stock_info.getPreClose() + "\n";
                result_str += "stock/" + "/stock_info/halted = " + stock_info.getHalted() + "\n";
                result_str += "stock/" + "/stock_info/timestamp = " + stock_info.getTimestamp() + "\n";
                result_str += "stock/" + "/stock_info/delay_mintue = " + stock_info.getDelayMintue() + "\n";


                break;

            case StockInfoList:
                System.out.println("数据封装 存储信息 列表");
                result_str += "数据封装 存储信息 列表" + "\n";


                StockProtos.StockInfo.List stock_info_list = stock.getStockInfoList();


                for (int i = 0; i < stock_info_list.getListCount(); i++) {

//                    NotificationProtos.NotifyData stock_info_info = notify_list.getList(i);

                    StockProtos.StockInfo stock_info_info = stock_info_list.getList(i);

                    System.out.println("数据封装 = " + "stock/" + "/stock_info=========== i = " + i);
                    System.out.println("数据封装 = " + "stock/" + "/stock_info/symbol = " + stock_info_info.getSymbol());
                    System.out.println("数据封装 = " + "stock/" + "/stock_info/market = " + stock_info_info.getMarket());
                    System.out.println("数据封装 = " + "stock/" + "/stock_info/name = " + stock_info_info.getName());
                    System.out.println("数据封装 = " + "stock/" + "/stock_info/latest_price = " + stock_info_info.getLatestPrice());
                    System.out.println("数据封装 = " + "stock/" + "/stock_info/pre_close = " + stock_info_info.getPreClose());
                    System.out.println("数据封装 = " + "stock/" + "/stock_info/halted = " + stock_info_info.getHalted());
                    System.out.println("数据封装 = " + "stock/" + "/stock_info/timestamp = " + stock_info_info.getTimestamp());
                    System.out.println("数据封装 = " + "stock/" + "/stock_info/delay_mintue = " + stock_info_info.getDelayMintue());

                    result_str += "stock/" + "/stock_info=========== i = " + i + "\n";
                    result_str += "stock/" + "/stock_info/symbol = " + stock_info_info.getSymbol() + "\n";
                    result_str += "stock/" + "/stock_info/market = " + stock_info_info.getMarket() + "\n";
                    result_str += "stock/" + "/stock_info/name = " + stock_info_info.getName() + "\n";
                    result_str += "stock/" + "/stock_info/latest_price = " + stock_info_info.getLatestPrice() + "\n";
                    result_str += "stock/" + "/stock_info/pre_close = " + stock_info_info.getPreClose() + "\n";
                    result_str += "stock/" + "/stock_info/halted = " + stock_info_info.getHalted() + "\n";
                    result_str += "stock/" + "/stock_info/timestamp = " + stock_info_info.getTimestamp() + "\n";
                    result_str += "stock/" + "/stock_info/delay_mintue = " + stock_info_info.getDelayMintue() + "\n";


                }

                break;

            case symbol:
                System.out.println("数据封装 符号");

                result_str += "数据封装 符号" + "\n";

                System.out.println("数据封装 = " + "notiffication/" + "/symbol = " + stock.getSymbol());

                result_str += "notiffication/" + "/symbol = " + "\n";

                break;

            case StockSymbolList:
                System.out.println("数据封装 符号列表");
                StockProtos.StockSymbol.List stock_symbol_list = stock.getStockSymbolList();
                result_str += "数据封装 符号列表 = size = " + stock_symbol_list.getListCount() + "\n";



                for (int i = 0; i < stock_symbol_list.getListCount(); i++) {

                    StockProtos.StockSymbol stocksymbol_info = stock_symbol_list.getList(i);

                    System.out.println("数据封装 = " + "stock/" + "/stocksymbol_info=========== i = " + i);
                    System.out.println("数据封装 = " + "stock/" + "/notify_id/symbol = " + stocksymbol_info.getSymbol());
                    System.out.println("数据封装 = " + "stock/" + "/notify_id/is_widget = " + stocksymbol_info.getIsWidget());
                    System.out.println("数据封装 = " + "stock/" + "/notify_id/order = " + stocksymbol_info.getOrder());

                    result_str += "stock/" + "/notify_id=========== i = " + i + "\n";
                    result_str += "stock" + "/notify_id/symbol = " + stocksymbol_info.getSymbol() + "\n";
                    result_str += "stock" + "/notify_id/is_widget = " + stocksymbol_info.getIsWidget() + "\n";
                    result_str += "stock" + "/notify_id/order = " + stocksymbol_info.getOrder() + "\n";

                }

                break;


        }
        return result_str;

    }


}
