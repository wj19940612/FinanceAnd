package com.sbai.finance.activity.leveltest;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.leveltest.ExamQuestionsModel;
import com.sbai.finance.net.Client;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.TitleBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LevelTestStartActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.joinPeopleNumber)
    TextView mJoinPeopleNumber;
    @BindView(R.id.startTest)
    TextView mStartTest;
    @BindView(R.id.historyResult)
    TextView mHistoryResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_test_start);
        ButterKnife.bind(this);
        translucentStatusBar();

        requestJoinTestedNumber();
        mJoinPeopleNumber.setText(getString(R.string.number_people_join, 0));

        // TODO: 2017/8/2 从登陆注册页面进来
        mTitleBar.setLeftText(R.string.pass);
        mTitleBar.setLeftTextColor(Color.WHITE);

    }

    // TODO: 2017/7/31 缺少请求参加测试人数接口
    private void requestJoinTestedNumber() {
        Client.requestJoinTestedNumber()
                .setTag(TAG)
                .setIndeterminate(this)
                .fireFree();
    }

    @OnClick({R.id.startTest, R.id.historyResult})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.startTest:
                if (LocalUser.getUser().isLogin()) {
                    openLevelExamQuestionsPage();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.historyResult:
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), HistoryTestResultActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
        }
    }

    // TODO: 2017/7/31 请求题库接口
    private void openLevelExamQuestionsPage() {
        Client.requestExamQuestions()
                .setTag(TAG)
                .setIndeterminate(this)
                .fire();

        ArrayList<ExamQuestionsModel> examQuestionsModels = new ArrayList<>();

        ExamQuestionsModel examQuestionsModel = new ExamQuestionsModel();
        ArrayList<ExamQuestionsModel.DataBean> dataBeenList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            ExamQuestionsModel.DataBean dataBean = new ExamQuestionsModel.DataBean(" 正常答案答案啊啊啊啊 " + i);
            dataBeenList.add(dataBean);
        }
        examQuestionsModel.setDataList(dataBeenList);
        examQuestionsModel.setTopic("这是一个正常的问题————请给予答案");
        examQuestionsModels.add(examQuestionsModel);


        ExamQuestionsModel examQuestionsModel1 = new ExamQuestionsModel();
        ArrayList<ExamQuestionsModel.DataBean> dataBeanArrayList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ExamQuestionsModel.DataBean aaaaa = new ExamQuestionsModel.DataBean("这是变态答案  哈哈哈哈哈哈哈哈哈哈哈安徽哈哈哈哈\n这是变态答案  哈哈哈哈哈哈哈哈哈哈哈安徽哈哈哈哈\n" +
                    "这是变态答案  哈哈哈哈哈哈哈哈哈哈哈安徽哈哈哈哈\n这是变态答案  哈哈哈哈哈哈哈哈哈哈哈安徽哈哈哈哈\n这是变态答案  哈哈哈哈哈哈哈哈哈哈哈安徽哈哈哈哈\n这是变态答案  哈哈哈哈哈哈哈哈哈哈哈安徽哈哈哈哈\n" +
                    "这是变态答案  哈哈哈哈哈哈哈哈哈哈哈安徽哈哈哈哈\n这是变态答案  哈哈哈哈哈哈哈哈哈哈哈安徽哈哈哈哈\n这是变态答案  哈哈哈哈哈哈哈哈哈哈哈安徽哈哈哈哈\n这是变态答案  哈哈哈哈哈哈哈哈哈哈哈安徽哈哈哈哈\n这是变态答案  哈哈哈哈哈哈哈哈哈哈哈安徽哈哈哈哈\n这是变态答案  哈哈哈哈哈哈哈哈哈哈哈安徽哈哈哈哈\n这是变态答案  哈哈哈哈哈哈哈哈哈哈哈安徽哈哈哈哈\n这是变态答案  哈哈哈哈哈哈哈哈哈哈哈安徽哈哈哈哈\n");
            dataBeanArrayList.add(aaaaa);
        }
        examQuestionsModel1.setTopic("这是一个变态  打死他 打死他 ");
        examQuestionsModel1.setDataList(dataBeanArrayList);
        examQuestionsModels.add(examQuestionsModel1);

        Launcher.with(getActivity(), LevelExamQuestionsActivity.class)
                .putExtra(Launcher.EX_PAYLOAD, examQuestionsModels)
                .execute();
    }
}
