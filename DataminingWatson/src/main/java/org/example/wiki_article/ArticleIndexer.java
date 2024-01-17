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

public class ArticleIndexer {
	private IndexWriter indexWriter;
	String indexPath = "Wiki-Index";

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

	public void indexArticle(Article currArticle, ArticleIndexer index) throws IOException {
		System.out.println("Indexing Article " + currArticle.getTitle());

		Document doc = new Document();
		doc.add(new StringField("Title", currArticle.getTitle(), Field.Store.YES));

		FieldType fieldType = new FieldType(StringField.TYPE_STORED);
		fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
		if (currArticle.getCategories().isEmpty()) {
			doc.add(new Field("Category", "", fieldType));
		} else {
			for (String category : currArticle.getCategories()) {
				doc.add(new Field("Category", category, fieldType));
			}
		}
		doc.add(new TextField("Body", currArticle.getBody(), Field.Store.NO));
		String searchableText = currArticle.getTitle() + " " + " " + currArticle.getCategories() + " " + currArticle.getBody();
		doc.add(new TextField("Content", searchableText, Field.Store.NO));

		index.indexWriter.addDocument(doc);
	}

	public void buildIndexes() throws IOException, XMLStreamException {
		getIndexWriter();

		ArticleParser parser = new ArticleParser();
		parser.run(this);

		closeIndexWriter();
	}
}