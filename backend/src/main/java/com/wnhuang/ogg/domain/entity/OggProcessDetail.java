package com.wnhuang.ogg.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author wnhuang
 * @created by Administrator on 2023-04-17-22:20
 */
@Data
public class OggProcessDetail {

    private String from;

    private String to;

    private Integer operationsTotal;
    private Integer discardsTotal;
    private Integer insertTotal;
    private Integer  updatesTotal;
    private Integer  ignoresTotal;
    private Integer  deletesTotal;

    private LocalDateTime sinceTime;
}
