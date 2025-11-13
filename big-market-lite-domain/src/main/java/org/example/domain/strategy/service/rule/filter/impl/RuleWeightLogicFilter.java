package org.example.domain.strategy.service.rule.filter.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.domain.strategy.model.entity.RuleActionEntity;
import org.example.domain.strategy.model.entity.RuleMatterEntity;
import org.example.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import org.example.domain.strategy.respository.IStrategyRepository;
import org.example.domain.strategy.service.annotation.LogicStrategy;
import org.example.domain.strategy.service.rule.filter.ILogicFilter;
import org.example.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import org.example.types.common.Constants;
import org.example.types.exception.AppException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 【抽奖前规则】 根据抽奖权重返回可抽奖范围
 */

@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_WEIGHT)
public class RuleWeightLogicFilter implements ILogicFilter<RuleActionEntity.RaffleBeforeEntity> {
    @Resource
    private IStrategyRepository repository;

    // 用户积分，用于确定奖励区间
    public Long userScore = 4500L;

    @Override
    public RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> filter(RuleMatterEntity ruleMatterEntity) {
        Long strategyId = ruleMatterEntity.getStrategyId();
        String userId = ruleMatterEntity.getUserId();
        Integer awardId = ruleMatterEntity.getAwardId();
        String ruleModel = ruleMatterEntity.getRuleModel();
        log.info("规则过滤-权重范围 userId:{} strategyId:{} ruleModel:{}", userId, strategyId, ruleModel);

        // 通过策略id + 奖品id + 规则模型(有的奖品对应多个规则模型，因此需要指定) 确定规则值
        String ruleValue = repository.queryStrategyRuleValue(strategyId, awardId, ruleModel);

        // 对ruleValue进行处理
        Map<Long, String> analyticalValueMap = getAnalyticalValue(ruleValue);
        if (null == analyticalValueMap || analyticalValueMap.isEmpty()) {
            // 无权重模型，放行
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }

        // 获奖区间从小到大进行排序
        // 4000 -> 5000 -> 6000
        ArrayList<Long> analyticalSortedKeys = new ArrayList<>(analyticalValueMap.keySet());
        Collections.sort(analyticalSortedKeys);

        // 确定抽奖区间(即找到小于userScored的最大获奖区间)
        // 2500积分 -> null  4500积分 -> 4000抽奖档  5500积分 -> 5000抽奖档
        Long nextValue = analyticalSortedKeys.stream()
                .sorted(Comparator.reverseOrder())
                .filter(key -> userScore >= key)
                .findFirst()
                .orElse(null);

        if (null != nextValue) {
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                    .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                    .ruleModel(DefaultLogicFactory.LogicModel.RULE_WEIGHT.getCode())
                    .data(RuleActionEntity.RaffleBeforeEntity.builder()
                            .strategyId(strategyId)
                            .awardId(awardId)
                            .ruleWeightValueKey(analyticalValueMap.get(nextValue))
                            .build())
                    .build();
        }

        // 积分未达到权重抽奖范围(<4000)，普通抽奖
        return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                .build();
    }

    // 处理ruleValue字符串，形成map映射关系
    private Map<Long, String> getAnalyticalValue(String ruleValue) {
        Map<Long, String> ruleValueMap = new HashMap<>();

        String[] ruleValueGroups = ruleValue.split(Constants.SPACE);
        for (String ruleValueKey : ruleValueGroups) {
            // 有效性检查
            if (null == ruleValueKey || StringUtils.isBlank(ruleValueKey)) {
                return null;
            }

            String[] parts = ruleValueKey.split(Constants.COLON);
            // 有效性检查
            if (parts.length != 2) {
                throw new AppException("rule_weight rule_value invalid input format " + ruleValueKey);
            }
            ruleValueMap.put(Long.parseLong(parts[0]), ruleValueKey);
        }

        return ruleValueMap;
    }
}
