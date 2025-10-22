package com.mexazon.app.controller;

import com.mexazon.app.service.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Controlador REST para consultar el catálogo de códigos postales,
 * colonias y alcaldías de la Ciudad de México (y potencialmente otras zonas).
 * <p>
 * Este endpoint es de solo lectura y permite al frontend llenar automáticamente
 * la información de dirección cuando el usuario selecciona un código postal
 * o una colonia específica.
 * </p>
 *
 * <h3>Responsabilidades principales:</h3>
 * <ul>
 *   <li>Consultar todas las colonias asociadas a un código postal.</li>
 *   <li>Consultar una entrada específica (CP + colonia) para obtener su alcaldía.</li>
 * </ul>
 *
 * <h3>Diseño e implementación:</h3>
 * <ul>
 *   <li>Utiliza el servicio {@link AddressService} para acceder a la información del catálogo.</li>
 *   <li>Opera únicamente con métodos <strong>GET</strong>, ya que el catálogo se carga desde el backend.</li>
 *   <li>Maneja errores con mensajes descriptivos y códigos HTTP adecuados:
 *     <ul>
 *       <li><b>404 Not Found</b>: cuando no existe el código postal o colonia solicitada.</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h3>Ejemplos de uso:</h3>
 *
 * <h4>➤ Consultar todas las colonias de un CP</h4>
 * <pre>
 * GET /api/postal-code-catalog?postalCode=04360
 * </pre>
 * <h4>Respuesta exitosa</h4>
 * <pre>
 * 200 OK
 * {
 *   "postalCode": "04360",
 *   "colonias": [
 *     { "colonia": "Coyoacán Centro", "alcaldia": "Coyoacán" },
 *     { "colonia": "Del Carmen", "alcaldia": "Coyoacán" }
 *   ]
 * }
 * </pre>
 *
 * <h4>➤ Consultar una colonia específica</h4>
 * <pre>
 * GET /api/postal-code-catalog/04360/Del%20Carmen
 * </pre>
 * <h4>Respuesta exitosa</h4>
 * <pre>
 * 200 OK
 * {
 *   "postalCode": "04360",
 *   "colonia": "Del Carmen",
 *   "alcaldia": "Coyoacán"
 * }
 * </pre>
 */
@RestController
@RequestMapping("/api/postal-code-catalog")
public class PostalCodeCatalogController {

    /** Servicio encargado de la lógica del catálogo de direcciones. */
    private final AddressService addressService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param addressService servicio que maneja consultas del catálogo postal.
     */
    public PostalCodeCatalogController(AddressService addressService) {
        this.addressService = addressService;
    }

    // ---------- GET /api/postal-code-catalog?postalCode=04360 ----------
    /**
     * Devuelve todas las colonias asociadas a un código postal.
     *
     * @param postalCode código postal a consultar.
     * @return {@code 200 OK} con lista de colonias y alcaldías,
     *         o {@code 404 Not Found} si no se encuentra el CP.
     */
    @GetMapping
    public ResponseEntity<?> getByPostalCode(@RequestParam String postalCode) {
        var list = addressService.findColoniasByPostalCode(postalCode);
        if (list.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Postal code not found"));
        }
        return ResponseEntity.ok(Map.of("postalCode", postalCode, "colonias", list));
    }

    // ---------- GET /api/postal-code-catalog/{postalCode}/{colonia} ----------
    /**
     * Devuelve una entrada exacta del catálogo (código postal + colonia).
     *
     * @param postalCode código postal
     * @param colonia nombre exacto de la colonia
     * @return {@code 200 OK} con los datos del registro, o
     *         {@code 404 Not Found} si no existe la combinación.
     */
    @GetMapping("/{postalCode}/{colonia}")
    public ResponseEntity<?> getExact(
            @PathVariable String postalCode,
            @PathVariable String colonia) {
        try {
            return ResponseEntity.ok(addressService.getCatalogEntry(postalCode, colonia));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}