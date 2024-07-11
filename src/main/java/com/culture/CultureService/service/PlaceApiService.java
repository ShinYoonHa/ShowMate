package com.culture.CultureService.service;

import com.culture.CultureService.entity.PlaceEntity;
import com.culture.CultureService.repository.PlaceRepository;
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
public class PlaceApiService {
    @Autowired
    private PlaceRepository placeRepository;

    @Value("${serviceKey}")
    private String serviceKey;

    private String baseUrl = "http://www.kopis.or.kr/openApi/restful/prfplc?";

    //공연시설 목록을 api로 가져와 DB에 저장하는 메소드
    public int fetchAndSavePlaceData(String page, String rows) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String urlStr = baseUrl + "service=" + serviceKey
                + "&cpage=" + page + "&rows=" + rows;
        System.out.println("<++++++++공연시설목록 API를 통해 시설목록 가져옵니다. +++++++++>");

        URI uri = new URI(urlStr);
        String xmlData = restTemplate.getForObject(uri, String.class);
        List<PlaceEntity> placeList = placeApiParseXml(xmlData);

        int cnt=0;
        for(PlaceEntity placeEntity : placeList) {
            if (!placeRepository.existsByPlaceId(placeEntity.getPlaceId())) {
                placeRepository.save(placeEntity);
                updatePlaceDetail(placeEntity.getPlaceId()); //공연 상세정보 검색 시 String 타입 ShowId 필요.
                cnt++;
            }
        }
        return cnt;
    }

    //xml 형식으로 오는 데이터 중 필요한 값을 추출해 리스트로 반환
    private List<PlaceEntity> placeApiParseXml(String xmlData) throws Exception {
        List<PlaceEntity> placeList = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xmlData)));

        NodeList placeNodes = document.getElementsByTagName("db");

        for (int i = 0; i < placeNodes.getLength(); i++) {
            Node placeListNode = placeNodes.item(i);

            if (placeListNode.getNodeType() == Node.ELEMENT_NODE) {
                Element placeListElement = (Element) placeListNode;

                PlaceEntity placeEntity = new PlaceEntity();

                placeEntity.setPlaceId(getElementValue(placeListElement, "mt10id"));
                placeEntity.setName(getElementValue(placeListElement, "fcltynm"));
                placeEntity.setFeature(getElementValue(placeListElement, "fcltychartr"));

                placeList.add(placeEntity);
            }
        }
        return placeList;
    }

    //XML 내 특정 태그의 값을 반환하는 유틸리티 메소드
    private String getElementValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if(nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return null;
    }

    //공연시설 상세정보를 받아와 기존에 저장된 기본 정보에 추가해 갱신
    private void updatePlaceDetail(String placeId) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String detailUrlStr = "http://www.kopis.or.kr/openApi/restful/prfplc/" + placeId + "?service=" + serviceKey;
        URI detailUri = new URI(detailUrlStr);
        String detailXmlData = restTemplate.getForObject(detailUri, String.class);
        PlaceEntity detailEntity = placeDetailParseXml(detailXmlData);

        if (detailEntity != null) {
            PlaceEntity existingEntity = placeRepository.findByPlaceId(placeId);
            if (existingEntity != null) {
                existingEntity.setSeat(detailEntity.getSeat());
                existingEntity.setAddress(detailEntity.getAddress());
                existingEntity.setUrl(detailEntity.getUrl());
                existingEntity.setLongitude(detailEntity.getLongitude());
                existingEntity.setLatitude(detailEntity.getLatitude());

                placeRepository.save(existingEntity);
            }
        }
    }

    //공연시설 상세정보 api 에서 필요한 값을 추출하는 메소드.
    private PlaceEntity placeDetailParseXml(String xmlData) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xmlData)));

        NodeList placeDetailNodes = document.getElementsByTagName("db");
        if (placeDetailNodes.getLength() > 0) {
            Node placeDetailNode = placeDetailNodes.item(0);
            if (placeDetailNode.getNodeType() == Node.ELEMENT_NODE) {
                Element placeDetailElement = (Element) placeDetailNode;

                PlaceEntity placeEntity = new PlaceEntity();

                placeEntity.setSeat(getElementValue(placeDetailElement, "seatscale"));
                placeEntity.setUrl(getElementValue(placeDetailElement, "relateurl"));
                placeEntity.setAddress(getElementValue(placeDetailElement, "adres"));
                placeEntity.setLongitude(getElementValue(placeDetailElement, "lo"));
                placeEntity.setLatitude(getElementValue(placeDetailElement, "la"));

                return placeEntity;
            }
        }
        return null;
    }

}
