# Reglas de negocio (primera entrega)

1. El nombre oficial del club es obligatorio y tiene un máximo de 150 caracteres.
2. Las siglas, si se ingresan, son únicas sin distinguir mayúsculas y minúsculas.
3. El año de fundación opcional debe estar entre 1800 y 2100.
4. El correo institucional, cuando se informa, debe tener formato válido.
5. Solo usuarios autenticados con rol `ADMIN` pueden operar en `/admin/**`.
6. Los formularios POST mantienen protección CSRF de Spring Security.
7. Desactivar un club conserva el registro y lo excluye del directorio público.
8. Solo clubes activos se muestran públicamente.
9. Cada alta, modificación o desactivación de club escribe auditoría en la misma transacción.
10. No hay alta pública de cuentas; la primera cuenta administrativa se crea únicamente cuando ambas variables de arranque están configuradas.
11. Un equipo requiere un club activo y un torneo válidos.
12. Un torneo pertenece a una temporada; las fechas de inicio/fin no pueden invertirse.
13. Un jugador solo se asigna a un equipo activo; su camiseta debe estar entre 1 y 99 y no repetirse en la misma plantilla.
14. Un partido requiere dos equipos distintos, ambos inscritos en el torneo seleccionado.
15. Para finalizar un partido deben existir ambos marcadores y ser no negativos.
16. Los partidos programados, aplazados y cancelados no suman a la tabla; solo los finalizados con marcador.
17. Si un partido tiene goles o autogoles en el acta, su conteo debe coincidir con el marcador antes de finalizar.
18. No se pueden agregar ni quitar eventos mientras el partido esté finalizado; primero debe reabrirse para corregirlo.
19. La clasificación ordena por puntos, diferencia de goles, goles a favor y nombre. Los desempates de enfrentamiento directo y reglas avanzadas siguen pendientes.
20. Las estadísticas individuales se calculan con eventos de partidos finalizados; los autogoles cuentan al equipo contrario en el marcador, no como gol individual del jugador.
21. Noticias en borrador o archivadas no son visibles en el portal público; el contenido editorial se escapa como texto.
22. Un integrante del cuerpo técnico requiere un equipo activo; su función pertenece al catálogo definido y el correo, si se informa, debe tener formato válido.
23. El directorio público muestra solo integrantes activos de equipos y clubes activos; desactivar conserva el registro y la auditoría.

La elegibilidad deportiva avanzada, conflictos horarios, aprobación independiente de resultados y reglas oficiales de desempate deben definirse con el reglamento aplicable antes de producción.
