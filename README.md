# ONVIF Profile T Client

Java Spring Boot ONVIF Profile T client for controlling IP cameras using SOAP 1.2 protocol.

📖 **[Token Rehberi (Türkçe)](TOKEN_REHBERI.md)** - Token parametreleri hakkında detaylı Türkçe dokümantasyon

## Overview

This is a comprehensive ONVIF Profile T client implementation designed to work with ONVIF-compliant IP cameras. It uses Spring Boot and Apache CXF for SOAP 1.2 communication with camera devices.

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Installation](#installation)
- [Configuration](#configuration)
- [Understanding Token Parameters](#understanding-token-parameters) ⭐
- [API Usage Examples](#api-usage-examples)
- [SOAP 1.2 Protocol](#soap-12-protocol)
- [H.264 and H.265 Support](#h264-and-h265-support)
- [Integration with OnvifServer](#integration-with-onvifserver)
- [License](#license)

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

## API Usage Examples

### Quick Start

Before making any API calls, ensure your camera is configured in `application.properties`. Here are some basic examples:

```bash
# Get device information (no token required)
curl -X GET http://localhost:8081/api/onvif/device/information

# Get available video sources (returns tokens you'll need)
curl -X GET http://localhost:8081/api/onvif/media/video-sources

# Get network interfaces (returns network tokens)
curl -X GET http://localhost:8081/api/onvif/network/interfaces
```

For detailed information about tokens and how to use them, see the [Understanding Token Parameters](#understanding-token-parameters) section below.

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

## Understanding Token Parameters

### What are Tokens?

In ONVIF protocol, **tokens** are unique identifiers used to reference specific resources on the camera. Think of them as IDs that allow you to interact with specific components of the camera system.

### Types of Tokens

1. **ProfileToken** - Identifies a media profile (combination of video/audio configurations)
2. **VideoSourceToken** - Identifies a specific video source (camera input)
3. **PresetToken** - Identifies a saved PTZ preset position
4. **ConfigurationToken** - Identifies a configuration (encoder, PTZ, etc.)
5. **NetworkInterfaceToken** - Identifies a network interface

### How to Obtain Tokens

Tokens are returned by the camera when you query for available resources. Here's how to get each type:

#### 1. Getting Video Source Tokens

```bash
# Call GetVideoSources endpoint
curl -X GET http://localhost:8081/api/onvif/media/video-sources

# Response example:
[
  {
    "token": "VideoSource_1",  # This is your videoSourceToken
    "framerate": 30.0,
    "resolution": {...}
  }
]
```

#### 2. Getting Profile Tokens

```bash
# Call GetProfiles endpoint (when implemented) or check GetVideoEncoderConfigurations
curl -X GET http://localhost:8081/api/onvif/media/encoder-configurations

# Response includes profile tokens in configuration objects
```

#### 3. Getting Preset Tokens

```bash
# Call GetPresets with a profileToken
curl -X GET "http://localhost:8081/api/onvif/ptz/presets?profileToken=Profile_1"

# Response example:
[
  {
    "token": "Preset_1",  # This is your presetToken
    "name": "Home Position",
    "ptzPosition": {...}
  }
]
```

#### 4. Getting Network Interface Tokens

```bash
# Call GetNetworkInterfaces endpoint
curl -X GET http://localhost:8081/api/onvif/network/interfaces

# Response example:
[
  {
    "token": "eth0",  # This is your network interface token
    "enabled": true,
    "info": {...}
  }
]
```

### Common Usage Patterns

#### Example 1: PTZ Control
```bash
# Step 1: Get available profiles to find a profileToken
curl -X GET http://localhost:8081/api/onvif/media/video-sources

# Step 2: Use the profileToken for PTZ operations
curl -X POST "http://localhost:8081/api/onvif/ptz/continuous-move?profileToken=Profile_1" \
  -H "Content-Type: application/json" \
  -d '{
    "panTilt": {"x": 0.5, "y": 0.0, "space": "http://www.onvif.org/ver10/tptz/PanTiltSpaces/VelocityGenericSpace"},
    "zoom": {"x": 0.0, "space": "http://www.onvif.org/ver10/tptz/ZoomSpaces/VelocityGenericSpace"}
  }'
```

#### Example 2: Image Settings
```bash
# Step 1: Get video source token
curl -X GET http://localhost:8081/api/onvif/media/video-sources
# Note the "token" field (e.g., "VideoSource_1")

# Step 2: Use the videoSourceToken to get/set imaging settings
curl -X GET "http://localhost:8081/api/onvif/imaging/settings?videoSourceToken=VideoSource_1"

# Step 3: Update imaging settings
curl -X POST "http://localhost:8081/api/onvif/imaging/settings?videoSourceToken=VideoSource_1" \
  -H "Content-Type: application/json" \
  -d '{
    "brightness": 50.0,
    "contrast": 50.0,
    "saturation": 50.0
  }'
```

#### Example 3: Working with Presets
```bash
# Step 1: Get profileToken (from video sources or profiles)
# Step 2: Create a new preset at current position
curl -X POST "http://localhost:8081/api/onvif/ptz/presets?profileToken=Profile_1&presetName=MyPreset"
# Returns: "Preset_1" (this is your new presetToken)

# Step 3: Go to the preset later
curl -X POST "http://localhost:8081/api/onvif/ptz/presets/goto?profileToken=Profile_1&presetToken=Preset_1" \
  -H "Content-Type: application/json" \
  -d '{"panTilt": {"x": 0.5, "y": 0.5}}'
```

### Important Notes

- Tokens are camera-specific and may vary between different camera models
- Tokens typically persist across reboots but may change after factory reset
- Some cameras use sequential numbering (Profile_1, Profile_2) while others use GUIDs
- Always query for available tokens rather than hard-coding them
- Tokens are case-sensitive

### Troubleshooting

**Q: I'm getting "Invalid Token" errors**
- Verify the token exists by calling the appropriate Get* endpoint
- Ensure you're using the correct token type for the operation
- Check that the token hasn't been deleted (for presets, configurations)

**Q: Where do I find the first profileToken?**
- Call `/api/onvif/media/video-sources` or `/api/onvif/media/encoder-configurations`
- Use the token from the first result
- Most cameras have at least one default profile

**Q: Can I create custom tokens?**
- No, tokens are generated by the camera
- You can only use tokens that the camera provides
- Some operations (like SetPreset) return new tokens when creating resources

## Integration with OnvifServer

This client is designed to work seamlessly with the [hediyenurtut/OnvifServer](https://github.com/hediyenurtut/OnvifServer) project.

## License

MIT License
