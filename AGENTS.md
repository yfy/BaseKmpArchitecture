# Base KMP Architecture — agent guide (AGENTS.md)

Kotlin Multiplatform template: one shared Kotlin core (domain + data + presentation state machine)
in `commonMain`, fully native UI per platform (Jetpack Compose / SwiftUI). The deep rulebook is
`.claude/skills/basekmp-architecture/SKILL.md` — read it before structural changes. `README.md` is
the human-facing doc.

## Hard rules (summary — full versions in the skill)

1. NEVER write code comments (no inline, block, KDoc, or `///`). Existing comments are deliberate
   trap-guards — do not add to them.
2. NEVER create Markdown/doc files on your own. The only docs are `README.md`, this file,
   `CLAUDE.md`, and the skills — touch them only when explicitly asked.
3. Every user-facing string in BOTH Turkish and English (`values/` + `values-en/`, `Localizable.xcstrings`).
4. No feature imports another feature; no bare `Flow` on the Swift boundary; `io.ktor` never in
   `domain`/`presentation` packages (all Konsist-enforced by `arch-test`).
5. Never hand-edit `iosApp/iosApp.xcodeproj/project.pbxproj` — Xcode 16 synchronized groups pick up
   any file under `iosApp/iosApp/**` automatically.
6. Do not commit unless asked.

## Module map

- `core:model` — pure models, `AppRoute` + `parseAppRoute()` (deep/universal links).
- `core:common` — `BaseViewModel` (state/effects/`onServiceError`/`serviceLaunch`), `AppError` +
  typed exceptions, `SessionManager`, `AppEventBus`, `TokenStore`, `PermissionController`,
  `NavigationResultBus`.
- `core:network` — Ktor client (bearer+refresh, retry, timeout, connectivity), `sendRequest`
  translator, `MockEngine` + `MockRoute` fixtures.
- `core:database` (Room KMP) · `core:datastore` (DataStore) · `core:analytics` · `core:ads` ·
  `core:notification` · `core:designsystem` (Android-only Compose; iOS design system is a Swift
  Package at `iosApp/Packages/DesignSystem`).
- `feature:*` — auth, onboarding, profile, settings, paywall; split `domain/ data/ presentation/ di/`
  (domain/data only when the feature owns rules or sources; read-only screens may use core repos directly).
- `shared` — iOS umbrella framework: exports, `startAppKoin()`, SKIE `StateBridge.kt`.
- `androidApp` / `iosApp` — native UIs. `arch-test` — Konsist rules (run first when unsure).

## Error handling (settled architecture — do not re-litigate)

- Expected business results are VALUES: repository maps status codes via `sendRequest(recover)` into
  sealed outcomes. Infra failures are typed exceptions → `toAppError()` at the VM edge.
- Each VM overrides `onServiceError(error)` once; call sites use plain `serviceLaunch { }`.
- Session expiry is transport-level: bearer refresh hook → `SessionManager.logout` → `AppEventBus` →
  both navigation shells. Never handled per-callsite.
- `sendRequest` is Ktor-HTTP only. A different backend dialect gets its own sibling translator in the
  data layer that keeps `SendRequestTest`'s contract green.

## Environments

`mock` / `dev` / `prod` = Koin module swap (`Koin.kt`). Mock JSON lives once in `mock-resources/`;
MockEngine serves it with a 400 ms delay so loading states are visible. Android flavor →
`BuildConfig.APP_ENVIRONMENT`; iOS → Info.plist `APP_ENVIRONMENT` (build-setting-fed; empty = MOCK).

## Search hygiene (token savers)

- Always exclude `build/` from greps/reads — generated Room/KSP sources there shadow real code.
- Placeholders are greppable: `grep -rn "TODO(template)"` returns every value an adopter must replace.
- The ~70 surviving comments repo-wide each guard a named trap; deleting or adding comments is a
  reviewed decision, not cleanup.

## Verify after changes

```bash
./gradlew :arch-test:test
./gradlew testDebugUnitTest
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
./gradlew :androidApp:assembleDebug
cd iosApp && xcodebuild -scheme iosApp -project iosApp.xcodeproj \
  -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' build CODE_SIGNING_ALLOWED=NO
```

Report results honestly; a failed step is reported, not hidden.
