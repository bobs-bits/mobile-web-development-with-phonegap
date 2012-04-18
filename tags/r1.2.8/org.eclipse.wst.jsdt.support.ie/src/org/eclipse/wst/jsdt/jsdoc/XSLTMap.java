package org.eclipse.wst.jsdt.jsdoc;
/*
 * Created on Apr 24, 2006
 *
 * Bradley Childs (childsb@us.ibm.com)
 * Copyright IBM 2006.
 *
 * XSL Mapper.  Very simple, takes a few things as input and translates them
 * via the XSL file defined.
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XSLTMap {

    private File stylesheet;

    public XSLTMap(File stylesheet) {
        this.stylesheet = stylesheet;
    }

    public String applyMap(String datastring) throws MappingException {
        ByteArrayOutputStream xOutputStream = new ByteArrayOutputStream(1024); // Buffer size- trivial
        applyMap(datastring, xOutputStream);
        String XMLText = xOutputStream.toString();
        try{
            xOutputStream.close();
        }catch(IOException ex){
            // I ran into a door.  I'm so clumsy.
        }
        return XMLText;
    }

    public void applyMap(String dataString, OutputStream _os) throws MappingException {
        Document document;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();

            byte[] docBytes = dataString.getBytes();

            document = builder.parse(new ByteArrayInputStream(docBytes));
            // Use a Transformer for output
            TransformerFactory tFactory = TransformerFactory.newInstance();
            StreamSource stylesource = new StreamSource(stylesheet);
            Transformer transformer = tFactory.newTransformer(stylesource);
            DOMSource source = new DOMSource(document);
            /* Need to get an output stream to a string */
            StreamResult result = new StreamResult(_os);
            transformer.transform(source, result);

            /* close things */
            transformer.clearParameters();



        } catch (SAXException sxe) {
            throw new MappingException("Transformer c error:\n" + sxe.getMessage());
        } catch (TransformerConfigurationException tce) {
            // Error generated by the parser
            throw new MappingException("Transformer c error:\n" + tce.getMessage());
        } catch (TransformerException te) {
            // Error generated by the parser
            throw new MappingException("Transformer error:\n" + te.getMessage());
        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            throw new MappingException("Transformer factory error:\n" + pce.getMessage());
        } catch (IOException ioe) {
            // I/O error (probably corrupt DTD)
            throw new MappingException("I/O Exception error (probably bad/missing XSL map) :\n" + ioe.getMessage());
        } catch (Exception ex){
            throw new MappingException("General exceptioned occured while mapping: " + ex.getMessage());
        }


    }

//    public static void main(String argv[]) {
//        if (argv.length != 2) {
//            System.err.println("Usage: java Stylizer stylesheet xmlfile");
//            System.exit(1);
//        }
//        File stylesheet = new File(argv[0]);
//        File datafile = new File(argv[1]);
//        XSLTMap t = new XSLTMap(stylesheet);
//        String dataString = "";
//        //System.out.println(t.applyMap(dataString));
//
//    }


}