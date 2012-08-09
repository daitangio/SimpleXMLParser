package com.gioorgi.xml.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.gioorgi.xml.SimpleXMLParser;

public class WordpressExportReader extends SimpleXMLParser {

	/**
	 * @param args
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void main(String[] args) throws SAXException, IOException {
		BasicConfigurator.configure();
		
		XMLReader sax2Parser = XMLReaderFactory.createXMLReader();
		SimpleXMLParser parser = new WordpressExportReader();
		parser.getLog().setLevel(Level.INFO);
		sax2Parser.setContentHandler(parser);
		File f = new File("c:/jjsoft/gioorgicom.wordpress.2012-08-07.xml");
		FileInputStream is = new FileInputStream(f);
		InputSource s = new InputSource(is);
		sax2Parser.parse(s);
		parser.getLog().info("DONE");
	}

	private String currentTitle,pid;

	public void do_RSS_CHANNEL_ITEM_TITLE(String title) {
		this.currentTitle = title;
	}

	
	// Catch <wp:post_id>1551</wp:post_id>
	public void do_RSS_CHANNEL_ITEM_POST_ID(String idz){
		pid=idz;
	}
	
	// Catch stuff like
	// <category domain="category"
	// nicename="software"><![CDATA[Software]]></category>
	// <category domain="series" nicename="version-control"><![CDATA[Version
	// Control]]></category>
	public void do_RSS_CHANNEL_ITEM_CATEGORY(Map catAttribs, String cdata) {
		if(catAttribs.get("domain").equals("series")){
			getLog().info(" POST:"+ pid+":"+ currentTitle+":" + cdata+ ":"+catAttribs.get("nicename"));
		}
	}

}
