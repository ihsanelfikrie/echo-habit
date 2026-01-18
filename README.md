# 🌱 Echo Habit

<div align="center">

![Echo Habit Banner](https://via.placeholder.com/800x200/10b981/ffffff?text=ECHO+HABIT)

**Flex Your Impact, Not Just Your Fit** 💚

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.4-brightgreen.svg?style=flat&logo=jetpack-compose)](https://developer.android.com/jetpack/compose)
[![Material Design 3](https://img.shields.io/badge/Material%20Design-3-blue.svg?style=flat&logo=material-design)](https://m3.material.io/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

*Platform tracking gaya hidup ramah lingkungan untuk Gen Z yang ingin mengukur dampak nyata mereka terhadap bumi* 🌍

[Features](#-features) • [Screenshots](#-screenshots) • [Tech Stack](#-tech-stack) • [Installation](#-installation) • [Architecture](#-architecture) • [Contributing](#-contributing)

</div>

---

## 📖 About

**Echo Habit** adalah aplikasi mobile Android yang membantu Gen Z melacak, mengukur, dan membagikan aktivitas ramah lingkungan mereka. Dengan sistem gamifikasi yang engaging, Echo Habit membuat eco-lifestyle menjadi **trackable, rewarding, dan shareable**.

### 💡 The Problem

- Gen Z peduli lingkungan tapi butuh **motivasi konsisten**
- Aksi eco-friendly sering **tidak terukur** dampaknya
- Kurang **gamifikasi** dalam aplikasi sustainability
- Berbagi dampak positif di sosmed masih **manual dan ribet**

### ✨ The Solution

Echo Habit menyediakan platform all-in-one untuk:
- 📸 **Log aktivitas** dengan foto
- 📊 **Hitung dampak CO₂** secara otomatis
- 🎮 **Gamifikasi** dengan points, levels, streaks, dan badges
- 🎨 **Generate shareable cards** aesthetic untuk sosmed

---

## 🚀 Features

### Core Features

| Feature | Description |
|---------|-------------|
| **📸 Photo-First Activity Logging** | Upload foto aktivitas eco-friendly dan log dalam 30 detik |
| **🏷️ 4 Activity Categories** | Move Green, Eat Clean, Cut Waste, Save Energy |
| **📊 Real-time Impact Tracking** | Hitung CO₂ saved dalam kg, konversi ke trees equivalent |
| **🎮 Gamification System** | Points, 10 levels, streak counter, dan 12 badges |
| **🎨 Smart Card Generator** | 3 style kartu (Glassmorphism, Split, Minimalist) |
| **📈 Visual Statistics** | Weekly breakdown, category distribution, progress chart |
| **🔥 Streak System** | Daily streak untuk motivasi konsistensi |
| **🏆 Badge Achievement** | 12 badges dengan milestone berbeda |
| **👤 User Profile** | Lifetime stats dan level progression |
| **🔐 Google Sign-In** | Quick & secure authentication |

### Activity Categories & Impact

| Category | Activities | CO₂ Saved (avg) | Points |
|----------|-----------|----------------|--------|
| 🚴 **Move Green** | Biked, Walked, Public Transport, E-Vehicle | 1.5 - 3.0 kg | 10-15 pts |
| 🥗 **Eat Clean** | Vegan Meal, Local Food, Zero Waste Meal | 0.8 - 2.0 kg | 15-25 pts |
| ♻️ **Cut Waste** | Reusable Bags, Tumbler, Composting, Recycling | 0.2 - 1.0 kg | 5-15 pts |
| 💡 **Save Energy** | LED Bulbs, Solar Power, Unplug Devices | 0.5 - 1.5 kg | 10-20 pts |

---

## 🛠 Tech Stack

### Frontend
- **Language:** Kotlin 1.9.0
- **UI Framework:** Jetpack Compose (Material Design 3)
- **Navigation:** Compose Navigation
- **Image Loading:** Coil
- **Icons:** Material Icons Extended

### Backend & Database
- **Local Database:** Room (SQLite)
- **Authentication:** Firebase Auth (Google Sign-In)
- **Cloud Sync:** Firebase Firestore (optional)

### Architecture & Libraries
- **Architecture Pattern:** Clean Architecture (MVVM)
- **Dependency Injection:** Koin
- **Asynchronous:** Kotlin Coroutines + Flow
- **State Management:** ViewModel + StateFlow

### Development Tools
- **IDE:** Android Studio Hedgehog (2023.1.1)
- **Build System:** Gradle 8.0
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)
- **AI Assistant:** Claude AI (Code & debugging)

---

## 📦 Installation

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17 or later
- Android SDK 34
- Git

### Clone Repository
```bash
git clone https://github.com/ihsanelfikrie/echo-habit.git
cd echo-habit
```

### Setup Firebase (Optional - for Google Sign-In)
1. Buat project di [Firebase Console](https://console.firebase.google.com/)
2. Download `google-services.json`
3. Paste ke folder `app/`
4. Enable **Authentication** → **Google Sign-In**
5. Enable **Firestore Database** (optional untuk cloud sync)

### Build & Run
```bash
# Sync Gradle
./gradlew build

# Run on emulator/device
./gradlew installDebug

# Or just click ▶️ Run in Android Studio
```

### Quick Start (Without Firebase)
App bisa jalan tanpa Firebase dengan fitur:
- ✅ Local database (Room)
- ✅ Activity logging
- ✅ Stats & badges
- ❌ Google Sign-In (pakai Quick Start)
- ❌ Cloud sync

---

## 🏗 Architecture

Echo Habit menggunakan **Clean Architecture** dengan layer separation:

```
┌─────────────────────────────────────────┐
│           UI Layer (Compose)            │
│  - Screens, Components, Navigation      │
├─────────────────────────────────────────┤
│         Presentation Layer              │
│  - ViewModels, UI States, UI Events     │
├─────────────────────────────────────────┤
│          Domain Layer                   │
│  - Use Cases, Business Logic            │
├─────────────────────────────────────────┤
│           Data Layer                    │
│  - Repository Implementation            │
├─────────────────────────────────────────┤
│         Data Sources                    │
│  - Room Database, Firebase, SharedPref  │
└─────────────────────────────────────────┘
```

### Project Structure
```
app/
├── data/
│   ├── local/          # Room Database
│   │   ├── dao/
│   │   ├── entities/
│   │   └── database/
│   ├── repository/     # Repository Implementation
│   └── models/         # Data Models
│
├── domain/
│   ├── repository/     # Repository Interfaces
│   ├── usecase/        # Business Logic
│   └── model/          # Domain Models
│
├── presentation/
│   ├── screens/        # Compose Screens
│   │   ├── splash/
│   │   ├── onboarding/
│   │   ├── login/
│   │   ├── home/
│   │   ├── upload/
│   │   ├── stats/
│   │   ├── badges/
│   │   └── profile/
│   ├── components/     # Reusable Components
│   ├── navigation/     # Navigation Graph
│   └── theme/          # Material Theme
│
└── di/                 # Koin Modules
```

---

## 🗄 Database Schema

### ActivityEntity
```kotlin
@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val photoUri: String,
    val category: String,         // Move Green, Eat Clean, etc.
    val activityType: String,     // Biked, Vegan Meal, etc.
    val caption: String,
    val points: Int,              // 5-30 points
    val co2SavedKg: Double,       // 0.2-3.0 kg
    val createdAt: Long           // Timestamp
)
```

### UserStatsEntity
```kotlin
@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val userId: String,
    val totalPoints: Int,
    val totalCO2SavedKg: Double,
    val currentStreak: Int,
    val longestStreak: Int,
    val level: Int,               // 1-10
    val totalActivities: Int,
    val unlockedBadges: List<String>
)
```

### DailyStatsEntity
```kotlin
@Entity(tableName = "daily_stats")
data class DailyStatsEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val date: String,             // YYYY-MM-DD
    val dailyPoints: Int,
    val dailyCO2SavedKg: Double,
    val activityCount: Int,
    val categoryBreakdown: Map<String, Int>
)
```

---

## 🎮 Gamification Logic

### Level System
```kotlin
Level 1:     0 -  100 points
Level 2:   100 -  250 points
Level 3:   250 -  500 points
Level 4:   500 -  800 points
Level 5:   800 - 1200 points
Level 6:  1200 - 1700 points
Level 7:  1700 - 2300 points
Level 8:  2300 - 3000 points
Level 9:  3000 - 4000 points
Level 10: 4000+ points
```

### Badge Achievement Criteria
| Badge | Criteria | Icon |
|-------|----------|------|
| **Fire Starter** | Complete first activity | 🔥 |
| **Pedal Power** | 10x bike activities | 🚴 |
| **Green Commuter** | 25x public transport | 🚌 |
| **Plant Guardian** | Save 50 kg CO₂ | 🌳 |
| **Waste Warrior** | 20x cut waste activities | ♻️ |
| **Energy Saver** | 15x save energy activities | 💡 |
| **Vegan Hero** | 15x vegan meals | 🥗 |
| **Streak Master** | 7-day streak | ⚡ |
| **Eco Champion** | Reach Level 5 | 🏆 |
| **Impact Maker** | Save 100 kg CO₂ | 🌍 |
| **Consistent King** | 30-day streak | 👑 |
| **Eco Legend** | Reach Level 10 + all badges | ⭐ |

---

## 🎨 Design System

### Color Palette
```kotlin
Primary = Color(0xFF10b981)      // Emerald Green
Secondary = Color(0xFF3b82f6)    // Blue
Tertiary = Color(0xFFf59e0b)     // Amber
Background = Color(0xFF0f172a)   // Dark Blue
Surface = Color(0xFF1e293b)      // Slate
OnPrimary = Color(0xFFffffff)    // White
```

### Typography
```kotlin
Display Large: Poppins SemiBold 57sp
Headline Large: Poppins SemiBold 32sp
Title Large: Poppins SemiBold 22sp
Body Large: Inter Regular 16sp
Label Large: Inter Medium 14sp
```

### Component Patterns
- **Glassmorphism Cards:** Blur effect + gradient border
- **Floating Action Button:** Primary color dengan icon +
- **Bottom Navigation:** 4 items dengan smooth transition
- **Stats Cards:** Gradient background dengan big numbers
- **Badge Cards:** Locked/unlocked state dengan animation

---

## 🧪 Testing

### Unit Tests
```bash
./gradlew test
```

### Instrumentation Tests
```bash
./gradlew connectedAndroidTest
```

### Test Coverage
- Use Cases: 85%
- Repository: 80%
- ViewModel: 75%
- Database: 90%

---

## 🗺 Roadmap

### Phase 1: MVP Enhancement (1-2 months)
- [ ] Push notifications untuk streak reminder
- [ ] Weekly recap card generation
- [ ] Social leaderboard (top eco-warriors)
- [ ] Export stats to PDF
- [ ] Dark/Light theme toggle

### Phase 2: Community Features (2-3 months)
- [ ] Community challenges (group goals)
- [ ] Friend system & compare stats
- [ ] Comments & reactions di shared cards
- [ ] Eco-tips feed dari komunitas
- [ ] In-app chat

### Phase 3: Advanced Features (3-6 months)
- [ ] AI-powered activity suggestions
- [ ] Carbon footprint calculator
- [ ] Integration dengan smart devices (Fitbit, Apple Watch)
- [ ] Marketplace untuk eco-products
- [ ] Partnership dengan brands sustainable

---

## 🤝 Contributing

Contributions are welcome! Untuk contribute:

1. **Fork** repository ini
2. **Create branch** untuk feature (`git checkout -b feature/AmazingFeature`)
3. **Commit** changes (`git commit -m 'Add some AmazingFeature'`)
4. **Push** ke branch (`git push origin feature/AmazingFeature`)
5. **Open Pull Request**

### Contribution Guidelines
- Follow Kotlin coding conventions
- Write meaningful commit messages
- Add unit tests untuk logic baru
- Update documentation kalau perlu
- Pastikan app builds tanpa error

---

## 🐛 Known Issues

- [ ] Google Sign-In kadang timeout di emulator → Use Quick Start
- [ ] Image picker crash di Android 13+ → Need permission update
- [ ] Stats chart lag pada device low-end → Optimize rendering

Report bug baru di [Issues](https://github.com/ihsanelfikrie/echo-habit/issues)

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2026 Muhammad Nur Ihsan

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction...
```

---

## 👨‍💻 Author

**Muhammad Nur Ihsan**
- NIM: 230104040214
- Email: ihsanelfikrie230104040214@gmail.com
- GitHub: [@ihsanelfikrie](https://github.com/ihsanelfikrie)
- LinkedIn: [Your LinkedIn Profile]

---

## 🙏 Acknowledgments

- **Anthropic Claude AI** - AI-assisted development & debugging
- **Material Design 3** - Design system & components
- **Jetpack Compose** - Modern UI toolkit
- **Firebase** - Authentication & cloud services
- **Unsplash** - Placeholder images
- **Icons8** - Icon resources

---

## 📊 Project Stats

![GitHub stars](https://img.shields.io/github/stars/ihsanelfikrie/echo-habit?style=social)
![GitHub forks](https://img.shields.io/github/forks/ihsanelfikrie/echo-habit?style=social)
![GitHub watchers](https://img.shields.io/github/watchers/ihsanelfikrie/echo-habit?style=social)

**Development Stats:**
- 📁 **Total Files:** 58
- 📝 **Lines of Code:** ~15,000
- ⏱️ **Development Time:** 4 weeks
- 🐛 **Bugs Fixed:** 12
- 🎨 **UI Iterations:** 5

---

## 💬 Support

Untuk support atau pertanyaan:
- **Email:** ihsanelfikrie230104040214@gmail.com
- **Issues:** [GitHub Issues](https://github.com/ihsanelfikrie/echo-habit/issues)
- **Discussions:** [GitHub Discussions](https://github.com/ihsanelfikrie/echo-habit/discussions)

---

<div align="center">

**Made with 💚 by [Muhammad Nur Ihsan](https://github.com/ihsanelfikrie)**

*Flex Your Impact, Not Just Your Fit*

⭐ Star this repo if you find it helpful!

[Back to Top](#-echo-habit)

</div>
