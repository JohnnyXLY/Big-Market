package org.example.domain.activity.service;

import org.example.domain.activity.model.entity.ActivityOrderEntity;
import org.example.domain.activity.model.entity.ActivityShopCartEntity;

/**
 * 抽奖活动订单接口
 */

public interface IRaffleOrder {

    /**
     * 以sku创建抽奖活动订单，获得参与抽奖资格
     * @param activityShopCartEntity 抽奖活动购物车实体对象，包含sku
     * @return 活动订单实体对象
     */
    ActivityOrderEntity createRaffleActivityOrder(ActivityShopCartEntity activityShopCartEntity);

}
