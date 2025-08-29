package com.scity.user.model.dto.tenant;

public class TenantStatusStatisticsDTO implements TenantStatusStatisticsProjection {
    private String status;
    private Long count;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
