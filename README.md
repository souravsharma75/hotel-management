# Hotel Management System

A backend-based Hotel Management System built using Spring Boot and MySQL.
The application provides hotel and room management, inventory management, hotel search, booking, guest management, JWT-based authentication, Razorpay payment integration, invoice generation, booking cancellation, and dynamic room pricing.

## Features

- Hotel management
- Room management with multiple room types
- One-year inventory generation for rooms
- Hotel search based on city, dates, and room availability
- Hotel and room information retrieval
- JWT-based authentication
- Role-based authorization for Hotel Manager and Guest
- Guest management for bookings
- Booking creation and cancellation
- Inventory availability and concurrency handling
- Automatic expiry of unpaid bookings after 10 minutes with inventory release
- Pessimistic locking for concurrent inventory booking
- Dynamic room pricing
- Razorpay order creation, payment verification, and refund handling
- PDF invoice generation for confirmed bookings
- Global exception handling
- DTO-based API responses
- Pagination for hotel search

## Tech Stack

### Backend

- Java 17
- Spring Boot
- Spring Data JPA
- Spring Security
- Hibernate
- REST APIs
- Maven

### Database

- MySQL

### Authentication & Security

- JWT Authentication
- Role-Based Authorization

### Payment

- Razorpay

### Testing & Development Tools

- Postman
- IntelliJ IDEA
- Git & GitHub

## Project Structure

```text
HM_Project/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/project/hotelmanagement/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── Entity/
│   │   │       ├── exception/
│   │   │       ├── repository/
│   │   │       ├── scheduler/
│   │   │       ├── security/
│   │   │       ├── service/
│   │   │       └── strategy/
│   │   └── resources/
│   │       ├── static/
│   │       └── application-example.properties
│   └── test/
├── screenshots/
├── pom.xml
└── README.md
```
## Important API Endpoints

### Authentication

| Method | Endpoint |
|---|---|
| POST | `/auth/register` |
| POST | `/auth/login` |
| POST | `/auth/register/hotel-manager` |

### Hotel & Room

| Method | Endpoint |
|---|---|
| POST | `/admin/hotels` |
| PATCH | `/admin/hotels/{id}/activate` |
| PATCH | `/admin/hotels/{id}/deactivate` |
| POST | `/admin/hotels/{hotelId}/rooms` |
| POST | `/hotels/search` |
| GET | `/hotels/{hotelId}/info` |

### Booking

| Method | Endpoint |
|---|---|
| POST | `/hotels/bookings/create` |
| POST | `/hotels/bookings/{bookingId}/guests` |
| DELETE | `/hotels/bookings/{bookingId}/cancel` |

### Payment & Invoice

| Method | Endpoint |
|---|---|
| POST | `/payments/create-order/{bookingId}` |
| POST | `/payments/verify` |
| GET | `/hotels/bookings/{bookingId}/invoice` |


## How to Run

### Prerequisites

- Java 17
- Maven
- MySQL
- Razorpay Test Account

### Steps

1. Clone the repository:

```bash
git clone https://github.com/souravsharma75/hotel-management.git
```

2. Open the project in IntelliJ IDEA or another Java IDE.

3. Create a MySQL database or allow the application to create it automatically using the configured JDBC URL.

4. Create `src/main/resources/application.properties` using `application-example.properties` as a reference.

5. Configure your:
   - MySQL username and password
   - JWT secret
   - Razorpay test key ID
   - Razorpay test key secret

6. Run the Spring Boot application.

7. Use Postman to test the REST APIs.

### Application URL

```text
http://localhost:8085/api/v1
```