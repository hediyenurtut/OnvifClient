package com.onvif.client.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onvif.client.model.device.*;
import com.onvif.client.model.network.*;
import com.onvif.client.model.ptz.*;
import com.onvif.client.model.media.*;
import com.onvif.client.model.imaging.*;
import com.onvif.client.service.*;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for ONVIF Client Operations
 * Provides endpoints to interact with ONVIF cameras
 */
@RestController
@RequestMapping("/api/onvif")
@RequiredArgsConstructor
public class OnvifController {
    private static final Logger log = LoggerFactory.getLogger(OnvifController.class);

    private final OnvifDeviceService deviceService;
    private final OnvifNetworkService networkService;
    private final OnvifPTZService ptzService;
    private final OnvifMediaService mediaService;
    private final OnvifImagingService imagingService;
    
    // ====== Device Management Endpoints ======
    
    @GetMapping("/device/information")
    public ResponseEntity<DeviceInformation> getDeviceInformation() {
        log.info("GET /api/onvif/device/information");
        DeviceInformation info = deviceService.getDeviceInformation();
        return ResponseEntity.ok(info);
    }
    
    @GetMapping("/device/services")
    public ResponseEntity<List<com.onvif.client.model.device.Service>> getServices(
            @RequestParam(defaultValue = "true") boolean includeCapability) {
        log.info("GET /api/onvif/device/services?includeCapability={}", includeCapability);
        List<com.onvif.client.model.device.Service> services = deviceService.getServices(includeCapability);
        return ResponseEntity.ok(services);
    }
    
    @GetMapping("/device/capabilities")
    public ResponseEntity<ServiceCapabilities> getServiceCapabilities() {
        log.info("GET /api/onvif/device/capabilities");
        ServiceCapabilities capabilities = deviceService.getServiceCapabilities();
        return ResponseEntity.ok(capabilities);
    }
    
    @GetMapping("/device/hostname")
    public ResponseEntity<HostnameInformation> getHostname() {
        log.info("GET /api/onvif/device/hostname");
        HostnameInformation hostname = deviceService.getHostname();
        return ResponseEntity.ok(hostname);
    }
    
    @PostMapping("/device/hostname")
    public ResponseEntity<Void> setHostname(@RequestBody String hostname) {
        log.info("POST /api/onvif/device/hostname: {}", hostname);
        deviceService.setHostname(hostname);
        return ResponseEntity.ok().build();
    }
    
    // ====== Network Management Endpoints ======
    
    @GetMapping("/network/interfaces")
    public ResponseEntity<List<NetworkInterface>> getNetworkInterfaces() {
        log.info("GET /api/onvif/network/interfaces");
        List<NetworkInterface> interfaces = networkService.getNetworkInterfaces();
        return ResponseEntity.ok(interfaces);
    }
    
    @PostMapping("/network/interfaces/{token}")
    public ResponseEntity<Void> setNetworkInterface(
            @PathVariable String token,
            @RequestBody NetworkInterface networkInterface) {
        log.info("POST /api/onvif/network/interfaces/{}", token);
        networkService.setNetworkInterfaces(token, networkInterface);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/network/gateway")
    public ResponseEntity<NetworkGateway> getNetworkDefaultGateway() {
        log.info("GET /api/onvif/network/gateway");
        NetworkGateway gateway = networkService.getNetworkDefaultGateway();
        return ResponseEntity.ok(gateway);
    }
    
    @PostMapping("/network/gateway")
    public ResponseEntity<Void> setNetworkDefaultGateway(
            @RequestParam(required = false) String ipv4,
            @RequestParam(required = false) String ipv6) {
        log.info("POST /api/onvif/network/gateway");
        networkService.setNetworkDefaultGateway(ipv4, ipv6);
        return ResponseEntity.ok().build();
    }
    
    // ====== PTZ Control Endpoints ======
    
    @PostMapping("/ptz/continuous-move")
    public ResponseEntity<Void> continuousMove(
            @RequestParam String profileToken,
            @RequestBody PTZVector velocity) {
        log.info("POST /api/onvif/ptz/continuous-move");
        ptzService.continuousMove(profileToken, velocity);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/ptz/stop")
    public ResponseEntity<Void> stop(
            @RequestParam String profileToken,
            @RequestParam(defaultValue = "true") boolean panTilt,
            @RequestParam(defaultValue = "true") boolean zoom) {
        log.info("POST /api/onvif/ptz/stop");
        ptzService.stop(profileToken, panTilt, zoom);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/ptz/status")
    public ResponseEntity<PTZStatus> getStatus(@RequestParam String profileToken) {
        log.info("GET /api/onvif/ptz/status");
        PTZStatus status = ptzService.getStatus(profileToken);
        return ResponseEntity.ok(status);
    }
    
    @PostMapping("/ptz/home/set")
    public ResponseEntity<Void> setHomePosition(@RequestParam String profileToken) {
        log.info("POST /api/onvif/ptz/home/set");
        ptzService.setHomePosition(profileToken);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/ptz/home/goto")
    public ResponseEntity<Void> gotoHomePosition(
            @RequestParam String profileToken,
            @RequestBody PTZVector speed) {
        log.info("POST /api/onvif/ptz/home/goto");
        ptzService.gotoHomePosition(profileToken, speed);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/ptz/presets")
    public ResponseEntity<List<PTZPreset>> getPresets(@RequestParam String profileToken) {
        log.info("GET /api/onvif/ptz/presets");
        List<PTZPreset> presets = ptzService.getPresets(profileToken);
        return ResponseEntity.ok(presets);
    }
    
    @PostMapping("/ptz/presets")
    public ResponseEntity<String> setPreset(
            @RequestParam String profileToken,
            @RequestParam String presetName,
            @RequestParam(required = false) String presetToken) {
        log.info("POST /api/onvif/ptz/presets");
        String token = ptzService.setPreset(profileToken, presetName, presetToken);
        return ResponseEntity.ok(token);
    }
    
    @PostMapping("/ptz/presets/goto")
    public ResponseEntity<Void> gotoPreset(
            @RequestParam String profileToken,
            @RequestParam String presetToken,
            @RequestBody PTZVector speed) {
        log.info("POST /api/onvif/ptz/presets/goto");
        ptzService.gotoPreset(profileToken, presetToken, speed);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/ptz/presets/{presetToken}")
    public ResponseEntity<Void> removePreset(
            @RequestParam String profileToken,
            @PathVariable String presetToken) {
        log.info("DELETE /api/onvif/ptz/presets/{}", presetToken);
        ptzService.removePreset(profileToken, presetToken);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/ptz/move-options")
    public ResponseEntity<PTZConfigurationOptions> getMoveOptions(
            @RequestParam String profileToken) {
        log.info("GET /api/onvif/ptz/move-options");
        PTZConfigurationOptions options = ptzService.getMoveOptions(profileToken);
        return ResponseEntity.ok(options);
    }
    
    // ====== Media Endpoints ======
    
    @GetMapping("/media/video-sources")
    public ResponseEntity<List<VideoSource>> getVideoSources() {
        log.info("GET /api/onvif/media/video-sources");
        List<VideoSource> sources = mediaService.getVideoSources();
        return ResponseEntity.ok(sources);
    }
    
    @GetMapping("/media/encoder-configurations")
    public ResponseEntity<List<VideoEncoderConfiguration>> getVideoEncoderConfigurations() {
        log.info("GET /api/onvif/media/encoder-configurations");
        List<VideoEncoderConfiguration> configs = mediaService.getVideoEncoderConfigurations();
        return ResponseEntity.ok(configs);
    }
    
    @PostMapping("/media/configuration")
    public ResponseEntity<Void> addConfiguration(
            @RequestParam String profileToken,
            @RequestParam String name,
            @RequestParam String configurationToken) {
        log.info("POST /api/onvif/media/configuration");
        mediaService.addConfiguration(profileToken, name, configurationToken);
        return ResponseEntity.ok().build();
    }
    
    // ====== Imaging Endpoints ======
    
    @GetMapping("/imaging/settings")
    public ResponseEntity<ImageSettings> getImageSettings(
            @RequestParam String videoSourceToken) {
        log.info("GET /api/onvif/imaging/settings");
        ImageSettings settings = imagingService.getImageSettings(videoSourceToken);
        return ResponseEntity.ok(settings);
    }
    
    @PostMapping("/imaging/settings")
    public ResponseEntity<Void> setImageSettings(
            @RequestParam String videoSourceToken,
            @RequestBody ImageSettings settings) {
        log.info("POST /api/onvif/imaging/settings");
        imagingService.setImageSettings(videoSourceToken, settings);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/imaging/options")
    public ResponseEntity<ImagingOptions> getImageOptions(
            @RequestParam String videoSourceToken) {
        log.info("GET /api/onvif/imaging/options");
        ImagingOptions options = imagingService.getOptions(videoSourceToken);
        return ResponseEntity.ok(options);
    }
}
