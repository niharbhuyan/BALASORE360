package com.example.data.repository

import com.example.data.local.HotspotEntity
import com.example.data.local.NewsArticleEntity
import com.example.data.local.ReviewEntity
import com.example.data.local.UserEntity

object DefaultData {
    fun getInitialHotspots(): List<HotspotEntity> = listOf(
        HotspotEntity(
            id = "chandipur_beach",
            name = "Chandipur Beach (Vanishing Sea)",
            odiaName = "ଚାନ୍ଦିପୁର ବେଳାଭୂମି",
            category = "Beach",
            shortDescription = "World-famous phenomenon where the sea recedes up to 5 km during low tide.",
            fullDescription = "Chandipur Beach is one of the most unique beaches on Earth. Twice each day during low tide, the Bay of Bengal recedes up to 5 kilometers into the sea, exposing a vast muddy seabed where visitors can walk, ride, or explore horseshoe crabs. When high tide approaches, the sea returns gently. It is also adjacent to the DRDO Integrated Test Range.",
            highlights = "5km Sea Recession, Horseshoe Crabs, Golden Sunsets, Casuarina Groves, Safe Walking",
            distanceKmFromBls = 16,
            bestTimeToVisit = "October to March (Ideal at low tide hours)",
            timings = "Open 24 hours (Daylight recommended for walking seabed)",
            entryFee = "Free Entry",
            specialty = "Rare Vanishing Sea phenomenon & Marine Biodiversity",
            localTip = "Check the live tide timetable in this app before visiting. Never venture far out when high tide is returning.",
            latitude = 21.4674,
            longitude = 87.0177
        ),
        HotspotEntity(
            id = "khirachora_gopinatha",
            name = "Khirachora Gopinatha Temple, Remuna",
            odiaName = "କ୍ଷୀରଚୋରା ଗୋପୀନାଥ ମନ୍ଦିର, ରେମୁଣା",
            category = "Temple",
            shortDescription = "Ancient 10th-century shrine famous for Lord Krishna stealing condensed milk for His devotee.",
            fullDescription = "Located in Remuna, 9 km from Balasore city, Khirachora Gopinatha is a deeply revered Vaishnavite pilgrimage site. Legend tells of Lord Gopinatha hiding a pot of sweet condensed milk ('Khira') in His robes for His ardent devotee Madhavendra Puri. The temple features beautiful stone architecture and serene prayer halls.",
            highlights = "Famous Amrita Keli Prasad, Sri Chaitanya Mahaprabhu Visit, 10th Century Kalinga Idols",
            distanceKmFromBls = 9,
            bestTimeToVisit = "Throughout the year (Mornings & Evenings)",
            timings = "6:00 AM – 12:30 PM, 4:00 PM – 8:30 PM",
            entryFee = "Free Entry (Prasad coupons available inside)",
            specialty = "Delectable 'Amrita Keli' (thickened sweet condensed milk)",
            localTip = "Buy the freshly prepared Khira Bhoga early in the morning or around 5 PM before it sells out.",
            latitude = 21.5283,
            longitude = 86.8725
        ),
        HotspotEntity(
            id = "emami_jagannath",
            name = "Emami Jagannath Temple",
            odiaName = "ଇମାମି ଜଗନ୍ନାଥ ମନ୍ଦିର",
            category = "Temple",
            shortDescription = "Grand sandstone architectural marvel honoring Lord Jagannath with lush landscaped gardens.",
            fullDescription = "Built with ornate Kalinga architectural brilliance using authentic Rajasthan and Odisha sandstone, the Emami Jagannath Temple in Balasore is a modern spiritual jewel. The main sanctum rises 78 feet high and houses idols of Lord Jagannath, Balabhadra, and Devi Subhadra. The surrounding complex includes musical fountains and illuminated gardens.",
            highlights = "78-foot Sandstone Shikhara, Musical Fountain, Pristine Gardens, Cultural Amphitheatre",
            distanceKmFromBls = 11,
            bestTimeToVisit = "Evening hours for lighting and cool breeze",
            timings = "6:00 AM – 1:00 PM, 4:00 PM – 9:00 PM",
            entryFee = "Free Entry",
            specialty = "Pristine cleanliness, evening aarti, and scenic architectural photography",
            localTip = "Visit during sunset to witness the golden light illuminate the sandstone carvings followed by the evening light show.",
            latitude = 21.4925,
            longitude = 86.9150
        ),
        HotspotEntity(
            id = "panchalingeswar",
            name = "Panchalingeswar Shrine, Nilagiri",
            odiaName = "ପଞ୍ଚଲିଙ୍ଗେଶ୍ୱର ମନ୍ଦିର, ନୀଳଗିରି",
            category = "Temple",
            shortDescription = "Sacred hillside shrine with five Shiva lingams naturally submerged under a flowing mountain stream.",
            fullDescription = "Nestled amidst the green canopy of the Nilagiri hills, Panchalingeswar is revered for five natural Shiva Lingams over which a perennial mountain spring continuously flows. Devotees ascend stone steps through forest greenery and touch the sacred lingas beneath the cool rushing waters.",
            highlights = "Stream Flowing Over Lingams, Nilagiri Hill Trek, Lush Sal Forests, Scenic Picnic Spot",
            distanceKmFromBls = 30,
            bestTimeToVisit = "September to March (Pleasant weather and active mountain stream)",
            timings = "6:00 AM – 6:00 PM",
            entryFee = "Free Entry (Parking charges apply for vehicles)",
            specialty = "Spiritual immersion in nature; touch lingas submerged in flowing water",
            localTip = "Wear comfortable non-slip footwear as the hillside rocks near the stream can be slippery.",
            latitude = 21.4286,
            longitude = 86.7119
        ),
        HotspotEntity(
            id = "talasari_beach",
            name = "Talasari Beach & Subarnarekha Estuary",
            odiaName = "ତାଳସାରୀ ବେଳାଭୂମି",
            category = "Beach",
            shortDescription = "Pristine palm-fringed coastal haven with red ghost crabs and tranquil sandbars.",
            fullDescription = "Situated near the border of Odisha and West Bengal, Talasari Beach is famed for its calm, palm-dotted shoreline and the delta where the Subarnarekha River meets the Bay of Bengal. Large colonies of red ghost crabs scurry across the damp sand, creating a crimson carpet on the golden beach.",
            highlights = "Red Ghost Crabs, Estuary Boat Rides, Coconut Palm Belts, Quiet Non-commercial Beach",
            distanceKmFromBls = 85,
            bestTimeToVisit = "October to February",
            timings = "Open 24 hours (Best at sunrise and late afternoon)",
            entryFee = "Free Entry",
            specialty = "Red ghost crabs and country boat rides to virgin sand islands",
            localTip = "Hire a local mechanized country boat to cross the backwater channel to the outer beach for solitude.",
            latitude = 21.5972,
            longitude = 87.4589
        ),
        HotspotEntity(
            id = "kuldiha_wildlife",
            name = "Kuldiha Wildlife Sanctuary",
            odiaName = "କୁଲଡିହା ବନ୍ୟପ୍ରାଣୀ ଅଭୟାରଣ୍ୟ",
            category = "Wildlife",
            shortDescription = "Protected biodiverse sanctuary home to Asian elephants, leopards, and giant squirrels.",
            fullDescription = "Spanning 272 sq km of mixed deciduous and Sal forest in the Nilagiri ranges, Kuldiha connects to the Similipal biosphere. It is an elephant sanctuary and eco-tourism paradise with salt licks at Goharipradhan where herds of wild elephants gather at dusk.",
            highlights = "Wild Elephant Herds, Birding Trails, Rissia Nature Camp, Eco Watch Towers",
            distanceKmFromBls = 45,
            bestTimeToVisit = "November to May (Forest safaris open)",
            timings = "Safari permits: 6:30 AM – 4:30 PM",
            entryFee = "Nominal Forest Permit Fee (Eco-tourism portal booking available)",
            specialty = "Elephant corridor sightings and eco-cottage stays at Rissia",
            localTip = "Book a certified forest safari guide from Nilagiri or Balasore forest division headquarters in advance.",
            latitude = 21.4019,
            longitude = 86.6347
        ),
        HotspotEntity(
            id = "chandaneswar_temple",
            name = "Chandaneswar Shiva Temple",
            odiaName = "ଚନ୍ଦନେଶ୍ୱର ଶିବ ମନ୍ଦିର",
            category = "Temple",
            shortDescription = "Historic pilgrimage hub famed for the grand annual Uda Parba and Chadak Mela.",
            fullDescription = "Located in Bhograi block near Jaleswar, Chandaneswar is dedicated to Lord Shiva and attracts lakhs of devotees from Odisha, West Bengal, and Jharkhand. The temple's annual Chadak Mela festival in the Odia month of Chaitra features traditional penance and ascetic rituals.",
            highlights = "Chadak Mela (April), Ancient Shiva Linga, Inter-state Cultural Crossroads",
            distanceKmFromBls = 88,
            bestTimeToVisit = "During Maha Shivaratri & Chaitra Festival (March - April) or Winter",
            timings = "5:30 AM – 9:00 PM",
            entryFee = "Free Entry",
            specialty = "Vibrant devotional mela and sacred pond for holy dip",
            localTip = "Combine your trip to Chandaneswar with Talasari and Udaipur beaches on the same route.",
            latitude = 21.6167,
            longitude = 87.4667
        ),
        HotspotEntity(
            id = "balaramgadi_port",
            name = "Balaramgadi Fishing Port & Budhabalanga Estuary",
            odiaName = "ବଳରାମଗଡ଼ି ମତ୍ସ୍ୟ ବନ୍ଦର",
            category = "Port",
            shortDescription = "Lively coastal fishing harbour where the Budhabalanga river empties into the sea.",
            fullDescription = "Balaramgadi is located where the historic Budhabalanga River joins the Bay of Bengal, 2 km from Chandipur. It is the heart of Balasore's deep-sea fishing fleet, where hundreds of colorful wooden boats and trawlers dock daily with fresh hilsa, pomfret, and tiger prawns.",
            highlights = "River-Sea Confluence, Fresh Coastal Seafood Market, River Cruise Boats, Fishermen Life",
            distanceKmFromBls = 18,
            bestTimeToVisit = "Early morning 6:00 AM – 9:00 AM when trawlers return with fresh catch",
            timings = "Open 24 hours",
            entryFee = "Free Entry",
            specialty = "Authentic maritime culture and freshest seafood",
            localTip = "Great photo opportunities of traditional wooden mechanized trawlers against sunrise.",
            latitude = 21.4795,
            longitude = 87.0392
        ),
        HotspotEntity(
            id = "drdo_missile_heritage",
            name = "Integrated Test Range (ITR) & Missile Hub",
            odiaName = "କ୍ଷେପଣାସ୍ତ୍ର ପରୀକ୍ଷଣ କେନ୍ଦ୍ର, ଚାନ୍ଦିପୁର",
            category = "Heritage",
            shortDescription = "The proud coastal launchpad of India's strategic defense systems (Agni, Akash, Prithvi).",
            fullDescription = "Balasore earned international acclaim as the 'Missile City of India'. The Integrated Test Range (ITR) at Chandipur and the offshore Dr. APJ Abdul Kalam Island (formerly Wheeler Island) are India's premier missile testing facilities. The city celebrates this legacy through commemorative defense installations and science awareness.",
            highlights = "Strategic Defense Legacy, APJ Abdul Kalam Island Tributes, Coastal Radar Points",
            distanceKmFromBls = 17,
            bestTimeToVisit = "Anytime (Security zone guidelines apply)",
            timings = "Outer viewing and coastal heritage open to public",
            entryFee = "Public coastal areas free; defense compound restricted",
            specialty = "National pride as India's defense testing headquarters",
            localTip = "Visit the Chandipur beach promenade to read commemorative plaques detailing the missile testing history.",
            latitude = 21.4550,
            longitude = 87.0100
        )
    )

    fun getInitialNews(): List<NewsArticleEntity> = listOf(
        NewsArticleEntity(
            title = "Balasore Railway Station Redevelopment Under Amrit Bharat Project Enters Final Phase",
            summary = "Upgraded terminal with modern concourse, roof plaza, 6 new escalators, and airport-style amenities to boost connectivity for coastal Odisha.",
            content = "The transformation of Balasore Railway Junction (BLS) under the central Amrit Bharat Station Scheme is moving at rapid pace. The multi-crore infrastructure upgrade includes a dedicated city-side concourse, expanded waiting lounges, solar power roofs, and improved traffic circulation for auto-rickshaws and buses. The project is expected to enhance tourism and ease travel for thousands of daily passengers.",
            category = "Civic & Transport",
            source = "District Information Office, Balasore",
            publishedAt = "Today, 08:30 AM",
            isBreaking = true,
            isBookmarked = false
        ),
        NewsArticleEntity(
            title = "Vanishing Sea Chandipur Draws Thousands of Weekend Travelers; Tide Safety Advisory Issued",
            summary = "District Administration deploys coastal lifeguards and issues timetable guidelines for low tide walking across the receding 5km sea bed.",
            content = "With pleasant coastal weather across the Bay of Bengal, Chandipur beach recorded heavy tourist footfall this weekend. Visitors experienced the renowned receding sea phenomenon where waters retreat up to 5 kilometers into the Bay. The local tourism desk has advised visitors to adhere strictly to siren warnings signaling the start of incoming high tide.",
            category = "Coastal & Tourism",
            source = "Balasore Tourism Bureau",
            publishedAt = "Today, 10:15 AM",
            isBreaking = false,
            isBookmarked = false
        ),
        NewsArticleEntity(
            title = "IMD Issues Marine Advisory for North Odisha Coast: Moderate Breeze & Calm Sea Conditions",
            summary = "Fishermen allowed normal offshore operations as Bay of Bengal depression system remains far south; pleasant conditions prevail.",
            content = "The Meteorological Centre, Bhubaneswar and Balasore coastal observatory reported steady atmospheric conditions with surface wind speeds between 12 to 18 km/h. Sea conditions along Chandipur, Kasafal, and Talasari remain safe for recreational boating and fishing activities for the next 48 hours.",
            category = "Weather Alert",
            source = "Regional Meteorological Centre",
            publishedAt = "Today, 07:00 AM",
            isBreaking = false,
            isBookmarked = false
        ),
        NewsArticleEntity(
            title = "Remuna Khirachora Gopinatha Temple Prepares Special Amrita Keli Bhog for Ekadashi",
            summary = "Thousands of devotees expected to arrive at the 10th-century shrine; special queue arrangements and parking facilities set up.",
            content = "Temple trustees and the district endowment department have finalized preparations for the upcoming sacred Ekadashi at the historic Khirachora Gopinatha Temple in Remuna. Additional counters for the holy Amrita Keli prasad made in traditional earthen pots have been organized to manage high pilgrim turnout smoothly.",
            category = "Culture & Heritage",
            source = "Remuna Heritage Council",
            publishedAt = "Yesterday, 04:45 PM",
            isBreaking = false,
            isBookmarked = false
        ),
        NewsArticleEntity(
            title = "Fakir Mohan University (FMU) Launches Coastal Marine Research Center at Nuapadhi Campus",
            summary = "New interdisciplinary facility will monitor mangrove ecosystems, marine fauna, and horseshoe crab conservation along the Balasore coast.",
            content = "Fakir Mohan University has inaugurated its specialized Center for Coastal & Marine Studies. In collaboration with national oceanographic bodies, researchers will study the ecological health of the Bay of Bengal shoreline, track the breeding of horseshoe crabs at Chandipur, and provide community training on sustainable aquaculture.",
            category = "Education & Tech",
            source = "FMU Media Cell",
            publishedAt = "Yesterday, 02:00 PM",
            isBreaking = false,
            isBookmarked = false
        ),
        NewsArticleEntity(
            title = "Budhabalanga River Embankment Modernization Clears Environmental Review",
            summary = "Strengthening of flood prevention walls and green riverfront corridor between Balaramgadi and Balasore town to commence shortly.",
            content = "The Water Resources Department has approved the phase-two strengthening of the Budhabalanga riverbanks. The plan includes stone-pitching along vulnerable turns, native tree plantations to prevent soil erosion during monsoons, and a public riverfront jogging promenade near the town entrance.",
            category = "Civic & Transport",
            source = "District Administration Balasore",
            publishedAt = "2 days ago",
            isBreaking = false,
            isBookmarked = false
        )
    )

    fun getInitialUsers(): List<UserEntity> = listOf(
        UserEntity(
            id = "niharbhuyan@gmail.com",
            fullName = "Nihar Bhuyan",
            email = "niharbhuyan@gmail.com",
            passwordHash = "balasore123",
            phoneNumber = "+91 94370 28192",
            locality = "Balasore Town",
            bio = "Lifelong Balasorian & coastal heritage enthusiast",
            avatarUri = null,
            securityQuestion = "What is your favorite place in Balasore?",
            securityAnswer = "Chandipur"
        ),
        UserEntity(
            id = "soumya.nayak@balasore.org",
            fullName = "Soumya Nayak",
            email = "soumya.nayak@balasore.org",
            passwordHash = "odisha2026",
            phoneNumber = "+91 98610 88231",
            locality = "Remuna",
            bio = "Balasore food & temple heritage guide",
            avatarUri = null,
            securityQuestion = "What is your favorite place in Balasore?",
            securityAnswer = "Remuna"
        )
    )

    fun getInitialReviews(): List<ReviewEntity> = listOf(
        ReviewEntity(
            targetType = "HOTSPOT",
            targetId = "chandipur_beach",
            targetTitle = "Chandipur Beach (Vanishing Sea)",
            userId = "soumya.nayak@balasore.org",
            userName = "Soumya Nayak",
            userLocality = "Remuna",
            rating = 5,
            comment = "Walking 2 kilometers out on the dry sea bed during low tide is a surreal experience! Make sure to listen for the coastal warning siren when high tide begins.",
            timestamp = System.currentTimeMillis() - 86400000L * 2
        ),
        ReviewEntity(
            targetType = "HOTSPOT",
            targetId = "chandipur_beach",
            targetTitle = "Chandipur Beach (Vanishing Sea)",
            userId = "niharbhuyan@gmail.com",
            userName = "Nihar Bhuyan",
            userLocality = "Balasore Town",
            rating = 5,
            comment = "The sunset over the casuarina trees is breathtaking. You can spot harmless horseshoe crabs near the mudflats. Truly one of the world's most unique wonders!",
            timestamp = System.currentTimeMillis() - 86400000L * 4
        ),
        ReviewEntity(
            targetType = "HOTSPOT",
            targetId = "khirachora_gopinatha",
            targetTitle = "Khirachora Gopinatha Temple, Remuna",
            userId = "soumya.nayak@balasore.org",
            userName = "Soumya Nayak",
            userLocality = "Remuna",
            rating = 5,
            comment = "Don't miss the heavenly Amrita Keli prasad! The stone carvings inside the sanctum are 10th-century masterpieces. Peace and divine tranquility.",
            timestamp = System.currentTimeMillis() - 86400000L * 3
        ),
        ReviewEntity(
            targetType = "HOTSPOT",
            targetId = "emami_jagannath",
            targetTitle = "Emami Jagannath Temple",
            userId = "niharbhuyan@gmail.com",
            userName = "Nihar Bhuyan",
            userLocality = "Balasore Town",
            rating = 5,
            comment = "The evening musical fountain and illumination on the sandstone shikhara is world-class. Pristine temple campus and lovely gardens.",
            timestamp = System.currentTimeMillis() - 86400000L * 1
        ),
        ReviewEntity(
            targetType = "HOTSPOT",
            targetId = "panchalingeswar",
            targetTitle = "Panchalingeswar Shrine, Nilagiri",
            userId = "soumya.nayak@balasore.org",
            userName = "Soumya Nayak",
            userLocality = "Remuna",
            rating = 5,
            comment = "Touching the sacred lingams submerged under the clear mountain stream while surrounded by lush green hills was an unforgettable spiritual trek.",
            timestamp = System.currentTimeMillis() - 86400000L * 5
        ),
        ReviewEntity(
            targetType = "HOTSPOT",
            targetId = "talasari_beach",
            targetTitle = "Talasari Beach & Subarnarekha Estuary",
            userId = "niharbhuyan@gmail.com",
            userName = "Nihar Bhuyan",
            userLocality = "Balasore Town",
            rating = 4,
            comment = "Thousands of red ghost crabs scurrying on the sand at morning sunrise. Highly recommend taking the small country boat across the river channel.",
            timestamp = System.currentTimeMillis() - 86400000L * 6
        ),
        ReviewEntity(
            targetType = "HOTSPOT",
            targetId = "kuldiha_wildlife",
            targetTitle = "Kuldiha Wildlife Sanctuary",
            userId = "niharbhuyan@gmail.com",
            userName = "Nihar Bhuyan",
            userLocality = "Balasore Town",
            rating = 5,
            comment = "Spotted a herd of wild elephants near Goharipradhan salt lick at 5 PM! The Rissia nature camp is very well maintained.",
            timestamp = System.currentTimeMillis() - 86400000L * 7
        ),
        ReviewEntity(
            targetType = "NEWS",
            targetId = "1",
            targetTitle = "Balasore Railway Station Redevelopment Under Amrit Bharat Project",
            userId = "niharbhuyan@gmail.com",
            userName = "Nihar Bhuyan",
            userLocality = "Balasore Town",
            rating = 5,
            comment = "The new escalators and airport-style roof plaza will be a game changer for coastal tourism and daily train commuters to Bhubaneswar and Kolkata.",
            timestamp = System.currentTimeMillis() - 86400000L * 1
        ),
        ReviewEntity(
            targetType = "NEWS",
            targetId = "2",
            targetTitle = "Vanishing Sea Chandipur Draws Thousands; Tide Safety Advisory Issued",
            userId = "soumya.nayak@balasore.org",
            userName = "Soumya Nayak",
            userLocality = "Remuna",
            rating = 5,
            comment = "Great initiative by the district police to install automated tidal siren warnings. Tourist safety should always be number one.",
            timestamp = System.currentTimeMillis() - 86400000L * 2
        ),
        ReviewEntity(
            targetType = "WEATHER",
            targetId = "coastal_weather_alert",
            targetTitle = "Balasore Marine & Coastal Weather System",
            userId = "niharbhuyan@gmail.com",
            userName = "Nihar Bhuyan",
            userLocality = "Balasore Town",
            rating = 5,
            comment = "The real-time tide prediction and wind speed readings are spot-on! Helped us plan our trip to Chandipur beach during the exact low-tide window.",
            timestamp = System.currentTimeMillis() - 86400000L * 1
        )
    )
}
