# Signup and Login Flow Documentation

## Authentication Flow

### Sign Up
1. User enters their details:
   - First Name
   - Last Name
   - Username
   - Email (will be used for login)
   - Password
   - Phone Number (optional)
   - Address (optional)
   - Profile Picture (optional)

2. Validation:
   - All required fields must be filled
   - Email must be valid format and unique
   - Password must meet security requirements
   - Username must be unique

3. API Request:
   - Endpoint: `POST /api/users`
   - Request Type: Multipart Form Data
   - Fields sent:
     ```json
     {
       "username": "string",
       "firstName": "string",
       "lastName": "string",
       "email": "string",
       "password": "string",
       "address": "string",
       "phoneNumber": "string"
     }
     ```
   - Profile picture sent as separate part if provided

4. Response:
   - Success: Returns UserResponse object
   - Error: Returns appropriate error message
     - Username exists
     - Email exists
     - Invalid format
     - Server error

### Login
1. User enters:
   - Email (used as primary login identifier)
   - Password
   - Remember Me (optional)

2. API Request:
   - Endpoint: `POST /api/auth/login`
   - Request Body:
     ```json
     {
       "email": "string",
       "password": "string"
     }
     ```

3. Response:
   - Success: Returns JWT token
   - Error: Returns 401 Unauthorized

4. Token Storage:
   - JWT token stored in SharedPreferences
   - Key: "jwt_token"
   - File: "pawlly_prefs"

5. Remember Me:
   - If enabled, stores email in SharedPreferences
   - Key: "saved_email"
   - Used to pre-fill email field on next app open

### Authentication Headers
For authenticated requests:
- Header: `Authorization: Bearer <token>`
- Token retrieved from SharedPreferences
- Used for all protected API endpoints

### Error Handling
1. Network Errors:
   - Display user-friendly message
   - Option to retry

2. Authentication Errors:
   - Invalid credentials: Show error message
   - Token expired: Redirect to login
   - Server error: Show appropriate message

### Security Considerations
1. Password:
   - Never stored locally
   - Transmitted securely
   - Hashed on server (BCrypt)

2. Token:
   - Stored securely in SharedPreferences
   - Cleared on logout
   - Refreshed when expired

3. Remember Me:
   - Only stores email
   - No sensitive data stored
