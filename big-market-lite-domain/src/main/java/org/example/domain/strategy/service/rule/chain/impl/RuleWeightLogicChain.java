package org.example.domain.strategy.service.rule.chain.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.respository.IStrategyRepository;
import org.example.domain.strategy.service.armory.IStrategyDispatch;
import org.example.domain.strategy.service.rule.chain.AbstractLogicChain;
import org.example.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import org.example.types.common.Constants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 权重抽奖责任链
 */

@Slf4j
@Component("rule_weight")
public class RuleWeightLogicChain extends AbstractLogicChain {

    @Resource
    private IStrategyRepository repository;

    @Resource
    private IStrategyDispatch strategyDispatch;

    // 用户积分，用于确定奖励区间
    public Long userScore = 0L;

    @Override
    protected String ruleModel() {
        return DefaultChainFactory.LogicModel.RULE_WEIGHT.getCode();
    }

    /**
     * 通过权重抽奖责任节点，获取奖品ID
     * 1. 符合权重抽奖条件，根据权重策略返回对应的奖品ID
     * 2. 不符合权重抽奖条件，执行默认责任节点的逻辑
     * @param userId 用户ID
     * @param strategyId 策略ID
     * @return 奖品ID
     */
    @Override
    public DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId) {
        log.info("抽奖责任链-权重开始 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());
        String ruleValue = repository.queryStrategyRuleValue(strategyId, ruleModel());

        // 获取积分区间和对应奖品的map映射
        Map<Long, String> analyticalValueMap = getAnalyticalValue(ruleValue);
        if (null == analyticalValueMap || analyticalValueMap.isEmpty()) {
            // 不符合权重抽奖条件，放行至默认责任节点
            log.info("抽奖责任链-权重放行 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());
            return next().logic(userId, strategyId);
        }

        // 符合抽奖条件
        // 对抽奖区间进行排序
        ArrayList<Long> analyticalSortedKeys = new ArrayList<>(analyticalValueMap.keySet());
        Collections.sort(analyticalSortedKeys);

        Long nextValue = analyticalSortedKeys.stream()
                .sorted(Comparator.reverseOrder())
                .filter(key -> userScore >= key)
                .findFirst()
                .orElse(null);

        if (null != nextValue) {
            Integer awardId = strategyDispatch.getRandomAwardId(strategyId, analyticalValueMap.get(nextValue));
            log.info("抽奖责任链-权重接管 userId: {} strategyId: {} ruleModel: {} awardId: {}", userId, strategyId, ruleModel(), awardId);
            return DefaultChainFactory.StrategyAwardVO.builder()
                    .awardId(awardId)
                    .logicModel(ruleModel())
                    .build();
        }

        // 继续过滤责任链
        log.info("抽奖责任链-权重放行 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());
        return next().logic(userId, strategyId);
    }

    /**
     * 处理规则值(ruleValue)字符串，构建map映射
     * 输入: 4000:102,103,104,105 5000:102,103,104,105,106,107 6000:102,103,104,105,106,107,108,109
     * -> [ "4000:102,103,104,105",
     *      "5000:102,103,104,105,106,107",
     *      "6000:102,103,104,105,106,107,108,109" ]
     * -> { 4000 -> "4000:102,103,104,105"
     *      5000 -> "5000:102,103,104,105,106,107"
     *      6000 -> "6000:102,103,104,105,106,107,108,109" }
     * @param ruleValue 规则值
     * @return 积分区间和奖品对应的map映射
     */
    private Map<Long, String> getAnalyticalValue(String ruleValue) {
        Map<Long, String> ruleValueMap = new HashMap<>();
        String[] ruleValueGroups = ruleValue.split(Constants.SPACE);

        for (String ruleValueKey : ruleValueGroups) {
            if (null == ruleValueKey) {
                return null;
            }

            String[] parts = ruleValueKey.split(Constants.COLON);
            // 合法性检查
            if (2 != parts.length) {
                throw new IllegalArgumentException("rule_weight rule_rule invalid input format" + ruleValueKey);
            }
            ruleValueMap.put(Long.parseLong(parts[0]), ruleValueKey);
        }

        return ruleValueMap;
    }
}
