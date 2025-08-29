package com.scity.user.utils;

import com.scity.user.model.dto.ResModel;
import com.scity.user.model.dto.sms.PayloadSMSDTO;
import com.scity.user.model.dto.tenant.TenantBasicDTO;
import com.scity.user.model.dto.location.LocationDTO;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class ClientUtils {
    @Autowired
    private EurekaClient eurekaClient;
    @Autowired
    private RestTemplate restTemplate;

    public LocationDTO getLocationById(UUID id) {
        String domain = eurekaClient.getNextServerFromEureka("device-service", false).getHomePageUrl();
        ResponseEntity<ResModel<LocationDTO>> response = restTemplate.exchange(
                domain + "device/location/internal/" + id,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return Objects.requireNonNull(response.getBody()).getData();
    }

    public List<LocationDTO> getLocations(List<UUID> ids, String tenantCode, boolean findAll) {
        String tenantParam;
        if (tenantCode==null || tenantCode.isEmpty())
            tenantParam = "";
        else
            tenantParam = "&tenantCode=" + tenantCode;

        String findAllParam = "&findAll=" + findAll;

        String domain = eurekaClient.getNextServerFromEureka("device-service", false).getHomePageUrl();
        UriComponentsBuilder componentsBuilder = UriComponentsBuilder.fromHttpUrl(domain + "/device/location/internal/all");
        componentsBuilder.queryParam("ids", ids);
        ResponseEntity<ResModel<List<LocationDTO>>> response = restTemplate.exchange(
                componentsBuilder.toUriString() + tenantParam + findAllParam,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return Objects.requireNonNull(response.getBody()).getData();
    }

    public void updateDeviceByTenantCode(TenantBasicDTO tenant) {
        String domain = eurekaClient.getNextServerFromEureka("device-service", false).getHomePageUrl();
        HttpEntity<TenantBasicDTO> request = new HttpEntity<>(tenant);
        restTemplate.exchange(
                domain + "device/devices/internal",
                HttpMethod.PUT,
                request,
                new ParameterizedTypeReference<>() {
                }
        );
    }
    
    public void sendSMS(String accessToken, PayloadSMSDTO payload) {
        HttpEntity<PayloadSMSDTO> payloadHttpEntity = new HttpEntity<>(payload);
        restTemplate.exchange(
                "https://api.stringee.com/v1/sms?access_token=" + accessToken,
                HttpMethod.POST,
                payloadHttpEntity,
                String.class
        );
    }
}
