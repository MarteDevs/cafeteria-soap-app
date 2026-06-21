import { callSoap, getNodes, getText } from './soapClient.js'

function parseUsuario(node) {
  if (!node) return null
  const id = getText(node, 'id')
  if (!id) return null // If JAX-WS returned null or empty
  return {
    id:       parseInt(id),
    username: getText(node, 'username'),
    nombre:   getText(node, 'nombre'),
    rol:      getText(node, 'rol')
  }
}

export async function login(username, password) {
  const xml = await callSoap('usuario', 'login', `<arg0>${username}</arg0><arg1>${password}</arg1>`)
  const nodes = getNodes(xml, 'return')
  if (nodes.length === 0) return null
  return parseUsuario(nodes[0])
}

export async function crearUsuario(username, password, nombre, rol) {
  const xml = await callSoap('usuario', 'crearUsuario', 
    `<arg0>${username}</arg0><arg1>${password}</arg1><arg2>${nombre}</arg2><arg3>${rol}</arg3>`)
  const nodes = getNodes(xml, 'return')
  if (nodes.length === 0) return null
  return parseUsuario(nodes[0])
}

export async function listarUsuarios() {
  const xml = await callSoap('usuario', 'listarUsuarios')
  return getNodes(xml, 'return').map(parseUsuario).filter(Boolean)
}

export async function eliminarUsuario(id) {
  const xml = await callSoap('usuario', 'eliminarUsuario', `<arg0>${id}</arg0>`)
  return getText(xml, 'return')
}
