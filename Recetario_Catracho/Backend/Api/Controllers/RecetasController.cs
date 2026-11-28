
using Application.DTOs;
using Application.Interfaces;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Http;

namespace Api.Controllers;

[ApiController]
[Route("api/recetas")]
public class RecetasController : ControllerBase
{
    private readonly IRecetaService _recetaService;
    private readonly IArchivoService _archivoService;

    public RecetasController(
        IRecetaService recetaService,
        IArchivoService archivoService)
    {
        _recetaService = recetaService;
        _archivoService = archivoService;
    }

    // GET: api/recetas
    [HttpGet]
    public async Task<IActionResult> ObtenerTodas(
        [FromQuery] string? search,
        [FromQuery] int page = 1,
        [FromQuery] int pageSize = 10,
        [FromQuery] int? categoriaId = null,
        CancellationToken cancellationToken = default)
    {
        if (page < 1)
            return BadRequest(new { mensaje = "La página debe ser mayor o igual a 1." });

        if (pageSize < 1 || pageSize > 100)
            return BadRequest(new { mensaje = "El tamaño de página debe estar entre 1 y 100." });

        if (categoriaId.HasValue && categoriaId.Value < 1)
            return BadRequest(new { mensaje = "El ID de la categoría debe ser mayor que cero." });

        var resultado = await _recetaService.ObtenerPaginadasAsync(
            search,
            categoriaId,
            page,
            pageSize,
            cancellationToken);

        return Ok(resultado);
    }

    // GET: api/recetas/1
    [HttpGet("{id:int}")]
    public async Task<IActionResult> ObtenerPorId(
        int id,
        CancellationToken cancellationToken)
    {
        var receta = await _recetaService.ObtenerPorIdAsync(
            id,
            cancellationToken);

        if (receta == null)
            return NotFound(new { mensaje = "La receta no existe." });

        return Ok(receta);
    }

    // POST: api/recetas
    [HttpPost]
    public async Task<IActionResult> Crear(
        [FromBody] CrearRecetaDto dto,
        CancellationToken cancellationToken)
    {
        try
        {
            var recetaCreada = await _recetaService.CrearAsync(
                dto,
                cancellationToken);

            return CreatedAtAction(
                nameof(ObtenerPorId),
                new { id = recetaCreada.Id },
                recetaCreada);
        }
        catch (InvalidOperationException ex)
        {
            return Conflict(new { mensaje = ex.Message });
        }
        catch (ArgumentException ex)
        {
            return BadRequest(new { mensaje = ex.Message });
        }
    }

    // PUT: api/recetas/1
    [HttpPut("{id:int}")]
    public async Task<IActionResult> Actualizar(
        int id,
        [FromBody] ActualizarRecetaDto dto,
        CancellationToken cancellationToken)
    {
        try
        {
            var recetaActualizada = await _recetaService.ActualizarAsync(
                id,
                dto,
                cancellationToken);

            if (recetaActualizada == null)
                return NotFound(new { mensaje = "La receta no existe." });

            return NoContent();
        }
        catch (InvalidOperationException ex)
        {
            return Conflict(new { mensaje = ex.Message });
        }
        catch (ArgumentException ex)
        {
            return BadRequest(new { mensaje = ex.Message });
        }
    }

    // DELETE: api/recetas/1
    [HttpDelete("{id:int}")]
    public async Task<IActionResult> Eliminar(
        int id,
        CancellationToken cancellationToken)
    {
        var receta = await _recetaService.ObtenerPorIdAsync(
            id,
            cancellationToken);

        if (receta == null)
            return NotFound(new { mensaje = "La receta no existe." });

        var eliminado = await _recetaService.EliminarAsync(
            id,
            cancellationToken);

        if (!eliminado)
            return NotFound(new { mensaje = "La receta no existe." });

        await _archivoService.EliminarArchivoAsync(
            receta.ImagenRuta,
            cancellationToken);

        await _archivoService.EliminarArchivoAsync(
            receta.VideoRuta,
            cancellationToken);

        return NoContent();
    }

    // PUT: api/recetas/1/imagen-base64
    [HttpPut("{id:int}/imagen-base64")]
    [RequestSizeLimit(7 * 1024 * 1024)]
    public async Task<IActionResult> SubirImagenBase64(
        int id,
        [FromBody] ImagenBase64Dto dto,
        CancellationToken cancellationToken)
    {
        var receta = await _recetaService.ObtenerPorIdAsync(
            id,
            cancellationToken);

        if (receta == null)
            return NotFound(new { mensaje = "La receta no existe." });

        if (dto == null ||
            string.IsNullOrWhiteSpace(dto.NombreArchivo) ||
            string.IsNullOrWhiteSpace(dto.ImagenBase64))
        {
            return BadRequest(new
            {
                mensaje = "Debes proporcionar el nombre del archivo y la imagen Base64."
            });
        }

        string? nuevaRuta = null;

        try
        {
            nuevaRuta = await _archivoService.GuardarImagenBase64Async(
                id,
                dto.NombreArchivo,
                dto.ImagenBase64,
                cancellationToken);

            var actualizada = await _recetaService.ActualizarImagenRutaAsync(
                id,
                nuevaRuta,
                cancellationToken);

            if (!actualizada)
            {
                await _archivoService.EliminarArchivoAsync(
                    nuevaRuta,
                    cancellationToken);

                return NotFound(new { mensaje = "La receta no existe." });
            }

            // Eliminar la imagen anterior después de guardar la nueva ruta.
            if (!string.IsNullOrWhiteSpace(receta.ImagenRuta))
            {
                await _archivoService.EliminarArchivoAsync(
                    receta.ImagenRuta,
                    cancellationToken);
            }

            return Ok(new
            {
                mensaje = "Imagen guardada correctamente.",
                imagenRuta = nuevaRuta
            });
        }
        catch (ArgumentException ex)
        {
            if (nuevaRuta != null)
                await _archivoService.EliminarArchivoAsync(nuevaRuta, cancellationToken);

            return BadRequest(new { mensaje = ex.Message });
        }
        catch (NotSupportedException ex)
        {
            if (nuevaRuta != null)
                await _archivoService.EliminarArchivoAsync(nuevaRuta, cancellationToken);

            return StatusCode(
                StatusCodes.Status415UnsupportedMediaType,
                new { mensaje = ex.Message });
        }
        catch (InvalidOperationException ex)
        {
            if (nuevaRuta != null)
                await _archivoService.EliminarArchivoAsync(nuevaRuta, cancellationToken);

            if (ex.Message.StartsWith("413:"))
            {
                return StatusCode(
                    StatusCodes.Status413PayloadTooLarge,
                    new { mensaje = ex.Message });
            }

            return BadRequest(new { mensaje = ex.Message });
        }
    }

    // GET: api/recetas/1/imagen
    [HttpGet("{id:int}/imagen")]
    public async Task<IActionResult> ObtenerImagen(
        int id,
        CancellationToken cancellationToken)
    {
        var receta = await _recetaService.ObtenerPorIdAsync(
            id,
            cancellationToken);

        if (receta == null)
            return NotFound(new { mensaje = "La receta no existe." });

        if (string.IsNullOrWhiteSpace(receta.ImagenRuta) ||
            !System.IO.File.Exists(receta.ImagenRuta))
        {
            return NotFound(new { mensaje = "La receta no tiene una imagen disponible." });
        }

        var extension = Path.GetExtension(receta.ImagenRuta).ToLowerInvariant();

        var contentType = extension switch
        {
            ".jpg" or ".jpeg" => "image/jpeg",
            ".png" => "image/png",
            ".webp" => "image/webp",
            _ => "application/octet-stream"
        };

        return PhysicalFile(
            receta.ImagenRuta,
            contentType);
    }

    // DELETE: api/recetas/1/imagen
    [HttpDelete("{id:int}/imagen")]
    public async Task<IActionResult> EliminarImagen(
        int id,
        CancellationToken cancellationToken)
    {
        var receta = await _recetaService.ObtenerPorIdAsync(
            id,
            cancellationToken);

        if (receta == null)
            return NotFound(new { mensaje = "La receta no existe." });

        if (string.IsNullOrWhiteSpace(receta.ImagenRuta))
            return NotFound(new { mensaje = "La receta no tiene imagen." });

        var rutaAnterior = receta.ImagenRuta;

        var actualizada = await _recetaService.ActualizarImagenRutaAsync(
            id,
            null,
            cancellationToken);

        if (!actualizada)
            return NotFound(new { mensaje = "La receta no existe." });

        await _archivoService.EliminarArchivoAsync(
            rutaAnterior,
            cancellationToken);

        return NoContent();
    }

    // POST: api/recetas/1/video
    [HttpPost("{id:int}/video")]
    [Consumes("multipart/form-data")]
    [RequestSizeLimit(32 * 1024 * 1024)]
    public async Task<IActionResult> SubirVideo(
        int id,
        [FromForm] IFormFile archivo,
        CancellationToken cancellationToken)
    {
        var receta = await _recetaService.ObtenerPorIdAsync(
            id,
            cancellationToken);

        if (receta == null)
            return NotFound(new { mensaje = "La receta no existe." });

        if (archivo == null || archivo.Length == 0)
            return BadRequest(new { mensaje = "Debes seleccionar un video." });

        string? nuevaRuta = null;

        try
        {
            await using var stream = archivo.OpenReadStream();

            nuevaRuta = await _archivoService.GuardarVideoAsync(
                id,
                stream,
                archivo.FileName,
                cancellationToken);

            var actualizada = await _recetaService.ActualizarVideoRutaAsync(
                id,
                nuevaRuta,
                cancellationToken);

            if (!actualizada)
            {
                await _archivoService.EliminarArchivoAsync(
                    nuevaRuta,
                    cancellationToken);

                return NotFound(new { mensaje = "La receta no existe." });
            }

            if (!string.IsNullOrWhiteSpace(receta.VideoRuta))
            {
                await _archivoService.EliminarArchivoAsync(
                    receta.VideoRuta,
                    cancellationToken);
            }

            return Ok(new
            {
                mensaje = "Video guardado correctamente.",
                videoRuta = nuevaRuta
            });
        }
        catch (ArgumentException ex)
        {
            if (nuevaRuta != null)
                await _archivoService.EliminarArchivoAsync(nuevaRuta, cancellationToken);

            return BadRequest(new { mensaje = ex.Message });
        }
        catch (NotSupportedException ex)
        {
            if (nuevaRuta != null)
                await _archivoService.EliminarArchivoAsync(nuevaRuta, cancellationToken);

            return StatusCode(
                StatusCodes.Status415UnsupportedMediaType,
                new { mensaje = ex.Message });
        }
        catch (InvalidOperationException ex)
        {
            if (nuevaRuta != null)
                await _archivoService.EliminarArchivoAsync(nuevaRuta, cancellationToken);

            if (ex.Message.StartsWith("413:"))
            {
                return StatusCode(
                    StatusCodes.Status413PayloadTooLarge,
                    new { mensaje = ex.Message });
            }

            return BadRequest(new { mensaje = ex.Message });
        }
    }

    // GET: api/recetas/1/video
    [HttpGet("{id:int}/video")]
    public async Task<IActionResult> ObtenerVideo(
        int id,
        CancellationToken cancellationToken)
    {
        var receta = await _recetaService.ObtenerPorIdAsync(
            id,
            cancellationToken);

        if (receta == null)
            return NotFound(new { mensaje = "La receta no existe." });

        if (string.IsNullOrWhiteSpace(receta.VideoRuta) ||
            !System.IO.File.Exists(receta.VideoRuta))
        {
            return NotFound(new { mensaje = "La receta no tiene un video disponible." });
        }

        return PhysicalFile(
            receta.VideoRuta,
            "video/mp4",
            enableRangeProcessing: true);
    }

    // DELETE: api/recetas/1/video
    [HttpDelete("{id:int}/video")]
    public async Task<IActionResult> EliminarVideo(
        int id,
        CancellationToken cancellationToken)
    {
        var receta = await _recetaService.ObtenerPorIdAsync(
            id,
            cancellationToken);

        if (receta == null)
            return NotFound(new { mensaje = "La receta no existe." });

        if (string.IsNullOrWhiteSpace(receta.VideoRuta))
            return NotFound(new { mensaje = "La receta no tiene video." });

        var rutaAnterior = receta.VideoRuta;

        var actualizada = await _recetaService.ActualizarVideoRutaAsync(
            id,
            null,
            cancellationToken);

        if (!actualizada)
            return NotFound(new { mensaje = "La receta no existe." });

        await _archivoService.EliminarArchivoAsync(
            rutaAnterior,
            cancellationToken);

        return NoContent();
    }
}
