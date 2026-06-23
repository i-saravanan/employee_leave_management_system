# 🏢 Employee Leave Management System

<div align="center">

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Auth-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)

<br/>

**A robust, production-ready REST API for managing employee leave requests with role-based access control, automatic leave balance tracking, and a complete leave lifecycle.**

[🚀 Getting Started](#-getting-started) · [📖 API Reference](#-api-endpoints) · [🏗️ Architecture](#️-architecture) · [📊 Database Schema](#-database-schema)

---

</div>

## ✨ Key Features

<table>
<tr>
<td width="50%">

### 🔐 Role-Based Access Control
Three distinct roles with granular permissions:
- **Employee** — Apply, view & cancel leaves
- **Manager** — Approve/reject team leaves
- **Admin** — Full system control

</td>
<td width="50%">

### 📋 Leave Lifecycle Management
Complete leave workflow:

```
Applied → Approved ✅
       → Rejected ❌
       → Cancelled 🚫
```

</td>
</tr>
<tr>
<td width="50%">

### 📊 Automatic Balance Tracking
Smart leave balance system:
- **Casual Leave** — 12 days/year
- **Sick Leave** — 10 days/year
- **Earned Leave** — 15 days/year

Balances auto-deduct on approval!

</td>
<td width="50%">

### 🔑 JWT Authentication
Secure, stateless authentication:
- Token-based session management
- BCrypt password hashing
- Role-based endpoint protection
- Auto-expiring tokens (24h)

</td>
</tr>
</table>

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT (Postman)                         │
└──────────────────────────────┬──────────────────────────────────┘
                               │ HTTP Requests
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                    🔐 JWT AUTHENTICATION FILTER                 │
│              Validates token → Sets SecurityContext             │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                     📡 CONTROLLER LAYER                         │
│                                                                 │
│   AuthController    EmployeeController    LeaveController       │
│   /api/auth/*       /api/employee/*       /api/manager/*        │
│                                                                 │
│                         AdminController                         │
│                         /api/admin/*                            │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                      ⚙️ SERVICE LAYER                           │
│           Business Logic · Validation · Balance Tracking        │
│                                                                 │
│     AuthService    LeaveService    EmployeeService              │
│                       AdminService                              │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                    🗄️ REPOSITORY LAYER                          │
│                  Spring Data JPA · Hibernate                    │
│                                                                 │
│  EmployeeRepository  LeaveApplicationRepository                 │
│                    LeaveBalanceRepository                       │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                     🐬 MySQL DATABASE                           │
│          employees · leave_applications · leave_balances        │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
src/main/java/com/leavemanagement/
│
├── 📄 LeaveManagementApplication.java     # Main entry point
│
├── 🔧 config/
│   └── SecurityConfig.java                # Spring Security & JWT filter chain
│
├── 📡 controller/
│   ├── AuthController.java                # Login & Registration
│   ├── EmployeeController.java            # Employee self-service
│   ├── LeaveController.java               # Manager leave actions
│   └── AdminController.java               # Admin management
│
├── 📦 dto/
│   ├── request/                           # Incoming request bodies
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── LeaveRequest.java
│   │   └── LeaveActionRequest.java
│   └── response/                          # Outgoing response bodies
│       ├── ApiResponse.java
│       ├── AuthResponse.java
│       ├── EmployeeResponse.java
│       ├── LeaveResponse.java
│       └── LeaveBalanceResponse.java
│
├── 🗃️ entity/
│   ├── Employee.java                      # User entity with roles
│   ├── LeaveApplication.java              # Leave request entity
│   └── LeaveBalance.java                  # Per-type yearly balance
│
├── 📋 enums/
│   ├── Role.java                          # EMPLOYEE, MANAGER, ADMIN
│   ├── LeaveType.java                     # CASUAL, SICK, EARNED
│   └── LeaveStatus.java                   # APPLIED, APPROVED, REJECTED, CANCELLED
│
├── ❌ exception/
│   ├── GlobalExceptionHandler.java        # Centralized error handling
│   ├── ResourceNotFoundException.java
│   ├── InsufficientLeaveBalanceException.java
│   └── InvalidOperationException.java
│
├── 🗄️ repository/
│   ├── EmployeeRepository.java
│   ├── LeaveApplicationRepository.java
│   └── LeaveBalanceRepository.java
│
├── 🔐 security/
│   ├── JwtTokenProvider.java              # Token generation & validation
│   ├── JwtAuthenticationFilter.java       # Request filter
│   └── CustomUserDetailsService.java      # User loading
│
└── ⚙️ service/
    ├── AuthService.java                   # Registration & login logic
    ├── EmployeeService.java               # Profile management
    ├── LeaveService.java                  # Core leave business logic
    └── AdminService.java                  # Employee CRUD operations
```

---

## 📊 Database Schema

```
┌──────────────────────┐       ┌───────────────────────────┐
│      employees       │       │    leave_applications     │
├──────────────────────┤       ├───────────────────────────┤
│ id          BIGINT   │──┐    │ id             BIGINT     │
│ name        VARCHAR  │  │    │ employee_id    BIGINT  FK─┤──►
│ email       VARCHAR  │  │    │ leave_type     ENUM       │
│ password    VARCHAR  │  │    │ start_date     DATE       │
│ role        ENUM     │  │    │ end_date       DATE       │
│ department  VARCHAR  │  │    │ number_of_days INT        │
│ manager_id  BIGINT   │──┘    │ reason         VARCHAR    │
│ joining_date DATE    │  ▲    │ status         ENUM       │
│ active      BOOLEAN  │  │    │ manager_remarks VARCHAR   │
└──────────────────────┘  │    │ applied_date   DATETIME   │
         │ self-ref        │    │ action_date    DATETIME   │
         └────────────────┘    └───────────────────────────┘
                                           
┌───────────────────────────┐              
│      leave_balances       │              
├───────────────────────────┤              
│ id             BIGINT     │              
│ employee_id    BIGINT  FK─┤──► employees
│ leave_type     ENUM       │              
│ total_leaves   INT        │              
│ used_leaves    INT        │              
│ year           INT        │              
│                           │              
│ UNIQUE(employee_id,       │              
│   leave_type, year)       │              
└───────────────────────────┘              
```

---

## 📖 API Endpoints

### 🔓 Authentication (`/api/auth`)

| Method | Endpoint | Description | Access |
|:------:|----------|-------------|:------:|
| `POST` | `/api/auth/register` | Register a new user | Public |
| `POST` | `/api/auth/login` | Login & get JWT token | Public |

### 👤 Employee (`/api/employee`)

| Method | Endpoint | Description | Access |
|:------:|----------|-------------|:------:|
| `GET` | `/api/employee/profile` | View own profile | 🟢 Employee+ |
| `POST` | `/api/employee/leave/apply` | Apply for leave | 🟢 Employee+ |
| `GET` | `/api/employee/leave/history` | View leave history | 🟢 Employee+ |
| `GET` | `/api/employee/leave/balance` | Check leave balances | 🟢 Employee+ |
| `PUT` | `/api/employee/leave/cancel/{id}` | Cancel pending leave | 🟢 Employee+ |

### 👨‍💼 Manager (`/api/manager`)

| Method | Endpoint | Description | Access |
|:------:|----------|-------------|:------:|
| `GET` | `/api/manager/leave/pending` | View pending approvals | 🟡 Manager+ |
| `PUT` | `/api/manager/leave/approve/{id}` | Approve leave request | 🟡 Manager+ |
| `PUT` | `/api/manager/leave/reject/{id}` | Reject leave request | 🟡 Manager+ |
| `GET` | `/api/manager/team` | View team members | 🟡 Manager+ |
| `GET` | `/api/manager/leave/team` | View all team leaves | 🟡 Manager+ |

### 🔴 Admin (`/api/admin`)

| Method | Endpoint | Description | Access |
|:------:|----------|-------------|:------:|
| `GET` | `/api/admin/employees` | List all employees | 🔴 Admin |
| `GET` | `/api/admin/employees/{id}` | Get employee by ID | 🔴 Admin |
| `POST` | `/api/admin/employees` | Create new employee | 🔴 Admin |
| `PUT` | `/api/admin/employees/{id}` | Update employee | 🔴 Admin |
| `DELETE` | `/api/admin/employees/{id}` | Deactivate employee | 🔴 Admin |
| `GET` | `/api/admin/leaves` | View all leave data | 🔴 Admin |
| `GET` | `/api/admin/leaves?status=APPLIED` | Filter leaves by status | 🔴 Admin |

---

## 🚀 Getting Started

### Prerequisites

| Tool | Version |
|------|---------|
| Java JDK | 17+ |
| Maven | 3.8+ |
| MySQL | 8.0+ |

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/i-saravanan/employee_leave_management_system.git
cd employee_leave_management_system
```

### 2️⃣ Set Up the Database

```sql
CREATE DATABASE leave_management_db;
```

### 3️⃣ Configure Application

Update `src/main/resources/application.properties` with your MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/leave_management_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

### 4️⃣ Build & Run

```bash
mvn clean install
mvn spring-boot:run
```

The application starts at **`http://localhost:8080`** 🎉

---

## 🧪 Testing with Postman

### Step 1 — Login as Admin

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "admin@company.com",
  "password": "password123"
}
```

### Step 2 — Copy the JWT Token from Response

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "email": "admin@company.com",
    "role": "ROLE_ADMIN",
    "name": "Admin User"
  }
}
```

### Step 3 — Use Token in Authorization Header

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Step 4 — Apply for Leave (as Employee)

```http
POST http://localhost:8080/api/employee/leave/apply
Authorization: Bearer <employee_token>
Content-Type: application/json

{
  "leaveType": "CASUAL",
  "startDate": "2026-07-10",
  "endDate": "2026-07-12",
  "reason": "Family function"
}
```

### Step 5 — Approve Leave (as Manager)

```http
PUT http://localhost:8080/api/manager/leave/approve/1
Authorization: Bearer <manager_token>
Content-Type: application/json

{
  "remarks": "Approved. Enjoy your time off!"
}
```

---

## 🔐 Default User Credentials

| Role | Email | Password |
|:----:|-------|:--------:|
| 🔴 Admin | `admin@company.com` | `password123` |
| 🟡 Manager | `manager@company.com` | `password123` |
| 🟢 Employee | `employee@company.com` | `password123` |

> ⚠️ **Note:** Change these credentials in production environments.

---

## 🛠️ Tech Stack

<div align="center">

| Layer | Technology |
|-------|-----------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.2.5 |
| **Security** | Spring Security + JWT (jjwt 0.12.5) |
| **ORM** | Spring Data JPA / Hibernate |
| **Database** | MySQL 8.0 |
| **Validation** | Jakarta Bean Validation |
| **Build Tool** | Maven |
| **Boilerplate** | Lombok |
| **API Testing** | Postman |

</div>

---

## 🔄 Leave Lifecycle Flow

```
                    ┌──────────┐
                    │ Employee │
                    │  applies │
                    └────┬─────┘
                         │
                         ▼
                  ┌──────────────┐
                  │   APPLIED    │◄──── Initial Status
                  └──────┬───────┘
                         │
              ┌──────────┼──────────┐
              │          │          │
              ▼          ▼          ▼
       ┌──────────┐ ┌──────────┐ ┌───────────┐
       │ APPROVED │ │ REJECTED │ │ CANCELLED │
       │    ✅    │ │    ❌   │ │    🚫     │
       └──────────┘ └──────────┘ └───────────┘
       by Manager   by Manager   by Employee
       (balance     (no balance  (before
        deducted)    change)      action)
```

---

## 📈 Future Enhancements

- [ ] 📧 Email notifications for leave status changes
- [ ] 🔑 Forgot Password / Password Reset flow
- [ ] 📅 Holiday calendar integration
- [ ] 📊 Leave analytics dashboard
- [ ] 🔔 Real-time notifications via WebSocket
- [ ] 📱 Frontend UI (React / Angular)
- [ ] 🧪 Unit & Integration tests
- [ ] 🐳 Docker containerization

---

## 🤝 Contributing

Contributions are welcome! Feel free to open issues and submit pull requests.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

<div align="center">

**Built with ❤️ using Spring Boot**

⭐ Star this repository if you found it helpful!

</div>
