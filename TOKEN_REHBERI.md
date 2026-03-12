# ONVIF Token Parametreleri Rehberi

## Token Nedir?

ONVIF protokolünde **token**'lar, kameradaki belirli kaynakları referans etmek için kullanılan benzersiz tanımlayıcılardır. Bunları kamera sisteminin belirli bileşenleriyle etkileşime girmenizi sağlayan ID'ler olarak düşünebilirsiniz.

## Token Türleri

1. **ProfileToken** - Bir medya profilini (video/ses konfigürasyonlarının kombinasyonu) tanımlar
2. **VideoSourceToken** - Belirli bir video kaynağını (kamera girişi) tanımlar
3. **PresetToken** - Kayıtlı bir PTZ preset pozisyonunu tanımlar
4. **ConfigurationToken** - Bir konfigürasyonu (encoder, PTZ, vb.) tanımlar
5. **NetworkInterfaceToken** - Bir ağ arayüzünü tanımlar

## Token'ları Nasıl Elde Ederim?

Token'lar, kullanılabilir kaynakları sorguladığınızda kamera tarafından döndürülür. Her token türünü nasıl alacağınız aşağıda açıklanmıştır:

### 1. Video Source Token Almak

```bash
# GetVideoSources endpoint'ini çağırın
curl -X GET http://localhost:8081/api/onvif/media/video-sources

# Yanıt örneği:
[
  {
    "token": "VideoSource_1",  # Bu sizin videoSourceToken'ınızdır
    "framerate": 30.0,
    "resolution": {...}
  }
]
```

### 2. Profile Token Almak

```bash
# Profil bilgilerini almak için GetVideoEncoderConfigurations'ı çağırın
curl -X GET http://localhost:8081/api/onvif/media/encoder-configurations

# Yanıt, konfigürasyon nesnelerinde token'ları içerir
# Not: Gelecek sürümlerde özel bir GetProfiles endpoint'i eklenebilir
```

### 3. Preset Token Almak

```bash
# profileToken ile GetPresets'i çağırın
curl -X GET "http://localhost:8081/api/onvif/ptz/presets?profileToken=Profile_1"

# Yanıt örneği:
[
  {
    "token": "Preset_1",  # Bu sizin presetToken'ınızdır
    "name": "Ana Pozisyon",
    "ptzPosition": {...}
  }
]
```

### 4. Network Interface Token Almak

```bash
# GetNetworkInterfaces endpoint'ini çağırın
curl -X GET http://localhost:8081/api/onvif/network/interfaces

# Yanıt örneği:
[
  {
    "token": "eth0",  # Bu sizin network interface token'ınızdır
    "enabled": true,
    "info": {...}
  }
]
```

## Yaygın Kullanım Örnekleri

### Örnek 1: PTZ Kontrolü
```bash
# Adım 1: profileToken bulmak için mevcut profilleri alın
curl -X GET http://localhost:8081/api/onvif/media/video-sources

# Adım 2: PTZ işlemleri için profileToken'ı kullanın
curl -X POST "http://localhost:8081/api/onvif/ptz/continuous-move?profileToken=Profile_1" \
  -H "Content-Type: application/json" \
  -d '{
    "panTilt": {"x": 0.5, "y": 0.0, "space": "http://www.onvif.org/ver10/tptz/PanTiltSpaces/VelocityGenericSpace"},
    "zoom": {"x": 0.0, "space": "http://www.onvif.org/ver10/tptz/ZoomSpaces/VelocityGenericSpace"}
  }'
```

### Örnek 2: Görüntü Ayarları
```bash
# Adım 1: Video source token'ı alın
curl -X GET http://localhost:8081/api/onvif/media/video-sources
# "token" alanını not edin (örn. "VideoSource_1")

# Adım 2: Görüntüleme ayarlarını almak/ayarlamak için videoSourceToken kullanın
curl -X GET "http://localhost:8081/api/onvif/imaging/settings?videoSourceToken=VideoSource_1"

# Adım 3: Görüntüleme ayarlarını güncelleyin
curl -X POST "http://localhost:8081/api/onvif/imaging/settings?videoSourceToken=VideoSource_1" \
  -H "Content-Type: application/json" \
  -d '{
    "brightness": 50.0,
    "contrast": 50.0,
    "saturation": 50.0
  }'
```

### Örnek 3: Preset'lerle Çalışmak
```bash
# Adım 1: profileToken alın (video kaynaklarından veya profillerden)
# Adım 2: Mevcut pozisyonda yeni bir preset oluşturun
curl -X POST "http://localhost:8081/api/onvif/ptz/presets?profileToken=Profile_1&presetName=BenimPresetim"
# Döndürür: "Preset_1" (bu sizin yeni presetToken'ınızdır)

# Adım 3: Daha sonra preset'e gidin
curl -X POST "http://localhost:8081/api/onvif/ptz/presets/goto?profileToken=Profile_1&presetToken=Preset_1" \
  -H "Content-Type: application/json" \
  -d '{"panTilt": {"x": 0.5, "y": 0.5}}'
```

## Java Kod Örnekleri

### Örnek 1: Video Source Token Kullanımı

```java
@RestController
public class MyController {
    
    @Autowired
    private OnvifMediaService mediaService;
    
    @Autowired
    private OnvifImagingService imagingService;
    
    @GetMapping("/adjust-brightness")
    public void adjustBrightness() {
        // 1. Video source'ları al
        List<VideoSource> sources = mediaService.getVideoSources();
        
        // 2. İlk video source'un token'ını al
        String videoSourceToken = sources.get(0).getToken();
        
        // 3. Mevcut ayarları al
        ImageSettings settings = imagingService.getImageSettings(videoSourceToken);
        
        // 4. Parlaklığı ayarla
        settings.setBrightness(75.0f);
        imagingService.setImageSettings(videoSourceToken, settings);
    }
}
```

### Örnek 2: PTZ Preset Kullanımı

```java
@RestController
public class PTZController {
    
    @Autowired
    private OnvifPTZService ptzService;
    
    @PostMapping("/save-and-goto-preset")
    public void saveAndGotoPreset(@RequestParam String profileToken) {
        // 1. Mevcut pozisyonda yeni preset oluştur
        String presetToken = ptzService.setPreset(profileToken, "MyHomePosition", null);
        
        // 2. Kamerayı hareket ettir
        // ... bazı PTZ hareketleri ...
        
        // 3. Kayıtlı preset'e geri dön
        PTZVector speed = new PTZVector();
        // speed yapılandırması...
        ptzService.gotoPreset(profileToken, presetToken, speed);
    }
}
```

### Örnek 3: Network Interface Token Kullanımı

```java
@RestController
public class NetworkController {
    
    @Autowired
    private OnvifNetworkService networkService;
    
    @PostMapping("/configure-network")
    public void configureNetwork() {
        // 1. Mevcut network interface'leri al
        List<NetworkInterface> interfaces = networkService.getNetworkInterfaces();
        
        // 2. İlk interface'in token'ını al
        String token = interfaces.get(0).getToken();
        
        // 3. Network ayarlarını güncelle
        NetworkInterface config = new NetworkInterface();
        // konfigürasyon ayarla...
        networkService.setNetworkInterfaces(token, config);
    }
}
```

## Önemli Notlar

- Token'lar kameraya özgüdür ve farklı kamera modelleri arasında değişiklik gösterebilir
- Token'lar genellikle yeniden başlatmalarda kalıcıdır ancak fabrika sıfırlamasından sonra değişebilir
- Bazı kameralar sıralı numaralandırma kullanır (Profile_1, Profile_2), diğerleri GUID kullanır
- Token'ları hard-code etmek yerine her zaman kullanılabilir token'ları sorgulayın
- Token'lar büyük/küçük harf duyarlıdır

## Sık Sorulan Sorular

**S: "Geçersiz Token" hataları alıyorum**
- Uygun Get* endpoint'ini çağırarak token'ın var olduğunu doğrulayın
- İşlem için doğru token türünü kullandığınızdan emin olun
- Token'ın silinmediğini kontrol edin (preset'ler, konfigürasyonlar için)

**S: İlk profileToken'ı nerede bulabilirim?**
- `/api/onvif/media/video-sources` veya `/api/onvif/media/encoder-configurations` çağrısı yapın
- İlk sonuçtan token'ı kullanın
- Çoğu kamerada en az bir varsayılan profil vardır

**S: Özel token'lar oluşturabilir miyim?**
- Hayır, token'lar kamera tarafından oluşturulur
- Yalnızca kameranın sağladığı token'ları kullanabilirsiniz
- Bazı işlemler (SetPreset gibi) kaynak oluştururken yeni token'lar döndürür

**S: Token'lar değişir mi?**
- Genellikle kalıcıdırlar ancak şu durumlarda değişebilirler:
  - Fabrika sıfırlaması
  - Firmware güncellemesi
  - Konfigürasyon değişiklikleri
  - Kaynak (preset, profil) silinip yeniden oluşturulması

## REST API Endpoint'leri ve Token Gereksinimleri

### Device Management
- `GET /api/onvif/device/information` - Token gerektirmez
- `GET /api/onvif/device/services` - Token gerektirmez

### Media Operations
- `GET /api/onvif/media/video-sources` - Token gerektirmez (token'ları döndürür)
- `GET /api/onvif/media/encoder-configurations` - Token gerektirmez (token'ları döndürür)
- `POST /api/onvif/media/configuration` - Gerektirir: `profileToken`, `configurationToken`

### PTZ Control
- `POST /api/onvif/ptz/continuous-move` - Gerektirir: `profileToken`
- `POST /api/onvif/ptz/stop` - Gerektirir: `profileToken`
- `GET /api/onvif/ptz/status` - Gerektirir: `profileToken`
- `GET /api/onvif/ptz/presets` - Gerektirir: `profileToken` (presetToken'ları döndürür)
- `POST /api/onvif/ptz/presets` - Gerektirir: `profileToken`, İsteğe bağlı: `presetToken`
- `POST /api/onvif/ptz/presets/goto` - Gerektirir: `profileToken`, `presetToken`
- `DELETE /api/onvif/ptz/presets/{presetToken}` - Gerektirir: `profileToken`, `presetToken`

### Imaging Operations
- `GET /api/onvif/imaging/settings` - Gerektirir: `videoSourceToken`
- `POST /api/onvif/imaging/settings` - Gerektirir: `videoSourceToken`
- `GET /api/onvif/imaging/options` - Gerektirir: `videoSourceToken`

### Network Configuration
- `GET /api/onvif/network/interfaces` - Token gerektirmez (token'ları döndürür)
- `POST /api/onvif/network/interfaces/{token}` - Gerektirir: `networkInterfaceToken` (path'te)

## Sorun Giderme İpuçları

1. **Token'ı bulamıyorum**: İlgili Get* metodunu çağırarak başlayın
2. **Token geçersiz**: Token'ın hala var olduğunu doğrulayın
3. **Hangi token'ı kullanmalıyım**: API dokümantasyonundaki yukarıdaki tabloya bakın
4. **Token formatı nedir**: Kameraya bağlı olarak değişir, her zaman API'den alın

## İleri Okuma

- ONVIF Core Specification: https://www.onvif.org/specs/core/ONVIF-Core-Specification.pdf
- ONVIF Profile T Specification: https://www.onvif.org/specs/stream/ONVIF-Streaming-Spec.pdf
- Proje README: README.md
