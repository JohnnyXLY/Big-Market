package org.example.domain.activity.service.rule;

/**
 * 责任链装配
 */

public interface IActionChainArmory {

    IActionChain next();

    IActionChain appendNext(IActionChain next);

}
