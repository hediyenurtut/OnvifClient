# ONVIF Client Documentation

## Table of Contents

1. [Introduction](#introduction)
2. [Installation](#installation)
3. [Quick Start](#quick-start)
4. [API Reference](#api-reference)
5. [Examples](#examples)
6. [Troubleshooting](#troubleshooting)
7. [FAQ](#faq)

## Introduction

ONVIF Client is a Python library for controlling ONVIF-compliant IP cameras. It provides a simple, intuitive interface for common camera operations.

### What is ONVIF?

ONVIF (Open Network Video Interface Forum) is an open industry standard for IP-based security products. It ensures interoperability between devices from different manufacturers.

### Supported Features

- Device discovery and connection
- Device information retrieval
- Media profile management
- RTSP stream URI retrieval
- PTZ control (Pan-Tilt-Zoom)
- Capability detection

## Installation

### Requirements

- Python 3.7 or higher
- pip package manager

### Install from Source

```bash
git clone https://github.com/hediyenurtut/OnvifClient.git
cd OnvifClient
pip install -r requirements.txt
```

### Install as Package

```bash
pip install -e .
```

## Quick Start

### Basic Connection

```python
from onvif_client import ONVIFClient

# Create client
client = ONVIFClient(
    ip="192.168.1.100",
    port=80,
    username="admin",
    password="admin"
)

# Connect to camera
if client.connect():
    print("Connected successfully!")
    
    # Get device info
    info = client.get_device_info()
    print(f"Camera: {info['manufacturer']} {info['model']}")
```

### Get Stream URI

```python
# Get RTSP stream URL
stream_uri = client.get_stream_uri()
print(f"Stream: {stream_uri}")

# You can use this URI with VLC, ffmpeg, or other media players
# Example: vlc rtsp://192.168.1.100:554/stream1
```

### Control PTZ

```python
import time

# Pan right
client.move_ptz(pan=0.5)
time.sleep(2)
client.stop_ptz()

# Zoom in
client.move_ptz(zoom=0.5)
time.sleep(2)
client.stop_ptz()
```

## API Reference

### ONVIFClient Class

#### Constructor

```python
ONVIFClient(ip: str, port: int, username: str, password: str)
```

**Parameters:**
- `ip` (str): Camera IP address
- `port` (int): ONVIF port (typically 80, 8080, or 8899)
- `username` (str): Authentication username
- `password` (str): Authentication password

#### Methods

##### connect()

Establish connection to the ONVIF camera.

```python
success = client.connect()
```

**Returns:** `bool` - True if successful, False otherwise

##### get_device_info()

Retrieve camera device information.

```python
info = client.get_device_info()
```

**Returns:** `dict` with keys:
- `manufacturer`: Device manufacturer
- `model`: Device model
- `firmware_version`: Firmware version
- `serial_number`: Serial number
- `hardware_id`: Hardware ID

**Returns:** `None` if not connected or error occurs

##### get_capabilities()

Get camera capabilities.

```python
caps = client.get_capabilities()
```

**Returns:** `dict` with boolean values:
- `analytics`: Analytics support
- `device`: Device management
- `events`: Event handling
- `imaging`: Imaging capabilities
- `media`: Media streaming
- `ptz`: PTZ control

##### get_profiles()

List available media profiles.

```python
profiles = client.get_profiles()
```

**Returns:** `list` of profile objects

##### get_stream_uri(profile_token=None)

Get RTSP stream URI for a profile.

```python
uri = client.get_stream_uri()  # Uses first profile
uri = client.get_stream_uri(profile_token="Profile_1")  # Specific profile
```

**Parameters:**
- `profile_token` (str, optional): Media profile token

**Returns:** `str` - RTSP stream URI

##### move_ptz(pan=0.0, tilt=0.0, zoom=0.0)

Control PTZ movement.

```python
client.move_ptz(pan=0.5, tilt=0.0, zoom=0.0)
```

**Parameters:**
- `pan` (float): Pan value from -1.0 (left) to 1.0 (right)
- `tilt` (float): Tilt value from -1.0 (down) to 1.0 (up)
- `zoom` (float): Zoom value from -1.0 (out) to 1.0 (in)

**Returns:** `bool` - True if successful

##### stop_ptz()

Stop PTZ movement.

```python
client.stop_ptz()
```

**Returns:** `bool` - True if successful

## Examples

### Example 1: Camera Information

```python
from onvif_client import ONVIFClient

client = ONVIFClient("192.168.1.100", 80, "admin", "password")

if client.connect():
    # Device info
    info = client.get_device_info()
    print(f"Manufacturer: {info['manufacturer']}")
    print(f"Model: {info['model']}")
    print(f"Firmware: {info['firmware_version']}")
    
    # Capabilities
    caps = client.get_capabilities()
    print(f"PTZ Support: {caps['ptz']}")
    print(f"Events Support: {caps['events']}")
```

### Example 2: Multiple Profiles

```python
from onvif_client import ONVIFClient

client = ONVIFClient("192.168.1.100", 80, "admin", "password")
client.connect()

# Get all profiles
profiles = client.get_profiles()

# Get stream for each profile
for profile in profiles:
    print(f"\nProfile: {profile.Name}")
    uri = client.get_stream_uri(profile.token)
    print(f"Stream URI: {uri}")
```

### Example 3: PTZ Patrol

```python
from onvif_client import ONVIFClient
import time

def patrol():
    client = ONVIFClient("192.168.1.100", 80, "admin", "password")
    client.connect()
    
    # Check PTZ support
    if not client.get_capabilities()['ptz']:
        print("PTZ not supported")
        return
    
    # Pan left
    print("Panning left...")
    client.move_ptz(pan=-0.5)
    time.sleep(3)
    client.stop_ptz()
    time.sleep(1)
    
    # Pan right
    print("Panning right...")
    client.move_ptz(pan=0.5)
    time.sleep(3)
    client.stop_ptz()
    time.sleep(1)
    
    # Return to center
    print("Returning to center...")
    client.move_ptz(pan=-0.25)
    time.sleep(1.5)
    client.stop_ptz()

patrol()
```

### Example 4: Using CLI

```bash
# Get device info
python onvif_cli.py --ip 192.168.1.100 --username admin --password admin info

# Get stream URI
python onvif_cli.py --ip 192.168.1.100 --username admin --password admin stream

# Control PTZ
python onvif_cli.py --ip 192.168.1.100 --username admin --password admin ptz --pan 0.5

# Stop PTZ
python onvif_cli.py --ip 192.168.1.100 --username admin --password admin ptz --stop
```

## Troubleshooting

### Connection Issues

**Problem:** `Failed to connect to camera`

**Solutions:**
1. Verify camera IP address is correct
2. Check that camera is powered on and on the network
3. Ping the camera: `ping 192.168.1.100`
4. Verify ONVIF port (common ports: 80, 8080, 8899)
5. Check firewall settings
6. Ensure camera supports ONVIF

**Problem:** `Authentication failed`

**Solutions:**
1. Verify username and password
2. Check if camera requires different authentication
3. Try resetting camera credentials

### PTZ Issues

**Problem:** PTZ commands don't work

**Solutions:**
1. Check if camera supports PTZ: `client.get_capabilities()['ptz']`
2. Verify PTZ service initialized: `client.ptz_service is not None`
3. Check camera documentation for PTZ support

### Stream Issues

**Problem:** Cannot get stream URI

**Solutions:**
1. Verify media profiles exist: `client.get_profiles()`
2. Check if camera supports RTSP streaming
3. Try different profile tokens

## FAQ

### Q: Which cameras are supported?

A: Any camera that complies with ONVIF Profile S or later. Common brands include:
- Hikvision
- Dahua
- Axis
- Vivotek
- Samsung
- And many others

### Q: How do I find my camera's IP address?

A: Options:
1. Check your router's connected devices
2. Use network scanning tools like Advanced IP Scanner
3. Check camera's display/OSD menu
4. Use manufacturer's discovery tool

### Q: What are common ONVIF ports?

A: Common ports:
- 80 (HTTP)
- 8080
- 8899
- 8000
- 554 (RTSP - for streaming, not ONVIF control)

### Q: How do I view the RTSP stream?

A: Use media players like:
- VLC: `vlc rtsp://camera-ip:554/stream`
- ffplay: `ffplay rtsp://camera-ip:554/stream`
- Browser (with WebRTC gateway)

### Q: Can I use this in production?

A: Yes, but consider:
- Proper error handling
- Connection retry logic
- Security (credentials management)
- Logging configuration

### Q: How do I contribute?

A: See [CONTRIBUTING.md](../CONTRIBUTING.md) for guidelines.

### Q: Where can I get help?

A: 
1. Check this documentation
2. Review examples in `examples/` directory
3. Open an issue on GitHub
4. Check ONVIF specifications at onvif.org

## Additional Resources

- [ONVIF Official Website](https://www.onvif.org/)
- [ONVIF Specifications](https://www.onvif.org/profiles/)
- [python-onvif-zeep](https://github.com/FalkTannhaeuser/python-onvif-zeep)
- [RTSP Protocol](https://en.wikipedia.org/wiki/Real_Time_Streaming_Protocol)
