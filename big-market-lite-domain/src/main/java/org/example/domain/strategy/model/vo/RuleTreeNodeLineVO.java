package org.example.domain.strategy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 规则树节点指向对象
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleTreeNodeLineVO {
    // 规则树ID
    private Integer treeId;

    /**
     * From和To的存在是为了判断当前树节点是否还能继续向下进行搜索
     * 若不能继续搜索，则执行当前节点的策略规则
     * 构建的这条链是从当前节点向下延伸的，因此From为当前节点，To为下一节点(左孩子/右孩子)
     */
    // From节点(当前节点)
    private String ruleNodeFrom;

    // To节点
    private String ruleNodeTo;

    // 限定类型
    // 1:=; 2:>; 3:<; 4:>=; 5<=; 6:enum[枚举范围]
    private RuleLimitTypeVO ruleLimitType;

    // 限定值
    private RuleLogicCheckTypeVO ruleLimitValue;
}
