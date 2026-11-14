
using Application.DTOs;
using Application.Interfaces;
using Domain.Entities;

namespace Application.Services;

public class RecetaService : IRecetaService
{
    private readonly IRecetaRepository _repository;
    private readonly ICategoriaRepository _categoriaRepository;

    public RecetaService(
        IRecetaRepository repository,
        ICategoriaRepository categoriaRepository)
    {
        _repository = repository;
        _categoriaRepository = categoriaRepository;
    }

    // Obtener todas las recetas.
    public async Task<IReadOnlyList<RecetaRespuestaDto>> ObtenerTodasAsync(
        CancellationToken cancellationToken = default)
    {
        var recetas = await _repository.ObtenerTodasAsync(
            cancellationToken);

        return recetas
    .Select(receta => MapearRespuesta(receta))
    .ToList();
    }

    // Obtener recetas con búsqueda, filtro y paginación.
    public async Task<RecetasPaginadasDto> ObtenerPaginadasAsync(
        string? search,
        int? categoriaId,
        int page,
        int pageSize,
        CancellationToken cancellationToken = default)
    {
        if (page < 1)
        {
            throw new ArgumentOutOfRangeException(
                nameof(page),
                "La página debe ser mayor o igual a 1.");
        }

        if (pageSize < 1 || pageSize > 100)
        {
            throw new ArgumentOutOfRangeException(
                nameof(pageSize),
                "El tamaño de página debe estar entre 1 y 100.");
        }

        if (categoriaId.HasValue && categoriaId.Value < 1)
        {
            throw new ArgumentException(
                "El ID de la categoría debe ser mayor que cero.");
        }

        var (recetas, totalRegistros) =
            await _repository.ObtenerPaginadasAsync(
                search,
                categoriaId,
                page,
                pageSize,
                cancellationToken);

        return new RecetasPaginadasDto
        {
            Recetas = recetas
    .Select(receta => MapearRespuesta(receta))
    .ToList(),
            TotalRegistros = totalRegistros,
            PaginaActual = page,
            PageSize = pageSize,
            TotalPaginas = (int)Math.Ceiling(
                totalRegistros / (double)pageSize)
        };
    }

    // Obtener una receta por su ID.
    public async Task<RecetaRespuestaDto?> ObtenerPorIdAsync(
        int id,
        CancellationToken cancellationToken = default)
    {
        var receta = await _repository.ObtenerPorIdAsync(
            id,
            cancellationToken);

        return receta == null ? null : MapearRespuesta(receta);
    }

    // Crear una receta.
    public async Task<RecetaRespuestaDto> CrearAsync(
        CrearRecetaDto dto,
        CancellationToken cancellationToken = default)
    {
        var nombre = dto.Nombre.Trim();

        var existe = await _repository.ExistePorNombreAsync(
            nombre,
            cancellationToken: cancellationToken);

        if (existe)
        {
            throw new InvalidOperationException(
                "Ya existe una receta con ese nombre.");
        }

        var categoria = await _categoriaRepository.ObtenerPorIdAsync(
            dto.CategoriaId,
            cancellationToken);

        if (categoria == null)
        {
            throw new ArgumentException(
                "La categoría seleccionada no existe.");
        }

        var receta = new Receta
        {
            Nombre = nombre,
            Descripcion = dto.Descripcion.Trim(),
            CategoriaId = dto.CategoriaId,
            Dificultad = dto.Dificultad,
            TiempoPreparacionMin = dto.TiempoPreparacionMin,
            Porciones = dto.Porciones,
            Ingredientes = dto.Ingredientes.Trim(),
            Pasos = dto.Pasos.Trim(),
            ImagenRuta = dto.ImagenRuta?.Trim(),
            VideoRuta = dto.VideoRuta?.Trim(),
            FechaCreacion = DateTime.UtcNow
        };

        var recetaCreada = await _repository.CrearAsync(
            receta,
            cancellationToken);

        return MapearRespuesta(
            recetaCreada,
            categoria.Nombre);
    }

    // Actualizar una receta.
    public async Task<RecetaRespuestaDto?> ActualizarAsync(
        int id,
        ActualizarRecetaDto dto,
        CancellationToken cancellationToken = default)
    {
        var receta = await _repository.ObtenerPorIdAsync(
            id,
            cancellationToken);

        if (receta == null)
        {
            return null;
        }

        var nombre = dto.Nombre.Trim();

        var existe = await _repository.ExistePorNombreAsync(
            nombre,
            excluirId: id,
            cancellationToken: cancellationToken);

        if (existe)
        {
            throw new InvalidOperationException(
                "Ya existe otra receta con ese nombre.");
        }

        var categoria = await _categoriaRepository.ObtenerPorIdAsync(
            dto.CategoriaId,
            cancellationToken);

        if (categoria == null)
        {
            throw new ArgumentException(
                "La categoría seleccionada no existe.");
        }

        receta.Nombre = nombre;
        receta.Descripcion = dto.Descripcion.Trim();
        receta.CategoriaId = dto.CategoriaId;
        receta.Dificultad = dto.Dificultad;
        receta.TiempoPreparacionMin = dto.TiempoPreparacionMin;
        receta.Porciones = dto.Porciones;
        receta.Ingredientes = dto.Ingredientes.Trim();
        receta.Pasos = dto.Pasos.Trim();
        receta.ImagenRuta = dto.ImagenRuta?.Trim();
        receta.VideoRuta = dto.VideoRuta?.Trim();

        var recetaActualizada = await _repository.ActualizarAsync(
            receta,
            cancellationToken);

        return MapearRespuesta(
            recetaActualizada,
            categoria.Nombre);
    }

    // Eliminar una receta.
    public async Task<bool> EliminarAsync(
        int id,
        CancellationToken cancellationToken = default)
    {
        return await _repository.EliminarAsync(
            id,
            cancellationToken);
    }

    // Convertir la entidad en un DTO de respuesta.
    private static RecetaRespuestaDto MapearRespuesta(
        Receta receta,
        string? nombreCategoria = null)
    {
        return new RecetaRespuestaDto
        {
            Id = receta.Id,
            Nombre = receta.Nombre,
            Descripcion = receta.Descripcion,
            CategoriaId = receta.CategoriaId,
            Categoria = nombreCategoria ?? receta.Categoria.Nombre,
            Dificultad = receta.Dificultad,
            TiempoPreparacionMin = receta.TiempoPreparacionMin,
            Porciones = receta.Porciones,
            Ingredientes = receta.Ingredientes,
            Pasos = receta.Pasos,
            ImagenRuta = receta.ImagenRuta,
            VideoRuta = receta.VideoRuta,
            FechaCreacion = receta.FechaCreacion
        };
    }
}
