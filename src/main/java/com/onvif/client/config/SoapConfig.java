package com.onvif.client.config;

import org.apache.cxf.binding.soap.Soap12;
import org.apache.cxf.binding.soap.SoapBindingConfiguration;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SOAP 1.2 Configuration for CXF
 */
@Configuration
public class SoapConfig {

    /**
     * Configure SOAP 1.2 Binding
     */
    @Bean
    public SoapBindingConfiguration soapBindingConfiguration() {
        SoapBindingConfiguration config = new SoapBindingConfiguration();
        config.setVersion(Soap12.getInstance());
        return config;
    }
    
    /**
     * Create JaxWsProxyFactoryBean for SOAP clients
     */
    public JaxWsProxyFactoryBean createProxyFactory(String serviceUrl) {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setAddress(serviceUrl);
        factory.setBindingId("http://www.w3.org/2003/05/soap/bindings/HTTP/");
        return factory;
    }
}
