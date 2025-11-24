package org.example.infrastructure.persistent.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.RuleTree;

/**
 * 规则树表DAO
 */

@Mapper
public interface IRuleTreeDao {
    /**
     * 根据树的ID获取对应规则树
     * @param treeId 规则树ID
     * @return 规则树
     */
    RuleTree queryRuleTreeByTreeId(String treeId);
}
