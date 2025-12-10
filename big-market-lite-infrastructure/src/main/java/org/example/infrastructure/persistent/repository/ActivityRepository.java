package org.example.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.activity.model.aggregate.CreateOrderAggregate;
import org.example.domain.activity.model.entity.ActivityCountEntity;
import org.example.domain.activity.model.entity.ActivityEntity;
import org.example.domain.activity.model.entity.ActivityOrderEntity;
import org.example.domain.activity.model.entity.ActivitySkuEntity;
import org.example.domain.activity.model.vo.ActivityStateVO;
import org.example.domain.activity.repository.IActivityRepository;
import org.example.infrastructure.persistent.dao.IRaffleActivityCountDao;
import org.example.infrastructure.persistent.dao.IRaffleActivityDao;
import org.example.infrastructure.persistent.dao.IRaffleActivitySkuDao;
import org.example.infrastructure.persistent.dao.IRaffleActivityAccountDao;
import org.example.infrastructure.persistent.dao.IRaffleActivityOrderDao;
import org.example.infrastructure.persistent.po.*;
import org.example.infrastructure.persistent.redis.IRedisService;
import org.example.types.common.Constants;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;
import org.example.types.exception.AppException;
import org.example.types.enums.ResponseCode;

import javax.annotation.Resource;

/**
 * 抽奖活动仓储服务
 */

@Slf4j
@Repository
public class ActivityRepository implements IActivityRepository {

    @Resource
    private IRedisService redisService;

    @Resource
    private IRaffleActivityDao raffleActivityDao;

    @Resource
    private IRaffleActivitySkuDao raffleActivitySkuDao;

    @Resource
    private IRaffleActivityCountDao raffleActivityCountDao;

    @Resource
    private IRaffleActivityOrderDao raffleActivityOrderDao;

    @Resource
    private IRaffleActivityAccountDao raffleActivityAccountDao;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private IDBRouterStrategy dbRouter;

    @Override
    public ActivitySkuEntity queryActivitySku(Long sku) {
        RaffleActivitySku raffleActivitySku = raffleActivitySkuDao.queryActivitySku(sku);

        return ActivitySkuEntity.builder()
                .sku(raffleActivitySku.getSku())
                .activityId(raffleActivitySku.getActivityId())
                .activityCountId(raffleActivitySku.getActivityCountId())
                .stockCount(raffleActivitySku.getStockCount())
                .stockCountSurplus(raffleActivitySku.getStockCountSurplus())
                .build();
    }

    @Override
    public ActivityEntity queryRaffleActivityByActivityId(Long activityId) {
        // 优先查询缓存
        String cacheKey = Constants.RedisKey.ACTIVITY_KEY + activityId;
        ActivityEntity activityEntity = redisService.getValue(cacheKey);
        if (null != activityEntity) {
            return activityEntity;
        }

        // 查询数据库
        RaffleActivity raffleActivity = raffleActivityDao.queryRaffleActivityByActivityId(activityId);
        activityEntity = ActivityEntity.builder()
                .activityId(raffleActivity.getActivityId())
                .activityName(raffleActivity.getActivityName())
                .activityDesc(raffleActivity.getActivityDesc())
                .beginDateTime(raffleActivity.getBeginDateTime())
                .endDateTime(raffleActivity.getEndDateTime())
                .strategyId(raffleActivity.getStrategyId())
                .state(ActivityStateVO.valueOf(raffleActivity.getState()))
                .build();
        redisService.setValue(cacheKey, activityEntity);

        return activityEntity;
    }

    @Override
    public ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId) {
        // 优先查询缓存
        String cacheKey = Constants.RedisKey.ACTIVITY_COUNT_KEY + activityCountId;
        ActivityCountEntity activityCountEntity = redisService.getValue(cacheKey);
        if (null != activityCountEntity) {
            return activityCountEntity;
        }

        // 查询数据库
        RaffleActivityCount raffleActivityCount = raffleActivityCountDao.queryRaffleActivityCountByActivityCountId(activityCountId);
        activityCountEntity = ActivityCountEntity.builder()
                .activityCountId(raffleActivityCount.getActivityCountId())
                .totalCount(raffleActivityCount.getTotalCount())
                .dayCount(raffleActivityCount.getDayCount())
                .monthCount(raffleActivityCount.getMonthCount())
                .build();
        redisService.setValue(cacheKey, activityCountEntity);

        return activityCountEntity;
    }

    @Override
    public void doSaveOrder(CreateOrderAggregate createOrderAggregate) {
        try {
            ActivityOrderEntity activityOrderEntity = createOrderAggregate.getActivityOrderEntity();

            // 构建订单对象
            RaffleActivityOrder raffleActivityOrder = RaffleActivityOrder.builder()
                    .userId(activityOrderEntity.getUserId())
                    .sku(activityOrderEntity.getSku())
                    .activityId(activityOrderEntity.getActivityId())
                    .activityName(activityOrderEntity.getActivityName())
                    .strategyId(activityOrderEntity.getStrategyId())
                    .orderId(activityOrderEntity.getOrderId())
                    .orderTime(activityOrderEntity.getOrderTime())
                    .totalCount(activityOrderEntity.getTotalCount())
                    .dayCount(activityOrderEntity.getDayCount())
                    .monthCount(activityOrderEntity.getMonthCount())
                    .state(activityOrderEntity.getState().getCode())
                    .outBusinessNo(activityOrderEntity.getOutBusinessNo())
                    .build();

            // 构建账户对象
            RaffleActivityAccount raffleActivityAccount = RaffleActivityAccount.builder()
                    .userId(activityOrderEntity.getUserId())
                    .activityId(activityOrderEntity.getActivityId())
                    .totalCount(activityOrderEntity.getTotalCount())
                    .totalCountSurplus(activityOrderEntity.getTotalCount())
                    .dayCount(activityOrderEntity.getDayCount())
                    .dayCountSurplus(activityOrderEntity.getDayCount())
                    .monthCount(activityOrderEntity.getMonthCount())
                    .monthCountSurplus(activityOrderEntity.getMonthCount())
                    .build();

            // 以用户ID作为切分键，通过 doRouter 设定路由【这样就保证了下面的操作，都是同一个链接下，也就保证了事务的特性】
            dbRouter.doRouter(createOrderAggregate.getUserId());

            // 编程式事务
            transactionTemplate.execute(status -> {
                try {
                    // 写入订单
                    raffleActivityOrderDao.insert(raffleActivityOrder);

                    // 更新账户
                    int count = raffleActivityAccountDao.updateAccountQuota(raffleActivityAccount);

                    // 若结果更新为0，则账户不存在，创建新账户。
                    if (0 == count) {
                        raffleActivityAccountDao.insert(raffleActivityAccount);
                    }

                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("写入订单记录，唯一索引冲突 userId: {} activityId: {} sku: {}", activityOrderEntity.getUserId(), activityOrderEntity.getActivityId(), activityOrderEntity.getSku(), e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode());
                }
            });
        } finally {
            dbRouter.clear();
        }

    }
}
