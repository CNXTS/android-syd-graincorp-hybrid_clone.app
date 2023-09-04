package com.webling.graincorp.constants;

public final class Analytics {

    public static final class ScreenNames {
        public static final String LOGIN = "Login Screen";
        public static final String HOME = "Home Screen";
        public static final String NOTIFICATIONS = "Notifications Screen";
        public static final String NOTIFICATION_SETTINGS = "Notification Settings Screen";
        public static final String SETTINGS = "Settings Screen";
        public static final String MENU = "Menu Screen";
        public static final String ONBOARDING = "Onboarding Screen";
        public static final String INTERNAL_WEBVIEW = "Internal Webview Screen";
    }

    public static final class CustomEvents {
        // General
        public static final String APP_FOREGROUNDED = "app_foregrounded";
        public static final String OFFLINE_TRY_AGAIN_TAPPED = "tap_offline_try_again";

        // Login
        public static final String LOGIN_SUCCESSFUL = "login_successful";
        public static final String LOGIN_FAILURE = "login_failure";
        public static final String TAP_FORGOT_PASSWORD = "tap_forgot_password";
        public static final String TAP_REGISTER = "tap_register";

        // Home
        public static final String TAP_VIEW_DELIVERIES = "tap_view_deliveries";
        public static final String TAP_HOME_ADD_NOW = "tap_home_add_now";
        public static final String TAP_NEW_BID_PRICE_SEARCH = "tap_new_bid_price_search";

        // Notifications
        public static final String TAP_LIST_NOTIFICATION = "tap_list_notification";
        public static final String TAP_VIEW_NOTIFICATION_SETTINGS = "tap_view_notification_settings";

        // Settings
        public static final String TAP_LIST_NOTIFICATION_SETTINGS = "tap_list_notification_settings";
        public static final String TAP_OTHER_SETTINGS = "tap_other_settings";

        // Menu
        public static final String TAP_MENU_LIST_ITEM = "tap_menu_list_item";
        public static final String TAP_SWITCH_TO_BUYER = "tap_switch_to_buyer";
        public static final String TAP_SWITCH_TO_GROWER = "tap_switch_to_grower";
        public static final String TAP_LOGOUT = "tap_logout";
    }

    public static final class EventParameters {
        public static final String PARAMETER_PN_ID = "pn_id";
        public static final String PARAMETER_USER_DEVICE_ID = "user_device_id";
        public static final String PARAMETER_MENU_ITEM_URL = "menu_url";
    }
}