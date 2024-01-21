package org.example.wiki_article;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArticleParseResult {
    Set<Article> articleSet;
    Map<String, List<String>> redirectPageTitles;

    public ArticleParseResult(Set<Article> articleSet, Map<String, List<String>> redirectPageTitles) {
        this.articleSet = articleSet;
        this.redirectPageTitles = redirectPageTitles;
    }

    public Set<Article> getArticleSet() {
        return articleSet;
    }

    public void setArticleSet(Set<Article> articleSet) {
        this.articleSet = articleSet;
    }

    public Map<String, List<String>> getRedirectPageTitles() {
        return redirectPageTitles;
    }

    public void setRedirectPageTitles(Map<String, List<String>> redirectPageTitles) {
        this.redirectPageTitles = redirectPageTitles;
    }
}
