package com.onvif.client.model.ptz;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF 1D Space Description
 */
@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Space1DDescription {
    
    @XmlElement(name = "URI")
    private String uri;
    
    @XmlElement(name = "XRange")
    private FloatRange xRange;
}
