package com.ths.service;

import java.util.HashMap;
import java.util.List;


public interface ThsGnDetailCrawlService {

    void putAllArrayBlockingQueue(List<HashMap<String, String>> list);

    void ConsumeCrawlerGnDetailData(int threadNumber);

}
