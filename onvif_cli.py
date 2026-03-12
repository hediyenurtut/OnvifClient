#!/usr/bin/env python
"""
ONVIF Client CLI
A command-line interface for the ONVIF Client
"""

import argparse
import sys
from onvif_client import ONVIFClient


def main():
    parser = argparse.ArgumentParser(description='ONVIF Camera Client CLI')
    parser.add_argument('--ip', required=True, help='Camera IP address')
    parser.add_argument('--port', type=int, default=80, help='ONVIF port (default: 80)')
    parser.add_argument('--username', required=True, help='Camera username')
    parser.add_argument('--password', required=True, help='Camera password')
    
    subparsers = parser.add_subparsers(dest='command', help='Command to execute')
    
    # Info command
    subparsers.add_parser('info', help='Get camera device information')
    
    # Capabilities command
    subparsers.add_parser('capabilities', help='Get camera capabilities')
    
    # Profiles command
    subparsers.add_parser('profiles', help='List media profiles')
    
    # Stream command
    stream_parser = subparsers.add_parser('stream', help='Get stream URI')
    stream_parser.add_argument('--profile', help='Profile token (optional)')
    
    # PTZ command
    ptz_parser = subparsers.add_parser('ptz', help='Control PTZ')
    ptz_parser.add_argument('--pan', type=float, default=0.0, help='Pan value (-1.0 to 1.0)')
    ptz_parser.add_argument('--tilt', type=float, default=0.0, help='Tilt value (-1.0 to 1.0)')
    ptz_parser.add_argument('--zoom', type=float, default=0.0, help='Zoom value (-1.0 to 1.0)')
    ptz_parser.add_argument('--stop', action='store_true', help='Stop PTZ movement')
    
    args = parser.parse_args()
    
    if not args.command:
        parser.print_help()
        sys.exit(1)
    
    # Create client and connect
    client = ONVIFClient(args.ip, args.port, args.username, args.password)
    
    if not client.connect():
        print("Failed to connect to camera")
        sys.exit(1)
    
    # Execute command
    if args.command == 'info':
        info = client.get_device_info()
        if info:
            print("\n=== Device Information ===")
            for key, value in info.items():
                print(f"{key}: {value}")
    
    elif args.command == 'capabilities':
        caps = client.get_capabilities()
        if caps:
            print("\n=== Camera Capabilities ===")
            for key, value in caps.items():
                print(f"{key}: {value}")
    
    elif args.command == 'profiles':
        profiles = client.get_profiles()
        if profiles:
            print("\n=== Media Profiles ===")
            for i, profile in enumerate(profiles):
                print(f"Profile {i+1}: {profile.Name} (Token: {profile.token})")
    
    elif args.command == 'stream':
        uri = client.get_stream_uri(args.profile if hasattr(args, 'profile') else None)
        if uri:
            print(f"\nRTSP Stream URI: {uri}")
    
    elif args.command == 'ptz':
        if args.stop:
            if client.stop_ptz():
                print("PTZ stopped")
            else:
                print("Failed to stop PTZ")
        else:
            if client.move_ptz(args.pan, args.tilt, args.zoom):
                print(f"PTZ moved: pan={args.pan}, tilt={args.tilt}, zoom={args.zoom}")
            else:
                print("Failed to move PTZ")


if __name__ == '__main__':
    main()
