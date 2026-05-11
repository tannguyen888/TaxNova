# Expense Controller - Hệ Thống Quản Lý Chi Phí

## 📋 Mục Đích

Expense Controller là một ứng dụng JavaFX dùng để quản lý chi phí, sản phẩm kho, giỏ hàng, và xuất hóa đơn cho khách hàng. Ứng dụng sử dụng PostgreSQL làm cơ sở dữ liệu chính.

## 🏗️ Kiến Trúc Dự Án

```
expensecontroll/kickstartfx/
├── app/
│   ├── src/main/
│   │   ├── java/io/abc_def/kickstart_fx/
│   │   │   ├── domain/              # Entities (User, Receipt, Product, Cart, CartItem)
│   │   │   ├── persistence/         # Repositories (UserRepository, ReceiptRepository, ProductRepository, CartRepository)
│   │   │   ├── page/                # UI Pages (LoginPageComp, DashboardPageComp, InventoryPageComp, etc.)
│   │   │   ├── revenue/             # Revenue services
│   │   │   ├── tax/                 # Tax calculation services
│   │   │   ├── reporting/           # Reporting & Invoice services
│   │   │   ├── login/               # Authentication services
│   │   │   ├── comp/                # UI Components
│   │   │   └── core/                # Core application logic
│   │   └── resources/
│   │       └── db/migration/        # Flyway database migrations
│   └── build.gradle
└── settings.gradle
```

## 🔧 Công Nghệ Sử Dụng

- **JavaFX 20+**: Framework UI
- **PostgreSQL**: Cơ sở dữ liệu
- **Gradle**: Build tool
- **Flyway**: Database migration
- **Lombok**: Reduce boilerplate code
- **AtlantaFX**: Modern UI theme

## 📦 Cài Đặt & Chạy

### Yêu Cầu Hệ Thống

- Java 17+
- PostgreSQL 12+
- Gradle 7+

### Bước 1: Cài Đặt PostgreSQL

```bash
# Tạo database
CREATE DATABASE taxService;

# Kết nối đến database
\c taxService
```

### Bước 2: Cấu Hình Kết Nối

Sửa file `app/dev.properties`:

```properties
io.adbc_def.kickstart_fx.datasource.url=jdbc:postgresql://localhost:5432/taxService
io.adbc_def.kickstart_fx.datasource.port=5432
io.adbc_def.kickstart_fx.datasource.username=postgres
io.adbc_def.kickstart_fx.datasource.password=lab
io.adbc_def.kickstart_fx.datasource.driver-class-name=org.postgresql.Driver
```

### Bước 3: Build Dự Án

```bash
cd expensecontroll/kickstartfx
./gradlew build
```

### Bước 4: Chạy Ứng Dụng

```bash
./gradlew run
```

## 👤 Đăng Nhập

**Tài khoản mặc định:**
- Username: `admin`
- Password: `admin123`

## 📑 Các Tính Năng Chính

### 1. 🔐 Quản Lý Người Dùng
- **LoginPageComp**: Đăng nhập/Đăng ký
- **ProfilePageComp**: Quản lý thông tin cá nhân
- Xác thực và phân quyền người dùng

### 2. 📊 Dashboard
- **DashboardPageComp**: Tổng quan doanh thu
- Biểu đồ thống kê doanh thu theo tháng
- Hiển thị danh sách hóa đơn gần đây

### 3. 💰 Quản Lý Thu Nhập
- **TaxPageComp**: Quản lý thuế
- **ReceiptsPageComp**: Danh sách hóa đơn
- **RevenueController**: Xử lý doanh thu

### 4. 📦 Quản Lý Kho (MỚI)
- **InventoryPageComp**: Quản lý sản phẩm trong kho
- Thêm/Sửa/Xóa sản phẩm
- Tìm kiếm sản phẩm theo mã hoặc tên
- Lọc sản phẩm theo loại
- Hiển thị tổng số lượng và giá trị kho

### 5. 🛒 Giỏ Hàng (MỚI)
- **ShoppingCartPageComp**: Quản lý giỏ hàng
- Thêm sản phẩm vào giỏ
- Xóa sản phẩm khỏi giỏ
- Tính toán tổng tiền, thuế, và tổng thanh toán
- Xuất hóa đơn từ giỏ hàng

### 6. 📄 Quản Lý Hóa Đơn (MỚI)
- **InvoicePageComp**: Danh sách hóa đơn đã xuất
- Xem chi tiết hóa đơn
- In hoặc xuất PDF hóa đơn
- Lịch sử hóa đơn theo ngày

### 7. 📈 Báo Cáo
- **ReportsPageComp**: Báo cáo doanh thu
- Biểu đồ theo tháng/năm
- Phân tích thuế theo loại

## 🗄️ Cấu Trúc Database

### Users Table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'USER'
);
```

### Products Table
```sql
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DOUBLE PRECISION NOT NULL,
    quantity BIGINT DEFAULT 0,
    category VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Carts Table
```sql
CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'ACTIVE'
);
```

### Cart Items Table
```sql
CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT REFERENCES carts(id),
    product_id BIGINT REFERENCES products(id),
    product_code VARCHAR(50),
    product_name VARCHAR(255),
    unit_price DOUBLE PRECISION NOT NULL,
    quantity BIGINT NOT NULL
);
```

### Invoices Table
```sql
CREATE TABLE invoices (
    id BIGSERIAL PRIMARY KEY,
    invoice_number VARCHAR(50) UNIQUE,
    user_id BIGINT REFERENCES users(id),
    cart_id BIGINT REFERENCES carts(id),
    total_amount DOUBLE PRECISION NOT NULL,
    tax_amount DOUBLE PRECISION DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'COMPLETED'
);
```

## 📝 Hướng Dẫn Thêm Tính Năng / Trang Mới

### Thêm Trang Mới

#### 1. Tạo Entity (Domain)
Tạo file mới trong `src/main/java/io/abc_def/kickstart_fx/domain/`:

```java
package io.abc_def.kickstart_fx.domain;

import javafx.beans.property.*;

public class YourEntity {
    private SimpleLongProperty id;
    private SimpleStringProperty name;

    public YourEntity() {
        this.id = new SimpleLongProperty();
        this.name = new SimpleStringProperty();
    }

    // Getters and Setters...
}
```

#### 2. Tạo Repository
Tạo file mới trong `src/main/java/io/abc_def/kickstart_fx/persistence/`:

```java
package io.abc_def.kickstart_fx.persistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class YourEntityRepository {
    private final DatabaseManager databaseManager;

    public YourEntityRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public List<YourEntity> findAll() {
        List<YourEntity> list = new ArrayList<>();
        String sql = "SELECT * FROM your_entity_table";
        try (Statement stmt = databaseManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // Map resultset to entity
                list.add(mapRowToEntity(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return list;
    }

    public void save(YourEntity entity) {
        // Implementation for save logic
    }

    private YourEntity mapRowToEntity(ResultSet rs) throws SQLException {
        // Map database columns to entity properties
        return new YourEntity();
    }
}
```

#### 3. Tạo Service
Tạo file mới trong `src/main/java/io/abc_def/kickstart_fx/domain/` hoặc thư mục thích hợp:

```java
package io.abc_def.kickstart_fx.domain;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class YourEntityService {
    private final YourEntityRepository repository;

    public YourEntityService(YourEntityRepository repository) {
        this.repository = repository;
    }

    public ObservableList<YourEntity> getAllEntities() {
        return FXCollections.observableArrayList(repository.findAll());
    }

    public void saveEntity(YourEntity entity) {
        repository.save(entity);
    }
}
```

#### 4. Tạo UI Page Component
Tạo file mới trong `src/main/java/io/abc_def/kickstart_fx/page/`:

```java
package io.abc_def.kickstart_fx.page;

import io.abc_def.kickstart_fx.comp.SimpleComp;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class YourPageComp extends SimpleComp {

    @Override
    protected Region createSimple() {
        VBox root = new VBox();
        root.setStyle("-fx-font-family:'Segoe UI'; -fx-background-color:#f5f7fa;");

        root.getChildren().add(createHeader());
        root.getChildren().add(createContent());

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        return scroll;
    }

    private VBox createHeader() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(30));
        box.setStyle("-fx-background-color:linear-gradient(to right,#1976d2,#1565c0);");

        Label title = new Label("Your Page Title");
        title.setStyle("-fx-font-size:32; -fx-text-fill:white; -fx-font-weight:bold;");

        Label sub = new Label("Your Page Subtitle");
        sub.setStyle("-fx-text-fill:white; -fx-font-size:14;");

        box.getChildren().addAll(title, sub);
        return box;
    }

    private VBox createContent() {
        VBox content = new VBox();
        content.setPadding(new Insets(20));
        // Add your UI components here
        return content;
    }
}
```

#### 5. Đăng Ký Page trong Navigation
Tìm file navigation/menu và thêm trang mới vào menu

#### 6. Cập Nhật Database Schema
Tạo file migration mới trong `src/main/resources/db/migration/`:

```sql
-- V2__your_migration.sql
CREATE TABLE IF NOT EXISTS your_entity_table (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🚀 Tối Ưu Hóa Hiệu Suất

### Query Optimization
- Sử dụng **indexed columns** cho WHERE clauses
- Sử dụng **prepared statements** để tránh SQL injection
- Limit results với LIMIT clause khi không cần all data

### Memory Management
- **DatabaseManager** xử lý connection pooling
- Tất cả resources đều được đóng đúng cách
- Use try-with-resources cho statements và result sets
- Gọi `dispose()` và `clear()` cho UI components khi không cần dùng

### UI Performance
- Tải dữ liệu trong background thread
- Dùng `Platform.runLater()` để update UI từ threads
- Cache dữ liệu khi có thể
- Sử dụng lazy loading cho danh sách lớn

## 🐛 Xử Lý Lỗi

Toàn bộ repositories và services đều có:
- Try-catch blocks để handle SQLException
- Proper logging với `System.err.println()`
- Meaningful error messages cho người dùng
- Transaction rollback khi có lỗi

## 📋 Checklist Hoàn Thành

- [x] Tạo entities (Product, Cart, CartItem)
- [x] Tạo repositories (ProductRepository, CartRepository)
- [x] Tạo services (ProductService, CartService, InvoiceService)
- [x] Cập nhật database schema
- [x] Tạo Inventory Management page
- [x] Tạo Shopping Cart page
- [x] Tạo Invoice Management page
- [x] Tối ưu hóa DatabaseManager
- [x] Viết README
- [ ] Test build
- [ ] Deploy

## 🔐 Bảo Mật

- Mật khẩu được hash bằng simple hash (nên upgrade lên bcrypt/scrypt)
- SQL injection đã được ngăn chặn bằng prepared statements
- Session management nên được implement

## 📞 Support

Nếu gặp lỗi:
1. Kiểm tra PostgreSQL service đang chạy
2. Kiểm tra connection string trong `dev.properties`
3. Kiểm tra Flyway migrations đã chạy
4. Xem logs trong console

## 📄 License

MIT License - Xem LICENSE file
