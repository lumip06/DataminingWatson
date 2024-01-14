package org.example.wiki_article;

public class Article {
	private String id;
	private String link;
	private String title;
	private String disc;

	/**
	 * Create a new Article instance
	 */
	public Article() {
	}

	/**
	 * Create a new Article instance
	 * 
	 * @param _id
	 *            Article ID
	 * @param _link
	 *            Article link (URL)
	 * @param _title
	 *            Article Title
	 * @param _disc
	 *            Article Description
	 */
	public Article(String _id, String _link, String _title, String _disc) {
		this.id = _id;
		this.title = _title;
		this.link = _link;
		this.disc = _disc;
	}

	/**
	 * Set the Article ID value
	 * 
	 * @param _id
	 *            Article ID value
	 */
	public void setId(String _id) {
		this.id = _id;
	}

	/**
	 * Get the Article ID value
	 * 
	 * @return Article ID
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Set the Article Link (URL) value
	 * 
	 * @param _link
	 *            Article Link value
	 */
	public void setLink(String _link) {
		this.link = _link;
	}

	/**
	 * Get the Article Link (URL) value
	 * 
	 * @return Article Link
	 */
	public String getLink() {
		return this.link;
	}

	/**
	 * Set the Article Title value
	 * 
	 * @param _title
	 *            Article Title value
	 */
	public void setTitle(String _title) {
		this.title = _title;
	}

	/**
	 * Get the Article Title value
	 * 
	 * @return Article Title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Set the Article Description value
	 * 
	 * @param _desc
	 *            Article Description value
	 */
	public void setDesc(String _desc) {
		this.disc = _desc;
	}

	/**
	 * Get the Article Description value
	 * 
	 * @return Article Description
	 */
	public String getDesc() {
		return this.disc;
	}

	public Article newArticle() {
		return new Article();
	}

	/**
	 * Pretty print Article instance
	 * 
	 * @return Article instance in a string
	 */
	public String toString() {
		String res = "Article " + getId() + ": **" + getTitle() + "** (" + getLink() + ")\n" + "-> " + getDesc() + "\n";

		return res;
	}
}
