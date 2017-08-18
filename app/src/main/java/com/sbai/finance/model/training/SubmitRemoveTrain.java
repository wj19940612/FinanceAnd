package com.sbai.finance.model.training;

import java.util.List;

/**
 * 提交训练答案
 */

public class SubmitRemoveTrain {

    /**
     * answers : [{"answerIdsMap":[{"v":{"optionId":893366896689184770},"k":{"optionId":893366896689184769}},{"v":{"optionId":893366896689184772},"k":{"optionId":893366896689184771}},{"v":{"optionId":893366896689184774},"k":{"optionId":893366896689184773}}],"topicId":"5981874668fa2534449545bf"}]
     * dataId : 1
     */

    private int dataId;
    private List<AnswersBean> answers;

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public List<AnswersBean> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswersBean> answers) {
        this.answers = answers;
    }

    public static class AnswersBean {
        /**
         * answerIdsMap : [{"v":{"optionId":893366896689184770},"k":{"optionId":893366896689184769}},{"v":{"optionId":893366896689184772},"k":{"optionId":893366896689184771}},{"v":{"optionId":893366896689184774},"k":{"optionId":893366896689184773}}]
         * topicId : 5981874668fa2534449545bf
         */

        private String topicId;
        private List<AnswerIdsMapBean> answerIdsMap;

        public String getTopicId() {
            return topicId;
        }

        public void setTopicId(String topicId) {
            this.topicId = topicId;
        }

        public List<AnswerIdsMapBean> getAnswerIdsMap() {
            return answerIdsMap;
        }

        public void setAnswerIdsMap(List<AnswerIdsMapBean> answerIdsMap) {
            this.answerIdsMap = answerIdsMap;
        }

        public static class AnswerIdsMapBean {
            /**
             * v : {"optionId":893366896689184770}
             * k : {"optionId":893366896689184769}
             */

            private VBean v;
            private KBean k;

            public VBean getV() {
                return v;
            }

            public void setV(VBean v) {
                this.v = v;
            }

            public KBean getK() {
                return k;
            }

            public void setK(KBean k) {
                this.k = k;
            }

            public static class VBean {
                /**
                 * optionId : 893366896689184770
                 */

                private String optionId;

                public String getOptionId() {
                    return optionId;
                }

                public void setOptionId(String optionId) {
                    this.optionId = optionId;
                }
            }

            public static class KBean {
                /**
                 * optionId : 893366896689184769
                 */

                private String optionId;

                public String getOptionId() {
                    return optionId;
                }

                public void setOptionId(String optionId) {
                    this.optionId = optionId;
                }
            }
        }
    }
}
