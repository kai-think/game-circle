package com.kai.common.config;

public class ConstantGB {
    public static class BbsStatus {
        public final static Integer Draft = 0;
        public final static Integer Published = 1;
        public final static Integer Deleted_By_User = -1;
        public final static Integer Deleted_By_Admin = -2;
    }

    public static class BbsType {
        public final static Integer Common = 0;
        public final static Integer Official = 1;
        public final static Integer Essence = 2;
        public final static Integer Activity = -1;
    }

    //0正常，-1被用户删除，-2被管理员删除
    public static class BbsReplyStatus {
        public final static Integer Normal = 0;
        public final static Integer Deleted_By_User = -1;
        public final static Integer Deleted_By_Admin = -2;
    }

    public static class BbsReplyType {
        public final static Integer Reply_Bbs = 0;
        public final static Integer Reply_Reply = 1;
        public final static Integer Reply_Reply_Reply = 2;
    }

    public static class ArticleReplyType {
        public final static Integer Reply_Article = 0;
        public final static Integer Reply_Reply = 1;
    }

    public static class ActivityStatus {
        public final static Integer Create = 0;
        public final static Integer Start = 1;
        public final static Integer End = 2;
        public final static Integer Cancel = -1;
    }
}
