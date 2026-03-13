# ActifyTask API Guide (Team Share)

## Base URLs
- Production (Render): `https://actifytask-api.onrender.com`
- Local: `http://localhost:8082`

## Auth & Roles
- JWT auth is required for all routes except `/api/auth/login`.
- Role routing:
  - `/api/admin/**` -> `ADMIN`
  - `/api/manager/**` -> `MANAGER`
  - `/api/user/**` -> any authenticated user

## Seeded Users (already available)
- Admin: `admin@actify.com` / `Admin@123`
- Manager: `manager@actify.com` / `Manager@123`
- User: `user@actify.com` / `User@1234`

---

## 1) Login
### POST `/api/auth/login`
**Request**
```json
{
  "email": "admin@actify.com",
  "password": "Admin@123"
}
```

**Response**
```json
{
  "token": "<jwt-token>"
}
```

**cURL**
```bash
curl -X POST 'https://actifytask-api.onrender.com/api/auth/login' \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@actify.com","password":"Admin@123"}'
```

---

## 2) Admin APIs (`/api/admin/users`) [ADMIN token]

### 2.1 Create User (insert user)
### POST `/api/admin/users`
**Request**
```json
{
  "name": "John Doe",
  "email": "john.doe@actify.com",
  "password": "JohnDoe@123",
  "roles": ["USER"]
}
```

> Valid roles: `ADMIN`, `MANAGER`, `USER`

**cURL**
```bash
ADMIN_TOKEN='<paste-admin-token>'

curl -X POST 'https://actifytask-api.onrender.com/api/admin/users' \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "name":"John Doe",
    "email":"john.doe@actify.com",
    "password":"JohnDoe@123",
    "roles":["USER"]
  }'
```

### 2.2 Get All Users
### GET `/api/admin/users`
```bash
curl -X GET 'https://actifytask-api.onrender.com/api/admin/users' \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

### 2.3 Get User By ID
### GET `/api/admin/users/{id}`
```bash
curl -X GET 'https://actifytask-api.onrender.com/api/admin/users/3' \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

### 2.4 Update User
### PUT `/api/admin/users/{id}`
**Request**
```json
{
  "name": "John D",
  "email": "john.d@actify.com"
}
```

```bash
curl -X PUT 'https://actifytask-api.onrender.com/api/admin/users/3' \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"name":"John D","email":"john.d@actify.com"}'
```

### 2.5 Assign Roles (insert role mapping)
### PUT `/api/admin/users/{id}/roles`
**Request**
```json
{
  "roles": ["MANAGER", "USER"]
}
```

```bash
curl -X PUT 'https://actifytask-api.onrender.com/api/admin/users/3/roles' \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"roles":["MANAGER","USER"]}'
```

### 2.6 Delete User
### DELETE `/api/admin/users/{id}`
```bash
curl -X DELETE 'https://actifytask-api.onrender.com/api/admin/users/5' \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

---

## 3) Manager APIs (`/api/manager`) [MANAGER token]

### 3.1 Get Users With Tasks
### GET `/api/manager/users`
```bash
MANAGER_TOKEN='<paste-manager-token>'

curl -X GET 'https://actifytask-api.onrender.com/api/manager/users' \
  -H "Authorization: Bearer $MANAGER_TOKEN"
```

### 3.2 Assign Task (insert task)
### POST `/api/manager/tasks/assign`
**Request**
```json
{
  "userId": 3,
  "title": "Prepare Q2 dashboard",
  "description": "Create KPI dashboard and share by EOD"
}
```

```bash
curl -X POST 'https://actifytask-api.onrender.com/api/manager/tasks/assign' \
  -H "Authorization: Bearer $MANAGER_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "userId":3,
    "title":"Prepare Q2 dashboard",
    "description":"Create KPI dashboard and share by EOD"
  }'
```

---

## 4) User APIs (`/api/user`) [Any logged-in user token]

### 4.1 Get Own Profile
### GET `/api/user/profile`
```bash
USER_TOKEN='<paste-user-token>'

curl -X GET 'https://actifytask-api.onrender.com/api/user/profile' \
  -H "Authorization: Bearer $USER_TOKEN"
```

### 4.2 Get Own Tasks
### GET `/api/user/tasks`
```bash
curl -X GET 'https://actifytask-api.onrender.com/api/user/tasks' \
  -H "Authorization: Bearer $USER_TOKEN"
```

---

## Quick Insert Data Flow (recommended demo)
1. Login as admin -> get `ADMIN_TOKEN`
2. Create a new user via `POST /api/admin/users`
3. Login as manager -> get `MANAGER_TOKEN`
4. Assign task to that user via `POST /api/manager/tasks/assign`
5. Login as created user -> get `USER_TOKEN`
6. Verify via `GET /api/user/tasks`

---

## Validation Rules
- `email`: must be valid email format
- `password`: 8-64 chars, must contain uppercase + lowercase + number
- `roles`: non-empty set when assigning roles
- `AssignTaskRequest.userId`: required
- `AssignTaskRequest.title`: required

---

## Common Error Responses
- `400`: validation failed
- `401`: invalid/expired/missing JWT
- `403`: role not allowed for endpoint
- `404`: resource not found
- `409`: duplicate user/email conflict
