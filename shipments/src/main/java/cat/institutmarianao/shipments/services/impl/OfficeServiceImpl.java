package cat.institutmarianao.shipments.services.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import cat.institutmarianao.shipments.model.Office;
import cat.institutmarianao.shipments.services.OfficeService;

@Service
public class OfficeServiceImpl implements OfficeService {

    @Value("${webService.host}")
    private String webServiceHost;

    @Value("${webService.port}")
    private String webServicePort;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Office getById(Long id) {
        final String baseUri = webServiceHost + ":" + webServicePort + "/offices/get/by/id/{id}";

        // Ajusta este código según la estructura de respuesta del servicio web
        Office office = restTemplate.getForObject(baseUri, Office.class, id);
        return office;
    }

    @Override
    public List<Office> getAllOffices() {
        final String uri = webServiceHost + ":" + webServicePort + "/offices/find/all";

        // Ajusta este código según la estructura de respuesta del servicio web
        Office[] officesArray = restTemplate.getForObject(uri, Office[].class);
        return Arrays.asList(officesArray);
    }
}
