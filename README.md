# 🔨 AuctionHub — Real-Time Auction Bidding System

**A full-stack auction platform that enables users to create auctions, place bids in real time and manage wallet transactions.**

AuctionHub is built using Java Spring Boot and React, with WebSocket-based communication for live bidding updates. It combines secure authentication, auction management and transactional wallet operations into a unified platform.

## ✨ Features

- 🔐 **JWT Authentication** — Secure user authentication and protected APIs.
- 👥 **Role-Based Access** — Access control based on user roles.
- 🏷️ **Auction Management** — Create and manage auctions.
- ⚡ **Real-Time Bidding** — Instant bid updates using WebSocket and STOMP.
- 💰 **Wallet Management** — Manage wallet balances and bid reservations.
- 🔒 **Transaction Management** — Maintain consistency during wallet and bidding operations.
- 📊 **Live Auction Updates** — Keep users synchronized with auction activity.

## 🛠️ Tech Stack

| Layer | Technologies |
|---|---|
| Backend | Java 21, Spring Boot |
| Security | Spring Security, JWT |
| Database | MySQL |
| ORM | Spring Data JPA, Hibernate |
| Real-Time Communication | WebSocket, STOMP |
| Frontend | React.js |
| Build Tool | Maven |

## 🏗️ Architecture

```text
           React Frontend
                 │
          REST API / HTTP
                 │
                 ▼
        Spring Boot Backend
                 │
       ┌─────────┼──────────┐
       │         │          │
 Authentication Auction   Wallet
  (JWT)         Service   Service
       │         │          │
       └─────────┼──────────┘
                 │
             Spring JPA
                 │
                 ▼
               MySQL

       WebSocket / STOMP
                 │
                 ▼
        Live Bidding Updates
```

## 🚀 Getting Started

### Prerequisites

- Java 21
- Maven
- Node.js and npm
- MySQL
- Git

### 1. Clone the repository

```bash
git clone https://github.com/gvdkreddy/RealTime-Auction-Bidding-System.git
cd RealTime-Auction-Bidding-System
```

### 2. Configure the database

Create a MySQL database and configure the database connection in the backend's application configuration.

Update the database URL, username and password with your local credentials.

### 3. Run the backend

Navigate to the backend directory and run the Spring Boot application using Maven.

```bash
cd backend
mvn spring-boot:run
```

### 4. Run the frontend

Open another terminal:

```bash
cd frontend
npm install
npm run dev
```

Open the local URL displayed by the frontend development server.

*Note: Configure the backend and frontend connection settings according to your local environment.*

## 🌐 Live Demo

[Launch AuctionHub](https://auction-system-xi-one.vercel.app/)

## 📂 Project Structure

```text
RealTime-Auction-Bidding-System/
│
├── backend/
│   └── Spring Boot application
│
├── frontend/
│   └── React application
│
├── LICENSE
└── README.md
```

## 🔑 Key Engineering Concepts

- RESTful API development using Spring Boot.
- JWT-based authentication and authorization.
- Real-time bid communication using WebSocket/STOMP.
- Transactional wallet reservations.
- Bid validation and consistency during auction operations.
- Relational database management using MySQL and JPA.

## 🤝 Contributing

Contributions, suggestions and bug reports are welcome.

1. Fork the repository.
2. Create a feature branch.
3. Commit your changes.
4. Open a pull request.

## 📄 License

This project is licensed under the Apache License 2.0. See [LICENSE](LICENSE) for details.

---

**Built by [G V D K Reddy](https://github.com/gvdkreddy)**

[GitHub Repository](https://github.com/gvdkreddy/RealTime-Auction-Bidding-System) · [Live Demo](https://auction-system-xi-one.vercel.app/)
