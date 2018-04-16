package com.example.artem.vestinewsreader;

import android.content.Context;
import com.example.artem.vestinewsreader.database.AppDataBase;
import com.example.artem.vestinewsreader.database.Article;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Downloader {

    private Context context;
    private AppDataBase appDataBase;
    private URL url;

    public Downloader(Context context){
        this.context = context;
        appDataBase = AppDataBase.getDatabase(context);
    }

    public void ProcessXml(String urlOfResource) {
        Document data = GetData(urlOfResource);
        if (data != null) {
            org.w3c.dom.Element root = data.getDocumentElement();
            Node channel = root.getChildNodes().item(1);
            NodeList items = channel.getChildNodes();
            for (int i = 0; i < items.getLength(); i++) {
                Node currentChild = items.item(i);
                if (currentChild.getNodeName().equalsIgnoreCase("item")) {
                    Article article = new Article();
                    NodeList itemChilds = currentChild.getChildNodes();
                    for (int j = 0; j < itemChilds.getLength(); j++) {
                        Node current = itemChilds.item(j);
                        if (current.getNodeName().equalsIgnoreCase("title")) {
                            article.setTitle(current.getTextContent());
                        } else if (current.getNodeName().equalsIgnoreCase("pubDate")) {
                            article.setDate(current.getTextContent());
                        } else if (current.getNodeName().equalsIgnoreCase("enclosure")) {
                            String body = nodeToString(current);
                            int urlStart = body.indexOf("http");
                            int urlFinish = body.indexOf("type");
                            String urlImage = body.substring(urlStart, urlFinish-2);

                            if(urlImage.contains("jpg")) {
                                article.setPicture(urlImage);
                            }

                        } else if(current.getNodeName().equalsIgnoreCase("yandex:full-text")){
                            article.setText(current.getTextContent());
                        }
                    }
                    appDataBase.articleDao().addArticle(article);
                }
            }
        }
    }

    private String nodeToString(Node node) {
        StringWriter sw = new StringWriter();
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException te) {
            System.out.println("nodeToString Transformer Exception");
        }
        return sw.toString();
    }

    private Document GetData(String urlOfResource){

        OkHttpClient client = new OkHttpClient();

        try{
            url = new URL(urlOfResource);
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            InputStream is = response.body().byteStream();
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            Document xmlDoc = documentBuilder.parse(is);
            return xmlDoc;

        }catch (IOException |ParserConfigurationException |SAXException e){
            e.printStackTrace();
            return null;
        }
    }
}
