package org.example.wiki_article;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ArticleParser {
	ArticleIndexer articleIndexer = null;
	String dataPath = "src/main/java/org/example/wiki-subset-20140602";

	public void run(ArticleIndexer articleIndexer) throws IOException {
		this.articleIndexer = articleIndexer;

		List<File> files = this.getFilesFromDirectory(new File(dataPath));
		List<Article> articleList = this.createArticlesFromDirectory(files);

		this.addArticlesToIndexWriter(articleList);
	}

	private void addArticlesToIndexWriter(List<Article> articleList) throws IOException {
		for (Article currArt : articleList) {
			articleIndexer.indexArticle(currArt, articleIndexer);
		}
	}

	public List<File> getFilesFromDirectory(File dir) {
		List<File> directories = new ArrayList<>();

		if (!dir.exists() || !dir.isDirectory()) {
			throw new RuntimeException("Folder does not exist");
		}

		File[] files = dir.listFiles();

		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					directories.add(file);
				}
			}
		} else {
			throw new RuntimeException("Files not found");
		}
		return directories;
	}

	private List<Article> createArticlesFromDirectory(List<File> files) {
		List<Article> articleList = new ArrayList<>();
		Map<String, String> redirectPageTitles = new HashMap<>();

		int i = 1;
		System.out.println("\n");
		for (File file : files) {
			ArticleParseResult result = processFile(file.getPath(), articleList, redirectPageTitles);

			articleList = result.getArticleList();
			redirectPageTitles = result.getRedirectPageTitles();

			System.out.println(">>> file no.: " + i);
			i++;
		}

		return articleList;
	}

	private ArticleParseResult processFile(String filePath, List<Article> articleList, Map<String, String> redirectPageTitles) {
		List<Article> auxArticleList = new ArrayList<>();
		Map<String, String> auxRedirectPageTitles = new HashMap<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			StringBuilder pageBody = new StringBuilder();
			Article article = new Article();
			boolean wasRedirect = false;

			while ((line = reader.readLine()) != null) {
				if (line.isBlank()) {
					continue;
				}
				if (isTitle(line)) {
					if (!article.getTitle().isEmpty() && !wasRedirect) {
						article.setBody(pageBody.toString());
						pageBody = new StringBuilder();
						auxArticleList.add(article);
					}
					wasRedirect = false;
					article = new Article();
					String title = retrieveTitle(line);
					article.setTitle(title);
				} else if (isCategoryLine(line)) {
					article.setCategories(tokenizeCategoryLine(line));
				} else if (isRedirectLine(line)) {
					auxRedirectPageTitles.put(article.getTitle(), getRedirectPageTitle(line));
					wasRedirect = true;
				} else {
					pageBody.append(line);
				}
			}

			if (!article.getTitle().isEmpty()) {
				auxArticleList.add(article);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		articleList.addAll(auxArticleList);
		redirectPageTitles.putAll(auxRedirectPageTitles);

		return new ArticleParseResult(auxArticleList, redirectPageTitles);
	}

	public static boolean isTitle(String input) {
		if (input == null) {
			return false;
		}
		String regex = "^\\[\\[(.*?)]]$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

	public static String retrieveTitle(String input) {
		String result = input.substring("[[".length());
		return result.substring(0, result.length() - "]]".length());
	}

	public static boolean isCategoryLine(String input) {
		return input != null && input.startsWith("CATEGORIES:");
	}

	public static List<String> tokenizeCategoryLine(String input) {
		return new ArrayList<>(Stream.of(input.substring("CATEGORIES:".length()).split(",")).map(String::trim).toList());
	}

	public static boolean isRedirectLine(String input) {
		return input != null && (input.startsWith("#REDIRECT") || input.startsWith("#redirect"));
	}

	public static String getRedirectPageTitle(String input) {
		return input.substring("#REDIRECT".length()).trim();
	}
}