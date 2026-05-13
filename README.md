# EduControl 📚

Sistema completo de **gestão de estudos** com interface Apple Liquid Glass, backend Spring Boot e banco H2 embutido.

---

## Funcionalidades

| Módulo | Descrição |
|---|---|
| 📊 **Dashboard** | Visão geral: tempo estudado hoje/semana, últimas atividades, gráfico de barras dos últimos 7 dias |
| 📚 **Matérias** | Cadastro de matérias → temas → conteúdos com cor, emoji e dias da semana |
| 📅 **Planejamento** | Grade semanal (dom–sáb) para organizar o que estudar em cada dia |
| ⏱️ **Timer** | Cronômetro com relógio circular tipo pomodoro, salva sessões automaticamente |
| 📝 **Anotações** | Anotações com busca e filtro por matéria |
| 📚 **Biblioteca** | Artigos, livros, excertos, links e vídeos organizados por matéria |
| 📈 **Análises** | Mapa de calor de 12 meses, gráfico de rosca por matéria, ranking de tempo |
| 🤖 **IA** | Gerador de perguntas, exercícios, resumos e flashcards via OpenAI GPT-4o-mini |

---

## Requisitos

- **Java 17+**
- **Maven 3.8+**
- **Node.js 18+** com **Angular CLI 20+**

---

## Como iniciar

### Terminal 1 — Backend

```bash
./start-backend.sh
# ou manualmente:
cd backend && mvn spring-boot:run
```

- API REST: http://localhost:8080/api
- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:file:./data/educontrol`
  - Usuário: `educontrol` | Senha: `educontrol123`

### Terminal 2 — Frontend

```bash
./start-frontend.sh
# ou manualmente:
cd frontend && ng serve --open
```

- App: http://localhost:4200

---

## Estrutura do projeto

```
EduControl/
├── backend/                 # Spring Boot 3.2 + H2
│   └── src/main/java/com/educontrol/
│       ├── config/          # CORS, RestTemplate
│       ├── entity/          # Subject, Topic, TopicItem, StudySession, Note, LibraryItem, WeeklyPlan
│       ├── repository/      # Spring Data JPA
│       ├── service/         # Lógica de negócio
│       ├── controller/      # REST controllers (/api/...)
│       └── dto/             # DTOs de request/response
├── frontend/                # Angular 20 (standalone)
│   └── src/app/
│       ├── core/            # models.ts, api.service.ts
│       └── features/        # dashboard, subjects, planner, timer, notes, library, analytics, ai-assistant
├── start-backend.sh
└── start-frontend.sh
```

---

## Configuração da IA (OpenAI)

A chave de API está em `backend/src/main/resources/application.properties`:

```properties
openai.api.key=sk-proj-...
openai.model=gpt-4o-mini
```

---

## Design

Interface **Apple Liquid Glass** com:
- Fundo gradiente mesh animado em roxo escuro
- Cards com `backdrop-filter: blur(20px)` e bordas semi-transparentes
- Animações spring e transições suaves
- 100% responsivo (desktop, tablet, mobile)
