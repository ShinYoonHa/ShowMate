package com.culture.CultureService.service;

import com.culture.CultureService.entity.AwardEntity;
import com.culture.CultureService.repository.AwardRepository;
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
public class AwardApiService {

    @Autowired
    private AwardRepository awardRepository;

    @Value("${awardServiceKey}")
    private String awardServiceKey;

    private final String baseUrl = "http://kopis.or.kr/openApi/restful/prfawad?";


    public void fetchAndSaveAwardData(String stDate, String edDate, String page, String rows) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String urlStr = baseUrl + "service=" + awardServiceKey
                + "&stdate=" + stDate + "&eddate=" + edDate
                + "&cpage=" + page + "&rows=" + rows;
        System.out.println("<---------수상목록 API를 통해 수상목록 가져옵니다. --------->");

        URI uri = new URI(urlStr);
        String xmlData = restTemplate.getForObject(uri, String.class);
        List<AwardEntity> awardList = awardApiparseXml(xmlData);

        for (AwardEntity awardEntity : awardList) {
            // 데이터가 존재하지 않을 때만 저장
            if (!awardRepository.existsByAwardId(awardEntity.getAwardId())) {
                awardRepository.save(awardEntity);
                System.out.println("Saved award: " + awardEntity);
                updateAwardDetail(awardEntity.getAwardId());
            }
        }

    }

    private List<AwardEntity> awardApiparseXml(String xmlData) throws Exception {
        List<AwardEntity> awardList = new ArrayList<>();

        // XML 파서 팩토리와 빌더를 생성
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        // XML 데이터를 파싱
        Document document = builder.parse(new InputSource(new StringReader(xmlData)));

        // "db" 태그를 가진 모든 노드
        NodeList awardListNodes = document.getElementsByTagName("db");
        System.out.println("awardList 개수 :" + awardListNodes.getLength());

        // 각 노드를 순회하며 AwardEntity 객체를 생성
        for (int i = 0; i < awardListNodes.getLength(); i++) {
            Node awardListNode = awardListNodes.item(i);

            // 노드가 엘리먼트 타입인지 확인
            if (awardListNode.getNodeType() == Node.ELEMENT_NODE) {
                Element awardListElement = (Element) awardListNode;

                AwardEntity awardEntity = new AwardEntity();
                // 각 필드 값을 설정
                awardEntity.setAwardId(getElementValue(awardListElement, "mt20id"));
                awardEntity.setTitle(getElementValue(awardListElement, "prfnm"));
                awardEntity.setStDate(getElementValue(awardListElement, "prfpdfrom"));
                awardEntity.setEdDate(getElementValue(awardListElement, "prfpdto"));
                awardEntity.setPlaceName(getElementValue(awardListElement, "fcltynm"));
                awardEntity.setPosterUrl(getElementValue(awardListElement, "poster"));
                awardEntity.setGenre(getElementValue(awardListElement, "genrenm"));
                awardEntity.setAwardName(getElementValue(awardListElement, "awards"));

                // 리스트에 추가
                awardList.add(awardEntity);
            }
        }
        return awardList;
    }

    private String getElementValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return null;
    }


    private void updateAwardDetail(String awardId) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String detailUrlStr = "http://kopis.or.kr/openApi/restful/prfawad?" + awardId + "?service" + awardServiceKey;
        URI detailUrl = new URI(detailUrlStr);
        String detailXmlData = restTemplate.getForObject(detailUrl, String.class);
        AwardEntity detailEntity = awardDetailParseXml(detailXmlData);

        if (detailEntity != null) {
            AwardEntity existingEntity = awardRepository.findByAwardId(awardId);
            if (existingEntity != null) {
                existingEntity.setCast(detailEntity.getCast());
                existingEntity.setStaff(detailEntity.getStaff());
                existingEntity.setRuntime(detailEntity.getRuntime());
                existingEntity.setAge(detailEntity.getAge());
                existingEntity.setProducer(detailEntity.getProducer());
                existingEntity.setTicketPrice(detailEntity.getTicketPrice());
                existingEntity.setState(detailEntity.getState());
                existingEntity.setStoryUrl(detailEntity.getStoryUrl());
                existingEntity.setPlaceId(detailEntity.getPlaceId());
                existingEntity.setTime(detailEntity.getTime());

                awardRepository.save(existingEntity);
            }
        }
    }

    private AwardEntity awardDetailParseXml(String xmlData) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xmlData)));

        NodeList awardDetailNodes = document.getElementsByTagName("db");
        if (awardDetailNodes.getLength() > 0) {
            Node awardDetailNode = awardDetailNodes.item(0);
            if (awardDetailNode.getNodeType() == Node.ELEMENT_NODE) {
                Element awardDetailElement = (Element) awardDetailNode;

                AwardEntity awardEntity = new AwardEntity();

                awardEntity.setCast(getElementValue(awardDetailElement, "prfcast"));
                awardEntity.setStaff(getElementValue(awardDetailElement, "prfcrew"));
                awardEntity.setRuntime(getElementValue(awardDetailElement, "prfruntime"));
                awardEntity.setAge(getElementValue(awardDetailElement, "prfage"));
                awardEntity.setProducer(getElementValue(awardDetailElement, "entrpsnm"));
                awardEntity.setTicketPrice(getElementValue(awardDetailElement, "pcseguidance"));
                awardEntity.setState(getElementValue(awardDetailElement, "prfstate"));
                awardEntity.setStoryUrl(getElementValue(awardDetailElement, "styurl"));
                awardEntity.setPlaceId(getElementValue(awardDetailElement, "mt10id"));
                awardEntity.setTime(getElementValue(awardDetailElement, "dtguidance"));

                return awardEntity;
            }
        }
        return null;
    }
}
