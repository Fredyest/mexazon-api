package com.mexazon.app.service;

import com.mexazon.app.model.MenuCategory;
import java.util.List;

/**
 * Servicio encargado de gestionar la lógica relacionada con el catálogo
 * de categorías de menú ({@link MenuCategory}) en Mexazón.
 * <p>
 * Este servicio proporciona acceso a las categorías precargadas en el sistema,
 * las cuales permiten clasificar los platillos de los negocios.
 * </p>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>Centraliza la obtención de categorías para desacoplar los controladores del repositorio.</li>
 *   <li>El catálogo de categorías se carga desde el backend o mediante un script de inicialización (seed).</li>
 *   <li>El servicio es de solo lectura, ya que el frontend no puede modificar las categorías existentes.</li>
 * </ul>
 *
 * <h3>Responsabilidades del servicio:</h3>
 * <ul>
 *   <li>Proveer la lista completa de categorías disponibles.</li>
 *   <li>Servir como punto único de acceso al catálogo de menú para otras capas del sistema.</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * // Obtener todas las categorías de menú
 * List&lt;MenuCategory&gt; categories = menuCategoryService.findAll();
 * </pre>
 */
public interface MenuCategoryService {

    /**
     * Recupera todas las categorías del catálogo de menú.
     *
     * @return lista completa de categorías disponibles.
     */
    List<MenuCategory> findAll();
}