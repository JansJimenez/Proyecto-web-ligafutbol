# Modelo de datos (primera entrega)

Las migraciones versionadas definen:

- `app_users`: identidad local para autenticación de sesión; contraseña BCrypt y rol almacenado.
- `clubs`: perfil institucional del club; los campos opcionales admiten `NULL`. `status` permite retirar un club del portal sin destruir su historial.
- `audit_log`: actor, acción, tipo/ID de entidad, detalle acotado y fecha.
- `seasons` y `competitions`: temporadas y torneos, con puntuación configurable por torneo.
- `teams`: relación de clubes participantes con torneo, grupo y estadio.
- `players`: asignación actual de jugadores a equipos; número de camiseta único por equipo.
- `matches`: calendario y marcador, con restricciones de equipos distintos, equipos inscritos en el mismo torneo validadas por servicio y marcador requerido para partidos finalizados.
- `news_articles`: publicaciones, autor, estado y slug público.
- `match_events`: eventos de acta asociados a partido, equipo y jugador; contempla goles, autogoles, asistencias, tarjetas y sustituciones.
- `technical_staff`: integrantes del cuerpo técnico asociados al equipo, con función, contacto opcional y estado.

Relaciones de esta fase:

```text
seasons 1--N competitions 1--N teams N--1 clubs
                              |
                              +--N players
                              +--N matches (local/visita)
matches 1--N match_events N--1 teams / players
teams 1--N technical_staff
app_users 1--N news_articles
audit_log (eventos administrativos)
```

El actor de auditoría se conserva como texto para que el registro sobreviva a cambios futuros de cuentas. Siglas opcionales tienen índice único sobre `LOWER(acronym)`. La integridad del estado y del año de fundación se refuerza con restricciones SQL.

Los resultados oficiales de clasificación se derivan de partidos con estado `FINALIZED`; las estadísticas individuales también se derivan de eventos de partidos finalizados. El servicio valida que los goles y autogoles asentados coincidan con el marcador antes de finalizar. Los eventos de un partido finalizado solo pueden corregirse al reabrirlo. La asignación actual de jugador no mantiene historial de transferencias. Fases eliminatorias, sanciones disciplinarias y galerías quedan pendientes.
