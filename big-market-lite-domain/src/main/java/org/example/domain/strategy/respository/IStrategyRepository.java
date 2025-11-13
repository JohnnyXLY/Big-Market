package org.example.domain.strategy.respository;

import org.example.domain.strategy.model.entity.StrategyAwardEntity;
import org.example.domain.strategy.model.entity.StrategyEntity;
import org.example.domain.strategy.model.entity.StrategyRuleEntity;
import org.example.domain.strategy.model.vo.StrategyAwardRuleModelVO;

import java.util.List;
import java.util.Map;

/**
 * 策略仓储接口
 */

public interface IStrategyRepository {
    /**
     * 根据策略id返回策略对应的所有策略奖品实体对象(StrategyAward)
     * @param strategyId
     * @return
     */
    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    /**
     * 存储策略及策略对应的奖品范围（奖品数量）
     * 存储策略对应的奖品映射表
     * @param key
     * @param rateRange
     * @param shuffleStrategyAwardSearchRateTable
     */
    void storeStrategyAwardSearchRateTable(String key, Integer rateRange, Map<Integer, Integer> shuffleStrategyAwardSearchRateTable);

    /**
     * 获取当前策略对应的奖品数量
     * @param strategyId
     * @return
     */
    Integer getRateRange(Long strategyId);

    /**
     * 获取当前策略对应的奖品数量
     * @param key
     * @return
     */
    Integer getRateRange(String key);

    /**
     * 根据策略名和随机数查找奖品映射表，返回对应的奖品id
     * @param key
     * @param rateKey
     * @return
     */
    Integer getStrategyAwardAssemble(String key, int rateKey);

    /**
     * 根据策略id查询策略实体(StrategyEntity)
     * @param strategyId
     * @return
     */
    StrategyEntity queryStrategyEntityByStrategyId(Long strategyId);

    /**
     * 根据策略id和规则模型查询策略规则(StrategyRule)
     * @param strategyId
     * @param ruleModel
     * @return
     */
    StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleModel);

    /**
     * 通过策略id + 奖品id + 规则模型(有的奖品对应多个规则模型，因此需要指定) 查询规则值
     * @param strategyId
     * @param awardId
     * @param ruleModel
     * @return
     */
    String queryStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel);

    /**
     * 通过策略id + 规则模型 查询规则值
     * @param strategyId
     * @param ruleModel
     * @return
     */
    String queryStrategyRuleValue(Long strategyId, String ruleModel);

    /**
     * 通过策略id + 奖品id查询获奖模型值对象(StrategyAwardRuleModelVO)
     * @param strategyId
     * @param awardId
     * @return
     */
    StrategyAwardRuleModelVO queryStrategyAwardRuleModelVO(Long strategyId, Integer awardId);
}
