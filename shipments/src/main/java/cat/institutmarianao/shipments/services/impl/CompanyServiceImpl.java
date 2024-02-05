package cat.institutmarianao.shipments.services.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import cat.institutmarianao.shipments.model.Company;
import cat.institutmarianao.shipments.services.CompanyService;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Value("${webService.host}")
    private String webServiceHost;

    @Value("${webService.port}")
    private String webServicePort;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<Company> getAllCompanies() {
        final String uri = webServiceHost + ":" + webServicePort + "/companies/find/all";
        Company[] companiesArray = restTemplate.getForObject(uri, Company[].class);
        return Arrays.asList(companiesArray);
    }

    @Override
    public Company getById(Long id) {
        final String baseUri = webServiceHost + ":" + webServicePort + "/companies/get/by/id/{id}";
        Company company = restTemplate.getForObject(baseUri, Company.class, id);
        return company;
    }
}
