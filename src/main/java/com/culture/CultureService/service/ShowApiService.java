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
    private String serviceKey;

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

        for (ShowEntity showEntity : showList) {
            // 데이터가 존재하지 않을 때만 저장
            if (!showRepository.existsByShowId(showEntity.getShowId())) {
                showRepository.save(showEntity);
                updateShowDetail(showEntity.getShowId());
            }
        }
    }

    public List<ShowEntity> showApiParseXml(String xmlData) throws Exception {
        List<ShowEntity> showList = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xmlData)));

        NodeList showListNodes = document.getElementsByTagName("db");
        System.out.println("showList 개수 :" + showListNodes.getLength());

        for (int i = 0; i < showListNodes.getLength(); i++) {
            Node showListNode = showListNodes.item(i);

            if (showListNode.getNodeType() == Node.ELEMENT_NODE) {
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
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return null;
    }

    private void updateShowDetail(String showId) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String detailUrlStr = "http://www.kopis.or.kr/openApi/restful/pblprfr/" + showId + "?service=" + serviceKey;
        URI detailUri = new URI(detailUrlStr);
        String detailXmlData = restTemplate.getForObject(detailUri, String.class);
        ShowEntity detailEntity = showDetailParseXml(detailXmlData);

        if (detailEntity != null) {
            ShowEntity existingEntity = showRepository.findByShowId(showId);
            if (existingEntity != null) {
                existingEntity.setCast(detailEntity.getCast());
                existingEntity.setStaff(detailEntity.getStaff());
                existingEntity.setRuntime(detailEntity.getRuntime());
                existingEntity.setAge(detailEntity.getAge());
                existingEntity.setProducer(detailEntity.getProducer());
                existingEntity.setTicketPrice(detailEntity.getTicketPrice());
                existingEntity.setSummary(detailEntity.getSummary());
                existingEntity.setArea(detailEntity.getArea());
                existingEntity.setChild(detailEntity.getChild());
                existingEntity.setState(detailEntity.getState());
                existingEntity.setStoryUrl(detailEntity.getStoryUrl());
                existingEntity.setPlaceId(detailEntity.getPlaceId());
                existingEntity.setTime(detailEntity.getTime());

                showRepository.save(existingEntity);
            }
        }
    }

    private ShowEntity showDetailParseXml(String xmlData) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xmlData)));

        NodeList showDetailNodes = document.getElementsByTagName("db");
        if (showDetailNodes.getLength() > 0) {
            Node showDetailNode = showDetailNodes.item(0);
            if (showDetailNode.getNodeType() == Node.ELEMENT_NODE) {
                Element showDetailElement = (Element) showDetailNode;

                ShowEntity showEntity = new ShowEntity();

                showEntity.setCast(getElementValue(showDetailElement, "prfcast"));
                showEntity.setStaff(getElementValue(showDetailElement, "prfcrew"));
                showEntity.setRuntime(getElementValue(showDetailElement, "prfruntime"));
                showEntity.setAge(getElementValue(showDetailElement, "prfage"));
                showEntity.setProducer(getElementValue(showDetailElement, "entrpsnm"));
                showEntity.setTicketPrice(getElementValue(showDetailElement, "pcseguidance"));
                showEntity.setSummary(getElementValue(showDetailElement, "sty"));
                showEntity.setArea(getElementValue(showDetailElement, "area"));
                showEntity.setChild(getElementValue(showDetailElement, "dtguidance"));
                showEntity.setState(getElementValue(showDetailElement, "prfstate"));
                showEntity.setStoryUrl(getElementValue(showDetailElement, "styurl"));
                showEntity.setPlaceId(getElementValue(showDetailElement, "mt10id"));
                showEntity.setTime(getElementValue(showDetailElement, "dtguidance"));

                return showEntity;
            }
        }
        return null;
    }
}
