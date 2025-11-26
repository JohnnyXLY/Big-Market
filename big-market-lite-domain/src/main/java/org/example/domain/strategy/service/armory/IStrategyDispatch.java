package org.example.domain.strategy.service.armory;

/**
 * 抽奖策略调度
 */

public interface IStrategyDispatch {
    /**
     * 获取抽奖策略装配的随机结果
     * @param strategyId 策略ID
     * @return 抽奖结果
     */
    Integer getRandomAwardId(Long strategyId);

    Integer getRandomAwardId(Long strategyId, String ruleWeightValue);

    /**
     * 库存扣减
     * @param strategyId 策略ID
     * @param awardId 奖品ID
     * @return 库存是否成功扣减
     */
    Boolean subtractionAwardStock(Long strategyId, Integer awardId);
}
