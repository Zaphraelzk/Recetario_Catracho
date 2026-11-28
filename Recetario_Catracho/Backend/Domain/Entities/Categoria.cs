using System.Text.Json.Serialization;

namespace Domain.Entities;

public class Categoria
{
    public int Id { get; set; }

    public string Nombre { get; set; } = string.Empty;

    public string? Descripcion { get; set; }

    // Evita ciclos al convertir las entidades a JSON.
    [JsonIgnore]
    public ICollection<Receta> Recetas { get; set; }
        = new List<Receta>();
}
