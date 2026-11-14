
using Application.DTOs;

namespace Application.Interfaces;

public interface IRecetaService
{
    // Obtener todas las recetas.
    Task<IReadOnlyList<RecetaRespuestaDto>> ObtenerTodasAsync(
        CancellationToken cancellationToken = default
    );

    // Obtener recetas con búsqueda, filtro y paginación.
    Task<RecetasPaginadasDto> ObtenerPaginadasAsync(
        string? search,
        int? categoriaId,
        int page,
        int pageSize,
        CancellationToken cancellationToken = default
    );

    // Obtener una receta por su ID.
    Task<RecetaRespuestaDto?> ObtenerPorIdAsync(
        int id,
        CancellationToken cancellationToken = default
    );

    // Crear una receta.
    Task<RecetaRespuestaDto> CrearAsync(
        CrearRecetaDto dto,
        CancellationToken cancellationToken = default
    );

    // Actualizar una receta.
    Task<RecetaRespuestaDto?> ActualizarAsync(
        int id,
        ActualizarRecetaDto dto,
        CancellationToken cancellationToken = default
    );

    // Eliminar una receta.
    Task<bool> EliminarAsync(
        int id,
        CancellationToken cancellationToken = default
    );
}
