package org.example;

import org.example.wiki_article.Article;

import java.util.Comparator;

public class CustomComparator implements Comparator<Article> {
    @Override
    public int compare(Article o1, Article o2) {
        return o1.getTitle().compareTo(o2.getTitle());
    }
}