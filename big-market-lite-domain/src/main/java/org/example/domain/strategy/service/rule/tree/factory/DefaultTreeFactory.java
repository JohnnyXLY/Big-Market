package org.example.domain.strategy.service.rule.tree.factory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import org.example.domain.strategy.model.vo.RuleTreeVO;
import org.example.domain.strategy.service.rule.tree.ILogicTreeNode;
import org.example.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import org.example.domain.strategy.service.rule.tree.factory.engine.impl.DecisionTreeEngine;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 规则树工厂
 */

@Service
public class DefaultTreeFactory {
    // 规则名和规则树节点构成的映射
    private final Map<String, ILogicTreeNode> logicTreeNodeGroup;

    public DefaultTreeFactory(Map<String, ILogicTreeNode> logicTreeNodeGroup) {
        this.logicTreeNodeGroup = logicTreeNodeGroup;
    }

    // 获取决策树引擎
    public IDecisionTreeEngine openLogicTree(RuleTreeVO ruleTreeVO) {
        return new DecisionTreeEngine(logicTreeNodeGroup, ruleTreeVO);
    }

    /**
     * 决策动作实体
     * 包含规则过滤校验类型值对象(该节点放行/接管)以及策略奖品实体对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TreeActionEntity {
        private RuleLogicCheckTypeVO ruleLogicCheckTypeVO;
        private StrategyAwardVO strategyAwardVO;
    }

    /**
     * 策略奖品实体类
     * 记录奖品ID和奖品规则
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StrategyAwardVO {
        // 奖品ID
        private Integer awardId;
        // 奖品规则
        private String awardRuleValue;
    }

}
