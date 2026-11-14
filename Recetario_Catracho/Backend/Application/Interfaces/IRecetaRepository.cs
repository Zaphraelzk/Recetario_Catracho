
using Domain.Entities;

namespace Application.Interfaces;

public interface IRecetaRepository
{
    Task<IReadOnlyList<Receta>> ObtenerTodasAsync(
        CancellationToken cancellationToken = default
    );

    Task<Receta?> ObtenerPorIdAsync(
        int id,
        CancellationToken cancellationToken = default
    );

    Task<bool> ExistePorNombreAsync(
        string nombre,
        int? excluirId = null,
        CancellationToken cancellationToken = default
    );

    Task<Receta> CrearAsync(
        Receta receta,
        CancellationToken cancellationToken = default
    );

    Task<Receta> ActualizarAsync(
        Receta receta,
        CancellationToken cancellationToken = default
    );

    Task<bool> EliminarAsync(
        int id,
        CancellationToken cancellationToken = default
    );

    Task<(IReadOnlyList<Receta> Recetas, int TotalRegistros)>
    ObtenerPaginadasAsync(
        string? search,
        int? categoriaId,
        int page,
        int pageSize,
        CancellationToken cancellationToken = default
    );
}
