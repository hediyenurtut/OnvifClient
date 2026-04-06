package com.onvif.client.model.media;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF GetStreamUri Response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class StreamUriResponse {

    @XmlElement(name = "Uri")
    private String uri;
}
