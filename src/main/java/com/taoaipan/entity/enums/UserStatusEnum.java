package com.taoaipan.entity.enums;

/**
 * @version 1.0
 * @Author TaoAi
 * @Date 2023/12/8 9:57
 * @description 用户状态枚举类
 */
public enum UserStatusEnum {
    DISABLE(0, "禁用"),
    ENABLE(1, "启用");


    private Integer status;
    private String desc;

    UserStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static UserStatusEnum getByStatus(Integer status) {
        for (UserStatusEnum item : UserStatusEnum.values()) {
            if (item.getStatus().equals(status)) {
                return item;
            }
        }
        return null;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
