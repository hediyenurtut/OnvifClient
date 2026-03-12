# Quick Setup Guide

## Prerequisites

- Python 3.7 or higher
- pip package manager
- An ONVIF-compliant IP camera on your network

## Installation Steps

### 1. Clone the Repository

```bash
git clone https://github.com/hediyenurtut/OnvifClient.git
cd OnvifClient
```

### 2. Install Dependencies

```bash
pip install -r requirements.txt
```

Or install in development mode:

```bash
pip install -e .
```

### 3. Find Your Camera

You need to know:
- Camera IP address (e.g., 192.168.1.100)
- ONVIF port (usually 80, 8080, or 8899)
- Username and password

To find your camera's IP:
- Check your router's DHCP client list
- Use a network scanner (e.g., Advanced IP Scanner, nmap)
- Check the camera's OSD menu or display

### 4. Test Connection

Try the basic example:

```bash
cd examples
python basic_usage.py
```

Edit the file first to set your camera's IP, port, username, and password.

### 5. Use the CLI Tool

```bash
# Get device info
python onvif_cli.py --ip 192.168.1.100 --username admin --password admin info

# Get stream URI
python onvif_cli.py --ip 192.168.1.100 --username admin --password admin stream
```

## Usage Examples

### Python Library

```python
from onvif_client import ONVIFClient

# Connect to camera
client = ONVIFClient("192.168.1.100", 80, "admin", "password")
if client.connect():
    # Get device info
    info = client.get_device_info()
    print(f"Camera: {info['manufacturer']} {info['model']}")
    
    # Get stream
    stream = client.get_stream_uri()
    print(f"Stream: {stream}")
```

### Command Line

```bash
# Device information
python onvif_cli.py --ip 192.168.1.100 --username admin --password admin info

# Camera capabilities
python onvif_cli.py --ip 192.168.1.100 --username admin --password admin capabilities

# Media profiles
python onvif_cli.py --ip 192.168.1.100 --username admin --password admin profiles

# Stream URI
python onvif_cli.py --ip 192.168.1.100 --username admin --password admin stream

# PTZ control (if supported)
python onvif_cli.py --ip 192.168.1.100 --username admin --password admin ptz --pan 0.5
python onvif_cli.py --ip 192.168.1.100 --username admin --password admin ptz --stop
```

## Common Issues

### "Failed to connect to camera"
- Verify camera IP address
- Check that camera is on the network (ping it)
- Verify ONVIF port number
- Check firewall settings

### "Authentication failed"
- Verify username and password
- Some cameras have separate ONVIF credentials

### "PTZ not working"
- Not all cameras support PTZ
- Check capabilities: `python onvif_cli.py ... capabilities`

## Next Steps

1. Read the full documentation in `docs/DOCUMENTATION.md`
2. Check out examples in `examples/` directory
3. Read the API reference in `README.md`
4. For issues, open a GitHub issue

## Support

- Documentation: See `README.md` and `docs/DOCUMENTATION.md`
- Examples: Check `examples/` directory
- Issues: GitHub Issues
- ONVIF Info: https://www.onvif.org/
