package com.mooc.commonbusiness.model.xuetang;

import java.io.Serializable;

/**
 * Created by peace on 2017/5/9.
 */

public class GetCourseExamsDataBean implements Serializable {

    /**
     * exam_gained_point : 0
     * homework_gained_point : 0
     * is_passed : false
     * chapters : [{"sequentials":[{"display_name":"6.4 毛笔写生演示","total_point":0,"format":"","due_time":null,"gained_point":0,"id":"971e88ed6aef4631912ec29688df745a"}],"display_name":"六、人物写生","id":"eb823f50b804495a9f312ec56359c4cf"}]
     * exam_total_point : 60
     * homework_total_point : 40
     * course_point : 0
     * passed_point : 100
     */

    private int exam_gained_point;
    private int homework_gained_point;
    private boolean is_passed;
    private int exam_total_point;
    private int homework_total_point;
    private int course_point;
    private int passed_point;
//    private List<ChaptersBean> chapters;

    public int getExam_gained_point() {
        return exam_gained_point;
    }

    public void setExam_gained_point(int exam_gained_point) {
        this.exam_gained_point = exam_gained_point;
    }

    public int getHomework_gained_point() {
        return homework_gained_point;
    }

    public void setHomework_gained_point(int homework_gained_point) {
        this.homework_gained_point = homework_gained_point;
    }

    public boolean isIs_passed() {
        return is_passed;
    }

    public void setIs_passed(boolean is_passed) {
        this.is_passed = is_passed;
    }

    public int getExam_total_point() {
        return exam_total_point;
    }

    public void setExam_total_point(int exam_total_point) {
        this.exam_total_point = exam_total_point;
    }

    public int getHomework_total_point() {
        return homework_total_point;
    }

    public void setHomework_total_point(int homework_total_point) {
        this.homework_total_point = homework_total_point;
    }

    public int getCourse_point() {
        return course_point;
    }

    public void setCourse_point(int course_point) {
        this.course_point = course_point;
    }

    public int getPassed_point() {
        return passed_point;
    }

    public void setPassed_point(int passed_point) {
        this.passed_point = passed_point;
    }

//    public List<ChaptersBean> getChapters() {
//        return chapters;
//    }
//
//    public void setChapters(List<ChaptersBean> chapters) {
//        this.chapters = chapters;
//    }

//    public static class ChaptersBean {
//        /**
//         * sequentials : [{"display_name":"6.4 毛笔写生演示","total_point":0,"format":"","due_time":null,"gained_point":0,"id":"971e88ed6aef4631912ec29688df745a"}]
//         * display_name : 六、人物写生
//         * id : eb823f50b804495a9f312ec56359c4cf
//         */
//
//        private String display_name;
//        private String id;
//        private List<SequentialsBean> sequentials;
//
//        public String getDisplay_name() {
//            return display_name;
//        }
//
//        public void setDisplay_name(String display_name) {
//            this.display_name = display_name;
//        }
//
//        public String getId() {
//            return id;
//        }
//
//        public void setId(String id) {
//            this.id = id;
//        }
//
//        public List<SequentialsBean> getSequentials() {
//            return sequentials;
//        }
//
//        public void setSequentials(List<SequentialsBean> sequentials) {
//            this.sequentials = sequentials;
//        }
//
//        public static class SequentialsBean {
//            /**
//             * display_name : 6.4 毛笔写生演示
//             * total_point : 0
//             * has_problem : false
//             * due_time : null
//             * gained_point : 0
//             * id : 971e88ed6aef4631912ec29688df745a
//             */
//
//            private String display_name;
//            private int total_point;
//            private String due_time;
//            private boolean has_submitted;
//            private int gained_point;
//            private String id;
//            private boolean has_problem;
//
//            public boolean isHas_problem() {
//                return has_problem;
//            }
//
//            public void setHas_problem(boolean has_problem) {
//                this.has_problem = has_problem;
//            }
//
//            public String getDisplay_name() {
//                return display_name;
//            }
//
//            public void setDisplay_name(String display_name) {
//                this.display_name = display_name;
//            }
//
//            public int getTotal_point() {
//                return total_point;
//            }
//
//            public void setTotal_point(int total_point) {
//                this.total_point = total_point;
//            }
//
//            public String getDue_time() {
//                return due_time;
//            }
//
//            public void setDue_time(String due_time) {
//                this.due_time = due_time;
//            }
//
//            public int getGained_point() {
//                return gained_point;
//            }
//
//            public void setGained_point(int gained_point) {
//                this.gained_point = gained_point;
//            }
//
//            public String getId() {
//                return id;
//            }
//
//            public void setId(String id) {
//                this.id = id;
//            }
//
//            public boolean isHas_submitted() {
//                return has_submitted;
//            }
//
//            public void setHas_submitted(boolean has_submitted) {
//                this.has_submitted = has_submitted;
//            }
//        }
//    }
}
