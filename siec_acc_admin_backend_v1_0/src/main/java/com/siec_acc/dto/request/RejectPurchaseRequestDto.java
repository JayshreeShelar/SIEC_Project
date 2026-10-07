
package com.siec_acc.dto.request;

import jakarta.validation.constraints.Size;

public class RejectPurchaseRequestDto {

    @Size(max = 2000, message = "Rejection reason must be at most 2000 characters")
    private String reason;

    public RejectPurchaseRequestDto() {}

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}


