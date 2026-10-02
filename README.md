# 🎉 TidyUp - Gamified Kids Chore Tracker

A fun, colorful Android app that encourages kids to do household chores by turning them into a competitive, points-based game!

## 📱 Features

### Kid Features
- **Daily Leaderboard** - See live points for all kids (updated daily)
- **Chore Selection** - Pick available chores and submit them for parent review
- **Background Color Themes** - Each kid's selected chore shows in their unique color
- **Celebration Messages** - Get random "Good job!" messages in English or Hebrew when completing chores
- **All-Time Leaderboard** - Track lifetime points with a crown 👑 for the top scorer

### Parent Features  
- **Parent Dashboard** - Review submitted chores and approve/reject them
- **Point Awards** - Approve chores to award kids points
- **Chore Rejection** - Send chores back for kids to redo if not done properly
- **Chore Management** - Create and manage available chores with point values (10-100)
- **Kid Management** - Add/remove kids with custom names, colors, and avatars

### App Features
- **Bilingual Support** - Switch between English and Hebrew at runtime
- **Local Storage** - All data stored locally on device (no cloud sync)
- **Daily Reset** - Points reset each day automatically (all-time scores preserved)
- **Automatic Comments Pool** - 10 different celebratory messages to keep it fresh

## 🏗️ Architecture

### Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Database**: Room + SQLite
- **Dependency Injection**: Hilt
- **Background Tasks**: WorkManager
- **Navigation**: Jetpack Navigation Compose

### Project Structure
```
app/src/main/
├── kotlin/com/example/choreapp/
│   ├── ChoreApp.kt                 # Application class with Hilt
│   ├── MainActivity.kt             # Entry point
│   ├── data/
│   │   ├── db/                    # Database layer (Entities, DAOs, Database)
│   │   ├── repository/            # Repository pattern for data access
│   │   └── DefaultComments.kt     # Comment seed data
│   ├── di/
│   │   └── DatabaseModule.kt      # Dependency injection setup
│   ├── domain/
│   │   └── model/                 # Data models and enums
│   ├── ui/
│   │   ├── Navigation.kt          # Navigation setup
│   │   ├── screens/               # All UI screens
│   │   ├── components/            # Reusable Compose components
│   │   └── theme/                 # Material Design theme
│   ├── utils/
│   │   ├── DateUtils.kt           # Date manipulation
│   │   ├── ColorUtils.kt          # Color management
│   │   └── WorkManagerUtil.kt     # Background task scheduling
│   ├── viewmodel/
│   │   └── ChoreAppViewModel.kt   # Main ViewModel with all business logic
│   └── workers/
│       └── DailyResetWorker.kt    # Daily background task
└── res/
    ├── values/                    # English strings
    ├── values-he/                 # Hebrew strings
    └── ...other resources
```

## 📊 Data Models

### Core Models
- **Cleaner** - Kid profile with name, color, avatar
- **Chore** - Task template with name, description, points (10-100)
- **ChoreInstance** - Active chore assignment with status tracking
- **DailyScore** - Points earned today (reset daily)
- **AllTimeScore** - Lifetime points for each kid
- **AppSettings** - App configuration (language, etc)
- **Comment** - Celebration messages in both languages

## 🔄 Workflows

### Kid Completing a Chore
1. Kid selects a chore from available list
2. Background changes to their color
3. Kid completes the chore
4. Kid submits for parent review
5. Random "Good job!" message displays
6. Parent reviews in dashboard

### Parent Approving/Rejecting
1. Parent sees submitted chores in dashboard
2. **Approve**: Chore marked complete, kid gets points (daily + all-time)
3. **Reject**: Chore returned to available for kid to redo

### Daily Reset
- Automatic at midnight via WorkManager
- Clears DailyScore table
- Preserves AllTimeScore
- Allows fresh leaderboard each day

## 🌍 Localization

### Supported Languages
- **English** (en)
- **Hebrew** (he)

All UI strings are in `res/values/strings.xml` and `res/values-he/strings.xml`.
Comment celebrations are stored in database with translations.

## 🛠️ Building & Running

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK API 24+
- Kotlin 1.9.10+

### Build
```bash
./gradlew build
```

### Run
```bash
./gradlew installDebug
```

Or open in Android Studio and click "Run".

## 📋 Implementation Status

### Completed Phases (16/21)
- ✅ Phase 1: Project Setup & Dependencies
- ✅ Phase 2: Room Entities & Repositories  
- ✅ Phase 3: Core UI Screens
- ✅ Phase 4: Chore Submission & Approval Workflow
- ✅ Phase 5: All-Time Leaderboard with Crown
- ✅ Phase 6: Settings & Management Screens

### In Progress / Planned (5/21)
- 🔄 Phase 7: Polish, Testing, Animations
  - Daily Reset Logic ✅
  - Error Handling (WIP)
  - UI Animations (Pending)
  - Test Suite (Pending)
  - Final Polish (Pending)

### Features Status
- ✅ Main leaderboard screen
- ✅ Chore selection screen  
- ✅ Parent approval dashboard
- ✅ All-time scores with crown
- ✅ Settings screen (language toggle)
- ✅ Cleaner management
- ✅ Chore management
- ✅ Bilingual support
- ✅ Daily score reset
- ⚠️ UI animations (basic animations added, more polish needed)
- ⚠️ Success dialogs (created but integration pending)
- ⚠️ Unit tests (not yet implemented)

## 🎨 UI/UX Design Notes

### Color Scheme
- **Primary**: Purple (#6200EE)
- **Secondary**: Teal (#03DAC6)
- **Success**: Green (#4CAF50)
- **Warning**: Amber (#FFD700)
- **Error**: Red (#E57373)

### Kid-Friendly Features
- Large, easy-to-tap buttons (48-56 dp)
- Emoji avatars for visual appeal
- Color-coded kids for quick identification
- Celebratory messages on success
- Simple, intuitive navigation

## 🔐 Security & Privacy

- **Local-only storage** - No data sent to cloud
- **No login required** - Trusted family device
- **No tracking** - No analytics or tracking
- **Privacy by default** - All data stays on device

## 📝 Future Enhancements

### Potential Features
1. Photo submissions for chore proof
2. Reward redemption system
3. Parent notifications
4. Chore difficulty levels
5. Time tracking for chores
6. Custom reward catalog
7. Export/backup functionality
8. Multi-device sync (optional)
9. Parent PIN protection for settings
10. Achievement badges/milestones

### UI/UX Improvements
1. More animations and transitions
2. Swipe gestures
3. Dark mode support
4. Customizable themes
5. Sound effects/haptic feedback
6. Progress indicators

## 🧪 Testing

To add tests:
```bash
./gradlew test              # Unit tests
./gradlew connectedAndroidTest  # Instrumented tests
```

## 📄 License

This project is provided as-is for family use.

## 👨‍👩‍👧‍👦 About

TidyUp was created to gamify household chores and encourage kids to take responsibility while having fun in a competitive, supportive environment.

**Happy cleaning! 🧹**

## Build
Open in Android Studio (AGP 9.2, compileSdk 36.1) or run `gradlew :app:assembleDebug` / `gradlew :app:testDebugUnitTest`. Uses KSP for Room and Hilt.
