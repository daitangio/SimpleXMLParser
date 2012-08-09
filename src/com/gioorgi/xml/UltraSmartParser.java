package com.gioorgi.xml;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.*;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * UltraSmartParser semplifica la scrittura di parser a discesa di tipo SAX.
 * 
 * Per usarla creare una sottoclasse, dopodiche' servirsene
 * come mostrato  negli esempi.
 * 
 * 
 * 
 * Speed Introduction
 * 
 * Questa classe e' in grado di richiamare via reflection metodi del tipo
 * do_TAGESTERNO_TAGINTERNO(String contenutoTag) ecc
 * 
 * E' sufficente definire il metodo che vi serve, e se la superclasse lo trova, 
 * lo chiama.
 * 
 * Per esempio per leggere la stringa "bad" di <A><bad>Hi spencer!</bad></A>
 * Basta creare il metodo do_A_BAD La classe cerca metodi con tag maiuscoli.
 * 
 * I tag possono contenere underscore, la la segnatura divente ambigua:
 * 
 * do_A_GOOD_START e' chiamata sia per: <A_GOOD_START>Hi</A_GOOD_START> sia per
 * <A><GOOD><START>Hoi</START></GOOD></A> Per questa ragione questa classe di
 * utilita' va usata con giudizio!
 * 
 * NB: Name spaces are ignored <wp:pippo> is "pippo"
 * @author giorgi
 * @version  20120808 "Karmak"
 * FirstRev  25-mag-2006 16.04.50 BETA
 * 
 */
public class UltraSmartParser extends org.xml.sax.helpers.DefaultHandler
		implements ContentHandler {

	private boolean optimize=true;
	private String currentContent = null;
	private Attributes currentAttributes;
	private Stack<String> tagNameStack = new Stack<String>();
	private Map<String,Boolean> toIgnore=new HashMap<String,Boolean>();
	

	
	/** Custom method a subclass can overwrite */
	public void start(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {		
	}

	public void end(String uri, String localName, String qName)
			throws SAXException {

	}

	
	
	// **********************************************************************
	// **********************************************************************

	/**
	 * Default beahavior is to remember failed method call.
	 * Anyway you can have truoble with tag with have sometimes attributes and sometimes have not 
	 *  (which is weird by the way)
	 * so  you can disable it 
	 * @param optimize
	 */
	public void setOptimize(boolean optimize) {
		this.optimize = optimize;
	}

	public final void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		tagNameStack.push(localName);
		currentContent = "";
		currentAttributes=attributes;
		start(uri, localName, qName, attributes);
	}

	public final void endElement(String uri, String localName, String qName)
			throws SAXException {
		// I can build the Final StackString:
		Logger logger = getLog();
		String method = buildMethodName();
		boolean skip= optimize && toIgnore.containsKey(method);
		if(!skip){
			// lc.info("Searching dynaMethod for:"+method);
			try {
				Method m = getClass().getMethod(method,
						new Class[] { String.class });
				logger.debug("Invoking:" + method);
				try {
					m.invoke(this, new Object[] { currentContent });
				} catch (Exception e) {
					logger.error("Method Call FAILED:", e);
					throw new SAXException("Failed Dynamic call of:" + m, e);
				}
			} catch (SecurityException e) {
	
			} catch (NoSuchMethodException e) {
				if(currentAttributes.getLength()>0){							
					//lc.debug(currentAttributes.getQName(0)+"="+currentAttributes.getValue(0));
					Map<String, String> m= new TreeMap<String, String>();
					for(int i=0; i<currentAttributes.getLength();i++){
						m.put(currentAttributes.getQName(i),currentAttributes.getValue(i));					
					}
					try {
						Method m2 = getClass().getMethod(method,
								new Class[] { Map.class,String.class });
						logger.debug("Trying Harder! Attributes:"+currentAttributes.getLength()+ " Invoking "+m2.getName());					
						try {
							m2.invoke(this, new Object[] { m, currentContent });
						} catch (Exception e2) {
							logger.error("Method Call FAILED:", e2);
							throw new SAXException("Failed Dynamic call of:" + m, e2);
						}
					}catch(NoSuchMethodException nsme){
						toIgnore.put(method, true);
					}
	
				}else{
					/// Hummm not sure it is a good idea....
					toIgnore.put(method, true);
				}
				
			}
		}else{
			//logger.info("Ignored Methods..."+method+ " TOTAL:"+toIgnore.size());
		}
		// Uscita dal blocco:
		tagNameStack.pop();
		currentContent = "";
		end(uri, localName, qName);
	}

	private String buildMethodName() {
		Iterator<String> i = tagNameStack.iterator();
		String method = "do";
		while (i.hasNext()) {
			method += "_" + i.next().toUpperCase();
		}
		return method;
	}

	public final void characters(char[] ch, int start, int length)
			throws SAXException {
		// Logger log=getLog();
		currentContent += extract(ch, start, length);
	}

	protected String extract(char[] ch, int start, int length) {
		String s = "";
		for (int i = 0; i < length; i++) {
			char c = ch[start + i];
			s += c;
		}
		return s;
	}

	public Logger getLog() {
		return Logger.getLogger(getClass());
	}


}
