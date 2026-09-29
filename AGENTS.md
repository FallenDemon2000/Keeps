# Keeps project guide

Use this file as a map to project resources; keep product requirements and design details in their source files.

## Product and design references

- Behavior requirements: `specs/keeps-design.md` (not present yet; consult it when added).
- UI mockups: [`specs/keeps-design.html`](specs/keeps-design.html). Consult before creating or changing UI.
- Fonts: [`androidApp/src/main/res/font/dm_sans_variable.ttf`](androidApp/src/main/res/font/dm_sans_variable.ttf) and [`androidApp/src/main/res/font/jetbrains_mono_variable.ttf`](androidApp/src/main/res/font/jetbrains_mono_variable.ttf).

## KMP and native UI

- Android app and native Compose UI: `androidApp/`.
- iOS app and native SwiftUI UI: `iosApp/iosApp/`.
- Shared Kotlin logic and platform-specific Kotlin code: `shared/src/`.
- Keep Android and iOS UI platform-specific; do not introduce shared UI.

## Android skills

Read the relevant skill before work in its area. These are all skills currently present under `.agents/skills/`:

| Skill | Location | Description |
|---|---|---|
| `android-compose-ui` | [`.agents/skills/android-compose-ui/SKILL.md`](.agents/skills/android-compose-ui/SKILL.md) | Compose screen/component structure, state ownership, design systems, navigation boundaries, recomposition, effects, previews, and accessibility. |
| `android-data-layer` | [`.agents/skills/android-data-layer/SKILL.md`](.agents/skills/android-data-layer/SKILL.md) | KMP data sources and repositories, DTOs/entities, mapping, Ktor, error handling, token storage, and offline-first patterns. |
| `android-di-koin` | [`.agents/skills/android-di-koin/SKILL.md`](.agents/skills/android-di-koin/SKILL.md) | Koin modules, dependency provisioning, ViewModel injection, and application wiring. |
| `android-error-handling` | [`.agents/skills/android-error-handling/SKILL.md`](.agents/skills/android-error-handling/SKILL.md) | Typed results and errors, including mapping and handling failures across layers. |
| `android-module-structure` | [`.agents/skills/android-module-structure/SKILL.md`](.agents/skills/android-module-structure/SKILL.md) | KMP/Android module layout, dependency boundaries, Gradle convention plugins, and version catalogs. |
| `android-navigation` | [`.agents/skills/android-navigation/SKILL.md`](.agents/skills/android-navigation/SKILL.md) | Type-safe Compose navigation, routes, feature graphs, and app-level wiring. |
| `android-presentation-mvvm` | [`.agents/skills/android-presentation-mvvm/SKILL.md`](.agents/skills/android-presentation-mvvm/SKILL.md) | Presentation architecture using ViewModels, StateFlow, screen state/actions, UI mapping, and one-time events. This is the available presentation skill; there is no MVI-specific skill here. |
| `android-testing` | [`.agents/skills/android-testing/SKILL.md`](.agents/skills/android-testing/SKILL.md) | Android/KMP tests for ViewModels, repositories, use cases, and Compose UI. |

## Custom agents

These are all custom agents currently present under `.agents/agents/`:

| Agent | Location | Description |
|---|---|---|
| `android-development` | [`.agents/agents/android-development.agent.md`](.agents/agents/android-development.agent.md) | Implements Android features using Kotlin, Compose, ViewModels, MVVM, and established architecture; takes work through implementation and testing. |
| `ai-retrospective` | [`.agents/agents/ai-retrospective.agent.md`](.agents/agents/ai-retrospective.agent.md) | Reviews a session for reusable Android learnings and recommends updates to existing skills or creation of new ones. |
