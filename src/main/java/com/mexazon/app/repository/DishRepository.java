package com.mexazon.app.repository;

import com.mexazon.app.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio JPA para la entidad {@link Dish}.
 * <p>
 * Permite acceder y manipular los platillos registrados por los negocios
 * dentro de la plataforma Mexazón. Incluye métodos personalizados para
 * realizar búsquedas filtradas por negocio, categoría, nombre o descripción.
 * </p>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>Extiende {@link JpaRepository} para heredar operaciones CRUD básicas.</li>
 *   <li>Utiliza consultas derivadas de método (Spring Data JPA Query Derivation)
 *       para simplificar las búsquedas más comunes.</li>
 *   <li>Incluye validación de duplicados por negocio para garantizar unicidad
 *       de nombres de platillos dentro del mismo establecimiento.</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * // Obtener todos los platillos de un negocio
 * List&lt;Dish&gt; menu = dishRepository.findByBusinessId(5L);
 *
 * // Buscar platillos de una categoría específica
 * List&lt;Dish&gt; tacos = dishRepository.findByBusinessIdAndCategoryId(5L, 2L);
 *
 * // Buscar platillos que contengan cierta palabra en el nombre
 * List&lt;Dish&gt; results = dishRepository.findByBusinessIdAndDishNameContainingIgnoreCase(5L, "taco");
 *
 * // Verificar si un negocio ya tiene un platillo con cierto nombre
 * boolean exists = dishRepository.existsByBusinessIdAndDishNameIgnoreCase(5L, "Tacos al Pastor");
 * </pre>
 *
 * <p><strong>Entidad:</strong> {@code Dish}</p>
 * <p><strong>Clave primaria:</strong> {@code Long}</p>
 */
@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
<<<<<<< HEAD
	  List<Dish> findAllByCategory(MenuCategory category);
	  List<Dish> findAllByBusiness(Business business);
	  List<Dish> findAllByCategoryCategoryId(Long categoryId);
	  List<Dish> findAllByBusinessBusinessId(Long businessId);
}
=======

    /**
     * Obtiene todos los platillos registrados por un negocio.
     *
     * @param businessId identificador único del negocio.
     * @return lista de platillos asociados al negocio.
     */
    List<Dish> findByBusinessId(Long businessId);

    /**
     * Obtiene todos los platillos de un negocio filtrados por categoría.
     *
     * @param businessId identificador del negocio.
     * @param categoryId identificador de la categoría.
     * @return lista de platillos dentro de esa categoría.
     */
    List<Dish> findByBusinessIdAndCategoryId(Long businessId, Long categoryId);

    /**
     * Busca platillos por coincidencia parcial en el nombre (ignora mayúsculas/minúsculas).
     *
     * @param businessId identificador del negocio.
     * @param namePart parte del nombre a buscar.
     * @return lista de platillos cuyos nombres contienen la cadena especificada.
     */
    List<Dish> findByBusinessIdAndDishNameContainingIgnoreCase(Long businessId, String namePart);

    /**
     * Busca platillos por coincidencia parcial en la descripción (ignora mayúsculas/minúsculas).
     *
     * @param businessId identificador del negocio.
     * @param descPart parte del texto a buscar en la descripción.
     * @return lista de platillos con descripciones que contienen la cadena indicada.
     */
    List<Dish> findByBusinessIdAndDescriptionContainingIgnoreCase(Long businessId, String descPart);

    /**
     * Verifica si ya existe un platillo con el mismo nombre dentro de un negocio.
     * <p>
     * Útil para prevenir duplicados al crear o actualizar registros.
     * </p>
     *
     * @param businessId identificador del negocio.
     * @param dishName nombre del platillo (sin importar mayúsculas/minúsculas).
     * @return {@code true} si existe un platillo con ese nombre, de lo contrario {@code false}.
     */
    boolean existsByBusinessIdAndDishNameIgnoreCase(Long businessId, String dishName);
}
>>>>>>> b2ca499d4ac5348bc4caf5ec0f8daa26e9443afc
