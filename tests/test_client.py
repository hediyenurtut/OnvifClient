"""
Unit tests for ONVIF Client
"""

import unittest
from unittest.mock import Mock, patch, MagicMock
from onvif_client import ONVIFClient


class TestONVIFClient(unittest.TestCase):
    """Test cases for ONVIFClient"""
    
    def setUp(self):
        """Set up test fixtures"""
        self.ip = "192.168.1.100"
        self.port = 80
        self.username = "admin"
        self.password = "admin"
        self.client = ONVIFClient(self.ip, self.port, self.username, self.password)
    
    def test_initialization(self):
        """Test client initialization"""
        self.assertEqual(self.client.ip, self.ip)
        self.assertEqual(self.client.port, self.port)
        self.assertEqual(self.client.username, self.username)
        self.assertEqual(self.client.password, self.password)
        self.assertIsNone(self.client.camera)
        self.assertIsNone(self.client.media_service)
        self.assertIsNone(self.client.ptz_service)
        self.assertIsNone(self.client.device_service)
    
    @patch('onvif_client.client.ONVIFCamera')
    def test_connect_success(self, mock_onvif_camera):
        """Test successful camera connection"""
        # Mock the camera and services
        mock_camera = Mock()
        mock_media_service = Mock()
        mock_device_service = Mock()
        mock_ptz_service = Mock()
        
        mock_camera.create_media_service.return_value = mock_media_service
        mock_camera.create_devicemgmt_service.return_value = mock_device_service
        mock_camera.create_ptz_service.return_value = mock_ptz_service
        
        mock_onvif_camera.return_value = mock_camera
        
        # Test connection
        result = self.client.connect()
        
        self.assertTrue(result)
        self.assertIsNotNone(self.client.camera)
        self.assertIsNotNone(self.client.media_service)
        self.assertIsNotNone(self.client.device_service)
        mock_onvif_camera.assert_called_once_with(
            self.ip, self.port, self.username, self.password
        )
    
    @patch('onvif_client.client.ONVIFCamera')
    def test_connect_failure(self, mock_onvif_camera):
        """Test failed camera connection"""
        mock_onvif_camera.side_effect = Exception("Connection error")
        
        result = self.client.connect()
        
        self.assertFalse(result)
    
    def test_get_device_info_not_connected(self):
        """Test get_device_info when not connected"""
        result = self.client.get_device_info()
        self.assertIsNone(result)
    
    @patch('onvif_client.client.ONVIFCamera')
    def test_get_device_info_success(self, mock_onvif_camera):
        """Test successful device info retrieval"""
        # Setup mock
        mock_camera = Mock()
        mock_device_service = Mock()
        
        mock_info = Mock()
        mock_info.Manufacturer = "TestManufacturer"
        mock_info.Model = "TestModel"
        mock_info.FirmwareVersion = "1.0.0"
        mock_info.SerialNumber = "12345"
        mock_info.HardwareId = "HW123"
        
        mock_device_service.GetDeviceInformation.return_value = mock_info
        mock_camera.create_devicemgmt_service.return_value = mock_device_service
        mock_camera.create_media_service.return_value = Mock()
        mock_onvif_camera.return_value = mock_camera
        
        # Connect and get info
        self.client.connect()
        result = self.client.get_device_info()
        
        self.assertIsNotNone(result)
        self.assertEqual(result['manufacturer'], "TestManufacturer")
        self.assertEqual(result['model'], "TestModel")
        self.assertEqual(result['firmware_version'], "1.0.0")
        self.assertEqual(result['serial_number'], "12345")
        self.assertEqual(result['hardware_id'], "HW123")
    
    def test_get_profiles_not_connected(self):
        """Test get_profiles when not connected"""
        result = self.client.get_profiles()
        self.assertIsNone(result)
    
    def test_get_stream_uri_not_connected(self):
        """Test get_stream_uri when not connected"""
        result = self.client.get_stream_uri()
        self.assertIsNone(result)
    
    def test_get_capabilities_not_connected(self):
        """Test get_capabilities when not connected"""
        result = self.client.get_capabilities()
        self.assertIsNone(result)
    
    def test_move_ptz_no_service(self):
        """Test move_ptz when PTZ service is not available"""
        result = self.client.move_ptz(pan=0.5)
        self.assertFalse(result)
    
    def test_stop_ptz_no_service(self):
        """Test stop_ptz when PTZ service is not available"""
        result = self.client.stop_ptz()
        self.assertFalse(result)


if __name__ == '__main__':
    unittest.main()
