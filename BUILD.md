# Como gerar o .AAB (Android App Bundle)

O `.aab` é o pacote que você envia para a Google Play Store. Há duas formas de gerá-lo.

---

## Opção A — Automático pelo GitHub Actions (recomendado)

A cada `push` na branch `main`, o GitHub compila o app e disponibiliza o `.aab`
para download. Para que ele saia **assinado** (pronto para a Play Store), configure
4 *secrets* no repositório.

### 1. Adicionar os secrets

No GitHub: **Settings → Secrets and variables → Actions → New repository secret**.
Crie estes 4:

| Secret | Valor |
|---|---|
| `FINAX_KEYSTORE_BASE64` | conteúdo do arquivo `finax-keystore-base64.txt` |
| `FINAX_KEYSTORE_PASSWORD` | `Finax@2026!` |
| `FINAX_KEY_ALIAS` | `finax` |
| `FINAX_KEY_PASSWORD` | `Finax@2026!` |

> O keystore e a senha acima foram gerados automaticamente. **Guarde o arquivo
> `finax-release.jks` e a senha em local seguro** — é com essa chave que o app é
> assinado. Se você perdê-la, não conseguirá publicar atualizações com a mesma
> identidade (a não ser que use o Play App Signing da Google).

### 2. Rodar o build

- Faça qualquer `push` na `main`, **ou**
- Vá em **Actions → Build AAB → Run workflow**.

### 3. Baixar o resultado

Ao terminar, abra a execução em **Actions** e baixe os artefatos:
- **finax-release-aab** → o `.aab` para a Play Store
- **finax-debug-apk** → um `.apk` de teste para instalar direto no celular

Se você não configurar os secrets, o build ainda roda, mas gera um `.aab`
**não assinado** (serve para teste, não para publicar).

---

## Opção B — Android Studio (no seu PC)

1. Instale o [Android Studio](https://developer.android.com/studio).
2. **File → Open** e selecione esta pasta do projeto.
3. Aguarde o **Gradle sync** terminar.
4. **Build → Generate Signed Bundle / APK → Android App Bundle**.
5. Selecione o keystore `finax-release.jks` (alias `finax`, senha `Finax@2026!`)
   ou crie um novo.
6. Escolha a variante **release** e finalize. O `.aab` será gerado em
   `app/build/outputs/bundle/release/`.

---

## Versão do app

Antes de publicar uma atualização, aumente em `app/build.gradle.kts`:

```kotlin
versionCode = 1      // incremente a cada envio: 2, 3, 4...
versionName = "1.0"  // versão visível ao usuário: "1.1", "2.0"...
```
