package com.ths.dao;

import com.ths.domain.StockDfcfFundFlowInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockDfcfFundFlowInfoMapper {

    List<String> selectThreeDownFourUp(@Param(value = "oneDay") String oneDay,
                                       @Param(value = "twoDay") String twoDay,
                                       @Param(value = "threeDay") String threeDay,
                                       @Param(value = "fourDay") String fourDay);

    List<StockDfcfFundFlowInfo> selectByTimeVersion(@Param(value = "stockCode") String stockCode, @Param(value = "timeVersion") String timeVersion);

    String selectStockNameByCrawlerVersion(@Param(value = "stockCode") String stockCode, @Param(value = "timeVersion") String timeVersion);

    List<String> selectByCrawlerVersion(@Param(value = "crawlerVersion") String crawlerVersion);

    int insertSelective(StockDfcfFundFlowInfo record);

    StockDfcfFundFlowInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockDfcfFundFlowInfo record);

}