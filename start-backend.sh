#!/bin/bash
# EduControl — Inicializar Backend (Spring Boot + H2)

# Encerra processo anterior na porta 8080, se houver
PREV=$(lsof -ti :8080 2>/dev/null)
if [ -n "$PREV" ]; then
  echo "⚠️  Encerrando processo anterior na porta 8080 (PID $PREV)..."
  kill -9 $PREV 2>/dev/null
  sleep 1
fi

echo "🚀 Iniciando backend EduControl..."
echo "   URL: http://localhost:8080"
echo "   H2 Console: http://localhost:8080/h2-console"
echo ""
cd "$(dirname "$0")/backend"
mvn spring-boot:run
