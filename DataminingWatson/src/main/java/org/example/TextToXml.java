package org.example;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TextToXml {
	BufferedReader inLink;
	BufferedReader inLabel;
	BufferedReader inAbst;
	StreamResult out;
	TransformerHandler th;
	int id = 1, cLink = 0, cTitle = 0, cDesc = 0;
	boolean flagAbst = true;

	/**
	 * Main method to run the Converter and get the work done, and calculate the elapsed time
	 * 
	 * @param args
	 *            argument parameters
	 */
	public static void main(String args[]) {
		long startTime = System.nanoTime();
		new TextToXml().buildUp();
		long estimatedTime = System.nanoTime() - startTime;
		double seconds = (double) estimatedTime / 1000000000.0;

		System.out.println("(Elapsed Time : " + seconds + " Seconds)");
		System.out.println("****************************");
		System.out.println("* Data Preprocessing DONE! *");
		System.out.println("****************************");
	}

	/**
	 * Create the initial XML file. Compare two different sets from two
	 * different text files if equals, it creates the XML <Article> tag.
	 */
	public void buildUp() {
		try {
			// create & open files
			inLabel = new BufferedReader(new FileReader("src/main/java/org/example/wiki_dataset/wiki_labels.txt"));
			inLink = new BufferedReader(new FileReader("src/main/java/org/example/wiki_dataset/wiki_links.txt"));
			inAbst = new BufferedReader(new FileReader("src/main/java/org/example/wiki_dataset/wiki_abstracts.txt"));
			out = new StreamResult("src/main/java/org/example/wiki_dataset/wiki_data.xml");
			List<String> vals_abst = new ArrayList<String>();

			openXml();
			String strLink, strLabel, strAbst;

			// test if "title in link" equals to "title in title"
			while ((strLink = inLink.readLine()) != null && (strLabel = inLabel.readLine()) != null) {

				List<String> vals_links;
				List<String> vals_labels;
				vals_links = getValuesLinks(strLink);
				vals_labels = getValuesLabel(strLabel);

				// test if there is a description stream, read it
				if (flagAbst == true) {
					strAbst = inAbst.readLine();
					if (strAbst != null)
						vals_abst = getValuesAbst(strAbst);
					else
						continue;
					flagAbst = false;
				}

				String str = vals_links.get(0);
				String str1 = java.net.URLDecoder.decode(str.replaceAll("_", " "), "UTF-8");
				String str2 = vals_labels.get(2).replaceAll("^\"|\"$", "");
				String str3 = vals_labels.get(2).replaceAll("^\\\"\\|\\\"", "");

				// System.out.print(str1 + "\n");
				// System.out.print("-------------------\n");
				// System.out.print(str2 + "\n");
				// System.out.print("-------------------\n");

				// start the XML Article and children tags, if conditions are
				// met
				if (str1.equals(str2) || str1.equals(str3)) {
					ArtTagStart(id);

					String str4 = vals_labels.get(0);
					String str5 = vals_abst.get(0);

					if (str4.equals(str5)) {
						// System.out.print(str5 + "\n" + str4 + "\n");
						// System.out.print("**********\n");

						begin(vals_links, vals_labels, vals_abst, true);
						flagAbst = true;
					} else {
						begin(vals_links, vals_labels, vals_abst, false);
					}

					ArtTagEnd();
					System.out.print("Processing Xml Entry " + id + "...\n");
					id++;
				}
			}
			inLabel.close();
			inLink.close();
			closeXml();
			Summary();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read a streaming string and split the line according to a REGEX. Breaks
	 * down each line into "to-be-tag" parts.
	 * 
	 * @param str
	 *            Streaming string from the "Links" text file.
	 * @return List of Strings, cut according to a REGEX.
	 */
	private List<String> getValuesLinks(String str) {
		final Pattern TagRegex = Pattern.compile("[^\t]*\\w");
		final Matcher matcher = TagRegex.matcher(str);
		final List<String> values = new ArrayList<String>();

		while (matcher.find()) {
			values.add(matcher.group(0));
		}
		return values;
	}

	/**
	 * Read a streaming string and split the line according to a REGEX. Breaks
	 * down each line into "to-be-tag" parts.
	 * 
	 * @param str
	 *            Streaming string from the "Labels" text file.
	 * @return List of Strings, cut according to a REGEX.
	 */
	private List<String> getValuesLabel(String str) {
		final Pattern TagRegex = Pattern.compile("<(.+?)>|\"(.+)\"");
		final Matcher matcher = TagRegex.matcher(str);
		final List<String> values = new ArrayList<String>();

		while (matcher.find()) {
			values.add(matcher.group(0));
		}
		return values;
	}

	/**
	 * Read a streaming string and split the line according to a REGEX. Breaks
	 * down each line into "to-be-tag" parts.
	 * 
	 * @param str
	 *            Streaming string from the "Abstracts" text file.
	 * @return List of Strings, cut according to a REGEX.
	 */
	private List<String> getValuesAbst(String str) {
		final Pattern TagRegex = Pattern.compile("<(.+?)>|\"(.+)\"");
		final Matcher matcher = TagRegex.matcher(str);
		final List<String> values = new ArrayList<String>();

		while (matcher.find()) {
			values.add(matcher.group(0));
		}
		return values;
	}

	/**
	 * Open XML file. Start the XML declaration and the top tag.
	 * 
	 * @throws ParserConfigurationException
	 * @throws TransformerConfigurationException
	 * @throws SAXException
	 */
	private void openXml() throws ParserConfigurationException, TransformerConfigurationException, SAXException {
		SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
		th = tf.newTransformerHandler();

		// START XML OUTPUT
		Transformer serializer = th.getTransformer();
		serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");

		th.setResult(out);
		th.startDocument();
		th.startElement(null, null, "Wiki", null);
	}

	/**
	 * Start Article tag, and do the complete ID tag.
	 * 
	 * @param c
	 *            ID value for the current article being read.
	 * @throws SAXException
	 */
	private void ArtTagStart(int c) throws SAXException {
		th.startElement(null, null, "Article", null);
		processIdTag(c);
	}

	/**
	 * Construct children XML tags for each Article.
	 * 
	 * @param vals_link
	 *            Values from the "Links" file.
	 * @param vals_label
	 *            Values from the "label" file.
	 * @param vals_abst
	 *            Values from the "Abstracts" file.
	 * @param flag
	 *            If a description is available or not.
	 */
	private void begin(List<String> vals_link, List<String> vals_label, List<String> vals_abst, boolean flag) {
		try {
			// no description available (no match)
			if (flag == false) {
				for (int i = 0; i < vals_link.size(); i++) {
					if (i == 0) {
						processTagTitle(vals_link.get(i));
					} else if (i == 2) {
						processTagLink(vals_link.get(i));
					}
				}
				processTagDesc("No Description Available!");
				for (int i = 0; i < vals_label.size(); i++) {
					if (i == 0) {
						processTagEntity(vals_label.get(i));
					} else if (i == 1) {
						processTagSchema(vals_label.get(i));
					}
				}
			}

			// description available (match)
			else if (flag == true) {
				for (int i = 0; i < vals_link.size(); i++) {
					if (i == 0) {
						processTagTitle(vals_link.get(i));
					} else if (i == 2) {
						processTagLink(vals_link.get(i));
					}
				}
				processTagDesc(vals_abst.get(2));
				for (int i = 0; i < vals_label.size(); i++) {
					if (i == 0) {
						processTagEntity(vals_label.get(i));
					} else if (i == 1) {
						processTagSchema(vals_label.get(i));
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Construct the ID tag.
	 * 
	 * @param c
	 *            ID value
	 * @throws SAXException
	 */
	private void processIdTag(int c) throws SAXException {
		String str = Integer.toString(c);
		th.startElement(null, null, "ID", null);
		th.characters(str.toCharArray(), 0, str.length());
		th.endElement(null, null, "ID");
	}

	/**
	 * Construct Title tag.
	 * 
	 * @param s
	 *            Title value
	 * @throws SAXException
	 * @throws UnsupportedEncodingException
	 */
	private void processTagTitle(String s) throws SAXException, UnsupportedEncodingException {
		th.startElement(null, null, "Title", null);
		String res_decoded = java.net.URLDecoder.decode(s, "UTF-8");
		th.characters(res_decoded.toCharArray(), 0, res_decoded.length());
		th.endElement(null, null, "Title");
		cTitle++;
	}

	/**
	 * Construct Link tag.
	 * 
	 * @param s
	 *            Link value
	 * @throws SAXException
	 * @throws UnsupportedEncodingException
	 */
	private void processTagLink(String s) throws SAXException, UnsupportedEncodingException {
		th.startElement(null, null, "Link", null);
		String res_decoded = java.net.URLDecoder.decode(s, "UTF-8");
		th.characters(res_decoded.toCharArray(), 0, res_decoded.length());
		// th.characters(s.toCharArray(), 0, s.length());
		th.endElement(null, null, "Link");
		cLink++;
	}

	/**
	 * Construct Description tag.
	 * 
	 * @param s
	 *            Description value
	 * @throws SAXException
	 */
	private void processTagDesc(String s) throws SAXException {
		th.startElement(null, null, "Description", null);
		String temp = s.replace("<", "");
		String str = temp.replace(">", "");
		th.characters(str.toCharArray(), 0, str.length());
		th.endElement(null, null, "Description");
		cDesc++;
	}

	/**
	 * Construct Entity tag.
	 * 
	 * @param s
	 *            Entity value
	 * @throws SAXException
	 */
	private void processTagEntity(String s) throws SAXException {
		th.startElement(null, null, "Entity", null);
		String temp = s.replace("<", "");
		String str = temp.replace(">", "");
		th.characters(str.toCharArray(), 0, str.length());
		th.endElement(null, null, "Entity");
	}

	/**
	 * Construct Schema tag.
	 * 
	 * @param s
	 *            Schema value
	 * @throws SAXException
	 */
	private void processTagSchema(String s) throws SAXException {
		th.startElement(null, null, "Schema", null);
		String temp = s.replace("<", "");
		String str = temp.replace(">", "");
		th.characters(str.toCharArray(), 0, str.length());
		th.endElement(null, null, "Schema");
	}

	/**
	 * End Article tag.
	 * 
	 * @throws SAXException
	 */
	private void ArtTagEnd() throws SAXException {
		th.endElement(null, null, "Article");
	}

	/**
	 * Close the XML declaration and the top tag, and close XML file.
	 * 
	 * @throws SAXException
	 */
	private void closeXml() throws SAXException {
		th.endElement(null, null, "Wiki");
		th.endDocument();
	}

	/**
	 * Print a summary of constructed XML tags.
	 */
	private void Summary() {
		System.out.println("Articles: " + id + "\nTitles: " + cTitle + "\nLinks: " + cLink + "\nDescription: " + cDesc);
	}
}
