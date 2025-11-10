package com.library.librarymanagement.dto.response.admin_dashboard;

import java.math.BigDecimal;

public class TotalRevenueResponse {

    private BigDecimal totalRevenue;

    public TotalRevenueResponse() {
    }

    public TotalRevenueResponse(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
