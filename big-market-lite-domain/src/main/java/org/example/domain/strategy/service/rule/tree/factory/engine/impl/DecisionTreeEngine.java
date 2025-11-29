package org.example.domain.strategy.service.rule.tree.factory.engine.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import org.example.domain.strategy.model.vo.RuleTreeNodeLineVO;
import org.example.domain.strategy.model.vo.RuleTreeNodeVO;
import org.example.domain.strategy.model.vo.RuleTreeVO;
import org.example.domain.strategy.service.rule.tree.ILogicTreeNode;
import org.example.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import org.example.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;

import java.util.List;
import java.util.Map;

/**
 * 决策树引擎
 * 决策的内容 -> 从根节点起，执行怎样的操作(action)，返回什么样的结果(award)
 */

@Slf4j
public class DecisionTreeEngine implements IDecisionTreeEngine {
    // 规则名和规则树节点构成的映射
    private final Map<String, ILogicTreeNode> logicTreeNodeGroup;

    // 规则树
    private final RuleTreeVO ruleTreeVO;

    public DecisionTreeEngine(Map<String, ILogicTreeNode> logicTreeNodeGroup, RuleTreeVO ruleTreeVO) {
        this.logicTreeNodeGroup = logicTreeNodeGroup;
        this.ruleTreeVO = ruleTreeVO;
    }

    /**
     * 决策执行
     * @param userId 用户ID
     * @param strategyId 策略ID
     * @param awardId 奖品ID
     * @return 策略奖品实例(奖品ID和奖品规则)
     */
    @Override
    public DefaultTreeFactory.StrategyAwardVO process(String userId, Long strategyId, Integer awardId) {
        DefaultTreeFactory.StrategyAwardVO strategyAwardVO = null;

        // 从规则树中获取信息
        // 根节点名称
        String treeRootRuleNode = ruleTreeVO.getTreeRootRuleNode();
        // 获取该树中所有的节点名称和节点构成的映射
        Map<String, RuleTreeNodeVO> treeNodeMap = ruleTreeVO.getTreeNodeMap();

        // 获取根节点并执行该决策树，叶子节点即为需要执行的决策
        RuleTreeNodeVO ruleTreeNodeVO = treeNodeMap.get(treeRootRuleNode);
        while(null != treeRootRuleNode) {
            // 获取节点实例，明确决策动作(TreeActionEntity)，构建策略奖品实体(StrategyAwardVO)
            ILogicTreeNode logicTreeNode = logicTreeNodeGroup.get(ruleTreeNodeVO.getRuleKey());
            String ruleValue = ruleTreeNodeVO.getRuleValue();

            DefaultTreeFactory.TreeActionEntity treeActionEntity = logicTreeNode.logic(userId, strategyId, awardId, ruleValue);

            // 获取规则过滤校验类型值(ALLOW/TAKE_OVER)
            RuleLogicCheckTypeVO ruleLogicCheckTypeVO = treeActionEntity.getRuleLogicCheckTypeVO();
            strategyAwardVO = treeActionEntity.getStrategyAwardVO();
            log.info("决策树引擎【{}】treeId:{} node:{} code:{}", ruleTreeVO.getTreeName(), ruleTreeVO.getTreeId(), treeRootRuleNode, ruleLogicCheckTypeVO.getCode());

            // 获取下一个树节点
            treeRootRuleNode = nextNode(ruleLogicCheckTypeVO.getCode(), ruleTreeNodeVO.getTreeNodeLineVOList());
            ruleTreeNodeVO = treeNodeMap.get(treeRootRuleNode);
        }
        return strategyAwardVO;
    }

    /**
     * 获取下一个决策树节点
     * @param matterValue 规则校验值
     * @param treeNodeLineVOList 决策树节点规则连线
     * @return 下一决策树节点名
     */
    public String nextNode(String matterValue, List<RuleTreeNodeLineVO> treeNodeLineVOList) {
        // 当前节点为叶子节点，不能继续向下搜索
        if (null == treeNodeLineVOList || treeNodeLineVOList.isEmpty()) {
            return null;
        }

        for (RuleTreeNodeLineVO nodeLine : treeNodeLineVOList) {
            if (decisionLogic(matterValue, nodeLine)) {
                // 规则校验值匹配，放行至下一决策树节点
                return nodeLine.getRuleNodeTo();
            }
        }

        return null;
    }

    /**
     * 决策逻辑，判断树节点的规则校验值是否匹配
     * 若匹配则放行至下一个节点，否则在当前节点停留并执行相关策略
     * @param matterValue 规则校验值
     * @param nodeLine 决策树节点规则连线
     * @return 是否匹配
     */
    public boolean decisionLogic(String matterValue, RuleTreeNodeLineVO nodeLine) {
        switch (nodeLine.getRuleLimitType()) {
            case EQUAL:
                return matterValue.equals(nodeLine.getRuleLimitValue().getCode());
            // 以下规则暂时不需要实现
            case GT:
            case LT:
            case GE:
            case LE:
            default:
                return false;
        }
    }
}
