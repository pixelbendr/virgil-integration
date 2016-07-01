package com.psyphertxt.android.cyfa;

import com.facebook.stetho.Stetho;
import com.firebase.client.Firebase;
import com.parse.Parse;
import com.parse.ParseObject;
import com.psyphertxt.android.cyfa.backend.parse.People;
import com.psyphertxt.android.cyfa.backend.parse.Profile;
import com.psyphertxt.android.cyfa.backend.parse.User;
import com.psyphertxt.android.cyfa.model.ContextUser;

import android.content.Context;
import android.content.res.Resources;

/**
 * This class contains application configuration and setup methods and constants
 */
public class Config {

    public static final String VERSION = "1.20.0";
    public static final Boolean IS_PROTO = false;
    public static final Boolean IS_PROD = false;
    public static Boolean IS_PASSCODE = false;
    public static Boolean IS_SAVED_PASSCODE = false;
    private static String FIREBASE_URL = "";
    public static final String CYFA_IO = "-cyfa-io";
    public static final String CYFA_IO_URL = "cyfa.io";

    //theme color names
    public static final String THEME_NAME_RED = "Pomegranate";
    public static final String THEME_NAME_YELLOW = "Amber";
    public static final String THEME_NAME_ORANGE = "Gold Drop";
    public static final String THEME_NAME_PINK = "Wild Strawberry";
    public static final String THEME_NAME_INDIGO = "Indigo";
    public static final String THEME_NAME_BLUE = "Robins Egg Blue";
    public static final String THEME_NAME_GRAY = "Lynch";
    public static final String THEME_NAME_GREEN = "Green Haze";
    public static final String KEY_COLOR_PRIMARY = "colorPrimary";
    public static final String KEY_COLOR_ACCENT = "colorAccent";

    //application constants
    public static final String KEY_OBJECT_ID = "objectId";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_CONTEXT_USER_ID = "contextUserId";
    public static final String KEY_MESSAGE_ID = "messageId";
    public static final String KEY_CONVERSATION_ID = "conversationId";
    public static final String KEY_PLAYER_ID = "playerId";
    public static final String KEY_GROUP_ID = "groupId";

    public static final String KEY_ACCEPTED = "accepted";
    public static final String KEY_IS_BLOCKED = "isBlocked";
    public static final String KEY_IS_CONTACT = "isContact";

    public static final String KEY_CONTACT = "contacts";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMER = "timer";
    public static final String KEY_SIGNATURE = "signature";
    public static final String KEY_USER = "user";
    public static final String KEY_USERS = "users";
    public static final String KEY_PROFILES = "profiles";
    public static final String KEY_PROFILE = "profile";
    public static final String KEY_CONTEXT_USER = "contextUser";
    public static final String KEY_DEVICE_USER = "deviceUser";
    public static final String KEY_PEOPLE = "people";
    public static final String KEY_NOTIFICATION = "notification";
    public static final String KEY_PHONE_NUMBER = "phoneNumber";
    public static final String KEY_CALLING_CODE = "callingCode";
    public static final String KEY_THEME = "theme";
    public static final String KEY_PASSCODE = "passcode";
    public static final String KEY_PROFILE_NAME = "profileName";
    public static final String KEY_TYPE = "type";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_CONTENT_TYPE = "contentType";
    public static final String KEY_GROUP_NAME = "groupName";
    public static final String KEY_NAME = "name";
    public static final String KEY_TITLE_ME = "Me";
    public static final String KEY_TITLE = "title";
    public static final String KEY_STAGE = "stage";
    public static final String KEY_SESSION_TOKEN = "sessionToken";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_CODE = "code";
    public static final String KEY_IS_EXISTING_USER = "isExistingUser";
    public static final String KEY_TEXT = "text";
    public static final String KEY_LAST_MESSAGE = "lastMessage";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_UPDATED_AT = "updatedAt";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_DELIVERED_AT = "deliveredAt";
    public static final String KEY_READ_AT = "readAt";
    public static final String KEY_STATUS = "status";
    public static final String KEY_ONLINE = "online";
    public static final String KEY_COUNT = "count";
    public static final String KEY_ACTIVE = "active";
    public static final String KEY_TYPING = "typing";
    public static final String KEY_RECENT = "recent";
    public static final String KEY_LAST_SEEN = "lastSeen";
    public static final String KEY_LINK = "link";
    public static final String KEY_CAPTION = "caption";
    static final String IDENTITY = "identity";
    static final String CARD_ID = "card_id";
    static final String PRIVATE_KEY = "privateKey";
    static final String PUBLIC_KEY = "publicKey";

    public static final String KEY_LIVE_TYPING = "live_typing";
    public static final String KEY_HIDE_REGULAR = "hide_regular";
    public static final String KEY_SHOW_TIMER = "show_timer";
    public static final String KEY_TIMER_INDEX = "timer_index";
    public static final String KEY_TIMER_VALUE = "timer_value";

    public static final String KEY = "key";
    public static final String VALUE = "value";
    public static final String NUMBER = "number";
    public static final String STRING = "string";
    public static final String TOKEN = "token";
    public static final String DATA = "data";
    public static final String HASH = "hash";

    public static final String SIGNUP_FLOW = "Sign Up Flow";
    public static final String LOGIN_FLOW = "Login Flow";
    public static final String BLANK_SLATE = "Blank Slate";
    public static final String THEMES = "Blank Slate";
    public static final String WALKTHROUGH_FLOW = "Walkthrough Flow";

    //convenient constants
    public static final int NUMBER_LENGTH = 8;
    public static final String TIMER_DEFAULT = "20";
    public static final String DELETED_MESSAGE = "Message was deleted";
    public static final int CODE_LENGTH = 4;
    public static final int DISPLAY_NAME_MIN_LENGTH = 4;
    public static final int DISPLAY_NAME_MAX_LENGTH = 14;
    public static final int STATUS_TEXT_MAX_LENGTH = 140;
    public static final int MESSAGE_LIMIT = 300;
    public static final int ZERO_LENGTH = 0;
    public static final int NUMBER_ONE = 1;
    public static final int DEFAULT_ANIMATION_DURATION = 1000;
    public static final int NUMBER_ZERO = 0;
    public static final int ONE_HUNDRED = 100;
    public static final String EMPTY_STRING = "";
    public static final String ELLIPSIS = "...";
    public static final String LAST_SEEN_STRING = "LAST SEEN ";

    //firebase constants
    //all firebase root references should be an auto generated name from
    //this URL http://kevinmlawson.com/herokuname/
    //so the expected format is [generated-name]-[firebase-reference-name]
    //example young-resonance-4834-games
    public static final String REF_MESSAGES = "ancient-pine-3010-messages";
    public static final String REF_SESSION = "damp-cloud-5318-session";
    public static final String REF_GROUPS = "ancient-pond-8420-groups";
    public static final String REF_STATUS = "crimson-haze-7489-status";
    public static final String REF_USERS = "throbbing-surf-8484-users";
    public static final String REF_SPEC = "lingering-lake-6221-spec";

    public static final String REF_CHILD_MEMBERS = "members";
    public static final String REF_CHILD_SESSION = "session";
    public static final String REF_CHILD_CONNECTED = "connected";
    public static final String REF_CONNECTION_STATUS = ".info/connected";
    public static final String REF_SERVER_TIME_OFFSET = ".info/serverTimeOffset";


    public static final String DB_NAME_CONTEXT_USERS = "CONTEXT_USERS";
    public static final String DB_NAME_PROFILE = "PROFILE";

    //parse cloud code function names
    public static final String DEFINE_USER_VALIDATION = "userValidation";
    public static final String DEFINE_NOTIFY_USER = "notifyUser";
    public static final String DEFINE_UPDATE_STATUS = "updateStatus";
    public static final String DEFINE_GET_CONTACTS = "getContacts";
    public static final String DEFINE_SETUP_USER = "setupUser";
    public static final String DEFINE_GENERATE_HASH = "generateHash";
    public static final String DEFINE_CHAT_REQUEST = "chatRequest";
    public static final String DEFINE_GENERATE_TOKEN = "generateToken";

    //one signal key|values
    public static final String ONE_SIGNAL = "onesignal_data";

    /**
     * this method should be called in a class which extends Application (entry point of main
     * application)
     * the reason being it contains most of the startup methods needed
     * for the application to work well
     *
     * @param context the application context
     */
    //TODO move setup functionality to grant
    public static void setup(Context context) {

        //enable local data store
       // Parse.enableLocalDatastore(context);

        //set up subclasses for parse
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Profile.class);
        ParseObject.registerSubclass(People.class);
        ParseObject.registerSubclass(ContextUser.class);

        //get application resources
        Resources resources = context.getResources();

        //Initialize Parse
        //set environment for Parse and Firebase
        if (Config.IS_PROD) {

            Parse.initialize(context, resources.getString(R.string.parse_applicationId_prod),
                    resources.getString(R.string.parse_clientKey_prod));
            FIREBASE_URL = resources.getString(R.string.firebase_prod);

        } else {

            Parse.initialize(new Parse.Configuration.Builder(context)
                    .applicationId("wFViOpJC6HdotAW4mvJByRbzPR6nKSrOAkpN9FaQ")
                    .clientKey("Wv9owXuKniqqM0kCf6roxwCkLlIKxKvIGY2RXTk3")
                    .server("https://cyfa-dev.herokuapp.com/parse/")
                    .enableLocalDataStore()
                    .build());

            FIREBASE_URL = resources.getString(R.string.firebase_dev);
        }

        //Initialize Firebase
        Firebase.setAndroidContext(context);

        //enable local storage for Firebase
        Firebase.getDefaultConfig().setPersistenceEnabled(true);

        //check if user has created a passcode
        Settings settings = new Settings(context);
       // settings.clearPasscode();
        if(!settings.getPasscode().isEmpty()){
            IS_SAVED_PASSCODE = true;
        }


    }

    public static void debug(Context context) {
        // Create an InitializerBuilder
        Stetho.InitializerBuilder initializerBuilder =
                Stetho.newInitializerBuilder(context);

        // Enable Chrome DevTools
        initializerBuilder.enableWebKitInspector(
                Stetho.defaultInspectorModulesProvider(context)
        );


        // Use the InitializerBuilder to generate an Initializer
        Stetho.Initializer initializer = initializerBuilder.build();

        // Initialize Stetho with the Initializer
        Stetho.initialize(initializer);
    }

    /**
     * returns a string with a firebase URL which could point to
     * dev or prod
     *
     * @return the firebase url
     */
    public static String getFirebaseURL() {
        return FIREBASE_URL;
    }

    public static final class NotificationType {
        public static final int TABBED = 0;
        public static final int NOTIFICATION = 1;
        public static final int PUSH_NOTIFICATION = 2;
        public static final int STATUS_NOTIFICATION = 3;
    }

}
