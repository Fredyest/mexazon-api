package com.mexazon.app.repository;

import com.mexazon.app.model.PostalCodeCatalog;
import com.mexazon.app.model.PostalCodeCatalogId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio JPA para la entidad {@link PostalCodeCatalog}.
 * <p>
 * Gestiona el catálogo de códigos postales, colonias y alcaldías,
 * permitiendo su consulta desde el backend o el frontend.
 * Este repositorio facilita búsquedas rápidas por código postal
 * y mantiene la integridad referencial con las direcciones de usuarios.
 * </p>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>Extiende {@link JpaRepository} para heredar operaciones CRUD y consultas básicas.</li>
 *   <li>Utiliza {@link PostalCodeCatalogId} como clave primaria compuesta
 *       ({@code postalCode + colonia}).</li>
 *   <li>Incluye un método de búsqueda por {@code postalCode} que devuelve
 *       todas las colonias y alcaldías asociadas a ese código.</li>
 *   <li>Se espera que los datos sean precargados desde el backend (seed) y 
 *       accedidos desde el frontend solo en modo lectura.</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * // Obtener todas las colonias asociadas a un código postal
 * List&lt;PostalCodeCatalog&gt; colonias = postalCodeCatalogRepository.findByPostalCode(\"04360\");
 * </pre>
 *
 * <p><strong>Entidad:</strong> {@code PostalCodeCatalog}</p>
 * <p><strong>Clave primaria:</strong> {@code PostalCodeCatalogId}</p>
 */
@Repository
public interface PostalCodeCatalogRepository extends JpaRepository<PostalCodeCatalog, PostalCodeCatalogId> {

    /**
     * Busca todas las colonias registradas que pertenecen a un código postal específico.
     *
     * @param postalCode código postal (CP) a consultar.
     * @return lista de registros del catálogo correspondientes a ese código postal.
     */
    List<PostalCodeCatalog> findByPostalCode(String postalCode);
}