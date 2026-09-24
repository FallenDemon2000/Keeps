---
name: android-data-layer
description: |
  Data layer patterns for Android/KMP - data sources, repositories, DTOs, mappers, Room entities, Ktor HttpClient, safe call helpers, token storage, and offline-first. Use this skill whenever writing or reviewing a data source or repository, creating DTOs or Room entities, writing mappers, setting up the Ktor HttpClient, handling network errors, or implementing token refresh. Trigger on phrases like "create a repository", "create a data source", "add a DAO", "Ktor client", "write a mapper", "DTO", "Room entity", "network call", "token storage", or "offline-first".
---
 
# Android / KMP Data Layer

## Scope and Design Process

Before writing entities or repositories, derive the data contract from all product surfaces:

- Requirements define invariants, lifecycle rules, edge cases, and deletion semantics.
- UI designs reveal which values are displayed, which states must be observable, and which values are merely derived.
- User actions reveal transaction boundaries and idempotency requirements.

Separate **source-of-truth data** from **derived projections**. Persist facts that must survive recomputation; calculate counters, percentages, streaks, summaries, and heatmap cells from those facts unless profiling proves a cache is necessary. A derived cache must have a deterministic rebuild path and must never be the only copy of user data.

For every proposed field, ask:

1. Is it user-authored or a computed value?
2. Can it be recomputed from other persisted data?
3. What historical meaning must remain stable after edits?
4. What uniqueness, range, lifecycle, and deletion rules must storage enforce?
5. Which operation must be atomic from the user's perspective?

## Error Handling

This skill uses `Result<T, E>`, `DataError`, and the extension helpers defined in the **android-error-handling** skill. Refer to that skill for the full `Result` wrapper, `DataError` sealed interface, and `map`/`onSuccess`/`onFailure`/`asEmptyResult` extensions.

---

## Data Source vs Repository

- **Data source** — accesses a single data source (local DB, remote API, file system). Most classes in the data layer are data sources.
- **Repository** — combines multiple data sources (e.g., a remote API + a local DB for offline-first). Only use the term "repository" when the class genuinely coordinates multiple sources.

```kotlin
// Single source → data source
interface NoteLocalDataSource {
    suspend fun getNotes(): Result<List<Note>, DataError.Local>
    suspend fun insertNote(note: Note): EmptyResult<DataError.Local>
}

interface NoteRemoteDataSource {
    suspend fun fetchNotes(): Result<List<Note>, DataError.Network>
}

// Multiple sources → repository
interface NoteRepository {
    suspend fun getNotes(): Result<List<Note>, DataError>
    suspend fun sync(): EmptyResult<DataError>
}
```

## Domain Layer Contracts

- Pure Kotlin — no Android/framework imports.
- Contains: domain models, data source/repository **interfaces**, error types.
- **Every data source or repository used by a ViewModel must have an interface in `domain`** — enforces that `presentation` never depends on `data`, and enables testing.
 
---
 
## DTOs and Domain Models
 
- Always separate: DTOs (data layer) ↔ Domain Models (domain layer).
- Domain models never go directly into Room entities or Ktor request/response bodies.
- Mappers are simple extension functions living in the data layer alongside the DTO:
 
```kotlin
fun NoteDto.toNote(): Note = Note(id = id, title = title, ...)
fun Note.toNoteDto(): NoteDto = NoteDto(id = id, title = title, ...)
fun NoteEntity.toNote(): Note = ...
fun Note.toNoteEntity(): NoteEntity = ...
```

Keep mapper functions in dedicated mapper files when a model file would otherwise mix persistence annotations, domain behavior, and conversion logic. For collections, provide named extensions such as `List<Entity>.toDomain()` so repositories do not repeat mapping lambdas throughout every observation method.

When a data model contains several independent concepts, prefer one top-level type per file. This keeps navigation, diffs, generated Room metadata, and future ownership boundaries clear. Grouping is appropriate only for genuinely inseparable small types.

## Room Schema Design

Use normalized tables for durable facts and enforce invariants at both domain and database boundaries:

- Store stable IDs, user-authored values, lifecycle dates, and configuration as scalar columns.
- Use a child table for repeatable facts such as events, completions, or measurements.
- Model “one fact per owner and date” with a composite primary key or a unique composite index.
- Use foreign keys with `ON DELETE CASCADE` only when child data has no meaning without its parent and product deletion explicitly removes that history.
- Add indexes for actual query patterns, especially foreign-key lookups and date-range queries.
- Use non-null columns and SQLite `CHECK` constraints for structural invariants; use domain validation for catalog membership, cross-field rules, and user-facing error details.

For calendar dates that represent a local business day rather than an instant, prefer `LocalDate` persisted as ISO-8601 `TEXT` (`yyyy-MM-dd`) through one Room converter. ISO text preserves chronological lexical ordering and is easy to inspect. Do not silently substitute epoch timestamps when timezone-independent calendar identity is the actual requirement.

For fixed weekly schedules, a bitmask is compact and efficient when the domain is strictly seven named days. Document the canonical bit order in one place and reuse the same helper in Kotlin, SQL queries, validators, and migrations. Use a normalized child table instead when schedules need per-day metadata, history, exceptions, or future extensibility.

Do not store a serialized JSON blob for core fields that the database must filter, constrain, or index. JSON is appropriate for opaque payloads whose internal structure is not queried by Room.

## Room Entity and DAO Organization

Place each Room entity in its own file and each DAO in its own file or clearly scoped DAO package. Keep DAOs focused on persistence operations:

- `Flow` for observable reads.
- `suspend` functions for writes and point reads.
- SQL queries that express filtering and ordering close to the database.
- No UI state, navigation, or presentation formatting in entities or DAOs.

Import referenced types in KDoc and comments instead of embedding fully qualified names. This keeps documentation readable and makes package moves safer.

Expose range-based queries for statistics rather than loading unbounded history when the screen only needs a window. If multiple screen projections need a consistent snapshot, coordinate their source flows or use a transaction-backed query rather than combining unrelated, independently changing snapshots.

## Repository Boundaries and Transactions

Use a repository only when it coordinates data sources or owns a meaningful domain-facing data contract. A local-only feature may still use a repository when it coordinates multiple DAOs and exposes screen-oriented operations, but do not add a repository solely as a pass-through wrapper.

The repository should:

- Validate and normalize domain input before persistence.
- Map entities to domain models and keep Room types out of presentation.
- Own transaction boundaries for multi-step operations.
- Expose idempotent operations where repeated user actions are possible.
- Return explicit typed outcomes for expected validation and persistence failures.
- Preserve coroutine cancellation and propagate unexpected failures instead of converting them into success-shaped defaults.

For binary toggle interactions, prefer a transaction that checks the current state and inserts or deletes exactly one row. Back the invariant with a composite key so concurrent or repeated requests cannot create duplicates. For destructive operations, delete the parent and rely on a configured cascade only when the foreign key is enabled and the behavior is verified.

## Validation and Invariants

Validate invariants in layers:

- Domain validation provides deterministic, testable rules and precise error categories.
- Repository validation protects the public data contract before calling Room.
- SQLite constraints protect against invalid writes from migrations, debug tools, or future callers.
- UI validation should present errors, not own business rules.

For multiple invalid fields, use an explicit ordered `when` decision so precedence is intentional and stable. Compute each validation result once before the `when` when the checks are non-trivial; do not call the same validator repeatedly in conditions and branches.

Normalize only where the product contract says normalization is allowed. A common safe policy is trim surrounding whitespace, reject blank-after-trim, enforce a Unicode-aware length limit, preserve casing, and avoid imposing uniqueness unless the product requires it.

## Derived Statistics and Historical Semantics

When a feature displays streaks, progress, rankings, summaries, or calendar visualizations:

- Define the denominator and date window explicitly.
- Exclude dates before an item's creation/start date from both misses and denominators.
- Distinguish scheduled occurrences from calendar days when schedules skip days.
- Define how an incomplete current day affects “current” metrics.
- Define whether edits reinterpret history or take effect from an effective date.
- Inject or pass a reference date into calculations so date-boundary logic is deterministic.

If an edit intentionally changes the interpretation of history, document that trade-off in the data contract. If historical truth must remain stable, persist versioned configuration with effective date ranges instead of overwriting the current configuration.

Keep statistics calculators pure: accept domain snapshots and a reference date, return domain projections, and avoid Room, Android clocks, or UI formatting. This makes calculations portable, repeatable, and independently verifiable.

## Room Migrations and Schema Documentation

Start with an explicit schema version and preserve a migration path. Do not enable destructive fallback for production data. Export Room schemas when the build supports it and review schema JSON changes with migrations.

Document:

- Column meaning and nullability.
- Date and timezone semantics.
- Bitmask or enum mappings.
- Foreign-key and deletion behavior.
- Uniqueness constraints.
- Derived-stat formulas and denominators.
- Whether edits preserve or reinterpret history.

Avoid speculative tables and fields for features not required by the current product. Prefer a small normalized source model with clear extension points over premature caches, audit tables, or generic JSON.

## Dependency Injection

Provide the Room database, DAOs, mappers/calculators, and repository through app-scoped DI. Create one database instance per application process, not per screen or ViewModel. Use explicit type bindings when a module contains several related infrastructure objects or when the public type matters, so registrations remain readable and refactors do not silently change resolution.

Keep database construction in the DI/data boundary. Do not let presentation code instantiate Room, DAOs, or repositories directly.

## Verification Workflow

After changing the data layer:

1. Run the narrowest build or compile task that exercises Room/KSP.
2. Run formatting/lint checks relevant to the changed source set.
3. Inspect generated schema output when entities or constraints changed.
4. Verify the working tree and staged diff for accidental file moves, stale imports, or duplicate types.
5. Confirm that expected failures remain typed and that cancellation is not swallowed.

Treat generated-code failures such as missing Room types or stale imports as structural refactor signals: inspect package declarations and imports before changing behavior. Do not “fix” a build by weakening constraints or hiding exceptions.
 
---
 
## Implementations

Name implementations for what makes them unique — never suffix with `Impl`.

### Data source (single source)

```kotlin
class RoomNoteDataSource(private val dao: NoteDao) : NoteLocalDataSource {
    override suspend fun getNotes(): Result<List<Note>, DataError.Local> {
        return try {
            Result.Success(dao.getAllNotes().map { it.toNote() })
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }
}
```

### Repository (multiple sources)

```kotlin
class OfflineFirstNoteRepository(
    private val localDataSource: NoteLocalDataSource,
    private val remoteDataSource: NoteRemoteDataSource
) : NoteRepository {
    override suspend fun getNotes(): Result<List<Note>, DataError> {
        return remoteDataSource.fetchNotes()
            .onSuccess { notes -> localDataSource.insertAll(notes) }
            .onFailure { localDataSource.getNotes() }
    }
}
```

Use names like `RoomNoteDataSource`, `KtorNoteDataSource`, `OfflineFirstNoteRepository`. The name should tell you what the class wraps or how it behaves.
 
---
 
## Ktor — HttpClient Factory (`core:data`)
 
Configure the client once. Accept the engine externally so tests can swap in a mock engine:
 
```kotlin
object HttpClientFactory {
    fun create(engine: HttpClientEngine): HttpClient = HttpClient(engine) {
        install(ContentNegotiation) { json() }
        install(Auth) {
            bearer {
                loadTokens { /* load from DataStore */ }
                refreshTokens { /* call refresh endpoint, save new tokens */ }
            }
        }
        install(Logging) { logger = Logger.DEFAULT; level = LogLevel.ALL }
        defaultRequest { contentType(ContentType.Application.Json) }
    }
}
```
 
Inject `HttpClient` via Koin. For KMP, use the platform default engine.
 
---
 
## Ktor — Safe Call Helpers (`core:data`)

Use `safeCall` / `responseToResult` helpers and typed extension functions (`HttpClient.get`, `HttpClient.post`, `HttpClient.delete`) to keep data source call sites clean and uniform. See the **android-error-handling** skill for the full implementation of these helpers.

```kotlin
suspend fun getNotes(): Result<List<NoteDto>, DataError.Network> {
    return httpClient.get(route = "/notes")
}
```
 
---
 
## Token Storage
 
Store tokens in DataStore (in `core:data` or a dedicated `:core:auth` / `:feature:auth:data` module). The Ktor `Auth` plugin reads/writes tokens and handles 401 refresh automatically.
 
---
 
## Room Migrations
 
Prefer `@Database(autoMigrations = [AutoMigration(from = 1, to = 2)])`. Use manual `Migration` objects when the schema change is too complex for auto-migration.
 
---
 
## Offline-First (when applicable)
 
Follow **Room as single source of truth**: fetch from network → persist to Room → expose DB `Flow` to the ViewModel. The ViewModel never observes network responses directly.
 
This pattern is optional — apply it when the project requires offline support.
 
---
 
## Naming Conventions
 
| Thing | Convention | Example |
|---|---|---|
| Data source interface | `<Entity><Local/Remote>DataSource` | `NoteLocalDataSource`, `NoteRemoteDataSource` |
| Data source impl | describe what makes it unique | `RoomNoteDataSource`, `KtorNoteDataSource` |
| Repository interface | `<Entity>Repository` (multi-source only) | `NoteRepository` |
| Repository impl | describe what makes it unique | `OfflineFirstNoteRepository` |
| DTO | `<Model>Dto` | `NoteDto` |
| Room entity | `<Model>Entity` | `NoteEntity` |
| Mapper | extension fun on source type | `fun NoteDto.toNote()` |
 
---
 
## Checklist: Adding a New Data Source or Repository

- [ ] Define domain model(s) in `feature:domain`
- [ ] Define data source or repository interface in `feature:domain`
- [ ] Define feature-specific error type(s) in `feature:domain` (implement `Error`) — see **android-error-handling** skill
- [ ] Define DTOs and Room entities in `feature:data`
- [ ] Write mappers as extension functions in `feature:data`
- [ ] Implement data source (single source) or repository (multi-source) in `feature:data`, named for what makes it unique
- [ ] Identify persisted facts versus derived values before adding columns
- [ ] Define keys, foreign keys, indexes, date representation, deletion behavior, and migration posture
- [ ] Add Flow reads, suspend writes, and transactions for multi-step user actions
- [ ] Validate domain invariants before persistence and backstop structural rules in SQLite
- [ ] Make date-window and statistic denominators explicit and deterministic
- [ ] Keep entities/DAOs/repositories free of presentation formatting
- [ ] Build with Room/KSP and inspect the generated schema after structural changes
 