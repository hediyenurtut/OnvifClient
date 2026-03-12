"""
PTZ Control Example
Demonstrates how to control PTZ (Pan-Tilt-Zoom) cameras
"""

from onvif_client import ONVIFClient
import time

def main():
    # Camera connection details
    CAMERA_IP = "192.168.1.100"
    CAMERA_PORT = 80
    USERNAME = "admin"
    PASSWORD = "admin"
    
    # Create and connect to camera
    client = ONVIFClient(CAMERA_IP, CAMERA_PORT, USERNAME, PASSWORD)
    
    if not client.connect():
        print("Failed to connect to camera")
        return
    
    # Check if PTZ is supported
    capabilities = client.get_capabilities()
    if not capabilities or not capabilities.get('ptz'):
        print("PTZ not supported on this camera")
        return
    
    print("PTZ is supported. Starting PTZ demo...")
    
    # Pan right
    print("Panning right...")
    client.move_ptz(pan=0.5, tilt=0.0, zoom=0.0)
    time.sleep(2)
    client.stop_ptz()
    
    time.sleep(1)
    
    # Pan left
    print("Panning left...")
    client.move_ptz(pan=-0.5, tilt=0.0, zoom=0.0)
    time.sleep(2)
    client.stop_ptz()
    
    time.sleep(1)
    
    # Tilt up
    print("Tilting up...")
    client.move_ptz(pan=0.0, tilt=0.5, zoom=0.0)
    time.sleep(2)
    client.stop_ptz()
    
    time.sleep(1)
    
    # Tilt down
    print("Tilting down...")
    client.move_ptz(pan=0.0, tilt=-0.5, zoom=0.0)
    time.sleep(2)
    client.stop_ptz()
    
    time.sleep(1)
    
    # Zoom in
    print("Zooming in...")
    client.move_ptz(pan=0.0, tilt=0.0, zoom=0.5)
    time.sleep(2)
    client.stop_ptz()
    
    time.sleep(1)
    
    # Zoom out
    print("Zooming out...")
    client.move_ptz(pan=0.0, tilt=0.0, zoom=-0.5)
    time.sleep(2)
    client.stop_ptz()
    
    print("PTZ demo complete!")

if __name__ == "__main__":
    main()
