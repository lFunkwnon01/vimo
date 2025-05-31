package com.proyecto_backend.ubicaciones.domain;


import com.proyecto_backend.ubicaciones.config.GoogleMapsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UbicacionService {

    private final GoogleMapsConfig googleMapsConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    public UbicacionGeografica geocodificarDireccion(String direccion) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(googleMapsConfig.getGeocodingUrl())
                    .queryParam("address", direccion + ", Perú")
                    .queryParam("key", googleMapsConfig.getApiKey())
                    .queryParam("language", "es")
                    .queryParam("region", "pe")
                    .build()
                    .toUriString();

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && "OK".equals(response.get("status"))) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

                if (!results.isEmpty()) {
                    return extraerUbicacion(results.get(0), direccion);
                }
            }

            log.warn("No se pudo geocodificar la dirección: {}", direccion);
            return crearUbicacionBasica(direccion);

        } catch (Exception e) {
            log.error("Error al geocodificar dirección: {}", direccion, e);
            return crearUbicacionBasica(direccion);
        }
    }

    private UbicacionGeografica extraerUbicacion(Map<String, Object> result, String direccionOriginal) {
        // Extraer coordenadas
        Map<String, Object> geometry = (Map<String, Object>) result.get("geometry");
        Map<String, Object> location = (Map<String, Object>) geometry.get("location");
        Double lat = (Double) location.get("lat");
        Double lng = (Double) location.get("lng");

        // Extraer componentes de dirección
        List<Map<String, Object>> addressComponents = (List<Map<String, Object>>) result.get("address_components");

        String region = null;
        String provincia = null;
        String distrito = null;
        String codigoPostal = null;

        for (Map<String, Object> component : addressComponents) {
            List<String> types = (List<String>) component.get("types");
            String longName = (String) component.get("long_name");

            if (types.contains("administrative_area_level_1")) {
                region = longName; // Lima, Junín, etc.
            } else if (types.contains("administrative_area_level_2")) {
                provincia = longName; // Lima, Huancayo, etc.
            } else if (types.contains("locality") || types.contains("administrative_area_level_3")) {
                distrito = longName; // Barranco, El Tambo, etc.
            } else if (types.contains("postal_code")) {
                codigoPostal = longName;
            }
        }

        String direccionCompleta = (String) result.get("formatted_address");

        return UbicacionGeografica.builder()
                .direccionCompleta(direccionCompleta)
                .region(region)
                .provincia(provincia)
                .distrito(distrito)
                .latitud(lat)
                .longitud(lng)
                .codigoPostal(codigoPostal)
                .pais("Perú")
                .build();
    }

    private UbicacionGeografica crearUbicacionBasica(String direccion) {
        return UbicacionGeografica.builder()
                .direccionCompleta(direccion)
                .pais("Perú")
                .build();
    }

    public List<String> autocompletarDireccion(String input) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(googleMapsConfig.getPlacesUrl())
                    .queryParam("input", input)
                    .queryParam("key", googleMapsConfig.getApiKey())
                    .queryParam("language", "es")
                    .queryParam("components", "country:pe")
                    .build()
                    .toUriString();

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && "OK".equals(response.get("status"))) {
                List<Map<String, Object>> predictions = (List<Map<String, Object>>) response.get("predictions");

                return predictions.stream()
                        .map(prediction -> (String) prediction.get("description"))
                        .toList();
            }

        } catch (Exception e) {
            log.error("Error en autocompletado: {}", input, e);
        }

        return List.of();
    }
}