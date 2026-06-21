import { callSoap, getNodes, getText } from './soapClient.js'

function parseCategoria(node) {
  return {
    id:          getText(node, 'id'),
    nombre:      getText(node, 'nombre'),
    descripcion: getText(node, 'descripcion'),
  }
}

export async function listarCategorias() {
  const xml = await callSoap('categoria', 'listarCategorias')
  return getNodes(xml, 'return').map(parseCategoria)
}

export async function crearCategoria(nombre, descripcion) {
  const xml = await callSoap('categoria', 'crearCategoria', `
    <arg0>
      <nombre>${nombre}</nombre>
      <descripcion>${descripcion}</descripcion>
    </arg0>`)
  return parseCategoria(getNodes(xml, 'return')[0])
}

export async function actualizarCategoria(id, nombre, descripcion) {
  const xml = await callSoap('categoria', 'actualizarCategoria', `
    <arg0>${id}</arg0>
    <arg1>
      <nombre>${nombre}</nombre>
      <descripcion>${descripcion}</descripcion>
    </arg1>`)
  return getText(xml, 'return')
}

export async function eliminarCategoria(id) {
  const xml = await callSoap('categoria', 'eliminarCategoria', `<arg0>${id}</arg0>`)
  return getText(xml, 'return')
}
