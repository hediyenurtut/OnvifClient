# ONVIF Profile T Client

Java Spring Boot ONVIF Profile T client for controlling IP cameras using SOAP 1.2 protocol.

## Overview

This is a comprehensive ONVIF Profile T client implementation designed to work with ONVIF-compliant IP cameras. It uses Spring Boot and Apache CXF for SOAP 1.2 communication with camera devices.

## Features

### Device Management
- ✅ GetServices - Retrieve available ONVIF services
- ✅ GetServiceCapabilities - Get service capabilities
- ✅ GetHostName - Get device hostname
- ✅ SetHostName - Set device hostname
- ✅ GetDeviceInformation - Get device manufacturer, model, firmware info

### Network Configuration
- ✅ GetNetworkInterfaces - Retrieve network interface configurations
- ✅ SetNetworkInterfaces - Configure network interfaces
- ✅ GetNetworkDefaultGateway - Get default gateway settings
- ✅ SetNetworkDefaultGateway - Configure default gateway

### PTZ Control
- ✅ Move - Absolute PTZ movement
- ✅ ContinuousMove - Continuous PTZ movement
- ✅ Stop - Stop PTZ movement
- ✅ GetStatus - Get current PTZ status
- ✅ SetHomePosition - Set home position
- ✅ GotoHomePosition - Go to home position
- ✅ GetPresets - Get PTZ presets
- ✅ SetPresets - Set PTZ presets
- ✅ GotoPreset - Go to specific preset
- ✅ RemovePreset - Remove preset
- ✅ GetMoveOptions - Get PTZ configuration options

### Media Operations
- ✅ GetVideoSources - Get available video sources
- ✅ GetVideoEncoderConfigurations - Get video encoder configurations
- ✅ AddConfiguration - Add media configuration
- ✅ H.264 Encoding/Decoding support
- ✅ H.265 Encoding/Decoding support

### Imaging Operations
- ✅ GetImageSettings - Get image settings (brightness, contrast, etc.)
- ✅ SetImageSettings - Configure image settings
- ✅ GetOptions - Get available imaging options

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.3**
- **Apache CXF 4.0.3** - SOAP 1.2 support
- **Lombok** - Reduce boilerplate code
- **Maven** - Dependency management

## Installation

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- ONVIF-compliant IP camera

### Build the Project

\`\`\`bash
git clone https://github.com/hediyenurtut/OnvifClient.git
cd OnvifClient
mvn clean install
\`\`\`

### Run the Application

\`\`\`bash
mvn spring-boot:run
\`\`\`

The application will start on port 8081.

## Configuration

Edit \`src/main/resources/application.properties\`:

\`\`\`properties
# ONVIF Camera Configuration
onvif.camera.host=192.168.1.100
onvif.camera.port=8080
onvif.camera.username=admin
onvif.camera.password=admin
onvif.camera.service-path=/onvif/device_service
\`\`\`

## SOAP 1.2 Protocol

This client uses SOAP 1.2 protocol as specified in the ONVIF standard. All requests are sent with:
- Content-Type: \`application/soap+xml; charset=utf-8\`
- SOAP Envelope namespace: \`http://www.w3.org/2003/05/soap-envelope\`

## H.264 and H.265 Support

The client includes models for both H.264 and H.265 video encoding configurations:
- H.264 Configuration (Main, Baseline, High profiles)
- H.265 Configuration (Main profile)

## Integration with OnvifServer

This client is designed to work seamlessly with the [hediyenurtut/OnvifServer](https://github.com/hediyenurtut/OnvifServer) project.

## License

MIT License
