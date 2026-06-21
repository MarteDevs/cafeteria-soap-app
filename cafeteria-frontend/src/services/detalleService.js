import { callSoap, getNodes, getText } from './soapClient.js'

function parseDetalle(node) {
  return {
    id:            getText(node, 'id'),
    cantidad:      parseInt(getText(node, 'cantidad')),
    precioUnitario:parseFloat(getText(node, 'precioUnitario')),
    subtotal:      parseFloat(getText(node, 'subtotal')),
    productoId:    getText(node, 'productoId') || getText(node, 'id', node.querySelector?.('[localName="producto"]')),
    productoNombre:getText(node, 'nombre', node.querySelector?.('[localName="producto"]')) || getText(node, 'nombre'), // Depende de la estructura exacta devuelta
  }
}

export async function listarDetallesPorPedido(pedidoId) {
  const xml = await callSoap('detalle', 'listarDetallesPorPedido', `<arg0>${pedidoId}</arg0>`)
  return getNodes(xml, 'return').map(parseDetalle)
}

export async function agregarDetalle(pedidoId, productoId, cantidad) {
  const xml = await callSoap('detalle', 'agregarDetalle',
    `<arg0>${pedidoId}</arg0><arg1>${productoId}</arg1><arg2>${cantidad}</arg2>`)
  return parseDetalle(getNodes(xml, 'return')[0])
}

export async function eliminarDetalle(detalleId) {
  const xml = await callSoap('detalle', 'eliminarDetalle', `<arg0>${detalleId}</arg0>`)
  return getText(xml, 'return')
}

export async function actualizarCantidad(detalleId, nuevaCantidad) {
  const xml = await callSoap('detalle', 'actualizarCantidad', `<arg0>${detalleId}</arg0><arg1>${nuevaCantidad}</arg1>`)
  return getText(xml, 'return')
}
