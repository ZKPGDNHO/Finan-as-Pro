#!/bin/bash
# Script simplificado para iniciar o Sistema de Finanças Pessoais

echo "========================================"
echo "🚀 Iniciando o Sistema de Finanças..."
echo "========================================"

# Iniciar o Backend no fundo (porta 8080)
echo "1️⃣ Ligando o servidor e banco de dados SQLite..."
cd backend
mvn spring-boot:run > /dev/null 2>&1 &
BACKEND_PID=$!
cd ..

# Iniciar o Frontend (Interface Vite na porta 5173) no fundo
echo "2️⃣ Ligando a interface visual..."
cd frontend
npm run dev > /dev/null 2>&1 &
FRONTEND_PID=$!
cd ..

echo ""
echo "✅ Quase lá! O sistema está sendo preparado."
echo "🌐 Acesse no seu navegador: http://localhost:5173"
echo "ℹ️  O tempo de carregamento inicial pode demorar alguns segundos."
echo ""
echo "🔴 IMPORTANTE: Para desligar o sistema quando terminar de usar, aperte [CTRL + C] nesta janela."

# Mantem o script rodando para o usuário poder encerrar tudo
trap "echo 'Encerrando o sistema...'; kill $BACKEND_PID; kill $FRONTEND_PID; exit" INT
wait
