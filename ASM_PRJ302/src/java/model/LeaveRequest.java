package model;

import java.sql.Date;
import java.sql.Timestamp;

public class LeaveRequest {
    private int id;
    private String fullName;
    private String leaveType;
    private Date startDate;
    private Date endDate;
    private String reason;
    private String status;
    private Timestamp modifiedDate;

    // Getters và Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
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
    public Timestamp getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(Timestamp modifiedDate) { this.modifiedDate = modifiedDate; }
}