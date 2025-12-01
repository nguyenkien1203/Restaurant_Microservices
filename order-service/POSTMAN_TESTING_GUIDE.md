# Order Service API Testing Guide

This guide provides complete instructions for testing the Order Service endpoints using Postman.

## 📋 Import the Collection

1. Open Postman
2. Click **Import** button
3. Select the file: `Order-Service-Postman-Collection.json`
4. The collection will be imported with all endpoints organized by role

## 🔧 Setup Environment Variables

The collection uses the following variables that you can configure:

| Variable | Default Value | Description |
|----------|---------------|-------------|
| `base_url` | `http://localhost:8083` | Order Service base URL |
| `jwt_token` | _(empty)_ | JWT token for authenticated users |
| `admin_jwt_token` | _(empty)_ | JWT token for admin user |
| `kitchen_jwt_token` | _(empty)_ | JWT token for kitchen staff |
| `driver_jwt_token` | _(empty)_ | JWT token for driver |

### How to Set Variables:

1. In Postman, right-click on the collection name
2. Select **Edit**
3. Go to **Variables** tab
4. Update the values as needed

## 🔐 Authentication

Most endpoints require authentication. Here's how to obtain JWT tokens:

### 1. Get JWT Token from Auth Service

First, authenticate with the Auth Service (typically running on port 8081):

```
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
  "username": "your_username",
  "password": "your_password"
}
```

### 2. Set the Token

Copy the JWT token from the response and:
- For regular users: Set it in `{{jwt_token}}`
- For admin users: Set it in `{{admin_jwt_token}}`
- For kitchen staff: Set it in `{{kitchen_jwt_token}}`
- For drivers: Set it in `{{driver_jwt_token}}`

## 📚 API Endpoints Overview

### 👤 Customer Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/orders` | Create new order | ✅ Yes |
| POST | `/api/orders/guest` | Create guest order | ❌ No |
| GET | `/api/orders/{id}` | Get order by ID | ✅ Yes |
| GET | `/api/orders/my-orders` | Get current user's orders | ✅ Yes |
| PUT | `/api/orders/{id}` | Update order (before confirmed) | ✅ Yes |
| DELETE | `/api/orders/{id}` | Cancel order | ✅ Yes |
| POST | `/api/orders/pre-order/{reservationId}` | Create pre-order for reservation | ✅ Yes |

### 👨‍💼 Admin Endpoints

| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| GET | `/api/orders` | Get all orders | ADMIN |
| PATCH | `/api/orders/{id}/status` | Update order status | ADMIN, KITCHEN |
| PATCH | `/api/orders/{id}/assign-driver` | Assign driver to order | ADMIN |

### 👨‍🍳 Kitchen Endpoints

| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| GET | `/api/orders/kitchen-queue` | Get kitchen queue | ADMIN, KITCHEN |
| PATCH | `/api/orders/{id}/status` | Update order status | ADMIN, KITCHEN |

### 🚗 Driver Endpoints

| Method | Endpoint | Description | Required Role |
|--------|----------|-------------|---------------|
| GET | `/api/orders/driver/assigned` | Get assigned orders | DRIVER |
| PATCH | `/api/orders/{id}/out-for-delivery` | Mark order out for delivery | DRIVER |
| PATCH | `/api/orders/{id}/delivered` | Mark order as delivered | DRIVER |

## 📝 Request Examples

### 1. Create a Delivery Order

```json
POST /api/orders
Authorization: Bearer {{jwt_token}}
Content-Type: application/json

{
  "orderType": "DELIVERY",
  "items": [
    {
      "menuItemId": 1,
      "quantity": 2,
      "notes": "No onions please"
    },
    {
      "menuItemId": 2,
      "quantity": 1,
      "notes": "Extra spicy"
    }
  ],
  "paymentMethod": "CASH",
  "deliveryAddress": "123 Main Street, District 1, Ho Chi Minh City",
  "notes": "Please ring the doorbell twice"
}
```

### 2. Create a Guest Order

```json
POST /api/orders/guest
Content-Type: application/json

{
  "orderType": "TAKEAWAY",
  "items": [
    {
      "menuItemId": 1,
      "quantity": 1
    }
  ],
  "paymentMethod": "CASH",
  "guestEmail": "guest@example.com",
  "guestPhone": "0901234567",
  "guestName": "John Doe",
  "notes": "I'll pick up at 6 PM"
}
```

### 3. Update Order Status (Admin/Kitchen)

```json
PATCH /api/orders/1/status
Authorization: Bearer {{admin_jwt_token}}
Content-Type: application/json

{
  "newStatus": "CONFIRMED",
  "reason": "Order confirmed by admin"
}
```

### 4. Assign Driver (Admin)

```json
PATCH /api/orders/1/assign-driver
Authorization: Bearer {{admin_jwt_token}}
Content-Type: application/json

{
  "driverId": 5
}
```

## 🔄 Order Status Flow

The order status follows this lifecycle:

```
PENDING → CONFIRMED → PREPARING → READY → OUT_FOR_DELIVERY → DELIVERED
                                      ↓
                                  COMPLETED (for DINE_IN)
        ↓
    CANCELLED (can be cancelled at any stage before DELIVERED)
```

### Status Descriptions:

- **PENDING**: Order placed, awaiting confirmation
- **CONFIRMED**: Order confirmed by restaurant
- **PREPARING**: Kitchen is preparing the order
- **READY**: Order is ready for pickup/delivery
- **OUT_FOR_DELIVERY**: Driver is delivering the order
- **DELIVERED**: Order delivered to customer
- **COMPLETED**: Order fully completed (for dine-in)
- **CANCELLED**: Order was cancelled

## 📦 Order Types

The service supports four order types:

1. **DINE_IN**: Eat at restaurant, linked to reservation
2. **PRE_ORDER**: Pre-order for future reservation
3. **TAKEAWAY**: Customer picks up from restaurant
4. **DELIVERY**: Delivered to customer address

## 💳 Payment Methods

Available payment methods:

- **CASH**: Cash on delivery/pickup
- **CARD**: Credit/Debit card
- **DIGITAL_WALLET**: Digital wallet (e.g., VNPAY, MoMo)

## ✅ Testing Workflow

### Basic Customer Flow:

1. **Create Order**: POST `/api/orders`
   - Copy the order ID from response
   
2. **View Order**: GET `/api/orders/{id}`
   - Verify order details
   
3. **View My Orders**: GET `/api/orders/my-orders`
   - See all your orders

4. **Update Order**: PUT `/api/orders/{id}`
   - Only works before order is confirmed
   
5. **Cancel Order**: DELETE `/api/orders/{id}`
   - Cancel if needed

### Admin/Kitchen Flow:

1. **View All Orders**: GET `/api/orders`
   
2. **View Kitchen Queue**: GET `/api/orders/kitchen-queue`
   
3. **Confirm Order**: PATCH `/api/orders/{id}/status`
   ```json
   {"newStatus": "CONFIRMED"}
   ```
   
4. **Start Preparing**: PATCH `/api/orders/{id}/status`
   ```json
   {"newStatus": "PREPARING"}
   ```
   
5. **Mark Ready**: PATCH `/api/orders/{id}/status`
   ```json
   {"newStatus": "READY"}
   ```

6. **Assign Driver** (for delivery): PATCH `/api/orders/{id}/assign-driver`
   ```json
   {"driverId": 5}
   ```

### Driver Flow:

1. **View Assigned Orders**: GET `/api/orders/driver/assigned`
   
2. **Start Delivery**: PATCH `/api/orders/{id}/out-for-delivery`
   
3. **Complete Delivery**: PATCH `/api/orders/{id}/delivered`

## 🐛 Troubleshooting

### Common Issues:

1. **401 Unauthorized**
   - Ensure you have a valid JWT token set in the environment variables
   - Check if the token has expired

2. **403 Forbidden**
   - Verify you have the correct role for the endpoint
   - Admin endpoints require ADMIN role
   - Kitchen endpoints require KITCHEN or ADMIN role
   - Driver endpoints require DRIVER role

3. **404 Not Found**
   - Check if the order ID exists
   - Verify the order service is running on the correct port

4. **400 Bad Request**
   - Validate your request body matches the required format
   - Ensure all required fields are present
   - Check that menuItemId references valid menu items

5. **Connection Refused**
   - Verify the order service is running
   - Check the port number (default: 8083)
   - Ensure the database is accessible

## 📊 Sample Test Cases

### Test Case 1: Complete Order Lifecycle

1. Create a delivery order
2. Admin confirms the order
3. Kitchen marks as preparing
4. Kitchen marks as ready
5. Admin assigns driver
6. Driver marks out for delivery
7. Driver marks as delivered

### Test Case 2: Guest Order

1. Create guest order without authentication
2. Verify order was created successfully
3. Note: Guest cannot view/update the order after creation

### Test Case 3: Order Cancellation

1. Create an order
2. Cancel the order before it's confirmed
3. Verify order status is CANCELLED

### Test Case 4: Pre-Order

1. Create a reservation first (using Reservation Service)
2. Create a pre-order linked to the reservation
3. Verify orderType is PRE_ORDER
4. Verify reservationId is set correctly

## 🔗 Related Services

The Order Service interacts with:

- **Auth Service** (Port 8081): For authentication
- **Menu Service** (Port 8082): To validate menu items and get pricing
- **Reservation Service** (Port 8085): For pre-orders and dine-in orders

## 📝 Notes

- Make sure all required services are running before testing
- Update the base_url if services are running on different ports
- Some endpoints may require specific order states to work (e.g., can't update a confirmed order)
- Test with different user roles to verify authorization is working correctly

## 🎯 Quick Start Commands

If you need to start the Order Service:

```bash
cd /Users/vonguyenkien/Workspaces/VNPAY_Microservices/order-service
./gradlew bootRun
```

Or using Docker:

```bash
docker-compose up order-service
```

---

**Happy Testing! 🚀**
