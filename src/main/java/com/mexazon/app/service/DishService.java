package com.mexazon.app.service;

import com.mexazon.app.model.Dish;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de gestionar la lógica de negocio relacionada con los platillos
 * ({@link Dish}) registrados por los negocios en Mexazón.
 * <p>
 * Define las operaciones principales para la creación, consulta individual y filtrado
 * de platillos según diversos criterios (categoría, rango de precios o texto de búsqueda).
 * </p>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>Abstrae la capa de acceso a datos del {@code DishRepository} para encapsular reglas de negocio.</li>
 *   <li>Por ahora, solo se permite la creación y lectura; las operaciones de actualización o eliminación
 *       se implementarán más adelante según las políticas de negocio.</li>
 *   <li>El método {@code listByBusiness} permite búsquedas flexibles con parámetros opcionales,
 *       lo que facilita su uso desde APIs REST o filtros dinámicos en frontend.</li>
 * </ul>
 *
 * <h3>Responsabilidades del servicio:</h3>
 * <ul>
 *   <li>Registrar un nuevo platillo asociado a un negocio.</li>
 *   <li>Consultar información detallada de un platillo.</li>
 *   <li>Listar los platillos de un negocio con filtros opcionales.</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * // Crear un nuevo platillo
 * Dish d = new Dish();
 * d.setBusinessId(5L);
 * d.setDishName("Tacos de suadero");
 * d.setCategoryId(2L);
 * d.setPrice(new BigDecimal("45.00"));
 * dishService.create(d);
 *
 * // Buscar platillo por ID
 * Optional&lt;Dish&gt; platillo = dishService.getById(10L);
 *
 * // Listar platillos de un negocio con filtros opcionales
 * List&lt;Dish&gt; resultados = dishService.listByBusiness(5L, 2L, "taco", new BigDecimal("20"), new BigDecimal("100"));
 * </pre>
 */
public interface DishService {

    /**
     * Crea y registra un nuevo platillo en el sistema.
     *
     * @param dish entidad {@link Dish} con la información del platillo.
     * @return el platillo creado y persistido.
     */
    Dish create(Dish dish);

    /**
     * Recupera un platillo específico por su identificador único.
     *
     * @param dishId identificador del platillo.
     * @return un {@link Optional} conteniendo el platillo, si existe.
     */
    Optional<Dish> getById(Long dishId);

    /**
     * Lista los platillos registrados por un negocio, aplicando filtros opcionales.
     * <p>
     * Los filtros pueden incluir categoría, búsqueda parcial en nombre o descripción,
     * y un rango de precios mínimo y máximo.
     * </p>
     *
     * @param businessId identificador del negocio propietario.
     * @param categoryId identificador de la categoría (opcional).
     * @param search texto a buscar en nombre o descripción (opcional).
     * @param minPrice precio mínimo (opcional).
     * @param maxPrice precio máximo (opcional).
     * @return lista filtrada de platillos que cumplen los criterios especificados.
     */
    List<Dish> listByBusiness(Long businessId, Long categoryId, String search, BigDecimal minPrice, BigDecimal maxPrice);
}