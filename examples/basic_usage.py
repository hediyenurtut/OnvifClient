"""
Basic ONVIF Camera Example
Demonstrates how to connect to a camera and get basic information
"""

from onvif_client import ONVIFClient

def main():
    # Camera connection details
    # Replace these with your camera's actual details
    CAMERA_IP = "192.168.1.100"
    CAMERA_PORT = 80
    USERNAME = "admin"
    PASSWORD = "admin"
    
    # Create ONVIF client
    client = ONVIFClient(CAMERA_IP, CAMERA_PORT, USERNAME, PASSWORD)
    
    # Connect to camera
    if not client.connect():
        print("Failed to connect to camera")
        return
    
    # Get device information
    print("\n=== Device Information ===")
    device_info = client.get_device_info()
    if device_info:
        for key, value in device_info.items():
            print(f"{key}: {value}")
    
    # Get camera capabilities
    print("\n=== Camera Capabilities ===")
    capabilities = client.get_capabilities()
    if capabilities:
        for key, value in capabilities.items():
            print(f"{key}: {value}")
    
    # Get media profiles
    print("\n=== Media Profiles ===")
    profiles = client.get_profiles()
    if profiles:
        for i, profile in enumerate(profiles):
            print(f"Profile {i+1}: {profile.Name} (Token: {profile.token})")
    
    # Get stream URI
    print("\n=== Stream URI ===")
    stream_uri = client.get_stream_uri()
    if stream_uri:
        print(f"RTSP Stream: {stream_uri}")

if __name__ == "__main__":
    main()
