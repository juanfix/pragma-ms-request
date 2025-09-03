# 🏗️ Hexagonal Architecture Template

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-8.x-blue.svg)](https://gradle.org/)
[![WebFlux](https://img.shields.io/badge/Spring-WebFlux-green.svg)](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
[![Clean Architecture](https://img.shields.io/badge/Architecture-Clean-yellow.svg)](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

Un template robusto y escalable basado en **Arquitectura Hexagonal** y **Clean Architecture** para el desarrollo de microservicios reactivos en Java. Este proyecto utiliza el **Scaffold Clean Architecture Plugin** de Bancolombia para generar y mantener una estructura de código consistente y de alta calidad.

## 🎯 Propósito

Este template está diseñado para ser la base de futuros microservicios, proporcionando:

- **Arquitectura Hexagonal** que permite el desacoplamiento entre la lógica de negocio y los detalles técnicos
- **Programación Reactiva** con Spring WebFlux para alta concurrencia y escalabilidad
- **Documentación automática** de APIs con Swagger/OpenAPI
- **Gestión de dependencias** optimizada con Gradle
- **Testing** integrado con JUnit 5, Jacoco y PiTest
- **Calidad de código** con SonarQube

## 🏛️ Arquitectura

![Clean Architecture](https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png)

### 📁 Estructura del Proyecto

```
template/
├── 📂 applications/
│   └── 📂 app-service/                    # Módulo de aplicación principal
│       ├── build.gradle
│       └── src/main/java/
│           └── co/com/pragma/
│               └── MainApplication.java   # Punto de entrada de la aplicación
│
├── 📂 domain/                             # Capa de Dominio (Core Business)
│   ├── 📂 model/                          # Entidades y modelos del dominio
│   │   ├── build.gradle
│   │   └── src/main/java/
│   └── 📂 usecase/                        # Casos de uso y lógica de aplicación
│       ├── build.gradle
│       └── src/main/java/
│
├── 📂 infrastructure/                     # Capa de Infraestructura
│   ├── 📂 driven-adapters/               # Adaptadores hacia servicios externos
│   ├── 📂 entry-points/                  # Puntos de entrada a la aplicación
│   │   └── 📂 reactive-web/              # API REST reactiva con WebFlux
│   │       ├── build.gradle
│   │       └── src/main/java/
│   └── 📂 helpers/                       # Utilidades y componentes comunes
│
├── 📂 deployment/                         # Configuración de despliegue
│   └── Dockerfile                        # Containerización
│
├── build.gradle                          # Configuración principal de Gradle
├── main.gradle                           # Configuraciones compartidas
└── settings.gradle                       # Configuración de módulos
```

## 🔧 Tecnologías Principales

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Java** | 21 | Lenguaje de programación |
| **Spring Boot** | 3.5.4 | Framework de aplicación |
| **Spring WebFlux** | 6.x | Programación reactiva |
| **Gradle** | 8.x | Gestión de dependencias y build |
| **Project Reactor** | 3.x | Programación reactiva |
| **SpringDoc OpenAPI** | 2.4.0 | Documentación automática de APIs |
| **Lombok** | 1.18.38 | Reducción de código boilerplate |
| **JUnit 5** | 5.x | Testing unitario |
| **Jacoco** | 0.8.13 | Cobertura de código |
| **PiTest** | 1.19.0 | Mutation testing |
| **SonarQube** | 6.2.0 | Calidad de código |

## 🏗️ Capas de la Arquitectura

### 🎯 Domain (Dominio)
**Ubicación**: `domain/`

La capa más interna que contiene:
- **Model**: Entidades, value objects y reglas de negocio fundamentales
- **UseCase**: Casos de uso que orquestan la lógica de aplicación

```java
// Ejemplo de estructura
domain/
├── model/
│   ├── entities/
│   ├── valueobjects/
│   └── exceptions/
└── usecase/
    ├── services/
    └── ports/
```

### 🔌 Infrastructure (Infraestructura)
**Ubicación**: `infrastructure/`

Contiene los adaptadores y puntos de entrada:

#### Entry Points (Puntos de Entrada)
- **reactive-web**: API REST reactiva con Spring WebFlux
- Maneja las peticiones HTTP y mapea hacia los casos de uso
- Integra Swagger para documentación automática

#### Driven Adapters (Adaptadores de Salida)
- Implementaciones concretas para acceso a datos
- Integración con servicios externos
- Persistencia en bases de datos

#### Helpers
- Utilidades transversales
- Configuraciones compartidas
- Componentes reutilizables

### 🚀 Applications (Aplicación)
**Ubicación**: `applications/app-service/`

Capa de configuración y arranque:
- Inyección de dependencias
- Configuración de beans
- Punto de entrada principal (`MainApplication.java`)

## 🚀 Inicio Rápido

### Prerrequisitos

- **Java 21** o superior
- **Gradle 8.x**
- **IDE** con soporte para Lombok

### Instalación

1. **Clonar el repositorio**:
```bash
git clone <repository-url>
cd template
```

2. **Ejecutar la aplicación**:
```bash
./gradlew bootRun
```

3. **Ejecutar tests**:
```bash
./gradlew test
```

4. **Generar reporte de cobertura**:
```bash
./gradlew jacocoTestReport
```

### Acceso a la Aplicación

Una vez iniciada la aplicación:

- **API REST**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI Docs**: `http://localhost:8080/v3/api-docs`

## 🛠️ Comandos Útiles

```bash
# Compilar proyecto
./gradlew build

# Ejecutar tests con reporte
./gradlew test jacocoTestReport

# Ejecutar mutation testing
./gradlew pitest

# Análisis de calidad con SonarQube
./gradlew sonar

# Limpiar build
./gradlew clean

# Generar JAR ejecutable
./gradlew bootJar
```

## 📦 Extensión del Template

### Agregar un nuevo Driven Adapter

```bash
# Usando el plugin Clean Architecture
./gradlew ca:generateDrivenAdapter --name=database --type=jpa
```

### Agregar un nuevo Entry Point

```bash
# Generar un nuevo entry point
./gradlew ca:generateEntryPoint --name=rsocket --type=rsocket
```

### Estructura de un Microservicio típico

Al extender este template para un microservicio específico:

1. **Definir entidades** en `domain/model`
2. **Crear casos de uso** en `domain/usecase`
3. **Implementar entry points** según necesidades (REST, GraphQL, etc.)
4. **Desarrollar driven adapters** para persistencia y servicios externos
5. **Configurar** propiedades específicas del servicio

## 🔍 Calidad de Código

Este template incluye herramientas para mantener alta calidad:

- **Cobertura mínima**: 80% (configurable en `build.gradle`)
- **Mutation testing** con PiTest
- **Análisis estático** con SonarQube
- **Patrones arquitecturales** validados con ArchUnit

## 🐳 Despliegue

### Docker

```bash
# Construir imagen
docker build -f deployment/Dockerfile -t microservice-template .

# Ejecutar contenedor
docker run -p 8080:8080 microservice-template
```

## 📚 Recursos Adicionales

- [Clean Architecture - Uncle Bob](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [Spring WebFlux Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
- [Scaffold Clean Architecture Plugin](https://github.com/bancolombia/scaffold-clean-architecture)

## 🤝 Contribución

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

## 📄 Licencia

Este proyecto está bajo la licencia MIT. Ver el archivo `LICENSE` para más detalles.

---

**Desarrollado con ❤️ para la comunidad de microservicios**
