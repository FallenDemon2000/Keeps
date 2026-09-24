---
name: android-presentation-mvvm
description: |
  Android/KMP presentation architecture with domain use cases, StateFlow-based ViewModels, explicit screen contracts, UI mapping, one-time events, and previewable Compose views. Use when creating or reviewing ViewModels, screen state, actions, events, domain-to-UI mapping, Compose screen boundaries, or process-death handling.
---

# Android Presentation Architecture

Use this skill when connecting Android UI to application/domain behavior. The goal is to keep composables focused on rendering, ViewModels focused on presentation state and orchestration, and domain use cases focused on business operations.

## Layer boundaries

Prefer this dependency direction:

```text
Compose UI -> ViewModel -> UseCase -> Repository/Data source
```

- **UI** renders presentation models and forwards user intent.
- **ViewModel** owns screen state, launches coroutines in `viewModelScope`, invokes use cases, and translates results into UI state/events.
- **UseCase** represents one business operation and exposes `operator fun invoke(...)`. It may coordinate repositories or domain services, but should not depend on Compose or Android UI types.
- **Repository/data source** owns persistence, networking, caching, and data mapping.

Do not let composables call repositories or data sources directly. Do not make ViewModels responsible for persistence details that belong in use cases or repositories.

## Use cases

Create a separate use-case class for each meaningful business operation:

```kotlin
class CreateItemUseCase(
    private val repository: ItemRepository,
) {
    suspend operator fun invoke(draft: ItemDraft): Result<Item> =
        repository.createItem(draft)
}
```

Guidelines:

- Name classes with the `UseCase` suffix.
- Put each use case in its own file when the project has multiple operations; this keeps dependencies, testing, and DI registration discoverable.
- Use `operator fun invoke` as the public entry point.
- Keep thin use cases thin. Add validation, coordination, or business rules there when they are domain behavior rather than UI behavior.
- Return typed domain results/errors rather than UI strings or Compose state.
- Register use cases in a domain DI module and inject only the use cases required by each ViewModel.

## ViewModel state

Represent the renderable state for a screen with one immutable state data class:

```kotlin
data class ItemListState(
    val items: List<ItemUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiText? = null,
)
```

Expose a read-only `StateFlow` and mutate the private flow with `.update { }`:

```kotlin
private val _state = MutableStateFlow(ItemListState())
val state = _state.asStateFlow()

private fun setLoading(value: Boolean) {
    _state.update { it.copy(isLoading = value) }
}
```

Use an action/intent API for screens with several user interactions:

```kotlin
sealed interface ItemListAction {
    data object Refresh : ItemListAction
    data class ItemClicked(val id: ItemId) : ItemListAction
}
```

For simple screens, named methods can be sufficient; do not introduce a large event hierarchy without a clear interaction benefit.

Keep one ViewModel per screen or cohesive screen flow. Put each ViewModel in its own file when the project contains multiple ViewModels. Inject only the use cases and services it needs.

## One-time effects

Use a separate event stream for effects that must not be replayed as persistent state, such as navigation, transient messages, or opening a system picker:

```kotlin
sealed interface ItemListEvent {
    data class NavigateToDetail(val id: ItemId) : ItemListEvent
    data class ShowMessage(val message: UiText) : ItemListEvent
}

private val _events = Channel<ItemListEvent>()
val events = _events.receiveAsFlow()
```

Do not encode one-time navigation as a Boolean in screen state. If an effect can be reconstructed from state and should survive recreation, model it as state instead.

## Compose screen boundaries

Use two composable responsibilities in the same screen file:

1. **Entry composable**: obtains the ViewModel with `koinViewModel()`, collects state with lifecycle awareness, observes one-time events, and wires navigation callbacks.
2. **View composable**: receives only the values and callbacks it renders, contains no ViewModel or repository reference, and is independently previewable.

Use a consistent name such as `<Feature>Screen` for the entry point and `<Feature>View` for the stateless rendering function. If a codebase uses `Root` instead, keep that convention consistently; do not pass ViewModels deeper into the composable tree.

```kotlin
@Composable
fun ItemListScreen(
    onOpenItem: (ItemId) -> Unit,
    viewModel: ItemListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ItemListView(
        items = state.items,
        isLoading = state.isLoading,
        onRefresh = viewModel::refresh,
        onItemClick = onOpenItem,
    )
}

@Composable
private fun ItemListView(
    items: List<ItemUi>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onItemClick: (ItemId) -> Unit,
) {
    // Render only the data needed by this view.
}
```

Pass selected state fields rather than the entire state when the view only needs a subset. Pass the complete state when the view genuinely renders most of it or when an action-based contract makes the full state clearer.

Keep production composable APIs explicit: require essential callbacks, IDs, modes, and data instead of hiding them behind default arguments. Default arguments are acceptable for optional configuration only when the default is unambiguous and safe. Put dummy values and no-op callbacks in previews, not production call sites.

Avoid fully qualified type references in implementation code. Import the type and use its short name; this keeps signatures readable and makes dependencies visible at the top of the file.

## UI models and mapping

Do not expose domain entities directly when the UI needs formatting, derived values, localized labels, or display-specific structure. Define presentation models with a `Ui` suffix:

```kotlin
data class ItemUi(
    val id: ItemId,
    val title: String,
    val formattedDate: String,
    val isSelected: Boolean,
)
```

Put domain-to-UI conversion in mapper extension functions, preferably in a dedicated mapper file or presentation/domain-mapping package:

```kotlin
fun Item.toItemUi(): ItemUi = ItemUi(
    id = id,
    title = title,
    formattedDate = date.format(displayFormatter),
    isSelected = false,
)
```

Keep formatting and derived display values out of the composable when they can be calculated once in the ViewModel or mapper. Keep UI-only models out of the data layer.

## Errors and localization

Use typed domain/data errors below the presentation layer. Map them to `UiText` or another presentation-specific message type at the presentation boundary:

- Use string resources for messages that can be localized.
- Use plain `String` for values that are inherently dynamic, such as a user-entered name or already-formatted numeric value.
- Do not put Android resource IDs in domain models or repositories.
- Surface failures explicitly; do not silently return an empty success-shaped state.

## Forms and process death

For forms with important user input, use `SavedStateHandle` for the minimal fields that must survive process death. Update it when the corresponding action changes:

```kotlin
savedStateHandle["title"] = action.title
_state.update { it.copy(title = action.title) }
```

Do not persist the entire state indiscriminately. Avoid using synthetic IDs or values derived from display text to identify an entity; carry the real ID through navigation and the ViewModel.

## Coroutines

- Launch screen work from `viewModelScope`.
- Prefer suspend/Flow APIs in use cases and repositories.
- Do not inject dispatchers merely by convention. Inject a dispatcher when production code explicitly switches to a non-main dispatcher and that behavior needs direct unit testing.
- Wrap genuinely blocking operations with `withContext(Dispatchers.IO)`.
- Collect long-lived flows in the ViewModel and expose state to Compose; avoid starting duplicate collectors from recomposition.

## Dependency injection

Use constructor-reference registrations where possible:

```kotlin
val featureDomainModule = module {
    singleOf(::CreateItemUseCase)
}

val featurePresentationModule = module {
    viewModelOf(::ItemListViewModel)
}
```

Keep DI modules separated by layer when the project has meaningful data, domain, and presentation registrations. Assemble them at application startup. Use `viewModelOf` for ViewModels and inject them at the screen entry boundary.

## File and naming conventions

Use names that make architecture discoverable:

| Concept | Convention |
|---|---|
| ViewModel | `<Feature>ViewModel` |
| State | `<Feature>State` or `<Feature>UiState` |
| Action | `<Feature>Action` |
| Event | `<Feature>Event` |
| Use case | `<Operation>UseCase` |
| UI model | `<Model>Ui` |
| Stateless renderer | `<Feature>View` |

Keep related declarations in focused files. Do not retain aggregate “all use cases” or “all ViewModels” files once the feature set grows enough that individual ownership is clearer.

## Implementation workflow

Before editing:

1. Identify the screen’s required behaviors and the data it actually renders.
2. Trace the existing repository/data APIs and domain models.
3. Search for existing mappers, error types, DI registrations, and naming conventions before adding new ones.
4. Decide which operations deserve separate use cases and which values belong in UI models.

While implementing:

1. Add or refine use cases first, preserving domain/data boundaries.
2. Add the ViewModel with injected use cases and a single state flow.
3. Add mapper extensions for domain-to-UI conversion.
4. Keep the entry composable thin and the `*View` composable stateless.
5. Provide explicit preview data instead of production defaults.
6. Update DI registration and navigation call sites together.

Before finishing:

- Search for fully qualified references, stale aggregate files, and accidental default arguments.
- Compile the smallest relevant Android target.
- Confirm previews and navigation call sites satisfy the explicit screen contracts.
- Verify that no ViewModel, repository, resource ID, or domain entity leaked into a lower-responsibility UI component.
