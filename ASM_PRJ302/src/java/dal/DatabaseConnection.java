package dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Chuỗi kết nối với SQL Server Express instance SQLEXPRESS
    private static final String DB_URL = "jdbc:sqlserver://LAPTOP-PH5V39NT\\SQLEXPRESS:1433;databaseName=Helios;trustServerCertificate=true";
    private static final String DB_USER = "than";
    private static final String DB_PASSWORD = "12345";

    public static Connection getConnection() throws SQLException {
        try {
            // Tải driver JDBC cho SQL Server (bắt buộc để kết nối)
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Kết nối tới SQL Server thành công!");
            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println("Lỗi: Driver JDBC cho SQL Server không được tìm thấy - " + e.getMessage());
            throw new SQLException("Driver JDBC không được tải. Vui lòng kiểm tra thư viện mssql-jdbc.", e);
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối tới SQL Server. Chuỗi kết nối: " + DB_URL);
            System.err.println("Lỗi chi tiết: " + e.getMessage());
            throw e;
        }
    }

    public static void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
                System.out.println("Kết nối tới SQL Server đã được đóng.");
            } catch (SQLException e) {
                System.err.println("Lỗi khi đóng kết nối tới SQL Server: " + e.getMessage());
                // Không ném ngoại lệ ra ngoài để tránh ảnh hưởng đến luồng chính
            }
        }
    }
}