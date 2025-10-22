package com.mexazon.app.repository;

import com.mexazon.app.model.PostPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio JPA para la entidad {@link PostPhoto}.
 * <p>
 * Permite gestionar las fotografías asociadas a las reseñas de usuarios ({@link com.mexazon.app.model.Post})
 * dentro de Mexazón. Incluye métodos personalizados para obtener fotos ordenadas
 * y validar la existencia de una imagen en una posición específica.
 * </p>
 *
 * <h3>Decisiones de diseño:</h3>
 * <ul>
 *   <li>Extiende {@link JpaRepository} para heredar operaciones CRUD completas.</li>
 *   <li>Los métodos personalizados aprovechan la sintaxis de derivación de Spring Data JPA.</li>
 *   <li>Las fotos se ordenan por {@code photoOrder} para mantener la secuencia visual definida en la reseña.</li>
 *   <li>El método de existencia ({@code existsByPost_PostIdAndPhotoOrder}) evita duplicación de orden en una reseña.</li>
 * </ul>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>
 * // Obtener todas las fotos asociadas a una reseña, ordenadas
 * List&lt;PostPhoto&gt; fotos = postPhotoRepository.findByPost_PostIdOrderByPhotoOrderAsc(12L);
 *
 * // Verificar si ya existe una foto en la posición 1 para una reseña
 * boolean existe = postPhotoRepository.existsByPost_PostIdAndPhotoOrder(12L, 1);
 * </pre>
 *
 * <p><strong>Entidad:</strong> {@code PostPhoto}</p>
 * <p><strong>Clave primaria:</strong> {@code Long}</p>
 */
@Repository
public interface PostPhotoRepository extends JpaRepository<PostPhoto, Long> {

    /**
     * Recupera todas las fotografías de una reseña específica,
     * ordenadas por el campo {@code photoOrder} de manera ascendente.
     *
     * @param postId identificador de la reseña a la que pertenecen las fotos.
     * @return lista de fotografías asociadas, en orden de aparición.
     */
    List<PostPhoto> findByPost_PostIdOrderByPhotoOrderAsc(Long postId);

    /**
     * Verifica si ya existe una fotografía en un orden específico dentro de una reseña.
     * <p>
     * Útil para evitar duplicaciones en el orden de las imágenes dentro de una misma publicación.
     * </p>
     *
     * @param postId identificador de la reseña.
     * @param photoOrder posición u orden de la foto dentro de la reseña.
     * @return {@code true} si ya existe una foto con ese orden, de lo contrario {@code false}.
     */
    boolean existsByPost_PostIdAndPhotoOrder(Long postId, Integer photoOrder);
}