package org.example.domain.strategy.service.rule.filter.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.entity.RuleActionEntity;
import org.example.domain.strategy.model.entity.RuleMatterEntity;
import org.example.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import org.example.domain.strategy.respository.IStrategyRepository;
import org.example.domain.strategy.service.annotation.LogicStrategy;
import org.example.domain.strategy.service.rule.filter.ILogicFilter;
import org.example.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * 【抽奖中规则】 抽奖n次后可解锁对应奖品
 */

@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_LOCK)
public class RuleLockLogicFilter implements ILogicFilter<RuleActionEntity.RaffleCenterEntity> {
    @Resource
    private IStrategyRepository repository;

    private Long userRaffleCount = 0L;

    @Override
    public RuleActionEntity<RuleActionEntity.RaffleCenterEntity> filter(RuleMatterEntity ruleMatterEntity) {
        Long strategyId = ruleMatterEntity.getStrategyId();
        String userId = ruleMatterEntity.getUserId();
        String ruleModel = ruleMatterEntity.getRuleModel();
        Integer awardId = ruleMatterEntity.getAwardId();
        log.info("规则过滤-次数锁 userId:{} strategyId:{} ruleModel:{}", userId, strategyId, ruleModel);

        // 查询规则值
        // rule_lock对应的rule_value是一个数值，表示解锁奖品所需抽奖次数
        String ruleValue = repository.queryStrategyRuleValue(strategyId, awardId, ruleModel);
        long raffleCount = Long.parseLong(ruleValue);

        // 抽奖次数达到规定值，放行
        if (userRaffleCount >= raffleCount) {
            return RuleActionEntity.<RuleActionEntity.RaffleCenterEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }

        // 抽奖次数不足，拦截
        return RuleActionEntity.<RuleActionEntity.RaffleCenterEntity>builder()
                .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                .build();
    }
}
