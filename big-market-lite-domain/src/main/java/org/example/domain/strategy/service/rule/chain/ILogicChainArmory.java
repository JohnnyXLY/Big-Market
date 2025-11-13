package org.example.domain.strategy.service.rule.chain;

/**
 * 责任链装配
 */

public interface ILogicChainArmory {

    /**
     * 获取责任链的下一个责任节点
     * @return
     */
    ILogicChain next();

    /**
     * 向责任链中添加下一个责任节点
     * @param next
     * @return
     */
    ILogicChain appendNext(ILogicChain next);
}
