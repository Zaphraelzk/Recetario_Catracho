
using Application.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace Api.Controllers;

[ApiController]
[Route("api/categorias")]
public class CategoriasController : ControllerBase
{
    private readonly ICategoriaRepository _repository;

    public CategoriasController(ICategoriaRepository repository)
    {
        _repository = repository;
    }

    // GET: api/categorias
    [HttpGet]
    public async Task<IActionResult> ObtenerTodas(
        CancellationToken cancellationToken)
    {
        var categorias = await _repository.ObtenerTodasAsync(
            cancellationToken
        );

        return Ok(categorias);
    }

    // GET: api/categorias/1
    [HttpGet("{id:int}")]
    public async Task<IActionResult> ObtenerPorId(
        int id,
        CancellationToken cancellationToken)
    {
        var categoria = await _repository.ObtenerPorIdAsync(
            id,
            cancellationToken
        );

        if (categoria == null)
        {
            return NotFound(new
            {
                mensaje = "La categoría no existe."
            });
        }

        return Ok(categoria);
    }
}
