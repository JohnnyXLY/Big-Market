package org.example.domain.strategy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import org.example.types.common.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * 策略规则模型值对象，从数据库中查询得到
 */

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StrategyAwardRuleModelVO {
    private String ruleModels;

    // 获取中置抽奖模型值列表
    public String[] raffleCenterRuleModelList() {
        if (null == ruleModels) {
            return null;
        }

        List<String> ruleModelList = new ArrayList<>();
        String[] ruleModelValues = ruleModels.split(Constants.SPLIT);

        for (String ruleModelValue : ruleModelValues) {
            if (DefaultLogicFactory.LogicModel.isCenter(ruleModelValue)) {
                ruleModelList.add(ruleModelValue);
            }
        }
        return ruleModelList.toArray(new String[0]);
    }

    // 获取后置抽奖模型值列表
    public String[] raffleAfterRuleModelList() {
        List<String> ruleModelList = new ArrayList<>();
        String[] ruleModelValues = ruleModels.split(Constants.SPACE);
        for (String ruleModelValue : ruleModelValues) {
            if (DefaultLogicFactory.LogicModel.isAfter(ruleModelValue)) {
                ruleModelList.add(ruleModelValue);
            }
        }
        return ruleModelList.toArray(new String[0]);
    }
}
