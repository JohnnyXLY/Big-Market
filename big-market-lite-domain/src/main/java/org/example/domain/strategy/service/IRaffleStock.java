package org.example.domain.strategy.service;

import org.example.domain.strategy.model.vo.StrategyAwardStockKeyVO;

/**
 * 抽奖库存相关服务
 */

public interface IRaffleStock {

    /**
     * 从消费队列中获取对象
     * @return 策略奖品库存Key标识值对象
     * @throws InterruptedException
     */
    StrategyAwardStockKeyVO takeQueueValue() throws InterruptedException;

    /**
     * 更新数据库中奖品库存
     * @param strategyId 策略ID
     * @param awardId 奖品ID
     */
    void updateStrategyAwardStock(Long strategyId, Integer awardId);
}
