# *ElectroFactura - Gestión de Facturas Eléctricas*

## Descripción
ElectroFactura es una aplicación de escritorio desarrollada en JavaFX para la gestión y visualización de facturas eléctricas. Permite consultar consumos por tramos horarios (punta, llano, valle) y generar facturas en formato PDF utilizando JasperReports.

Esta aplicación fue desarrollada para ElectriCity S.A. como parte de un proyecto de distribución profesional.

## Características
* Visualización de consumos mediante gráficos de barras
* Filtrado por tramos horarios: Punta, Llano y Valle
* Selección de mes para consultar consumos específicos
* Generación de facturas en formato PDF
* Base de datos SQLite integrada
* Interfaz moderna con JavaFX
* Arquitectura modular y fácilmente extensible

## Tecnologías utilizadas
* Java 23: Lenguaje principal
* JavaFX 23: Interfaz gráfica
* SQLite 3.42: Base de datos embebida
* JasperReports 6.20: Generación de reportes PDF
* Gradle 8: Gestión de dependencias y construcción
* JSmooth: Creación de .exe
* Inno Setup 6: Creación de instalador para Windows
* jpackage (incluido en JDK): Empaquetado nativo

## Estructura del proyecto
```
Mario_PR51/
│
├── src/
│ └── main/
│ ├── java/
│ │ └── org/example/mario_pr51/
│ │ ├── BD/
│ │ │ ├── ConexionBD.java # Conexión a SQLite
│ │ │ └── Reporte.java # Lógica de reportes y gráficos
│ │ ├── HelloApplication.java # Clase principal JavaFX
│ │ ├── HelloController.java # Controlador de la vista
│ │ └── Launcher.java # Punto de entrada
│ │
│ └── resources/
│ └── org/example/mario_pr51/
│ ├── hello-view.fxml # Vista principal
│ └── facturaLuz.jrxml # Plantilla de JasperReports
│
├── Practica5DB.db # Base de datos SQLite
├── build.gradle.kts # Configuración de Gradle
└── README.md # Documentación
```
## Requisitos del sistema
* Sistema operativo: Windows 10/11 (64 bits)
* Java Runtime: JRE 23 o superior
* Espacio en disco: 100 MB
* Memoria RAM: 512 MB mínimo

## Uso de la aplicación
* Selecciona un mes en el desplegable
* Marca los tramos que quieres visualizar (Punta, Llano, Valle)
* El gráfico mostrará los consumos del mes seleccionado
* Pulsa "Generar factura" para crear un PDF con los datos actuales

## Distribución
La aplicación se distribuye mediante:

* Instalador nativo generado con jpackage (.msi)
* Instalador personalizado generado con Inno Setup (.exe)
* Página web informativa en GitHub Pages

## Instalación y Más
[Enlace a Drive](https://drive.google.com/drive/folders/1TgHl96MFC-bJQO_cCcZ9x9QupH3hXkvm?usp=sharing)

## Licencia
Este proyecto es de uso educativo y no comercial para la asignatura de Desarrollo de Interfaces.
