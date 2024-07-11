package com.culture.CultureService.service;

import com.culture.CultureService.entity.GenreStatisticEntity;
import com.culture.CultureService.repository.GenreStatisticRepository;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class GenreStatisticApiService {

    private final GenreStatisticRepository genreStatisticRepository;

    @Value("${genreServiceKey}")
    private String genreServiceKey;

    private final String baseUrl = "http://kopis.or.kr/openApi/restful/boxStatsCate";

    public void fetchAndSaveGenreStatistics(String stDate, String edDate) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String urlStr = baseUrl + "?service=" + genreServiceKey + "&stdate=" + stDate + "&eddate=" + edDate;
        System.out.println("@@@@@@@@@urlStr = " + urlStr);

        URI uri = new URI(urlStr);
        String xmlData = restTemplate.getForObject(uri, String.class);
        System.out.println("Received XML Data: " + xmlData); // API 응답 출력

        List<GenreStatisticEntity> genreStatisticList = parseXml(xmlData);
        System.out.println("genreStatisticList 개수 :" + genreStatisticList.size());

        for (GenreStatisticEntity entity : genreStatisticList) {
            System.out.println("Saving entity: " + entity);
            genreStatisticRepository.save(entity);
        }
    }

    public List<GenreStatisticEntity> parseXml(String xmlData) throws Exception {
        List<GenreStatisticEntity> genreStatisticList = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xmlData)));

        NodeList nodeList = document.getElementsByTagName("boxStatsof");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                GenreStatisticEntity entity = new GenreStatisticEntity();
                entity.setGenre(getElementValue(element, "catenm"));
                entity.setPerformanceCount(Integer.parseInt(getElementValue(element, "prfcnt")));
                entity.setShowCount(Integer.parseInt(getElementValue(element, "prfdtcnt")));
                entity.setReservationCount(Integer.parseInt(getElementValue(element, "ntssnmrssm")));
                entity.setCancellationCount(Integer.parseInt(getElementValue(element, "cancelnmrssm")));
                entity.setTotalTicketSales(Integer.parseInt(getElementValue(element, "totnmrssm")));
                entity.setTotalTicketRevenue(Double.parseDouble(getElementValue(element, "ntssamountsm")));

                genreStatisticList.add(entity);
            }
        }
        return genreStatisticList;
    }

    private String getElementValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return null;
    }

}
