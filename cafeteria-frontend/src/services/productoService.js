import { callSoap, getNodes, getText, getChildText, getChildNode } from './soapClient.js'

function parseProducto(node) {
  const catNode = getChildNode(node, 'categoria')
  return {
    id:          getChildText(node, 'id'),
    nombre:      getChildText(node, 'nombre'),
    precio:      parseFloat(getChildText(node, 'precio')),
    stock:       parseInt(getChildText(node, 'stock')),
    categoriaId: catNode ? getChildText(catNode, 'id') : '',
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
