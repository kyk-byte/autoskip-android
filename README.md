# AutoSkip for Android

[English](#english) · [Русский](#русский)

> Stable download: `0.1.0`. Public beta: `0.2.0-beta.2` with experimental TikTok support. YouTube and TikTok can change their interfaces without notice, so detection may temporarily stop working after an app update.

## English

AutoSkip automatically activates YouTube's own visible **Skip** button. It uses Android Accessibility Service and does nothing until a visible, enabled, clickable skip control becomes available.

### Download

**[Download the latest APK](https://github.com/kyk-byte/autoskip-android/releases/latest/download/AutoSkip-latest.apk)**

**[Download the TikTok beta](https://github.com/kyk-byte/autoskip-android/releases/download/v0.2.0-beta.2/AutoSkip-0.2.0-beta.2.apk)**

Alternative: open [all releases](https://github.com/kyk-byte/autoskip-android/releases) and download the APK attached to the newest version.

Releases starting with `0.2.0-beta.2` use one persistent release certificate, so future versions can update normally. Android, your browser, or Play Protect may still warn about an app installed outside Google Play. Download only from this repository. If Chrome blocks the file, open **Downloads**, select the APK, and confirm **Download anyway**. Installation may also require **Settings → Install unknown apps → Allow from this source** for your browser or file manager.

### Compatibility

- Android 8.0 or newer (`minSdk 26`).
- Android 14 is supported by the project configuration.
- Built with Android SDK 35 for Android 8.0–15.
- One universal APK supports ARM, ARM64, x86, and x86_64.
- Actual behavior depends on YouTube exposing its Skip button to Android Accessibility.

### Install and enable

1. Download `AutoSkip-latest.apk` using the link above.
2. Open the downloaded file and allow installation from your browser or file manager if Android asks.
3. Install and open AutoSkip.
4. Tap **Open settings**.
5. Open **Installed apps/services** and select **AutoSkip for YouTube**.
6. If Android says the setting is restricted, open AutoSkip's system **App info**, use the top-right menu or warning banner, and choose **Allow restricted settings**. This is an Android protection for sideloaded accessibility apps and cannot be granted silently by AutoSkip.
7. Read Android's warning, then enable the service.
8. Return to AutoSkip and enable its main switch.
9. Enable YouTube and, in the beta, TikTok separately.

Some Android vendors restrict background services. If AutoSkip stops after leaving the app, allow background activity and remove battery optimization for AutoSkip in system settings.

### Privacy and limitations

- Monitors only the selected YouTube and TikTok packages.
- Matches Russian and English skip labels.
- Checks visibility, enabled state, and clickability.
- Stores settings and statistics only on the device.
- Requests no `INTERNET` permission.
- Does not block unskippable ads, filter network traffic, modify YouTube, or access a Google account.

Independent project. Not affiliated with or endorsed by Google or YouTube.

---

## Русский

AutoSkip автоматически нажимает штатную видимую кнопку **«Пропустить»** в YouTube. Приложение использует Android Accessibility Service и ничего не делает, пока доступная, активная и кликабельная кнопка пропуска не появилась.

### Скачать

**[Скачать последнюю версию APK](https://github.com/kyk-byte/autoskip-android/releases/latest/download/AutoSkip-latest.apk)**

**[Скачать beta с поддержкой TikTok](https://github.com/kyk-byte/autoskip-android/releases/download/v0.2.0-beta.2/AutoSkip-0.2.0-beta.2.apk)**

Альтернативный вариант: откройте [все релизы](https://github.com/kyk-byte/autoskip-android/releases) и скачайте APK из самой новой версии.

Начиная с `0.2.0-beta.2`, релизы подписываются одним постоянным сертификатом, поэтому следующие версии смогут обновляться поверх установленной. Android, браузер или Play Protect всё равно могут предупредить, что приложение устанавливается не из Google Play. Скачивайте APK только из этого репозитория. Если Chrome блокирует файл, откройте **Загрузки**, выберите APK и подтвердите **Всё равно скачать**. Для установки также может потребоваться открыть **Настройки → Установка неизвестных приложений** и разрешить установку браузеру или файловому менеджеру.

### Совместимость

- Android 8.0 и новее (`minSdk 26`).
- Android 14 поддерживается конфигурацией проекта.
- Сборка использует Android SDK 35 и предназначена для Android 8.0–15.
- Один универсальный APK поддерживает ARM, ARM64, x86 и x86_64.
- Фактическая работа зависит от того, показывает ли YouTube кнопку пропуска в Android Accessibility.

### Установка и включение

1. Скачайте `AutoSkip-latest.apk` по ссылке выше.
2. Откройте файл и разрешите браузеру или файловому менеджеру установку приложений из этого источника, если Android попросит.
3. Установите и запустите AutoSkip.
4. Нажмите **«Открыть настройки»**.
5. Откройте список установленных приложений/служб и выберите **«AutoSkip для YouTube»**.
6. Если Android пишет, что настройка ограничена, откройте системную страницу **«О приложении»** AutoSkip, нажмите меню справа сверху или красное предупреждение и выберите **«Разрешить ограниченные настройки» / «Удалить запрет»**. Это защита Android для Accessibility-приложений, установленных APK-файлом; AutoSkip не может выдать это разрешение сам себе.
7. Прочитайте системное предупреждение и включите службу.
8. Вернитесь в AutoSkip и включите главный переключатель.
9. Отдельно включите YouTube и, в beta-версии, TikTok.

Некоторые оболочки Android ограничивают фоновые службы. Если AutoSkip останавливается после выхода из приложения, разрешите фоновую работу и отключите оптимизацию батареи для AutoSkip в системных настройках.

### Приватность и ограничения

- Следит только за выбранными пакетами YouTube и TikTok.
- Распознаёт русские и английские подписи кнопки пропуска.
- Проверяет видимость, активность и кликабельность элемента.
- Хранит настройки и статистику только на устройстве.
- Не запрашивает разрешение `INTERNET`.
- Не блокирует непропускаемую рекламу, не фильтрует трафик, не изменяет YouTube и не получает доступ к аккаунту Google.

Независимый проект. Не связан с Google или YouTube и не одобрен ими.

---

## Development / Разработка

Requirements / Требования:

- JDK 17
- Android SDK 35

```powershell
.\gradlew.bat test assembleDebug
```

Every push to `main` runs tests and stores an APK artifact. A tag such as `v0.2.0` creates a public GitHub Release containing both versioned and stable download filenames.

Каждый push в `main` запускает тесты и сохраняет APK-artifact. Тег вида `v0.2.0` создаёт публичный GitHub Release с версионным и постоянным именами APK.

Version is configured in `app/build.gradle`. Increase both values before the next release:

```groovy
versionCode 3
versionName "0.2.0-beta.2"
```

## License / Лицензия

[MIT](LICENSE)
