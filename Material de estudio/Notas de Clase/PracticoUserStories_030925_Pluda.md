# Notas de clase ISW 03/09/25 - Notas de clase

Que no es una User Storie? Una US NO ES una especificacion de un rq, uso las US como un mecanismo para **identificar requerimientos**, que luego estaran en el Product Backlog
Las CCC de las US:

- Conversacion
- Tarjeta (Card)
- Confirmacion
  La US deberia ser escrita por el Procduct Owner

Los criterios de aceptacion son una forma de **delimitar las US**, en un criterio de aceptacion incluyo RN, RNF o RQ de performance vinculados a esa caracteristica.

### Criterio de aceptacion != Prueba de usuario

El criterio de aceptación es una referencia técnica y contractual para el equipo; la prueba de usuario es una validación práctica y experiencial por parte de los usuarios. Cumplir los criterios de aceptación no garantiza automáticamente que los usuarios estén satisfechos tras probar el sistema.

#### Ejercicio de BodyPainting para repasar:

##### US para el Reembolso

> **Como** [rol] **Quiero** [accion] **Para** [valorEsperado]

**Como** _cliente_ quiero reclamar el reembolso del pedido
**para** recuperar el dinero es este ultimo.

##### Criterios de aceptacion:

- El reclamo debe ser realizado dentro de los primeros 5 dias habiles.
- Debe ser anulado el pedido a la hora de ser realizado el reclamo.
- El usuario puede tener la posibilidad de realizar un reclamo.
- Un reclamo de reembolso debe estar asociado a un pedido.

Si es opcional uso puede, si es obligatorio uso **debe**, es muy importante la forma de redactar el criterio de aceptacion, se redacta de una manera u otra en funcion de si es "Obliegatorio" u "Opcional"

##### Pruebas de usuario

Sintaxis

> **Probar** [accion]

- **_Probar_** _realizar reclamo de reembolso_ dentro de los 5 dias de haber sido registrado el pedido (**Pasa**)
- **_Probar_** _realizar_ el reclamo del reembolso 6 dias habiles de haber sido recibido el pedido (**Falla**)
- **_Probar_** _realizar_ un reclamo de reembolso sin asociarle un pedido (**Falla**)

#### Ejercicio: Practico 1

##### Definicion de roles:

- _Responsable de familia_ // **_Note_**: El nombre del rol debe ser algo que **represente al rol**, **NO** debe ser usuario o cliente.

##### Definicion de User Stories

Registrar gastos: // **_Note_**: Evitar poner "quiero poder", con "quiero" ya basta

- Como usuario quiero registrar los tipos de gastos para diferenciar los tipos de gastos realizados en mi familia.
- Como usuario quiero registrar un responsable de gastos para diferenciar el miembro de mi familia que realizo cierto gasto.
- Como usuario quiero registrar un gasto para administrar los gastos mensuales de mi familia.
- Como usuario quiero loguearme en la aplicacion para poder registrar mis gastos.

Visualizar gastos:

- Como usuario quiero consultar mis gastos para llevar la revisar la contabilidad mensual de mi familia.
- Como usuario quiero consultar mis gastos ordenados segun un filtro avanzado para llevar la contabilidad de familia.
- Como usuario quiero ver mis gastos por periodo para llevar el control de las finanzas de mi familia en un periodo determinado.
- Como usuario quiero ver mis gastos filtrados por tipo de gasto para ver lo que gaste por tipo de gasto.
- Como usuario quiero ver mis gastos filtrados por responsable de gasto para ver los gastos de cada miembro de mi familia.
- Como usuario quiero ver mis gastos filtrados por cierto rango de montos para ver los gastos mas o menos significativos realizados en mi familia.

##### Criterios de aceptacion:

- La sesion no debe caducar.
- Los gastos del mes actual deben ser mostrados por defecto en orden descendente.
- La fecha de los gastos debe poder ser modificada por el usuario.
- Mis gastos deben ser poder visualizados segun las siguientes columnas: monto, tipo de gasto y la fecha en la que el gasto sea realizado.
- Para registrar el gasto se deben incluir el tipo de gasto, monto, fecha, responsable de gasto.
- Por defecto se debera registrar el pago con la fecha actual.
- El monto del gasto debe ser mayor que cero

##### Pruebas de usuario:

// **_Note_**: Tiene sentido poner escenarios de fallo en caso de periodos o el tipo de filtro aplicado, por ejemplo el periodo

- Probar loguearme en la aplicacion (funciona)
- Probar desloguearme de la aplicacion (falla) (**Preguntar, creo que esta mal**)
- Probar visualizar los gastos realizados (funciona)
- Probar visualizar los gastos ordenados por tipo de gasto (funciona)
- Probar realizar el registro de un gasto sin el responsable del gasto o sin tipo de gasto (falla)
- Probar realizar los gastos para un periodo invalido (falla)
- Probar realizar el registro de un gasto menor o igual a cero (falla)
- Probar ingresar un gasto con todos los datos validos (funciona)

Tips para saber donde parar

- Tiene que estar el camino feliz
- En los criterios de aceptacion tiene que haber pruebas de generacion de objetos (o entidades o lo que sea)
- Chequear si vale la pena probar negativos, etc.

Clase que viene traer leido el ejercicio de ecomomy park conel listado de user stories hecho
