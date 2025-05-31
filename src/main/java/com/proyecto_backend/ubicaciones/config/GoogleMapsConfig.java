package com.proyecto_backend.ubicaciones.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class GoogleMapsConfig {

    @Value("${google.maps.api.key}")
    private String apiKey;

    @Value("${google.maps.geocoding.url:https://maps.googleapis.com/maps/api/geocode/json}")
    private String geocodingUrl;

    @Value("${google.maps.places.url:https://maps.googleapis.com/maps/api/place/autocomplete/json}")
    private String placesUrl;
}