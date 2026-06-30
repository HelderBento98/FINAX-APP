# Finax App

Aplicativo Android nativo da **Varetas Brasil** — sistema de gestão de ordens de serviço
(orçamentos, pagamentos, lembretes e relatórios) para pequenas empresas.

Construído em **Kotlin + Jetpack Compose**, com foco em publicação na Google Play Store.

## Stack

- **Kotlin** + **Jetpack Compose** (Material 3)
- **Room** — persistência de ordens de serviço e lembretes
- **DataStore Preferences** — perfil/configurações da empresa
- **Navigation Compose** — navegação entre telas
- **Coil** — carregamento de imagens (logo, QR Code PIX)
- **Gson** — backup/restauração em JSON
- **AlarmManager + BroadcastReceiver** — notificações de lembretes
- **PdfDocument** — geração de orçamentos, extratos e garantias em PDF

## Arquitetura

Clean architecture: `data` (Room + DataStore + Repository) → `viewmodel` (StateFlow) → `ui` (Compose).

```
app/src/main/java/com/finax/app/
├── data/          # model, db (Room), preferences (DataStore), repository
├── viewmodel/     # AppViewModel + AppUiState
├── notifications/ # ReminderReceiver + ReminderScheduler
├── utils/         # FormatUtils, PixUtils (CRC16), PdfUtils
└── ui/            # theme, components, navigation, screens
```

## Telas

Início · Nova OS · Histórico · Lista de OS · Detalhes da OS · Calendário · Resumo Detalhado · Ajustes

## Como rodar

1. Abrir a pasta do projeto no **Android Studio**
2. Aguardar o **Gradle sync**
3. Rodar em um emulador ou device Android (minSdk 26 / targetSdk 35)

Para gerar o pacote de publicação: **Build → Generate Signed Bundle / APK**.
