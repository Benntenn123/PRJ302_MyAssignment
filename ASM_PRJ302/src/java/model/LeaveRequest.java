package model;

import java.sql.Date;
import java.sql.Timestamp;

public class LeaveRequest {
    private int id;
    private int userId; // Thêm userId
    private String fullName;
    private String leaveType;
    private Date startDate;
    private Date endDate;
    private String reason;
    private String status;
    private Timestamp createdDate; // Thêm createdDate
    private Timestamp modifiedDate;

    // Getters và Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getCreatedDate() { return createdDate; }
    public void setCreatedDate(Timestamp createdDate) { this.createdDate = createdDate; }
    public Timestamp getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(Timestamp modifiedDate) { this.modifiedDate = modifiedDate; }

    // Thêm toString() để hỗ trợ debug
    @Override
    public String toString() {
        return "LeaveRequest{" +
                "id=" + id +
                ", userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", leaveType='" + leaveType + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", reason='" + reason + '\'' +
                ", status='" + status + '\'' +
                ", createdDate=" + createdDate +
                ", modifiedDate=" + modifiedDate +
                '}';
    }
}