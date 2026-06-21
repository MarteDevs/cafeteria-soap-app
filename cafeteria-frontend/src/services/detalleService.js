import { callSoap, getNodes, getText, getChildText, getChildNode } from './soapClient.js'

function parseDetalle(node) {
  const prodNode = getChildNode(node, 'producto')
  return {
    id:            getChildText(node, 'id'),
    cantidad:      parseInt(getChildText(node, 'cantidad')),
    precioUnitario:parseFloat(getChildText(node, 'precioUnitario')),
    subtotal:      parseFloat(getChildText(node, 'subtotal')),
    productoId:    prodNode ? getChildText(prodNode, 'id') : '',
    productoNombre:prodNode ? getChildText(prodNode, 'nombre') : 'Desconocido',
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
