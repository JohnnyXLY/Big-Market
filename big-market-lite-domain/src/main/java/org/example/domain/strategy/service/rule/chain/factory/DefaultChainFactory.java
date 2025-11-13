package org.example.domain.strategy.service.rule.chain.factory;

import org.example.domain.strategy.model.entity.StrategyEntity;
import org.example.domain.strategy.respository.IStrategyRepository;
import org.example.domain.strategy.service.rule.chain.ILogicChain;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 策略责任链工厂，负责责任节点装配
 */

@Service
public class DefaultChainFactory {
    private final Map<String, ILogicChain> logicChainGroup;
    protected IStrategyRepository repository;

    // 此处的依赖注入方式为构造方法注入
    public DefaultChainFactory(IStrategyRepository repository, Map<String, ILogicChain> logicChainGroup) {
        /**
         * @Service是Spring的组件注解，被Spring扫描并识别为需要管理的Bean
         * Spring会创建该类(DefaultChainFactory)的实例(defaultChainfactory)，并自动完成依赖注入
         * 根据@Component注解中的value值完成logicChainGroup的构建
         * 例如，impl包下有@Component("rule_blacklist"),@Component("rule_weight")
         * 则构建K-V键值对 "rule_blacklist" -> BlackListLoginChain, "rule_weight" -> RuleWeightLogicChain
         */
        this.repository = repository;
        this.logicChainGroup = logicChainGroup;
    }

    /**
     * 根据策略ID构建责任链
     * @param strategyId 策略ID
     * @return 责任链(头节点)
     */
    public ILogicChain openLogicChain(Long strategyId) {
        StrategyEntity strategyEntity = repository.queryStrategyEntityByStrategyId(strategyId);
        // 这里是从redis中查询到的数据，因此这里的ruleModdel中不含default
        String[] ruleModels = strategyEntity.ruleModels();

        if (null == ruleModels || 0 == ruleModels.length) {
            // 填充默认责任链
            return logicChainGroup.get("default");
        }

        ILogicChain logicChain = logicChainGroup.get(ruleModels[0]);
        ILogicChain current = logicChain;
        for (int i = 1; i < ruleModels.length; i ++ ) {
            // 构建责任链
            // appendNext()的返回值是current.next,已经完成了节点的更新
            current = current.appendNext(logicChainGroup.get(ruleModels[i]));
        }

        // 添加默认责任节点兜底
        current.appendNext(logicChainGroup.get("default"));

        // 返回责任链头节点
        return logicChain;
    }
}
