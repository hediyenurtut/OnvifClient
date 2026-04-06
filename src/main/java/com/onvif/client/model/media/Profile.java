package com.onvif.client.model.media;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF Media Profile
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Profile {

    @XmlAttribute(name = "token")
    private String token;

    @XmlElement(name = "Name")
    private String name;

    @XmlElement(name = "VideoSourceConfigurationToken")
    private String videoSourceConfigurationToken;

    @XmlElement(name = "VideoEncoderConfigurationToken")
    private String videoEncoderConfigurationToken;
}
