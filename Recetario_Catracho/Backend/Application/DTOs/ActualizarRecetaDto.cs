
using System.ComponentModel.DataAnnotations;
using Domain.Enums;

namespace Application.DTOs;

public class ActualizarRecetaDto
{
    [Required(ErrorMessage = "El nombre es obligatorio.")]
    [StringLength(100, ErrorMessage = "El nombre no puede superar los 100 caracteres.")]
    public string Nombre { get; set; } = string.Empty;

    [Required(ErrorMessage = "La descripción es obligatoria.")]
    [StringLength(500, ErrorMessage = "La descripción no puede superar los 500 caracteres.")]
    public string Descripcion { get; set; } = string.Empty;

    [Range(1, int.MaxValue, ErrorMessage = "Debe seleccionar una categoría válida.")]
    public int CategoriaId { get; set; }

    [EnumDataType(typeof(Dificultad), ErrorMessage = "La dificultad seleccionada no es válida.")]
    public Dificultad Dificultad { get; set; }

    [Range(1, 600, ErrorMessage = "El tiempo debe estar entre 1 y 600 minutos.")]
    public int TiempoPreparacionMin { get; set; }

    [Range(1, 50, ErrorMessage = "Las porciones deben estar entre 1 y 50.")]
    public int Porciones { get; set; }

    [Required(ErrorMessage = "Los ingredientes son obligatorios.")]
    [StringLength(2000, ErrorMessage = "Los ingredientes no pueden superar los 2000 caracteres.")]
    public string Ingredientes { get; set; } = string.Empty;

    [Required(ErrorMessage = "Los pasos de preparación son obligatorios.")]
    [StringLength(4000, ErrorMessage = "Los pasos no pueden superar los 4000 caracteres.")]
    public string Pasos { get; set; } = string.Empty;

    [StringLength(500, ErrorMessage = "La ruta de imagen no puede superar los 500 caracteres.")]
    public string? ImagenRuta { get; set; }

    [StringLength(500, ErrorMessage = "La ruta de video no puede superar los 500 caracteres.")]
    public string? VideoRuta { get; set; }
}
