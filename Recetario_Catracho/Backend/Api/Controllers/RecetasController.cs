
using Application.DTOs;
using Application.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace Api.Controllers;

[ApiController]
[Route("api/recetas")]
public class RecetasController : ControllerBase
{
    private readonly IRecetaService _recetaService;

    public RecetasController(IRecetaService recetaService)
    {
        _recetaService = recetaService;
    }

    // GET: api/recetas
    // GET: api/recetas?search=pollo&page=1&pageSize=10&categoriaId=3
    [HttpGet]
    public async Task<IActionResult> ObtenerTodas(
        [FromQuery] string? search,
        [FromQuery] int page = 1,
        [FromQuery] int pageSize = 10,
        [FromQuery] int? categoriaId = null,
        CancellationToken cancellationToken = default)
    {
        if (page < 1)
        {
            return BadRequest(new
            {
                mensaje = "La página debe ser mayor o igual a 1."
            });
        }

        if (pageSize < 1 || pageSize > 100)
        {
            return BadRequest(new
            {
                mensaje = "El tamaño de página debe estar entre 1 y 100."
            });
        }

        if (categoriaId.HasValue && categoriaId.Value < 1)
        {
            return BadRequest(new
            {
                mensaje = "El ID de la categoría debe ser mayor que cero."
            });
        }

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
        {
            return NotFound(new
            {
                mensaje = "La receta no existe."
            });
        }

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
                recetaCreada
            );
        }
        catch (InvalidOperationException ex)
        {
            return Conflict(new
            {
                mensaje = ex.Message
            });
        }
        catch (ArgumentException ex)
        {
            return BadRequest(new
            {
                mensaje = ex.Message
            });
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
            {
                return NotFound(new
                {
                    mensaje = "La receta no existe."
                });
            }

            return NoContent();
        }
        catch (InvalidOperationException ex)
        {
            return Conflict(new
            {
                mensaje = ex.Message
            });
        }
        catch (ArgumentException ex)
        {
            return BadRequest(new
            {
                mensaje = ex.Message
            });
        }
    }

    // DELETE: api/recetas/1
    [HttpDelete("{id:int}")]
    public async Task<IActionResult> Eliminar(
        int id,
        CancellationToken cancellationToken)
    {
        var eliminado = await _recetaService.EliminarAsync(
            id,
            cancellationToken);

        if (!eliminado)
        {
            return NotFound(new
            {
                mensaje = "La receta no existe."
            });
        }

        return NoContent();
    }
}
