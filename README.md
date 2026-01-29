# Ứng dụng e-Wallet

Hệ thống quản lý tài chính cá nhân toàn diện được xây dựng với Spring Boot, giúp người dùng theo dõi thu nhập, chi tiêu và quản lý các hoạt động tài chính của mình.

## 🚀 Tính năng

- **Quản lý người dùng**: Đăng ký, xác thực với token JWT
- **Theo dõi thu nhập**: Thêm, xem và quản lý các nguồn thu nhập
- **Quản lý chi tiêu**: Theo dõi chi tiêu hàng ngày với danh mục
- **Dashboard**: Tổng quan về thống kê tài chính
- **Danh mục**: Tổ chức giao dịch với các danh mục tùy chỉnh
- **Thông báo**: Lời nhắc hàng ngày và tóm tắt chi tiêu
- **Email thông báo**: Cảnh báo email tự động (có thể cấu hình)
- **Lọc dữ liệu**: Lọc giao dịch theo khoảng thời gian và từ khóa

## 🛠 Công nghệ sử dụng

- **Backend**: Spring Boot 4.0.2
- **Database**: PostgreSQL (Production) / SQL Server (Development)
- **Xác thực**: Spring Security với JWT
- **ORM**: Spring Data JPA với Hibernate
- **Build Tool**: Maven
- **Java Version**: 21
- **Email**: Spring Mail với tích hợp Brevo
- **Testing**: JUnit 5

## 📋 Yêu cầu

- Java 21 hoặc cao hơn
- Maven 3.6 hoặc cao hơn
- PostgreSQL database (cho production)
- SQL Server (cho development)

## 🚀 Bắt đầu nhanh

1. **Clone repository**
   ```bash
   git clone <repository-url>
   cd e-Wallet
   ```

2. **Cấu hình Database**
   
   Cập nhật `application.properties` cho SQL Server (development):
   ```properties
   spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=eWallet;encrypt=true;trustServerCertificate=true
   spring.datasource.username=sa
   spring.datasource.password=12345
   ```

   Hoặc cập nhật `application-prod.properties` cho PostgreSQL (production):
   ```properties
   spring.datasource.url=jdbc:postgresql://your-postgres-host:5432/ewallet
   spring.datasource.username=your-username
   spring.datasource.password=your-password
   ```

3. **Build và Run**
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

4. **Truy cập ứng dụng**
   - API Base URL: `http://localhost:8080/api/v1`
   - Profile mặc định: Development (SQL Server), Production (PostgreSQL)

## 📁 Cấu trúc Project

```
src/main/java/com/phs/ewallet/
├── controller/          # REST API endpoints
│   ├── CategoryController.java
│   ├── DashboardController.java
│   ├── ExpenseController.java
│   ├── FilterController.java
│   ├── HomeController.java
│   ├── IncomeController.java
│   └── ProfileController.java
├── dto/                # Data Transfer Objects
├── entity/             # JPA Entities
│   ├── Category.java
│   ├── Expense.java
│   ├── Income.java
│   └── Profile.java
├── repository/         # JPA Repositories
├── security/           # Security configuration
├── service/            # Business logic
│   ├── AppUserDetailsService.java
│   ├── CategoryService.java
│   ├── DashboardService.java
│   ├── EmailService.java
│   ├── ExpenseService.java
│   ├── IncomeService.java
│   ├── NotificationService.java
│   └── ProfileService.java
├── util/               # Utility classes
│   └── JwtUtil.java
└── EWalletApplication.java # Main application class
```

## 🔐 Xác thực

Ứng dụng sử dụng JWT (JSON Web Tokens) để xác thực:

1. **Đăng ký**: Tạo tài khoản người dùng mới
2. **Đăng nhập**: Xác thực và nhận JWT token
3. **Truy cập API**: Bao gồm JWT token trong Authorization header cho các endpoint được bảo vệ

## 📊 API Endpoints

### Xác thực
- `POST /api/v1/auth/register` - Đăng ký người dùng mới
- `POST /api/v1/auth/login` - Đăng nhập người dùng

### Quản lý Profile
- `GET /api/v1/profile` - Lấy thông tin profile người dùng hiện tại
- `PUT /api/v1/profile` - Cập nhật profile người dùng

### Danh mục
- `GET /api/v1/categories` - Lấy tất cả danh mục
- `POST /api/v1/categories` - Tạo danh mục mới
- `PUT /api/v1/categories/{id}` - Cập nhật danh mục
- `DELETE /api/v1/categories/{id}` - Xóa danh mục

### Thu nhập
- `GET /api/v1/income` - Lấy tất cả bản ghi thu nhập
- `POST /api/v1/income` - Thêm thu nhập mới
- `DELETE /api/v1/income/{id}` - Xóa bản ghi thu nhập

### Chi tiêu
- `GET /api/v1/expenses` - Lấy tất cả bản ghi chi tiêu
- `POST /api/v1/expenses` - Thêm chi tiêu mới
- `DELETE /api/v1/expenses/{id}` - Xóa bản ghi chi tiêu

### Dashboard
- `GET /api/v1/dashboard` - Lấy tổng quan tài chính

### Lọc
- `POST /api/v1/filter/expenses` - Lọc chi tiêu theo tiêu chí
- `POST /api/v1/filter/income` - Lọc thu nhập theo tiêu chí

## ⚙️ Cấu hình

### Cấu hình Database
- **Development**: SQL Server trên localhost:1433
- **Production**: PostgreSQL (cấu hình trong application-prod.properties)

### Cấu hình Email
- Sử dụng Brevo (Sendinblue) cho thông báo email
- Cấu hình với environment variables:
  - `BREVO_USERNAME`
  - `BREVO_PASSWORD`
  - `BREVO_FROM_EMAIL`

### Tác vụ theo lịch
- **Lời nhắc hàng ngày**: 9:00 PM (múi giờ Việt Nam) - Lời nhắc thêm thu/chi
- **Tóm tắt chi tiêu**: 11:00 PM (múi giờ Việt Nam) - Tóm tắt chi tiêu hàng ngày

## 🧪 Testing

Chạy bộ test:
```bash
./mvnw test
```

## 📝 Environment Variables

Environment variables tùy chọn cho cấu hình:
- `BREVO_USERNAME` - Tên đăng nhập dịch vụ email Brevo
- `BREVO_PASSWORD` - Mật khẩu dịch vụ email Brevo
- `BREVO_FROM_EMAIL` - Địa chỉ email người gửi

- PostgreSQL và SQL Server communities
- Tất cả những người đóng góp đã giúp định hình project này
