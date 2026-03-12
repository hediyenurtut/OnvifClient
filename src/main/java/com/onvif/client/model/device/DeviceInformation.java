package com.onvif.client.model.device;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.xml.bind.annotation.*;

/**
 * ONVIF GetDeviceInformation Response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "GetDeviceInformationResponse", namespace = "http://www.onvif.org/ver10/device/wsdl")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceInformation {
    
    @XmlElement(name = "Manufacturer")
    private String manufacturer;
    
    @XmlElement(name = "Model")
    private String model;
    
    @XmlElement(name = "FirmwareVersion")
    private String firmwareVersion;
    
    @XmlElement(name = "SerialNumber")
    private String serialNumber;
    
    @XmlElement(name = "HardwareId")
    private String hardwareId;
}
