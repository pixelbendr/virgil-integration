package com.psyphertxt.android.cyfa.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NameUtils {

    private Random rnd = new Random();
    private String[] ADJECTIVES = {
            "autumn", "hidden", "bitter", "misty", "silent", "empty", "dry", "dark",
            "summer", "icy", "delicate", "quiet", "white", "cool", "spring", "winter",
            "patient", "twilight", "dawn", "crimson", "wispy", "weathered", "blue",
            "billowing", "broken", "cold", "damp", "falling", "frosty", "green",
            "long", "late", "lingering", "bold", "little", "morning", "muddy", "old",
            "red", "rough", "still", "small", "sparkling", "throbbing", "shy",
            "wandering", "withered", "wild", "black", "young", "holy", "solitary",
            "fragrant", "aged", "snowy", "proud", "floral", "restless", "divine",
            "polished", "ancient", "purple", "lively", "nameless", "lucky", "odd", "tiny",
            "free", "dry", "yellow", "orange", "gentle", "tight", "super", "royal", "broad",
            "steep", "flat", "square", "round", "mute", "noisy", "hushy", "raspy", "soft",
            "shrill", "rapid", "sweet", "curly", "calm", "jolly", "fancy", "plain", "shinny",
            "able", "action", "active", "actual", "adept", "adored", "adroit", "affectionate", "agile",
            "airy", "alert", "alive", "alter", "amiable", "ample", "and", "anima", "apt", "ardent", "are",
            "astute", "august", "avid", "awake", "aware", "balmy", "benevolent", "big", "billowing",
            "blessed", "bold", "boss", "brainy", "brave", "brawny", "breezy", "brief", "bright",
            "brisk", "busy", "calm", "can", "canny", "cared", "caring", "casual", "celestial", "charming",
            "chic", "chief", "choice", "chosen", "chummy", "civic", "civil", "classy", "clean", "clear",
            "clever", "close", "cogent", "composed", "condemned", "cool", "cosmic", "cozy", "cuddly",
            "cute", "dainty", "dandy", "dapper", "daring", "dear", "decent", "deep", "deft", "deluxe",
            "devout", "direct", "divine", "doted", "doting", "dreamy", "driven", "dry", "earthy", "easy",
            "elated", "end", "energized", "enigmatic", "equal", "exact", "exotic", "expert", "exuberant",
            "fair", "famed", "famous", "fancy", "fast", "fiery", "fine", "fit", "flashy", "fleet", "flowing",
            "fluent", "fluffy", "fluttering", "flying", "fond", "for", "frank", "free", "fresh", "frightened",
            "full", "fun", "funny", "fuscia", "gas", "genial", "gentle", "giddy", "gifted", "giving", "glad",
            "gnarly", "gold", "golden", "good", "goodly", "graceful", "grand", "greasy", "great", "green",
            "grieving", "groovy", "guided", "gutsy", "haloed", "happy", "hardy", "harmonious", "hearty",
            "heroic", "high", "hip", "hollow", "holy", "homeless", "honest", "huge", "human", "humane",
            "humble", "hunky", "icy", "ideal", "immune", "indigo", "inquisitive", "jazzed", "jazzy",
            "jolly", "jovial", "joyful", "joyous", "jubilant", "juicy", "just", "keen", "khaki",
            "kind", "kingly", "large", "lavish", "lawful", "left", "legal", "legit", "light", "like",
            "liked", "likely", "limber", "limitless", "lively", "loved", "lovely", "loyal", "lucid",
            "lucky", "lush", "main", "major", "master", "mature", "max", "maxed", "mellow", "merciful",
            "merry", "mighty", "mint", "mirthful", "modern", "modest", "money", "moonlit", "moral",
            "moving", "mucho", "mutual", "mysterious", "native", "natural", "near", "neat", "needed",
            "new", "nice", "nifty", "nimble", "noble", "normal", "noted", "novel", "okay", "open",
            "outrageous", "overt", "pacific", "parched", "peachy", "peppy", "pithy", "placid",
            "pleasant", "plucky", "plum", "poetic", "poised", "polite", "posh", "potent", "pretty",
            "prime", "primo", "prized", "pro", "prompt", "proper", "proud", "pumped", "punchy", "pure",
            "purring", "quaint", "quick", "quiet", "rad", "radioactive", "rapid", "rare", "reach", "ready",
            "real", "regal", "resilient", "rich", "right", "robust", "rooted", "rosy", "rugged", "safe",
            "sassy", "saucy", "savvy", "scenic", "screeching", "secret", "seemly", "sensitive", "serene",
            "sharp", "showy", "shrewd", "simple", "sleek", "slick", "smart", "smiley", "smooth", "snappy",
            "snazzy", "snowy", "snugly", "social", "sole", "solitary", "sound", "spacial", "spicy", "spiffy",
            "spry", "stable", "star", "stark", "steady", "stoic", "strong", "stunning", "sturdy", "suave",
            "subtle", "sunny", "sunset", "super", "superb", "sure", "swank", "sweet", "swell", "swift",
            "talented", "teal", "the", "thriving", "tidy", "timely", "top", "tops", "tough", "touted",
            "tranquil", "trim", "tropical", "true", "trusty", "try", "undisturbed", "unique", "united",
            "unsightly", "unwavering", "upbeat", "uplifting", "urbane", "usable", "useful", "utmost",
            "valid", "vast", "vestal", "viable", "vital", "vivid", "vocal", "vogue", "voiceless",
            "volant", "wandering", "wanted", "warm", "wealthy", "whispering", "whole", "winged",
            "wired", "wise", "witty", "wooden", "worthy", "zealous"
    };

    private String[] NOUNS = {
            "waterfall", "river", "breeze", "moon", "rain", "wind", "sea", "morning",
            "snow", "lake", "sunset", "pine", "shadow", "leaf", "dawn", "glitter",
            "forest", "hill", "cloud", "meadow", "sun", "glade", "bird", "brook",
            "butterfly", "bush", "dew", "dust", "field", "fire", "flower", "firefly",
            "feather", "grass", "haze", "mountain", "night", "pond", "darkness",
            "snowflake", "silence", "sound", "sky", "shape", "surf", "thunder",
            "violet", "water", "wildflower", "wave", "water", "resonance", "sun",
            "wood", "dream", "cherry", "tree", "fog", "frost", "voice", "paper",
            "frog", "smoke", "star", "atom", "band", "bar", "base", "block", "boat",
            "term", "credit", "art", "fashion", "truth", "disk", "math", "unit", "cell",
            "scene", "heart", "recipe", "union", "limit", "bread", "toast", "bonus",
            "lab", "mud", "mode", "poetry", "tooth", "hall", "king", "queen", "lion", "tiger",
            "penguin", "kiwi", "cake", "mouse", "rice", "coke", "hola", "salad", "hat",
            "abyss", "animal", "apple", "atoll", "aurora", "autumn", "bacon", "badlands", "ball",
            "banana", "bath", "beach", "bear", "bed", "bee", "bike", "bird", "boat", "book", "bowl",
            "branch", "bread", "breeze", "briars", "brook", "brush", "bunny", "candy", "canopy",
            "canyon", "car", "cat", "cave", "cavern", "cereal", "chair", "chasm", "chip", "cliff",
            "coal", "coast", "cookie", "cove", "cow", "crater", "creek", "darkness", "dawn", "desert",
            "dew", "dog", "door", "dove", "drylands", "duck", "dusk", "earth", "fall", "farm", "fern",
            "field", "firefly", "fish", "fjord", "flood", "flower", "flowers", "fog", "foliage", "forest",
            "freeze", "frog", "fu", "galaxy", "garden", "geyser", "gift", "glass", "grove", "guide", "guru",
            "hat", "hug", "hero", "hill", "horse", "house", "hurricane", "ice", "iceberg", "island", "juice",
            "lagoon", "lake", "land", "lawn", "leaf", "leaves", "light", "lion", "marsh", "meadow", "milk",
            "mist", "moon", "moss", "mountain", "mouse", "nature", "oasis", "ocean", "pants", "peak", "pebble",
            "pine", "pilot", "plane", "planet", "plant", "plateau", "pond", "prize", "rabbit", "rain", "range",
            "reef", "reserve", "resonance", "river", "rock", "sage", "salute", "sanctuary", "sand", "sands",
            "shark", "shelter", "shirt", "shoe", "silence", "sky", "smokescreen", "snowflake", "socks", "soil",
            "soul", "soup", "sparrow", "spoon", "spring", "star", "stone", "storm", "stream", "summer", "summit",
            "sun", "sunrise", "sunset", "sunshine", "surf", "swamp", "table", "teacher", "temple", "thorns", "tiger",
            "tigers", "towel", "train", "tree", "truck", "tsunami", "tundra", "valley", "volcano", "water",
            "waterfall", "waves", "wild", "willow", "window", "winds", "winter", "zebra"
    };

    private final String delimiter;
    private String tokenChars;
    private final int tokenLength;
    private final boolean tokenHex;

    NameUtils(String delimiter, String tokenChars, int tokenLength, boolean tokenHex) {
        this.delimiter = delimiter;
        this.tokenChars = tokenChars;
        this.tokenLength = tokenLength;
        this.tokenHex = tokenHex;
    }

    /**
     * Generate Heroku-like random names
     *
     * @return String
     */
    public String generate() {
        String adjective, noun, token = "";

        if (tokenHex) tokenChars = "0123456789abcdefghijklmnopqrstuvwxyz";

        adjective = ADJECTIVES[rnd.nextInt(ADJECTIVES.length)];
        noun = NOUNS[rnd.nextInt(NOUNS.length)];

        for (int i = 0; i < tokenLength; i++) {
            token += tokenChars.charAt(rnd.nextInt(tokenChars.length()));
        }

        List<String> list = new ArrayList<>(Arrays.asList(adjective, noun, token));
        list.removeAll(Arrays.asList("", null));

        return join(list, delimiter);
    }

    private static String join(List<String> list, String delim) {
        StringBuilder sb = new StringBuilder();
        String loopDelim = "";
        for (String s : list) {
            sb.append(loopDelim);
            sb.append(s);
            loopDelim = delim;
        }
        return sb.toString();
    }


}
