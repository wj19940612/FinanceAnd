package com.sbai.finance.model.leveltest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${wangJie} on 2017/8/4.
 * 测试提交答案的model
 * {"answers":[{"answerIds":[{"optionId":892321037251899393}],"topicId":"5980309868fad7db045c8986"}]}
 * <p>
 * answerIds 可能有多个答案 optionId 选择的答案ID
 * topicId 题目ID
 */

public class TestAnswerUtils {

    private List<AnswersBean> answers;

    public List<AnswersBean> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<AnswersBean> answers) {
        this.answers = answers;
    }

    public static class AnswersBean {
        /**
         * answerIds : [{"optionId":892321037251899393}]
         * topicId : 5980309868fad7db045c8986
         */

        private String topicId;
        private List<AnswerIdsBean> answerIds;

        public String getTopicId() {
            return topicId;
        }

        public void setTopicId(String topicId) {
            this.topicId = topicId;
        }

        public List<AnswerIdsBean> getAnswerIds() {
            return answerIds;
        }

        public void setAnswerIds(List<AnswerIdsBean> answerIds) {
            this.answerIds = answerIds;
        }

        public static class AnswerIdsBean {
            /**
             * optionId : 892321037251899393
             */

            private long optionId;


            public long getOptionId() {
                return optionId;
            }

            public void setOptionId(long optionId) {
                this.optionId = optionId;
            }

            @Override
            public String toString() {
                return "AnswerIdsBean{" +
                        "optionId=" + optionId +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "AnswersBean{" +
                    "topicId='" + topicId + '\'' +
                    ", answerIds=" + answerIds +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TestAnswerUtils{" +
                "answers=" + answers +
                '}';
    }
}
