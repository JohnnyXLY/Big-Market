package org.example.infrastructure.persistent.dao;

import org.example.infrastructure.persistent.po.RaffleActivity;
import org.example.infrastructure.persistent.po.RaffleActivitySku;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品skuDAO
 */

@Mapper
public interface IRaffleActivitySkuDao {

    /**
     * 根据sku值查询抽奖活动sku持久化对象
     * @param sku 商品sku
     * @return 抽奖活动sku持久化对象
     */
    RaffleActivitySku queryActivitySku(Long sku);

}
