package org.example.domain.strategy.service.armory;

import org.example.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.List;

/**
 * 策略装配库（兵工厂），负责初始化策略计算
 */

public interface IStrategyArmory {
    /**
     * 装配抽奖策略配置「触发的时机可以为活动审核通过后进行调用」
     * @param strategyId
     * @return 装配结果
     */
    boolean assembleLotteryStrategy(Long strategyId);

    void assembleLotteryStrategy(String key, List<StrategyAwardEntity> strategyAwardEntities);
}
