package org.example.domain.strategy.respository;

import org.example.domain.strategy.model.entity.StrategyAwardEntity;
import org.example.domain.strategy.model.entity.StrategyEntity;
import org.example.domain.strategy.model.entity.StrategyRuleEntity;
import org.example.domain.strategy.model.vo.RuleTreeVO;
import org.example.domain.strategy.model.vo.StrategyAwardRuleModelVO;
import org.example.domain.strategy.model.vo.StrategyAwardStockKeyVO;

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

    /**
     * 根据树的ID查询规则树对象
     * @param treeId 树ID
     * @return 规则树对象
     */
    RuleTreeVO queryRuleTreeVOByTreeId(String treeId);

    /**
     * 缓存奖品库存总量
     * @param cacheKey 缓存key(包含strategyId、awardId的信息)
     * @param awardCount 奖品库存总量
     */
    void cacheStrategyAwardCount(String cacheKey, Integer awardCount);

    /**
     * 库存扣减
     * @param cacheKey 缓存key
     * @return 库存是否扣减
     */
    Boolean subtractionAwardStock(String cacheKey);

    /**
     * 将奖品标识值对象写入奖品库存消费队列
     * @param strategyAwardStockKeyVO 策略奖品库存Key标识值对象
     */
    void awardStockConsumeSendQueue(StrategyAwardStockKeyVO strategyAwardStockKeyVO);

    /**
     * 从消费队列中获取对象
     * @return 策略奖品库存Key标识值对象
     */
    StrategyAwardStockKeyVO takeQueueValue();

    /**
     * 更新数据库库存信息
     * @param strategyId 策略ID
     * @param awardId 奖品ID
     */
    void updateStrategyAwardStock(Long strategyId, Integer awardId);
}
