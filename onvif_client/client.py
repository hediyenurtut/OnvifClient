"""
ONVIF Client Implementation
Provides a simple interface to interact with ONVIF-compliant IP cameras
"""

from onvif import ONVIFCamera
from typing import Optional, Dict, Any, List
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class ONVIFClient:
    """
    A simple ONVIF client for IP cameras
    
    This client provides basic functionality for:
    - Connecting to ONVIF cameras
    - Retrieving device information
    - Getting media profiles and stream URIs
    - PTZ control (if supported)
    """
    
    def __init__(self, ip: str, port: int, username: str, password: str):
        """
        Initialize ONVIF client
        
        Args:
            ip: Camera IP address
            port: ONVIF port (usually 80, 8080, or 8899)
            username: Camera username
            password: Camera password
        """
        self.ip = ip
        self.port = port
        self.username = username
        self.password = password
        self.camera = None
        self.media_service = None
        self.ptz_service = None
        self.device_service = None
        
    def connect(self) -> bool:
        """
        Connect to the ONVIF camera
        
        Returns:
            bool: True if connection successful, False otherwise
        """
        try:
            logger.info(f"Connecting to camera at {self.ip}:{self.port}")
            self.camera = ONVIFCamera(
                self.ip,
                self.port,
                self.username,
                self.password
            )
            
            # Initialize services
            self.media_service = self.camera.create_media_service()
            self.device_service = self.camera.create_devicemgmt_service()
            
            # Try to create PTZ service (may not be available on all cameras)
            try:
                self.ptz_service = self.camera.create_ptz_service()
            except Exception as e:
                logger.warning(f"PTZ service not available: {e}")
                
            logger.info("Successfully connected to camera")
            return True
            
        except Exception as e:
            logger.error(f"Failed to connect to camera: {e}")
            return False
    
    def get_device_info(self) -> Optional[Dict[str, Any]]:
        """
        Get camera device information
        
        Returns:
            dict: Device information including manufacturer, model, firmware version, etc.
        """
        if not self.device_service:
            logger.error("Not connected to camera")
            return None
            
        try:
            info = self.device_service.GetDeviceInformation()
            return {
                'manufacturer': info.Manufacturer,
                'model': info.Model,
                'firmware_version': info.FirmwareVersion,
                'serial_number': info.SerialNumber,
                'hardware_id': info.HardwareId
            }
        except Exception as e:
            logger.error(f"Failed to get device info: {e}")
            return None
    
    def get_profiles(self) -> Optional[List[Any]]:
        """
        Get available media profiles from the camera
        
        Returns:
            list: List of media profiles
        """
        if not self.media_service:
            logger.error("Not connected to camera")
            return None
            
        try:
            profiles = self.media_service.GetProfiles()
            logger.info(f"Found {len(profiles)} media profiles")
            return profiles
        except Exception as e:
            logger.error(f"Failed to get profiles: {e}")
            return None
    
    def get_stream_uri(self, profile_token: Optional[str] = None) -> Optional[str]:
        """
        Get RTSP stream URI for a media profile
        
        Args:
            profile_token: Media profile token (uses first profile if not specified)
            
        Returns:
            str: RTSP stream URI
        """
        if not self.media_service:
            logger.error("Not connected to camera")
            return None
            
        try:
            # Get profiles if token not provided
            if not profile_token:
                profiles = self.get_profiles()
                if not profiles:
                    return None
                profile_token = profiles[0].token
            
            # Create stream setup
            stream_setup = {
                'Stream': 'RTP-Unicast',
                'Transport': {'Protocol': 'RTSP'}
            }
            
            # Get stream URI
            uri_response = self.media_service.GetStreamUri({
                'StreamSetup': stream_setup,
                'ProfileToken': profile_token
            })
            
            logger.info(f"Stream URI: {uri_response.Uri}")
            return uri_response.Uri
            
        except Exception as e:
            logger.error(f"Failed to get stream URI: {e}")
            return None
    
    def get_capabilities(self) -> Optional[Dict[str, Any]]:
        """
        Get camera capabilities
        
        Returns:
            dict: Camera capabilities
        """
        if not self.device_service:
            logger.error("Not connected to camera")
            return None
            
        try:
            capabilities = self.device_service.GetCapabilities()
            return {
                'analytics': hasattr(capabilities, 'Analytics'),
                'device': hasattr(capabilities, 'Device'),
                'events': hasattr(capabilities, 'Events'),
                'imaging': hasattr(capabilities, 'Imaging'),
                'media': hasattr(capabilities, 'Media'),
                'ptz': hasattr(capabilities, 'PTZ')
            }
        except Exception as e:
            logger.error(f"Failed to get capabilities: {e}")
            return None
    
    def move_ptz(self, pan: float = 0.0, tilt: float = 0.0, zoom: float = 0.0) -> bool:
        """
        Move PTZ camera
        
        Args:
            pan: Pan value (-1.0 to 1.0)
            tilt: Tilt value (-1.0 to 1.0)
            zoom: Zoom value (-1.0 to 1.0)
            
        Returns:
            bool: True if successful, False otherwise
        """
        if not self.ptz_service:
            logger.error("PTZ service not available")
            return False
            
        try:
            # Get the first profile
            profiles = self.get_profiles()
            if not profiles:
                return False
            
            profile_token = profiles[0].token
            
            # Create PTZ request
            request = self.ptz_service.create_type('ContinuousMove')
            request.ProfileToken = profile_token
            request.Velocity = {
                'PanTilt': {'x': pan, 'y': tilt},
                'Zoom': {'x': zoom}
            }
            
            # Send move command
            self.ptz_service.ContinuousMove(request)
            logger.info(f"PTZ moved: pan={pan}, tilt={tilt}, zoom={zoom}")
            return True
            
        except Exception as e:
            logger.error(f"Failed to move PTZ: {e}")
            return False
    
    def stop_ptz(self) -> bool:
        """
        Stop PTZ movement
        
        Returns:
            bool: True if successful, False otherwise
        """
        if not self.ptz_service:
            logger.error("PTZ service not available")
            return False
            
        try:
            profiles = self.get_profiles()
            if not profiles:
                return False
            
            profile_token = profiles[0].token
            
            request = self.ptz_service.create_type('Stop')
            request.ProfileToken = profile_token
            request.PanTilt = True
            request.Zoom = True
            
            self.ptz_service.Stop(request)
            logger.info("PTZ stopped")
            return True
            
        except Exception as e:
            logger.error(f"Failed to stop PTZ: {e}")
            return False
