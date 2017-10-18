package com.sbai.finance;

/**
 * 会使用多次的 Intent Extra key 放这里，针对特别页面的放页面里面
 */
public interface ExtraKeys {

    String PAGE_TYPE = "page_type";
    String PHONE = "phone";
    String HAS_SECURITY_PSD = "has_security_psd";
    String HAS_LOGIN_PSD = "has_login_psd";
    String AUTH_CODE = "auth_code";

    String TRAINING = "training";
    String QUESTION = "question";
    String PRAISE = "praise";

    String TRAINING_DETAIL = "training_targets";
    String TRAINING_SUBMIT = "training_submit";

    String FIRST_TEST = "first_test";
    String HISTORY_TEST_RESULT = "history_test_result";

    String TRAIN_QUESTIONS = "train_questions";
    String TRAIN_LEVEL = "train_level";
    String TRAIN_RESULT = "train_result";

    String RECHARGE_TYPE = "recharge_type";
    String USER_FUND = "user_fund";

    String BATTLE = "battle";

    String MAIN_PAGE_CURRENT_ITEM = "main_page_currentItem";
    String ACTIVITY = "activity";
    String VARIETY = "variety";

    String QUESTION_ID = "question_id";

    String PLAYING_ID = "playing_id";
    String PLAYING_URL= "playing_url";
    String PLAYING_AVATAR = "playing_avatar";
    String IS_FROM_MISS_TALK = "is_from_miss_talk";

    String PUSH_FEEDBACK = "push_feedback";
}
