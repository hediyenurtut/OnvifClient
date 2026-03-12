# OnvifClient

A simple and easy-to-use Python ONVIF client for controlling IP cameras.

## Overview

OnvifClient is a Python library that provides a simple interface to interact with ONVIF-compliant IP cameras. It supports basic camera operations including:

- Device discovery and connection
- Retrieving device information
- Getting media profiles and stream URIs
- PTZ (Pan-Tilt-Zoom) control
- Camera capabilities detection

## Features

- ✅ Easy camera connection with IP, port, username, and password
- ✅ Retrieve device information (manufacturer, model, firmware, etc.)
- ✅ Get media profiles and RTSP stream URIs
- ✅ PTZ control support (if camera supports it)
- ✅ Camera capabilities detection
- ✅ Comprehensive error handling and logging
- ✅ Simple and intuitive API

## Installation

### From Source

```bash
git clone https://github.com/hediyenurtut/OnvifClient.git
cd OnvifClient
pip install -r requirements.txt
```

### Using pip (after publishing)

```bash
pip install onvif-client
```

## Requirements

- Python 3.7+
- onvif-zeep >= 0.2.12
- zeep >= 4.2.1
- requests >= 2.31.0

## Quick Start

### Basic Usage

```python
from onvif_client import ONVIFClient

# Create client instance
client = ONVIFClient(
    ip="192.168.1.100",
    port=80,
    username="admin",
    password="admin"
)

# Connect to camera
if client.connect():
    # Get device information
    device_info = client.get_device_info()
    print(f"Camera Model: {device_info['model']}")
    print(f"Firmware: {device_info['firmware_version']}")
    
    # Get stream URI
    stream_uri = client.get_stream_uri()
    print(f"RTSP Stream: {stream_uri}")
```

### PTZ Control

```python
from onvif_client import ONVIFClient
import time

client = ONVIFClient("192.168.1.100", 80, "admin", "admin")
client.connect()

# Pan right
client.move_ptz(pan=0.5, tilt=0.0, zoom=0.0)
time.sleep(2)
client.stop_ptz()

# Zoom in
client.move_ptz(pan=0.0, tilt=0.0, zoom=0.5)
time.sleep(2)
client.stop_ptz()
```

## API Reference

### ONVIFClient

#### `__init__(ip: str, port: int, username: str, password: str)`

Initialize the ONVIF client.

**Parameters:**
- `ip` (str): Camera IP address
- `port` (int): ONVIF port (usually 80, 8080, or 8899)
- `username` (str): Camera username
- `password` (str): Camera password

#### `connect() -> bool`

Connect to the ONVIF camera.

**Returns:**
- `bool`: True if connection successful, False otherwise

#### `get_device_info() -> Optional[Dict[str, Any]]`

Get camera device information.

**Returns:**
- `dict`: Device information including manufacturer, model, firmware version, serial number, and hardware ID

#### `get_profiles() -> Optional[List[Any]]`

Get available media profiles from the camera.

**Returns:**
- `list`: List of media profiles

#### `get_stream_uri(profile_token: Optional[str] = None) -> Optional[str]`

Get RTSP stream URI for a media profile.

**Parameters:**
- `profile_token` (str, optional): Media profile token (uses first profile if not specified)

**Returns:**
- `str`: RTSP stream URI

#### `get_capabilities() -> Optional[Dict[str, Any]]`

Get camera capabilities.

**Returns:**
- `dict`: Camera capabilities (analytics, device, events, imaging, media, ptz)

#### `move_ptz(pan: float = 0.0, tilt: float = 0.0, zoom: float = 0.0) -> bool`

Move PTZ camera.

**Parameters:**
- `pan` (float): Pan value (-1.0 to 1.0)
- `tilt` (float): Tilt value (-1.0 to 1.0)
- `zoom` (float): Zoom value (-1.0 to 1.0)

**Returns:**
- `bool`: True if successful, False otherwise

#### `stop_ptz() -> bool`

Stop PTZ movement.

**Returns:**
- `bool`: True if successful, False otherwise

## Examples

Check the `examples/` directory for more comprehensive examples:

- `basic_usage.py` - Basic camera connection and information retrieval
- `ptz_control.py` - PTZ control demonstration

## ONVIF Profiles

This client supports ONVIF Profile S, which includes:
- Video streaming
- PTZ control
- Media configuration
- Device management

## Troubleshooting

### Connection Issues

1. **Verify camera IP and port**: Make sure the camera is accessible on the network
2. **Check credentials**: Ensure username and password are correct
3. **Firewall**: Check if firewall is blocking the connection
4. **ONVIF support**: Verify that your camera supports ONVIF protocol

### PTZ Not Working

1. **Check capabilities**: Use `get_capabilities()` to verify PTZ support
2. **Camera model**: Some cameras don't support PTZ
3. **Profile**: Ensure the media profile supports PTZ

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

MIT License

## Acknowledgments

Built with [onvif-zeep](https://github.com/FalkTannhaeuser/python-onvif-zeep) library.

## Support

For issues and questions, please open an issue on GitHub.

## Roadmap

- [ ] Device discovery (WS-Discovery)
- [ ] Event handling and subscriptions
- [ ] Snapshot capture
- [ ] Audio support
- [ ] Advanced PTZ presets
- [ ] Recording management
- [ ] Analytics support