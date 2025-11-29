package org.example.domain.strategy.service;

import org.example.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.List;

/**
 * 策略奖品接口
 */

public interface IRaffleAward {

    /**
     * 根据策略ID查询奖品配置
     * @param strategyId 策略ID
     * @return 奖品实体列表
     */
    List<StrategyAwardEntity> queryRaffleStrategyAwardList(Long strategyId);

}
