package org.example.infrastructure.persistent.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.RaffleActivityCount;

/**
 * 抽奖活动次数配置表DAO
 */

@Mapper
public interface IRaffleActivityCountDao {

    /**
     * 根据活动次数编号查询抽奖活动次数持久化对象
     * @param activityCountId 抽奖活动次数编号
     * @return 抽奖活动次数持久化对象
     */
    RaffleActivityCount queryRaffleActivityCountByActivityCountId(Long activityCountId);

}
