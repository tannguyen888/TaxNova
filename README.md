# TaxNova - Expense Control

A JavaFX desktop application for managing expenses, revenue, and tax calculations.

## Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| JavaFX | 27-ea | UI Framework |
| JDK | 25 | Java Version |
| Gradle | 9 | Build Tool |
| PostgreSQL | 42.7.3 | Database |
| Flyway | 10.10.0 | DB Migration |
| Guice | - | Dependency Injection |
| Lombok | - | Code Generation |
| AtlantaFX | 2.1.0 | UI Theme |

## Features

- 🔐 User authentication with login/logout
- 📊 Dashboard with revenue and tax summary
- 💰 Receipt management (add, delete, view)
- 🧾 Tax calculation engine
- 📄 PDF report export
- 🤖 AI chat assistant
- 🗄 PostgreSQL database with auto migration

## Project Structure
```
io.abc_def.kickstart_fx/
├── domain/         → User, Receipt models
├── login/          → Authentication
├── dashboard/      → Summary view
├── revenue/        → Receipt management
├── tax/            → Tax calculation
├── reporting/      → PDF export
├── ai/             → AI chat
└── persistence/    → Database layer
```

## Getting Started

### Prerequisites
- JDK 25
- PostgreSQL (database: `taxService`)
- Gradle 9

### Setup

1. Clone the repository
```bash
git clone https://github.com/your-username/expensecontroll.git
cd expensecontroll
```

2. Configure database connection in `DatabaseManager.java`
```java
private static final String URL = "jdbc:postgresql://localhost:5432/taxService";
private static final String USER = "postgres";
private static final String PASSWORD = "your_password";
```

3. Run the application
```bash
./gradlew run
```

Database tables will be created automatically on first run via Flyway migration.

## Build
```bash
./gradlew clean build
```


run app // VietNamese
1. Chạy app: ./gradlew run
2. Login page hiển thị (yêu cầu tài khoản)
3. Đăng ký account: username + password
4. Sau khi đăng nhập → Vào Dashboard
5. Quản lý chi phí, thuế, hóa đơn
## License

//ENG
1. Run the app: ./gradlew run
2. Login page displayed (account required)
3. Register account: username + password
4. After logging in → Go to Dashboard
5. Manage expenses, taxes, invoices
## License

This project is based on [KickstartFX](https://kickstartfx.xpipe.io/) template.
