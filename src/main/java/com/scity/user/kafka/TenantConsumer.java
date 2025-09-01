package com.scity.user.kafka;

import com.scity.user.model.dto.tenant.TenantBasicDTO;
import com.scity.user.utils.ClientUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TenantConsumer {
    @Autowired
    private Gson gson;
    @Autowired
    private ClientUtils clientUtils;
    @Autowired
    private ModelMapper modelMapper;

    @KafkaListener(topics = "update_device_from_tenant_topic", groupId = "user-service-tenant")
    public void updateDeviceFromTenant(String message) {
        TenantBasicDTO tenant = gson.fromJson(message, TenantBasicDTO.class);
        clientUtils.updateDeviceByTenantCode(tenant);
        log.info("UPDATE DEVICE INFO FROM TENANT {}", message);
    }
}
