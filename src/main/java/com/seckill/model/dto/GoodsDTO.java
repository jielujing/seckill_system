package com.seckill.model.dto;

import com.seckill.model.entity.Goods;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Dell
 * @create 2019-07-21 0:48
 */
public class GoodsDTO extends Goods {
    private BigDecimal seckillPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;

    public Integer getStockCount() {
        return stockCount;
    }
    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }
    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public BigDecimal getSeckillPrice() {
        return seckillPrice;
    }
    public void setSeckillPrice(BigDecimal seckillPrice) {
        this.seckillPrice = seckillPrice;
    }

    @Override
    public String toString() {
        return "GoodsDTO{" +
                "seckillPrice=" + seckillPrice +
                ", stockCount=" + stockCount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
