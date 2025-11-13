package org.example.test.domain;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.entity.RaffleAwardEntity;
import org.example.domain.strategy.model.entity.RaffleFactorEntity;
import org.example.domain.strategy.service.IRaffleStrategy;
import org.example.domain.strategy.service.armory.IStrategyArmory;
import org.example.domain.strategy.service.rule.filter.impl.RuleLockLogicFilter;
import org.example.domain.strategy.service.rule.filter.impl.RuleWeightLogicFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;

/**
 * 抽奖策略测试
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleStrategyTest {
    @Resource
    private IStrategyArmory strategyArmory;

    @Resource
    private IRaffleStrategy raffleStrategy;

    @Resource
    private RuleWeightLogicFilter ruleWeightLogicFilter;

    @Resource
    private RuleLockLogicFilter ruleLockLogicFilter;

    @Before
    public void setUp() {
        // 策略装配 100001、100002、100003
        log.info("测试结果：{}", strategyArmory.assembleLotteryStrategy(100001L));
        log.info("测试结果：{}", strategyArmory.assembleLotteryStrategy(100002L));
        log.info("测试结果：{}", strategyArmory.assembleLotteryStrategy(100003L));

        // 通过反射给对象赋值
        ReflectionTestUtils.setField(ruleWeightLogicFilter, "userScore", 5500L);
        ReflectionTestUtils.setField(ruleLockLogicFilter, "userRaffleCount", 0L);
    }

    @Test
    public void test_performRaffle() {
        // RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity);
        // 需要构建一个RaffleFactorEntity(userId, strategyId)对象传入
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .strategyId(100001L)
                .userId("JohnnyXLY")
                .build();

        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);

        log.info("请求参数：{}", JSON.toJSONString(raffleFactorEntity));
        log.info("测试结果：{}", JSON.toJSONString(raffleAwardEntity));
    }

    @Test
    public void test_performRaffle_blacklist() {
        // RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity);
        // 需要构建一个RaffleFactorEntity(userId, strategyId)对象传入
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .strategyId(100001L)
                .userId("user001")  // 黑名单用户: user001, user002, user003
                .build();

        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);

        log.info("请求参数：{}", JSON.toJSONString(raffleFactorEntity));
        log.info("测试结果：{}", JSON.toJSONString(raffleAwardEntity));
    }

    @Test
    public void test_raffle_center_rule_lock() {
        // RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity);
        // 需要构建一个RaffleFactorEntity(userId, strategyId)对象传入
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .strategyId(100003L)
                .userId("JohnnyXLY")
                .build();

        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);

        log.info("请求参数：{}", JSON.toJSONString(raffleFactorEntity));
        log.info("测试结果：{}", JSON.toJSONString(raffleAwardEntity));
    }
}
