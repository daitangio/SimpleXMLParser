SimpleXMLParser
===============

The easiest way of parsing xml in Java, avoiding complex XPath learning...
Take a look to http://gioorgi.com/2012/simplexmlparser/ ‎
for a tutorial introduction


So What?
=========

SimpleXMLParser simplify implementation of SAX-based XML Parser
Take a look to the example package for a fast start. 
 

Speed Introduction
==================
To start, subclass SimpleXMLParser.
Then if you want to read the code inside

    <my><dear><tag>I want this string</tag></dear></my>
 
implement a simple method called
 public void do_MY_DEAR_TAG(String tagContent)
 {
  ....
 }


In the method, the tag are always in uppercase.
The class is also able to parse tag attributes (see WordpressExportReader for an example)
 
The parser has an "ignore list" to skip tag sequence he thinks will throw a no such method error,
improving performance.
Because this feature is still experimental, you can turn it off with 
 setOptimize(false)

 
