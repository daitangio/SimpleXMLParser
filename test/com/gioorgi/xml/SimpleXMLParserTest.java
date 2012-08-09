package com.gioorgi.xml;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.gioorgi.xml.examples.UltraSmartExample;

@SuppressWarnings("unused")
public class SimpleXMLParserTest {

	private boolean success=false;
	private String expectedOutput="";
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		success=false;
		expectedOutput="";
	}

	@After
	public void tearDown() throws Exception {
		success=false;
		expectedOutput="";
	}

	@Test
	public void testSimpleParse() {		
		parse("<A_GOOD_START>Hi inside tag A_GOOD_START</A_GOOD_START>",
				new SimpleXMLParser(){
					public void do_A_GOOD_START(String content){	
						assertEquals("Hi inside tag A_GOOD_START",content);
						success=true; // Required to be sure we are here
					}
				});						
	}
	
	@Test 
	public void testXmlNamespaceIgnored(){
		parse("<A xmlns:my='http://gioorgi.com'> <my:GOOD_START>Example of ignored my namespace</my:GOOD_START></A>",
				new SimpleXMLParser(){
					public void do_A_GOOD_START(String content){
						success=true;
						expectedOutput=content;
					}
				}
		);
		assertEquals("Example of ignored my namespace",expectedOutput);
	}
	
	@Test
	public void testAttributePassingButNotReading(){
		parse("<A_GOOD_START ciao='karmak'>Hi inside tag A_GOOD_START</A_GOOD_START>",
				new SimpleXMLParser(){
					public void do_A_GOOD_START(String content){	
						assertEquals("Hi inside tag A_GOOD_START",content);
						success=true; // Required to be sure we are here
					}
				});	
	}
	
	
	@Test
	public void testAttributePassingAndtReading(){
		// Note: without xml version spec(first line)+ you got trubles! */
		parse("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+				
				"<A><GOOD_START ciao=\"karmak\">Hi inside tag A_GOOD_START</GOOD_START></A>",
				new SimpleXMLParser(){
					public void do_A_GOOD_START(Map<String,String> attribs, String content){
						System.out.println("ATTRIBS:"+attribs);
						assertEquals("Hi inside tag A_GOOD_START",content);						
						success=attribs.get("ciao").equals("karmak");
						if(!success){
							getLog().info("Attributes are wrong?!...");
						}
					}
				});	
	}


	
	private  void parse(String str, SimpleXMLParser parser)  {
		try{
			//System.err.println(str);
			XMLReader sax2Parser = XMLReaderFactory.createXMLReader();
			sax2Parser.setContentHandler(parser);
			ByteArrayInputStream is = new ByteArrayInputStream(str.getBytes());
			InputSource s = new InputSource(is);
			sax2Parser.parse(s);
			if(!success){
				System.out.println("Failed. Input:"+str);
			}
			assertTrue(success);
		} catch (SAXException e) {
			throw new RuntimeException("test failed.",e);
		} catch (IOException e) {
			throw new RuntimeException("test failed.",e);
		}finally {
			
		}
	}
}
