package com.culture.CultureService.service;

import com.culture.CultureService.entity.ShowEntity;
import com.culture.CultureService.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShowApiService {

    @Autowired
    private ShowRepository showRepository;

    @Value("${serviceKey}")
    private String serviceKey; //보안을 위해 application.properties에 서비스키 정의.

    private final String baseUrl = "http://www.kopis.or.kr/openApi/restful/pblprfr?";

    public void fetchAndSaveShowData(String stDate, String edDate, String page, String rows) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String urlStr = baseUrl + "service=" + serviceKey
                + "&stdate=" + stDate + "&eddate=" + edDate
                + "&cpage=" + page + "&rows=" + rows;
        System.out.println("@@@@@@@@@@urlStr = " + urlStr);

        URI uri = new URI(urlStr);
        String xmlData = restTemplate.getForObject(uri, String.class);
        List<ShowEntity> showList = showApiParseXml(xmlData);

        //db에 저장
        for(ShowEntity showEntity : showList) {
            showRepository.save(showEntity);
        }
    }

    
    public List<ShowEntity> showApiParseXml(String xmlData) throws Exception {
        List<ShowEntity> showList = new ArrayList<>();

        //Open Api에서 추출한 xml 데이터에서 원하는 정보 추출
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xmlData)));

        NodeList showListNodes = document.getElementsByTagName("db");
        System.out.println("showList 개수 :" + showListNodes.getLength());

        for(int i=0; i<showListNodes.getLength(); i++) {
            Node showListNode = showListNodes.item(i);

            if(showListNode.getNodeType() == Node.ELEMENT_NODE) {
                Element showListElement = (Element) showListNode;

                ShowEntity showEntity = new ShowEntity();

                showEntity.setShowId(getElementValue(showListElement, "mt20id"));
                showEntity.setTitle(getElementValue(showListElement, "prfnm"));
                showEntity.setStDate(getElementValue(showListElement, "prfpdfrom"));
                showEntity.setEdDate(getElementValue(showListElement, "prfpdto"));
                showEntity.setPlaceName(getElementValue(showListElement, "fcltynm"));
                showEntity.setGenre(getElementValue(showListElement, "genrenm"));
                showEntity.setPosterUrl(getElementValue(showListElement, "poster"));

                showList.add(showEntity);
            }
        }
        return showList;
    }

    private String getElementValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if(nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return null;
    }
}
