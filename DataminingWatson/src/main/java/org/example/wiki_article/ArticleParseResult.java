package org.example.wiki_article;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArticleParseResult {
    Set<Article> articleList;
    Map<String, List<String>> redirectPageTitles;

    public ArticleParseResult(Set<Article> articleList, Map<String, List<String>> redirectPageTitles) {
        this.articleList = articleList;
        this.redirectPageTitles = redirectPageTitles;
    }

    public Set<Article> getArticleList() {
        return articleList;
    }

    public void setArticleList(Set<Article> articleList) {
        this.articleList = articleList;
    }

    public Map<String, List<String>> getRedirectPageTitles() {
        return redirectPageTitles;
    }

    public void setRedirectPageTitles(Map<String, List<String>> redirectPageTitles) {
        this.redirectPageTitles = redirectPageTitles;
    }
}
