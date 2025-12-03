package org.example.infrastructure.persistent.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.infrastructure.persistent.po.RaffleActivity;

/**
 * 抽奖活动表DAO
 */

@Mapper
public interface IRaffleActivityDao {

    /**
     * 根据活动ID获取抽奖活动对象
     * @param activityId 活动ID
     * @return 抽奖活动对象
     */
    RaffleActivity queryRaffleActivityByActivityId(Long activityId);

}
