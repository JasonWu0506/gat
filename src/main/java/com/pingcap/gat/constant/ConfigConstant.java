package com.pingcap.gat.constant;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

public class ConfigConstant {
    public static final String CC_URL= "https://api.codecov.io/api/v2/github/%s/repos/%s/branches/%s/?format=json";

   

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

}
