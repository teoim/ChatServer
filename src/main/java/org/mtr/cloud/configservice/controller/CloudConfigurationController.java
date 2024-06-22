package org.mtr.cloud.configservice.controller;

import org.mtr.cloud.configservice.configuration.Configuration;
import org.mtr.cloud.configservice.controller.dto.CloudConfigurationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CloudConfigurationController {

    @Autowired
    private Configuration applicationPropertiesTestConfig;

    @GetMapping("/config")
    public CloudConfigurationDTO getCloudConfiguration(){
//        return new CloudConfigurationDTO("test1", "test2");
        return new CloudConfigurationDTO(
                applicationPropertiesTestConfig.getConfig1(),
                applicationPropertiesTestConfig.getConfig2());
    }
}
