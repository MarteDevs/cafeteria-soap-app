const BASE = '/ws'
const NS   = 'http://service.dev.mars/'

export async function callSoap(endpoint, method, paramsXml = '') {
  const envelope = `
    <soapenv:Envelope
      xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
      xmlns:tns="${NS}">
      <soapenv:Header/>
      <soapenv:Body>
        <tns:${method}>${paramsXml}</tns:${method}>
      </soapenv:Body>
    </soapenv:Envelope>`

  const res = await fetch(`${BASE}/${endpoint}`, {
    method:  'POST',
    headers: { 'Content-Type': 'text/xml;charset=UTF-8' },
    body:    envelope
  })

  if (!res.ok) throw new Error(`SOAP error ${res.status}`)
  const text = await res.text()
  return new DOMParser().parseFromString(text, 'text/xml')
}

// Helpers para extraer datos del XML de respuesta
export function getNodes(xml, tag) {
  return [...xml.getElementsByTagNameNS('*', tag)]
}

export function getText(node, tag) {
  const el = node.getElementsByTagNameNS('*', tag)[0]
  return el ? el.textContent.trim() : ''
}
