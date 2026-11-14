BEGIN TRANSACTION;
EXEC sp_rename N'[Recetas].[Preparacion]', N'Pasos', 'COLUMN';

EXEC sp_rename N'[Recetas].[TiempoPreparacionMinutos]', N'TiempoPreparacionMin', 'COLUMN';

EXEC sp_rename N'[Recetas].[ImagenUrl]', N'ImagenRuta', 'COLUMN';

DECLARE @var nvarchar(max);
SELECT @var = QUOTENAME([d].[name])
FROM [sys].[default_constraints] [d]
INNER JOIN [sys].[columns] [c] ON [d].[parent_column_id] = [c].[column_id] AND [d].[parent_object_id] = [c].[object_id]
WHERE ([d].[parent_object_id] = OBJECT_ID(N'[Recetas]') AND [c].[name] = N'Nombre');
IF @var IS NOT NULL EXEC(N'ALTER TABLE [Recetas] DROP CONSTRAINT ' + @var + ';');
ALTER TABLE [Recetas] ALTER COLUMN [Nombre] nvarchar(100) NOT NULL;

DECLARE @var1 nvarchar(max);
SELECT @var1 = QUOTENAME([d].[name])
FROM [sys].[default_constraints] [d]
INNER JOIN [sys].[columns] [c] ON [d].[parent_column_id] = [c].[column_id] AND [d].[parent_object_id] = [c].[object_id]
WHERE ([d].[parent_object_id] = OBJECT_ID(N'[Recetas]') AND [c].[name] = N'Descripcion');
IF @var1 IS NOT NULL EXEC(N'ALTER TABLE [Recetas] DROP CONSTRAINT ' + @var1 + ';');
ALTER TABLE [Recetas] ALTER COLUMN [Descripcion] nvarchar(500) NOT NULL;

DECLARE @var2 nvarchar(max);
SELECT @var2 = QUOTENAME([d].[name])
FROM [sys].[default_constraints] [d]
INNER JOIN [sys].[columns] [c] ON [d].[parent_column_id] = [c].[column_id] AND [d].[parent_object_id] = [c].[object_id]
WHERE ([d].[parent_object_id] = OBJECT_ID(N'[Recetas]') AND [c].[name] = N'Ingredientes');
IF @var2 IS NOT NULL EXEC(N'ALTER TABLE [Recetas] DROP CONSTRAINT ' + @var2 + ';');
ALTER TABLE [Recetas] ALTER COLUMN [Ingredientes] nvarchar(2000) NOT NULL;

DECLARE @var3 nvarchar(max);
SELECT @var3 = QUOTENAME([d].[name])
FROM [sys].[default_constraints] [d]
INNER JOIN [sys].[columns] [c] ON [d].[parent_column_id] = [c].[column_id] AND [d].[parent_object_id] = [c].[object_id]
WHERE ([d].[parent_object_id] = OBJECT_ID(N'[Recetas]') AND [c].[name] = N'Pasos');
IF @var3 IS NOT NULL EXEC(N'ALTER TABLE [Recetas] DROP CONSTRAINT ' + @var3 + ';');
ALTER TABLE [Recetas] ALTER COLUMN [Pasos] nvarchar(4000) NOT NULL;

ALTER TABLE [Recetas] ADD [Dificultad] int NOT NULL DEFAULT 0;

ALTER TABLE [Recetas] ADD [FechaCreacion] datetime2 NOT NULL DEFAULT (GETUTCDATE());

ALTER TABLE [Recetas] ADD [VideoRuta] nvarchar(500) NULL;

CREATE UNIQUE INDEX [IX_Recetas_Nombre] ON [Recetas] ([Nombre]);

ALTER TABLE [Recetas] ADD CONSTRAINT [CK_Recetas_Porciones] CHECK ([Porciones] BETWEEN 1 AND 50);

ALTER TABLE [Recetas] ADD CONSTRAINT [CK_Recetas_Tiempo] CHECK ([TiempoPreparacionMin] BETWEEN 1 AND 600);

INSERT INTO [__EFMigrationsHistory] ([MigrationId], [ProductVersion])
VALUES (N'20261108180732_ActualizarModeloReceta', N'10.0.14');

COMMIT;
GO

