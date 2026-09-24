---
name: android-compose-ui
description: |
  Portable Jetpack Compose UI guidance for Android/KMP: state ownership, screen/component architecture, MaterialTheme-based design systems, typed navigation boundaries, recomposition, lazy layouts, side effects, previews, accessibility, and UI refactoring. Use when creating or reviewing composables, screens, reusable components, navigation shells, design systems, or Compose performance.
---

# Android Compose UI Engineering

## Core principle

Keep composables declarative and UI-focused. A composable should render supplied state and forward user actions through callbacks. Business rules, persistence, data transformation, validation, and navigation decisions belong in ViewModels, domain code, repositories, or the app navigation layer.

Prefer a root/screen split:

- A root composable collects ViewModel state with lifecycle awareness and translates UI events into actions.
- A stateless screen composable receives stable state and callbacks.
- Reusable components receive typed values and callbacks rather than reaching into feature state.

Do not hide application state in a reusable component. A component may own only transient Compose-internal state such as `LazyListState`, `ScrollState`, or `PagerState`.

## State ownership and recomposition

Keep application state in a ViewModel `StateFlow` and collect it with `collectAsStateWithLifecycle()`. Do not use `remember` or `rememberSaveable` for form state, selections, loading state, or persisted UI decisions.

Use `@Stable` only when a state type contains fields Compose cannot treat as stable, such as mutable or interface-based collections. Do not add stability annotations speculatively.

Keep derived business values in the ViewModel or domain layer. If a value is derived only from Compose-owned state, `derivedStateOf` may be appropriate.

For lazy layouts, provide a `key` whenever the item has an obvious unique identifier:

```kotlin
LazyColumn {
    items(items = state.items, key = { it.id }) { item ->
        ItemRow(item = item, onClick = { onAction(Action.Open(item.id)) })
    }
}
```

## MaterialTheme as the design-system contract

Use `MaterialTheme.colorScheme` and `MaterialTheme.typography` directly in components and screens. Configure product-specific colors, font families, weights, and sizes in the theme definition rather than creating parallel global objects such as `AppColors` or `AppTextStyles`.

This keeps components theme-aware, makes previews and alternate themes work automatically, and avoids duplicate sources of truth.

Use semantic theme roles where possible:

| Need | Prefer |
|---|---|
| screen background | `colorScheme.background` |
| card/container | `colorScheme.surface` |
| nested/elevated surface | `colorScheme.surfaceVariant` or a deliberately configured container role |
| primary action | `colorScheme.primary` / `onPrimary` |
| supporting accent | `colorScheme.secondary` |
| body/heading text | `colorScheme.onSurface` |
| subdued text | `colorScheme.onSurfaceVariant` |
| destructive action | `colorScheme.error` |
| typography | `MaterialTheme.typography.*` |

Use `.copy(...)` only for a local variation, such as a different size or color. If the variation recurs across screens, add or configure an appropriate Material typography or color role instead of introducing another token object.

Apply the theme and a full-size background surface at the activity/app root:

```kotlin
AppTheme {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        AppRoot()
    }
}
```

## Component organization

Put reusable UI primitives in a dedicated presentation component package, for example `presentation/ui/components`. Give each meaningful component its own file:

- buttons
- cards/surfaces
- text fields
- icon buttons
- selectors/chips
- progress indicators
- headers
- badges or checkboxes

Remove aggregate “design system” files once their contents are split. One-component-per-file improves discoverability, reviewability, reuse, and incremental maintenance.

Design-system components should:

- be generic and feature-independent;
- use `MaterialTheme` rather than hard-coded product tokens;
- expose typed parameters and callbacks;
- support a `Modifier` parameter, normally first after required parameters;
- use slot APIs only where flexible content is genuinely part of the component contract;
- avoid feature-specific navigation, repository calls, or domain models.

Feature-specific compositions may remain beside their screen, but should compose the shared primitives rather than duplicate their styling.

## Screen and navigation structure

Keep the activity responsible for system setup and the themed app root. Let the app root own a `Scaffold` when the app has global layout concerns, then delegate navigation to a private or separately defined `AppNavHost`:

```kotlin
@Composable
fun AppRoot() {
    val navController = rememberNavController()
    Scaffold { padding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(padding)
        )
    }
}
```

Inside the navigation host, extract repeated navigation behavior into local callbacks:

```kotlin
val onNavigate: (Route) -> Unit = { navController.navigate(it) }
val onBackClick = { navController.popBackStack() }
```

Use typed, serializable route objects instead of string route names and manually assembled argument paths. Prefer `data object` routes for destinations without arguments and `data class` routes for scalar arguments. Pass IDs, not complex domain objects; load destination data in the destination ViewModel.

Keep navigation wiring centralized. Screens should expose callbacks such as `onOpenDetails`, `onSave`, and `onBack`, rather than importing or manipulating a global `NavController`.

## Side effects

Avoid side effects in screen rendering. Dispatch an action to the ViewModel when work belongs to application logic. When an Android lifecycle or platform API must be observed from Compose, isolate it in a small dedicated effect composable using `DisposableEffect` or `LaunchedEffect`.

Do not use broad effects to compensate for unclear state ownership, and do not introduce custom `CompositionLocal`s unless there is a demonstrated cross-cutting dependency that cannot be modeled through parameters or the theme.

## Text input and forms

Text input state belongs in the ViewModel. Forward every change as an action:

```kotlin
TextField(
    value = state.title,
    onValueChange = { onAction(Action.TitleChanged(it)) }
)
```

Expose validation state from the ViewModel and render it explicitly. Do not silently reject invalid input in a button callback. Keep input normalization and domain validation in the appropriate state/domain layer.

## Animations and performance

Avoid unnecessary recomposition during animations. Prefer `graphicsLayer`, offset lambdas, or `Canvas` for visual animation. If using `animateFloatAsState`, apply the result in a draw/graphics phase when possible.

Defer state reads to layout or draw phases when the value only affects rendering. Do not optimize prematurely; first keep state stable, lazy content keyed, and component boundaries clear.

## Previews

Every screen and important reusable component should have a meaningful preview wrapped in the real app theme. Use realistic representative state and include separate empty, error, or selected-state previews when those states materially affect layout.

Previews should not instantiate repositories or ViewModels. Supply sample state directly.

## Accessibility and localization

Give every interactive icon or visual control a meaningful `contentDescription`; use `null` only for decorative graphics. Prefer string resources for user-visible text and accessibility labels so UI can be localized.

Ensure controls have a clear semantic role, sufficient touch target size, readable contrast, and distinct selected/disabled/error states. Do not communicate important state through color alone.

## Refactoring workflow

Before changing a Compose UI layer:

1. Inspect the existing theme, navigation shell, screen boundaries, and component usages.
2. Identify the semantic theme roles needed and map them to `MaterialTheme`.
3. Separate reusable primitives from feature-specific composition.
4. Define typed route objects and centralize navigation wiring.
5. Make components stateless and move application state upward.
6. Compile after structural changes, then inspect for stale package imports or duplicate token systems.
7. Verify previews and interaction boundaries, especially nested clickable controls such as a card with a checkbox.

Avoid large unrelated refactors. When moving components, update package declarations and imports together, remove obsolete aggregate files, and verify the final package tree and clean build.

## Common pitfalls

- Keeping a custom color/style singleton after migrating to `MaterialTheme`, creating two competing design systems.
- Hard-coding colors in screens instead of assigning semantic roles in the theme.
- Using string navigation routes with manually concatenated arguments.
- Passing complex objects through navigation.
- Letting reusable components own ViewModel or repository state.
- Using `remember` for persisted form state.
- Omitting lazy item keys when stable IDs exist.
- Making a whole card clickable without preventing an inner checkbox/button from receiving its own action.
- Hiding validation failures with early returns and no visible error state.
- Leaving stale packages or imports after a directory refactor.
