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


    String FIRST_TEST = "first_test";
    String HISTORY_TEST_RESULT = "history_test_result";

    String TRAIN_QUESTIONS = "train_questions";
}
