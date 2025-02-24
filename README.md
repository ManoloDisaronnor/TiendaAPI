# TELARES DEL SUR

Aplicación desarrollada en Kotlin usando la API [FakeStoreAPI](https://fakestoreapi.com/). La API se usa para cargar los productos actualizados a una base de datos de Firestore. Esta aplicación tiene autenticación de usuarios con Firebase Auth.

## ¿Cómo interactuar con la aplicación?

- En la pantalla principal, puedes ver todos los productos actualizados recogidos de la API.
- Puedes filtrar estos productos por categoría o buscar por el "title" del producto (el nombre).
- Puedes ver los detalles de cada producto, incluyendo:
  - Descripción.
  - Un objeto `rating` que muestra la cantidad de votos y la media de opiniones (no se pueden ver opiniones de usuarios específicos).
- Con el botón de abajo en la pantalla principal o en los detalles del producto, puedes añadir elementos al carrito.
  - Al añadir un producto, el icono realiza una pequeña animación y aparece un icono rojo en la `TopBar`, indicando que el producto ha sido añadido.
- Al hacer clic en el icono de perfil en la parte superior derecha de la `TopBar`, se abrirá un menú desplegable que permite:
  - Acceder al perfil del usuario (donde solo se puede cambiar el nombre de usuario).
  - Cerrar sesión.
  - Acceder al carrito.
- En el carrito:
  - Puedes incrementar o disminuir el número de unidades de cada producto.
  - Deslizando a la derecha una línea de producto, aparecerá un icono para eliminarla completamente.
- En la factura, puedes revisar tu compra antes de finalizar el pago.
- Al finalizar la compra, se mostrará una animación de una tarjeta de crédito para simular la gestión del pago.
- Finalmente, los productos se eliminarán del carrito y se regresará a la pantalla principal.

## Datos de Firestore

- La base de datos almacena los productos de la API en caso de inconsistencias.
- En la clase `ProductoItem` se implementa un método `equals` para verificar si un producto devuelto por la API es igual al almacenado en la base de datos.
  - Si se detectan cambios en la API, se eliminan los productos de Firestore y se vuelven a generar con los datos más recientes.
  - Este proceso se realiza al iniciar la aplicación, por lo que se muestra una pantalla de carga mientras se actualizan los datos.
- La base de datos almacena:
  - Información de los productos con los mismos atributos que devuelve la API.
  - Una colección `carrito` que guarda:
    - ID del producto.
    - Cantidad de unidades.
    - Precio.
    - ID del usuario que añadió el producto.
