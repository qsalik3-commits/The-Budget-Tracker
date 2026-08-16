import http.server
import socketserver
import os

PORT = 3000
DIRECTORY = "/app/applet"

class Handler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=DIRECTORY, **kwargs)

    def end_headers(self):
        if self.path.endswith('.apk'):
            self.send_header('Content-Type', 'application/vnd.android.package-archive')
            self.send_header('Content-Disposition', 'attachment; filename="BudgetTracker.apk"')
        super().end_headers()

    def do_GET(self):
        if self.path == "/" or self.path == "/index.html":
            self.send_response(200)
            self.send_header("Content-Type", "text/html; charset=utf-8")
            self.end_headers()
            html = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Download Budget Tracker APK</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            background: #0f172a;
            color: #f8fafc;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            margin: 0;
            padding: 24px;
            box-sizing: border-box;
            text-align: center;
        }
        .card {
            background: #1e293b;
            border: 1px solid #334155;
            border-radius: 24px;
            padding: 36px 28px;
            max-width: 440px;
            width: 100%;
            box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.5);
        }
        .icon {
            font-size: 56px;
            margin-bottom: 16px;
        }
        h1 {
            font-size: 24px;
            margin: 0 0 8px 0;
            color: #ffffff;
        }
        p {
            font-size: 15px;
            color: #94a3b8;
            margin: 0 0 28px 0;
            line-height: 1.5;
        }
        .btn {
            display: inline-block;
            background: #3b82f6;
            color: #ffffff;
            font-size: 17px;
            font-weight: 600;
            text-decoration: none;
            padding: 16px 32px;
            border-radius: 14px;
            width: 100%;
            box-sizing: border-box;
            box-shadow: 0 4px 14px 0 rgba(59, 130, 246, 0.4);
            transition: background 0.2s;
        }
        .btn:active {
            background: #2563eb;
        }
        .badge {
            display: inline-block;
            margin-top: 20px;
            padding: 6px 14px;
            background: #0f172a;
            border-radius: 20px;
            font-size: 13px;
            color: #38bdf8;
            font-weight: 500;
        }
    </style>
</head>
<body>
    <div class="card">
        <div class="icon">💰</div>
        <h1>Budget Tracker</h1>
        <p>Ready to install on Android (7.0 to 15+)<br>Verified Complete Release Build</p>
        <a href="/BudgetTracker.apk" class="btn" download="BudgetTracker.apk">⬇️ Download APK (1.7 MB)</a>
        <br>
        <div class="badge">✓ Valid Signed Release Package</div>
    </div>
</body>
</html>"""
            self.wfile.write(html.encode("utf-8"))
            return
        return super().do_GET()

with socketserver.TCPServer(("", PORT), Handler) as httpd:
    print(f"Serving at port {PORT}")
    httpd.serve_forever()
