package org.example.wiki_article;

import org.example.CustomComparator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ArticleParser {
	String dataPath = "src/main/java/org/example/wiki-subset-20140602";
	ArticleIndexer articleIndexer;

	public ArticleParser(ArticleIndexer articleIndexer) {
		this.articleIndexer = articleIndexer;
	}

	public void run() throws IOException {
		List<File> files = this.getFilesFromDirectory(new File(dataPath));
		ArticleParseResult result = this.parseFiles(files);

		this.addArticlesToIndexWriter(result);
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

	private void addArticlesToIndexWriter(ArticleParseResult result) throws IOException {
		for (Article currentArticle : result.getArticleSet()) {
			articleIndexer.indexArticle(currentArticle, result.getRedirectPageTitles().get(currentArticle.getTitle()));
		}
	}

	private ArticleParseResult parseFiles(List<File> files) {
		Set<Article> articleSet = new TreeSet<>(new CustomComparator());
		Map<String, List<String>> redirectPageTitles = new HashMap<>();
		ArticleParseResult result = new ArticleParseResult(articleSet, redirectPageTitles);

		int i = 1;
		System.out.println("\n");
		for (File file : files) {
			result = processFile(file.getPath(), articleSet, redirectPageTitles);

			articleSet = result.getArticleSet();
			redirectPageTitles = result.getRedirectPageTitles();

			System.out.println(">>> file no.: " + i);
			i++;
		}

		return result;
	}

	private ArticleParseResult processFile(String filePath, Set<Article> articleSet, Map<String, List<String>> redirectPageTitles) {
		Set<Article> auxArticleSet = new TreeSet<>(new CustomComparator());
		Map<String, List<String>> auxRedirectPageTitles = new HashMap<>();

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
						auxArticleSet.add(article);
					}
					wasRedirect = false;
					article = new Article();
					String title = retrieveTitle(line);
					article.setTitle(title);
				} else if (isCategoryLine(line)) {
					article.setCategories(tokenizeCategoryLine(line));
				} else if (isRedirectLine(line)) {
					String redirectPage = getRedirectPageTitle(line);
					if (!auxRedirectPageTitles.containsKey(redirectPage)) {
						auxRedirectPageTitles.put(redirectPage, new ArrayList<>());
					}
					auxRedirectPageTitles.get(redirectPage).add(article.getTitle());
					wasRedirect = true;
				} else {
					pageBody.append(line);
				}
			}

			if (!article.getTitle().isEmpty()) {
				auxArticleSet.add(article);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		articleSet.addAll(auxArticleSet);

		for (Map.Entry<String, List<String>> item : auxRedirectPageTitles.entrySet()) {
			String key = item.getKey();
			List<String> value = item.getValue();

			if (redirectPageTitles.containsKey(key)) {
				redirectPageTitles.get(key).addAll(value);
			} else {
				redirectPageTitles.put(key, value);
			}
		}

		return new ArticleParseResult(articleSet, redirectPageTitles);
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