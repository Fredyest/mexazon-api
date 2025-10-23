package com.mexazon.app.service.impl;

import com.mexazon.app.model.Dish;
import com.mexazon.app.repository.DishRepository;
import com.mexazon.app.service.DishService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementación del servicio {@link DishService}.
 * <p>
 * Gestiona la lógica de negocio relacionada con los platillos ({@link Dish})
 * registrados por los negocios en Mexazón.
 * </p>
 *
 * <h3>Responsabilidades principales:</h3>
 * <ul>
 *   <li>Validar y registrar nuevos platillos.</li>
 *   <li>Consultar platillos por ID.</li>
 *   <li>Listar y filtrar platillos por negocio con criterios opcionales (categoría, texto, precios).</li>
 * </ul>
 *
 * <h3>Diseño e implementación:</h3>
 * <ul>
 *   <li>Utiliza {@link DishRepository} como capa de acceso a datos.</li>
 *   <li>Incluye validaciones de integridad antes de persistir un nuevo platillo:</li>
 *   <ul>
 *     <li>El precio debe ser mayor o igual a 0.</li>
 *     <li>No puede haber nombres duplicados dentro del mismo negocio.</li>
 *   </ul>
 *   <li>Los filtros de búsqueda se aplican en memoria usando streams de Java para mantener claridad.
 *       En escenarios de alto volumen de datos, se recomienda migrar a
 *       <strong>Specifications</strong> o <strong>QueryDSL</strong> con paginación.</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * // Crear un nuevo platillo
 * Dish nuevo = new Dish();
 * nuevo.setBusinessId(5L);
 * nuevo.setCategoryId(2L);
 * nuevo.setDishName("Tacos al Pastor");
 * nuevo.setDescription("Tacos tradicionales con piña");
 * nuevo.setPrice(new BigDecimal("45.00"));
 * dishService.create(nuevo);
 *
 * // Obtener un platillo por ID
 * Optional&lt;Dish&gt; dish = dishService.getById(10L);
 *
 * // Listar platillos filtrando por categoría y precio
 * List&lt;Dish&gt; lista = dishService.listByBusiness(5L, 2L, "taco", new BigDecimal("30"), new BigDecimal("100"));
 * </pre>
 */
@Service
public class DishServiceImpl implements DishService {

    /** Repositorio que gestiona las operaciones CRUD sobre los platillos. */
    private final DishRepository repo;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param repo repositorio JPA para la entidad {@link Dish}.
     */
    public DishServiceImpl(DishRepository repo) {
        this.repo = repo;
    }

    /**
     * Crea un nuevo platillo en el sistema, aplicando validaciones previas.
     *
     * @param dish entidad {@link Dish} a registrar.
     * @return el platillo persistido.
     * @throws IllegalArgumentException si el precio es nulo o menor que cero.
     * @throws DataIntegrityViolationException si ya existe un platillo con el mismo nombre en el negocio.
     */
    @Override
    public Dish create(Dish dish) {
        // Validar precio
        if (dish.getPrice() == null || dish.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be >= 0");
        }

        // Validar duplicado
        if (repo.existsByBusinessIdAndDishNameIgnoreCase(dish.getBusinessId(), dish.getDishName())) {
            throw new DataIntegrityViolationException("Dish name already exists for this business");
        }

        return repo.save(dish);
    }

    /**
     * Recupera un platillo por su identificador único.
     *
     * @param dishId identificador del platillo.
     * @return un {@link Optional} con el platillo si existe.
     */
    @Override
    public Optional<Dish> getById(Long dishId) {
        return repo.findById(dishId);
    }

    /**
     * Lista los platillos de un negocio aplicando filtros opcionales:
     * <ul>
     *   <li>Por categoría</li>
     *   <li>Por texto en nombre o descripción (búsqueda parcial, sin distinción de mayúsculas)</li>
     *   <li>Por rango de precios (mínimo y/o máximo)</li>
     * </ul>
     *
     * @param businessId identificador del negocio.
     * @param categoryId identificador de la categoría (opcional).
     * @param search texto de búsqueda en nombre o descripción (opcional).
     * @param minPrice precio mínimo (opcional).
     * @param maxPrice precio máximo (opcional).
     * @return lista filtrada de platillos.
     */
    @Override
    public List<Dish> listByBusiness(Long businessId,
                                     Long categoryId,
                                     String search,
                                     BigDecimal minPrice,
                                     BigDecimal maxPrice) {

        // Obtener lista base según categoría (si aplica)
        List<Dish> base = (categoryId != null)
                ? repo.findByBusinessIdAndCategoryId(businessId, categoryId)
                : repo.findByBusinessId(businessId);

        // Filtro por texto (nombre o descripción)
        if (search != null && !search.isBlank()) {
            String q = search.trim().toLowerCase();
            base = base.stream()
                    .filter(d -> (d.getDishName() != null && d.getDishName().toLowerCase().contains(q))
                              || (d.getDescription() != null && d.getDescription().toLowerCase().contains(q)))
                    .collect(Collectors.toList());
        }

        // Filtros de rango de precio
        if (minPrice != null) {
            base = base.stream()
                    .filter(d -> d.getPrice() != null && d.getPrice().compareTo(minPrice) >= 0)
                    .collect(Collectors.toList());
        }

        if (maxPrice != null) {
            base = base.stream()
                    .filter(d -> d.getPrice() != null && d.getPrice().compareTo(maxPrice) <= 0)
                    .collect(Collectors.toList());
        }

        return base;
    }
}