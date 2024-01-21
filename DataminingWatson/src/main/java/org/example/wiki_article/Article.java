package org.example.wiki_article;

import java.util.ArrayList;
import java.util.List;

public class Article {
	private String title;
	private List<String> categories;
	private String body;

	public Article() {
		this.title = "";
		this.categories = new ArrayList<>();
		this.body = "";
	}

	public Article(String title, List<String> categories, String body) {
		this.title = title;
		this.categories = categories;
		this.body = body;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Article newArticle() {
		return new Article();
	}

	public String toString() {
		String res = "Article " + getTitle() + ": **" + "https://ro.wikipedia.org/wiki/" + getTitle() + "** (" + getCategories().toString() + ")\n";

		return res;
	}
}
