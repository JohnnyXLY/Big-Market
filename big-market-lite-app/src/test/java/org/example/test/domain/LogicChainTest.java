package org.example.test.domain;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.service.armory.IStrategyArmory;
import org.example.domain.strategy.service.rule.chain.ILogicChain;
import org.example.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import org.example.domain.strategy.service.rule.chain.impl.RuleWeightLogicChain;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;

/**
 * 抽奖责任链测试
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class LogicChainTest {
    @Resource
    private IStrategyArmory strategyArmory;

    @Resource
    private DefaultChainFactory defaultChainFactory;

    @Resource
    private RuleWeightLogicChain ruleWeightLogicChain;

    @Before
    public void setUp() {
        // 策略装配
        log.info("测试结果：{}", strategyArmory.assembleLotteryStrategy(100001L));
        log.info("测试结果：{}", strategyArmory.assembleLotteryStrategy(100002L));
        log.info("测试结果：{}", strategyArmory.assembleLotteryStrategy(100003L));
    }

    @Test
    public void test_LogicChain_rule_blacklist() {
        ILogicChain logicChain = defaultChainFactory.openLogicChain(100001L);
        DefaultChainFactory.StrategyAwardVO strategyAwardVO = logicChain.logic("user001", 100001L);
        log.info("测试结果：{}", strategyAwardVO);
    }

    @Test
    public void test_LogicChain_rule_weight() {
        // 通过反射修改userScored的值
        ReflectionTestUtils.setField(ruleWeightLogicChain, "userScore", 5500L);

        ILogicChain logicChain = defaultChainFactory.openLogicChain(100001L);
        DefaultChainFactory.StrategyAwardVO strategyAwardVO = logicChain.logic("JohnnyXLY", 100001L);
        log.info("测试结果：{}", strategyAwardVO);
    }

    @Test
    public void test_LogicChain_rule_default() {
        ILogicChain logicChain = defaultChainFactory.openLogicChain(100003L);
        DefaultChainFactory.StrategyAwardVO strategyAwardVO = logicChain.logic("JohnnyXLY", 100003L);
        log.info("测试结果：{}", strategyAwardVO);
    }
}
