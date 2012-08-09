package com.gioorgi.xml.examples;

/*
 * Created on 25-mag-2006 by [GG]
 */

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.gioorgi.xml.UltraSmartParser;

public class UltraSmartExample extends UltraSmartParser {

	/**
	 * @param args
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void main(String[] args) throws SAXException, IOException {
		// System.setProperty("org.xml.sax.driver","org.apache.crimson.parser.XMLReaderImpl");
		BasicConfigurator.configure();
		String str = "<A><bad>Hi spencer!</bad><GOOD><DAY>is</DAY><DAY>is2</DAY></GOOD></A>";

		test(str);
		// Test 2
		// xmlns:wp="http://wordpress.org/export/1.2/"
		test("<A_GOOD_START>Hi inside tag A_GOOD_START </A_GOOD_START>");
		test("<A xmlns:my='http://gioorgi.com'> <my:GOOD_START>Example of ignored my namespace</my:GOOD_START></A>");
		test("<A><GOOD><START>Hi inside a simple &amp; nested START TAG </START></GOOD></A>");
		test("<_>True Ugly do__ interceptor </_>");
	}

	public void do__(String ugly) {
		getLog().info("do__:" + ugly);
	}

	public void do_A_GOOD_START(String s) {
		getLog().info("do_A_GOOD_START:" + s);
	}

	private static void test(String str) throws SAXException, IOException {
		XMLReader sax2Parser = XMLReaderFactory.createXMLReader();
		UltraSmartExample singleSignOnParser = new UltraSmartExample();
		sax2Parser.setContentHandler(singleSignOnParser);
		ByteArrayInputStream is = new ByteArrayInputStream(str.getBytes());
		InputSource s = new InputSource(is);
		sax2Parser.parse(s);
	}

	public void do_A(String s) {
		getLog().info("A Tag:" + s + ":");
	}

	public void do_A_GOOD_DAY(String content) {
		getLog().info("TAG DAY:" + content);
	}

	public void do_A_BAD(String content) {
		getLog().info("From BAD:" + content);
	}
}
