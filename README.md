# Liga Copa Perú

Aplicación web Maven con Java 25 LTS, Spring Boot 4.1.2, Spring MVC y Thymeleaf. PostgreSQL es el perfil predeterminado; `local` usa H2 en archivo y `sqlserver` permite conectar con SQL Server usando autenticación integrada de Windows.

## Alcance implementado

- Portal público con identidad configurable y directorio de clubes activos.
- Gestión administrativa de clubes: crear, listar, editar y desactivar (sin borrado físico).
- Temporadas y torneos configurables, incluyendo categoría y puntos por victoria/empate/derrota.
- Inscripción de equipos a clubes y torneos, con categoría/grupo, escudo cargado desde el dispositivo y desactivación lógica.
- Gestión de jugadores por equipo, posición, número de camiseta, fecha de nacimiento, fotografía y documento privado.
- Programación de partidos, registro de resultados y estados; solo encuentros `FINALIZED` cuentan en la tabla.
- Tabla de posiciones derivada de resultados finalizados y puntuación configurable por torneo.
- Actas con goles, autogoles, asistencias, tarjetas y sustituciones; los goles/autogoles registrados deben coincidir con el marcador al finalizar.
- Estadísticas de jugadores agregadas desde eventos de partidos finalizados.
- Gestión administrativa y directorio público del cuerpo técnico, con entrenadores, delegados, fotografías y documentos privados.
- Noticias con borrador/publicación/archivo, imagen de portada y rutas públicas amigables; el contenido se renderiza escapado.
- Carga local segura de imágenes PNG/JPG y documentos PNG/JPG/PDF; se validan firmas y dimensiones, se normalizan las imágenes y se generan nombres aleatorios.
- Los documentos de identidad se guardan fuera del directorio público y solo se descargan con una sesión administrativa autorizada.
- Validación de formularios en español, condiciones de inscripción, unicidad y restricciones de base de datos.
- Inicio/cierre de sesión con Spring Security, BCrypt, autorización de servidor por rol `ADMIN` y CSRF habilitado.
- Cuenta inicial opcional creada desde variables de entorno; no hay registro público.
- Auditoría transaccional de operaciones deportivas, noticias y cambios de clubes.
- Migración versionada Flyway y validación de esquema Hibernate.
- Interfaz pública y administrativa adaptable a celulares, tablets y computadoras, con menús táctiles, formularios cómodos en pantalla pequeña y tablas cortas presentadas como tarjetas; las tablas extensas conservan desplazamiento horizontal para no ocultar datos.

Los archivos de marca Copa Perú y FPF en `src/main/resources/static/images` provienen del portal oficial de la Copa Perú y se usan para identificar la competición. Confirma la autorización de marca correspondiente antes de publicar el portal fuera de un entorno de prueba.

No se incluyen todavía fases eliminatorias avanzadas, generador automático de calendarios, flujo independiente de aprobación de resultados, sanciones disciplinarias, recuperación de contraseñas, roles configurables, reportes/exportaciones, despliegue cloud ni datos ficticios. El portal no inventa marcadores ni participantes.

## Requisitos

- JDK 25 LTS. El código apunta a Java 25; comprueba con `java -version`.
- Visual Studio Code con Extension Pack for Java y Spring Boot Extension Pack (recomendaciones en `.vscode/extensions.json`).
- PostgreSQL local o administrado para el perfil predeterminado; SQL Server local está disponible mediante el perfil `sqlserver`.
- Docker Desktop es opcional; se incluye `compose.yaml`.
- Maven global no es necesario: usa Maven Wrapper.

En Windows, instala Java 25 desde Eclipse Adoptium (Temurin) o una distribución oficial de OpenJDK. Por ejemplo, con `winget install EclipseAdoptium.Temurin.25.JDK`; después abre una terminal nueva y verifica `java -version` y `JAVA_HOME`.

## Ejecutar en local para pruebas (recomendado)

Este perfil guarda la base en `.\data\liga-copa-peru.mv.db` y mantiene los datos al cerrar y volver a iniciar la aplicación. La carpeta `data` está excluida de Git. No necesitas PostgreSQL ni Docker para esta modalidad.

```powershell
$env:ADMIN_USERNAME = "admin"
$env:ADMIN_PASSWORD = "define-una-clave-local-de-12-caracteres-o-mas"
$env:SPRING_PROFILES_ACTIVE = "local"
$env:PORT = "8080"
.\mvnw.cmd spring-boot:run
```

Entra a `http://localhost:8080`; la administración está en `/admin`. Configura una contraseña propia antes de ejecutar. Si omites las variables del administrador, el portal público abre, pero el sistema avisa en el log que no hay cuenta administrativa. Flyway crea y actualiza el archivo H2 al arrancar. Para ejecutar el JAR con esa misma base, configura `SPRING_PROFILES_ACTIVE=local` antes de `java -jar`.

Los datos se guardan bajo el directorio desde el que ejecutas el comando. Haz una copia de seguridad cerrando la aplicación y copiando `data\liga-copa-peru.mv.db`. Para reiniciar intencionalmente las pruebas desde cero, cierra la aplicación y elimina únicamente ese archivo local de datos. No hagas esto si necesitas conservar pruebas.

Los archivos que se cargan desde los formularios se guardan por defecto fuera de la carpeta del proyecto, en `%USERPROFILE%\.liga-copa-peru\uploads` en Windows. Las imágenes públicas están en `public` y los documentos privados en `private`. Configura una carpeta persistente propia con `UPLOADS_LOCATION` si necesitas cambiar el destino. Incluye la carpeta de archivos en tus copias de seguridad. En los formularios se aceptan imágenes PNG/JPG de hasta 5 MB; para los documentos de identidad se permite además PDF de hasta 5 MB. Se aceptan imágenes de hasta 6000 × 6000 píxeles y 12 megapíxeles. La aplicación no conserva el nombre original del archivo.

Las imágenes de equipos, jugadores, cuerpo técnico y noticias pueden aparecer en el portal público. Los documentos de identidad son opcionales, no se muestran en páginas públicas y únicamente se descargan desde una sesión de administración. La aplicación no almacena el número del DNI. Confirma la base legal, el consentimiento, los plazos de conservación y las reglas de elegibilidad de la organización antes de recopilar documentos reales; para pruebas, utiliza datos ficticios.

Reglas aplicadas en los formularios deportivos: un equipo requiere un club activo y un torneo válido, y su nombre no se repite dentro del mismo torneo; un jugador requiere nombres, apellidos, equipo activo y fecha de nacimiento anterior a hoy, y el número de camiseta (si se asigna) debe estar entre 1 y 99 y ser único en su equipo; cada integrante técnico requiere nombre, apellidos, equipo activo y función; una noticia requiere título, resumen y contenido. Los documentos personales permanecen opcionales hasta que la organización defina qué reglamento o proceso de inscripción los exige.

El almacenamiento local es para desarrollo y pruebas: en una nube con discos efímeros se debe configurar un volumen persistente o un servicio de objetos antes del despliegue.

## PostgreSQL (producción o prueba de compatibilidad)

El perfil predeterminado sigue conectado a PostgreSQL y mantiene Flyway. Puedes usar una instalación local o Docker Compose opcional. Para Compose, copia `.env.example` a `.env`, cambia la contraseña y ejecuta `docker compose up -d postgres`. El puerto host por defecto es `55432` para evitar conflictos con instancias locales. Alternativamente crea la base `liga_copa_peru` en PostgreSQL y concede al usuario de aplicación permisos para migrar.

## SQL Server Management Studio 21 (Windows Authentication)

La captura corresponde al servidor `JANSJIMENEZ`, autenticación de Windows, cifrado obligatorio y certificado confiado. El perfil `sqlserver` usa esos parámetros por JDBC en el puerto `1433`; la identidad será la cuenta de Windows que ejecuta Java. Ejecuta VS Code o la terminal con la misma cuenta autorizada en SSMS. No configures un usuario ni una contraseña SQL para este perfil.

1. En SSMS, conectado al servidor `JANSJIMENEZ` con autenticación de Windows, abre **Nueva consulta** y ejecuta el script [sqlserver-create-database.sql](docs/sqlserver-create-database.sql). Este crea `liga_copa_peru` si todavía no existe; no borra bases ni tablas.
2. Asegúrate de que la cuenta de Windows usada por la aplicación tiene permisos para conectarse a esa base y crear tablas durante las migraciones. Para pruebas locales puedes asignarle `db_owner` solo sobre `liga_copa_peru`; usa permisos más restringidos al desplegar.
3. La autenticación integrada del driver JDBC de Microsoft requiere `mssql-jdbc_auth-13.4.0.x64.dll` (Java de 64 bits). Descarga el [paquete JDBC oficial de Microsoft](https://learn.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server), coloca el DLL de la carpeta `auth\x64` en un directorio incluido en `PATH` y abre una terminal nueva. Mantén el DLL en la misma versión del driver JDBC; no lo agregues al repositorio.
4. Inicia la aplicación con el perfil SQL Server:

```powershell
$env:SPRING_PROFILES_ACTIVE = "sqlserver"
$env:SQLSERVER_HOST = "JANSJIMENEZ"
$env:SQLSERVER_PORT = "1433"
$env:SQLSERVER_DATABASE = "liga_copa_peru"
$env:ADMIN_USERNAME = "admin"
$env:ADMIN_PASSWORD = "define-una-clave-local-de-12-caracteres-o-mas"
.\mvnw.cmd spring-boot:run
```

El driver usa `encrypt=true;trustServerCertificate=true`, equivalente a las opciones de la captura. La confianza explícita en el certificado es apropiada para pruebas locales; en producción configura un certificado válido y desactiva `trustServerCertificate`. El equipo SQL debe tener TCP/IP habilitado y aceptar conexiones en el puerto configurado. No uses la base `master` como base de la aplicación.

## Configuración y ejecución

Verifica Java y Maven Wrapper:

```powershell
java -version
.\mvnw.cmd -version
```

El perfil PostgreSQL obtiene `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` del entorno. PowerShell no carga `.env` automáticamente; puedes establecerlos directamente:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:55432/liga_copa_peru"
$env:DB_USERNAME = "liga_app"
$env:DB_PASSWORD = "tu-clave-local"
$env:ADMIN_USERNAME = "admin"
$env:ADMIN_PASSWORD = "una-clave-unica-de-al-menos-12"
$env:SESSION_COOKIE_SECURE = "false"
.\mvnw.cmd spring-boot:run
```

El perfil local H2 no necesita las variables de PostgreSQL. Si falta la base del perfil activo, el arranque falla explícitamente; la aplicación no simula que guardó datos.

## Pruebas y JAR ejecutable

```powershell
.\mvnw.cmd clean verify
.\mvnw.cmd clean package
Get-ChildItem .\target\liga-copa-peru-*.jar
java -jar .\target\liga-copa-peru-0.0.1-SNAPSHOT.jar
```

El nombre de JAR anterior corresponde a la versión actual. Confirma el nombre producido en `target` antes de ejecutarlo. Las pruebas usan H2 en memoria y ejecutan las migraciones Flyway; el perfil `local` usa H2 en archivo.

## Variables de entorno

| Variable | Obligatoria | Uso |
|---|---:|---|
| `DB_URL` | Solo PostgreSQL | JDBC URL PostgreSQL |
| `DB_USERNAME` | Solo PostgreSQL | Usuario PostgreSQL |
| `DB_PASSWORD` | Solo PostgreSQL | Contraseña PostgreSQL |
| `SQLSERVER_HOST` | Perfil `sqlserver` | Servidor SQL Server; predeterminado `JANSJIMENEZ` |
| `SQLSERVER_PORT` | Perfil `sqlserver` | Puerto TCP; predeterminado `1433` |
| `SQLSERVER_DATABASE` | Perfil `sqlserver` | Base de datos; predeterminada `liga_copa_peru` |
| `ADMIN_USERNAME` / `ADMIN_PASSWORD` | Para habilitar acceso inicial | Crea una sola cuenta `ADMIN` si aún no existe; no restablece una cuenta existente |
| `POSTGRES_HOST_PORT` | No | Puerto local publicado por Compose; predeterminado `55432` |
| `PORT` | No | Puerto HTTP; predeterminado `8080` |
| `LEAGUE_NAME` | No | Nombre institucional visible |
| `UPLOADS_LOCATION` | No | Carpeta persistente para imágenes y documentos; predeterminada `%USERPROFILE%\.liga-copa-peru\uploads` en Windows |
| `SESSION_COOKIE_SECURE` | No | En producción debe ser `true` detrás de HTTPS |
| `THYMELEAF_CACHE` | No | Caché de vistas; predeterminado `false` |

No se almacenan credenciales reales en los archivos de configuración. Los servicios SMTP y el almacenamiento cloud de archivos aún no están integrados.

## Migraciones y datos

Flyway mantiene las migraciones versionadas. PostgreSQL y H2 usan `src/main/resources/db/migration`; el perfil `sqlserver` usa migraciones equivalentes en `src/main/resources/db/sqlserver-migration`. Hibernate usa `ddl-auto: validate`; no crea ni modifica el esquema automáticamente. Las migraciones crean usuarios/clubes/auditoría, temporadas/torneos/equipos/jugadores/partidos, noticias, eventos de partido, cuerpo técnico y referencias a los archivos cargados. No se cargan clubes de demostración ni credenciales conocidas.

## Despliegue

El proyecto produce un JAR Spring Boot ejecutable compatible con Java 25. En un proveedor Java o contenedor:

1. Construye el artefacto con `.\mvnw.cmd clean verify package`.
2. Usa una base PostgreSQL administrada y configura `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` como secretos del proveedor.
3. Proporciona de forma segura `ADMIN_USERNAME` y `ADMIN_PASSWORD` para el primer arranque. Cambia las credenciales de despliegue y no las pongas en el repositorio.
4. Configura `PORT`, TLS/HTTPS en el proveedor y `SESSION_COOKIE_SECURE=true`.
5. Ejecuta `java -jar liga-copa-peru-0.0.1-SNAPSHOT.jar`. Flyway aplica migraciones en el inicio.
6. Configura backups administrados de PostgreSQL, retención y monitoreo de salud en `/actuator/health`.

El despliegue cloud no se ha ejecutado y no se han configurado dominios, credenciales ni servicios externos. La base local H2 es solo para desarrollo/pruebas; no la uses como sustituto de PostgreSQL administrado en producción.

## Evolución prevista

Continuar con fases/grupos formalizados y reglas configurables de desempate; generación de calendario; aprobación independiente de resultados; sanciones disciplinarias; roles y permisos configurables; gestión segura de usuarios/recuperación de acceso; almacenamiento cloud de archivos; reportes; SEO y pruebas contra PostgreSQL y SQL Server. Definir reglamentos, colores, escudo, formatos y criterios de desempate con la organización antes de habilitarlos.
