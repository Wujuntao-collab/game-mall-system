// serve.js —— 零依赖静态服务器 + /api 反向代理（避免 vite/esbuild 的子进程限制）
const http = require('http');
const fs = require('fs');
const path = require('path');

const ROOT = path.join(__dirname, 'dist');
const PORT = Number(process.env.PORT || 5173);
const API_TARGET = { host: '127.0.0.1', port: Number(process.env.API_PORT || 8080) };

const MIME = {
  '.html': 'text/html; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.mjs': 'application/javascript; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.svg': 'image/svg+xml',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.webp': 'image/webp',
  '.ico': 'image/x-icon',
  '.woff': 'font/woff',
  '.woff2': 'font/woff2',
  '.ttf': 'font/ttf'
};

function proxy(req, res) {
  const options = {
    host: API_TARGET.host,
    port: API_TARGET.port,
    path: req.url,
    method: req.method,
    headers: { ...req.headers, host: API_TARGET.host + ':' + API_TARGET.port }
  };
  const upstream = http.request(options, (up) => {
    res.writeHead(up.statusCode || 502, up.headers);
    up.pipe(res);
  });
  upstream.on('error', (e) => {
    res.writeHead(502, { 'Content-Type': 'application/json; charset=utf-8' });
    res.end(JSON.stringify({ code: 502, message: '后端未启动: ' + e.message }));
  });
  req.pipe(upstream);
}

const server = http.createServer((req, res) => {
  if (req.url.startsWith('/api')) return proxy(req, res);

  let filePath = decodeURIComponent(req.url.split('?')[0]);
  if (filePath === '/') filePath = '/index.html';
  let full = path.join(ROOT, filePath);
  if (!full.startsWith(ROOT)) {
    res.writeHead(403).end('forbidden');
    return;
  }
  fs.readFile(full, (err, data) => {
    if (err) {
      // SPA 回退
      fs.readFile(path.join(ROOT, 'index.html'), (e2, html) => {
        if (e2) {
          res.writeHead(404).end('not found');
          return;
        }
        res.writeHead(200, { 'Content-Type': MIME['.html'] });
        res.end(html);
      });
      return;
    }
    res.writeHead(200, { 'Content-Type': MIME[path.extname(full).toLowerCase()] || 'application/octet-stream' });
    res.end(data);
  });
});

server.listen(PORT, '127.0.0.1', () => {
  console.log(`前端已启动: http://127.0.0.1:${PORT}  (代理 /api -> ${API_TARGET.host}:${API_TARGET.port})`);
});
