# Pet (Rehome) API Documentation

## Entity: `PetEntity`
- **Table:** `pets`
- **Fields:**
  - `pid` (int, primary key, auto-generated)
  - `name` (String)
  - `type` (String)
  - `breed` (String)
  - `age` (int)
  - `gender` (String)
  - `description` (String)
  - `photo` (String, URL to uploaded photo)
  - `status` (String)
  - `userName` (String)
  - `address` (String)
  - `contactNumber` (String)
  - `submissionDate` (String)

---

## Repository: `PetRepository`
- Extends `JpaRepository<PetEntity, Integer>`
- Custom query:
  - `List<PetEntity> findPetsByName(String name)`

---

## Service: `PetService`
- **Create:**
  - `postPetRecord(PetEntity pet)` — Save a new pet record.
- **Read:**
  - `getAllPets()` — Get all pets.
  - `getPetById(int id)` — Get a pet by its ID.
- **Update:**
  - `putPetDetails(int pid, PetEntity newPetDetails)` — Update a pet's status (other fields not updated).
- **Delete:**
  - `deletePet(int pid)` — Delete a pet by its ID.

---

## Controller: `PetController`
- **Base path:** `/api/pet`
- **Endpoints:**
  - `GET /api/pet/test` — Test endpoint, returns a string.
  - `POST /api/pet/postpetrecord` — Create a new pet record.
    - **Request:** `multipart/form-data`
      - Fields: `name`, `type`, `breed`, `age`, `gender`, `description`, `photo` (file), `status`, `userName`, `address`, `contactNumber`, `submissionDate`
    - **Photo:** Saved to `uploads/pets/`, URL stored in DB.
    - **Response:** Created `PetEntity`.
  - `GET /api/pet/getAllPets` — Get all pets.
  - `GET /api/pet/getPet/{id}` — Get a pet by ID.
  - `PUT /api/pet/putPetDetails` — Update a pet's status (by `pid`, with `PetEntity` in body).
  - `DELETE /api/pet/deletePetDetails/{pid}` — Delete a pet by ID.

---

## Photo Uploads
- Photos are uploaded as multipart files and saved to the `uploads/pets/` directory.
- The file URL is stored in the `photo` field of the pet.
- Photo is required for pet creation.

---

## Notes & Limitations
- The update endpoint only updates the `status` field, not other pet details.
- No filtering, pagination, or advanced search (except by name in the repository, but not exposed in the controller).
- No authentication/authorization checks in the controller (unless handled globally).
