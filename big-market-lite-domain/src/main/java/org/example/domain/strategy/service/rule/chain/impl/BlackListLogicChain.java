package org.example.domain.strategy.service.rule.chain.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.respository.IStrategyRepository;
import org.example.domain.strategy.service.rule.chain.AbstractLogicChain;
import org.example.types.common.Constants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 黑名单责任链
 */

@Slf4j
@Component("rule_blacklist")
public class BlackListLogicChain extends AbstractLogicChain {

    @Resource
    private IStrategyRepository repository;

    @Override
    protected String ruleModel() {
        return "rule_blacklist";
    }

    /**
     * 通过黑名单责任节点，获取奖品ID
     * 1. 当前用户为黑名单用户，返回固定的奖品ID
     * 2. 用户不是黑名单用户，则通过下一个责任节点继续尝试获取奖品ID
     * @param userId 用户ID
     * @param strategyId 策略ID
     * @return 奖品ID
     */
    @Override
    public Integer logic(String userId, Long strategyId) {
        log.info("抽奖责任链-黑名单开始 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());

        // 查询规则值配置
        String ruleValue = repository.queryStrategyRuleValue(strategyId, ruleModel());
        String[] splitRuleValue = ruleValue.split(Constants.COLON);
        Integer awardId = Integer.parseInt(splitRuleValue[0]);

        // 黑名单过滤
        String[] userBlackIds = splitRuleValue[1].split(Constants.SPLIT);
        for (String userBlackId : userBlackIds) {
            // null无法调用equals()方法，因此要将非空值放在左侧
            if (userId.equals(userBlackId)) {
                log.info("抽奖责任链-黑名单接管 userId: {} strategyId: {} ruleModel: {} awardId: {}", userId, strategyId, ruleModel(), awardId);
                return awardId;
            }
        }

        // 非黑名单用户，交由下一责任节点接管
        log.info("抽奖责任链-黑名单放行 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());
        return next().logic(userId, strategyId);
    }
}
