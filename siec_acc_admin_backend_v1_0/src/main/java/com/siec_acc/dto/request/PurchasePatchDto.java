package com.siec_acc.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Used for PATCH. Every field is optional (null = "leave unchanged"),
 * but any value that IS sent must still be valid.
 */
public class PurchasePatchDto {

    @Pattern(regexp = ".*\\S.*", message = "Item name cannot be blank")
    @Size(max = 255, message = "Item name must be at most 255 characters")
    private String itemName;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Size(max = 30, message = "Unit must be at most 30 characters")
    private String unit;

    @Pattern(regexp = ".*\\S.*", message = "Requested by cannot be blank")
    @Size(max = 255, message = "Requested by must be at most 255 characters")
    private String requestedBy;

    @Size(max = 255, message = "Department must be at most 255 characters")
    private String department;

    private LocalDate requiredDate;

    @Pattern(regexp = "(?i)low|medium|high", message = "Priority must be low, medium or high")
    private String priority;

    @Pattern(regexp = "(?i)pending|approved|rejected", message = "Status must be pending, approved or rejected")
    private String status;

    @Size(max = 2000, message = "Remarks must be at most 2000 characters")
    private String remarks;

    public PurchasePatchDto() {}

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public LocalDate getRequiredDate() { return requiredDate; }
    public void setRequiredDate(LocalDate requiredDate) { this.requiredDate = requiredDate; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}

