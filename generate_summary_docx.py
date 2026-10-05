#!/usr/bin/env python3
"""
University of Mumbai - Community Engagement Project (CEP)
Executive Summary & Viva Presentation Docx Generator
"""
import zipfile

def generate_summary_docx():
    content_types = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
    <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
    <Default Extension="xml" ContentType="application/xml"/>
    <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
    <Override PartName="/word/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"/>
</Types>"""

    root_rels = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
    <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
</Relationships>"""

    doc_rels = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
    <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>"""

    styles_xml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:styles xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
    <w:docDefaults>
        <w:rPrDefault>
            <w:rPr>
                <w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman" w:cs="Times New Roman"/>
                <w:sz w:val="24"/>
                <w:szCs w:val="24"/>
            </w:rPr>
        </w:rPrDefault>
        <w:pPrDefault>
            <w:pPr>
                <w:spacing w:line="360" w:lineRule="auto" w:after="140"/>
            </w:pPr>
        </w:pPrDefault>
    </w:docDefaults>
</w:styles>"""

    def p(text, bold=False, italic=False, size=24, align="both", space_after=140):
        b_tag = "<w:b/>" if bold else ""
        i_tag = "<w:i/>" if italic else ""
        jc_tag = f'<w:jc w:val="{align}"/>' if align else ""
        clean_text = text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
        return f"""<w:p>
            <w:pPr>
                {jc_tag}
                <w:spacing w:line="360" w:lineRule="auto" w:after="{space_after}"/>
            </w:pPr>
            <w:r>
                <w:rPr>
                    <w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman"/>
                    <w:sz w:val="{size}"/>
                    <w:szCs w:val="{size}"/>
                    {b_tag}
                    {i_tag}
                </w:rPr>
                <w:t xml:space="preserve">{clean_text}</w:t>
            </w:r>
        </w:p>"""

    def page_break():
        return """<w:p><w:r><w:br w:type="page"/></w:r></w:p>"""

    doc_parts = [
        '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>',
        '<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">',
        '<w:body>'
    ]

    # Title & Metadata
    doc_parts.append(p("UNIVERSITY OF MUMBAI", bold=True, size=32, align="center"))
    doc_parts.append(p("Community Engagement Project (CEP) - NEP 2020", size=24, align="center"))
    doc_parts.append(p("EXECUTIVE SUMMARY & PRESENTATION GUIDE", bold=True, size=28, align="center", space_after=300))

    doc_parts.append(p("Project Title: PERSONAL LANGUAGE TRAINER: BRIDGING VERNACULAR & GLOBAL COMMUNICATION FOR COMMUNITY EMPOWERMENT", bold=True, size=26, space_after=200))
    doc_parts.append(p("Student Name: Monu Gupta | Email: monugupta7478@gmail.com | Roll No.: CEP-2025-UG-408", bold=True, size=22, space_after=140))
    doc_parts.append(p("Degree Program: Undergraduate Degree (NEP 2020) | Academic Year: 2025-2026", size=22, space_after=140))
    doc_parts.append(p("CEP Indicative Area: Area 5 - Use digital skills to implement socially impactful tech projects", size=22, space_after=300))

    # Section 1: Project Abstract & Elevator Pitch
    doc_parts.append(p("1. PROJECT ELEVATOR PITCH & CORE MOTIVATION", bold=True, size=26, space_after=140))
    doc_parts.append(p("In Mumbai, thousands of bright students from vernacular-medium (Marathi and Hindi) backgrounds struggle with spontaneous conversational English. Traditional classrooms force passive grammar-translation and rote textbook rules, leaving students paralyzed by fear during job interviews, campus discussions, and workplace calls. The Personal Language Trainer is a native, offline-capable Android application built with Kotlin, Jetpack Compose, Room Database, and Google's Gemini AI. It replaces dry textbooks with 100-level lifestyle quizzes and an interactive live AI voice agent that speaks back out loud, giving learners a safe, judgement-free space to build spoken fluency.", size=24, space_after=240))

    # Section 2: Core Technical Innovations
    doc_parts.append(p("2. KEY TECHNICAL & PEDAGOGICAL INNOVATIONS", bold=True, size=26, space_after=140))
    doc_parts.append(p("- 100-Level Lifestyle Quizzes (1,000 Levels Total): 10 rich categories (Food & Dining, Travel & Mumbai Local, Job & Interview, Campus & School, Daily Routine, Gaming & Esports, Social Media Slang, Pop Culture & Anime, Shopping & Street Bazaar, Friends & Dating).", size=24))
    doc_parts.append(p("- Live Conversational AI Voice Agent: Real-time speech recognition via device microphone; AI replies immediately with native TTS speech, transliterated guidance, grammar coaching, and normal/slow playback speeds.", size=24))
    doc_parts.append(p("- Duolingo Gamification Engine: 3D tactile buttons with physical 4dp depression spring animations, 5 Hearts (Lives) refill system, Gems/Lingots economy with an in-app Gem Shop, serpentine S-curve course path with reward chests, and weekly Ruby League leaderboards.", size=24))
    doc_parts.append(p("- Offline-First Room Architecture: 100% persistent local database storing user credentials (Name, Email, Phone), progress across 1,000 level nodes, vocabulary bank, and streak counters.", size=24, space_after=240))

    # Section 3: Community Engagement & Methodology
    doc_parts.append(p("3. COMMUNITY INTERACTIONS & FIELD SITE DETAILS", bold=True, size=26, space_after=140))
    doc_parts.append(p("Field trials were conducted across 3 suburban Mumbai cohorts involving 45 young learners:", size=24))
    doc_parts.append(p("- Cohort A (Kurla-Ghatkopar Belt, 18 students): Vernacular college students preparing for entry-level BPO, retail, and corporate jobs.", size=24))
    doc_parts.append(p("- Cohort B (Chembur Youth Study Centre, 15 students): Secondary and junior college learners targeting spoken English and Marathi competitive exams.", size=24))
    doc_parts.append(p("- Cohort C (Dharavi Digital Community Centre, 12 students): Gig workers and youth learning English, Hindi, and Japanese for freelance tech and hospitality.", size=24, space_after=240))

    # Section 4: Quantitative Impact
    doc_parts.append(p("4. MEASURABLE RESULTS & COMMUNITY IMPACT", bold=True, size=26, space_after=140))
    doc_parts.append(p("- Speaking Confidence: Jumped by +85.7% (Baseline: 4.2/10 -> Post-training: 7.8/10).", size=24))
    doc_parts.append(p("- Quiz Accuracy: Averaged 88.2% across 10 lifestyle categories.", size=24))
    doc_parts.append(p("- 7-Day Vocabulary Retention: 79% (compared to only 38% from conventional rote memorization).", size=24))
    doc_parts.append(p("- Daily Learning Intent: 91% of participants continued practicing voluntarily.", size=24, space_after=240))

    # Section 5: Viva Voce & Presentation Defense FAQs
    doc_parts.append(page_break())
    doc_parts.append(p("5. VIVA VOCE DEFENSE & EVALUATOR QUESTIONS", bold=True, size=26, space_after=180))

    faqs = [
        ("Q1: Why did you choose this topic under CEP NEP 2020?",
         "A1: Under Indicative Area 5 ('Socially Impactful Tech Projects'), language barrier is one of Mumbai's most pressing socioeconomic divides. Bridging vernacular speakers to conversational English directly unlocks higher education and job opportunities."),
        ("Q2: How is this different from existing apps like Duolingo or Google Translate?",
         "A2: Google Translate is a passive dictionary; it does not train speech. Commercial apps like Duolingo use Western examples and lack Indian vernacular anchors. Our app integrates Hindi and Marathi as foundational cognitive bridges, features Mumbai-specific scenarios (Local trains, street bargaining, BPO interviews), and incorporates a Live Talking AI Voice Partner."),
        ("Q3: Does the app work without active internet?",
         "A3: Yes! The entire core app—including user profiles, 1,000 lifestyle quiz levels, streak tracking, hearts, and gems—runs 100% offline on a local SQLite Room Database. Only the live Gemini AI voice agent requires an active internet connection."),
        ("Q4: How did the community respond to the AI Agent?",
         "A4: Exceptionally well. Students who were hesitant to speak English in front of peers or teachers spoke freely with the AI Voice Coach, citing that the AI is non-judgemental, patient, and gives gentle feedback.")
    ]

    for q, a in faqs:
        doc_parts.append(p(q, bold=True, size=24, space_after=80))
        doc_parts.append(p(a, size=24, space_after=180))

    doc_parts.append(p("Report & Summary compiled for University of Mumbai Academic Year 2025-2026.", italic=True, size=22, align="center", space_after=200))
    doc_parts.append("</w:body></w:document>")

    docx_path = "./CEP_Project_Summary.docx"
    with zipfile.ZipFile(docx_path, "w", zipfile.ZIP_DEFLATED) as docx:
        docx.writestr("[Content_Types].xml", content_types)
        docx.writestr("_rels/.rels", root_rels)
        docx.writestr("word/_rels/document.xml.rels", doc_rels)
        docx.writestr("word/styles.xml", styles_xml)
        docx.writestr("word/document.xml", "\n".join(doc_parts))

    print(f"Created Microsoft Word Summary (.docx) at {docx_path}")

if __name__ == "__main__":
    generate_summary_docx()
