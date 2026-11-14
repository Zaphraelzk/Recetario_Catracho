
using Application.Interfaces;
using Application.Services;
using Infrastructure.Data;
using Infrastructure.Repositories;
using Microsoft.EntityFrameworkCore;
using Scalar.AspNetCore;

var builder = WebApplication.CreateBuilder(args);

// Registrar controladores.
builder.Services.AddControllers();

// Configurar respuestas de error con ProblemDetails.
builder.Services.AddProblemDetails();

// Registrar autorización.
builder.Services.AddAuthorization();

// Conexión a SQL Server.
var connectionString = builder.Configuration
    .GetConnectionString("DefaultConnection")
    ?? throw new InvalidOperationException(
        "No se encontró la cadena de conexión DefaultConnection."
    );

builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseSqlServer(connectionString));

// Registrar repositorios.
builder.Services.AddScoped<
    ICategoriaRepository,
    CategoriaRepository
>();

builder.Services.AddScoped<
    IRecetaRepository,
    RecetaRepository
>();

// Registrar servicios.
builder.Services.AddScoped<
    IRecetaService,
    RecetaService
>();

// Generar documentación OpenAPI.
builder.Services.AddOpenApi();

// Construir la aplicación.
var app = builder.Build();

// Habilitar OpenAPI y Scalar.
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
    app.MapScalarApiReference();
}

// Manejo centralizado de excepciones no controladas.
app.UseExceptionHandler();

// Habilitar autorización.
app.UseAuthorization();

// Registrar endpoints de los controladores.
app.MapControllers();

app.Run();
