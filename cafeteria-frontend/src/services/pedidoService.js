import { callSoap, getNodes, getText } from './soapClient.js'

function parsePedido(node) {
  return {
    id:     getText(node, 'id'),
    mesa:   getText(node, 'mesa'),
    estado: getText(node, 'estado'),
    total:  parseFloat(getText(node, 'total')),
    fecha:  getText(node, 'fecha'),
  }
}

export async function listarPedidos() {
  const xml = await callSoap('pedido', 'listarPedidos')
  return getNodes(xml, 'return').map(parsePedido)
}

export async function crearPedido(mesa) {
  const xml = await callSoap('pedido', 'crearPedido', `<arg0>${mesa}</arg0>`)
  return parsePedido(getNodes(xml, 'return')[0])
}

export async function cambiarEstado(id, nuevoEstado) {
  const xml = await callSoap('pedido', 'cambiarEstado',
    `<arg0>${id}</arg0><arg1>${nuevoEstado}</arg1>`)
  return getText(xml, 'return')
}

export async function cancelarPedido(id) {
  const xml = await callSoap('pedido', 'cancelarPedido', `<arg0>${id}</arg0>`)
  return getText(xml, 'return')
}

export async function listarPorEstado(estado) {
  const xml = await callSoap('pedido', 'listarPorEstado', `<arg0>${estado}</arg0>`)
  return getNodes(xml, 'return').map(parsePedido)
}
