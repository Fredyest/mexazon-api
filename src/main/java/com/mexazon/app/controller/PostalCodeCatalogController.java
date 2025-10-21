package com.mexazon.app.controller;

import com.mexazon.app.model.PostalCodeCatalog;
import com.mexazon.app.model.PostalCodeId;
import com.mexazon.app.service.impl.PostalCodeCatalogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión del catálogo de códigos postales ({@link PostalCodeCatalog}).
 *
 * Base path: {@code /api/postal-codes}
 *
 * Endpoints:
 * <ul>
 *   <li>Listar todos los registros del catálogo.</li>
 *   <li>Obtener un registro por clave compuesta (código postal + colonia).</li>
 *   <li>Crear un registro nuevo.</li>
 *   <li>Actualizar un registro existente.</li>
 *   <li>Eliminar un registro por su clave compuesta.</li>
 * </ul>
 *
 * Notas:
 * - La entidad usa clave compuesta {@link PostalCodeId} (postalCode + colonia).
 * - Las validaciones/actualizaciones de campos viven en {@link PostalCodeCatalogService}.
 */
@RestController
@RequestMapping("/api/postal-codes")
public class PostalCodeCatalogController {

    private final PostalCodeCatalogService service;

    public PostalCodeCatalogController(PostalCodeCatalogService service) {
        this.service = service;
    }

    // ---------- READ ----------
    /**
     * Lista todos los registros del catálogo de códigos postales.
     *
     * Método/URL: GET /api/postal-codes
     *
     * Responses:
     * - 200 OK: lista de registros
     *
     * Ejemplo:
     * <pre>
     * curl "http://localhost:8080/api/postal-codes"
     * </pre>
     */
    @GetMapping
    public ResponseEntity<List<PostalCodeCatalog>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    /**
     * Obtiene un registro del catálogo por su clave compuesta.
     *
     * Método/URL: GET /api/postal-codes/{postalCode}/{colonia}
     *
     * Path params:
     * - {@code postalCode}: código postal (ej. "01000")
     * - {@code colonia}: nombre de la colonia (URL-encoded si lleva espacios)
     *
     * Responses:
     * - 200 OK: registro encontrado
     * - 404 Not Found: si no existe
     *
     * Ejemplo:
     * <pre>
     * curl "http://localhost:8080/api/postal-codes/01000/San%20Ángel"
     * </pre>
     */
    @GetMapping("/{postalCode}/{colonia}")
    public ResponseEntity<PostalCodeCatalog> findById(@PathVariable String postalCode,
                                                      @PathVariable String colonia) {
        PostalCodeCatalog pc = service.findById(postalCode, colonia)
                .orElseThrow(() -> new IllegalArgumentException(
                        "PostalCodeCatalog no encontrado con id [" + postalCode + ", " + colonia + "]"));
        return ResponseEntity.ok(pc);
    }

    // ---------- CREATE ----------
    /**
     * Crea un nuevo registro en el catálogo.
     *
     * Método/URL: POST /api/postal-codes
     *
     * Body:
     * <pre>
     * {
     *   "postalCodeId": {
     *     "postalCode": "01000",
     *     "colonia": "San Ángel"
     *   },
     *   "alcaldia": "Álvaro Obregón"
     * }
     * </pre>
     *
     * Responses:
     * - 201 Created: registro creado
     * - 400 Bad Request: validaciones fallidas (si las agregas en el servicio)
     *
     * Ejemplo:
     * <pre>
     * curl -X POST "http://localhost:8080/api/postal-codes" \
     *   -H "Content-Type: application/json" \
     *   -d '{
     *         "postalCodeId":{"postalCode":"01000","colonia":"San Ángel"},
     *         "alcaldia":"Álvaro Obregón"
     *       }'
     * </pre>
     */
    @PostMapping
    public ResponseEntity<PostalCodeCatalog> create(@RequestBody CreatePostalCodeRequest request) {
        PostalCodeCatalog entity = new PostalCodeCatalog();
        entity.setId(request.postalCodeId);
        entity.setAlcaldia(request.alcaldia);
        // Agrega más setters si tu modelo incluye otros campos

        PostalCodeCatalog saved = service.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ---------- UPDATE ----------
    /**
     * Actualiza un registro existente del catálogo.
     *
     * Método/URL: PUT /api/postal-codes/{postalCode}/{colonia}
     *
     * Path params:
     * - {@code postalCode}: código postal
     * - {@code colonia}: colonia
     *
     * Body (campos opcionales; null conserva el valor actual en servicio si así lo implementas):
     * <pre>
     * {
     *   "alcaldia": "Nueva Alcaldía"
     * }
     * </pre>
     *
     * Responses:
     * - 200 OK: registro actualizado
     * - 404 Not Found: registro no encontrado
     *
     * Ejemplo:
     * <pre>
     * curl -X PUT "http://localhost:8080/api/postal-codes/01000/San%20Ángel" \
     *   -H "Content-Type: application/json" \
     *   -d '{"alcaldia":"Álvaro Obregón"}'
     * </pre>
     */
    @PutMapping("/{postalCode}/{colonia}")
    public ResponseEntity<PostalCodeCatalog> update(@PathVariable String postalCode,
                                                    @PathVariable String colonia,
                                                    @RequestBody UpdatePostalCodeRequest request) {
        PostalCodeCatalog updatedData = new PostalCodeCatalog();
        updatedData.setAlcaldia(request.alcaldia);
        // Agrega más setters si tu modelo incluye otros campos

        PostalCodeCatalog updated = service.update(postalCode, colonia, updatedData);
        return ResponseEntity.ok(updated);
    }

    // ---------- DELETE ----------
    /**
     * Elimina un registro del catálogo por su clave compuesta.
     *
     * Método/URL: DELETE /api/postal-codes/{postalCode}/{colonia}
     *
     * Path params:
     * - {@code postalCode}: código postal
     * - {@code colonia}: colonia
     *
     * Responses:
     * - 204 No Content: eliminado
     * - 404 Not Found: si no existe
     *
     * Ejemplo:
     * <pre>
     * curl -X DELETE "http://localhost:8080/api/postal-codes/01000/San%20Ángel"
     * </pre>
     */
    @DeleteMapping("/{postalCode}/{colonia}")
    public ResponseEntity<Void> delete(@PathVariable String postalCode,
                                       @PathVariable String colonia) {
        service.delete(postalCode, colonia);
        return ResponseEntity.noContent().build();
    }

    // ========= DTOs de request =========

    /**
     * Payload para crear un registro del catálogo de códigos postales.
     */
    public static class CreatePostalCodeRequest {
        /** Clave compuesta (código postal + colonia). */
        public PostalCodeId postalCodeId;
        /** Alcaldía/municipio asociado. */
        public String alcaldia;
    }

    /**
     * Payload para actualizar un registro del catálogo de códigos postales.
     */
    public static class UpdatePostalCodeRequest {
        /** Nueva alcaldía/municipio (opcional). */
        public String alcaldia;
    }
}