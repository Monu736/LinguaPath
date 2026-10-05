package com.example.quiz

import com.example.data.model.LifestyleCategory
import com.example.data.model.QuizQuestion
import com.example.data.model.QuizType
import com.example.data.model.SupportedLanguage

data class CategoryQuizData(
    val category: LifestyleCategory,
    val levelNumber: Int,
    val title: String,
    val subtitle: String,
    val xpReward: Int,
    val questions: List<QuizQuestion>
)

object QuizContentGenerator {

    fun generateQuizForLevel(
        category: LifestyleCategory,
        levelNumber: Int,
        language: SupportedLanguage
    ): CategoryQuizData {
        val levelTier = when {
            levelNumber <= 20 -> "Beginner Tier • Core Vocabulary & Emojis"
            levelNumber <= 40 -> "Elementary Tier • Practical Phrases & Audio"
            levelNumber <= 60 -> "Intermediate Tier • Sentence Builder & Unscramble"
            levelNumber <= 80 -> "Advanced Tier • Street Slang & Fast Choice"
            else -> "Master Tier • Speed Blitz & Boss Run"
        }

        val levelTitle = getLevelTitle(category, levelNumber)
        val questions = createQuestions(category, levelNumber, language)

        return CategoryQuizData(
            category = category,
            levelNumber = levelNumber,
            title = "Level $levelNumber: $levelTitle",
            subtitle = levelTier,
            xpReward = 30 + (levelNumber / 10) * 10,
            questions = questions
        )
    }

    private fun getLevelTitle(category: LifestyleCategory, level: Int): String {
        return when (category) {
            LifestyleCategory.FOOD -> when (level % 10) {
                1 -> "Street Food & Snacks"
                2 -> "Ordering at a Café"
                3 -> "Spicy vs Sweet Flavors"
                4 -> "Breakfast Favorites"
                5 -> "Restaurant Etiquette & Bill"
                6 -> "Kitchen Verbs & Recipes"
                7 -> "Fast Food & Burgers"
                8 -> "Traditional Desserts"
                9 -> "Baking & Coffee Blends"
                else -> "Master Chef Feast"
            }
            LifestyleCategory.TRAVEL -> when (level % 10) {
                1 -> "Airport Check-in"
                2 -> "Asking Directions"
                3 -> "Train & Metro Tickets"
                4 -> "Hotel Room Booking"
                5 -> "Taxi & Ride Sharing"
                6 -> "Sightseeing Landmarks"
                7 -> "Packing Suitcase & Luggage"
                8 -> "Emergency & Medical Help"
                9 -> "Currency Exchange"
                else -> "Globetrotter Expedition"
            }
            LifestyleCategory.JOB -> when (level % 10) {
                1 -> "First Job Interview"
                2 -> "Resume & Core Skills"
                3 -> "Professional Email Writing"
                4 -> "Meeting Introductions"
                5 -> "Client Presentations"
                6 -> "Project Deadlines & Tasks"
                7 -> "Salary Negotiation"
                8 -> "Remote Work & Zoom Calls"
                9 -> "Office Etiquette & Watercooler"
                else -> "Executive Promotion Pitch"
            }
            LifestyleCategory.SCHOOL -> when (level % 10) {
                1 -> "Classroom Greetings"
                2 -> "Asking Teacher Questions"
                3 -> "Homework & Assignments"
                4 -> "Library & Quiet Study"
                5 -> "Canteen Gossips & Lunch"
                6 -> "Science Lab & Experiments"
                7 -> "Sports Day & Gym Class"
                8 -> "Exams & Study Groups"
                9 -> "College Fest & Campus Life"
                else -> "Graduation Day Honors"
            }
            LifestyleCategory.ROUTINE -> when (level % 10) {
                1 -> "Morning Alarm & Waking Up"
                2 -> "Brushing & Morning Tea"
                3 -> "Gym Workout & Stretching"
                4 -> "Daily Commute & Traffic"
                5 -> "Cooking Lunch at Home"
                6 -> "Afternoon Power Nap"
                7 -> "Cleaning & Home Chores"
                8 -> "Evening Walk & Sunset"
                9 -> "Nighttime Wind Down"
                else -> "Unstoppable Daily Habit"
            }
            LifestyleCategory.GAMING -> when (level % 10) {
                1 -> "Voice Comms & Discord"
                2 -> "Battle Royale Hot Drop"
                3 -> "Clutch 1v3 Victory"
                4 -> "Loot Chests & Weapons"
                5 -> "Nerfed vs Buffed Weapons"
                6 -> "Role Selection (DPS/Tank/Heal)"
                7 -> "Rage Quits & GG WP"
                8 -> "Ranked Competitive Grind"
                9 -> "Streamer Hype & Donos"
                else -> "Esports Grand Champion"
            }
            LifestyleCategory.SOCIAL_MEDIA -> when (level % 10) {
                1 -> "Viral Slang: Rizz & Cap"
                2 -> "Instagram Reel Captions"
                3 -> "TikTok Trends & Audios"
                4 -> "Vibe Check & Aesthetics"
                5 -> "DMs, Ghosting & Left on Read"
                6 -> "Meme Reactions & Emojis"
                7 -> "Flexing & Drip Check"
                8 -> "Hashtags & Going Viral"
                9 -> "Influencer Live Streams"
                else -> "Ultimate Internet GOAT"
            }
            LifestyleCategory.ENTERTAINMENT -> when (level % 10) {
                1 -> "Anime Quotes & Powers"
                2 -> "Pop Songs & Catchy Lyrics"
                3 -> "Movie Genres & Cinema"
                4 -> "Binge-watching Series"
                5 -> "Live Concerts & Festivals"
                6 -> "Webtoons & Manga Tropes"
                7 -> "Soundtrack & Heavy Beats"
                8 -> "Plot Twists & Spoilers"
                9 -> "Fan Theories & Fandoms"
                else -> "Pop Culture Icon"
            }
            LifestyleCategory.SHOPPING -> when (level % 10) {
                1 -> "Sneaker Drop & Footwear"
                2 -> "Bargaining at Street Bazaar"
                3 -> "Fitting Room & Sizing"
                4 -> "Discounts & Flash Sales"
                5 -> "Online Orders & Delivery"
                6 -> "Streetwear & Hoodie Styles"
                7 -> "Return Policy & Receipts"
                8 -> "Cash vs Digital Payment"
                9 -> "Thrifting & Vintage Finds"
                else -> "Fashion Runway Star"
            }
            LifestyleCategory.FRIENDS -> when (level % 10) {
                1 -> "Making Weekend Plans"
                2 -> "Crush Confessions & Dating"
                3 -> "Inside Jokes & Laughter"
                4 -> "Cheering Up a Sad Friend"
                5 -> "Late Night Deep Talks"
                6 -> "Apologies & Making Up"
                7 -> "Birthday Surprises"
                8 -> "Road Trip Roadies"
                9 -> "True Friendship Promises"
                else -> "Ride or Die Bestie"
            }
        }
    }

    private fun createQuestions(
        category: LifestyleCategory,
        level: Int,
        language: SupportedLanguage
    ): List<QuizQuestion> {
        return when (language) {
            SupportedLanguage.ENGLISH -> createEnglishQuestions(category, level)
            SupportedLanguage.HINDI -> createHindiQuestions(category, level)
            SupportedLanguage.MARATHI -> createMarathiQuestions(category, level)
            SupportedLanguage.JAPANESE -> createJapaneseQuestions(category, level)
        }
    }

    // ==========================================
    // 🇬🇧 ENGLISH QUESTIONS
    // ==========================================
    private fun createEnglishQuestions(category: LifestyleCategory, level: Int): List<QuizQuestion> {
        val list = mutableListOf<QuizQuestion>()

        when (category) {
            LifestyleCategory.FOOD -> {
                list.add(
                    QuizQuestion(
                        id = "en_food_${level}_1",
                        type = QuizType.MULTIPLE_CHOICE,
                        question = "Which polite phrase is used to ask for the bill at a restaurant?",
                        options = listOf("Could we have the check, please?", "Give me paper money!", "I want to exit now.", "How much cost everything?"),
                        correctAnswer = "Could we have the check, please?",
                        explanation = "'Could we have the check/bill, please?' is the standard polite request in restaurants."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "en_food_${level}_2",
                        type = QuizType.LISTENING,
                        question = "Listen to the chef's instruction and choose what was ordered:",
                        audioText = "One large iced latte with oat milk, extra caramel drizzle.",
                        options = listOf("Large iced latte with oat milk & caramel", "Hot black coffee with skim milk", "Green tea with honey", "Chocolate milkshake with extra cream"),
                        correctAnswer = "Large iced latte with oat milk & caramel",
                        explanation = "Audio says: 'One large iced latte with oat milk, extra caramel drizzle.'"
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "en_food_${level}_3",
                        type = QuizType.SENTENCE_ARRANGEMENT,
                        question = "Arrange the words to form a correct order:",
                        scrambledWords = listOf("table", "a", "like", "for", "two,", "We", "would", "please."),
                        targetSentence = "We would like a table for two, please.",
                        correctAnswer = "We would like a table for two, please.",
                        explanation = "Subject (We) + would like + Object (a table for two) + polite marker (please)."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "en_food_${level}_4",
                        type = QuizType.FILL_IN_BLANK,
                        question = "This curry is way too ____; could I have a glass of water?",
                        options = listOf("spicy", "bland", "crunchy", "frozen"),
                        correctAnswer = "spicy",
                        explanation = "Curry that makes you ask for water is 'spicy' (hot)."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "en_food_${level}_5",
                        type = QuizType.TRUE_FALSE,
                        question = "True or False: 'Delicious' and 'Mouthwatering' have almost the same positive meaning for food.",
                        options = listOf("True", "False"),
                        correctAnswer = "True",
                        explanation = "Both describe appealing, tasty food."
                    )
                )
            }
            LifestyleCategory.GAMING -> {
                list.add(
                    QuizQuestion(
                        id = "en_game_${level}_1",
                        type = QuizType.MULTIPLE_CHOICE,
                        question = "What does the gaming acronym 'GG' stand for at the end of a match?",
                        options = listOf("Good Game", "Get Going", "Great Guns", "Go Green"),
                        correctAnswer = "Good Game",
                        explanation = "'GG' is universally used by gamers as a sporting gesture meaning 'Good Game'."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "en_game_${level}_2",
                        type = QuizType.LISTENING,
                        question = "Listen to the squad leader's voice comms:",
                        audioText = "Enemy spotted on the roof! Cover me while I reload.",
                        options = listOf("Enemy on the roof, need cover to reload", "Heal me right now in the basement", "Fall back to the extraction zone", "Throw a smoke grenade over there"),
                        correctAnswer = "Enemy on the roof, need cover to reload",
                        explanation = "Audio says: 'Enemy spotted on the roof! Cover me while I reload.'"
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "en_game_${level}_3",
                        type = QuizType.SENTENCE_ARRANGEMENT,
                        question = "Unscramble the callout:",
                        scrambledWords = listOf("the", "dropped", "He", "legendary", "sniper", "rifle!"),
                        targetSentence = "He dropped the legendary sniper rifle!",
                        correctAnswer = "He dropped the legendary sniper rifle!",
                        explanation = "Subject (He) + Verb (dropped) + Object (the legendary sniper rifle)."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "en_game_${level}_4",
                        type = QuizType.FILL_IN_BLANK,
                        question = "When a player wins a 1v4 round with incredible skill, it is called a ____.",
                        options = listOf("clutch", "camp", "gank", "spawn"),
                        correctAnswer = "clutch",
                        explanation = "A 'clutch' is winning an intense round when outnumbered against all odds."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "en_game_${level}_5",
                        type = QuizType.MULTIPLE_CHOICE,
                        question = "What does 'Nerf' mean when game developers release a patch?",
                        options = listOf("Reduce the power of a weapon or character", "Make a weapon much stronger", "Remove sound effects", "Ban cheating players"),
                        correctAnswer = "Reduce the power of a weapon or character",
                        explanation = "'Nerf' means weakening an overpowered item or character."
                    )
                )
            }
            LifestyleCategory.SOCIAL_MEDIA -> {
                list.add(
                    QuizQuestion(
                        id = "en_social_${level}_1",
                        type = QuizType.MULTIPLE_CHOICE,
                        question = "What does modern Gen-Z slang 'No Cap' mean?",
                        options = listOf("No lie / Totally honest", "Don't wear a hat", "Ran out of internet data", "The video has no captions"),
                        correctAnswer = "No lie / Totally honest",
                        explanation = "'No cap' means 'for real' or 'I am telling the 100% truth'."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "en_social_${level}_2",
                        type = QuizType.LISTENING,
                        question = "Listen to this social media update:",
                        audioText = "This new dance trend is completely taking over my feed!",
                        options = listOf("New dance trend taking over feed", "Nobody is watching the new video", "Deleting all my social apps", "Post photos only on weekends"),
                        correctAnswer = "New dance trend taking over feed",
                        explanation = "Audio says: 'This new dance trend is completely taking over my feed!'"
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "en_social_${level}_3",
                        type = QuizType.SENTENCE_ARRANGEMENT,
                        question = "Arrange the viral caption:",
                        scrambledWords = listOf("The", "vibes", "were", "unmatched", "tonight!"),
                        targetSentence = "The vibes were unmatched tonight!",
                        correctAnswer = "The vibes were unmatched tonight!",
                        explanation = "'The vibes were unmatched tonight!' is a popular caption for memorable hangouts."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "en_social_${level}_4",
                        type = QuizType.FILL_IN_BLANK,
                        question = "If someone has unmatched charisma and charm when talking to their crush, they have high ____.",
                        options = listOf("Rizz", "Lag", "Ping", "Cringe"),
                        correctAnswer = "Rizz",
                        explanation = "'Rizz' (derived from charisma) means romantic charm or magnetism."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "en_social_${level}_5",
                        type = QuizType.TRUE_FALSE,
                        question = "True or False: Calling someone the 'G.O.A.T' is an insult.",
                        options = listOf("True", "False"),
                        correctAnswer = "False",
                        explanation = "False! 'G.O.A.T' stands for 'Greatest Of All Time', the highest compliment."
                    )
                )
            }
            LifestyleCategory.TRAVEL -> {
                list.add(
                    QuizQuestion(
                        id = "en_travel_${level}_1",
                        type = QuizType.MULTIPLE_CHOICE,
                        question = "Which question do you ask when you need directions to the nearest train station?",
                        options = listOf("Excuse me, could you tell me where the nearest station is?", "Why is there no station here?", "Drive me to the train right now.", "Where are trains existing?"),
                        correctAnswer = "Excuse me, could you tell me where the nearest station is?",
                        explanation = "Polite inquiry starting with 'Excuse me, could you tell me...'."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "en_travel_${level}_2",
                        type = QuizType.LISTENING,
                        question = "Listen to the airport boarding announcement:",
                        audioText = "Flight 402 to Tokyo is now boarding at Gate 18.",
                        options = listOf("Flight 402 to Tokyo boarding at Gate 18", "Flight 18 to London is cancelled", "Arriving flight from Paris delayed", "Boarding Gate 40 closing now"),
                        correctAnswer = "Flight 402 to Tokyo boarding at Gate 18",
                        explanation = "Audio says: 'Flight 402 to Tokyo is now boarding at Gate 18.'"
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "en_travel_${level}_3",
                        type = QuizType.SENTENCE_ARRANGEMENT,
                        question = "Arrange the check-in sentence:",
                        scrambledWords = listOf("I", "have", "reservation", "under", "a", "Gupta.", "the", "name"),
                        targetSentence = "I have a reservation under the name Gupta.",
                        correctAnswer = "I have a reservation under the name Gupta.",
                        explanation = "Standard hotel/airline check-in phrasing."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "en_travel_${level}_4",
                        type = QuizType.FILL_IN_BLANK,
                        question = "Please show your ____ and boarding pass at the security checkpoint.",
                        options = listOf("passport", "sneakers", "receipt", "menu"),
                        correctAnswer = "passport",
                        explanation = "Travelers must present their passport or government ID at airport security."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "en_travel_${level}_5",
                        type = QuizType.MULTIPLE_CHOICE,
                        question = "What does a 'round-trip' ticket include?",
                        options = listOf("Both going to a destination and returning back", "Only a one-way flight", "A free meal at the airport", "Unlimited bus rides in a circle"),
                        correctAnswer = "Both going to a destination and returning back",
                        explanation = "A round-trip ticket covers your departure and return journey."
                    )
                )
            }
            LifestyleCategory.JOB -> {
                list.add(
                    QuizQuestion(
                        id = "en_job_${level}_1",
                        type = QuizType.MULTIPLE_CHOICE,
                        question = "How should you professionally answer: 'Tell me about yourself' in a job interview?",
                        options = listOf("Summarize your education, top strengths, and relevant career achievements", "Talk about your favorite childhood cartoons for 10 minutes", "Say 'Everything is in my resume, just read it'", "Explain your entire life story starting from birth"),
                        correctAnswer = "Summarize your education, top strengths, and relevant career achievements",
                        explanation = "Keep it concise, relevant, and focused on your professional impact."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "en_job_${level}_2",
                        type = QuizType.LISTENING,
                        question = "Listen to the manager's email summary:",
                        audioText = "Please send over the updated financial report before Friday noon.",
                        options = listOf("Send updated financial report before Friday noon", "Cancel all meetings this Friday", "Prepare a keynote on Thursday night", "Review the client contract next Monday"),
                        correctAnswer = "Send updated financial report before Friday noon",
                        explanation = "Audio says: 'Please send over the updated financial report before Friday noon.'"
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "en_job_${level}_3",
                        type = QuizType.SENTENCE_ARRANGEMENT,
                        question = "Unscramble this professional sign-off:",
                        scrambledWords = listOf("forward", "hearing", "to", "I", "look", "from", "you."),
                        targetSentence = "I look forward to hearing from you.",
                        correctAnswer = "I look forward to hearing from you.",
                        explanation = "'I look forward to hearing from you' is a polite closing statement."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "en_job_${level}_4",
                        type = QuizType.FILL_IN_BLANK,
                        question = "We need to finish this project before the strict client ____ on Wednesday.",
                        options = listOf("deadline", "lunch", "bonus", "vacation"),
                        correctAnswer = "deadline",
                        explanation = "A 'deadline' is the final date or time by which something must be finished."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "en_job_${level}_5",
                        type = QuizType.TRUE_FALSE,
                        question = "True or False: Using all CAPS in a professional email implies SHOUTING and is considered rude.",
                        options = listOf("True", "False"),
                        correctAnswer = "True",
                        explanation = "Writing in ALL CAPS gives an aggressive impression in workplace communications."
                    )
                )
            }
            else -> {
                // Default fallback rich questions for SCHOOL, ROUTINE, ENTERTAINMENT, SHOPPING, FRIENDS
                list.add(
                    QuizQuestion(
                        id = "en_gen_${level}_1",
                        type = QuizType.MULTIPLE_CHOICE,
                        question = "Which phrase best expresses appreciation to a close friend?",
                        options = listOf("I really appreciate your support!", "You owe me money.", "Don't bother me.", "Whatever you say."),
                        correctAnswer = "I really appreciate your support!",
                        explanation = "Expressing gratitude strengthens friendships."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "en_gen_${level}_2",
                        type = QuizType.LISTENING,
                        question = "Listen and identify the statement:",
                        audioText = "Let us catch up over coffee this Saturday afternoon.",
                        options = listOf("Catch up over coffee Saturday afternoon", "Study alone in the room all weekend", "Cancel the Saturday party", "Go to the supermarket Friday morning"),
                        correctAnswer = "Catch up over coffee Saturday afternoon",
                        explanation = "Audio says: 'Let us catch up over coffee this Saturday afternoon.'"
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "en_gen_${level}_3",
                        type = QuizType.SENTENCE_ARRANGEMENT,
                        question = "Arrange the words correctly:",
                        scrambledWords = listOf("having", "fun", "We", "today.", "are", "so", "much"),
                        targetSentence = "We are having so much fun today.",
                        correctAnswer = "We are having so much fun today.",
                        explanation = "Subject (We) + are having + so much fun today."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "en_gen_${level}_4",
                        type = QuizType.FILL_IN_BLANK,
                        question = "I set my ____ for 6:30 AM so I wouldn't be late for morning classes.",
                        options = listOf("alarm", "pillow", "sneaker", "wardrobe"),
                        correctAnswer = "alarm",
                        explanation = "An alarm clock wakes you up on time."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "en_gen_${level}_5",
                        type = QuizType.TRUE_FALSE,
                        question = "True or False: The word 'Binge-watch' means watching multiple episodes of a show in one sitting.",
                        options = listOf("True", "False"),
                        correctAnswer = "True",
                        explanation = "'Binge-watch' is a modern pop culture word for marathon watching."
                    )
                )
            }
        }

        return list
    }

    // ==========================================
    // 🇮🇳 HINDI QUESTIONS (With Devanagari + Roman Transliteration)
    // ==========================================
    private fun createHindiQuestions(category: LifestyleCategory, level: Int): List<QuizQuestion> {
        val list = mutableListOf<QuizQuestion>()

        when (category) {
            LifestyleCategory.FOOD -> {
                list.add(
                    QuizQuestion(
                        id = "hi_food_${level}_1",
                        type = QuizType.MULTIPLE_CHOICE,
                        question = "रेस्टोरेंट में बिल माँगने के लिए कौन सा वाक्य सही है? (Which is the polite phrase to ask for the bill?)",
                        subtitle = "Aap bill mangne ke liye kya kahenge?",
                        options = listOf("कृपया बिल ले आइए (Kripya bill le aaiye)", "पैसे ले जाओ (Paise le jao)", "मुझे खाना नहीं चाहिए (Mujhe khana nahi chahiye)", "दुकान बंद करो (Dukan band karo)"),
                        correctAnswer = "कृपया बिल ले आइए (Kripya bill le aaiye)",
                        explanation = "'कृपया बिल ले आइए' (Please bring the bill) is the respectful way to ask for the check."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "hi_food_${level}_2",
                        type = QuizType.LISTENING,
                        question = "ऑडियो सुनें और सही अर्थ चुनें: (Listen to native Hindi audio):",
                        audioText = "भैया, पानी पूरी में तीखा थोड़ा कम रखना।",
                        options = listOf("Keep the spice low in pani puri", "Put extra red chili in everything", "Pack 10 plates of samosas", "I want hot sweet milk"),
                        correctAnswer = "Keep the spice low in pani puri",
                        explanation = "'भैया, पानी पूरी में तीखा थोड़ा कम रखना' means 'Brother, please keep the spice a bit less in pani puri.'"
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "hi_food_${level}_3",
                        type = QuizType.SENTENCE_ARRANGEMENT,
                        question = "सही क्रम में वाक्य बनाएँ: (Arrange the words in SOV order):",
                        scrambledWords = listOf("खाना", "बहुत", "यह", "स्वादिष्ट", "है।"),
                        targetSentence = "यह खाना बहुत स्वादिष्ट है।",
                        correctAnswer = "यह खाना बहुत स्वादिष्ट है।",
                        explanation = "Hindi SOV Word order: यह खाना (Subject) + बहुत स्वादिष्ट (Object/Adj) + है (Verb)."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "hi_food_${level}_4",
                        type = QuizType.FILL_IN_BLANK,
                        question = "मुझे सुबह एक कप गरम ____ पीने की आदत है। (I have a habit of drinking hot ____ in the morning)",
                        options = listOf("चाय (Tea / Chai)", "नमक (Salt)", "रोटी (Roti)", "चावल (Rice)"),
                        correctAnswer = "चाय (Tea / Chai)",
                        explanation = "चाय (Tea/Chai) is the classic hot morning beverage."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "hi_food_${level}_5",
                        type = QuizType.TRUE_FALSE,
                        question = "सच या झूठ (True or False): 'स्वादिष्ट' (Swaadisht) और 'लाजवाब' (Laajawaab) दोनों खाने की तारीफ़ के शब्द हैं।",
                        options = listOf("सच (True)", "झूठ (False)"),
                        correctAnswer = "सच (True)",
                        explanation = "Both words mean delicious and extraordinary in culinary praise."
                    )
                )
            }
            LifestyleCategory.GAMING -> {
                list.add(
                    QuizQuestion(
                        id = "hi_game_${level}_1",
                        type = QuizType.MULTIPLE_CHOICE,
                        question = "गेमिंग में जब कोई दोस्त शानदार खेल दिखाए, तो क्या कहते हैं?",
                        subtitle = "Gaming slang for amazing play:",
                        options = listOf("क्या क्लच मारा यार! (OP Play)", "गेम डिलीट कर दे", "इंटरनेट बंद करो", "बैटरी खत्म हो गई"),
                        correctAnswer = "क्या क्लच मारा यार! (OP Play)",
                        explanation = "'OP' (Overpowered) और 'क्लच' गेमर्स के पसंदीदा तारीफ़ के शब्द हैं।"
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "hi_game_${level}_2",
                        type = QuizType.LISTENING,
                        question = "वॉइस चैट सुनें और सही जवाब चुनें:",
                        audioText = "जल्दी आओ, छत पर दो बंदे हैं!",
                        options = listOf("Come quick, two enemies on the roof!", "Let us buy food in the market", "The game is updating now", "Turn off your microphone"),
                        correctAnswer = "Come quick, two enemies on the roof!",
                        explanation = "'जल्दी आओ, छत पर दो बंदे हैं!' means 'Come quick, two players/enemies on the roof!'"
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "hi_game_${level}_3",
                        type = QuizType.SENTENCE_ARRANGEMENT,
                        question = "वाक्य सही क्रम में लगाएँ:",
                        scrambledWords = listOf("हम", "यह", "मैच", "ज़रूर", "जीतेंगे!"),
                        targetSentence = "हम यह मैच ज़रूर जीतेंगे!",
                        correctAnswer = "हम यह मैच ज़रूर जीतेंगे!",
                        explanation = "'हम यह मैच ज़रूर जीतेंगे!' means 'We will definitely win this match!'"
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "hi_game_${level}_4",
                        type = QuizType.FILL_IN_BLANK,
                        question = "इंटरनेट धीमा होने से गेम में ____ बढ़ जाता है। (Due to slow internet, ____ increases)",
                        options = listOf("पिंग (Ping/Lag)", "जीत (Win)", "रिवॉर्ड (Reward)", "ग्राफिक्स (Graphics)"),
                        correctAnswer = "पिंग (Ping/Lag)",
                        explanation = "Slow internet causes high ping or lag."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "hi_game_${level}_5",
                        type = QuizType.TRUE_FALSE,
                        question = "सच या झूठ: 'GG' का मतलब 'Good Game' (अच्छा मुकाबला) होता है।",
                        options = listOf("सच (True)", "झूठ (False)"),
                        correctAnswer = "सच (True)",
                        explanation = "GG is standard worldwide gaming etiquette."
                    )
                )
            }
            LifestyleCategory.TRAVEL -> {
                list.add(
                    QuizQuestion(
                        id = "hi_travel_${level}_1",
                        type = QuizType.MULTIPLE_CHOICE,
                        question = "रास्ता पूछने के लिए कौन सा सवाल सबसे विनम्र है? (Most polite way to ask directions):",
                        options = listOf("माफ़ कीजिए, रेलवे स्टेशन किस तरफ़ है? (Excuse me, which way is the railway station?)", "स्टेशन कहाँ छुपा रखा है?", "मुझे स्टेशन ले चलो अभी!", "यहाँ ट्रेन क्यों नहीं आती?"),
                        correctAnswer = "माफ़ कीजिए, रेलवे स्टेशन किस तरफ़ है? (Excuse me, which way is the railway station?)",
                        explanation = "'माफ़ कीजिए...' (Excuse me) shows respect and politeness."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "hi_travel_${level}_2",
                        type = QuizType.LISTENING,
                        question = "मेट्रो स्टेशन की घोषणा सुनें:",
                        audioText = "अगला स्टेशन राजीव चौक है, दरवाजे बाईं तरफ खुलेंगे।",
                        options = listOf("Next station is Rajiv Chowk, doors open on the left", "Train terminating immediately at first stop", "Platform change to number nine", "Tickets not available here"),
                        correctAnswer = "Next station is Rajiv Chowk, doors open on the left",
                        explanation = "Famous Delhi Metro announcement translated verbatim."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "hi_travel_${level}_3",
                        type = QuizType.SENTENCE_ARRANGEMENT,
                        question = "वाक्य को सही क्रम में जमाएँ:",
                        scrambledWords = listOf("होटल", "में", "मेरा", "कमरा", "बुक", "है।"),
                        targetSentence = "होटल में मेरा कमरा बुक है।",
                        correctAnswer = "होटल में मेरा कमरा बुक है।",
                        explanation = "My room is booked in the hotel."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "hi_travel_${level}_4",
                        type = QuizType.FILL_IN_BLANK,
                        question = "विदेश यात्रा के लिए ____ और वीज़ा सबसे ज़रूरी दस्तावेज़ हैं।",
                        options = listOf("पासपोर्ट (Passport)", "चश्मा (Glasses)", "छाता (Umbrella)", "खिलौना (Toy)"),
                        correctAnswer = "पासपोर्ट (Passport)",
                        explanation = "A passport and visa are mandatory for international travel."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "hi_travel_${level}_5",
                        type = QuizType.MULTIPLE_CHOICE,
                        question = "'किराया' (Kiraya) शब्द का क्या अर्थ है?",
                        options = listOf("Fare / Rent / Cost of travel", "Speed of the car", "Distance in kilometers", "Traffic light"),
                        correctAnswer = "Fare / Rent / Cost of travel",
                        explanation = "किराया means fare or rent."
                    )
                )
            }
            else -> {
                list.add(
                    QuizQuestion(
                        id = "hi_gen_${level}_1",
                        type = QuizType.MULTIPLE_CHOICE,
                        question = "'शुभ प्रभात' (Shubh Prabhat) का सही अंग्रेजी अनुवाद क्या है?",
                        options = listOf("Good morning", "Good night", "Thank you very much", "See you tomorrow"),
                        correctAnswer = "Good morning",
                        explanation = "शुभ प्रभात means Good morning."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "hi_gen_${level}_2",
                        type = QuizType.LISTENING,
                        question = "ऑडियो सुनें और सही वाक्य पहचानें:",
                        audioText = "आपसे मिलकर बहुत खुशी हुई।",
                        options = listOf("Nice to meet you / Glad to meet you", "Where are you going right now?", "I don't know the answer", "Please wait here for five minutes"),
                        correctAnswer = "Nice to meet you / Glad to meet you",
                        explanation = "'आपसे मिलकर बहुत खुशी हुई' means 'Nice to meet you.'"
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "hi_gen_${level}_3",
                        type = QuizType.SENTENCE_ARRANGEMENT,
                        question = "सही क्रम में वाक्य बनाएँ:",
                        scrambledWords = listOf("मैं", "रोज़", "नया", "कुछ", "सीखता", "हूँ।"),
                        targetSentence = "मैं रोज़ कुछ नया सीखता हूँ।",
                        correctAnswer = "मैं रोज़ कुछ नया सीखता हूँ।",
                        explanation = "I learn something new every day."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "hi_gen_${level}_4",
                        type = QuizType.FILL_IN_BLANK,
                        question = "दोस्ती में विश्वास और ____ सबसे बड़ी ताक़त होती है।",
                        options = listOf("प्यार (Love / Care)", "गुस्सा (Anger)", "धोखा (Betrayal)", "झूठ (Lies)"),
                        correctAnswer = "प्यार (Love / Care)",
                        explanation = "Love and trust build strong friendships."
                    )
                )
                list.add(
                    QuizQuestion(
                        id = "hi_gen_${level}_5",
                        type = QuizType.TRUE_FALSE,
                        question = "सच या झूठ: 'धन्यवाद' (Dhanyavaad) का अर्थ 'Thank you' होता है।",
                        options = listOf("सच (True)", "झूठ (False)"),
                        correctAnswer = "सच (True)",
                        explanation = "धन्यवाद means Thank you."
                    )
                )
            }
        }

        return list
    }

    // ==========================================
    // 🇮🇳 MARATHI QUESTIONS (Side Course with Devanagari & Transliteration)
    // ==========================================
    private fun createMarathiQuestions(category: LifestyleCategory, level: Int): List<QuizQuestion> {
        val list = mutableListOf<QuizQuestion>()

        list.add(
            QuizQuestion(
                id = "mr_${category.key}_${level}_1",
                type = QuizType.MULTIPLE_CHOICE,
                question = "मराठीत 'नमस्कार / कसे आहात?' चा अर्थ काय आहे? (What is the meaning in English?)",
                subtitle = "Namaskar / Kase aahat?",
                options = listOf("Hello / How are you?", "Goodbye and good night", "Where is the restaurant?", "Thank you very much"),
                correctAnswer = "Hello / How are you?",
                explanation = "'कसे आहात?' means 'How are you?' in Marathi."
            )
        )
        list.add(
            QuizQuestion(
                id = "mr_${category.key}_${level}_2",
                type = QuizType.LISTENING,
                question = "मराठी ऑडिओ ऐका आणि योग्य अर्थ निवडा: (Listen to Marathi audio):",
                audioText = "मला खूप भूक लागली आहे, काहीतरी छान खायला द्या.",
                options = listOf("I am very hungry, give me something nice to eat", "I am going to sleep right now", "Where can I buy a train ticket?", "Please speak more slowly"),
                correctAnswer = "I am very hungry, give me something nice to eat",
                explanation = "'मला खूप भूक लागली आहे' means 'I am feeling very hungry.'"
            )
        )
        list.add(
            QuizQuestion(
                id = "mr_${category.key}_${level}_3",
                type = QuizType.SENTENCE_ARRANGEMENT,
                question = "शब्दांची योग्य रचना करा: (Arrange in Marathi SOV order):",
                scrambledWords = listOf("मी", "मराठी", "भाषण", "शिकत", "आहे."),
                targetSentence = "मी मराठी भाषण शिकत आहे.",
                correctAnswer = "मी मराठी भाषण शिकत आहे.",
                explanation = "Subject (मी) + Object (मराठी भाषण) + Verb (शिकत आहे)."
            )
        )
        list.add(
            QuizQuestion(
                id = "mr_${category.key}_${level}_4",
                type = QuizType.FILL_IN_BLANK,
                question = "पुण्यात प्रसिद्ध नाश्ता ____ आणि पोहे खूप लोकप्रिय आहेत.",
                options = listOf("मिसळ पाव (Misal Pav)", "पिझ्झा (Pizza)", "बर्गर (Burger)", "आईस्क्रीम (Ice cream)"),
                correctAnswer = "मिसळ पाव (Misal Pav)",
                explanation = "Misal Pav and Pohe are Maharashtra's iconic culinary favorites."
            )
        )
        list.add(
            QuizQuestion(
                id = "mr_${category.key}_${level}_5",
                type = QuizType.TRUE_FALSE,
                question = "खरे की खोटे (True or False): 'धन्यवाद' (Dhanyavaad) चा अर्थ आभार मानणे (Thank you) असा होतो.",
                options = listOf("खरे (True)", "खोटे (False)"),
                correctAnswer = "खरे (True)",
                explanation = "'धन्यवाद' means Thank you in Marathi."
            )
        )

        return list
    }

    // ==========================================
    // 🇯🇵 JAPANESE QUESTIONS (Side Course with Kanji/Kana + Romaji)
    // ==========================================
    private fun createJapaneseQuestions(category: LifestyleCategory, level: Int): List<QuizQuestion> {
        val list = mutableListOf<QuizQuestion>()

        list.add(
            QuizQuestion(
                id = "ja_${category.key}_${level}_1",
                type = QuizType.MULTIPLE_CHOICE,
                question = "What does 'こんにちは (Konnichiwa)' mean in Japanese?",
                subtitle = "Romaji: Konnichiwa",
                options = listOf("Hello / Good afternoon", "Goodbye", "Delicious food", "Excuse me"),
                correctAnswer = "Hello / Good afternoon",
                explanation = "'こんにちは (Konnichiwa)' is the standard greeting for hello / good afternoon."
            )
        )
        list.add(
            QuizQuestion(
                id = "ja_${category.key}_${level}_2",
                type = QuizType.LISTENING,
                question = "Listen to the Japanese phrase said before eating a meal:",
                audioText = "いただきます！",
                options = listOf("Itadakimasu! (I humbly receive this food)", "Gochisousama! (Thank you for the meal)", "Oishii desu! (It tastes good)", "Sayonara! (Farewell)"),
                correctAnswer = "Itadakimasu! (I humbly receive this food)",
                explanation = "Japanese culture always starts meals with 'いただきます (Itadakimasu)'."
            )
        )
        list.add(
            QuizQuestion(
                id = "ja_${category.key}_${level}_3",
                type = QuizType.SENTENCE_ARRANGEMENT,
                question = "Arrange the Japanese sentence (Watashi wa gakusei desu):",
                scrambledWords = listOf("私", "は", "学生", "です。"),
                targetSentence = "私は学生です。",
                correctAnswer = "私は学生です。",
                explanation = "Topic (Watashi) + Particle (wa) + Noun (gakusei = student) + Copula (desu)."
            )
        )
        list.add(
            QuizQuestion(
                id = "ja_${category.key}_${level}_4",
                type = QuizType.FILL_IN_BLANK,
                question = "When food is delicious, you exclaim: 'これ、とても ____ です！ (Kore, totemo ____ desu!)'",
                options = listOf("美味しい (Oishii / Delicious)", "高い (Takai / Expensive)", "遠い (Tooi / Far)", "寒い (Samui / Cold)"),
                correctAnswer = "美味しい (Oishii / Delicious)",
                explanation = "'美味しい (Oishii)' means tasty or delicious."
            )
        )
        list.add(
            QuizQuestion(
                id = "ja_${category.key}_${level}_5",
                type = QuizType.TRUE_FALSE,
                question = "True or False: 'ありがとう (Arigatou)' means 'Thank you'.",
                options = listOf("True", "False"),
                correctAnswer = "True",
                explanation = "'ありがとう (Arigatou)' is the casual expression for thank you."
            )
        )

        return list
    }
}
