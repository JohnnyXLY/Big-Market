package org.example.domain.strategy.service.rule.chain;

/**
 * 抽奖规则策略责任链接口
 * 将原有的抽奖前置规则抽象，变更为责任链处理
 * 责任链工厂顺序读取责任节点，填充至责任链上
 * 一种可能的责任链顺序
 * rule_blacklist -> rule_weight -> default
 */

public interface ILogicChain extends ILogicChainArmory {
    /**
     * 责任链接口，不同策略实现逻辑不同
     * @param userId 用户ID
     * @param strategyId 策略ID
     * @return 奖品ID
     */
    Integer logic(String userId, Long strategyId);
}
