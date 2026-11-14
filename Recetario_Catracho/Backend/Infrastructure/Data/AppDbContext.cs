
using Domain.Entities;
using Microsoft.EntityFrameworkCore;

namespace Infrastructure.Data;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options)
        : base(options)
    {
    }

    public DbSet<Receta> Recetas => Set<Receta>();

    public DbSet<Categoria> Categorias => Set<Categoria>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        // Configuración de Categoria
        modelBuilder.Entity<Categoria>(entity =>
        {
            entity.ToTable("Categorias");

            entity.HasKey(c => c.Id);

            entity.Property(c => c.Nombre)
                .IsRequired()
                .HasMaxLength(100);

            entity.Property(c => c.Descripcion)
                .HasMaxLength(500);

            entity.HasIndex(c => c.Nombre)
                .IsUnique();

            // Datos semilla oficiales
            entity.HasData(
              new Categoria
               {
                Id = 1,
                Nombre = "Desayunos",
                Descripcion = "Recetas para el desayuno"
               },
              new Categoria
               {
                Id = 2,
                Nombre = "Sopas y caldos",
                Descripcion = "Sopas tradicionales hondureñas"
               },
              new Categoria
               {
                Id = 3,
                Nombre = "Platos fuertes",
                Descripcion = "Platos principales"
               },
              new Categoria
               {
                Id = 4,
                Nombre = "Acompañamientos",
                Descripcion = "Complementos para las comidas"
               },
              new Categoria
               {
                Id = 5,
                Nombre = "Postres",
                Descripcion = "Postres y dulces tradicionales"
               },
              new Categoria
               {
                Id = 6,
                Nombre = "Bebidas",
                Descripcion = "Bebidas tradicionales hondureñas"
               }
           );
        });

        // Configuración de Receta
        modelBuilder.Entity<Receta>(entity =>
        {
            entity.ToTable("Recetas", table =>
            {
                table.HasCheckConstraint(
                    "CK_Recetas_Tiempo",
                    "[TiempoPreparacionMin] BETWEEN 1 AND 600");

                table.HasCheckConstraint(
                    "CK_Recetas_Porciones",
                    "[Porciones] BETWEEN 1 AND 50");
            });

            entity.HasKey(r => r.Id);

            entity.Property(r => r.Nombre)
                .IsRequired()
                .HasMaxLength(100);

            entity.HasIndex(r => r.Nombre)
                .IsUnique();

            entity.Property(r => r.Descripcion)
                .HasMaxLength(500);

            entity.Property(r => r.Ingredientes)
                .IsRequired()
                .HasMaxLength(2000);

            entity.Property(r => r.Pasos)
                .IsRequired()
                .HasMaxLength(4000);

            entity.Property(r => r.ImagenRuta)
                .HasMaxLength(500);

            entity.Property(r => r.VideoRuta)
                .HasMaxLength(500);

            entity.Property(r => r.Dificultad)
                .HasConversion<int>();

            entity.Property(r => r.FechaCreacion)
                .IsRequired();

            // Relación entre Receta y Categoria
            entity.HasOne(r => r.Categoria)
                .WithMany(c => c.Recetas)
                .HasForeignKey(r => r.CategoriaId)
                .OnDelete(DeleteBehavior.Restrict);
        });
    }
}
