import { callSoap, getNodes, getText } from './soapClient.js'

function parseProducto(node) {
  return {
    id:          getText(node, 'id'),
    nombre:      getText(node, 'nombre'),
    precio:      parseFloat(getText(node, 'precio')),
    stock:       parseInt(getText(node, 'stock')),
    categoriaId: getText(node, 'categoriaId') || getText(node, 'id', node.querySelector?.('[localName="categoria"]')),
  }
}

export async function listarProductos() {
  const xml = await callSoap('producto', 'listarProductos')
  return getNodes(xml, 'return').map(parseProducto)
}

export async function crearProducto(nombre, precio, stock, categoriaId) {
  const xml = await callSoap('producto', 'crearProducto', `
    <arg0>
      <nombre>${nombre}</nombre>
      <precio>${precio}</precio>
      <stock>${stock}</stock>
      <categoria><id>${categoriaId}</id></categoria>
    </arg0>`)
  return parseProducto(getNodes(xml, 'return')[0])
}

export async function actualizarProducto(id, nombre, precio, stock, categoriaId) {
  const xml = await callSoap('producto', 'actualizarProducto', `
    <arg0>${id}</arg0>
    <arg1>
      <nombre>${nombre}</nombre>
      <precio>${precio}</precio>
      <stock>${stock}</stock>
      <categoria><id>${categoriaId}</id></categoria>
    </arg1>`)
  return getText(xml, 'return')
}

export async function eliminarProducto(id) {
  const xml = await callSoap('producto', 'eliminarProducto', `<arg0>${id}</arg0>`)
  return getText(xml, 'return')
}

export async function listarPorCategoria(categoriaId) {
  const xml = await callSoap('producto', 'listarPorCategoria', `<arg0>${categoriaId}</arg0>`)
  return getNodes(xml, 'return').map(parseProducto)
}
