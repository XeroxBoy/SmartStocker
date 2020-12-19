package com.ths.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ths.dao.StockDfcfFundFlowInfoMapper;
import com.ths.domain.StockDfcfFundFlowInfo;
import com.ths.util.HttpClientUtils;
import com.ths.util.HttpUtils;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class DFCFFundFlowCrawlerJob {
    private final static Logger LOGGER = LoggerFactory.getLogger(DFCFFundFlowCrawlerJob.class);
    // 股票数据类型
    private static LinkedHashMap<String, String> STOCK_TYPE_MAP = new LinkedHashMap<>();
    private static LinkedHashMap<String, String> STOCK_TIME_MAP = new LinkedHashMap<>();

    @Autowired
    private StockDfcfFundFlowInfoMapper stockDfcfFundFlowInfoMapper;


    @PostConstruct
    private void initConstruct() {
//         http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?type=ct&st=(BalFlowMain)&sr=-1&p=1&ps=50&js=var%20rvrYpRDw={pages:(pc),date:%222014-10-22%22,data:[(x)]}&token=894050c76af8597a853f5b408b759f5d&cmd=C._AB&sty=DCFFITA&rt=50882971
        // TODO cmd=C._AB
//        STOCK_TYPE_MAP.put("全部股票", "C._AB");
        STOCK_TYPE_MAP.put("沪深A股", "C._A");
//        STOCK_TYPE_MAP.put("沪市A股", "C.2");
//        STOCK_TYPE_MAP.put("深市A股", "C._SZAME");
//        STOCK_TYPE_MAP.put("创业板", "C.80");
//        STOCK_TYPE_MAP.put("中小版", "C.13");
//        STOCK_TYPE_MAP.put("沪市B股", "C.3");
//        STOCK_TYPE_MAP.put("深市B股", "C.7");
        // TODO 排行榜       注：此处东方财富反爬虫放毒，3、5、10日排行的数据返回中，股票顺序是正确的，其他数据却是当日排行的数据
        STOCK_TIME_MAP.put("今日排行", "(BalFlowMain)");
//        STOCK_TIME_MAP.put("3日排行", "(BalFlowMainNet3)");
//        STOCK_TIME_MAP.put("5日排行", "(BalFlowMainNet5)");
//        STOCK_TIME_MAP.put("10日排行", "(BalFlowMainNet10)");
    }

    //    private final static String TOKEN_OLD = "894050c76af8597a853f5b408b759f5d";
//    private final static String sty = "DCFFITA";
//    private final static String type = "ct";
//    private final static String st = "(BalFlowMain)";
//    private final static String sr = "-1";
//    private final static String ps = "50";
    private static String JS_NAME = "var {{jsName.DATA}}={pages:(pc),date:\"2014-10-22\",data:[(x)]}";


    //    @Value("${nufm.dfcf.fund.flow.url}")
    private String dfcfFundFlowUrl = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx";

//    @Scheduled(cron = "0 0 8-23 * * ?")
    @Scheduled(cron = "0/10 * * * * ?")
    public void execute() {
//        String thsFengHongSwitch = System.getProperty("dfcf.fund.flow.switch");
//        if (!"true".equals(thsFengHongSwitch)) {
//            return;
//        }
        Set<String> set = STOCK_TYPE_MAP.keySet();
        Set<String> timeSet = STOCK_TIME_MAP.keySet();
        // 当前发起抓取的抓取小时时间点 如：2018-05-20 17:00:00
        String formatDay = new SimpleDateFormat("yyyy-MM-dd HH:00:00").format(new Date());
        HttpClientUtils httpClientUtils = new HttpClientUtils();
        HttpGet HTMLHttpGet = HttpUtils.get("http://data.eastmoney.com/zjlx/detail.html");
        try {
            String html = httpClientUtils.executeWithResult(HTMLHttpGet);
            LOGGER.info("抓取东方财富的个股资金流的数据,请求抓取html, 返回的结果为[{}]", html);
        } catch (Exception e) {

        }
        String key = "沪深A股";
        String timeKey = "今日排行";
        // 循环抓取不同股市的数据
//        for (String key : set) {
        // 循环抓取不同排行榜的数据
//            for (String timeKey : timeSet) {
        try {
            String jsName = new String(JS_NAME).replace("{{jsName.DATA}}", randomJSCode());
            // 排行榜
            String stValue = STOCK_TIME_MAP.get(timeKey);
            // 股市
            String cmdValue = STOCK_TYPE_MAP.get(key);
            HttpGet httpGet = createDFCFHttpGet(jsName, stValue, cmdValue, 1, key, timeKey);
            String result = httpClientUtils.executeWithResult(httpGet, "utf-8");
            LOGGER.info("抓取东方财富的个股资金流的数据,请求抓取后,股市[{}]，排行榜[{}],[{}]页, 返回的结果为[{}]", key, timeKey, 1, result);
            String[] s1 = result.split("data:");
            String dataArr = s1[1].substring(0, s1[1].length() - 1);
            LOGGER.info("抓取东方财富的个股资金流的数据，解析返回数据,股市[{}]，排行榜[{}],[{}]页,解析返回的数据股票数值为[{}]", key, timeKey, 1, dataArr);
            String data1 = s1[0];
            int a1 = data1.indexOf("pages:");
            int a2 = data1.indexOf(",date");
            String page = data1.substring(a1 + 6, a2);
            LOGGER.info("抓取东方财富的个股资金流的数据，解析返回数据,股市[{}]，排行榜[{}],[{}]页, 解析返回的数据page值为[{}]", key, timeKey, 1, page);
            int pageInt = Integer.valueOf(page);
            if (pageInt > 1) {
                // TODO 循环抓取分页中其他页的数据
                int allPage = pageInt + 1;
                for (int i = 1; i < allPage; ++i) {
                    HttpGet httpGet2 = createDFCFHttpGet(jsName, stValue, cmdValue, i, key, timeKey);
                    try {
                        // 休眠个3秒
                        Thread.sleep(3 * 1000l);
                        String result2 = httpClientUtils.executeWithResult(httpGet2, "utf-8");
                        LOGGER.info("抓取东方财富的个股资金流的数据,请求抓取后,股市[{}]，排行榜[{}],[{}]页, 返回的结果为[{}]", key, timeKey, i, result2);
                        String[] dateS2 = result2.split("data:");
                        String dataArr2 = dateS2[1].substring(0, dateS2[1].length() - 1);
                        LOGGER.info("抓取东方财富的个股资金流的数据，解析返回数据,股市[{}]，排行榜[{}],[{}]页,解析返回的数据股票数值为[{}]", key, timeKey, 1, dataArr2);
                        JSONArray jsonArray = JSONArray.parseArray(dataArr2);
                        int size = jsonArray.size();
                        for (int k = 0; k < size; k++) {
                            // 存储数据
                            insertDfcfFundFlowInfoData(jsonArray, k, key, timeKey, formatDay, i);
                        }
                    } catch (Exception e) {
                        LOGGER.error("抓取东方财富的个股资金流的数据，解析返回数据,股市[{}]，排行榜[{}],[{}]页, 解析返回的数据page值为[{}],出现异常:", key, timeKey, 1, page, e);
                    }

                }
            }
        } catch (Exception e) {
            LOGGER.error("抓取东方财富的个股资金流的数据，请求抓取出现异常，异常为", e);
        } finally {
            httpClientUtils.close();
        }
//            }
//        }


    }

    private void insertDfcfFundFlowInfoData(JSONArray jsonArray, int k, String key, String timeKey, String formatDay, int stockPage) {
        try {
            String infoStr = (String) jsonArray.get(k);
            LOGGER.info("解析东方财富的个股资金流的数据,第[{}]个，当前数据为[{}]", k, infoStr);
            String[] infoStrings = infoStr.split(",");
            // 清洗数据将 "-" 转化为 "0"
            cleanInfoStringArr(infoStrings);
            String stockCode = infoStrings[1];
            String stockName = infoStrings[2];
            String stockTime = infoStrings[15];
            BigDecimal priceNew = new BigDecimal(infoStrings[3]).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal change = new BigDecimal(infoStrings[4]).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal mainNetInflowAmount = new BigDecimal(infoStrings[5]).setScale(4, BigDecimal.ROUND_HALF_UP);
            BigDecimal mainNetProportion = new BigDecimal(infoStrings[6]).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal superBigPartNetInFlowAmount = new BigDecimal(infoStrings[7]).setScale(4, BigDecimal.ROUND_HALF_UP);
            BigDecimal superBigPartNetProportion = new BigDecimal(infoStrings[8]).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal bigPartNetInFlowAmount = new BigDecimal(infoStrings[9]).setScale(4, BigDecimal.ROUND_HALF_UP);
            BigDecimal bigPartNetProportion = new BigDecimal(infoStrings[10]).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal middlePartNetInFlowAmount = new BigDecimal(infoStrings[11]).setScale(4, BigDecimal.ROUND_HALF_UP);
            BigDecimal middlePartNetProportion = new BigDecimal(infoStrings[12]).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal litterPartNetInFlowAmount = new BigDecimal(infoStrings[13]).setScale(4, BigDecimal.ROUND_HALF_UP);
            BigDecimal litterPartNetProportion = new BigDecimal(infoStrings[14]).setScale(2, BigDecimal.ROUND_HALF_UP);
            Date time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(infoStrings[15]);
            String timeVersion = key + "#" + timeKey + "#" + stockTime;
            String crawlerVersion = key + "#" + timeKey + "#" + formatDay;
            BigDecimal someInfo = new BigDecimal(0);
            if (infoStrings[16] != null) {
                someInfo = new BigDecimal(infoStrings[16]).setScale(4, BigDecimal.ROUND_HALF_UP);
            }
            // 创建VO
            StockDfcfFundFlowInfo dfcfPersonStocksDataVO = new StockDfcfFundFlowInfo();
            dfcfPersonStocksDataVO.setStockMarket(key);
            dfcfPersonStocksDataVO.setStockRank(timeKey);
            dfcfPersonStocksDataVO.setStockCode(stockCode);
            dfcfPersonStocksDataVO.setStockName(stockName);
            dfcfPersonStocksDataVO.setPriceNew(priceNew);
            dfcfPersonStocksDataVO.setStockChange(change);
            dfcfPersonStocksDataVO.setMainNetInflowAmount(mainNetInflowAmount);
            dfcfPersonStocksDataVO.setMainNetProportion(mainNetProportion);
            dfcfPersonStocksDataVO.setSuperBigPartNetInflowAmount(superBigPartNetInFlowAmount);
            dfcfPersonStocksDataVO.setSuperBigPartNetProportion(superBigPartNetProportion);
            dfcfPersonStocksDataVO.setBigPartNetInflowAmount(bigPartNetInFlowAmount);
            dfcfPersonStocksDataVO.setBigPartNetProportion(bigPartNetProportion);
            dfcfPersonStocksDataVO.setMiddlePartNetInflowAmount(middlePartNetInFlowAmount);
            dfcfPersonStocksDataVO.setMiddlePartNetProportion(middlePartNetProportion);
            dfcfPersonStocksDataVO.setLitterPartNetInflowAmount(litterPartNetInFlowAmount);
            dfcfPersonStocksDataVO.setLitterPartNetProportion(litterPartNetProportion);
            dfcfPersonStocksDataVO.setCountTime(time);
            dfcfPersonStocksDataVO.setStockPage(stockPage);
            dfcfPersonStocksDataVO.setSomeinfo(someInfo.toString());
            dfcfPersonStocksDataVO.setTimeVersion(timeVersion);
            dfcfPersonStocksDataVO.setCrawlerVersion(crawlerVersion);
            dfcfPersonStocksDataVO.setUpdateTime(new Date());
            List<StockDfcfFundFlowInfo> stockDfcfFundFlowInfoList = stockDfcfFundFlowInfoMapper.selectByTimeVersion(stockCode, timeVersion);
            if (CollectionUtils.isEmpty(stockDfcfFundFlowInfoList)) {
                dfcfPersonStocksDataVO.setCreateTime(new Date());
                int a = stockDfcfFundFlowInfoMapper.insertSelective(dfcfPersonStocksDataVO);
                LOGGER.info("插入东方财富的数据成功[{}]，数据为[{}]", a, JSON.toJSONString(dfcfPersonStocksDataVO));
            } else {
                dfcfPersonStocksDataVO.setId(stockDfcfFundFlowInfoList.get(0).getId());
                int a = stockDfcfFundFlowInfoMapper.updateByPrimaryKeySelective(dfcfPersonStocksDataVO);
                LOGGER.info("更新东方财富的数据成功[{}]，数据为[{}]", a, JSON.toJSONString(dfcfPersonStocksDataVO));
            }
        } catch (Exception e) {
            LOGGER.error("插入或更新东方财富的数据，出现异常{}", e);
        }
    }

    private void cleanInfoStringArr(String[] infoStrings) {
        for (int i = 0; i < infoStrings.length; ++i) {
            infoStrings[i] = infoStrings[i].equals("-") ? "0" : infoStrings[i];
        }
    }

    private HttpGet createDFCFHttpGet(String jsName, String stValue, String cmdValue, int i, String key, String timeKey) {
        String time = String.valueOf(System.currentTimeMillis() / 30000);
        // 生成随机的js name code
        String code = randomJSCode();
        String jsNameReal = jsName.replace("{{name.data}}", code);
        Map<String, Object> params = new HashMap<>();
        params.put("type", "ct");
        params.put("st", stValue);
        params.put("sr", "-1");
        params.put("p", String.valueOf(i));
        params.put("ps", "50");
        params.put("js", jsNameReal);
        params.put("token", "894050c76af8597a853f5b408b759f5d");
        params.put("cmd", cmdValue);
        params.put("sty", "DCFFITA");
        params.put("rt", time);
        LOGGER.info("抓取东方财富的个股资金流的数据，请求抓取之前,股市[{}]，排行榜[{}],[{}]页", key, timeKey, i);
        HttpGet httpGet = HttpUtils.get(dfcfFundFlowUrl, params);
        httpGet = addHeaddfcf(httpGet);
        return httpGet;
    }

    private String randomJSCode() {
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String[] codes = str.split("");
        int num = 8;
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < num; i++) {
            int a = random.nextInt(52);
            code.append(codes[a]);
        }
        return code.toString();
    }

    private HttpGet addHeaddfcf(HttpGet httpGet) {

        httpGet.addHeader("Accept", "*/*");
        httpGet.addHeader("Accept-Encoding", "gzip, deflate");
        httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
        httpGet.addHeader("Connection", "keep-alive");
        httpGet.addHeader("Host", "nufm.dfcfw.com");
        httpGet.addHeader("Referer", "http://data.eastmoney.com/zjlx/detail.html");
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36");

        return httpGet;
    }


}
