package org.example.domain.strategy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 规则树对象
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleTreeVO {
    // 规则树ID
    private Integer treeId;

    // 规则树名
    private String treeName;

    // 规则树描述
    private String treeDesc;

    // 规则树根节点(根节点名)
    private String treeRootRuleNode;

    // 规则节点
    private Map<String, RuleTreeNodeVO> treeNodeMap;
}
