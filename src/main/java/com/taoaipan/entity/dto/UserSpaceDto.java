package com.taoaipan.entity.dto;

import java.io.Serializable;

/**
 * @version 1.0
 * @Author TaoAi
 * @Date 2023/12/8 11:13
 * @description 用户空间 DTO对象
 */
public class UserSpaceDto implements Serializable {
    private Long useSpace;
    private Long totalSpace;

    public Long getUseSpace() {
        return useSpace;
    }

    public void setUseSpace(Long useSpace) {
        this.useSpace = useSpace;
    }

    public Long getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(Long totalSpace) {
        this.totalSpace = totalSpace;
    }
}
