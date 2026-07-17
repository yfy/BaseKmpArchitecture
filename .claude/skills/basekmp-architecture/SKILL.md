---
name: basekmp-architecture
description: Use when working in the BaseKmpArchitecture KMP repo — adding or editing feature modules and screens, the shared Kotlin core, Koin DI, Android Compose or iOS SwiftUI UI, auth/session/networking, design tokens, navigation, or localization. Encodes the project's hard rules and conventions so changes stay consistent and pass arch-test + both builds.
---

# BaseKmpArchitecture

Kotlin Multiplatform base: shared Kotlin core (domain + data + presentation state machine) in
`commonMain`, native UI per platform (Jetpack Compose / SwiftUI). Reference doc: `README.md`.

## ⛔ HARD RULES (non-negotiable — apply to every change)

1. **NEVER write comments in code.** No inline comments, no block comments, no KDoc, no Swift `///`
   doc comments — in any Kotlin or Swift you add or modify. Make code self-documenting through naming
   and structure only. Do not "explain" a change with a comment; if it needs explaining, rename it.
   This is the single most important rule.
2. **NEVER create Markdown or documentation files on your own.** No summary docs, no plan/report/notes
   `.md`, no `CHANGELOG`, no "migration guide" — not in the repo root, not in `docs/`, not anywhere.
   The only docs are `README.md`, `CLAUDE.md`, `AGENTS.md`, and the skills under `.claude/skills/`,
   and you touch those **only when explicitly asked**. Report your work in the chat reply instead of
   writing a file.
3. **Every user-facing string in BOTH Turkish and English.** Android: `androidApp/src/main/res/values/strings.xml`
   (TR, default) + `values-en/strings.xml`. iOS: `iosApp/iosApp/Localizable.xcstrings` with `tr` + `en`.
   Keep keys aligned across platforms; reuse EXISTING keys (e.g. `common_ok`, not `action_ok`). Never
   hardcode user text in a ViewModel — VMs emit semantic enums / `AppError`; text is resolved in the UI.
4. **No feature imports another feature** (enforced by `arch-test`). **No bare `Flow` on the Swift
   boundary** (commonMain presentation / `shared` public API). Impls are `internal` + Koin; explicit
   API mode is on, so public surface is deliberate.
5. **Never hand-edit `iosApp/iosApp.xcodeproj/project.pbxproj`.** It uses Xcode 16 file-system
   synchronized groups — any file under `iosApp/iosApp/**` is picked up automatically.
6. **Do not commit unless asked.** After changes, run the relevant builds/tests (see Verification)
   and report results honestly.

## The one principle: Native UI, Shared Core

Domain, data, and the presentation state machine are shared Kotlin. UI and navigation are native and
NOT shared — only `AppRoute` + `parseAppRoute` (in `core:model`) cross over. Android uses Navigation
Compose; iOS uses `NavigationStack`.

## Core principles (see README → Architecture)

1. Contract layer is framework-free (pure Kotlin).
2. `api/impl` is a discipline, not a module split — interface in `commonMain`, impl `internal` + Koin.
3. Prefer `interface + Koin` over `expect/actual`; `expect/actual` only for real platform primitives.
4. One capability = one module; `core:designsystem` is a plain Android library (not KMP).
5. Native UI; navigation not shared; only the route definition + `parseAppRoute` are shared.
6. The Swift boundary carries flat, immutable, conflated state (bridged by SKIE).
7. Exceptions never cross into Swift — `asPhaseFlow()` + `toAppError()` with `CancellationException`
   re-thrown at the front (structured concurrency preserved).
8. Features cannot depend on each other.
9. Export surface is audited — explicit API + `internal` by default + Konsist (`arch-test`).
10. Localization: the VM emits semantic state; text is resolved in the platform UI.

## Module map

Gradle modules (`settings.gradle.kts`): 8 `core/*`, 5 `feature/*`, `arch-test`, `androidApp`, `shared`.
`iosApp` is an Xcode project, NOT a Gradle module.

- `core:model` — pure models + `AppRoute` sealed + `parseAppRoute()` (URI/payload + https universal links → route).
- `core:common` — `BaseViewModel<S, E>`, `asPhaseFlow()`, `toAppError()`, `AppError`; `SessionManager`,
  `AppEventBus`, `TokenStore`, `PermissionController`, `NavigationResultBus`, `CurrentActivityHolder` (androidMain).
- `core:network` — Ktor (`createHttpClient`, engine expect/actual OkHttp/Darwin); bearer+refresh auth,
  `HttpRequestRetry`, `ConnectivityChecker`; mock engine + `MockRoute`.
- `core:database` — Room KMP (`UserCache`); `core:datastore` — DataStore (`PreferencesStore`, `PreferenceKeys`).
- `core:analytics`, `core:notification`, `core:designsystem` (Compose theme + components; colors generated).
- `feature/*` — `auth`, `onboarding`, `profile`, `settings`, `paywall`. Each: `domain/` `data/` `presentation/` `di/`
  (`domain/`+`data/` only when the feature owns business rules or its own data sources — read-only screens
  like profile/settings may consume core repositories directly from the VM).
- `shared` — iOS umbrella framework: exports core+features, `startAppKoin()`, the SKIE `StateBridge.kt`.
- `androidApp` — Compose app; `iosApp` — SwiftUI app. Environment chosen at startup → Koin.

## The single ViewModel base

`core/common/.../base/BaseViewModel.kt`:

```kotlin
abstract class BaseViewModel<S, E>(initialState: S) : ViewModel() {
    val state: StateFlow<S>
    val effects: Flow<E>
    protected fun setState(reducer: S.() -> S)
    protected fun emitEffect(effect: E)
    protected val scope: CoroutineScope
    protected open fun onServiceError(error: AppError)
    protected fun serviceLaunch(onError: (AppError) -> Unit = ::onServiceError, block): Job
    fun clear()
}
```

- `state` = the single immutable `UiState` every screen exposes.
- `effects` = OPTIONAL one-shot events (navigation triggers, toast, permission). Screens with no events
  use `BaseViewModel<XUiState, Nothing>` and never emit. Effects live only in the `base` package so the
  Swift boundary stays clean.
- Each VM overrides `onServiceError(error)` ONCE (map `AppError` → its `UiError` + stop loading) and
  call sites use plain `serviceLaunch { }`. The member wrapper logs a central `app_error` analytics
  event before any override runs — never log errors per-VM.

## Add a feature (recipe)

For `feature/wishlist`:

1. `feature/wishlist/build.gradle.kts` → `plugins { alias(libs.plugins.kmp.feature) }` +
   `android { namespace = "com.yfy.kmp.feature.wishlist" }`; add `include(":feature:wishlist")` to `settings.gradle.kts`.
2. `domain/` — `WishlistRepository` (returns `Flow`), sealed `WishlistOutcome` / `InputError`, use cases.
3. `data/` — `internal class WishlistApi(client, baseUrl)` (pure transport: one suspend fun per endpoint)
   + `internal class WishlistRepositoryImpl(api, ...)` mapping via `sendRequest(recover = { status -> ... })`
   from `core:network`; `internal` DTOs + mappers.
4. `presentation/` — `data class WishlistUiState`, `enum WishlistUiError` (semantic, no text),
   `class WishlistViewModel(...) : BaseViewModel<WishlistUiState, Nothing>(WishlistUiState())` with a
   single `override fun onServiceError(error: AppError)`.
5. `di/WishlistModule.kt` — `single<WishlistRepository> { ... }`, `factory { UseCase(get()) }`, `factory { WishlistViewModel(get()) }`.
6. Wire into `shared/src/commonMain/.../Koin.kt`: add module to `featureModules`, add
   `fun getWishlistViewModel(): WishlistViewModel = KoinPlatform.getKoin().get()`; export in
   `shared/build.gradle.kts` if iOS uses it; add `fun wishlistState(viewModel: WishlistViewModel) = viewModel.state`
   to `shared/src/iosMain/.../StateBridge.kt` (typed SKIE accessor).
7. Route (if navigable): add a case to `AppRoute` (`core:model`); update `parseAppRoute` only for a new path/param.
8. UI both platforms: Android Compose screen under `androidApp/.../feature/wishlist/` + a
   `WishlistNavGraph.kt` in the same package (`internal fun NavGraphBuilder.wishlistGraph(nav)` holding
   its `composable<>` blocks and any feature-private routes; wrap every click-driven nav lambda in
   `dropUnlessResumed { }` — a double-tap otherwise pops the root and blanks the NavHost), then ONE
   `wishlistGraph(nav)` line in `AppNavHost.kt`; iOS SwiftUI view + model under `iosApp/iosApp/Features/Wishlist/` + a case in
   `AppDestination.swift`, a `WishlistDestinationView` struct in the feature folder (single-screen
   features may inline directly), then ONE grouped case in `ContentView.view(for:)` and its
   `screenName(for:)` entry — the exhaustive switch makes the compiler point at both (no pbxproj edit).
9. TR+EN strings; `commonTest` VM + repository tests; verify (below).

## Feature-to-feature: forbidden; share via core

Promote shared parts to `core`: types → `core:model`; behavior → a `core` interface + Koin; persisted
data → `core:datastore` / `core:database`. Runtime communication (all in `core:common`):

- `SessionManager` — current-user / login state.
- `AppEventBus` — app-wide one-shot events (`AppEvent.LoggedOut`, `AppEvent.SessionExpired`).
- `NavigationResultBus` — cross-screen result: `post(key, value)` / `resultsFor(key)`.

## iOS specifics

- **SKIE bridge:** `state`/`effects` are declared on the generic base, so the ObjC export erases the
  type argument; `StateBridge.kt` exposes concrete typed accessors (`loginState(viewModel:)` →
  `SkieSwiftStateFlow<LoginUiState>`). Swift shells (`StateScreenModel` / `BaseScreenModel`) consume
  with `Task { for await }` and publish to `@Published`. `VMOwner` calls `clear()` on `deinit`.
- **Synchronized groups** (`objectVersion 71`): new files under `iosApp/iosApp/**` auto-include; never
  hand-edit pbxproj. The `DesignSystem` Swift Package lives at `iosApp/Packages/DesignSystem`.
- **Navigation root** (`ContentView.swift`): after auth, **Home is the root** — `goHome()` sets
  `start = .home`, `path = []` (so Back never returns to Login). Logout / `SessionExpired` flips the
  root back to Login. Inner screens push onto Home.
- **Localization:** `L(key)` reads `AppL.bundle`; use existing keys. Apply `.appScreenBackground()`
  (DesignSystem) to full screens so they match the login gradient.

## Auth / session / networking

- `SessionManager` (single source of "logged in"): `isLoggedIn`, `currentUserId`, `setLoggedIn(id)`,
  `logout(reason)`. Logout clears session id + cached user + tokens and emits an `AppEvent`; both
  navigation shells observe the bus and reset to login through one path (voluntary + forced 401 alike).
- `TokenStore` (interface + Koin): iOS Keychain / Android EncryptedSharedPreferences. Login/signup
  persist tokens; mock fixtures carry dummy tokens so the storage path runs under `mock`.
- `createHttpClient` (`core:network`) for `dev`/`prod` installs Ktor `Auth` bearer (load + refresh
  via `POST /auth/refresh`; on failure → `SessionManager.logout(SessionExpired)`), `HttpRequestRetry`
  (5xx + IO/timeout, backoff), and a `ConnectivityChecker` (`ConnectivityManager` / `NWPathMonitor`)
  that throws `NoConnectivityException` when offline.
- `AppError` cases: `NoConnectivity`, `Timeout`, `Network`, `Server(code)`, `Unauthorized`, `Unknown`.
  Map via `toAppError()`; map to the feature's `UiError` enum in the VM.

## Error handling — where each piece lives

- **Expected business results are VALUES.** The repository maps them via `sendRequest(recover = { status -> ... })`
  into the feature's sealed outcomes (`LoginOutcome.InvalidCredentials`, …). Never model an expected
  result as an exception.
- **Infrastructure failures are TYPED EXCEPTIONS** (`core:common`: `NetworkException`, `ServerException`,
  `TimeoutException`, `NoConnectivityException`) mapped to `AppError` via `toAppError()` at the VM edge.
- **Each ViewModel overrides `onServiceError(error: AppError)` ONCE per class** (map `AppError` → the
  feature's `UiError` + stop loading). Call sites use plain `serviceLaunch { }` — it defaults to
  `onServiceError`. Pass `serviceLaunch(onError = ...)` only for a screen-specific reaction.
- **Error analytics is centralized** in `BaseViewModel.serviceLaunch` (an `app_error` event fires before
  any override runs). Never log errors per-VM.
- **Session expiry is TRANSPORT-level:** the Ktor bearer refresh hook → `SessionManager.logout` →
  `AppEventBus` → both navigation shells. Never handle it per-callsite, per-usecase, or per-VM.
- **`sendRequest` is Ktor-HTTP ONLY.** A different backend dialect (Firebase/Supabase SDK, envelope
  responses) gets its own sibling translator in the data layer that keeps `SendRequestTest`'s contract
  green (CancellationException rethrown first; expected results → values via recover; everything else →
  the typed exceptions above; single emission). Status codes and SDK exceptions NEVER appear in
  `domain` or `presentation` packages — the Konsist rule `domain and presentation do not import ktor`
  fails the build otherwise.

## Design tokens (single source)

Colors live ONCE in `design-tokens.json`. `./gradlew generateDesignTokens` writes Android
`DesignTokens.kt` (consumed by `AppColors`) + iOS `DesignTokens.swift` (consumed by `AppTheme`). It
runs before `core:designsystem` compiles and before the iOS framework build phase. Never hand-edit a
color in two places — edit the JSON and rebuild.

## Other capabilities

- **Permissions:** `PermissionController` (interface + Koin); Android needs the ActivityResult seam
  (`AndroidPermissionRequester`) wired in `MainActivity`. Call `getPermissionController().request(...)`.
- **Universal links:** placeholder `example.com`; AASA + assetlinks templates in `universal-links/`;
  iOS entitlement + `onContinueUserActivity`, Android `autoVerify` intent-filter; `parseAppRoute` handles https.
- **WebView:** Android `AppWebView`, iOS `WebView` / `WebViewScreen` (used for Terms/Privacy).

## Environments

`mock` / `dev` / `prod` = Koin module swap (`mockNetworkModule(routes)` / `debugNetworkModule` /
`prodNetworkModule`). Mock JSON lives once in `mock-resources/`; Gradle copies it to both platforms.
MockEngine answers with a 400 ms delay (and `MockBillingClient` likewise) so loading states stay
visible; tests use the 0 ms default. Android picks via flavor → `BuildConfig.APP_ENVIRONMENT` (mock
and dev flavors override the launcher name and icon background so installs are distinguishable);
iOS via the Info.plist `APP_ENVIRONMENT` key (fed by an Xcode build setting; empty resolves to MOCK).

Every value an adopter must replace carries the `TODO(template): replace before release` marker —
when adding a new placeholder (key, id, URL), tag it the same way.

## Verification (run after changes)

```bash
./gradlew :arch-test:test
./gradlew testDebugUnitTest
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
./gradlew :androidApp:assembleDebug
cd iosApp && xcodebuild -scheme iosApp -project iosApp.xcodeproj \
  -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' build CODE_SIGNING_ALLOWED=NO
```

Tests are `commonTest` VM + repository tests (run on both platforms) + Konsist `arch-test` + thin iOS
XCUITests (`iosApp/iosAppUITests/`). Xcode 16+ is required (SKIE Swift generation + synchronized groups).

The 8 Konsist rules: feature isolation, no platform imports in commonMain, no `io.ktor` in
domain/presentation, no bare `Flow` at the Swift boundary, ViewModels extend `BaseViewModel`, no
androidMain `BaseViewModel` subclasses, repository impls live in `data`, commonMain `Impl` classes
are `internal`.

## When unsure

Match existing patterns in the nearest sibling file; see `README.md` for the architecture. And never
add comments.
