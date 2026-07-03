# TaxNova - Expense Control

![License: KickstartFX](https://img.shields.io/badge/License-KickstartFX-lightgrey.svg)

A JavaFX desktop application for managing expenses, revenue, tax calculations, and financial reporting.

---

## Overview

TaxNova is a desktop finance management system designed to support:

- Expense tracking and management
- Revenue and receipt processing
- Tax calculation automation
- PDF financial report generation
- Structured financial data storage and analysis

The system is built as a desktop-first application with a layered architecture and persistent database integration.

---

## Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| JavaFX | 27-ea | UI Framework |
| JDK | 25 | Runtime |
| Gradle | 9 | Build Tool |
| PostgreSQL | 42.7.3 | Database |
| Flyway | 10.10.0 | Database Migration |
| Guice | - | Dependency Injection |
| Lombok | - | Code Generation |
| AtlantaFX | 2.1.0 | UI Theme |

---

## Features

- User authentication (login, register, logout)
- Dashboard with revenue and tax overview
- Expense and receipt management (CRUD operations)
- Tax calculation engine
- PDF report export
- PostgreSQL persistence with Flyway migration support

---

## Project Structure
io.abc_def.kickstart_fx/
├── domain/ → Core models (User, Receipt)
├── login/ → Authentication module
├── dashboard/ → Main dashboard UI
├── revenue/ → Expense and receipt management
├── tax/ → Tax calculation logic
├── reporting/ → PDF export service
├── ai/ → Optional AI module
└── persistence/ → Database access layer


---

## Getting Started

### Prerequisites

- JDK 25
- PostgreSQL (database: taxService)
- Gradle 9

---

## Setup

### 1. Clone repository

bash
git clone https://github.com/your-username/expensecontroll.git
cd expensecontroll
---
. Configure database

Update DatabaseManager.java:

private static final String URL = "jdbc:postgresql://localhost:5432/taxService";
private static final String USER = "postgres";
private static final String PASSWORD = "your_password";

How to Use
English
Run application: ./gradlew run
Login screen appears
Create account (username and password)
Login to access dashboard
Manage expenses, revenue, and tax records


License
This project is based on the KickstartFX template.
https://kickstartfx.xpipe.io/

Notes
Ensure PostgreSQL is running before starting the application
Database schema is initialized automatically via Flyway on first run
UI is built using AtlantaFX styling framework


