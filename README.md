# GitHub Repository Finder

A modern Android application built with Kotlin that allows users to search for GitHub repositories and explore detailed information about them.

## Features

- **GitHub Repository Search**: Search for GitHub repositories by name using the GitHub API
- **Local Database Caching**: Store search results in a Room database for offline access
- **Real-time Filtering**: Filter repositories by name, language, owner, description, or ID as you type
- **Detailed Repository Information**: View repository ID, name, owner, description, language, and star count
- **In-app Repository Viewing**: Open repository URLs directly within the app using a WebView
- **Network Error Handling**: Graceful handling of network failures with user feedback
- **Loading State Indicators**: Visual feedback during network operations
- **Empty State Handling**: Clear indication when no repositories are found

## Technical Implementation

### Architecture

This application follows the MVVM (Model-View-ViewModel) architecture pattern with Clean Architecture principles:

- **UI Layer**
  - `MainActivity`: Main search interface with SearchView and RecyclerView
  - `WebViewActivity`: In-app browser for viewing repository details
  - `RepositoryAdapter`: RecyclerView adapter with DiffUtil for efficient updates

- **ViewModel Layer**
  - `RepositoryViewModel`: Manages repository data and UI state, handles search operations

- **Repository Layer**
  - `GitHubRepository`: Single source of truth that coordinates between network and database

- **Data Layer**
  - Network: `NetworkService`, `GitHubApiService` (Retrofit interface)
  - Database: `AppDatabase`, `RepoDao` (Room database)
  - Cache: `Cacher` (Handles caching logic)
  - Models: `GHRepo`, `SearchResponse`, `RepoItem`, `Owner`

### Libraries & Technologies

- **Kotlin**: 100% Kotlin codebase
- **Coroutines**: For asynchronous operations
- **Room**: SQLite database for local storage
- **LiveData**: Observable data holder for UI updates
- **ViewModel**: Lifecycle-aware data management
- **Retrofit**: Type-safe HTTP client for API communication
- **OkHttp**: HTTP client with logging support
- **GSON**: JSON serialization/deserialization
- **ViewBinding**: Type-safe view binding
- **RecyclerView & DiffUtil**: Efficient list rendering
- **Material Components**: For consistent modern UI

### Key Features Implementation

- **Search Mechanism**: Two-pronged approach:
  - Network search - When user submits a query via SearchView
  - Local filtering - Real-time filtering of cached results as user types

- **Caching Strategy**:
  - Results from network requests are stored in a Room database
  - Repository information is available offline after initial search
  - Search is performed on database when offline

- **UI Components**:
  - MaterialCardView for repository items with consistent styling
  - ProgressBar for loading state indication
  - Empty state view when no repositories are found

## Implementation Details

### Database Schema
- **Table**: `repositories` with entity class `GHRepo`
- **Fields**: id, name, repoURL, ownerLogin, description, stars, language
- **Indices**: Compound index on name+ownerLogin, and index on language

### Search Functionality
- **Network Search**: GitHub Search API with star-based sorting
- **Local Search**: SQL LIKE queries on multiple fields (name, language, owner, description, id)

### Error Handling
- Network errors show Toast messages
- Empty results display a dedicated message view
- Loading states use a central ProgressBar

## Requirements

- Android 7.0 (API level 24) or higher
- Internet connection for initial repository search

## Project Structure
