package com.pingcap.gat.constant;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

public class ConfigConstant {
    public static final String CC_URL= "https://api.codecov.io/api/v2/github/%s/repos/%s/branches/%s/?format=json";

    public static final String CCTOKEN="2fe381f4-603f-4bb8-913c-dd9dd112faaf";
    public static final String GHTOKEN = "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";

    public static  String GHPRURI = "https://api.github.com/repos/%s/%s/pulls/%s";
    public static  String GHISSUESURI = "https://api.github.com/repos/%s/%s/pulls/%d";
    public static  String GHURI = "https://api.github.com/repos/%s/%s/issues";

    public static final String DATE_FORMAT="yyyy-MM-dd'T'HH:mm:ss";
    public static final String NULL_DATE="0000-00-00T00:00:00Z";
    public static final String SEARCH_CREATED_URI ="https://api.github.com/search/issues?q=repo:%s/%s/+created:";

    public static final String SEARCH_CLOSED_PR_SINCE_URI ="https://api.github.com/search/issues?per_page=%d&page=%d&order=asc&sort=created&";
    public static final String SEARCH_CLOSED_PR_SINCE_QUERY="q=repo:%s/%s/+is:pr+created:>=%s+closed:<=%s";

    public static final String SEARCH_CLOSED_URI ="https://api.github.com/search/issues?q=repo:%s/%s/+closed:";
    public static final String SEARCH_OPEN_URI ="https://api.github.com/search/issues?q=repo:%s/%s/+state:open";
    public static final String SEARCH_CREATED_BYTYPE_URI ="https://api.github.com/search/issues?q=repo:%s/%s/+is:%s+created:";
    public static final String SEARCH_CLOSED_BYTYPE_URI ="https://api.github.com/search/issues?q=repo:%s/%s/+is:%s+closed:";
    public static final String SEARCH_OPEN_BYTYPE_URI ="https://api.github.com/search/issues?q=repo:%s/%s/+is:%s+state:open";
    public static final String SEARCH_UPDATED_URI ="https://api.github.com/search/issues?q=repo:%s/%s/+updated:";
    public static final String GENERAL_SEARCH_URI ="https://api.github.com/search/issues?q=repo:%s/%s/";
    public static final String REST_REQUEST_URI="https://api.github.com/repos/%s/%s/issues?state=all";
    public static final String PAGING_POSTFIX="per_page=100&page=";
    public static final String ISSUE_REQUEST_URI="https://api.github.com/repos/%s/%s/issues/%d";

    public final static String UPLOAD_PATH_PREFIX = "static/uploadFile/";
//    public static final Map<String, JsonElement> SPRINTS;
//    static {
//        SPRINTS = new HashMap<String, JsonElement>();
//        SPRINTS.put("sprint 8", new JsonParser().parse("{\"version\":\"v6.1.0\",\"begin\":\"2022.4.11\",\"release\":\"22022.6.13\",\"codefreeze\":\"2022.5.20\"}"));
//        SPRINTS.put("sprint 9", new JsonParser().parse("{\"version\":\"v6.2.0-DMR\",\"begin\":\"2022.6.13\",\"release\":\"2022.8.23\",\"codefreeze\":\"2022.7.22\"}"));
//        SPRINTS.put("sprint 10", new JsonParser().parse("{\"version\":\"v6.3.0-DMR\",\"begin\":\"2022.8.22\",\"release\":\"2022.9.29\",\"codefreeze\":\"2022.9.20\"}"));
//        SPRINTS.put("sprint 11", new JsonParser().parse("{\"version\":\"v6.4.0-DMR\",\"begin\":\"2022.10.10\",\"release\":\"2022.11.17\",\"codefreeze\":\"2022.11.4\"}"));
//        SPRINTS.put("sprint 12", new JsonParser().parse("{\"version\":\"v6.5.0\",\"begin\":\"2022.11.21\",\"release\":\"2022.12.29\",\"codefreeze\":\"2022.12.2\"}"));
//        SPRINTS.put("sprint 13", new JsonParser().parse("{\"version\":\"v6.6.0-DMR\",\"begin\":\"2022.1.3\",\"release\":\"2023.2.20\",\"codefreeze\":\"2023.2.9\"}"));
//        SPRINTS.put("sprint 14", new JsonParser().parse("{\"version\":\"v7.0.0-DMR\",\"begin\":\"2023.2.20\",\"release\":\"2023.3.30\",\"codefreeze\":\"2023.3.20\"}"));
//        SPRINTS.put("sprint 15", new JsonParser().parse("{\"version\":\"v7.1.0-rc\",\"begin\":\"2023.4.3\",\"release\":\"2023.5.31\",\"codefreeze\":\"2023.4.20\"}"));
//        SPRINTS.put("sprint 16", new JsonParser().parse("{\"version\":\"v7.2.0-DMR\",\"begin\":\"2023.6.1\",\"release\":\"2023.6.29\",\"codefreeze\":\"2023.6.19\"}"));
//
//
//
//    }
//    public static final JsonArray CHERRYPICKINFO;
//    static
//    {
//        CHERRYPICKINFO = new JsonArray();
//        CHERRYPICKINFO.add(new JsonParser().parse("{\"key\":\"needs-cherry-pick-release-7.1\",\"value\":\"N\"}"));
//        CHERRYPICKINFO.add(new JsonParser().parse("{\"key\":\"needs-cherry-pick-release-7.0\",\"value\":\"N\"}"));
//        CHERRYPICKINFO.add(new JsonParser().parse("{\"key\":\"needs-cherry-pick-release-6.5\",\"value\":\"N\"}"));
//        CHERRYPICKINFO.add(new JsonParser().parse("{\"key\":\"needs-cherry-pick-release-6.4\",\"value\":\"N\"}"));
//        CHERRYPICKINFO.add(new JsonParser().parse("{\"key\":\"needs-cherry-pick-release-6.3\",\"value\":\"N\"}"));
//        CHERRYPICKINFO.add(new JsonParser().parse("{\"key\":\"needs-cherry-pick-release-6.2\",\"value\":\"N\"}"));
//        CHERRYPICKINFO.add(new JsonParser().parse("{\"key\":\"needs-cherry-pick-release-6.1\",\"value\":\"N\"}"));
//        CHERRYPICKINFO.add(new JsonParser().parse("{\"key\":\"needs-cherry-pick-release-6.0\",\"value\":\"N\"}"));
//        CHERRYPICKINFO.add(new JsonParser().parse("{\"key\":\"needs-cherry-pick-release-5\",\"value\":\"N\"}"));
//        CHERRYPICKINFO.add(new JsonParser().parse("{\"key\":\"needs-cherry-pick-release-4\",\"value\":\"N\"}"));
//        CHERRYPICKINFO.add(new JsonParser().parse("{\"key\":\"affects-7.1\",\"value\":\"N\"}"));
//        CHERRYPICKINFO.add(new JsonParser().parse("{\"key\":\"affects-7.0\",\"value\":\"N\"}"));
//        CHERRYPICKINFO.add(new JsonParser().parse("{\"key\":\"affects-6.6\",\"value\":\"N\"}"));
//        CHERRYPICKINFO.add(new JsonParser().parse("{\"key\":\"affects-6.5\",\"value\":\"N\"}"));
//        CHERRYPICKINFO.add(new JsonParser().parse("{\"key\":\"affects-6.4\",\"value\":\"N\"}"));
//        CHERRYPICKINFO.add(new JsonParser().parse("{\"key\":\"affects-6.3\",\"value\":\"N\"}"));
//        CHERRYPICKINFO.add(new JsonParser().parse("{\"key\":\"affects-6.2\",\"value\":\"N\"}"));
//        CHERRYPICKINFO.add(new JsonParser().parse("{\"key\":\"affects-6.1\",\"value\":\"N\"}"));
//        CHERRYPICKINFO.add(new JsonParser().parse("{\"key\":\"affects-6.0\",\"value\":\"N\"}"));
//        CHERRYPICKINFO.add(new JsonParser().parse("{\"key\":\"affects-5\",\"value\":\"N\"}"));
//        CHERRYPICKINFO.add(new JsonParser().parse("{\"key\":\"affects-4\",\"value\":\"N\"}"));
//
//
//    }
}
