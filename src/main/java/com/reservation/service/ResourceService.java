package com.reservation.service;

import com.reservation.model.dto.ResourceRequest;
import com.reservation.model.dto.ResourceResponse;
import com.reservation.model.entity.Resource;
import com.reservation.repository.ResourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceService {
    
    private final ResourceRepository resourceRepository;
    
    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }
    
    // Listar todos los recursos activos
    public List<ResourceResponse> getAllActiveResources() {
        return resourceRepository.findByIsActiveTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    // Obtener recurso por ID
    public ResourceResponse getResourceById(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado con ID: " + id));
        return toResponse(resource);
    }
    
    // Crear nuevo recurso (solo ADMIN)
    @Transactional
    public ResourceResponse createResource(ResourceRequest request) {
        Resource resource = new Resource();
        resource.setName(request.getName());
        resource.setDescription(request.getDescription());
        resource.setCapacity(request.getCapacity());
        resource.setLocation(request.getLocation());
        resource.setIsActive(true);
        
        Resource saved = resourceRepository.save(resource);
        return toResponse(saved);
    }
    
    // Actualizar recurso (solo ADMIN)
    @Transactional
    public ResourceResponse updateResource(Long id, ResourceRequest request) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado con ID: " + id));
        
        resource.setName(request.getName());
        resource.setDescription(request.getDescription());
        resource.setCapacity(request.getCapacity());
        resource.setLocation(request.getLocation());
        
        Resource updated = resourceRepository.save(resource);
        return toResponse(updated);
    }
    
    // Eliminar recurso (soft delete - solo ADMIN)
    @Transactional
    public void deleteResource(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado con ID: " + id));
        resource.setIsActive(false);
        resourceRepository.save(resource);
    }
    
    // Método privado para convertir Entity → DTO
    private ResourceResponse toResponse(Resource resource) {
        return new ResourceResponse(
                resource.getId(),
                resource.getName(),
                resource.getDescription(),
                resource.getCapacity(),
                resource.getLocation(),
                resource.getIsActive(),
                resource.getCreatedAt()
        );
    }
}