package com.onvif.client.model.ptz;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.xml.bind.annotation.*;

/**
 * ONVIF 1D Vector (for Zoom)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Vector1D {
    
    @XmlElement(name = "x")
    private float x;
    
    @XmlAttribute(name = "space")
    private String space;
}
