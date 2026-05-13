#!/usr/bin/env bash
# ═══════════════════════════════════════════════════════════════════════════════
#  EduControl — Criador de Aplicativo macOS + Ícones PWA
#  Uso: ./create-app.sh
#  Resultado: EduControl.app  (clique duplo para abrir o sistema)
# ═══════════════════════════════════════════════════════════════════════════════
set -euo pipefail

DIR="$(cd "$(dirname "$0")" && pwd)"
APP="$DIR/EduControl.app"

echo ""
echo "╔══════════════════════════════════════╗"
echo "║   EduControl — Criando .app bundle   ║"
echo "╚══════════════════════════════════════╝"
echo ""

# ─── 1. Gerar ícones ─────────────────────────────────────────────────────────
echo "▶  Gerando ícones …"
python3 "$DIR/scripts/generate-icon.py"

# ─── 2. Converter para .icns ─────────────────────────────────────────────────
echo "▶  Convertendo para .icns …"
iconutil -c icns "$DIR/EduControl.iconset" -o "$DIR/EduControl.icns"
rm -rf "$DIR/EduControl.iconset"
echo "   AppIcon.icns criado."

# ─── 3. Criar estrutura do .app ──────────────────────────────────────────────
echo "▶  Criando EduControl.app …"
rm -rf "$APP"
mkdir -p "$APP/Contents/MacOS"
mkdir -p "$APP/Contents/Resources"

cp "$DIR/EduControl.icns" "$APP/Contents/Resources/AppIcon.icns"
rm -f "$DIR/EduControl.icns"

# ─── 4. Info.plist ───────────────────────────────────────────────────────────
cat > "$APP/Contents/Info.plist" << 'PLIST'
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN"
  "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
  <key>CFBundleExecutable</key>      <string>EduControl</string>
  <key>CFBundleIconFile</key>        <string>AppIcon</string>
  <key>CFBundleIdentifier</key>      <string>com.educontrol.app</string>
  <key>CFBundleName</key>            <string>EduControl</string>
  <key>CFBundleDisplayName</key>     <string>EduControl</string>
  <key>CFBundleVersion</key>         <string>1.0.0</string>
  <key>CFBundleShortVersionString</key> <string>1.0</string>
  <key>CFBundlePackageType</key>     <string>APPL</string>
  <key>LSMinimumSystemVersion</key>  <string>11.0</string>
  <key>NSHighResolutionCapable</key> <true/>
  <key>LSUIElement</key>             <false/>
</dict>
</plist>
PLIST

# ─── 5. Launcher script (executado ao clicar no .app) ────────────────────────
cat > "$APP/Contents/MacOS/EduControl" << 'LAUNCHER'
#!/usr/bin/env bash
# Resolve o diretório raiz do projeto a partir de dentro do .app
APP_BUNDLE="$(cd "$(dirname "$0")/../.." && pwd)"
PROJECT_DIR="$(dirname "$APP_BUNDLE")"
BACKEND="$PROJECT_DIR/backend"
FRONTEND="$PROJECT_DIR/frontend"

# Se frontend já estiver rodando, só abre o navegador
if lsof -i :4200 -t &>/dev/null 2>&1; then
  open "http://localhost:4200"
  exit 0
fi

# Encerra processos órfãos nas portas antes de iniciar
lsof -ti :8080 | xargs kill -9 2>/dev/null || true
lsof -ti :4200 | xargs kill -9 2>/dev/null || true
sleep 1

# Notificação de início
osascript -e 'display notification "Iniciando servidores…" with title "EduControl 📚" subtitle "Aguarde alguns instantes"' 2>/dev/null || true

# Abre dois terminais: backend e frontend
osascript - "$BACKEND" "$FRONTEND" << 'EOF'
on run argv
  set backendDir  to item 1 of argv
  set frontendDir to item 2 of argv

  tell application "Terminal"
    activate

    -- Janela 1: Backend
    set w1 to do script "clear; printf '\\033[1;35m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\\n  📚  EduControl — Backend  (porta 8080)\\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\\033[0m'; cd " & quoted form of backendDir & "; mvn spring-boot:run"

    -- Janela 2: Frontend (aguarda 20s para o backend subir)
    set w2 to do script "clear; printf '\\033[1;34m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\\n  🎨  EduControl — Frontend  (porta 4200)\\n  Aguardando backend iniciar…\\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\\033[0m'; cd " & quoted form of frontendDir & "; sleep 20 && ng serve --host 0.0.0.0 --open"

    -- Posiciona as janelas lado a lado
    try
      set bounds of w1 to {0, 23, 950, 500}
      set bounds of w2 to {960, 23, 1910, 500}
    end try
  end tell
end run
EOF
LAUNCHER

chmod +x "$APP/Contents/MacOS/EduControl"

# ─── 6. Registro no Launch Services (atualiza ícone no Finder) ───────────────
/System/Library/Frameworks/CoreServices.framework/Frameworks/LaunchServices.framework/Support/lsregister \
  -f "$APP" 2>/dev/null || true

echo ""
echo "╔══════════════════════════════════════════════════════════╗"
echo "║  ✅  EduControl.app criado com sucesso!                  ║"
echo "║                                                          ║"
echo "║  Como usar:                                              ║"
echo "║  • Clique duplo em EduControl.app para abrir o sistema  ║"
echo "║  • Ou arraste para /Applications para instalar          ║"
echo "║                                                          ║"
echo "║  No celular (mesma rede Wi-Fi):                          ║"
echo "║  • Acesse http://<IP-do-mac>:4200                        ║"
echo "║  • iOS: botão Compartilhar → Adicionar à Tela de Início  ║"
echo "║  • Android: menu ⋮ → Adicionar à tela inicial           ║"
echo "╚══════════════════════════════════════════════════════════╝"
echo ""
