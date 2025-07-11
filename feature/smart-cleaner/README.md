# 🧹 Smart Cleaner Feature

## Overview

The **Smart Cleaner** is a powerful feature that helps users identify and clean up unnecessary media files from their device. It automatically scans for duplicate images, large videos, and old media files, providing users with detailed information about potential space savings.

## ✨ Features

### 🔍 **Smart Detection**
- **Duplicate Images**: Finds identical images using MD5 hash comparison
- **Large Videos**: Identifies videos larger than a configurable threshold (default: 100MB)
- **Old Media**: Locates media files older than a specified period (default: 1 year)

### 📊 **Space Analysis**
- **Real-time Space Calculation**: Shows potential space savings in MB
- **Item Count**: Displays the number of items in each cleanup category
- **Detailed Breakdown**: Provides size information for each cleanup group

### 🎯 **User-Friendly Interface**
- **Modern Material 3 Design**: Beautiful, intuitive UI with dark/light theme support
- **Selective Cleanup**: Choose specific items or categories to delete
- **Bulk Operations**: Select all or clear all selections with one tap
- **Category Filtering**: Switch between Duplicates, Large Videos, and Old Media
- **Progress Indicators**: Shows loading and deletion progress

### ⚡ **Performance Optimizations**
- **Cached Media Data**: Single MediaStore query with efficient in-memory filtering
- **Configurable Thresholds**: Adjustable parameters for different use cases
- **Efficient Flow Handling**: Using `combine` for optimal performance
- **Smart UI Updates**: Atomic state updates for better performance

## 🏗️ Architecture

### **Clean Architecture Implementation**
```
feature/smart-cleaner/
├── presentation/
│   ├── SmartCleanerScreen.kt      # Main UI screen with category filtering
│   ├── SmartCleanerViewModel.kt   # Business logic with generalized methods
│   ├── SmartCleanerState.kt       # UI state management
│   ├── SmartCleanerEvent.kt       # User interactions
│   └── CleanerCategory.kt         # Sealed interface for categories
├── navigation/
│   └── SmartCleanerNavigation.kt  # Navigation setup
└── build.gradle.kts               # Module dependencies
```

### **Domain Layer**
```
domain/
├── model/cleaner/
│   └── CleanupItem.kt             # Data models
├── repository/
│   └── CleanupRepository.kt       # Repository interface
└── usecase/cleaner/
    ├── FindDuplicateImagesUseCase.kt
    ├── FindLargeVideosUseCase.kt
    ├── FindOldMediaUseCase.kt
    └── DeleteMediaItemsUseCase.kt
```

### **Data Layer**
```
data/
├── repository/
│   └── CleanupRepositoryImpl.kt   # Repository implementation
└── cleaner/
    └── CleanupManager.kt          # Core cleanup logic
```
## 🧪 Testing

### **Unit Tests**
- **ViewModel Tests**: Test business logic and state management
- **Repository Tests**: Verify data layer functionality
- **Use Case Tests**: Ensure proper domain logic
- **CleanupManager Tests**: Test MediaManager integration

### **Test Coverage**
```bash
./gradlew :feature:smart-cleaner:test
```

## 🔒 Permissions

The Smart Cleaner requires the following permissions:
- **READ_EXTERNAL_STORAGE**: To scan media files
- **WRITE_EXTERNAL_STORAGE**: To delete selected files

## 🎨 Design System

### **Material 3 Integration**
- **Color Scheme**: Adapts to light/dark themes
- **Typography**: Consistent with app design
- **Icons**: Material Design iconography
- **Components**: Cards, buttons, and chips


## 🚀 Future Enhancements (Suggestions)

### **Planned Features**
- **Smart Recommendations**: AI-powered cleanup suggestions
- **Scheduled Cleanup**: Automatic periodic scanning
- **Cloud Integration**: Sync cleanup across devices
- **Advanced Filters**: More granular filtering options
- **Batch Operations**: Enhanced bulk management
- **User Preferences**: Save custom thresholds

### **Performance Optimizations**
- **Background Scanning**: Non-blocking media analysis
- **Incremental Updates**: Only scan new/changed files
- **Smart Caching**: Intelligent cache invalidation

---

**Smart Cleaner** transforms the way users manage their media storage, providing an intelligent, safe, and efficient way to reclaim valuable device space while maintaining a beautiful and intuitive user experience. The implementation follows clean architecture principles with proper MediaManager integration, sealed interface design, generalized methods, and optimized UI updates for optimal performance. 
