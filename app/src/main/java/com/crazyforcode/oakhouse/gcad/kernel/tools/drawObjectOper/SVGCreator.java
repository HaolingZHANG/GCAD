package com.crazyforcode.oakhouse.gcad.kernel.tools.drawObjectOper;

import android.util.Log;
import android.util.Xml;

import com.crazyforcode.oakhouse.gcad.draw.assist.AlgoPoint;
import com.crazyforcode.oakhouse.gcad.draw.legendPaint.DrawObject;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;

/**
 * Created by Master_Jedi on 2016/04/23.
 */
public class SVGCreator {
    /**svg file head*/
    final static String xmlns = "http://www.w3.org/2000/svg",
                        xmlnslink = "http://www.w3.org/1999/xlink",
                        DOCTYPE = "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"" +
                                " \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">";

    final static String id105 = "_x31_06.2_Earth_bank__x5F_thicker_x5F__4";

    final static String FILL_NONE = "none";

    static float boundRight = 0,
                    boundBottom = 0;

    public static void OutSVGFile(HashMap<Integer, ArrayList<DrawObject>> drawObjects, String filepath) {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            xmlSerializer.setOutput(writer);

            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);//"<?xml version=\"1.0\" encoding=\"utf-8\"?>"
            xmlSerializer.flush();
            writer.append(DOCTYPE);
            xmlSerializer.startTag("", "svg");
            xmlSerializer.attribute("", "version", "1.1");
            xmlSerializer.attribute("", "id", id105);
            xmlSerializer.attribute("", "xmlns", xmlns);
            xmlSerializer.attribute("", "xmlns:xlink", xmlnslink);
            xmlSerializer.attribute("", "x", "0px");//都是0px
            xmlSerializer.attribute("", "y", "0px");//都是0px
            xmlSerializer.attribute("", "viewBox", "0 0 "+boundRight+" "+boundBottom);//bounds (maybe
            xmlSerializer.attribute("", "enable-background", "new 0 0 "+boundRight+" "+boundBottom);
            xmlSerializer.attribute("", "xml:space", "preserve");//?

            xmlSerializer.startTag("", "polyline");
            xmlSerializer.attribute("", "fill", FILL_NONE);
            xmlSerializer.attribute("", "stroke", "#D47A00");//TODO color class
            xmlSerializer.attribute("", "stroke-width", "1.05");//TODO DrawObject attr class
            xmlSerializer.attribute("", "stroke-linejoin", "bevel");//TODO seem like some thing should write live
            xmlSerializer.attribute("", "stroke-miterlimit", "10");//?
            {
                /*for (AlgoPoint point :drawObject.iterator()) {

                }*/
                StringBuffer sb = new StringBuffer();
                sb.append(253+",");//x
                sb.append(412.9+" ");//y
                sb.append(340.8+",");//x
                sb.append(396.8+" ");//y
                sb.append(343+",");//x
                sb.append(445.5+" ");//y
                xmlSerializer.attribute("", "points", sb.toString());
            }
            xmlSerializer.endTag("", "polyline");

            xmlSerializer.endTag("", "svg");
            xmlSerializer.endDocument();
            xmlSerializer.flush();
        } catch (IOException e) {
            Log.i("-=-=-=-xml or sw error", xmlSerializer.toString());
            e.printStackTrace();
        }

        File file = new File(filepath + "/Data/project.svg");
        try {
            file.createNewFile();

            FileOutputStream out = new FileOutputStream(file);
            out.write(writer.toString().getBytes());
            Log.i("writer", "++++++++++++++++++++++");
            Log.i("writer", writer.toString());
            Log.i("writer length", writer.toString().getBytes().length+"");
            Log.i("writer", "++++++++++++++++++++++");
            out.close();
        } catch (IOException e) {
            Log.i("-=-=-=-outStr error", e.toString());
            e.printStackTrace();
        }
    }

    public static String CreateXMLString(DrawObject db) {

        return "";
    }

    /**DOM type to parse svg(xml)*/
    public static void parseSVGFile(File svgFile, HashMap<Integer, ArrayList<DrawObject>> drawobjs) {
        /*TransformerFactory saxFactory = SAXTransformerFactory.newInstance();
        try {
            Transformer transformer = saxFactory.newTransformer();
            transformer.
        } catch (TransformerConfigurationException e) {
            Log.i("SVGCreator", e.toString());
            e.printStackTrace();
        }*/

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            //此句后文件进入内存
            Document dom = builder.parse(svgFile);//Document in org.w3c

            Element root = dom.getDocumentElement();//Element in org.w3c
            NodeList nodes = root.getElementsByTagName("g");//svg group

            DrawObject painter = null;
            //root.getNodeType() == Node.ELEMENT_NODE;

            if (nodes.getLength() > 0){
                int length = nodes.getLength();
                for (int i = 0; i < length; i++) {
                    drawobjs.get(painter.getSign()).add(parseNode((Element) nodes.item(i)));
                }
            }else /**no g tag, mean there only one drawobj*/{
                painter = parseNode(root);

                if (drawobjs.get(painter.getSign()) == null)
                    drawobjs.put(painter.getSign(), new ArrayList<DrawObject>());

                drawobjs.get(painter.getSign()).add(painter);
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

/*    private static DrawObject parseNode(NodeList nodes) throws ParserConfigurationException{
        int length = nodes.getLength();
        for (int i = 0; i < length; i++) {
            parseNode((Element) nodes.item(i));
        }
    }*/

    private static DrawObject parseNode(Element node) throws ParserConfigurationException{
        /**tag name*/
        final String POLY_LINE = "polyline",
                POLYGON = "polygon",
                PATH = "path",
                LINE = "line";

        DrawObject painter = null;
        /**get root id*/
        String id = node.getAttribute("id");

        /**get trunk line inside*/
        int drawType = DrawObjects.DRAW_TYPE_STRAIGHT;
        String trunkType = POLY_LINE;
        if (node.getElementsByTagName("polyline").getLength() > 0) {//TODO 如何确定主干线段有待完善
            node = (Element) node.getElementsByTagName("polyline").item(0);
        }else if (node.getElementsByTagName("polygon").getLength() > 0) {
            node = (Element) node.getElementsByTagName("polygon").item(0);
            trunkType = POLYGON;
        }else if ((node.getElementsByTagName("path").getLength() == 1
                && node.getElementsByTagName("line").getLength() > 0)
                || (node.getElementsByTagName("path").getLength() > 1
                && node.getElementsByTagName("line").getLength() < 1)) {
            node = (Element) node.getElementsByTagName("path").item(0);
            drawType = DrawObjects.DRAW_TYPE_CURL;
            trunkType = PATH;
        }else if ((node.getElementsByTagName("line").getLength() == 1 &&
                node.getElementsByTagName("path").getLength() > 0)||
                (node.getElementsByTagName("line").getLength() > 1 &&
                node.getElementsByTagName("path").getLength() < 1)) {
            node = (Element) node.getElementsByTagName("line").item(0);
            trunkType = LINE;
        }else if (node.getElementsByTagName("g").getLength() > 0) {
            return parseNode((Element) node.getElementsByTagName("g").item(0));
        }else
            throw new ParserConfigurationException("svg tag lose");

        painter = DrawObjectFactory.getDrawObjectById(id, drawType);
        if (trunkType == PATH) {
            String pointString = ((Element) node.getElementsByTagName(trunkType).item(0))
                    .getAttribute("d");
            //TODO parse path tag
        }else {
            String pointString = ((Element) node.getElementsByTagName(trunkType).item(0))
                    .getAttribute("points");
            String[] pointPairs = pointString.split("[, ]");//TODO 正则
            for (String pointCo : pointPairs) {
                //FOr test
                Log.i("every point coodinate", Float.parseFloat(pointCo) + "");
            }
            int j = 0, pairLength = pointPairs.length;
            while (j < pairLength) {
                painter.addPoint(new AlgoPoint(Float.parseFloat(pointPairs[j++]),
                        Float.parseFloat(pointPairs[j++])));
            }
        }

        return painter;
    }

    public static void parseSVGStream(InputStream svgFile, HashMap<Integer, ArrayList<DrawObject>> drawobjs) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();

            reader.setContentHandler(new SVGDefaultHandler());
            //TODO reader.parse(svgStream);
            parser.parse(svgFile, new SVGDefaultHandler());
        } catch (ParserConfigurationException e) {
            Log.i("SVGCreator class", e.toString());
            e.printStackTrace();
        } catch (SAXException e) {
            Log.i("SVGCreator class", e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("SVGCreator class", e.toString());
            e.printStackTrace();
        }
    }

    private static class SVGDefaultHandler extends DefaultHandler {
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            super.startElement(uri, localName, qName, attributes);
        }

        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            super.endElement(uri, localName, qName);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
        }
    }
}
