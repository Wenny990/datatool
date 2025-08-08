export default function fetchStreamRequest(
  api,
  data,
  onRead,
  onFinish,
  onError,
  onStart
) {
  // 创建一个 AbortController 实例
  const controller = new AbortController()
  const signal = controller.signal
  const headers = new Headers()
  headers.append('Content-Type', 'application/json')
  headers.append('token', `Bearer ${111111111}`)
  const body = JSON.stringify(data)
  fetch(api, {
    method: 'POST',
    headers: headers,
    body: body,
    signal,
  })
    .then(response => {
      onStart();
      if (!response.ok) {
        onError(`HTTP error! status: ${response.status}`)
      }
      const reader = response.body.getReader()
      const decoder = new TextDecoder()
      let buffer = ''
      const readStream = () => {
        reader
          .read()
          .then(({ done, value }) => {
            if (done) {
              onFinish()
              return
            }

            // 将 Uint8Array 转换为字符串并追加到缓冲区
            buffer += decoder.decode(value, { stream: true })

            // 尝试按行解析 JSON
            const lines = buffer.split('\n')
            buffer = lines.pop() // 最后一行可能不完整，保留在缓冲区中

            lines.forEach(line => {
              if (line.trim()) {
                const data = JSON.parse(line.replace('data:', ''))
                if (data.choices[0].delta.content) {
                  onRead(data.choices[0].delta.content)
                }
              }
            })

            // 继续读取下一个 chunk
            readStream()
          })
          .catch(error => {
            onError(error)
          })
      }
      readStream()
    })
    .catch(error => {
      onError(error)
    })

  return () => {
    controller.abort()
  }
}
