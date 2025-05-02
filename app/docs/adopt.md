# 🐾 Pawlly Mobile – Adopt Feature Functional & UI Spec

## Overview
The Adopt feature allows users to:
- Browse adoptable pets
- Filter by preferences (species, breed, etc.)
- View full pet profiles
- Start a 7-step adoption process
- Upload and view pet images using the ImageCarousel component

All UI is built in Jetpack Compose with Material3 design system.

---

## 🔁 Flow Summary

1. **AdoptScreen** (Landing)
2. **Filter UI** (optional chips or modal)
3. **SearchResultsScreen** (list/grid of pet cards)
4. **PetDetailScreen** (expanded pet profile with ImageCarousel)
5. **AdoptionConfirmationDialog**
6. **AdoptionStepScreens** (1–7)
7. **AdoptionFinishScreen**

---

## 📱 AdoptScreen (Default Landing)
**Purpose:** Entry point to adoption

### Layout
- Greeting text ("Looking to adopt?")
- Hero banner image (static)
- Filter chips (Dog, Cat, Small, Nearby)
- Featured pet carousel (using ImageCarousel component)
- "Browse All" CTA button

### Navigation
- PetCard → PetDetailScreen
- Browse All → SearchResultsScreen

---

## 🔎 SearchResultsScreen
**Purpose:** Show adoptable pets in list/grid

### Layout
- Top bar with search and filter
- Pet cards using PetCard component:
  - Pet photo (using ImageCarousel for multiple images)
  - Name, breed, age, location
- Empty state view if no matches

### Navigation
- PetCard → PetDetailScreen

---

## 🐶 PetDetailScreen
**Purpose:** Full profile of selected pet

### Layout
- ImageCarousel for multiple pet photos
- Bio section: name, breed, age, gender
- Tags (vaccinated, neutered, friendly)
- Owner/shelter info (if static)
- "Adopt Now" CTA at bottom

### Navigation
- CTA → AdoptionConfirmationDialog

---

## ❗ AdoptionConfirmationDialog
Simple confirmation before continuing:
- Text: "Are you sure you want to start the adoption process for [pet name]?"
- Buttons: [Cancel], [Continue]

### Navigation
- Continue → AdoptionStep1

---

## 📋 7-Step Process – Static UI

Each screen:
- Step title (e.g., "Step 1 of 7")
- Inputs as specified
- Back + Continue buttons
- Uses Material3 components for consistent styling

### Step 1: Start
- Display user info (email, name)
- Checkbox to accept terms
- CTA: "Start"

### Step 2: Address
- Address Line 1 & 2
- Postcode, Town
- Landline, Mobile (with "Send Code" and Verification field)

### Step 3: Home
- Garden? (Yes/No)
- Home type (Dropdown)
- Household setting (Dropdown)
- Activity level (Dropdown)

### Step 4: Images of Home
- Upload 2–4 images using ImagePicker
- ImageCarousel to preview uploaded images
- Fixed 600x600px format instructions

### Step 5: People in Home
- # of adults / children
- Age of youngest child (dropdown)
- Visiting children? (Y/N + dropdown)
- Any flatmates/lodgers? (Y/N)

### Step 6: Other Animals
- Any allergies in household? (Y/N)
- Other pets? (Y/N → species/age/gender text area)
- Neutered? Vaccinated? (Y/N/NA)
- Experience description (textarea)

### Step 7: Confirm
- Thank you message
- CTA: "Return to Profile" or "Adopt More"

---

## 🔗 Navigation Flow (Routes)

| Screen | Route |
|--------|-------|
| Adopt Home | adopt |
| Results | adopt/results |
| Pet Details | adopt/pet/{id} |
| Step 1 | adopt/start |
| Step 2 | adopt/address |
| Step 3 | adopt/home |
| Step 4 | adopt/images |
| Step 5 | adopt/roommate |
| Step 6 | adopt/other-animals |
| Step 7 | adopt/confirm |
| Finish | adopt/finish |

---

## 💡 Implementation Notes
- All UI components use Material3 design system
- Image handling:
  - ImageCarousel component for displaying multiple images
  - ImagePicker for photo selection
  - Coil for image loading and caching
- Forms are scrollable when needed
- Each screen is a separate Composable function
- Navigation uses Navigation Compose
- State management follows MVVM pattern
- All text uses Inter font family
- Consistent padding and spacing throughout
- Error states and loading indicators implemented
- Accessibility support included

