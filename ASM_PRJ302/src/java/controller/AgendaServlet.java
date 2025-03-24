package controller;

import dal.DatabaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.TextStyle;

@WebServlet(name = "AgendaServlet", urlPatterns = {"/agenda"})
public class AgendaServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        // Check if user has appropriate role (not Employee)
        String role = (String) session.getAttribute("role");
        if (role == null || role.equals("Employee")) {
            response.sendRedirect("home");
            return;
        }
        
        // Get filter parameters
        int userId = (Integer) session.getAttribute("userId");
        int userRole = getUserRole(userId);
        
        // Get current year and month if not specified
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) + 1; // Java months are 0-based
        
        int year = currentYear;
        int month = currentMonth;
        String department = "";
        
        // Process filter parameters
        try {
            String yearParam = request.getParameter("year");
            if (yearParam != null && !yearParam.isEmpty()) {
                year = Integer.parseInt(yearParam);
            }
            
            String monthParam = request.getParameter("month");
            if (monthParam != null && !monthParam.isEmpty()) {
                month = Integer.parseInt(monthParam);
            }
            
            String deptParam = request.getParameter("department");
            if (deptParam != null) {
                department = deptParam;
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid date parameters.");
        }
        
        // Set attributes for the filter form
        request.setAttribute("currentYear", currentYear);
        request.setAttribute("year", year);
        request.setAttribute("month", month);
        request.setAttribute("department", department);
        
        // Get departments for filter dropdown
        List<String> departments = getDepartments(userId, userRole);
        request.setAttribute("departments", departments);
        
        // Create month names for dropdown
        Calendar monthCal = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
        Date[] monthNames = new Date[12];
        for (int i = 0; i < 12; i++) {
            monthCal.set(Calendar.MONTH, i);
            monthNames[i] = monthCal.getTime();
        }
        request.setAttribute("monthNames", monthNames);
        
        // Get calendar day headers
        List<DayHeader> dayHeaders = getDayHeaders(year, month);
        request.setAttribute("dayHeaders", dayHeaders);
        
        // Get team members with attendance data
        List<TeamMember> teamMembers = getTeamAttendance(userId, userRole, year, month, department);
        request.setAttribute("teamMembers", teamMembers);
        
        request.getRequestDispatcher("agenda.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
    
    // Get user's role ID
    private int getUserRole(int userId) {
        String sql = "SELECT r.RoleID " +
                     "FROM UserRoles ur " +
                     "INNER JOIN Roles r ON ur.RoleID = r.RoleID " +
                     "WHERE ur.UserID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("RoleID");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user role: " + e.getMessage());
        }
        return -1;
    }
    
    // Get list of departments that the user has access to
    private List<String> getDepartments(int userId, int userRole) {
        List<String> departments = new ArrayList<>();
        
        // If user role is not high enough, return empty list
        if (userRole <= 1) {
            return departments;
        }
        
        // For higher roles, get all departments
        String sql = "SELECT DISTINCT Department FROM Users WHERE Department IS NOT NULL AND Department != ''";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String dept = rs.getString("Department");
                if (dept != null && !dept.isEmpty()) {
                    departments.add(dept);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving departments: " + e.getMessage());
        }
        
        Collections.sort(departments);
        return departments;
    }
    
    // Get day headers for the month
    private List<DayHeader> getDayHeaders(int year, int month) {
        List<DayHeader> headers = new ArrayList<>();
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();
        
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(year, month, day);
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            boolean isWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
            
            DayHeader header = new DayHeader();
            header.setDay(day);
            header.setDayOfWeek(dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()));
            header.setWeekend(isWeekend);
            
            headers.add(header);
        }
        
        return headers;
    }
    
    // Get team members with their attendance data
    private List<TeamMember> getTeamAttendance(int userId, int userRole, int year, int month, String department) {
        List<TeamMember> teamMembers = new ArrayList<>();
        
        // If user role is not high enough, return empty list
        if (userRole <= 1) {
            return teamMembers;
        }
        
        // Build query based on department filter
        StringBuilder sqlBuilder = new StringBuilder(
            "SELECT u.UserID, u.FullName, u.Department " +
            "FROM Users u " +
            "LEFT JOIN UserRoles ur ON u.UserID = ur.UserID " +
            "LEFT JOIN Roles r ON ur.RoleID = r.RoleID " +
            "WHERE r.RoleID <= ? "  // Users with lower or equal role level
        );
        
        if (department != null && !department.isEmpty()) {
            sqlBuilder.append("AND u.Department = ? ");
        }
        
        sqlBuilder.append("ORDER BY u.Department, u.FullName");
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlBuilder.toString())) {
            
            stmt.setInt(1, userRole);
            if (department != null && !department.isEmpty()) {
                stmt.setString(2, department);
            }
            
            ResultSet rs = stmt.executeQuery();
            
            // Get days in month
            YearMonth yearMonth = YearMonth.of(year, month);
            int daysInMonth = yearMonth.lengthOfMonth();
            
            // Store all leave requests for the month
            Map<Integer, List<LeaveDay>> allLeaveRequests = getAllLeaveRequests(year, month);
            
            while (rs.next()) {
                int memberId = rs.getInt("UserID");
                String fullName = rs.getString("FullName");
                String dept = rs.getString("Department");
                
                TeamMember member = new TeamMember();
                member.setUserId(memberId);
                member.setFullName(fullName);
                member.setDepartment(dept);
                
                // Create attendance data for each day
                List<AttendanceDay> attendanceData = new ArrayList<>();
                for (int day = 1; day <= daysInMonth; day++) {
                    LocalDate date = LocalDate.of(year, month, day);
                    
                    AttendanceDay attendanceDay = new AttendanceDay();
                    attendanceDay.setDay(day);
                    
                    // Check if weekend
                    DayOfWeek dayOfWeek = date.getDayOfWeek();
                    boolean isWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
                    attendanceDay.setWeekend(isWeekend);
                    
                    // Check if on leave
                    boolean onLeave = false;
                    String leaveType = "";
                    
                    if (allLeaveRequests.containsKey(memberId)) {
                        List<LeaveDay> leaves = allLeaveRequests.get(memberId);
                        for (LeaveDay leave : leaves) {
                            if (leave.getDay() == day) {
                                onLeave = true;
                                leaveType = leave.getLeaveType();
                                break;
                            }
                        }
                    }
                    
                    attendanceDay.setOnLeave(onLeave);
                    attendanceDay.setLeaveType(leaveType);
                    
                    attendanceData.add(attendanceDay);
                }
                
                member.setAttendanceData(attendanceData);
                teamMembers.add(member);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving team members: " + e.getMessage());
        }
        
        return teamMembers;
    }
    
    // Get all leave requests for the specified month
    private Map<Integer, List<LeaveDay>> getAllLeaveRequests(int year, int month) {
        Map<Integer, List<LeaveDay>> leaveRequests = new HashMap<>();
        
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        
        Date startDate = Date.from(startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
        
        String sql = "SELECT lr.UserID, lr.LeaveType, lr.StartDate, lr.EndDate " +
                     "FROM LeaveRequests lr " +
                     "WHERE lr.Status = 'Approved' " +
                     "AND ((lr.StartDate BETWEEN ? AND ?) OR (lr.EndDate BETWEEN ? AND ?) " +
                     "OR (lr.StartDate <= ? AND lr.EndDate >= ?))";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, new java.sql.Date(startDate.getTime()));
            stmt.setDate(2, new java.sql.Date(endDate.getTime()));
            stmt.setDate(3, new java.sql.Date(startDate.getTime()));
            stmt.setDate(4, new java.sql.Date(endDate.getTime()));
            stmt.setDate(5, new java.sql.Date(startDate.getTime()));
            stmt.setDate(6, new java.sql.Date(endDate.getTime()));
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int userId = rs.getInt("UserID");
                String leaveType = rs.getString("LeaveType");
                java.sql.Date sqlStartDate = rs.getDate("StartDate");
                java.sql.Date sqlEndDate = rs.getDate("EndDate");
                
                // Convert to LocalDate
                LocalDate leaveStartDate = sqlStartDate.toLocalDate();
                LocalDate leaveEndDate = sqlEndDate.toLocalDate();
                
                // Adjust dates to be within month
                if (leaveStartDate.isBefore(startOfMonth)) {
                    leaveStartDate = startOfMonth;
                }
                if (leaveEndDate.isAfter(endOfMonth)) {
                    leaveEndDate = endOfMonth;
                }
                
                // Create LeaveDay objects for each day of leave
                for (LocalDate date = leaveStartDate; !date.isAfter(leaveEndDate); date = date.plusDays(1)) {
                    if (date.getMonthValue() == month && date.getYear() == year) {
                        LeaveDay leaveDay = new LeaveDay();
                        leaveDay.setDay(date.getDayOfMonth());
                        leaveDay.setLeaveType(leaveType);
                        
                        if (!leaveRequests.containsKey(userId)) {
                            leaveRequests.put(userId, new ArrayList<>());
                        }
                        leaveRequests.get(userId).add(leaveDay);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving leave requests: " + e.getMessage());
        }
        
        return leaveRequests;
    }
    
    // Inner class for day headers
    public static class DayHeader {
        private int day;
        private String dayOfWeek;
        private boolean weekend;
        
        public int getDay() { return day; }
        public void setDay(int day) { this.day = day; }
        public String getDayOfWeek() { return dayOfWeek; }
        public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
        public boolean isWeekend() { return weekend; }
        public void setWeekend(boolean weekend) { this.weekend = weekend; }
    }
    
    // Inner class for team member
    public static class TeamMember {
        private int userId;
        private String fullName;
        private String department;
        private List<AttendanceDay> attendanceData;
        
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public List<AttendanceDay> getAttendanceData() { return attendanceData; }
        public void setAttendanceData(List<AttendanceDay> attendanceData) { this.attendanceData = attendanceData; }
    }
    
    // Inner class for attendance day
    public static class AttendanceDay {
        private int day;
        private boolean weekend;
        private boolean onLeave;
        private String leaveType;
        
        public int getDay() { return day; }
        public void setDay(int day) { this.day = day; }
        public boolean isWeekend() { return weekend; }
        public void setWeekend(boolean weekend) { this.weekend = weekend; }
        public boolean isOnLeave() { return onLeave; }
        public void setOnLeave(boolean onLeave) { this.onLeave = onLeave; }
        public String getLeaveType() { return leaveType; }
        public void setLeaveType(String leaveType) { this.leaveType = leaveType; }
    }
    
    // Inner class for leave day
    public static class LeaveDay {
        private int day;
        private String leaveType;
        
        public int getDay() { return day; }
        public void setDay(int day) { this.day = day; }
        public String getLeaveType() { return leaveType; }
        public void setLeaveType(String leaveType) { this.leaveType = leaveType; }
    }
}
