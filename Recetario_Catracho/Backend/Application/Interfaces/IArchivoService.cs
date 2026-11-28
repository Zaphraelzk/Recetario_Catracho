
namespace Application.Interfaces;

public interface IArchivoService
{
    Task<string> GuardarImagenBase64Async(
        int recetaId,
        string nombreArchivo,
        string imagenBase64,
        CancellationToken cancellationToken = default
    );

    Task<string> GuardarVideoAsync(
        int recetaId,
        Stream archivo,
        string nombreArchivo,
        CancellationToken cancellationToken = default
    );

    Task EliminarArchivoAsync(
        string? ruta,
        CancellationToken cancellationToken = default
    );
}
