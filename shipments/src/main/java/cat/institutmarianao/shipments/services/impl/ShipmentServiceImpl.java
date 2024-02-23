package cat.institutmarianao.shipments.services.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import cat.institutmarianao.shipments.model.Action;
import cat.institutmarianao.shipments.model.Shipment;
import cat.institutmarianao.shipments.model.forms.ShipmentsFilter;
import cat.institutmarianao.shipments.services.ShipmentService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Service
public class ShipmentServiceImpl implements ShipmentService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${webService.host}")
    private String webServiceHost;

    @Value("${webService.port}")
    private String webServicePort;

    @Override
    public List<Shipment> filterShipments(ShipmentsFilter shipmentStatus) {
        final String baseUri = webServiceHost + ":" + webServicePort + "/shipments/find/all";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUri)
                .queryParam("status", shipmentStatus.getStatus())
                .queryParam("category", shipmentStatus.getCategory());

        if (shipmentStatus.getReceptionist() != null) {
            builder.queryParam("receivedBy", shipmentStatus.getReceptionist());
        }

        if (shipmentStatus.getCourierAssigned() != null) {
            builder.queryParam("courierAssigned", shipmentStatus.getCourierAssigned());
        }

        ResponseEntity<Shipment[]> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, Shipment[].class);

        Shipment[] shipmentsArray = response.getBody();
        
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
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Action> request = new HttpEntity<>(action, headers);

        return restTemplate.postForObject(uri, request, Action.class);
    }
    
}
