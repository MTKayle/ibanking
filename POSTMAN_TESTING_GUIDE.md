# Postman Testing Guide for iBanking Backend

This guide explains how to test all APIs added/updated in this project using Postman (or curl). It covers user registration (normal and with face verification), login (password and face), user settings (smart eKYC, face recognition), smart OTP update, and admin user management endpoints.

## Base URL

- Default: `http://localhost:8080`
- Use a Postman Environment variable `base_url` with value `http://localhost:8080`.

## Environment variables (create a Postman Environment)

- base_url = http://localhost:8080
- customer_token =
- officer_token =
- test_phone_customer = 0123456789
- test_phone_officer = 0987654321
- test_user_id_customer = 1

## Notes before testing

- Make sure the application is running (mvn spring-boot:run or run from your IDE).
- Configure Face++ and Cloudinary in `src/main/resources/application.properties` if you want to test face features live:
  - `faceplus.api.key`, `faceplus.api.secret`, `faceplus.api.url`
  - `cloudinary` credentials if used
- `faceplus.confidence.threshold` default is 70.0 (percent).
- You can create an OFFICER user by registering normally then updating DB to set role = 'officer', or by creating an account and updating role via SQL.

## Quick flow overview (recommended order)

1. Register a CUSTOMER (`/api/auth/register`) or register-with-face (`/api/auth/register-with-face`) which also stores selfie to Cloudinary.
2. Login with password (`/api/auth/login`) to get a token.
3. As the user, enable face recognition and smart eKYC (PATCH `/api/users/{userId}/settings`).
4. Upload a selfie and call `/api/auth/login-with-face` to log in by face.
5. Use OFFICER token to call admin endpoints (GET `/api/users`, lock/unlock users, update user info).
6. Update smart OTP via PATCH `/api/users/{userId}/smart-otp` (only allowed when smartEkycEnabled = true).

---

## 1) Register (normal)

```http
POST {{base_url}}/api/auth/register
Content-Type: application/json

{
  "phone": "0123456789",
  "password": "password123",
  "fullName": "Nguyen Van A",
  "email": "nguyenvana@gmail.com",
  "cccdNumber": "001234567890",
  "dateOfBirth": "1990-01-15",
  "permanentAddress": "123 ABC St",
  "temporaryAddress": "456 XYZ St"
}
```

**Expected:** 201 Created with AuthResponse JSON containing token and user info.

**Save token:** copy the `token` from response to `customer_token` environment variable.

**Notes:**
- The project uses phone as primary login identifier. Username isn't used.
- `photoUrl` will be null for normal registration.

---

## 2) Register with face verification (register-with-face)

```http
POST {{base_url}}/api/auth/register-with-face
Content-Type: multipart/form-data

phone=0123456789
email=nguyenvana@gmail.com
password=password123
fullName=Nguyen Van A
cccdNumber=001234567890
dateOfBirth=1990-01-15
permanentAddress=123 ABC St
temporaryAddress=456 XYZ St
cccdPhoto=@C:\path\to\cccd.jpg
selfiePhoto=@C:\path\to\selfie.jpg
```

**Behavior:**
- The server will call Face++ to compare cccdPhoto and selfiePhoto.
- If match confidence < threshold (default 70), registration fails.
- On success, selfie is uploaded to Cloudinary and `photoUrl` is saved to user.
- Response: 201 Created with AuthResponse (token + user info).

---

## 3) Login (password)

```http
POST {{base_url}}/api/auth/login
Content-Type: application/json

{
  "phone": "0123456789",
  "password": "password123"
}
```

**Response:** 200 OK with AuthResponse (token, userId, email, fullName, phone, role)

**Save token** to `customer_token` (or `officer_token` for officer account).

---

## 4) Login with face (face recognition)

```http
POST {{base_url}}/api/auth/login-with-face
Content-Type: multipart/form-data

phone=0123456789
facePhoto=@C:\path\to\selfie.jpg
```

**Behavior:**
- The endpoint checks: user exists, not locked, `faceRecognitionEnabled` == true, and `photoUrl` is set.
- It calls Face++ to compare uploaded face with stored `photoUrl`.
- If confidence >= threshold (default 70) returns AuthResponse (token + user info).
- Else returns an error (authentication failed).

**Example curl (Windows cmd):**

```cmd
curl -X POST "http://localhost:8080/api/auth/login-with-face" ^
  -H "Content-Type: multipart/form-data" ^
  -F "phone=0123456789" ^
  -F "facePhoto=@C:\\path\\to\\selfie.jpg"
```

---

## 5) Update user settings (enable/disable smart eKYC and face recognition)

```http
PATCH {{base_url}}/api/users/{userId}/settings
Authorization: Bearer {{customer_token}} (or {{officer_token}})
Content-Type: application/json

{
  "smartEkycEnabled": true,
  "faceRecognitionEnabled": true
}
```

**Behavior:**
- OFFICER can update any user.
- CUSTOMER can update only their own userId.

---

## 6) Update smart OTP (only allowed if smartEkycEnabled == true)

```http
PATCH {{base_url}}/api/users/{userId}/smart-otp
Authorization: Bearer {{customer_token}} (or {{officer_token}})
Content-Type: application/json

{
  "smatOTP": "SOME_SECRET"
}
```

**Behavior:**
- If user.smartEkycEnabled != true the request is rejected (IllegalStateException -> handled by GlobalExceptionHandler).
- OFFICER may update for any user; CUSTOMER only for own user.

---

## 7) Admin / User Management endpoints (OFFICER only)

All endpoints below require Authorization: Bearer {{officer_token}}

- GET {{base_url}}/api/users — returns only users with role = customer
- GET {{base_url}}/api/users/{userId}
- PUT {{base_url}}/api/users/{userId} — update user info (email, fullName, addresses, dateOfBirth)
- PATCH {{base_url}}/api/users/{userId}/lock — lock/unlock account
  Body: { "locked": true }

---

## 8) Error cases & expected status codes

- 400 Bad Request: validation errors, missing/invalid input (e.g. smatOTP empty)
- 401 Unauthorized: when required but no/invalid token provided
- 403 Forbidden: user not authorized to perform an action (customer accessing officer-only endpoint)
- 404 Not Found: user or resource not found
- 409 Conflict or 400: business rule violations (e.g. face match below threshold) — project currently throws IllegalArgumentException/IllegalStateException; these are handled by `GlobalExceptionHandler` (check the handler to see mapped status codes)
- 423 Locked: login attempt for a locked account (AccountLockedException -> mapped to 423 if handler set)

---

## 9) JWT inspection

- JWT token payload includes claims: `userId`, `fullName`, `phone`, `role`, `sub` (phone), `iat`, `exp`.
- You can inspect token on https://jwt.io by pasting the token and using algorithm HS256. Ensure the secret in `application.properties` matches what you use to inspect (if you rely on jwt.io verify).

---

## 10) Postman collection structure suggestion

- Folder: Auth
  - POST register
  - POST register-with-face (multipart)
  - POST login
  - POST login-with-face (multipart)

- Folder: Users
  - GET /api/users (OFFICER)
  - GET /api/users/{id} (OFFICER)
  - PUT /api/users/{id} (OFFICER)
  - PATCH /api/users/{id}/lock (OFFICER)
  - PATCH /api/users/{id}/settings (CUSTOMER/ OFFICER)
  - PATCH /api/users/{id}/smart-otp (CUSTOMER/ OFFICER)

---

## 11) Troubleshooting tips

- If Face++ calls fail:
  - Check `faceplus.api.url`, `faceplus.api.key`, `faceplus.api.secret` in `application.properties`.
  - Ensure Face++ account has quota and the URL endpoint matches the API you're calling (compare vs. `/facepp/v3/compare` docs).

- If Cloudinary upload fails:
  - Check Cloudinary credentials and ensure the `CloudinaryService` bean is configured.

- If JWT tokens appear invalid on jwt.io:
  - Ensure algorithm HS256 and the secret used on jwt.io matches `app.jwt.secret` in `application.properties`.

---

If you want, I can also:
- Export a ready-to-import Postman collection JSON for all requests above.
- Add Postman tests (assertions) to automatically validate responses.
- Update `GlobalExceptionHandler` to standardize HTTP status codes for IllegalStateException / IllegalArgumentException.

Tell me which extra item you'd like and I'll add it next.
