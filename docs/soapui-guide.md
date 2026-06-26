# 🔌 Guía de Pruebas con SoapUI

SoapUI es una herramienta excelente para probar servicios web SOAP. En esta guía aprenderás cómo importar los WSDL de **Cafetería Ambrosia**, configurar un proyecto y realizar peticiones paso a paso con ejemplos reales de sobres XML.

---

## 1. Instalación y Preparación

1.  **Descargar SoapUI**: Descarga e instala la versión Open Source de SoapUI desde su [sitio web oficial](https://www.soapui.org/tools/soapui/).
2.  **Iniciar el Servidor**: Asegúrate de que el backend de la cafetería esté en ejecución (ya sea mediante Docker con `docker compose up` o de manera local en el puerto `8085`).
3.  **Endpoints Disponibles**: Los WSDL publicados son:
    *   **Categorías**: `http://localhost:8085/ws/categoria?wsdl`
    *   **Productos**: `http://localhost:8085/ws/producto?wsdl`
    *   **Pedidos**: `http://localhost:8085/ws/pedido?wsdl`
    *   **Detalles de Pedidos**: `http://localhost:8085/ws/detalle?wsdl`
    *   **Usuarios**: `http://localhost:8085/ws/usuario?wsdl`

---

## 2. Creación del Proyecto en SoapUI

1.  Abre SoapUI.
2.  Haz clic en **File** -> **New SOAP Project** (o presiona `Ctrl + N`).
3.  Completa los campos:
    *   **Project Name**: `Cafeteria Ambrosia SOAP`
    *   **Initial WSDL**: Ingresa el WSDL del servicio que deseas probar (por ejemplo, el de usuarios): `http://localhost:8085/ws/usuario?wsdl`
4.  Asegúrate de que la casilla **Create Requests** esté marcada para que SoapUI genere automáticamente plantillas de peticiones XML para cada método.
5.  Haz clic en **OK**.
6.  *Opcional*: Para importar los demás WSDL al mismo proyecto, haz clic derecho sobre el nombre del proyecto en la barra lateral izquierda, selecciona **Add WSDL** e ingresa la URL de los otros endpoints.

---

## 3. Ejemplos de Peticiones SOAP (Sobres XML)

A continuación, tienes las plantillas de sobres XML listas para copiar y pegar en SoapUI para cada uno de los servicios.

### 🔑 A. Servicio de Usuarios (`ws/usuario`)

#### 1. Iniciar Sesión (`login`)
*   **Acción**: Envía las credenciales y devuelve el usuario si coinciden.
*   **Sobre XML**:
    ```xml
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tns="http://service.dev.mars/">
       <soapenv:Header/>
       <soapenv:Body>
          <tns:login>
             <!-- Username (mesero1, cocinero1, admin) -->
             <arg0>mesero1</arg0>
             <!-- Password (123) -->
             <arg1>123</arg1>
          </tns:login>
       </soapenv:Body>
    </soapenv:Envelope>
    ```

#### 2. Registrar Nuevo Usuario (`crearUsuario`)
*   **Acción**: Crea un usuario con rol de `MESERO`, `COCINERO` o `ADMINISTRADOR`.
*   **Sobre XML**:
    ```xml
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tns="http://service.dev.mars/">
       <soapenv:Header/>
       <soapenv:Body>
          <tns:crearUsuario>
             <!-- Username (Ej. jperez) -->
             <arg0>jperez</arg0>
             <!-- Password -->
             <arg1>clave123</arg1>
             <!-- Nombre Completo -->
             <arg2>Julio Perez</arg2>
             <!-- Rol (MESERO | COCINERO | ADMINISTRADOR) -->
             <arg3>MESERO</arg3>
          </tns:crearUsuario>
       </soapenv:Body>
    </soapenv:Envelope>
    ```

---

### 📝 B. Servicio de Pedidos (`ws/pedido`)

#### 1. Crear Nuevo Pedido (`crearPedido`)
*   **Acción**: Inicializa un pedido asociándole una mesa y el nombre del mesero que lo atiende.
*   **Sobre XML**:
    ```xml
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tns="http://service.dev.mars/">
       <soapenv:Header/>
       <soapenv:Body>
          <tns:crearPedido>
             <!-- Identificador de la Mesa (Ej: Mesa 4) -->
             <arg0>Mesa 4</arg0>
             <!-- Nombre/Username del Mesero -->
             <arg1>Juan Mesero</arg1>
          </tns:crearPedido>
       </soapenv:Body>
    </soapenv:Envelope>
    ```

#### 2. Cambiar Estado del Pedido (`cambiarEstado`)
*   **Acción**: Actualiza el flujo de preparación del pedido en cocina.
*   **Valores permitidos**: `PENDIENTE`, `EN_PREPARACION`, `ENTREGADO`, `CANCELADO`.
*   **Sobre XML**:
    ```xml
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tns="http://service.dev.mars/">
       <soapenv:Header/>
       <soapenv:Body>
          <tns:cambiarEstado>
             <!-- ID del Pedido (Ej. 1) -->
             <arg0>1</arg0>
             <!-- Nuevo Estado -->
             <arg1>EN_PREPARACION</arg1>
          </tns:cambiarEstado>
       </soapenv:Body>
    </soapenv:Envelope>
    ```

---

### 🛍️ C. Servicio de Detalles de Pedido (`ws/detalle`)

#### 1. Agregar Producto al Carrito (`agregarDetalle`)
*   **Acción**: Añade una cantidad específica de un producto a un pedido activo. Calcula automáticamente el subtotal en base al precio unitario del catálogo.
*   **Sobre XML**:
    ```xml
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tns="http://service.dev.mars/">
       <soapenv:Header/>
       <soapenv:Body>
          <tns:agregarDetalle>
             <!-- ID del Pedido -->
             <arg0>1</arg0>
             <!-- ID del Producto (Ej. 2 para Café Americano) -->
             <arg1>2</arg1>
             <!-- Cantidad a pedir -->
             <arg2>3</arg2>
          </tns:agregarDetalle>
       </soapenv:Body>
    </soapenv:Envelope>
    ```

---

### ☕ D. Servicio de Productos y Categorías (`ws/producto`, `ws/categoria`)

#### 1. Listar Productos en Catálogo (`listarProductos`)
*   **Acción**: Obtiene la lista completa de productos disponibles con sus precios en soles (`S/.`) y stock.
*   **Sobre XML**:
    ```xml
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tns="http://service.dev.mars/">
       <soapenv:Header/>
       <soapenv:Body>
          <tns:listarProductos/>
       </soapenv:Body>
    </soapenv:Envelope>
    ```

#### 2. Crear un Nuevo Producto (`crearProducto`)
*   **Acción**: Agrega un producto vinculándolo a una categoría existente.
*   **Sobre XML**:
    ```xml
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tns="http://service.dev.mars/">
       <soapenv:Header/>
       <soapenv:Body>
          <tns:crearProducto>
             <arg0>
                <!-- Nombre del Producto -->
                <nombre>Frappé de Menta</nombre>
                <!-- Precio unitario en soles -->
                <precio>14.50</precio>
                <!-- Stock inicial -->
                <stock>30</stock>
                <!-- Objeto Categoría (requiere el ID de categoría) -->
                <categoria>
                   <id>2</id>
                </categoria>
             </arg0>
          </tns:crearProducto>
       </soapenv:Body>
    </soapenv:Envelope>
    ```
