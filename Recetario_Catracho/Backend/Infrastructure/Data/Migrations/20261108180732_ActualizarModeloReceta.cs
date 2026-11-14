
using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Infrastructure.Data.Migrations
{
    public partial class ActualizarModeloReceta : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            // Renombrar columnas conservando su contenido
            migrationBuilder.RenameColumn(
                name: "Preparacion",
                table: "Recetas",
                newName: "Pasos");

            migrationBuilder.RenameColumn(
                name: "TiempoPreparacionMinutos",
                table: "Recetas",
                newName: "TiempoPreparacionMin");

            migrationBuilder.RenameColumn(
                name: "ImagenUrl",
                table: "Recetas",
                newName: "ImagenRuta");

            // Ajustar longitudes
            migrationBuilder.AlterColumn<string>(
                name: "Nombre",
                table: "Recetas",
                type: "nvarchar(100)",
                maxLength: 100,
                nullable: false,
                oldClrType: typeof(string),
                oldType: "nvarchar(150)",
                oldMaxLength: 150);

            migrationBuilder.AlterColumn<string>(
                name: "Descripcion",
                table: "Recetas",
                type: "nvarchar(500)",
                maxLength: 500,
                nullable: false,
                oldClrType: typeof(string),
                oldType: "nvarchar(1000)",
                oldMaxLength: 1000);

            migrationBuilder.AlterColumn<string>(
                name: "Ingredientes",
                table: "Recetas",
                type: "nvarchar(2000)",
                maxLength: 2000,
                nullable: false,
                oldClrType: typeof(string),
                oldType: "nvarchar(max)");

            migrationBuilder.AlterColumn<string>(
                name: "Pasos",
                table: "Recetas",
                type: "nvarchar(4000)",
                maxLength: 4000,
                nullable: false,
                oldClrType: typeof(string),
                oldType: "nvarchar(max)");

            // Agregar nuevas columnas
            migrationBuilder.AddColumn<int>(
                name: "Dificultad",
                table: "Recetas",
                type: "int",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<DateTime>(
                name: "FechaCreacion",
                table: "Recetas",
                type: "datetime2",
                nullable: false,
                defaultValueSql: "GETUTCDATE()");

            migrationBuilder.AddColumn<string>(
                name: "VideoRuta",
                table: "Recetas",
                type: "nvarchar(500)",
                maxLength: 500,
                nullable: true);

            // Crear índice único para Nombre
            migrationBuilder.CreateIndex(
                name: "IX_Recetas_Nombre",
                table: "Recetas",
                column: "Nombre",
                unique: true);

            // Restricciones
            migrationBuilder.AddCheckConstraint(
                name: "CK_Recetas_Porciones",
                table: "Recetas",
                sql: "[Porciones] BETWEEN 1 AND 50");

            migrationBuilder.AddCheckConstraint(
                name: "CK_Recetas_Tiempo",
                table: "Recetas",
                sql: "[TiempoPreparacionMin] BETWEEN 1 AND 600");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            // Eliminar índice y restricciones
            migrationBuilder.DropIndex(
                name: "IX_Recetas_Nombre",
                table: "Recetas");

            migrationBuilder.DropCheckConstraint(
                name: "CK_Recetas_Porciones",
                table: "Recetas");

            migrationBuilder.DropCheckConstraint(
                name: "CK_Recetas_Tiempo",
                table: "Recetas");

            // Eliminar columnas agregadas
            migrationBuilder.DropColumn(
                name: "Dificultad",
                table: "Recetas");

            migrationBuilder.DropColumn(
                name: "FechaCreacion",
                table: "Recetas");

            migrationBuilder.DropColumn(
                name: "VideoRuta",
                table: "Recetas");

            // Restaurar longitudes originales
            migrationBuilder.AlterColumn<string>(
                name: "Pasos",
                table: "Recetas",
                type: "nvarchar(max)",
                nullable: false,
                oldClrType: typeof(string),
                oldType: "nvarchar(4000)",
                oldMaxLength: 4000);

            migrationBuilder.AlterColumn<string>(
                name: "Nombre",
                table: "Recetas",
                type: "nvarchar(150)",
                maxLength: 150,
                nullable: false,
                oldClrType: typeof(string),
                oldType: "nvarchar(100)",
                oldMaxLength: 100);

            migrationBuilder.AlterColumn<string>(
                name: "Descripcion",
                table: "Recetas",
                type: "nvarchar(1000)",
                maxLength: 1000,
                nullable: false,
                oldClrType: typeof(string),
                oldType: "nvarchar(500)",
                oldMaxLength: 500);

            migrationBuilder.AlterColumn<string>(
                name: "Ingredientes",
                table: "Recetas",
                type: "nvarchar(max)",
                nullable: false,
                oldClrType: typeof(string),
                oldType: "nvarchar(2000)",
                oldMaxLength: 2000);

            // Restaurar nombres originales
            migrationBuilder.RenameColumn(
                name: "Pasos",
                table: "Recetas",
                newName: "Preparacion");

            migrationBuilder.RenameColumn(
                name: "TiempoPreparacionMin",
                table: "Recetas",
                newName: "TiempoPreparacionMinutos");

            migrationBuilder.RenameColumn(
                name: "ImagenRuta",
                table: "Recetas",
                newName: "ImagenUrl");
        }
    }
}
