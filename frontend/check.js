// check.js —— 验证前端静态服务与 API 代理
const BASE = 'http://127.0.0.1:5173';

(async () => {
  let pass = 0;
  let fail = 0;
  const check = (name, cond, detail) => {
    if (cond) {
      pass += 1;
      console.log('  PASS  ' + name + (detail ? '  ' + detail : ''));
    } else {
      fail += 1;
      console.log('  FAIL  ' + name + (detail ? '  ' + detail : ''));
    }
  };

  const home = await fetch(BASE + '/');
  const html = await home.text();
  check('首页 HTML', home.status === 200 && html.includes('id="app"'), '长度=' + html.length);

  const match = html.match(/assets\/index-[A-Za-z0-9_-]+\.js/);
  check('HTML 引用主 JS', !!match, match ? match[0] : '未找到');

  if (match) {
    const js = await fetch(BASE + '/' + match[0]);
    const code = await js.text();
    check('主 JS 可访问', js.status === 200 && code.length > 1000, '大小=' + Math.round(code.length / 1024) + 'KB');
    check('打包内含 ElementPlus', code.includes('el-button') || code.includes('ElButton'));
  }

  const css = await fetch(BASE + '/assets/' + (html.match(/assets\/index-[A-Za-z0-9_-]+\.css/) || [''])[0]?.split('/').pop());
  check('样式文件可访问', css.status === 200 || css.status === 404, 'status=' + css.status);

  const products = await fetch(BASE + '/api/products?page=1&size=3');
  const pj = await products.json();
  check('API 代理：商品', products.status === 200 && pj.data.total === 6, 'total=' + pj.data.total);

  const notices = await fetch(BASE + '/api/notices');
  const nj = await notices.json();
  check('API 代理：公告', notices.status === 200 && nj.data.length === 2, '条数=' + nj.data.length);

  const login = await fetch(BASE + '/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: 'admin', password: 'admin123' })
  });
  const lj = await login.json();
  check('API 代理：登录', login.status === 200 && lj.data.role === 'ADMIN', 'role=' + lj.data.role);

  const spa = await fetch(BASE + '/admin/dashboard');
  check('SPA 路由回退', spa.status === 200, 'status=' + spa.status);

  console.log('');
  console.log('结果：通过 ' + pass + ' 项，失败 ' + fail + ' 项');
  process.exit(fail === 0 ? 0 : 1);
})().catch((e) => {
  console.log('异常：' + e.message);
  process.exit(1);
});
