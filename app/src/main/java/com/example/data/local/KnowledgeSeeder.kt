package com.example.data.local

object KnowledgeSeeder {
    val entries: List<KnowledgeEntryEntity> = listOf(
        KnowledgeEntryEntity(
            id = "gk_capitals",
            subject = "General Knowledge",
            question = "What are the major world capitals and international organization headquarters?",
            summary = "Key capitals (New Delhi, Tokyo, Canberra, Ottawa, Bern) and UN/WHO/UNESCO headquarters.",
            keywords = "capital capitals un headquarters geneva new york paris vienna india japan australia canada switzerland",
            answer = "Here is a quick guide to important capitals and global headquarters:\n\n" +
                "• **India**: New Delhi  |  **Japan**: Tokyo  |  **Australia**: Canberra\n" +
                "• **Canada**: Ottawa  |  **Brazil**: Brasília  |  **Switzerland**: Bern\n" +
                "• **United Nations (UN)**: New York, USA\n" +
                "• **WHO & WTO**: Geneva, Switzerland\n" +
                "• **UNESCO**: Paris, France  |  **IAEA**: Vienna, Austria\n" +
                "• **International Court of Justice**: The Hague, Netherlands"
        ),
        KnowledgeEntryEntity(
            id = "gk_nobel",
            subject = "General Knowledge",
            question = "Who started the Nobel Prize and what are its six categories?",
            summary = "Alfred Nobel's 1895 will created prizes in Physics, Chemistry, Medicine, Literature, Peace, and later Economics.",
            keywords = "nobel prize alfred nobel categories physics chemistry medicine peace literature economics",
            answer = "The **Nobel Prize** was established by the 1895 will of Swedish inventor **Alfred Nobel** (inventor of dynamite) and first awarded in **1901**.\n\n" +
                "Its six categories are:\n" +
                "1. **Physics**  2. **Chemistry**  3. **Physiology or Medicine**\n" +
                "4. **Literature**  5. **Peace** (awarded in Oslo, Norway)\n" +
                "6. **Economic Sciences** (added in 1968 by Sweden's central bank)."
        ),
        KnowledgeEntryEntity(
            id = "gk_wonders",
            subject = "General Knowledge",
            question = "What are the New Seven Wonders of the World?",
            summary = "Taj Mahal, Great Wall of China, Petra, Colosseum, Chichen Itza, Machu Picchu, and Christ the Redeemer.",
            keywords = "seven wonders world taj mahal great wall petra colosseum machu picchu chichen itza christ redeemer",
            answer = "The **New Seven Wonders of the World** (chosen in 2007) are:\n\n" +
                "1. **Taj Mahal** — Agra, India\n" +
                "2. **Great Wall of China** — China\n" +
                "3. **Petra** — Ma'an, Jordan\n" +
                "4. **The Colosseum** — Rome, Italy\n" +
                "5. **Chichén Itzá** — Yucatán, Mexico\n" +
                "6. **Machu Picchu** — Cusco Region, Peru\n" +
                "7. **Christ the Redeemer** — Rio de Janeiro, Brazil\n" +
                "*(Honorary mention: The Great Pyramid of Giza, Egypt, the only surviving Ancient Wonder.)*"
        ),
        KnowledgeEntryEntity(
            id = "gk_constitution",
            subject = "General Knowledge",
            question = "What are the key facts about the Constitution of India and fundamental rights?",
            summary = "Adopted Nov 26, 1949, enforced Jan 26, 1950; Dr. B.R. Ambedkar chaired the Drafting Committee.",
            keywords = "constitution india ambedkar fundamental rights republic day preamble articles",
            answer = "The **Constitution of India** is the longest written constitution of any sovereign nation:\n\n" +
                "• **Adopted**: 26 November 1949 (Constitution Day)\n" +
                "• **Enforced**: 26 January 1950 (Republic Day)\n" +
                "• **Drafting Committee Chair**: Dr. B. R. Ambedkar\n" +
                "• **6 Fundamental Rights**: Right to Equality, Freedom, Against Exploitation, Freedom of Religion, Cultural & Educational Rights, and Constitutional Remedies (Article 32)."
        ),
        KnowledgeEntryEntity(
            id = "gk_olympics",
            subject = "General Knowledge",
            question = "What do the five Olympic rings represent and when did the modern Games begin?",
            summary = "Five inhabited continents united in sport; first modern Olympics held in Athens in 1896.",
            keywords = "olympics rings colors athens 1896 pierre coubertin sport motto",
            answer = "The **Modern Olympic Games** began in **Athens, Greece in 1896**, revived by **Pierre de Coubertin**.\n\n" +
                "• **Five Interlocking Rings** (Blue, Yellow, Black, Green, Red) symbolize the **five inhabited continents** united through sport—at least one of those colors appears on every national flag.\n" +
                "• **Olympic Motto**: *Citius, Altius, Fortius – Communiter* (Faster, Higher, Stronger – Together)."
        ),
        KnowledgeEntryEntity(
            id = "sci_light",
            subject = "Science & Physics",
            question = "What is the speed of light and why is it a cosmic speed limit?",
            summary = "299,792,458 m/s in a vacuum; Einstein's special relativity links space, time, and E=mc².",
            keywords = "speed of light physics relativity einstein vacuum photon e=mc2",
            answer = "In a vacuum, light travels at exactly **299,792,458 meters per second** (about **300,000 km/s**).\n\n" +
                "According to **Einstein's Special Relativity**, as an object with mass accelerates toward the speed of light, its relativistic energy requirement grows toward infinity. Only massless particles like **photons** travel at *c*.\n\n" +
                "Light from the Sun takes roughly **8 minutes and 20 seconds** to reach Earth."
        ),
        KnowledgeEntryEntity(
            id = "sci_newton",
            subject = "Science & Physics",
            question = "What are Newton's Three Laws of Motion?",
            summary = "Inertia, F = m·a, and equal-and-opposite action-reaction forces.",
            keywords = "newton laws motion inertia force mass acceleration action reaction physics",
            answer = "**Sir Isaac Newton's Three Laws of Motion**:\n\n" +
                "1. **First Law (Inertia)**: An object stays at rest or moves at constant velocity unless acted upon by a net external force.\n" +
                "2. **Second Law (F = m·a)**: Net force equals mass times acceleration. Larger force or smaller mass means greater acceleration.\n" +
                "3. **Third Law (Action–Reaction)**: Every force exerted by object A on object B produces an equal and opposite force from B on A."
        ),
        KnowledgeEntryEntity(
            id = "sci_thermodynamics",
            subject = "Science & Physics",
            question = "What are the Laws of Thermodynamics?",
            summary = "Thermal equilibrium (0th), conservation of energy (1st), entropy increase (2nd), absolute zero (3rd).",
            keywords = "thermodynamics energy entropy heat temperature absolute zero conservation",
            answer = "The **Four Laws of Thermodynamics** govern heat and energy:\n\n" +
                "• **0th Law**: Defines temperature via thermal equilibrium.\n" +
                "• **1st Law**: Energy cannot be created or destroyed, only transformed (Conservation of Energy).\n" +
                "• **2nd Law**: In an isolated system, total **entropy** (disorder) never decreases; heat flows spontaneously from hot to cold.\n" +
                "• **3rd Law**: As temperature approaches **absolute zero (0 K / -273.15°C)**, entropy of a perfect crystal approaches a minimum."
        ),
        KnowledgeEntryEntity(
            id = "sci_gravity",
            subject = "Science & Physics",
            question = "How does gravity work and what is Earth's escape velocity?",
            summary = "Mass curves spacetime (General Relativity); Earth's gravity g ≈ 9.81 m/s² and escape velocity is 11.2 km/s.",
            keywords = "gravity gravitation earth 9.8 escape velocity orbit moon spacetime",
            answer = "**Gravity** is the attractive interaction between objects with mass or energy. On Earth's surface, gravitational acceleration is **9.81 m/s²**.\n\n" +
                "• **Newton's Law**: Force decreases with the square of distance (`F = G·m₁·m₂ / r²`).\n" +
                "• **Einstein's General Relativity**: Massive objects curve spacetime, guiding planets and even bending light.\n" +
                "• **Earth's Escape Velocity**: **11.186 km/s** (~40,270 km/h) to break free of Earth's gravitational pull without further propulsion."
        ),
        KnowledgeEntryEntity(
            id = "chem_periodic",
            subject = "Chemistry",
            question = "How is the Periodic Table organized and what are its main groups?",
            summary = "Ordered by atomic number (protons); periods are rows and groups share valence electron properties.",
            keywords = "periodic table elements atomic number mendeleev noble gases halogens alkali metals",
            answer = "The **Periodic Table** arranges **118 known elements** by increasing **atomic number** (number of protons):\n\n" +
                "• **Periods (Rows 1–7)**: Show the number of electron shells.\n" +
                "• **Groups (Columns 1–18)**: Elements in the same group have similar valence electrons and chemical behavior.\n" +
                "  - **Group 1**: Alkali Metals (e.g., Li, Na, K — very reactive)\n" +
                "  - **Group 17**: Halogens (e.g., F, Cl, Br)\n" +
                "  - **Group 18**: Noble Gases (e.g., He, Ne, Ar — stable and inert)."
        ),
        KnowledgeEntryEntity(
            id = "chem_ph",
            subject = "Chemistry",
            question = "What is the pH scale and how do acids and bases differ?",
            summary = "pH 0–14 measures H+ concentration; <7 is acidic, 7 is neutral water, >7 is basic/alkaline.",
            keywords = "ph scale acid base alkaline hydrogen ion neutral litmus",
            answer = "The **pH scale (0 to 14)** measures how acidic or basic a water-based solution is based on hydrogen ion (`H⁺`) concentration:\n\n" +
                "• **pH < 7 (Acidic)**: Donates protons; tastes sour; turns blue litmus red (e.g., lemon juice ~2, vinegar ~3).\n" +
                "• **pH = 7 (Neutral)**: Pure water.\n" +
                "• **pH > 7 (Basic / Alkaline)**: Accepts protons; feels soapy; turns red litmus blue (e.g., baking soda ~8.3, soap ~10).\n" +
                "Each unit change in pH represents a **10× change** in acidity."
        ),
        KnowledgeEntryEntity(
            id = "bio_photosynthesis",
            subject = "Biology",
            question = "How does photosynthesis work in plants?",
            summary = "Chloroplasts convert CO2, water, and sunlight into glucose and oxygen.",
            keywords = "photosynthesis plant chlorophyll chloroplast sunlight glucose oxygen carbon dioxide",
            answer = "**Photosynthesis** is how green plants, algae, and cyanobacteria convert light energy into chemical energy:\n\n" +
                "**Equation**: `6 CO₂ + 6 H₂O + Light → C₆H₁₂O₆ (Glucose) + 6 O₂`\n\n" +
                "1. **Light-Dependent Reactions** (in thylakoid membranes): Chlorophyll absorbs sunlight, splits water molecules, releases **oxygen**, and produces ATP/NADPH.\n" +
                "2. **Calvin Cycle** (in the stroma): Uses ATP/NADPH to fix **carbon dioxide** into **glucose**."
        ),
        KnowledgeEntryEntity(
            id = "bio_dna",
            subject = "Biology",
            question = "What is DNA and how does it store genetic information?",
            summary = "Double-helix molecule made of A-T and C-G nucleotide base pairs encoding proteins.",
            keywords = "dna rna genetics double helix nucleotide adenine thymine cytosine guanine gene chromosome",
            answer = "**DNA (Deoxyribonucleic Acid)** is the hereditary molecule of life, structured as a **double helix** (discovered by Watson, Crick, Franklin, and Wilkins):\n\n" +
                "• **Four Bases**: **Adenine (A)** pairs with **Thymine (T)**; **Cytosine (C)** pairs with **Guanine (G)**.\n" +
                "• **Central Dogma**: `DNA → (Transcription) → mRNA → (Translation) → Protein`.\n" +
                "Humans have about **3 billion base pairs** organized across **23 pairs of chromosomes**."
        ),
        KnowledgeEntryEntity(
            id = "bio_human_body",
            subject = "Biology",
            question = "What are key facts about the human body (bones, blood, heart, brain)?",
            summary = "206 adult bones, 4-chamber heart, universal donor O-negative, ~86 billion neurons.",
            keywords = "human body bones blood heart brain neurons universal donor organs liver skin",
            answer = "Essential **Human Biology** facts:\n\n" +
                "• **Bones**: 206 in adults (femur is longest; stapes in the ear is smallest).\n" +
                "• **Largest Organ**: Skin (external) and Liver (internal).\n" +
                "• **Heart**: 4 chambers pumping ~5 liters of blood per minute at rest.\n" +
                "• **Blood**: Universal red-cell donor is **O-negative**; universal recipient is **AB-positive**.\n" +
                "• **Brain**: Contains roughly **86 billion neurons**."
        ),
        KnowledgeEntryEntity(
            id = "math_pythagoras",
            subject = "Mathematics",
            question = "What is the Pythagorean Theorem and how is it used?",
            summary = "In a right triangle, a² + b² = c² where c is the hypotenuse.",
            keywords = "pythagorean theorem right triangle hypotenuse geometry math formula a2+b2=c2",
            answer = "In any **right-angled triangle**, the square of the hypotenuse (`c`, the side opposite the 90° angle) equals the sum of the squares of the other two sides (`a` and `b`):\n\n" +
                "**Formula**: `a² + b² = c²`\n\n" +
                "**Example**: If legs are `3` and `4`, then `c = √(3² + 4²) = √(9 + 16) = √25 = 5`.\n" +
                "Common Pythagorean triples: `(3, 4, 5)`, `(5, 12, 13)`, `(8, 15, 17)`."
        ),
        KnowledgeEntryEntity(
            id = "math_quadratic",
            subject = "Mathematics",
            question = "How do you solve a quadratic equation using the quadratic formula?",
            summary = "x = (-b ± √(b² - 4ac)) / 2a; the discriminant b² - 4ac tells root count.",
            keywords = "quadratic formula equation roots discriminant algebra polynomial x2 ax2+bx+c",
            answer = "For any quadratic equation `ax² + bx + c = 0` (with `a ≠ 0`), the solutions are:\n\n" +
                "**Formula**: `x = (-b ± √(b² - 4ac)) / (2a)`\n\n" +
                "The **discriminant** `Δ = b² - 4ac` tells you the nature of the roots:\n" +
                "• `Δ > 0`: Two distinct real roots\n" +
                "• `Δ = 0`: One repeated real root\n" +
                "• `Δ < 0`: Two complex conjugate roots."
        ),
        KnowledgeEntryEntity(
            id = "math_primes",
            subject = "Mathematics",
            question = "What are prime numbers and what is the Fundamental Theorem of Arithmetic?",
            summary = "Numbers >1 divisible only by 1 and themselves; every integer >1 factors uniquely into primes.",
            keywords = "prime numbers composite factors arithmetic euclid 2 3 5 7 11",
            answer = "A **prime number** is a whole number greater than 1 whose only positive divisors are **1 and itself**.\n\n" +
                "• **Smallest & only even prime**: `2`\n" +
                "• **First 10 primes**: `2, 3, 5, 7, 11, 13, 17, 19, 23, 29`\n" +
                "• **Fundamental Theorem of Arithmetic**: Every integer greater than 1 can be written as a **unique product of prime numbers** (e.g., `60 = 2² × 3 × 5`)."
        ),
        KnowledgeEntryEntity(
            id = "math_calculus",
            subject = "Mathematics",
            question = "What is the difference between differentiation and integration in calculus?",
            summary = "Derivatives measure instantaneous rate of change (slope); integrals measure accumulated area under a curve.",
            keywords = "calculus derivative integral differentiation integration slope area limit",
            answer = "Calculus studies continuous change through two inverse operations:\n\n" +
                "• **Differentiation (Derivative)**: Finds the **instantaneous rate of change** or slope of a curve at a point. Example: derivative of position is velocity; power rule `d/dx(xⁿ) = n·xⁿ⁻¹`.\n" +
                "• **Integration (Integral)**: Finds the **total accumulation** or area under a curve.\n" +
                "• **Fundamental Theorem of Calculus**: Differentiation and integration undo each other."
        ),
        KnowledgeEntryEntity(
            id = "hist_renaissance",
            subject = "History",
            question = "What was the Renaissance and why did it transform Europe?",
            summary = "14th–17th century rebirth of art, science, and humanism starting in Florence, Italy.",
            keywords = "renaissance italy florence leonardo da vinci michelangelo galileo printing press gutenberg humanism",
            answer = "The **Renaissance** (*\"rebirth\"*, ~1300–1600) began in **Florence, Italy** and marked the transition from the Middle Ages to the Modern Era:\n\n" +
                "• **Humanism**: Renewed focus on classical Greek/Roman learning, reason, and human potential.\n" +
                "• **Art & Science**: Figures like **Leonardo da Vinci**, **Michelangelo**, **Copernicus**, and **Galileo** reshaped art, anatomy, and astronomy.\n" +
                "• **Catalyst**: **Johannes Gutenberg's movable-type printing press (~1440)** spread ideas rapidly across Europe."
        ),
        KnowledgeEntryEntity(
            id = "hist_india_freedom",
            subject = "History",
            question = "What are the major milestones of the Indian Independence Movement?",
            summary = "1857 Revolt, 1885 INC, 1920 Non-Cooperation, 1930 Dandi March, 1942 Quit India, Aug 15 1947 Independence.",
            keywords = "india independence 1947 gandhi bhagat singh subhas chandra bose 1857 dandi march quit india",
            answer = "Key milestones in **India's Independence Movement**:\n\n" +
                "• **1857**: First War of Independence (Revolt of 1857)\n" +
                "• **1885**: Founding of the Indian National Congress\n" +
                "• **1919**: Jallianwala Bagh tragedy & Rowlatt protests\n" +
                "• **1920–22**: Non-Cooperation Movement led by Mahatma Gandhi\n" +
                "• **1930**: Civil Disobedience & Dandi Salt March\n" +
                "• **1942**: Quit India Movement (*\"Do or Die\"*) & Azad Hind Fauj led by Netaji Subhas Chandra Bose\n" +
                "• **15 August 1947**: India gained independence."
        ),
        KnowledgeEntryEntity(
            id = "hist_world_wars",
            subject = "History",
            question = "What caused World War I and World War II, and when did they occur?",
            summary = "WWI (1914–1918) sparked by alliances & Sarajevo; WWII (1939–1945) led to the United Nations.",
            keywords = "world war 1 2 wwi wwii 1914 1918 1939 1945 united nations treaty versailles",
            answer = "**World War I (1914–1918)**:\n" +
                "Triggered by militarism, secret alliances, imperialism, nationalism, and the assassination of Archduke Franz Ferdinand. Ended with the **1919 Treaty of Versailles**.\n\n" +
                "**World War II (1939–1945)**:\n" +
                "Fought between the **Allies** and **Axis** powers across Europe, Africa, and the Pacific. Ended in 1945 and led directly to the founding of the **United Nations (UN)** to prevent future global conflicts."
        ),
        KnowledgeEntryEntity(
            id = "geo_earth",
            subject = "Geography",
            question = "What are Earth's continents, oceans, highest peak, and longest rivers?",
            summary = "7 continents, 5 oceans, Mt. Everest (8,848.86 m), Nile & Amazon rivers, Mariana Trench.",
            keywords = "continents oceans everest nile amazon mariana trench pacific asia africa geography",
            answer = "Key **World Geography** facts:\n\n" +
                "• **7 Continents (by area)**: Asia (largest), Africa, North America, South America, Antarctica, Europe, Australia/Oceania.\n" +
                "• **5 Oceans**: Pacific (largest & deepest), Atlantic, Indian, Southern, Arctic.\n" +
                "• **Highest Point**: Mount Everest (**8,848.86 m**), Himalayas.\n" +
                "• **Deepest Point**: Challenger Deep in the **Mariana Trench** (~10,935 m).\n" +
                "• **Longest River**: Nile (~6,650 km); **Largest by volume**: Amazon."
        ),
        KnowledgeEntryEntity(
            id = "geo_atmosphere",
            subject = "Geography",
            question = "What are the layers of Earth's atmosphere?",
            summary = "Troposphere, Stratosphere (ozone), Mesosphere, Thermosphere, and Exosphere.",
            keywords = "atmosphere layers troposphere stratosphere ozone mesosphere thermosphere exosphere weather",
            answer = "Earth's **Atmosphere** has five primary layers (bottom to top):\n\n" +
                "1. **Troposphere (0–12 km)**: Where we live and where almost all **weather** occurs.\n" +
                "2. **Stratosphere (12–50 km)**: Contains the **ozone layer** that absorbs harmful UV radiation; commercial jets fly near its bottom.\n" +
                "3. **Mesosphere (50–85 km)**: Coldest layer; most **meteors** burn up here.\n" +
                "4. **Thermosphere (85–600 km)**: Home to **auroras** and the International Space Station.\n" +
                "5. **Exosphere (>600 km)**: Gradually merges into outer space."
        ),
        KnowledgeEntryEntity(
            id = "cs_algorithms",
            subject = "Computer Science",
            question = "What is Big-O notation and how do common time complexities compare?",
            summary = "Measures algorithm scaling with input size n: O(1) < O(log n) < O(n) < O(n log n) < O(n²).",
            keywords = "big o time complexity algorithm binary search sorting hash map computer science",
            answer = "**Big-O notation** describes how an algorithm's runtime or memory grows as input size `n` increases:\n\n" +
                "• `O(1)` **Constant**: Hash map lookup or array index access.\n" +
                "• `O(log n)` **Logarithmic**: Binary search on a sorted list.\n" +
                "• `O(n)` **Linear**: Scanning an unsorted list once.\n" +
                "• `O(n log n)` **Linearithmic**: Merge Sort / Quick Sort (average).\n" +
                "• `O(n²)` **Quadratic**: Nested loops like Bubble Sort."
        ),
        KnowledgeEntryEntity(
            id = "cs_internet",
            subject = "Computer Science",
            question = "How do DNS, HTTP/HTTPS, and TCP/IP work together when loading a webpage?",
            summary = "DNS resolves domains to IP addresses, TCP ensures reliable delivery, TLS encrypts HTTPS.",
            keywords = "dns http https tcp ip internet browser networking ssl tls port 443",
            answer = "When you open a website:\n\n" +
                "1. **DNS (Domain Name System)** translates a human domain (like `archive.org`) into an **IP address**.\n" +
                "2. **TCP/IP** establishes a reliable connection and breaks data into packets routed across networks.\n" +
                "3. **TLS (for HTTPS)** performs a cryptographic handshake so traffic is encrypted and authenticated.\n" +
                "4. **HTTP** sends `GET`/`POST` requests and receives HTML, JSON, or media streams."
        ),
        KnowledgeEntryEntity(
            id = "econ_basics",
            subject = "Economics",
            question = "What are GDP, inflation, and supply and demand?",
            summary = "GDP measures total output; inflation tracks rising price levels; supply and demand set market equilibrium.",
            keywords = "economics gdp inflation supply demand central bank interest rate recession",
            answer = "Core **Economics** concepts:\n\n" +
                "• **GDP (Gross Domestic Product)**: Total market value of all final goods and services produced inside a country in a given period.\n" +
                "• **Inflation**: The rate at which the general price level rises, reducing purchasing power over time.\n" +
                "• **Supply & Demand**: Higher demand or lower supply tends to raise prices; higher supply or lower demand tends to lower prices until **market equilibrium** is reached."
        ),
        KnowledgeEntryEntity(
            id = "lit_devices",
            subject = "Literature",
            question = "What are common literary devices like metaphor, simile, irony, and alliteration?",
            summary = "Metaphor, simile, personification, irony, foreshadowing, and alliteration enrich writing.",
            keywords = "literature metaphor simile irony alliteration personification poetry english grammar",
            answer = "Essential **Literary Devices**:\n\n" +
                "• **Simile**: Compares two things using *like* or *as* (e.g., *\"brave as a lion\"*).\n" +
                "• **Metaphor**: Direct comparison without *like/as* (e.g., *\"time is a thief\"*).\n" +
                "• **Personification**: Giving human traits to non-human things.\n" +
                "• **Alliteration**: Repeating initial consonant sounds (*\"silent stars shimmer\"*).\n" +
                "• **Irony**: Contrast between expectation and reality."
        )
    )
}
