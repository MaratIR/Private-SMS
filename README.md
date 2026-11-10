# Private Msg

A small Android experiment for geeks: **encrypted chat over plain SMS**.

When there is **no internet** (or you simply do not want an IP messenger), the mobile network can still deliver SMS. This app uses that channel: messages look like normal SMS, but the body is ciphertext with a shared secret you agree on beforehand.

> Not a Signal replacement. Not “for everyone”. Just a friendly idea: *SMS as a transport when the network stack you usually rely on is gone.*

---

## Idea (EN)

- Both people install the app.
- You create a chat with their number and enter the **same passphrase/code** (agreed out of band).
- Outgoing SMS body: `prvtsnd:[encrypted payload]`.
- Crypto: AES, key derived from the passphrase via PBKDF2.
- The other phone finds SMS with the `prvtsnd:` prefix, decrypts, and shows them in the chat UI.

## Идея (RU)

- Оба ставят приложение.
- Создаёте чат с номером собеседника и вводите **один и тот же код** (договариваетесь заранее, не в приложении).
- В SMS уходит строка вида `prvtsnd:[зашифрованный текст]`.
- Шифрование: AES, ключ из пароля через PBKDF2.
- У получателя приложение ловит SMS с префиксом `prvtsnd:`, расшифровывает и показывает в чате.

Интернет не нужен для доставки — нужен обычный SMS у оператора. Это запасной / «гиковский» канал, не повседневный мессенджер.

---

## Permissions

- Send / receive SMS  
- Read SMS (to find `prvtsnd:` messages)  
- Contacts (pick a recipient)

## Create a chat

1. Tap **New chat**.
2. Pick a contact.
3. Enter the shared **code**.
4. Save — messages go out as encrypted SMS (`prvtsnd:...`).

The other person does the same with your number and the **same code**.

---

## Build APK

Needs **JDK 17+** and **Android SDK** (`ANDROID_HOME`).

### Gradle Wrapper (once)

Put `gradle-wrapper.jar` into `gradle/wrapper/` (Gradle **8.14.4**):

```bash
curl -L -o gradle/wrapper/gradle-wrapper.jar \
  "https://github.com/gradle/gradle/raw/v8.14.4/gradle/wrapper/gradle-wrapper.jar"
```

### Debug

```bash
# Windows
gradlew.bat assembleDebug

# Linux / macOS / Git Bash
./gradlew assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

### Signed release (optional)

```bash
keytool -genkey -v -keystore privatemsg-release.keystore -alias privatemsg \
  -keyalg RSA -keysize 2048 -validity 10000
```

In `local.properties` (never commit this file):

```properties
release.storeFile=privatemsg-release.keystore
release.storePassword=...
release.keyAlias=privatemsg
release.keyPassword=...
```

```bash
./gradlew assembleRelease
```

APK: `app/build/outputs/apk/release/app-release.apk`

Sideloading may show Play Protect warnings — normal for apps outside the store.

### Android Studio

**File → Open** → project folder → **Build → Build APK(s)**.

---

## Limits (honest)

- Shared passphrase model (no modern key exchange / forward secrecy like Signal).
- SMS length and operator quirks apply.
- Metadata (who texted whom, when) is still visible to the carrier.
- MVP / hobby project — review crypto if you rely on it seriously.

---

## License

MIT (or set your preferred license before publishing).
