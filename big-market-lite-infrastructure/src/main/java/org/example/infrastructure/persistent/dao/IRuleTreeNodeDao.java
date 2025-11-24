package org.example.infrastructure.persistent.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.RuleTreeNode;

import java.util.List;

/**
 * 规则树节点表DAO
 */

@Mapper
public interface IRuleTreeNodeDao {

    /**
     * 根据树的ID获取对应规则树节点
     * @param treeId 规则树ID
     * @return 规则树节点集合
     */
    List<RuleTreeNode> queryRuleTreeNodeListByTreeId(String treeId);

}
