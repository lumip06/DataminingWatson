package org.example.wiki_article;

import java.util.List;
import java.util.Map;

public class ArticleParseResult {
    List<Article> articleList;
    Map<String, String> redirectPageTitles;

    public ArticleParseResult(List<Article> articleList, Map<String, String> redirectPageTitles) {
        this.articleList = articleList;
        this.redirectPageTitles = redirectPageTitles;
    }

    public List<Article> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<Article> articleList) {
        this.articleList = articleList;
    }

    public Map<String, String> getRedirectPageTitles() {
        return redirectPageTitles;
    }

    public void setRedirectPageTitles(Map<String, String> redirectPageTitles) {
        this.redirectPageTitles = redirectPageTitles;
    }
}
