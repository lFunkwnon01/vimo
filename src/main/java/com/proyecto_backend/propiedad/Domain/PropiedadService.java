package com.proyecto_backend.propiedad.Domain;

import com.proyecto_backend.Excepctions.ResourceNotFoundException;
import com.proyecto_backend.propiedad.Dto.PropiedadRequest;
import com.proyecto_backend.propiedad.Dto.PropiedadResponse;
import com.proyecto_backend.propiedad.Repository.PropiedadRepository;
import com.proyecto_backend.ubicaciones.domain.UbicacionGeografica;
import com.proyecto_backend.ubicaciones.domain.UbicacionService;
import com.proyecto_backend.usuario.Domain.Usuario;
import com.proyecto_backend.usuario.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropiedadService {

    private final PropiedadRepository propiedadRepository;
    private final UsuarioRepository usuarioRepository;
    private final UbicacionService ubicacionService;

    public PropiedadResponse crear(PropiedadRequest dto) {
        Usuario propietario = usuarioRepository.findById(dto.getPropietarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (propietario)", dto.getPropietarioId()));

        Propiedad propiedad = mapToEntity(dto, propietario);

        UbicacionGeografica ubicacion = ubicacionService.geocodificarDireccion(dto.getDireccion());
        propiedad.setUbicacion(ubicacion);

        // Por defecto, la propiedad no está verificada
        propiedad.setVerificada(false);

        return mapToResponse(propiedadRepository.save(propiedad));
    }

    public PropiedadResponse obtener(Long id) {
        Propiedad p = propiedadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad", id));
        return mapToResponse(p);
    }

    public List<PropiedadResponse> listar() {
        return propiedadRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PropiedadResponse actualizar(Long id, PropiedadRequest dto) {
        Propiedad p = propiedadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad", id));

        Usuario propietario = usuarioRepository.findById(dto.getPropietarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario (propietario)", dto.getPropietarioId()));

        p.setTitulo(dto.getTitulo());
        p.setDescripcion(dto.getDescripcion());
        p.setDireccion(dto.getDireccion());
        p.setTipo(dto.getTipo());
        p.setMetrosCuadrados(dto.getMetrosCuadrados());
        p.setPrecio(dto.getPrecio());
        p.setEstado(dto.getEstado());
        p.setPropietario(propietario);
        p.setImagenes(dto.getImagenes());
        // La verificación solo la puede cambiar el admin, no desde aquí

        return mapToResponse(propiedadRepository.save(p));
    }

    public void eliminar(Long id) {
        if (!propiedadRepository.existsById(id))
            throw new ResourceNotFoundException("Propiedad", id);
        propiedadRepository.deleteById(id);
    }

    private Propiedad mapToEntity(PropiedadRequest d, Usuario propietario) {
        return Propiedad.builder()
                .titulo(d.getTitulo())
                .descripcion(d.getDescripcion())
                .direccion(d.getDireccion())
                .tipo(d.getTipo())
                .metrosCuadrados(d.getMetrosCuadrados())
                .precio(d.getPrecio())
                .estado(d.getEstado())
                .propietario(propietario)
                .imagenes(d.getImagenes())
                .build();
    }

    private PropiedadResponse mapToResponse(Propiedad p) {
        return PropiedadResponse.builder()
                .id(p.getId())
                .titulo(p.getTitulo())
                .descripcion(p.getDescripcion())
                .direccion(p.getDireccion())
                .tipo(p.getTipo())
                .metrosCuadrados(p.getMetrosCuadrados())
                .precio(p.getPrecio())
                .estado(p.getEstado())
                .propietarioId(p.getPropietario().getId())
                .imagenes(p.getImagenes())
                .verificada(p.getVerificada())
                .creadoEn(p.getCreadoEn())
                .build();
    }

    public List<PropiedadResponse> listarPorPropietario(Long propietarioId) {
        return propiedadRepository.findByPropietarioId(propietarioId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PropiedadResponse> listarPorEstado(EstadoPropiedad estado) {
        return propiedadRepository.findByEstado(estado)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PropiedadResponse> listarPorTipo(TipoPropiedad tipo) {
        return propiedadRepository.findByTipo(tipo)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PropiedadResponse> listarPorPropietarioYEstado(Long propietarioId, EstadoPropiedad estado) {
        return propiedadRepository.findByPropietarioIdAndEstado(propietarioId, estado)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PropiedadResponse> listarVerificadas() {
        return propiedadRepository.findByVerificadaTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PropiedadResponse> listarNoVerificadas() {
        return propiedadRepository.findByVerificadaFalse()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}