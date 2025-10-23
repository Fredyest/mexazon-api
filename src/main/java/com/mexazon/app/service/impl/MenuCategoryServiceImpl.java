package com.mexazon.app.service.impl;

import com.mexazon.app.model.MenuCategory;
import com.mexazon.app.repository.MenuCategoryRepository;
import com.mexazon.app.service.MenuCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio {@link MenuCategoryService}.
 * <p>
 * Gestiona la lógica de negocio asociada al catálogo de categorías de menú
 * ({@link MenuCategory}) en Mexazón.
 * </p>
 *
 * <h3>Responsabilidades principales:</h3>
 * <ul>
 *   <li>Proporcionar la lista completa de categorías de menú disponibles.</li>
 *   <li>Servir como intermediario entre el controlador y el repositorio
 *       {@link MenuCategoryRepository}.</li>
 *   <li>Mantener la capa de servicio desacoplada de la persistencia.</li>
 * </ul>
 *
 * <h3>Diseño e implementación:</h3>
 * <ul>
 *   <li>La tabla de categorías actúa como un catálogo estático o semiestático,
 *       precargado por el backend o mediante un script de inicialización (seed data).</li>
 *   <li>El servicio es de solo lectura: no se permiten operaciones de creación,
 *       actualización o eliminación desde el frontend.</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * // Obtener todas las categorías disponibles
 * List&lt;MenuCategory&gt; categorias = menuCategoryService.findAll();
 * </pre>
 */
@Service
public class MenuCategoryServiceImpl implements MenuCategoryService {

    /** Repositorio de categorías de menú. */
    private final MenuCategoryRepository repo;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param repo repositorio JPA para la entidad {@link MenuCategory}.
     */
    public MenuCategoryServiceImpl(MenuCategoryRepository repo) {
        this.repo = repo;
    }

    /**
     * Recupera todas las categorías de menú disponibles en el catálogo.
     *
     * @return lista completa de categorías.
     */
    @Override
    public List<MenuCategory> findAll() {
        return repo.findAll();
    }
}