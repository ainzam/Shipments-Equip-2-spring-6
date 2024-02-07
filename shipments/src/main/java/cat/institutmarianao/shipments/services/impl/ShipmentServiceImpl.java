package cat.institutmarianao.shipments.services.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import cat.institutmarianao.shipments.model.Action;
import cat.institutmarianao.shipments.model.Shipment;
import cat.institutmarianao.shipments.model.forms.ShipmentsFilter;
import cat.institutmarianao.shipments.services.ShipmentService;

@Service
public class ShipmentServiceImpl implements ShipmentService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${webService.host}")
    private String webServiceHost;

    @Value("${webService.port}")
    private String webServicePort;

    @Override
    public List<Shipment> filterShipments(ShipmentsFilter filter) {
        final String baseUri = webServiceHost + ":" + webServicePort + "/shipments/find/all/" + filter.getStatus();
        Shipment[] shipmentsArray = restTemplate.getForObject(baseUri, Shipment[].class);
        return Arrays.asList(shipmentsArray);
    }

    @Override
    public Shipment add(Shipment shipment) {
        final String uri = webServiceHost + ":" + webServicePort + "/shipments/save";
        return restTemplate.postForObject(uri, shipment, Shipment.class);
    }

    @Override
    public Action tracking(Action action) {
        final String uri = webServiceHost + ":" + webServicePort + "/shipments/find/tracking/by/id/" + action.getShipmentId();
        return restTemplate.getForObject(uri, Action.class);
    }

}
