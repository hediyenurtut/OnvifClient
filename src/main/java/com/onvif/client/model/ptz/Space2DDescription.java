package com.onvif.client.model.ptz;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF 2D Space Description
 */
@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Space2DDescription {
    
    @XmlElement(name = "URI")
    private String uri;
    
    @XmlElement(name = "XRange")
    private FloatRange xRange;
    
    @XmlElement(name = "YRange")
    private FloatRange yRange;
}
