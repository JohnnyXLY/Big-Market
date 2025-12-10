package org.example.infrastructure.persistent.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.RaffleActivityAccount;

/**
 * 抽奖活动账户表DAO
 */

@Mapper
public interface IRaffleActivityAccountDao {

    /**
     * 插入一条账户记录
     * @param raffleActivityAccount 抽奖活动账户表持久化对象
     */
    void insert(RaffleActivityAccount raffleActivityAccount);

    /**
     * 更新账户记录
     * 0 -> 未找到账户记录，无更新
     * 1 -> 找到账户记录，成功更新
     * @param raffleActivityAccount 抽奖活动账户表持久化对象
     * @return 更新结果(数据库中变更的行数)
     */
    int updateAccountQuota(RaffleActivityAccount raffleActivityAccount);

}
