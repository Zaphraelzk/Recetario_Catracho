
using Application.Interfaces;
using Domain.Entities;
using Infrastructure.Data;
using Microsoft.EntityFrameworkCore;

namespace Infrastructure.Repositories;

public class RecetaRepository : IRecetaRepository
{
    private readonly AppDbContext _context;

    public RecetaRepository(AppDbContext context)
    {
        _context = context;
    }

    // Obtener todas las recetas.
    public async Task<IReadOnlyList<Receta>> ObtenerTodasAsync(
        CancellationToken cancellationToken = default)
    {
        return await _context.Recetas
            .AsNoTracking()
            .Include(r => r.Categoria)
            .OrderBy(r => r.Nombre)
            .ToListAsync(cancellationToken);
    }

    // Obtener recetas con búsqueda, filtro y paginación.
    public async Task<(IReadOnlyList<Receta> Recetas, int TotalRegistros)>
        ObtenerPaginadasAsync(
            string? search,
            int? categoriaId,
            int page,
            int pageSize,
            CancellationToken cancellationToken = default)
    {
        IQueryable<Receta> consulta = _context.Recetas
            .AsNoTracking()
            .Include(r => r.Categoria);

        // Buscar por nombre de receta.
        if (!string.IsNullOrWhiteSpace(search))
        {
            var termino = search.Trim();

            consulta = consulta.Where(r =>
                r.Nombre.Contains(termino));
        }

        // Filtrar por categoría.
        if (categoriaId.HasValue)
        {
            consulta = consulta.Where(r =>
                r.CategoriaId == categoriaId.Value);
        }

        // Obtener el total antes de aplicar la paginación.
        var totalRegistros = await consulta.CountAsync(
            cancellationToken);

        // Aplicar ordenamiento y paginación.
        var recetas = await consulta
            .OrderBy(r => r.Nombre)
            .ThenBy(r => r.Id)
            .Skip((page - 1) * pageSize)
            .Take(pageSize)
            .ToListAsync(cancellationToken);

        return (recetas, totalRegistros);
    }

    // Obtener una receta por su ID.
    public async Task<Receta?> ObtenerPorIdAsync(
        int id,
        CancellationToken cancellationToken = default)
    {
        return await _context.Recetas
            .AsNoTracking()
            .Include(r => r.Categoria)
            .FirstOrDefaultAsync(
                r => r.Id == id,
                cancellationToken
            );
    }

    // Comprobar si existe una receta con ese nombre.
    public async Task<bool> ExistePorNombreAsync(
        string nombre,
        int? excluirId = null,
        CancellationToken cancellationToken = default)
    {
        return await _context.Recetas
            .AnyAsync(
                r => r.Nombre == nombre &&
                     (!excluirId.HasValue || r.Id != excluirId.Value),
                cancellationToken
            );
    }

    // Crear una receta.
    public async Task<Receta> CrearAsync(
        Receta receta,
        CancellationToken cancellationToken = default)
    {
        _context.Recetas.Add(receta);

        await _context.SaveChangesAsync(cancellationToken);

        return receta;
    }

    // Actualizar una receta.
    public async Task<Receta> ActualizarAsync(
        Receta receta,
        CancellationToken cancellationToken = default)
    {
        _context.Recetas.Update(receta);

        await _context.SaveChangesAsync(cancellationToken);

        return receta;
    }

    // Eliminar una receta.
    public async Task<bool> EliminarAsync(
        int id,
        CancellationToken cancellationToken = default)
    {
        var receta = await _context.Recetas.FindAsync(
            new object[] { id },
            cancellationToken
        );

        if (receta == null)
        {
            return false;
        }

        _context.Recetas.Remove(receta);

        await _context.SaveChangesAsync(cancellationToken);

        return true;
    }
}
