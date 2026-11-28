
using System.Collections.Generic;

namespace Application.DTOs;

public class RecetasPaginadasDto
{
    public IReadOnlyList<RecetaRespuestaDto> Recetas { get; set; }
        = Array.Empty<RecetaRespuestaDto>();

    public int TotalRegistros { get; set; }

    public int PaginaActual { get; set; }

    public int PageSize { get; set; }

    public int TotalPaginas { get; set; }
}
