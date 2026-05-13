#!/bin/bash
# EduControl — Inicializar Frontend (Angular)
echo "🎨 Iniciando frontend EduControl..."
echo "   URL: http://localhost:4200"
echo ""
cd "$(dirname "$0")/frontend"
ng serve --open
