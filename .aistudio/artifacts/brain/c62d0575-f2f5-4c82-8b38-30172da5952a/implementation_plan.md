# Lingopath: Unsequenced Quizzes, Diamond Shop, Authentic Auth & Real Progression

A comprehensive transformation for **Lingopath** to ensure genuine learning, fair quizzes, a rich diamond economy, complete removal of fake leaderboard bots, and a real user authentication system.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following decisions synthesize the user's requirements and clarifying answers:

- **Language & Jetpack Compose Choice**:
  - *User Question*: "Can't we use Java with Jetpack Compose?"
  - *Technical Grounding & Decision*: Google created Jetpack Compose specifically as a Kotlin-first declarative framework; it relies directly on the Kotlin Compiler plugin (`@Composable`), inline reified types, and coroutines. Compose cannot be authored in Java. We retain modern Kotlin + Jetpack Compose so the tactile 3D physical buttons, smooth 60fps screen transitions, audio feedback, and reactive state management remain fast, stable, and bug-free.
- **Leaderboard Removal**:
  - *Confirmed*: The fake leaderboard with mock competitor bots ("Ruby League Rank #4", Aarav Sharma, etc.) is completely removed from all screens, banners, and modals.
- **Quiz Shuffling & Challenge**:
  - *Confirmed*: All quiz questions (multiple choice, listening dialogues, fill-in-the-blanks) will dynamically shuffle option choices upon presentation so option 1 is never predictably the correct answer. Answer matching is position-agnostic.
- **Diamond Economy & Shop**:
  - *Confirmed*: Users can spend their earned Diamonds (💎) on cosmetic profile frames, unique avatars, level fast-passes, streak freezes, and heart refills.
- **Authentication**:
  - *Confirmed*: Support both Google Sign-In and Email with Password credentials with real session persistence.

---

## 1. Overview & Core Concept

**Lingopath** is a personal language trainer and gamified lifestyle learning companion for English, Hindi, Marathi, and Japanese.

- **What It Does**: Provides 1,000 progressive lifestyle lessons across 10 categories (Food, Travel, Office, College, Gaming, Pop Culture, etc.), interactive quiz evaluations, live AI conversational practice, and a diamond rewards system.
- **The Problem Solved**:
  1. Quizzes previously had the correct answer positioned as the first choice (`options[0]`), making quizzes predictable and unchallenging.
  2. Mock rival bots created a fake sense of competition.
  3. Diamonds lacked utility beyond basic heart refills.
  4. User identity was a local guest profile rather than a verified account.
- **Key Value**: Every piece of progress, score, achievement, and coin balance in Lingopath is 100% authentic, challenging, and tied to the user's real learning journey.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **App Identity**: The app is renamed to **Lingopath** across launcher titles, headers, and metadata.
2. **Clean Home Screen (No Fake Bots)**:
   - The top Duolingo-style status bar displays Language selector, Streak 🔥, Diamonds 💎, and Hearts ❤️.
   - The fake "Ruby League #4" bot card is replaced with real user-driven elements: **Daily Quests Progress**, **Category Roadmaps**, and **Level Mastery Highlights**.
3. **Challenging, Unsequenced Quiz Arena**:
   - When entering any of the 100 levels, question options are shuffled cleanly on each quiz attempt.
   - The user cannot simply click option #1 to win; they must evaluate the vocabulary and grammar context.
   - Immediate tactile feedback indicates correct (green celebration) or incorrect (red review banner) solutions with explanation breakdowns.
4. **Expanded Diamond Shop**:
   - Accessible via the diamond pill in the top bar or shop button.
   - **Avatar Frames**: Bronze Polyglot (150 💎), Silver Linguist (300 💎), Royal Gold Champion (500 💎), Cyber Neon Glow (750 💎).
   - **Exclusive Avatars**: Clever Fox (250 💎), Grandmaster Owl (400 💎), Linguistic Lion (600 💎), Mythic Dragon (1000 💎).
   - **Level Fast-Pass**: Unlock the next 5 levels in any chosen lifestyle category (150 💎).
   - **Consumables**: Heart Refill (100 💎), Streak Freeze (200 💎).
   - Immediate visual equipping on the user's profile and top bar!
5. **Real Authentication & Profile Management**:
   - Login dialog offering **Google Sign-In** and **Email & Password** authentication.
   - Includes full form validation, account registration/login switching, password toggling, and active session status.
   - Logged-in profile displays equipped frames, acquired avatars, real XP, and vocabulary mastery stats.

### Visual Styling & Theme

```
┌────────────────────────────────────────────────────────┐
│                      LINGOPATH                         │
├────────────────────────────────────────────────────────┤
│ [🇬🇧 EN]   🔥 3 Days   💎 450 Gems   ❤️ 5 Hearts   [⚡] │
├────────────────────────────────────────────────────────┤
│  Hello, Monu Gupta! 👋                                 │
│  📧 monugupta7478@gmail.com • Google Verified          │
├────────────────────────────────────────────────────────┤
│  🎯 DAILY QUESTS CARD                                  │
│  ├─ Earn 50 XP today (25/50)                           │
│  ├─ Complete 3 Quiz Levels (1/3)                       │
│  └─ Speak with AI Partner (2/3 mins)                   │
├────────────────────────────────────────────────────────┤
│  🍕 FOOD & DINING (100 Levels)                         │
│  [1: Samosa] -> [2: Cutting Chai] -> [🔒 3] -> ...     │
├────────────────────────────────────────────────────────┤
│  💎 GEM SHOP (FRAMES, AVATARS, LEVEL UNLOCKS)          │
└────────────────────────────────────────────────────────┘
```

---

## 3. Key Product Decisions & Trade-Offs

- **Option Shuffling Strategy**:
  - *Approach*: Perform dynamic randomized shuffling (`options.shuffled()`) in `LevelLessonBlueprint.toQuestions()` or when initializing question state in `InteractiveDuoQuizRunner`.
  - *Why*: Ensures every time a question is rendered, the options appear in randomized positions, and the user's selection is matched directly against `correctAnswer` by value rather than index.
- **Purging Fake Leaderboards**:
  - *Approach*: Completely remove `DuoLeaguesDialog` and the fake leaderboard strip from `HomeScreen.kt` and `DuoComponents.kt`.
  - *Why*: User explicitly requested complete removal of the fake competitor bots to ensure complete authenticity.
- **Equippable Frames & Avatars in Diamond Shop**:
  - *Approach*: Add `avatarFrame`, `unlockedFrames`, and `unlockedAvatars` fields to `UserProfile`. When a user spends diamonds in `DuoShopDialog`, verify diamond balance, deduct cost, add the item to unlocked inventory, and auto-equip it.
  - *Visual Reward*: Renders a distinctive glowing or colored border around the profile avatar throughout the app (top bar and profile screen).
- **Authentication**:
  - *Approach*: Build a real authentication dialog and session controller in the app allowing users to sign in with Google or Email/Password, with persistent local credential caching and profile binding.

---

## 4. Technical Architecture & Data Strategy

```
┌─────────────────────────────────────────────────────────────────┐
│                       Lingopath App UI                          │
├─────────────────────────────────────────────────────────────────┤
│  HomeScreen        QuizArenaScreen       ProfileScreen          │
│  (Top Bar, Quests, (Shuffled Options,    (Avatar Frames,        │
│   Category Paths)   Hearts, Tactile)      Auth Status, Stats)   │
├─────────────────────────────────────────────────────────────────┤
│                        UI Modals                                │
│  ┌──────────────────────┐  ┌─────────────────────────────────┐  │
│  │   DuoShopDialog      │  │      ProfileLoginDialog         │  │
│  │ (Frames, Avatars,    │  │ (Google Sign-In, Email/Password,│  │
│  │  Level Fast-Pass)    │  │  Session State, Avatar Preview) │  │
│  └──────────────────────┘  └─────────────────────────────────┘  │
├─────────────────────────────────────────────────────────────────┤
│                       TrainerViewModel                          │
│  • onBuyShopItem(itemId, cost)                                  │
│  • onEquipFrame(frameKey)                                       │
│  • onAuthenticate(email, password, isGoogle)                    │
│  • onUnlockCategoryLevels(category, count)                      │
├─────────────────────────────────────────────────────────────────┤
│                 Database & Persistent Storage                   │
│  • UserProfile (Gems, Hearts, Equipped Frame, Auth Info)        │
│  • QuizLevelProgress (1,000 Levels unlocked & stars)            │
│  • Spaced Repetition Vocabulary & Mistake Log                   │
└─────────────────────────────────────────────────────────────────┘
```

### Component & File Implementation Map

1. **App Identity**:
   - `metadata.json`: Update name to "Lingopath", keep `majorCapabilities`.
   - `app/src/main/res/values/strings.xml`: Update `app_name` to "Lingopath".
   - `settings.gradle.kts`: Update `rootProject.name = "Lingopath"`.
2. **Quiz Unsequencing & Difficulty**:
   - `CommonModels.kt`: In `LevelLessonBlueprint.toQuestions()`, shuffle `mcqOptions.shuffled()`, `audioOptions.shuffled()`, and `fillOptions.shuffled()` so the correct answer is randomized across positions.
   - `QuizArenaScreen.kt`: Ensure option buttons reference the shuffled option string directly for selection and correctness checks.
3. **Leaderboard Removal**:
   - `HomeScreen.kt`: Remove the Ruby League leaderboard card and dialog trigger.
   - `DuoComponents.kt`: Remove `DuoLeaguesDialog`.
4. **Diamond Shop & Economy Expansion**:
   - `Models.kt`: Add `avatarFrame`, `unlockedFrames`, `unlockedAvatars` to `UserProfile`.
   - `DuoComponents.kt`: Expand `DuoShopDialog` with categories:
     - 🎨 Avatar Frames (Bronze, Silver, Royal Gold, Cyber Neon).
     - 🎭 Unlockable Avatars (Fox, Owl, Lion, Dragon).
     - 🔓 Level Fast-Pass (Instant unlock 5 levels).
     - ❤️ Heart Refills & 🧊 Streak Freezes.
   - `TrainerViewModel.kt` & `TrainerRepository.kt`: Add methods to purchase frames, purchase avatars, equip cosmetics, and unlock category levels using diamonds.
5. **Real Authentication System**:
   - `ProfileModal.kt`: Enhance `ProfileLoginDialog` into a full authentication screen supporting:
     - Google Sign-In button with branded styling.
     - Email & Password input with togglable password visibility and tab switching (Sign In vs Sign Up).
     - Real session persistence updating `UserProfile`.
6. **Avatar Frame Display**:
   - Update avatar containers in `HomeScreen.kt`, `ProfileScreen.kt`, and `DuoTopBar` to render the equipped border frame styling (e.g., Neon glow, Gold border, Bronze badge).
