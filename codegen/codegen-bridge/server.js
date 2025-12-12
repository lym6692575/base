import express from 'express'
import bodyParser from 'body-parser'
import { spawn } from 'child_process'
import fs from 'fs'
import path from 'path'
import { fileURLToPath } from 'url'
import { v4 as uuidv4 } from 'uuid'
import iconv from 'iconv-lite'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

const app = express()
app.use(bodyParser.json({ limit: '1mb' }))

app.post('/generate', async (req, res) => {
  try {
    const tmpDir = path.join(__dirname, 'tmp')
    if (!fs.existsSync(tmpDir)) fs.mkdirSync(tmpDir)
    const cfgPath = path.join(tmpDir, `codegen-${uuidv4()}.json`)
    fs.writeFileSync(cfgPath, JSON.stringify(req.body, null, 2), 'utf-8')

    const jar = path.join('d:\\code\\codegen\\codegen-tool\\target', 'codegen-tool-0.0.1-SNAPSHOT.jar')
    const dep = path.join('d:\\code\\codegen\\codegen-tool\\target', 'dependency')
    const cp = `${jar};${dep}\\*`

    const child = spawn('java', ['-cp', cp, 'com.dqjq.codegen.CodegenRunner', cfgPath])

    let stdout = ''
    let stderr = ''
    child.stdout.on('data', d => { stdout += iconv.decode(d, 'gbk') })
    child.stderr.on('data', d => { stderr += iconv.decode(d, 'gbk') })

    child.on('close', code => {
      try { fs.unlinkSync(cfgPath) } catch (e) {}
      if (code === 0) {
        res.json({ ok: true, log: stdout })
      } else {
        res.status(500).json({ ok: false, code, log: stdout, err: stderr })
      }
    })
  } catch (e) {
    res.status(500).json({ ok: false, err: String(e) })
  }
})

const port = 3001
app.listen(port, () => {})

