package org.example.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 抽奖响应对象
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RaffleResponseDTO {

    // 奖品ID
    private Integer awardId;

    // 排序编号【策略奖品配置的奖品顺序编号】
    private Integer awardIndex;

}
