package org.example.wiki_article;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ArticleIndexer {
	private IndexWriter indexWriter;
	static String indexPath = "Wiki-Index";

	public ArticleIndexer() {
	}

	private IndexWriter getIndexWriter() throws IOException {
		File dir = new File(indexPath);
		if (indexWriter == null) {
			if (!dir.exists()) {
				dir.mkdir();
			}

			Directory indexDir = FSDirectory.open(dir.toPath());
			IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
			config.setOpenMode(OpenMode.CREATE);
			indexWriter = new IndexWriter(indexDir, config);
		}
		return indexWriter;
	}

	private void closeIndexWriter() throws IOException {
		if (indexWriter != null) {
			indexWriter.commit();
			indexWriter.close();
		}
	}

	public void indexArticle(Article currentArticle, List<String> redirectedPages, ArticleIndexer index) throws IOException {
		System.out.println("Indexing Article " + currentArticle.getTitle());

		Document currentDocument = new Document();

		FieldType titleFieldType = new FieldType(StringField.TYPE_STORED);
		titleFieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
		currentDocument.add(new Field("Title", currentArticle.getTitle(), titleFieldType));

		if (redirectedPages != null) {
			for (String title : redirectedPages){
				currentDocument.add(new Field("Title", title, titleFieldType));
			}
		}

		FieldType categoryFieldType = new FieldType(StringField.TYPE_STORED);
		categoryFieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
		if (currentArticle.getCategories().isEmpty()) {
			currentDocument.add(new Field("Category", "", categoryFieldType));
		} else {
			for (String category : currentArticle.getCategories()) {
				currentDocument.add(new Field("Category", category, categoryFieldType));
			}
		}
		currentDocument.add(new TextField("Body", currentArticle.getBody(), Field.Store.NO));
//		String searchableText = "title:\"" + currentArticle.getTitle() + "\" OR cate" + currentArticle.getCategories() + " " + currentArticle.getBody();
//		currentDocument.add(new TextField("Content", currentArticle.getBody(), Field.Store.NO));

		index.indexWriter.addDocument(currentDocument);
	}

	public void buildIndexes() throws IOException, XMLStreamException {
		deleteFilesFromDirectory();
		getIndexWriter();

		ArticleParser parser = new ArticleParser();
		parser.run(this);

		closeIndexWriter();
	}

	public static void deleteFilesFromDirectory() {
		File folder = new File(indexPath);
		if (!folder.exists() || !folder.isDirectory()) {
			throw new RuntimeException("Folder does not exist");
		}

		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					file.delete();
				}
			}
		}
	}
}