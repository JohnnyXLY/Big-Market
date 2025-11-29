package org.example.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 抽奖奖品列表响应对象
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RaffleAwardListResponseDTO {

    // 奖品ID
    private Integer awardId;

    // 奖品标题
    private String awardTitle;

    // 奖品副标题
    private String awardSubtitle;

    // 排序编号
    private Integer sort;

}
