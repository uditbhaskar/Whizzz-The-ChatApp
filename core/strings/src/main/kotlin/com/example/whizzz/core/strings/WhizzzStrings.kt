package com.example.whizzz.core.strings

/**
 * Global string constants: UI copy, errors, Firebase schema keys, prefs, and FCM.
 *
 * @author udit
 */
object WhizzzStrings {

    /**
     * Navigation route segments (Compose NavHost).
     *
     * @author udit
     */
    object Nav {
        const val SPLASH = "splash"
        const val LOGIN = "login"
        const val REGISTER = "register"
        const val FORGOT = "forgot"
        const val HOME = "home"
        const val CHAT_PREFIX = "chat"
        const val CHAT_PATTERN = "$CHAT_PREFIX/{peerId}"
        const val ARG_PEER_ID = "peerId"
    }

    /**
     * Main UI labels and titles.
     *
     * @author udit
     */
    object Ui {
        const val APP_NAME = "Whizzz"
        const val LOGIN = "Login"
        const val REGISTER = "Register"
        const val SIGN_UP = "Sign up"
        const val EMAIL = "Email"
        const val PASSWORD = "Password"
        const val USERNAME = "Username"
        const val SIGN_UP_HERE = "Sign up here."
        const val ALREADY_HAVE_AN_ACCOUNT = "Already have an account?"
        const val LOGIN_HERE = " Login."
        const val DONT_HAVE_AN_ACCOUNT = "Don't have an account?"
        const val LOG_OUT = "Log out"
        const val CHATS = "Chats"
        const val USERS = "Users"
        const val PROFILE = "Profile"
        const val NAME = "Name"
        const val SEEN = "Seen"
        const val SEARCH = "Search…"
        const val TYPE_MESSAGE = "Type a message"
        const val SEND = "Send"
        const val NO_CHATS_YET = "All your chats will appear here!"
        const val BIO = "Bio"
        const val SAVE = "Save"
        const val EDIT_USERNAME = "Edit username"
        const val EDIT_BIO = "Edit bio"
        const val CHANGE_PHOTO = "Change photo"
        const val RESET_EMAIL_HINT = "Enter your email"
        const val SEND_RESET = "Send reset link"
        const val BACK = "Back"
        const val TRY_AGAIN = "Try again"
        const val CLOSE = "Close"
        const val OFFLINE_INDICATOR =
            "You're offline. Chats and profile may not update until you reconnect."
        const val HI_THERE = "Hi there!"
        const val LOG_IN_TO_CONTINUE = "Log in to continue."
        const val WELCOME_SIGN = "Welcome!"
        const val FORGET_PASSWORD_LINE = "Forget password?\u0020"
        const val RESET_PASSWORD_TITLE = "Reset Password"
        const val SHOW_PASSWORD = "Show password"
        const val HIDE_PASSWORD = "Hide password"
    }

    /**
     * Error and validation messages.
     *
     * @author udit
     */
    object Errors {
        const val GENERIC = "Something went wrong. Try again."
        const val FILL_EMAIL_PASSWORD = "Fill in email and password."
        const val SIGN_IN_FAILED = "Sign-in failed."
        const val ALL_FIELDS_REQUIRED = "All fields are required."
        const val REGISTRATION_FAILED = "Registration failed."
        const val ENTER_EMAIL = "Enter your email."
        const val REQUEST_FAILED = "Request failed."
        const val UPDATE_FAILED = "Update failed."
        const val UPLOAD_FAILED = "Upload failed."
        const val NOT_SIGNED_IN = "Not signed in"
        const val NO_UID_AFTER_SIGNUP = "No UID after sign-up"
        const val FIREBASE_CONFIG_API_KEY =
            "Firebase is not set up: in Firebase Console add an Android app with package com.example.whizzz, " +
                "download google-services.json, and replace the file in app/."
        const val INVALID_EMAIL_OR_PASSWORD = "Incorrect email or password."
        const val INVALID_EMAIL = "That email address is not valid."
        const val EMAIL_ALREADY_IN_USE = "That email is already registered."
        const val WEAK_PASSWORD = "Password is too weak (use at least 6 characters)."
        const val USER_DISABLED = "This account has been disabled."
        const val TOO_MANY_ATTEMPTS = "Too many attempts. Try again later."
        const val OPERATION_NOT_ALLOWED =
            "Email/password sign-in is disabled. Enable it in Firebase Console → Authentication → Sign-in method."
        const val NETWORK_ERROR = "Network error. Check your connection."
        const val OFFLINE_GENERIC =
            "You're offline. Check your Wi-Fi or mobile data, then try again."
        const val OFFLINE_AUTH_SIGN_IN =
            "You're offline. Connect to the internet to sign in."
        const val OFFLINE_AUTH_SIGN_UP =
            "You're offline. Connect to the internet to create an account."
        const val OFFLINE_AUTH_RESET =
            "You're offline. Connect to the internet to send a reset link."
        const val LOAD_PROFILE_FAILED =
            "Couldn't load your profile. Check your connection and try again."
        const val LOAD_MESSAGES_FAILED =
            "Couldn't load messages. Check your connection and try again."
        const val LOAD_PEOPLE_FAILED =
            "Couldn't load people. Check your connection and try again."
        const val LOAD_CHATS_FAILED =
            "Couldn't load your chats. Check your connection and try again."
        const val MESSAGE_SEND_FAILED =
            "Message couldn't be sent. Check your connection and try again."
        const val PROFILE_SAVE_OFFLINE =
            "You're offline. Connect to the internet to save changes."
        const val PROFILE_PHOTO_OFFLINE =
            "You're offline. Connect to the internet to update your photo."
    }

    /**
     * Success / info messages.
     *
     * @author udit
     */
    object Messages {
        const val RESET_EMAIL_SENT = "Check your inbox for reset instructions."
    }

    /**
     * Realtime Database nodes and field keys.
     *
     * @author udit
     */
    object Db {
        const val NODE_USERS = "Users"
        const val NODE_CHATS = "Chats"
        const val NODE_CHAT_LIST = "ChatList"
        const val NODE_TOKENS = "Tokens"

        const val CHILD_ID = "id"
        const val CHILD_USERNAME = "username"
        const val CHILD_EMAIL_ID = "emailId"
        const val CHILD_TIMESTAMP = "timestamp"
        const val CHILD_IMAGE_URL = "imageUrl"
        const val CHILD_BIO = "bio"
        const val CHILD_STATUS = "status"
        const val CHILD_SEARCH = "search"
        const val CHILD_SENDER_ID = "senderId"
        const val CHILD_RECEIVER_ID = "receiverId"
        const val CHILD_MESSAGE = "message"
        const val CHILD_SEEN = "seen"
        const val CHILD_TOKEN = "token"

        const val ORDER_BY_USERNAME = "username"
        const val ORDER_BY_SEARCH = "search"
        const val SEARCH_SUFFIX_HIGH = "\uf8ff"
    }

    /**
     * Default profile values on registration.
     *
     * @author udit
     */
    object Defaults {
        const val PROFILE_IMAGE = "default"
        const val NEW_USER_BIO = "Hey there!"
        const val STATUS_OFFLINE = "offline"
        const val PRESENCE_ONLINE = "online"
    }

    /**
     * SharedPreferences keys.
     *
     * @author udit
     */
    object Prefs {
        const val FILE_NAME = "PREFS"
        const val KEY_CURRENT_USER = "currentuser"
        const val VALUE_NO_ACTIVE_CHAT = "none"
    }

    /**
     * FCM notification title and data-map keys (client + Cloud Functions).
     *
     * @author udit
     */
    object Fcm {
        const val NOTIFICATION_TITLE = "New Message"
        const val USER = "user"
        const val BODY = "body"
        const val TITLE = "title"
        const val SENT = "sent"
    }

    /**
     * Media / file hints.
     *
     * @author udit
     */
    object Media {
        const val JPEG_EXTENSION = "jpg"
    }

    /**
     * System notification channel (FCM).
     *
     * @author udit
     */
    object Notification {
        const val CHANNEL_ID_MESSAGES = "whizzz_messages"
        const val CHANNEL_NAME_MESSAGES = "Messages"
    }
}
