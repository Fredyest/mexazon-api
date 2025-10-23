package com.mexazon.app.repository;

import com.mexazon.app.model.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad {@link MenuCategory}.
 * <p>
 * Gestiona el catálogo de categorías de menú disponibles en Mexazón.
 * Este repositorio permite realizar operaciones CRUD sobre la tabla
 * {@code menu_categories}, que se utiliza como catálogo de referencia
 * para clasificar los platillos de cada negocio.
 * </p>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>Extiende {@link JpaRepository} para heredar métodos CRUD básicos.</li>
 *   <li>El catálogo de categorías es gestionado desde el backend o mediante
 *       scripts de inicialización (seed data), no desde el frontend.</li>
 *   <li>En caso de necesitar filtros por nombre o búsqueda parcial, pueden
 *       agregarse métodos derivados posteriormente (por ejemplo,
 *       {@code findByCategoryNameContainingIgnoreCase}).</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * // Obtener todas las categorías de menú
 * List&lt;MenuCategory&gt; categories = menuCategoryRepository.findAll();
 *
 * // Buscar una categoría por ID
 * Optional&lt;MenuCategory&gt; category = menuCategoryRepository.findById(1L);
 * </pre>
 *
 * <p><strong>Entidad:</strong> {@code MenuCategory}</p>
 * <p><strong>Clave primaria:</strong> {@code Long}</p>
 */
@Repository
public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {}