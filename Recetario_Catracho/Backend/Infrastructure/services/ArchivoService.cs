
using Application.Interfaces;

namespace Infrastructure.Services;

public class ArchivoService : IArchivoService
{
    private readonly string _carpetaImagenes;
    private readonly string _carpetaVideos;

    private const long MaxImagenBytes = 5 * 1024 * 1024;
    private const long MaxVideoBytes = 30 * 1024 * 1024;

    public ArchivoService()
    {
        var carpetaBase = Path.Combine(
            AppContext.BaseDirectory,
            "uploads");

        _carpetaImagenes = Path.Combine(carpetaBase, "imagenes");
        _carpetaVideos = Path.Combine(carpetaBase, "videos");

        Directory.CreateDirectory(_carpetaImagenes);
        Directory.CreateDirectory(_carpetaVideos);
    }

    public async Task<string> GuardarImagenBase64Async(
        int recetaId,
        string nombreArchivo,
        string imagenBase64,
        CancellationToken cancellationToken = default)
    {
        if (string.IsNullOrWhiteSpace(imagenBase64))
            throw new ArgumentException("La imagen Base64 está vacía.");

        var coma = imagenBase64.IndexOf(',');

        if (imagenBase64.StartsWith(
                "data:",
                StringComparison.OrdinalIgnoreCase) && coma >= 0)
        {
            imagenBase64 = imagenBase64[(coma + 1)..];
        }

        byte[] bytes;

        try
        {
            bytes = Convert.FromBase64String(imagenBase64);
        }
        catch (FormatException)
        {
            throw new ArgumentException(
                "El Base64 de la imagen no es válido.");
        }

        if (bytes.Length > MaxImagenBytes)
        {
            throw new InvalidOperationException(
                "413:La imagen supera los 5 MB.");
        }

        var extension = DetectarFormatoImagen(bytes);

        if (extension == null)
        {
            throw new NotSupportedException(
                "415:Formato no permitido. Use JPG, PNG o WebP.");
        }

        var nombreUnico = $"{Guid.NewGuid():N}{extension}";
        var rutaCompleta = Path.Combine(
            _carpetaImagenes,
            nombreUnico);

        await File.WriteAllBytesAsync(
            rutaCompleta,
            bytes,
            cancellationToken);

        return rutaCompleta;
    }

    public async Task<string> GuardarVideoAsync(
        int recetaId,
        Stream archivo,
        string nombreArchivo,
        CancellationToken cancellationToken = default)
    {
        if (archivo == null || !archivo.CanRead)
        {
            throw new ArgumentException(
                "El archivo de video no es válido.");
        }

        if (!string.Equals(
                Path.GetExtension(nombreArchivo),
                ".mp4",
                StringComparison.OrdinalIgnoreCase))
        {
            throw new NotSupportedException(
                "415:El video debe tener extensión MP4.");
        }

        var nombreUnico = $"{Guid.NewGuid():N}.mp4";
        var rutaCompleta = Path.Combine(
            _carpetaVideos,
            nombreUnico);

        var temporal = rutaCompleta + ".tmp";

        try
        {
            await using (var salida = new FileStream(
                temporal,
                FileMode.Create,
                FileAccess.Write,
                FileShare.None))
            {
                var buffer = new byte[81920];
                long total = 0;
                int leidos;

                while ((leidos = await archivo.ReadAsync(
                    buffer.AsMemory(0, buffer.Length),
                    cancellationToken)) > 0)
                {
                    total += leidos;

                    if (total > MaxVideoBytes)
                    {
                        throw new InvalidOperationException(
                            "413:El video supera los 30 MB.");
                    }

                    await salida.WriteAsync(
                        buffer.AsMemory(0, leidos),
                        cancellationToken);
                }
            }

            var cabecera = new byte[12];

            await using (var lectura = File.OpenRead(temporal))
            {
                if (lectura.Length < 12)
                {
                    throw new NotSupportedException(
                        "415:El archivo no es un MP4 válido.");
                }

                var cantidad = await lectura.ReadAsync(
                    cabecera.AsMemory(0, cabecera.Length),
                    cancellationToken);

                if (cantidad < 12)
                {
                    throw new NotSupportedException(
                        "415:El archivo no es un MP4 válido.");
                }
            }

            var esMp4 =
                cabecera[4] == 'f' &&
                cabecera[5] == 't' &&
                cabecera[6] == 'y' &&
                cabecera[7] == 'p';

            if (!esMp4)
            {
                throw new NotSupportedException(
                    "415:El video debe tener formato MP4.");
            }

            File.Move(temporal, rutaCompleta);

            return rutaCompleta;
        }
        catch
        {
            if (File.Exists(temporal))
                File.Delete(temporal);

            throw;
        }
    }

    public Task EliminarArchivoAsync(
        string? ruta,
        CancellationToken cancellationToken = default)
    {
        cancellationToken.ThrowIfCancellationRequested();

        if (string.IsNullOrWhiteSpace(ruta))
            return Task.CompletedTask;

        var raizUploads = Path.GetFullPath(
            Path.Combine(
                AppContext.BaseDirectory,
                "uploads"));

        var rutaArchivo = Path.GetFullPath(ruta);

        var prefijo = raizUploads + Path.DirectorySeparatorChar;

        if (!rutaArchivo.StartsWith(
                prefijo,
                StringComparison.OrdinalIgnoreCase))
        {
            throw new InvalidOperationException(
                "La ruta del archivo está fuera de uploads.");
        }

        if (File.Exists(rutaArchivo))
            File.Delete(rutaArchivo);

        return Task.CompletedTask;
    }

    private static string? DetectarFormatoImagen(byte[] bytes)
    {
        // JPG
        if (bytes.Length >= 3 &&
            bytes[0] == 0xFF &&
            bytes[1] == 0xD8 &&
            bytes[2] == 0xFF)
        {
            return ".jpg";
        }

        // PNG
        byte[] png =
        {
            0x89, 0x50, 0x4E, 0x47,
            0x0D, 0x0A, 0x1A, 0x0A
        };

        if (bytes.Length >= png.Length &&
            bytes.Take(8).SequenceEqual(png))
        {
            return ".png";
        }

        // WebP
        if (bytes.Length >= 12 &&
            bytes[0] == 'R' &&
            bytes[1] == 'I' &&
            bytes[2] == 'F' &&
            bytes[3] == 'F' &&
            bytes[8] == 'W' &&
            bytes[9] == 'E' &&
            bytes[10] == 'B' &&
            bytes[11] == 'P')
        {
            return ".webp";
        }

        return null;
    }
}
