# 🐾 Pawlly Mobile - API Documentation

> **Note for Mobile Developers:**
> - When using the Android emulator, use `http://10.0.2.2:8080/api/` as the base URL to access the backend.
> - For real devices, use your host machine's local network IP (e.g., `http://192.168.1.100:8080/api/`).

## Adoption APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/adoptions` | Get all adoptions |
| GET | `/api/adoptions/{id}` | Get adoption by ID |
| POST | `/api/adoptions` | Create new adoption |
| PUT | `/api/adoptions/{id}` | Update adoption by ID |
| DELETE | `/api/adoptions/{id}` | Delete adoption by ID |

## Lost and Found APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/lostandfound` | Create lost/found report |
| GET | `/api/lostandfound` | Get all reports (optional filter by creatorid) |
| PUT | `/api/lostandfound/{id}` | Update lost/found report |
| DELETE | `/api/lostandfound/{id}` | Delete lost/found report |

## Pet Listing / Rehome APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/pet/postpetrecord` | Add new pet record |
| GET | `/api/pet/getAllPets` | Get all pets |
| GET | `/api/pet/getPet/{id}` | Get pet by ID |
| PUT | `/api/pet/putPetDetails` | Update pet details |
| DELETE | `/api/pet/deletePetDetails/{pid}` | Delete pet by ID |

## User APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users` | Create user |
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |
| PUT | `/api/users/{id}` | Update user (with optional profile picture) |
| DELETE | `/api/users/{id}` | Delete user (admin only) |
| GET | `/api/users/me` | Get current authenticated user |
| POST | `/api/users/change-password` | Change password |
| POST | `/api/auth/login` | **Login: returns** `{ "token": "<JWT token>" }` **on success** |

> **Note:** Clients must parse the login response as JSON and extract the `token` property. The backend does not return a plain string for the token.